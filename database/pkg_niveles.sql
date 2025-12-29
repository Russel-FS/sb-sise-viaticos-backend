-- PAQUETE: PKG_NIVELES
-- DESCRIPCIÓN: Gestión de niveles jerárquicos

CREATE OR REPLACE PACKAGE PKG_NIVELES AS

    -- Listar todos los niveles
    PROCEDURE PRC_LISTAR_NIVELES (
        p_cursor OUT SYS_REFCURSOR
    );

    -- Obtener nivel por ID
    FUNCTION FNC_OBTENER_POR_ID (
        p_id_nivel IN NUMBER
    ) RETURN SYS_REFCURSOR;

    -- Obtener nivel por Nombre  
    FUNCTION FNC_OBTENER_POR_NOMBRE (
        p_nombre IN VARCHAR2
    ) RETURN SYS_REFCURSOR;

    -- Listar niveles por estado activo
    PROCEDURE PRC_LISTAR_POR_ACTIVO (
        p_activo IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Guardar nivel - insertar o actualizar
    PROCEDURE PRC_GUARDAR_NIVEL (
        p_id_nivel IN OUT NUMBER,
        p_nombre IN VARCHAR2,
        p_desc IN VARCHAR2,
        p_activo IN NUMBER,
        p_user IN VARCHAR2
    );

    -- Eliminar nivel
    PROCEDURE PRC_ELIMINAR_NIVEL (
        p_id_nivel IN NUMBER
    );

END PKG_NIVELES;
/

CREATE OR REPLACE PACKAGE BODY PKG_NIVELES AS

    PROCEDURE PRC_LISTAR_NIVELES (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT id_nivel, nombre_nivel, descripcion, user_crea, fecha_crea, activo
            FROM niveles_jerarquicos
            ORDER BY id_nivel;
    END PRC_LISTAR_NIVELES;

    FUNCTION FNC_OBTENER_POR_ID (
        p_id_nivel IN NUMBER
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT id_nivel, nombre_nivel, descripcion, user_crea, fecha_crea, activo
            FROM niveles_jerarquicos
            WHERE id_nivel = p_id_nivel;
        RETURN v_cursor;
    END FNC_OBTENER_POR_ID;

    FUNCTION FNC_OBTENER_POR_NOMBRE (
        p_nombre IN VARCHAR2
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT id_nivel, nombre_nivel, descripcion, user_crea, fecha_crea, activo
            FROM niveles_jerarquicos
            WHERE UPPER(nombre_nivel) = UPPER(p_nombre);
        RETURN v_cursor;
    END FNC_OBTENER_POR_NOMBRE;

    PROCEDURE PRC_LISTAR_POR_ACTIVO (
        p_activo IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT id_nivel, nombre_nivel, descripcion, user_crea, fecha_crea, activo
            FROM niveles_jerarquicos
            WHERE activo = p_activo
            ORDER BY id_nivel;
    END PRC_LISTAR_POR_ACTIVO;

    PROCEDURE PRC_GUARDAR_NIVEL (
        p_id_nivel IN OUT NUMBER,
        p_nombre IN VARCHAR2,
        p_desc IN VARCHAR2,
        p_activo IN NUMBER,
        p_user IN VARCHAR2
    ) AS
    BEGIN
        IF p_id_nivel IS NULL THEN
            INSERT INTO niveles_jerarquicos (
                nombre_nivel, descripcion, activo, user_crea, fecha_crea
            ) VALUES (
                p_nombre, p_desc, p_activo, p_user, SYSTIMESTAMP
            ) RETURNING id_nivel INTO p_id_nivel;
        ELSE
            UPDATE niveles_jerarquicos SET
                nombre_nivel = p_nombre,
                descripcion = p_desc,
                activo = p_activo,
                user_mod = p_user,
                fecha_mod = SYSTIMESTAMP
            WHERE id_nivel = p_id_nivel;
        END IF;
        COMMIT;
    END PRC_GUARDAR_NIVEL;

    PROCEDURE PRC_ELIMINAR_NIVEL (
        p_id_nivel IN NUMBER
    ) AS
    BEGIN
        DELETE FROM niveles_jerarquicos WHERE id_nivel = p_id_nivel;
        COMMIT;
    END PRC_ELIMINAR_NIVEL;

END PKG_NIVELES;
/