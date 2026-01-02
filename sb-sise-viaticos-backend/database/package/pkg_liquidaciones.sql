CREATE OR REPLACE TYPE T_LIQUIDACION AS OBJECT ( 
    monto_asignado NUMBER(12,2),
    monto_rendido_validado NUMBER(12,2), 
    CONSTRUCTOR FUNCTION T_LIQUIDACION(
        p_monto_asignado NUMBER,
        p_id_solicitud NUMBER
    ) RETURN SELF AS RESULT, 
    STATIC FUNCTION calcular_monto_validado(p_id_solicitud NUMBER) RETURN NUMBER,
    MEMBER FUNCTION calcular_saldo_empresa RETURN NUMBER,
    MEMBER FUNCTION calcular_saldo_empleado RETURN NUMBER, 
    MEMBER FUNCTION obtener_tipo_liquidacion RETURN VARCHAR2,
    MEMBER FUNCTION validar_liquidacion RETURN VARCHAR2,
    MEMBER FUNCTION generar_mensaje_liquidacion RETURN VARCHAR2,
    MEMBER FUNCTION calcular_porcentaje_consumo RETURN NUMBER, 
    MEMBER PROCEDURE aplicar_politica_redondeo(
        p_saldo_empresa IN OUT NUMBER,
        p_saldo_empleado IN OUT NUMBER
    ),
    MEMBER PROCEDURE generar_resumen(
        p_tipo_liq OUT VARCHAR2,
        p_saldo_empresa OUT NUMBER,
        p_saldo_empleado OUT NUMBER,
        p_mensaje OUT VARCHAR2
    )
);
/

