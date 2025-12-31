-- PAQUETE: PKG_TARIFARIO
-- DESCRIPCIÓN: Gestión de tarifario de viáticos

CREATE OR REPLACE PACKAGE PKG_TARIFARIO AS

    -- Listar todo el tarifario con detalles 
    PROCEDURE PRC_LISTAR_TARIFARIO (
        p_cursor OUT SYS_REFCURSOR
    );

    -- Listar tarifario por nivel y zona
    PROCEDURE PRC_LISTAR_POR_NIVEL_ZONA (
        p_id_nivel IN NUMBER,
        p_id_zona IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Listar tarifario por estado
    PROCEDURE PRC_LISTAR_POR_ACTIVO (
        p_activo IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    );

    -- Obtener tarifa por ID
    FUNCTION FNC_OBTENER_POR_ID (
        p_id_tarifa IN NUMBER
    ) RETURN SYS_REFCURSOR;

    -- Guardar tarifa - insertar o actualizar
    PROCEDURE PRC_GUARDAR_TARIFA (
        p_id_tarifa IN OUT NUMBER,
        p_id_zona IN NUMBER,
        p_id_nivel IN NUMBER,
        p_id_tipo_gasto IN NUMBER,
        p_monto IN NUMBER,
        p_moneda IN VARCHAR2,
        p_activo IN NUMBER,
        p_user IN VARCHAR2
    );

    -- Eliminar tarifa
    PROCEDURE PRC_ELIMINAR_TARIFA (
        p_id_tarifa IN NUMBER
    );

    -- Contar tarifas
    FUNCTION FNC_CONTAR_TARIFAS RETURN NUMBER;

END PKG_TARIFARIO;
/

CREATE OR REPLACE PACKAGE BODY PKG_TARIFARIO AS

    PROCEDURE PRC_LISTAR_TARIFARIO (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT t.id_tarifa, t.id_zona, z.nombre_zona, t.id_nivel, n.nombre_nivel, 
                   t.id_tipo_gasto, tg.nombre_gasto, tg.es_asignable_por_dia, t.monto_limite_diario, t.moneda,
                   t.user_crea, t.fecha_crea, t.activo
            FROM tarifario_viaticos t
            JOIN zonas_geograficas z ON t.id_zona = z.id_zona
            JOIN niveles_jerarquicos n ON t.id_nivel = n.id_nivel
            JOIN tipos_gasto tg ON t.id_tipo_gasto = tg.id_tipo
            ORDER BY z.nombre_zona, n.id_nivel;
    END PRC_LISTAR_TARIFARIO;

    PROCEDURE PRC_LISTAR_POR_NIVEL_ZONA (
        p_id_nivel IN NUMBER,
        p_id_zona IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT t.id_tarifa, t.id_zona, z.nombre_zona, t.id_nivel, n.nombre_nivel, 
                   t.id_tipo_gasto, tg.nombre_gasto, tg.es_asignable_por_dia, t.monto_limite_diario, t.moneda,
                   t.user_crea, t.fecha_crea, t.activo
            FROM tarifario_viaticos t
            JOIN zonas_geograficas z ON t.id_zona = z.id_zona
            JOIN niveles_jerarquicos n ON t.id_nivel = n.id_nivel
            JOIN tipos_gasto tg ON t.id_tipo_gasto = tg.id_tipo
            WHERE t.id_nivel = p_id_nivel AND t.id_zona = p_id_zona
            ORDER BY tg.nombre_gasto;
    END PRC_LISTAR_POR_NIVEL_ZONA;

    PROCEDURE PRC_LISTAR_POR_ACTIVO (
        p_activo IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT t.id_tarifa, t.id_zona, z.nombre_zona, t.id_nivel, n.nombre_nivel, 
                   t.id_tipo_gasto, tg.nombre_gasto, tg.es_asignable_por_dia, t.monto_limite_diario, t.moneda,
                   t.user_crea, t.fecha_crea, t.activo
            FROM tarifario_viaticos t
            JOIN zonas_geograficas z ON t.id_zona = z.id_zona
            JOIN niveles_jerarquicos n ON t.id_nivel = n.id_nivel
            JOIN tipos_gasto tg ON t.id_tipo_gasto = tg.id_tipo
            WHERE t.activo = p_activo
            ORDER BY z.nombre_zona, n.id_nivel;
    END PRC_LISTAR_POR_ACTIVO;

    FUNCTION FNC_OBTENER_POR_ID (
        p_id_tarifa IN NUMBER
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT t.id_tarifa, t.id_zona, z.nombre_zona, t.id_nivel, n.nombre_nivel, 
                   t.id_tipo_gasto, tg.nombre_gasto, tg.es_asignable_por_dia, t.monto_limite_diario, t.moneda,
                   t.user_crea, t.fecha_crea, t.activo
            FROM tarifario_viaticos t
            JOIN zonas_geograficas z ON t.id_zona = z.id_zona
            JOIN niveles_jerarquicos n ON t.id_nivel = n.id_nivel
            JOIN tipos_gasto tg ON t.id_tipo_gasto = tg.id_tipo
            WHERE t.id_tarifa = p_id_tarifa;
        RETURN v_cursor;
    END FNC_OBTENER_POR_ID;

    PROCEDURE PRC_GUARDAR_TARIFA (
        p_id_tarifa IN OUT NUMBER,
        p_id_zona IN NUMBER,
        p_id_nivel IN NUMBER,
        p_id_tipo_gasto IN NUMBER,
        p_monto IN NUMBER,
        p_moneda IN VARCHAR2,
        p_activo IN NUMBER,
        p_user IN VARCHAR2
    ) AS
    BEGIN
        IF p_id_tarifa IS NULL THEN
            INSERT INTO tarifario_viaticos (
                id_zona, id_nivel, id_tipo_gasto, 
                monto_limite_diario, moneda, activo, user_crea, fecha_crea
            ) VALUES (
                p_id_zona, p_id_nivel, p_id_tipo_gasto, 
                p_monto, p_moneda, p_activo, p_user, SYSTIMESTAMP
            ) RETURNING id_tarifa INTO p_id_tarifa;
        ELSE
            UPDATE tarifario_viaticos SET
                id_zona = p_id_zona,
                id_nivel = p_id_nivel,
                id_tipo_gasto = p_id_tipo_gasto,
                monto_limite_diario = p_monto,
                moneda = p_moneda,
                activo = p_activo,
                user_mod = p_user,
                fecha_mod = SYSTIMESTAMP
            WHERE id_tarifa = p_id_tarifa;
        END IF;
        COMMIT;
    END PRC_GUARDAR_TARIFA;

    PROCEDURE PRC_ELIMINAR_TARIFA (
        p_id_tarifa IN NUMBER
    ) AS
    BEGIN
        DELETE FROM tarifario_viaticos WHERE id_tarifa = p_id_tarifa;
        COMMIT;
    END PRC_ELIMINAR_TARIFA;

    FUNCTION FNC_CONTAR_TARIFAS RETURN NUMBER AS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count FROM tarifario_viaticos;
        RETURN v_count;
    END FNC_CONTAR_TARIFAS;

END PKG_TARIFARIO;
/