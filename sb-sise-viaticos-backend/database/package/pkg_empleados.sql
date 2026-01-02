-- PAQUETE: PKG_EMPLEADOS
-- DESCRIPCIÓN: Gestión de empleados

-- Tipo para representar un empleado en consultas
CREATE OR REPLACE TYPE T_EMPLEADO_REC AS OBJECT (
    id_empleado NUMBER,
    nombres VARCHAR2(100),
    apellidos VARCHAR2(100),
    dni CHAR(8),
    email VARCHAR2(100),
    id_nivel NUMBER,
    cuenta_bancaria VARCHAR2(20),
    user_crea VARCHAR2(30),
    fecha_crea TIMESTAMP
);
/

-- Tabla de empleados para la lista
CREATE OR REPLACE TYPE T_EMPLEADO_TAB AS TABLE OF T_EMPLEADO_REC;
/

CREATE OR REPLACE PACKAGE PKG_EMPLEADOS AS

    -- Listar 
    PROCEDURE PRC_LISTAR_EMPLEADOS (
        p_cursor OUT SYS_REFCURSOR
    );
    -- Guardar empleado crear o actualizar
    PROCEDURE PRC_GUARDAR_EMPLEADO (
        p_id_empleado IN NUMBER,               
        p_nombres IN VARCHAR2,
        p_apellidos IN VARCHAR2,
        p_dni IN CHAR,
        p_email IN VARCHAR2,
        p_id_nivel IN NUMBER,
        p_cuenta_bancaria IN VARCHAR2,
        p_user_crea IN VARCHAR2,
        p_id_empleado_out OUT NUMBER
    );

    -- Eliminar empleado y su usuario asociado
    PROCEDURE PRC_ELIMINAR_EMPLEADO (
        p_id_empleado IN NUMBER
    );

    -- Obtener empleado por ID
    FUNCTION FNC_OBTENER_EMPLEADO (
        p_id_empleado IN NUMBER
    ) RETURN T_EMPLEADO_REC;

    -- Validar si DNI ya existe excepto para el empleado actual
    FUNCTION FNC_VALIDAR_DNI (
        p_dni IN CHAR,
        p_id_empleado_actual IN NUMBER DEFAULT NULL
    ) RETURN NUMBER;   

    -- Validar si email ya existe excepto para el empleado actual
    FUNCTION FNC_VALIDAR_EMAIL (
        p_email IN VARCHAR2,
        p_id_empleado_actual IN NUMBER DEFAULT NULL
    ) RETURN NUMBER;  

END PKG_EMPLEADOS;
/

