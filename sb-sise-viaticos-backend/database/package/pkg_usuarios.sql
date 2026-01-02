-- Active: 1766458157960@@127.0.0.1@1521@XE@C
-- PAQUETE: PKG_USUARIOS
-- DESCRIPCIÓN: Gestión de usuarios del sistema (con soporte multi-rol)

-- lista de IDs de roles
CREATE OR REPLACE TYPE T_ID_TAB AS TABLE OF NUMBER;
/

CREATE OR REPLACE PACKAGE PKG_USUARIOS AS

    -- Listar todos los usuarios
    PROCEDURE PRC_LISTAR_USUARIOS (
        p_cursor OUT SYS_REFCURSOR
    );

    -- Buscar usuario por username (incluye roles)
    FUNCTION FNC_OBTENER_POR_USERNAME (
        p_username IN VARCHAR2
    ) RETURN SYS_REFCURSOR;

    -- Buscar usuario por email
    FUNCTION FNC_OBTENER_POR_EMAIL (
        p_email IN VARCHAR2
    ) RETURN SYS_REFCURSOR;

    -- Obtener roles de un usuario
    PROCEDURE PRC_OBTENER_ROLES_USUARIO (
        p_id_usuario IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Crear nuevo usuario asociado a un empleado (sin roles inicialmente)
    PROCEDURE PRC_CREAR_USUARIO (
        p_id_empleado IN NUMBER,
        p_username IN VARCHAR2,
        p_email IN VARCHAR2,
        p_password IN VARCHAR2,
        p_user_crea IN VARCHAR2,
        p_id_usuario OUT NUMBER
    );

    -- Asignar roles a un usuario (reemplaza los existentes)
    -- Recibe una tabla de IDs de roles directamente
    PROCEDURE PRC_ASIGNAR_ROLES (
        p_id_usuario IN NUMBER,
        p_roles_ids IN T_ID_TAB,
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

    PROCEDURE PRC_ACTUALIZAR_PASSWORD (
        p_id_usuario IN NUMBER,
        p_password IN VARCHAR2
    );

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
                e.nombres AS nombres_empleado,
                e.apellidos AS apellidos_empleado,
                u.username,
                u.email,
                u.password,
                u.activo,
                u.user_crea,
                u.fecha_crea
            FROM usuarios u
            INNER JOIN empleados e ON u.id_empleado = e.id_empleado
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
                e.nombres AS nombres_empleado,
                e.apellidos AS apellidos_empleado,
                u.username,
                u.email,
                u.password,
                u.activo,
                u.user_crea,
                u.fecha_crea
            FROM usuarios u
            INNER JOIN empleados e ON u.id_empleado = e.id_empleado
            WHERE u.username = p_username;
        
        RETURN v_cursor;
    END FNC_OBTENER_POR_USERNAME;

    -- Buscar usuario por email
    FUNCTION FNC_OBTENER_POR_EMAIL (
        p_email IN VARCHAR2
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT 
                u.id_usuario,
                u.id_empleado,
                e.nombres AS nombres_empleado,
                e.apellidos AS apellidos_empleado,
                u.username,
                u.email,
                u.password,
                u.activo,
                u.user_crea,
                u.fecha_crea
            FROM usuarios u
            INNER JOIN empleados e ON u.id_empleado = e.id_empleado
            WHERE u.email = p_email;
        
        RETURN v_cursor;
    END FNC_OBTENER_POR_EMAIL;

    -- Obtener roles de un usuario
    PROCEDURE PRC_OBTENER_ROLES_USUARIO (
        p_id_usuario IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT r.id_rol, r.codigo_rol, r.nombre_rol
            FROM usuarios_roles ur
            INNER JOIN roles r ON ur.id_rol = r.id_rol
            WHERE ur.id_usuario = p_id_usuario
            ORDER BY r.nombre_rol;
    END PRC_OBTENER_ROLES_USUARIO;

    -- Crear nuevo usuario asociado a un empleado
    PROCEDURE PRC_CREAR_USUARIO (
        p_id_empleado IN NUMBER,
        p_username IN VARCHAR2,
        p_email IN VARCHAR2,
        p_password IN VARCHAR2,
        p_user_crea IN VARCHAR2,
        p_id_usuario OUT NUMBER
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
            activo,
            user_crea,
            fecha_crea
        ) VALUES (
            p_id_empleado,
            p_username,
            p_email,
            p_password,
            1,
            p_user_crea,
            SYSTIMESTAMP
        ) RETURNING id_usuario INTO p_id_usuario;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END PRC_CREAR_USUARIO;
 
    PROCEDURE PRC_ASIGNAR_ROLES (
        p_id_usuario IN NUMBER,
        p_roles_ids IN T_ID_TAB,
        p_user_crea IN VARCHAR2
    ) AS
    BEGIN
        -- Eliminar roles actuales
        DELETE FROM usuarios_roles WHERE id_usuario = p_id_usuario;

        
        IF p_roles_ids IS NOT NULL AND p_roles_ids.COUNT > 0 THEN
            FOR i IN 1..p_roles_ids.COUNT LOOP
                INSERT INTO usuarios_roles (id_usuario, id_rol, user_crea, fecha_asignacion)
                VALUES (p_id_usuario, p_roles_ids(i), p_user_crea, SYSTIMESTAMP);
            END LOOP;
        END IF;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END PRC_ASIGNAR_ROLES;

    -- Eliminar usuario por ID de empleado
    PROCEDURE PRC_ELIMINAR_USUARIO_POR_EMPLEADO (
        p_id_empleado IN NUMBER
    ) AS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM usuarios
        WHERE id_empleado = p_id_empleado;

        IF v_count = 0 THEN 
            RETURN;
        END IF;

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

    -- Actualizar contraseña
    PROCEDURE PRC_ACTUALIZAR_PASSWORD (
        p_id_usuario IN NUMBER,
        p_password IN VARCHAR2
    ) AS
    BEGIN
        UPDATE usuarios
        SET password = p_password,
            fecha_mod = SYSTIMESTAMP
        WHERE id_usuario = p_id_usuario;
        
        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END PRC_ACTUALIZAR_PASSWORD;

END PKG_USUARIOS;
/