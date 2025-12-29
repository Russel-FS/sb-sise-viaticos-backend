-- Active: 1766458157960@@127.0.0.1@1521@XE@C
-- PAQUETE: PKG_ZONAS
-- DESCRIPCIÓN: Gestión de zonas geográficas

CREATE OR REPLACE PACKAGE PKG_ZONAS AS

    -- Listar todas las zonas
    PROCEDURE PRC_LISTAR_ZONAS (
        p_cursor OUT SYS_REFCURSOR
    );

    -- Obtener zona por Nombre
    FUNCTION FNC_OBTENER_POR_NOMBRE (
        p_nombre IN VARCHAR2
    ) RETURN SYS_REFCURSOR;

    -- Listar zonas por estado
    PROCEDURE PRC_LISTAR_POR_ACTIVO (
        p_activo IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Guardar zona - insertar o actualizar
    PROCEDURE PRC_GUARDAR_ZONA (
        p_id_zona IN OUT NUMBER,
        p_nombre IN VARCHAR2,
        p_desc IN VARCHAR2,
        p_activo IN NUMBER,
        p_user IN VARCHAR2
    );

    -- Eliminar zona
    PROCEDURE PRC_ELIMINAR_ZONA (
        p_id_zona IN NUMBER
    );

END PKG_ZONAS;
/

CREATE OR REPLACE PACKAGE BODY PKG_ZONAS AS

    PROCEDURE PRC_LISTAR_ZONAS (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT id_zona, nombre_zona, descripcion, user_crea, fecha_crea, activo
            FROM zonas_geograficas
            ORDER BY nombre_zona;
    END PRC_LISTAR_ZONAS;

    FUNCTION FNC_OBTENER_POR_NOMBRE (
        p_nombre IN VARCHAR2
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT id_zona, nombre_zona, descripcion, user_crea, fecha_crea, activo
            FROM zonas_geograficas
            WHERE UPPER(nombre_zona) = UPPER(p_nombre);
        RETURN v_cursor;
    END FNC_OBTENER_POR_NOMBRE;

    PROCEDURE PRC_LISTAR_POR_ACTIVO (
        p_activo IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT id_zona, nombre_zona, descripcion, user_crea, fecha_crea, activo
            FROM zonas_geograficas
            WHERE activo = p_activo
            ORDER BY nombre_zona;
    END PRC_LISTAR_POR_ACTIVO;

    FUNCTION FNC_OBTENER_POR_ID (
        p_id_zona IN NUMBER
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT id_zona, nombre_zona, descripcion, user_crea, fecha_crea, activo
            FROM zonas_geograficas
            WHERE id_zona = p_id_zona;
        RETURN v_cursor;
    END FNC_OBTENER_POR_ID;

    PROCEDURE PRC_GUARDAR_ZONA (
        p_id_zona IN OUT NUMBER,
        p_nombre IN VARCHAR2,
        p_desc IN VARCHAR2,
        p_activo IN NUMBER,
        p_user IN VARCHAR2
    ) AS
    BEGIN
        IF p_id_zona IS NULL THEN
            INSERT INTO zonas_geograficas (
                nombre_zona, descripcion, activo, user_crea, fecha_crea
            ) VALUES (
                p_nombre, p_desc, p_activo, p_user, SYSTIMESTAMP
            ) RETURNING id_zona INTO p_id_zona;
        ELSE
            UPDATE zonas_geograficas SET
                nombre_zona = p_nombre,
                descripcion = p_desc,
                activo = p_activo,
                user_mod = p_user,
                fecha_mod = SYSTIMESTAMP
            WHERE id_zona = p_id_zona;
        END IF;
        COMMIT;
    END PRC_GUARDAR_ZONA;

    PROCEDURE PRC_ELIMINAR_ZONA (
        p_id_zona IN NUMBER
    ) AS
    BEGIN
        DELETE FROM zonas_geograficas WHERE id_zona = p_id_zona;
        COMMIT;
    END PRC_ELIMINAR_ZONA;

END PKG_ZONAS;
/