-- cuerpo del paquete
CREATE OR REPLACE PACKAGE BODY PKG_EMPLEADOS AS
 
    -- listar empleados
    PROCEDURE PRC_LISTAR_EMPLEADOS (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT 
                id_empleado,
                nombres,
                apellidos,
                dni,
                email,
                id_nivel,
                cuenta_bancaria,
                user_crea,
                fecha_crea
            FROM empleados
            ORDER BY apellidos, nombres;
    END PRC_LISTAR_EMPLEADOS;

    -- guardar empleado crear o actualizar
    PROCEDURE PRC_GUARDAR_EMPLEADO (
        p_id_empleado IN NUMBER,
        p_nombres IN VARCHAR2,
        p_apellidos IN VARCHAR2,
        p_dni IN CHAR,
        p_email IN VARCHAR2,
        p_id_nivel IN NUMBER,
        p_cuenta_bancaria IN VARCHAR2,
        p_user_crea IN VARCHAR2,
        p_id_empleado_out OUT NUMBER
    ) AS
        v_count NUMBER;
        v_is_new NUMBER;
    BEGIN
        -- Determinar si es nuevo o actualización
        v_is_new := CASE WHEN p_id_empleado IS NULL THEN 1 ELSE 0 END;

        -- validar DNI único
        IF FNC_VALIDAR_DNI(p_dni, p_id_empleado) = 1 THEN
            RAISE_APPLICATION_ERROR(-20001, 'Ya existe un empleado con el DNI: ' || p_dni);
        END IF;

        -- validar email único
        IF FNC_VALIDAR_EMAIL(p_email, p_id_empleado) = 1 THEN
            RAISE_APPLICATION_ERROR(-20002, 'Ya existe un empleado con el email: ' || p_email);
        END IF;

        -- crear nuevo empleado
        IF v_is_new = 1 THEN
            INSERT INTO empleados (
                nombres, apellidos, dni, email, id_nivel, cuenta_bancaria,
                user_crea, fecha_crea
            ) VALUES (
                p_nombres, p_apellidos, p_dni, p_email, p_id_nivel, p_cuenta_bancaria,
                p_user_crea, SYSTIMESTAMP
            ) RETURNING id_empleado INTO p_id_empleado_out;

        -- actualizar empleado
            -- actualizar empleado
        ELSE
            UPDATE empleados
            SET nombres = p_nombres,
                apellidos = p_apellidos,
                dni = p_dni,
                email = p_email,
                id_nivel = p_id_nivel,
                cuenta_bancaria = p_cuenta_bancaria,
                user_mod = p_user_crea,
                fecha_mod = SYSTIMESTAMP
            WHERE id_empleado = p_id_empleado;

            p_id_empleado_out := p_id_empleado;
        END IF;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END PRC_GUARDAR_EMPLEADO;

    -- eliminar empleado
    PROCEDURE PRC_ELIMINAR_EMPLEADO (
        p_id_empleado IN NUMBER
    ) AS
        v_count NUMBER;
    BEGIN
        -- verificar que el empleado existe
        SELECT COUNT(*) INTO v_count
        FROM empleados
        WHERE id_empleado = p_id_empleado;

        IF v_count = 0 THEN
            RAISE_APPLICATION_ERROR(-20003, 'Empleado no encontrado');
        END IF;
 
        DELETE FROM empleados
        WHERE id_empleado = p_id_empleado;

        COMMIT;

    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END PRC_ELIMINAR_EMPLEADO;

    -- obtener empleado
    FUNCTION FNC_OBTENER_EMPLEADO (
        p_id_empleado IN NUMBER
    ) RETURN T_EMPLEADO_REC AS
        v_empleado T_EMPLEADO_REC;
    BEGIN
        SELECT T_EMPLEADO_REC(
            id_empleado,
            nombres,
            apellidos,
            dni,
            email,
            id_nivel,
            cuenta_bancaria,
            user_crea,
            fecha_crea
        )
        INTO v_empleado
        FROM empleados
        WHERE id_empleado = p_id_empleado;

        RETURN v_empleado;

    EXCEPTION
        WHEN NO_DATA_FOUND THEN
            RAISE_APPLICATION_ERROR(-20004, 'Empleado no encontrado');
        WHEN OTHERS THEN
            RAISE;
    END FNC_OBTENER_EMPLEADO;

    -- validar DNI
    FUNCTION FNC_VALIDAR_DNI (
        p_dni IN CHAR,
        p_id_empleado_actual IN NUMBER DEFAULT NULL
    ) RETURN NUMBER AS
        v_count NUMBER;
    BEGIN
        IF p_id_empleado_actual IS NULL THEN
            -- Para nuevo empleado verificar si DNI existe
            SELECT COUNT(*)
            INTO v_count
            FROM empleados
            WHERE dni = p_dni;
        ELSE
            -- Para actualización  verificar si DNI existe en otro empleado
            SELECT COUNT(*)
            INTO v_count
            FROM empleados
            WHERE dni = p_dni
              AND id_empleado != p_id_empleado_actual;
        END IF;

        RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
    END FNC_VALIDAR_DNI;

    -- validar email
    FUNCTION FNC_VALIDAR_EMAIL (
        p_email IN VARCHAR2,
        p_id_empleado_actual IN NUMBER DEFAULT NULL
    ) RETURN NUMBER AS
        v_count NUMBER;
    BEGIN
        IF p_id_empleado_actual IS NULL THEN
            -- Para nuevo empleado 
            SELECT COUNT(*)
            INTO v_count
            FROM empleados
            WHERE email = p_email;
        ELSE
            -- Para actualizaciónc
            SELECT COUNT(*)
            INTO v_count
            FROM empleados
            WHERE email = p_email
              AND id_empleado != p_id_empleado_actual;
        END IF;

        RETURN CASE WHEN v_count > 0 THEN 1 ELSE 0 END;
    END FNC_VALIDAR_EMAIL;

END PKG_EMPLEADOS;
/