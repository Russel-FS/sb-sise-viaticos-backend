-- PAQUETE: PKG_ROLES
-- DESCRIPCIÓN: Gestión de roles de usuario

CREATE OR REPLACE PACKAGE PKG_ROLES AS

    -- Listar todos los roles
    PROCEDURE PRC_LISTAR_ROLES (
        p_cursor OUT SYS_REFCURSOR
    );

    -- Obtener rol por ID
    FUNCTION FNC_OBTENER_POR_ID (
        p_id_rol IN NUMBER
    ) RETURN SYS_REFCURSOR;

    -- Obtener rol por Código
    FUNCTION FNC_OBTENER_POR_CODIGO (
        p_codigo IN VARCHAR2
    ) RETURN SYS_REFCURSOR;

    -- Guardar rol - insertar o actualizar
    PROCEDURE PRC_GUARDAR_ROL (
        p_id_rol IN OUT NUMBER,
        p_nombre IN VARCHAR2,
        p_codigo IN VARCHAR2
    );

    -- Eliminar rol
    PROCEDURE PRC_ELIMINAR_ROL (
        p_id_rol IN NUMBER
    );

END PKG_ROLES;
/

CREATE OR REPLACE PACKAGE BODY PKG_ROLES AS

    PROCEDURE PRC_LISTAR_ROLES (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT id_rol, nombre_rol, codigo
            FROM roles
            ORDER BY nombre_rol;
    END PRC_LISTAR_ROLES;

    FUNCTION FNC_OBTENER_POR_ID (
        p_id_rol IN NUMBER
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT id_rol, nombre_rol, codigo
            FROM roles
            WHERE id_rol = p_id_rol;
        RETURN v_cursor;
    END FNC_OBTENER_POR_ID;

    FUNCTION FNC_OBTENER_POR_CODIGO (
        p_codigo IN VARCHAR2
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT id_rol, nombre_rol, codigo
            FROM roles
            WHERE UPPER(codigo) = UPPER(p_codigo);
        RETURN v_cursor;
    END FNC_OBTENER_POR_CODIGO;

    PROCEDURE PRC_GUARDAR_ROL (
        p_id_rol IN OUT NUMBER,
        p_nombre IN VARCHAR2,
        p_codigo IN VARCHAR2
    ) AS
    BEGIN
        IF p_id_rol IS NULL THEN
            INSERT INTO roles (
                nombre_rol, codigo
            ) VALUES (
                p_nombre, p_codigo
            ) RETURNING id_rol INTO p_id_rol;
        ELSE
            UPDATE roles SET
                nombre_rol = p_nombre,
                codigo = p_codigo
            WHERE id_rol = p_id_rol;
        END IF;
        COMMIT;
    END PRC_GUARDAR_ROL;

    PROCEDURE PRC_ELIMINAR_ROL (
        p_id_rol IN NUMBER
    ) AS
    BEGIN
        DELETE FROM roles WHERE id_rol = p_id_rol;
        COMMIT;
    END PRC_ELIMINAR_ROL;

END PKG_ROLES;
/