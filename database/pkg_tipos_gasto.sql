-- PAQUETE: PKG_TIPOS_GASTO
-- DESCRIPCIÓN: Gestión de tipos de gasto

CREATE OR REPLACE PACKAGE PKG_TIPOS_GASTO AS

    -- Listar todos los tipos de gasto
    PROCEDURE PRC_LISTAR_TIPOS (
        p_cursor OUT SYS_REFCURSOR
    );

    -- Obtener tipo de gasto por ID
    FUNCTION FNC_OBTENER_POR_ID (
        p_id_tipo IN NUMBER
    ) RETURN SYS_REFCURSOR;
    
    -- Obtener tipo de gasto por nombre
    FUNCTION FNC_OBTENER_POR_NOMBRE (
        p_nombre IN VARCHAR2
    ) RETURN SYS_REFCURSOR;

    -- Listar tipos de gasto por estado
    PROCEDURE PRC_LISTAR_POR_ACTIVO (
        p_activo IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Guardar tipo de gasto - insertar o actualizar
    PROCEDURE PRC_GUARDAR_TIPO (
        p_id_tipo IN OUT NUMBER,
        p_nombre IN VARCHAR2,
        p_requiere_factura IN NUMBER,
        p_cuenta_contable IN VARCHAR2,
        p_es_asignable IN NUMBER,
        p_activo IN NUMBER,
        p_user IN VARCHAR2
    );

    -- Eliminar tipo de gasto
    PROCEDURE PRC_ELIMINAR_TIPO (
        p_id_tipo IN NUMBER
    );

    -- Contar tipos de gasto
    FUNCTION FNC_CONTAR_TIPOS RETURN NUMBER;

END PKG_TIPOS_GASTO;
/

CREATE OR REPLACE PACKAGE BODY PKG_TIPOS_GASTO AS

    PROCEDURE PRC_LISTAR_TIPOS (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT id_tipo, nombre_gasto, requiere_factura, cuenta_contable, 
                   es_asignable_por_dia, user_crea, fecha_crea, activo
            FROM tipos_gasto
            ORDER BY nombre_gasto;
    END PRC_LISTAR_TIPOS;

    FUNCTION FNC_OBTENER_POR_ID (
        p_id_tipo IN NUMBER
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT id_tipo, nombre_gasto, requiere_factura, cuenta_contable, 
                   es_asignable_por_dia, user_crea, fecha_crea, activo
            FROM tipos_gasto
            WHERE id_tipo = p_id_tipo;
        RETURN v_cursor;
    END FNC_OBTENER_POR_ID;
    
    FUNCTION FNC_OBTENER_POR_NOMBRE (
        p_nombre IN VARCHAR2
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT id_tipo, nombre_gasto, requiere_factura, cuenta_contable, 
                   es_asignable_por_dia, user_crea, fecha_crea, activo
            FROM tipos_gasto
            WHERE UPPER(nombre_gasto) = UPPER(p_nombre);
        RETURN v_cursor;
    END FNC_OBTENER_POR_NOMBRE;

    PROCEDURE PRC_LISTAR_POR_ACTIVO (
        p_activo IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT id_tipo, nombre_gasto, requiere_factura, cuenta_contable, 
                   es_asignable_por_dia, user_crea, fecha_crea, activo
            FROM tipos_gasto
            WHERE activo = p_activo
            ORDER BY nombre_gasto;
    END PRC_LISTAR_POR_ACTIVO;

    PROCEDURE PRC_GUARDAR_TIPO (
        p_id_tipo IN OUT NUMBER,
        p_nombre IN VARCHAR2,
        p_requiere_factura IN NUMBER,
        p_cuenta_contable IN VARCHAR2,
        p_es_asignable IN NUMBER,
        p_activo IN NUMBER,
        p_user IN VARCHAR2
    ) AS
    BEGIN
        IF p_id_tipo IS NULL THEN
            INSERT INTO tipos_gasto (
                nombre_gasto, requiere_factura, cuenta_contable, 
                es_asignable_por_dia, activo, user_crea, fecha_crea
            ) VALUES (
                p_nombre, p_requiere_factura, p_cuenta_contable, 
                p_es_asignable, p_activo, p_user, SYSTIMESTAMP
            ) RETURNING id_tipo INTO p_id_tipo;
        ELSE
            UPDATE tipos_gasto SET
                nombre_gasto = p_nombre,
                requiere_factura = p_requiere_factura,
                cuenta_contable = p_cuenta_contable,
                es_asignable_por_dia = p_es_asignable,
                activo = p_activo,
                user_mod = p_user,
                fecha_mod = SYSTIMESTAMP
            WHERE id_tipo = p_id_tipo;
        END IF;
        COMMIT;
    END PRC_GUARDAR_TIPO;

    PROCEDURE PRC_ELIMINAR_TIPO (
        p_id_tipo IN NUMBER
    ) AS
    BEGIN
        DELETE FROM tipos_gasto WHERE id_tipo = p_id_tipo;
        COMMIT;
    END PRC_ELIMINAR_TIPO;

    FUNCTION FNC_CONTAR_TIPOS RETURN NUMBER AS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count FROM tipos_gasto;
        RETURN v_count;
    END FNC_CONTAR_TIPOS;

END PKG_TIPOS_GASTO;
/