CREATE OR REPLACE TYPE BODY T_LIQUIDACION AS

     -- Constructor para inicializar los montos
    CONSTRUCTOR FUNCTION T_LIQUIDACION(
        p_monto_asignado NUMBER,
        p_id_solicitud NUMBER
    ) RETURN SELF AS RESULT IS
    BEGIN
        SELF.monto_asignado := NVL(p_monto_asignado, 0); 
        SELF.monto_rendido_validado := T_LIQUIDACION.calcular_monto_validado(p_id_solicitud);
        RETURN;
    END;

    STATIC FUNCTION calcular_monto_validado(p_id_solicitud NUMBER) RETURN NUMBER IS
        v_monto NUMBER(12,2);
    BEGIN
        SELECT NVL(SUM(d.monto_total), 0)
        INTO v_monto
        FROM rendicion_cuentas r
        JOIN detalle_comprobantes d ON r.id_rendicion = d.id_rendicion
        WHERE r.id_comision = p_id_solicitud
          AND d.estado = 'ACEPTADO';
        RETURN v_monto;
    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN 0;
        WHEN OTHERS THEN
            RETURN 0;
    END;

    -- Calcula el saldo a favor de la empresa - empleado debe devolver
    MEMBER FUNCTION calcular_saldo_empresa RETURN NUMBER IS
        v_saldo NUMBER(12,2);
    BEGIN
        IF monto_asignado > monto_rendido_validado THEN
            v_saldo := monto_asignado - monto_rendido_validado;
        ELSE
            v_saldo := 0;
        END IF;
        RETURN v_saldo;
    END calcular_saldo_empresa;

    -- Calcula el saldo a favor del empleado  empresa debe pagar 
    MEMBER FUNCTION calcular_saldo_empleado RETURN NUMBER IS
        v_saldo NUMBER(12,2);
    BEGIN
        IF monto_rendido_validado > monto_asignado THEN
            v_saldo := monto_rendido_validado - monto_asignado;
        ELSE
            v_saldo := 0;
        END IF;
        RETURN v_saldo;
    END calcular_saldo_empleado;

    -- tipo de liquidación
    MEMBER FUNCTION obtener_tipo_liquidacion RETURN VARCHAR2 IS
    BEGIN
        IF monto_asignado = monto_rendido_validado THEN
            RETURN 'EQUILIBRADA';
        ELSIF monto_asignado > monto_rendido_validado THEN
            RETURN 'DEVOLUCION_EMPLEADO';
        ELSE
            RETURN 'PAGO_EMPLEADO';
        END IF;
    END obtener_tipo_liquidacion;

    -- Valida la coherencia de la liquidación
    MEMBER FUNCTION validar_liquidacion RETURN VARCHAR2 IS
    BEGIN
        -- Validar que los montos sean positivos
        IF monto_asignado < 0 THEN
            RETURN 'ERROR: Monto asignado no puede ser negativo';
        END IF;
        
        IF monto_rendido_validado < 0 THEN
            RETURN 'ERROR: Monto rendido no puede ser negativo';
        END IF;
        
        -- Validar que haya al menos un monto
        IF monto_asignado = 0 AND monto_rendido_validado = 0 THEN
            RETURN 'ADVERTENCIA: Liquidación sin montos';
        END IF;
        
        -- Validar coherencia de magnitudes 
        IF monto_asignado > 0 AND monto_rendido_validado > (monto_asignado * 2) THEN
            RETURN 'ADVERTENCIA: Rendición excede el 200% del monto asignado';
        END IF;
        
        RETURN 'VALIDO';
    END validar_liquidacion;

    --  mensaje  de la liquidación
    MEMBER FUNCTION generar_mensaje_liquidacion RETURN VARCHAR2 IS
        v_tipo VARCHAR2(50);
        v_diferencia NUMBER(12,2);
    BEGIN
        v_tipo := SELF.obtener_tipo_liquidacion();
        
        CASE v_tipo
            WHEN 'EQUILIBRADA' THEN
                RETURN 'Liquidación sin saldo pendiente. Gastos = Asignación.';
            WHEN 'DEVOLUCION_EMPLEADO' THEN
                v_diferencia := SELF.calcular_saldo_empresa();
                RETURN 'El empleado debe devolver S/. ' || TO_CHAR(v_diferencia, '999,999.99') || ' a la empresa.';
            WHEN 'PAGO_EMPLEADO' THEN
                v_diferencia := SELF.calcular_saldo_empleado();
                RETURN 'La empresa debe pagar S/. ' || TO_CHAR(v_diferencia, '999,999.99') || ' al empleado.';
            ELSE
                RETURN 'Estado de liquidación desconocido.';
        END CASE;
    END generar_mensaje_liquidacion;

    -- porcentaje de consumo del presupuesto
    MEMBER FUNCTION calcular_porcentaje_consumo RETURN NUMBER IS
    BEGIN
        IF monto_asignado = 0 THEN
            RETURN 0;
        END IF;
        RETURN ROUND((monto_rendido_validado / monto_asignado) * 100, 2);
    END calcular_porcentaje_consumo;

    -- Aplica política de redondeo  
    MEMBER PROCEDURE aplicar_politica_redondeo(
        p_saldo_empresa IN OUT NUMBER,
        p_saldo_empleado IN OUT NUMBER
    ) IS
    BEGIN
        -- Si el saldo es menor a 1.00, se considera saldado
        IF ABS(p_saldo_empresa) < 1 THEN
            p_saldo_empresa := 0;
        END IF;
        
        IF ABS(p_saldo_empleado) < 1 THEN
            p_saldo_empleado := 0;
        END IF;
    END aplicar_politica_redondeo;

    -- Genera un resumen completo de la liquidación
    MEMBER PROCEDURE generar_resumen(
        p_tipo_liq OUT VARCHAR2,
        p_saldo_empresa OUT NUMBER,
        p_saldo_empleado OUT NUMBER,
        p_mensaje OUT VARCHAR2
    ) IS
    BEGIN
        -- Calcular valores
        p_tipo_liq := SELF.obtener_tipo_liquidacion();
        p_saldo_empresa := SELF.calcular_saldo_empresa();
        p_saldo_empleado := SELF.calcular_saldo_empleado();
        
        -- Aplicar política de redondeo
        SELF.aplicar_politica_redondeo(p_saldo_empresa, p_saldo_empleado);
        
        -- Generar mensaje
        p_mensaje := SELF.generar_mensaje_liquidacion();
    END generar_resumen;

END;
/

-- PAQUETE: PKG_LIQUIDACIONES
-- DESCRIPCIÓN: Gestión de liquidaciones finales usando
--              el objeto T_LIQUIDACION para cálculos

