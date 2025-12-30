-- PAQUETE: PKG_RENDICIONES
-- DESCRIPCIÓN: Gestión de rendiciones de cuentas

CREATE OR REPLACE PACKAGE PKG_RENDICIONES AS

    -- Listar todas las rendiciones
    PROCEDURE PRC_LISTAR_TODAS (
        p_cursor OUT SYS_REFCURSOR
    );

    -- Obtener rendición por ID
    FUNCTION FNC_OBTENER_POR_ID (
        p_id_rendicion IN NUMBER
    ) RETURN SYS_REFCURSOR;

    -- Obtener rendición por ID de solicitud
    FUNCTION FNC_OBTENER_POR_SOLICITUD (
        p_id_comision IN NUMBER
    ) RETURN SYS_REFCURSOR;

    -- Guardar rendición - insertar o actualizar
    PROCEDURE PRC_GUARDAR_RENDICION (
        p_id_rendicion IN OUT NUMBER,
        p_id_comision IN NUMBER,
        p_fecha_pres IN TIMESTAMP,
        p_total_bruto IN NUMBER,
        p_total_aceptado IN NUMBER,
        p_comentarios IN VARCHAR2,
        p_user IN VARCHAR2
    );

    -- Eliminar rendición
    PROCEDURE PRC_ELIMINAR_RENDICION (
        p_id_rendicion IN NUMBER
    );

END PKG_RENDICIONES;
/

CREATE OR REPLACE PACKAGE BODY PKG_RENDICIONES AS

    PROCEDURE PRC_LISTAR_TODAS (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM rendicion_cuentas
            ORDER BY fecha_crea DESC;
    END PRC_LISTAR_TODAS;

    FUNCTION FNC_OBTENER_POR_ID (
        p_id_rendicion IN NUMBER
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT * FROM rendicion_cuentas
            WHERE id_rendicion = p_id_rendicion;
        RETURN v_cursor;
    END FNC_OBTENER_POR_ID;

    FUNCTION FNC_OBTENER_POR_SOLICITUD (
        p_id_comision IN NUMBER
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT * FROM rendicion_cuentas
            WHERE id_comision = p_id_comision;
        RETURN v_cursor;
    END FNC_OBTENER_POR_SOLICITUD;

    PROCEDURE PRC_GUARDAR_RENDICION (
        p_id_rendicion IN OUT NUMBER,
        p_id_comision IN NUMBER,
        p_fecha_pres IN TIMESTAMP,
        p_total_bruto IN NUMBER,
        p_total_aceptado IN NUMBER,
        p_comentarios IN VARCHAR2,
        p_user IN VARCHAR2
    ) AS
    BEGIN
        IF p_id_rendicion IS NULL THEN
            INSERT INTO rendicion_cuentas (
                id_comision, fecha_presentacion, total_gastado_bruto,
                total_aceptado, comentarios_empleado, user_crea, fecha_crea
            ) VALUES (
                p_id_comision, p_fecha_pres, p_total_bruto,
                p_total_aceptado, p_comentarios, p_user, SYSTIMESTAMP
            ) RETURNING id_rendicion INTO p_id_rendicion;
        ELSE
            UPDATE rendicion_cuentas SET
                id_comision = p_id_comision,
                fecha_presentacion = p_fecha_pres,
                total_gastado_bruto = p_total_bruto,
                total_aceptado = p_total_aceptado,
                comentarios_empleado = p_comentarios,
                user_mod = p_user,
                fecha_mod = SYSTIMESTAMP
            WHERE id_rendicion = p_id_rendicion;
        END IF;
        COMMIT;
    END PRC_GUARDAR_RENDICION;

    PROCEDURE PRC_ELIMINAR_RENDICION (
        p_id_rendicion IN NUMBER
    ) AS
    BEGIN
        DELETE FROM rendicion_cuentas WHERE id_rendicion = p_id_rendicion;
        COMMIT;
    END PRC_ELIMINAR_RENDICION;

END PKG_RENDICIONES;
/