-- PAQUETE: PKG_COMPROBANTES
-- DESCRIPCIÓN: Gestión de detalles de comprobantes de rendición

CREATE OR REPLACE PACKAGE PKG_COMPROBANTES AS

    -- Listar todos los comprobantes
    PROCEDURE PRC_LISTAR_TODOS (
        p_cursor OUT SYS_REFCURSOR
    );

    -- Listar comprobantes por ID de rendición
    PROCEDURE PRC_LISTAR_POR_RENDICION (
        p_id_rendicion IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Obtener un comprobante por ID
    PROCEDURE PRC_OBTENER_COMPROBANTE (
        p_id_detalle IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Guardar comprobante - insertar o actualizar
    PROCEDURE PRC_GUARDAR_COMPROBANTE (
        p_id_detalle IN OUT NUMBER,
        p_id_rendicion IN NUMBER,
        p_id_tipo_gasto IN NUMBER,
        p_fecha_emision IN TIMESTAMP,
        p_tipo_comprobante IN VARCHAR2,
        p_serie_comprobante IN VARCHAR2,
        p_numero_comprobante IN VARCHAR2,
        p_ruc_emisor IN VARCHAR2,
        p_razon_social_emisor IN VARCHAR2,
        p_monto_total IN NUMBER,
        p_archivo_url IN VARCHAR2,
        p_estado IN VARCHAR2,
        p_motivo_rechazo IN VARCHAR2,
        p_user_crea IN VARCHAR2
    );

    -- Eliminar comprobante
    PROCEDURE PRC_ELIMINAR_COMPROBANTE (
        p_id_detalle IN NUMBER
    );

END PKG_COMPROBANTES;
/

CREATE OR REPLACE PACKAGE BODY PKG_COMPROBANTES AS

    PROCEDURE P_ACTUALIZAR_TOTAL_ACEPTADO(p_id_rendicion IN NUMBER) IS
        v_total NUMBER(12,2);
    BEGIN
        SELECT NVL(SUM(monto_total), 0)
        INTO v_total
        FROM detalle_comprobantes
        WHERE id_rendicion = p_id_rendicion
          AND estado = 'ACEPTADO';
          
        UPDATE rendicion_cuentas
        SET total_aceptado = v_total
        WHERE id_rendicion = p_id_rendicion;
    END;

    PROCEDURE PRC_LISTAR_TODOS (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT d.*, t.nombre_gasto, t.cuenta_contable, r.id_comision
            FROM detalle_comprobantes d
            JOIN tipos_gasto t ON d.id_tipo_gasto = t.id_tipo
            JOIN rendicion_cuentas r ON d.id_rendicion = r.id_rendicion
            ORDER BY d.fecha_crea DESC;
    END PRC_LISTAR_TODOS;

    PROCEDURE PRC_LISTAR_POR_RENDICION (
        p_id_rendicion IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT d.*, t.nombre_gasto, t.cuenta_contable, r.id_comision
            FROM detalle_comprobantes d
            JOIN tipos_gasto t ON d.id_tipo_gasto = t.id_tipo
            JOIN rendicion_cuentas r ON d.id_rendicion = r.id_rendicion
            WHERE d.id_rendicion = p_id_rendicion
            ORDER BY d.fecha_emision DESC;
    END PRC_LISTAR_POR_RENDICION;

    PROCEDURE PRC_OBTENER_COMPROBANTE (
        p_id_detalle IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT d.*, t.nombre_gasto, t.cuenta_contable, r.id_comision
            FROM detalle_comprobantes d
            JOIN tipos_gasto t ON d.id_tipo_gasto = t.id_tipo
            JOIN rendicion_cuentas r ON d.id_rendicion = r.id_rendicion
            WHERE d.id_detalle = p_id_detalle;
    END PRC_OBTENER_COMPROBANTE;

    PROCEDURE PRC_GUARDAR_COMPROBANTE (
        p_id_detalle IN OUT NUMBER,
        p_id_rendicion IN NUMBER,
        p_id_tipo_gasto IN NUMBER,
        p_fecha_emision IN TIMESTAMP,
        p_tipo_comprobante IN VARCHAR2,
        p_serie_comprobante IN VARCHAR2,
        p_numero_comprobante IN VARCHAR2,
        p_ruc_emisor IN VARCHAR2,
        p_razon_social_emisor IN VARCHAR2,
        p_monto_total IN NUMBER,
        p_archivo_url IN VARCHAR2,
        p_estado IN VARCHAR2,
        p_motivo_rechazo IN VARCHAR2,
        p_user_crea IN VARCHAR2
    ) AS
        v_monto_bruto NUMBER(12,2);
        v_monto_igv NUMBER(12,2);
    BEGIN
        -- Cálculo automático de importes  
        v_monto_bruto := ROUND(p_monto_total / 1.18, 2);
        v_monto_igv := p_monto_total - v_monto_bruto;

        IF p_id_detalle IS NULL THEN
            INSERT INTO detalle_comprobantes (
                id_rendicion, id_tipo_gasto, fecha_emision, tipo_comprobante,
                serie_comprobante, numero_comprobante, ruc_emisor, razon_social_emisor,
                monto_bruto, monto_igv, monto_total, archivo_url, estado,
                motivo_rechazo, user_crea, fecha_crea
            ) VALUES (
                p_id_rendicion, p_id_tipo_gasto, p_fecha_emision, p_tipo_comprobante,
                p_serie_comprobante, p_numero_comprobante, p_ruc_emisor, p_razon_social_emisor,
                v_monto_bruto, v_monto_igv, p_monto_total, p_archivo_url, p_estado,
                p_motivo_rechazo, p_user_crea, SYSTIMESTAMP
            ) RETURNING id_detalle INTO p_id_detalle;
        ELSE
            UPDATE detalle_comprobantes SET
                id_tipo_gasto = p_id_tipo_gasto,
                fecha_emision = p_fecha_emision,
                tipo_comprobante = p_tipo_comprobante,
                serie_comprobante = p_serie_comprobante,
                numero_comprobante = p_numero_comprobante,
                ruc_emisor = p_ruc_emisor,
                razon_social_emisor = p_razon_social_emisor,
                monto_bruto = v_monto_bruto,
                monto_igv = v_monto_igv,
                monto_total = p_monto_total,
                archivo_url = p_archivo_url,
                estado = p_estado,
                motivo_rechazo = p_motivo_rechazo,
                user_mod = p_user_crea,
                fecha_mod = SYSTIMESTAMP
            WHERE id_detalle = p_id_detalle;
        END IF;
        
        -- Recalcular total aceptado
        P_ACTUALIZAR_TOTAL_ACEPTADO(p_id_rendicion);
        
        COMMIT;
    END PRC_GUARDAR_COMPROBANTE;

    PROCEDURE PRC_ELIMINAR_COMPROBANTE (
        p_id_detalle IN NUMBER
    ) AS
        v_id_rendicion NUMBER;
    BEGIN
        SELECT id_rendicion INTO v_id_rendicion FROM detalle_comprobantes WHERE id_detalle = p_id_detalle;
        DELETE FROM detalle_comprobantes WHERE id_detalle = p_id_detalle;
        
        -- Recalcular tras eliminación
        P_ACTUALIZAR_TOTAL_ACEPTADO(v_id_rendicion);
        
        COMMIT;
    END PRC_ELIMINAR_COMPROBANTE;

END PKG_COMPROBANTES;
/