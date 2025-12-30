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
        p_monto_bruto IN NUMBER,
        p_monto_igv IN NUMBER,
        p_monto_total IN NUMBER,
        p_imagen_url IN VARCHAR2,
        p_validado IN NUMBER,
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

    PROCEDURE PRC_LISTAR_TODOS (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM detalle_comprobantes
            ORDER BY fecha_crea DESC;
    END PRC_LISTAR_TODOS;

    PROCEDURE PRC_LISTAR_POR_RENDICION (
        p_id_rendicion IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM detalle_comprobantes 
            WHERE id_rendicion = p_id_rendicion
            ORDER BY fecha_emision DESC;
    END PRC_LISTAR_POR_RENDICION;

    PROCEDURE PRC_OBTENER_COMPROBANTE (
        p_id_detalle IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM detalle_comprobantes 
            WHERE id_detalle = p_id_detalle;
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
        p_monto_bruto IN NUMBER,
        p_monto_igv IN NUMBER,
        p_monto_total IN NUMBER,
        p_imagen_url IN VARCHAR2,
        p_validado IN NUMBER,
        p_motivo_rechazo IN VARCHAR2,
        p_user_crea IN VARCHAR2
    ) AS
    BEGIN
        IF p_id_detalle IS NULL THEN
            INSERT INTO detalle_comprobantes (
                id_rendicion, id_tipo_gasto, fecha_emision, tipo_comprobante,
                serie_comprobante, numero_comprobante, ruc_emisor, razon_social_emisor,
                monto_bruto, monto_igv, monto_total, imagen_url, validado,
                motivo_rechazo, user_crea, fecha_crea
            ) VALUES (
                p_id_rendicion, p_id_tipo_gasto, p_fecha_emision, p_tipo_comprobante,
                p_serie_comprobante, p_numero_comprobante, p_ruc_emisor, p_razon_social_emisor,
                p_monto_bruto, p_monto_igv, p_monto_total, p_imagen_url, p_validado,
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
                monto_bruto = p_monto_bruto,
                monto_igv = p_monto_igv,
                monto_total = p_monto_total,
                imagen_url = p_imagen_url,
                validado = p_validado,
                motivo_rechazo = p_motivo_rechazo,
                user_mod = p_user_crea,
                fecha_mod = SYSTIMESTAMP
            WHERE id_detalle = p_id_detalle;
        END IF;
        COMMIT;
    END PRC_GUARDAR_COMPROBANTE;

    PROCEDURE PRC_ELIMINAR_COMPROBANTE (
        p_id_detalle IN NUMBER
    ) AS
    BEGIN
        DELETE FROM detalle_comprobantes WHERE id_detalle = p_id_detalle;
        COMMIT;
    END PRC_ELIMINAR_COMPROBANTE;

END PKG_COMPROBANTES;
/