CREATE OR REPLACE PACKAGE PKG_LIQUIDACIONES AS

    -- Listar todas las liquidaciones
    PROCEDURE PRC_LISTAR_TODAS (
        p_cursor OUT SYS_REFCURSOR
    );

    -- Obtener liquidación por ID de solicitud
    FUNCTION FNC_OBTENER_POR_SOLICITUD (
        p_id_solicitud IN NUMBER
    ) RETURN SYS_REFCURSOR;

    -- Obtener liquidación por ID
    PROCEDURE PRC_OBTENER_POR_ID (
        p_id_liquidacion IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Guardar liquidación usando el objeto T_LIQUIDACION
    PROCEDURE PRC_GUARDAR_LIQUIDACION (
        p_id_liquidacion IN OUT NUMBER,
        p_id_solicitud IN NUMBER,
        p_monto_asignado IN NUMBER,
        p_fecha_cierre IN DATE,
        p_user_crea IN VARCHAR2
    );

    -- Eliminar liquidación
    PROCEDURE PRC_ELIMINAR_LIQUIDACION (
        p_id_liquidacion IN NUMBER
    );

END PKG_LIQUIDACIONES;
/

CREATE OR REPLACE PACKAGE BODY PKG_LIQUIDACIONES AS

    PROCEDURE PRC_LISTAR_TODAS (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM liquidacion_final
            ORDER BY fecha_crea DESC;
    END PRC_LISTAR_TODAS;

    FUNCTION FNC_OBTENER_POR_SOLICITUD (
        p_id_solicitud IN NUMBER
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT * FROM liquidacion_final
            WHERE id_comision = p_id_solicitud;
        RETURN v_cursor;
    END FNC_OBTENER_POR_SOLICITUD;

    PROCEDURE PRC_OBTENER_POR_ID (
        p_id_liquidacion IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM liquidacion_final
            WHERE id_liquidacion = p_id_liquidacion;
    END PRC_OBTENER_POR_ID;

    -- Guardado 
    PROCEDURE PRC_GUARDAR_LIQUIDACION (
        p_id_liquidacion IN OUT NUMBER,
        p_id_solicitud IN NUMBER,
        p_monto_asignado IN NUMBER,
        p_fecha_cierre IN DATE,
        p_user_crea IN VARCHAR2
    ) AS 
        v_liquidacion T_LIQUIDACION;  
        v_tipo_liq VARCHAR2(50);
        v_saldo_empresa NUMBER(12,2);
        v_saldo_empleado NUMBER(12,2);
        v_mensaje VARCHAR2(500);
        v_validacion VARCHAR2(500);
    BEGIN 
     
        v_liquidacion := T_LIQUIDACION(p_monto_asignado, p_id_solicitud);
        
        -- Validar la liquidación
        v_validacion := v_liquidacion.validar_liquidacion();
        IF v_validacion != 'VALIDO' THEN
            RAISE_APPLICATION_ERROR(-20001, 'Validación fallida: ' || v_validacion);
        END IF;
        
        -- Generar resumen completo
        v_liquidacion.generar_resumen(
            v_tipo_liq,
            v_saldo_empresa,
            v_saldo_empleado,
            v_mensaje
        );
        
        -- registro bd
        IF p_id_liquidacion IS NULL THEN
            INSERT INTO liquidacion_final (
                id_comision, monto_asignado, monto_rendido_validado,
                saldo_a_favor_empresa, saldo_a_favor_empleado,
                fecha_cierre, estado_cierre, mensaje_liquidacion, user_crea, fecha_crea
            ) VALUES (
                p_id_solicitud, p_monto_asignado, v_liquidacion.monto_rendido_validado,
                v_saldo_empresa, v_saldo_empleado,
                p_fecha_cierre, v_tipo_liq, v_mensaje, p_user_crea, SYSTIMESTAMP
            ) RETURNING id_liquidacion INTO p_id_liquidacion;
        ELSE
            UPDATE liquidacion_final SET
                id_comision = p_id_solicitud,
                monto_asignado = p_monto_asignado,
                monto_rendido_validado = v_liquidacion.monto_rendido_validado,
                saldo_a_favor_empresa = v_saldo_empresa,
                saldo_a_favor_empleado = v_saldo_empleado,
                fecha_cierre = p_fecha_cierre,
                estado_cierre = v_tipo_liq,
                mensaje_liquidacion = v_mensaje,
                user_mod = p_user_crea,
                fecha_mod = SYSTIMESTAMP
            WHERE id_liquidacion = p_id_liquidacion;
        END IF;
        
        COMMIT;
    END PRC_GUARDAR_LIQUIDACION;

    PROCEDURE PRC_ELIMINAR_LIQUIDACION (
        p_id_liquidacion IN NUMBER
    ) AS
    BEGIN
        DELETE FROM liquidacion_final WHERE id_liquidacion = p_id_liquidacion;
        COMMIT;
    END PRC_ELIMINAR_LIQUIDACION;

END PKG_LIQUIDACIONES;
/