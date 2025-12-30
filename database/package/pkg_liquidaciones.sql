-- PAQUETE: PKG_LIQUIDACIONES
-- DESCRIPCIÓN: Gestión de liquidaciones finales de comisiones

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

    -- Guardar liquidación (Insertar o Actualizar)
    PROCEDURE PRC_GUARDAR_LIQUIDACION (
        p_id_liquidacion IN OUT NUMBER,
        p_id_solicitud IN NUMBER,
        p_monto_asignado IN NUMBER,
        p_monto_rendido_validado IN NUMBER,
        p_saldo_empresa IN NUMBER,
        p_saldo_empleado IN NUMBER,
        p_fecha_cierre IN DATE,
        p_estado_cierre IN VARCHAR2,
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

    PROCEDURE PRC_GUARDAR_LIQUIDACION (
        p_id_liquidacion IN OUT NUMBER,
        p_id_solicitud IN NUMBER,
        p_monto_asignado IN NUMBER,
        p_monto_rendido_validado IN NUMBER,
        p_saldo_empresa IN NUMBER,
        p_saldo_empleado IN NUMBER,
        p_fecha_cierre IN DATE,
        p_estado_cierre IN VARCHAR2,
        p_user_crea IN VARCHAR2
    ) AS
    BEGIN
        IF p_id_liquidacion IS NULL THEN
            INSERT INTO liquidacion_final (
                id_comision, monto_asignado, monto_rendido_validado,
                saldo_a_favor_empresa, saldo_a_favor_empleado,
                fecha_cierre, estado_cierre, user_crea, fecha_crea
            ) VALUES (
                p_id_solicitud, p_monto_asignado, p_monto_rendido_validado,
                p_saldo_empresa, p_saldo_empleado,
                p_fecha_cierre, p_estado_cierre, p_user_crea, SYSTIMESTAMP
            ) RETURNING id_liquidacion INTO p_id_liquidacion;
        ELSE
            UPDATE liquidacion_final SET
                id_comision = p_id_solicitud,
                monto_asignado = p_monto_asignado,
                monto_rendido_validado = p_monto_rendido_validado,
                saldo_a_favor_empresa = p_saldo_empresa,
                saldo_a_favor_empleado = p_saldo_empleado,
                fecha_cierre = p_fecha_cierre,
                estado_cierre = p_estado_cierre,
                user_mod = p_user_crea,
                fecha_mod = SYSTIMESTAMP
            WHERE id_liquidacion = p_id_liquidacion;
        END IF;
    END PRC_GUARDAR_LIQUIDACION;

    PROCEDURE PRC_ELIMINAR_LIQUIDACION (
        p_id_liquidacion IN NUMBER
    ) AS
    BEGIN
        DELETE FROM liquidacion_final WHERE id_liquidacion = p_id_liquidacion;
    END PRC_ELIMINAR_LIQUIDACION;

END PKG_LIQUIDACIONES;
/