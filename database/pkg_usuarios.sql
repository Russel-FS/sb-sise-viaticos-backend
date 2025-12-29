-- PAQUETE: PKG_USUARIOS
-- DESCRIPCIÓN: Gestión de usuarios del sistema

CREATE OR REPLACE PACKAGE PKG_USUARIOS AS

    -- Listar todos los usuarios
    PROCEDURE PRC_LISTAR_USUARIOS (
        p_cursor OUT SYS_REFCURSOR
    );

    -- Buscar usuario por username
    FUNCTION FNC_OBTENER_POR_USERNAME (
        p_username IN VARCHAR2
    ) RETURN SYS_REFCURSOR;

    -- Crear nuevo usuario asociado a un empleado
    PROCEDURE PRC_CREAR_USUARIO (
        p_id_empleado IN NUMBER,
        p_username IN VARCHAR2,
        p_email IN VARCHAR2,
        p_password IN VARCHAR2,
        p_rol_id IN NUMBER,
        p_user_crea IN VARCHAR2
    );

    -- Eliminar usuario por ID de empleado
    PROCEDURE PRC_ELIMINAR_USUARIO_POR_EMPLEADO (
        p_id_empleado IN NUMBER
    );

    -- Obtener ID de usuario por ID de empleado
    FUNCTION FNC_OBTENER_USUARIO_POR_EMPLEADO (
        p_id_empleado IN NUMBER
    ) RETURN NUMBER;

END PKG_USUARIOS;
/

-- Cuerpo del paquete
CREATE OR REPLACE PACKAGE BODY PKG_USUARIOS AS

    -- Listar todos los usuarios
    PROCEDURE PRC_LISTAR_USUARIOS (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT 
                u.id_usuario,
                u.id_empleado,
                u.username,
                u.email,
                u.password,
                u.id_rol,
                u.activo,
                u.user_crea,
                u.fecha_crea
            FROM usuarios u
            ORDER BY u.username;
    END PRC_LISTAR_USUARIOS;

    -- Buscar usuario por username
    FUNCTION FNC_OBTENER_POR_USERNAME (
        p_username IN VARCHAR2
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT 
                u.id_usuario,
                u.id_empleado,
                u.username,
                u.email,
                u.password,
                u.id_rol,
                u.activo,
                u.user_crea,
                u.fecha_crea
            FROM usuarios u
            WHERE u.username = p_username;
        
        RETURN v_cursor;
    END FNC_OBTENER_POR_USERNAME;

    -- Crear nuevo usuario asociado a un empleado
    PROCEDURE PRC_CREAR_USUARIO (
        p_id_empleado IN NUMBER,
        p_username IN VARCHAR2,
        p_email IN VARCHAR2,
        p_password IN VARCHAR2,
        p_rol_id IN NUMBER,
        p_user_crea IN VARCHAR2
    ) AS
        v_count NUMBER;
    BEGIN
        -- Verificar que el empleado existe
        SELECT COUNT(*) INTO v_count
        FROM empleados
        WHERE id_empleado = p_id_empleado;

        IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20101, 'Empleado no encontrado');
        END IF;

        -- Verificar que no existe ya un usuario para este empleado
        SELECT COUNT(*) INTO v_count
        FROM usuarios
        WHERE id_empleado = p_id_empleado;

        IF v_count > 0 THEN
            RAISE_APPLICATION_ERROR(-20102, 'Ya existe un usuario para este empleado');
        END IF;

        -- Crear el usuario
        INSERT INTO usuarios (
            id_empleado,
            username,
            email,
            password,
            id_rol,
            activo,
            user_crea,
            fecha_crea
        ) VALUES (
            p_id_empleado,
            p_username,
            p_email,
            p_password,
            p_rol_id,
            1,  -- activo por defecto
            p_user_crea,
            SYSTIMESTAMP
        );

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END PRC_CREAR_USUARIO;

    -- Eliminar usuario por ID de empleado
    PROCEDURE PRC_ELIMINAR_USUARIO_POR_EMPLEADO (
        p_id_empleado IN NUMBER
    ) AS
        v_count NUMBER;
    BEGIN
        -- Verificar que el usuario existe
        SELECT COUNT(*) INTO v_count
        FROM usuarios
        WHERE id_empleado = p_id_empleado;

        IF v_count = 0 THEN 
            RETURN;
        END IF;

        -- Eliminar el usuario
        DELETE FROM usuarios
        WHERE id_empleado = p_id_empleado;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END PRC_ELIMINAR_USUARIO_POR_EMPLEADO;

    -- Obtener ID de usuario por ID de empleado
    FUNCTION FNC_OBTENER_USUARIO_POR_EMPLEADO (
        p_id_empleado IN NUMBER
    ) RETURN NUMBER AS
        v_id_usuario NUMBER;
    BEGIN
        SELECT id_usuario
        INTO v_id_usuario
        FROM usuarios
        WHERE id_empleado = p_id_empleado;

        RETURN v_id_usuario;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RETURN NULL;
        WHEN OTHERS THEN
            RAISE;
    END FNC_OBTENER_USUARIO_POR_EMPLEADO;

END PKG_USUARIOS;
/