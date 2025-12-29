-- Active: 1766458157960@@127.0.0.1@1521@XE@C
-- Tipos  colecciones
CREATE OR REPLACE TYPE T_ITINERARIO_REC AS OBJECT (
    id_zona NUMBER,
    ciudad_especifica VARCHAR2(100),
    noches NUMBER
);
/

CREATE OR REPLACE TYPE T_ITINERARIO_TAB AS TABLE OF T_ITINERARIO_REC;
/

-- Paquete de Solicitudes
CREATE OR REPLACE PACKAGE PKG_SOLICITUDES AS

    -- Registrar solicitud completa
    PROCEDURE PRC_REGISTRAR_SOLICITUD (
    p_id_empleado IN NUMBER,
    p_motivo IN VARCHAR2,
    p_fecha_inicio IN DATE,
    p_itinerario IN T_ITINERARIO_TAB,
    p_user_crea IN VARCHAR2,
    p_id_comision_out OUT NUMBER
);

    -- Listar todas las solicitudes
    PROCEDURE PRC_LISTAR_TODAS (p_cursor OUT SYS_REFCURSOR);

    -- Obtener solicitud por ID
    FUNCTION FNC_OBTENER_POR_ID (p_id_comision IN NUMBER) RETURN SYS_REFCURSOR;

    -- Eliminar solicitud
    PROCEDURE PRC_ELIMINAR_SOLICITUD (p_id_comision IN NUMBER);

    -- Actualizar estado de solicitud
    PROCEDURE PRC_ACTUALIZAR_ESTADO (
    p_id_comision IN NUMBER,
    p_estado IN VARCHAR2,
    p_comentario IN VARCHAR2 DEFAULT NULL
   );

    -- Listar por empleado
    PROCEDURE PRC_LISTAR_POR_EMPLEADO (
    p_id_empleado IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
   );

    -- Listar por estado
    PROCEDURE PRC_LISTAR_POR_ESTADO (
    p_estado IN VARCHAR2,
    p_cursor OUT SYS_REFCURSOR
   );

    -- Listar por empleado y estado
    PROCEDURE PRC_LISTAR_POR_EMP_Y_EST (
    p_id_empleado IN NUMBER,
    p_estado IN VARCHAR2,
    p_cursor OUT SYS_REFCURSOR
    );

    -- Obtener Top 5 por empleado
    PROCEDURE PRC_TOP5_POR_EMPLEADO (
    p_id_empleado IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
    );

    -- Obtener Top 5 general
    PROCEDURE PRC_TOP5_GENERAL (p_cursor OUT SYS_REFCURSOR);

    -- Obtener itinerario por comision
    PROCEDURE PRC_OBTENER_ITINERARIO (
    p_id_comision IN NUMBER,
    p_cursor OUT SYS_REFCURSOR
    );

    -- Contar solicitudes por empleado y estado
    FUNCTION FNC_CONTAR_SOLICITUDES (
    p_id_empleado IN NUMBER DEFAULT NULL,
    p_estado IN VARCHAR2 DEFAULT NULL
    ) RETURN NUMBER;

END PKG_SOLICITUDES;
/

CREATE OR REPLACE PACKAGE BODY PKG_SOLICITUDES AS

    PROCEDURE PRC_REGISTRAR_SOLICITUD (
        p_id_empleado IN NUMBER,
        p_motivo IN VARCHAR2,
        p_fecha_inicio IN DATE,
        p_itinerario IN T_ITINERARIO_TAB,
        p_user_crea IN VARCHAR2,
        p_id_comision_out OUT NUMBER
    ) AS
        v_id_nivel NUMBER;
        v_total_noches NUMBER := 0;
        v_fecha_fin DATE;
        v_monto_total NUMBER := 0;
        v_monto_diario_zona NUMBER;
        v_dias_tramo NUMBER;
    BEGIN
        -- Obtener el nivel del empleado para los calculos del tarifario
        SELECT id_nivel INTO v_id_nivel FROM empleados WHERE id_empleado = p_id_empleado;

        -- Calcular total de noches y monto acumulado
        FOR i IN 1 .. p_itinerario.COUNT LOOP
            v_total_noches := v_total_noches + p_itinerario(i).noches;
            
            -- Suma del tarifario diario para la zona y nivel del empleado
            SELECT SUM(monto_limite_diario)
            INTO v_monto_diario_zona
            FROM tarifario_viaticos
            WHERE id_zona = p_itinerario(i).id_zona
              AND id_nivel = v_id_nivel
              AND activo = 1;

            -- Lógica de días a calcular si es el último tramo se suma el día de retorno
            IF i = p_itinerario.COUNT THEN
                v_dias_tramo := p_itinerario(i).noches + 1;
            ELSE
                v_dias_tramo := p_itinerario(i).noches;
            END IF;

            v_monto_total := v_monto_total + (NVL(v_monto_diario_zona, 0) * v_dias_tramo);
        END LOOP;

        v_fecha_fin := p_fecha_inicio + v_total_noches;

        -- datos de la solicitud
        INSERT INTO solicitud_comision (
            id_empleado, motivo_viaje, fecha_solicitud, fecha_inicio, fecha_fin, 
            estado, monto_total, user_crea, fecha_crea
        ) VALUES (
            p_id_empleado, p_motivo, SYSDATE, p_fecha_inicio, v_fecha_fin,
            'BORRADOR', v_monto_total, p_user_crea, SYSTIMESTAMP
        ) RETURNING id_comision INTO p_id_comision_out;

        -- datos del itinerario
        FOR i IN 1 .. p_itinerario.COUNT LOOP
            INSERT INTO itinerario_viaje (
                id_comision, id_zona_destino, ciudad_especifica, noches,
                user_crea, fecha_crea
            ) VALUES (
                p_id_comision_out, p_itinerario(i).id_zona, p_itinerario(i).ciudad_especifica, 
                p_itinerario(i).noches, p_user_crea, SYSTIMESTAMP
            );
        END LOOP;

        COMMIT;
    EXCEPTION
        WHEN OTHERS THEN
            ROLLBACK;
            RAISE;
    END PRC_REGISTRAR_SOLICITUD;

    PROCEDURE PRC_LISTAR_TODAS (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT s.*, e.nombres, e.apellidos, e.dni, e.email as emp_email
            FROM solicitud_comision s
            JOIN empleados e ON s.id_empleado = e.id_empleado
            ORDER BY s.fecha_crea DESC;
    END PRC_LISTAR_TODAS;

    FUNCTION FNC_OBTENER_POR_ID (
        p_id_comision IN NUMBER
    ) RETURN SYS_REFCURSOR AS
        v_cursor SYS_REFCURSOR;
    BEGIN
        OPEN v_cursor FOR
            SELECT s.*, e.nombres, e.apellidos, e.dni, e.email as emp_email
            FROM solicitud_comision s
            JOIN empleados e ON s.id_empleado = e.id_empleado
            WHERE s.id_comision = p_id_comision;
        RETURN v_cursor;
    END FNC_OBTENER_POR_ID;

    PROCEDURE PRC_ELIMINAR_SOLICITUD (
        p_id_comision IN NUMBER
    ) AS
    BEGIN
        DELETE FROM itinerario_viaje WHERE id_comision = p_id_comision;
        DELETE FROM solicitud_comision WHERE id_comision = p_id_comision;
        COMMIT;
    END PRC_ELIMINAR_SOLICITUD;

    PROCEDURE PRC_ACTUALIZAR_ESTADO (
        p_id_comision IN NUMBER,
        p_estado IN VARCHAR2,
        p_comentario IN VARCHAR2 DEFAULT NULL
    ) AS
    BEGIN
        UPDATE solicitud_comision SET
            estado = p_estado,
            comentario_rechazo = p_comentario,
            fecha_mod = SYSTIMESTAMP
        WHERE id_comision = p_id_comision;
        COMMIT;
    END PRC_ACTUALIZAR_ESTADO;

    PROCEDURE PRC_LISTAR_POR_EMPLEADO (
        p_id_empleado IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT s.*, e.nombres, e.apellidos, e.dni, e.email as emp_email
            FROM solicitud_comision s
            JOIN empleados e ON s.id_empleado = e.id_empleado
            WHERE s.id_empleado = p_id_empleado
            ORDER BY s.fecha_crea DESC;
    END PRC_LISTAR_POR_EMPLEADO;

    PROCEDURE PRC_LISTAR_POR_ESTADO (
        p_estado IN VARCHAR2,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT s.*, e.nombres, e.apellidos, e.dni, e.email as emp_email
            FROM solicitud_comision s
            JOIN empleados e ON s.id_empleado = e.id_empleado
            WHERE s.estado = p_estado
            ORDER BY s.fecha_crea DESC;
    END PRC_LISTAR_POR_ESTADO;

    PROCEDURE PRC_LISTAR_POR_EMP_Y_EST (
        p_id_empleado IN NUMBER,
        p_estado IN VARCHAR2,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT s.*, e.nombres, e.apellidos, e.dni, e.email as emp_email
            FROM solicitud_comision s
            JOIN empleados e ON s.id_empleado = e.id_empleado
            WHERE s.id_empleado = p_id_empleado AND s.estado = p_estado
            ORDER BY s.fecha_crea DESC;
    END PRC_LISTAR_POR_EMP_Y_EST;

    PROCEDURE PRC_TOP5_POR_EMPLEADO (
        p_id_empleado IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM (
                SELECT s.*, e.nombres, e.apellidos, e.dni, e.email as emp_email
                FROM solicitud_comision s
                JOIN empleados e ON s.id_empleado = e.id_empleado
                WHERE s.id_empleado = p_id_empleado
                ORDER BY s.fecha_crea DESC
            ) WHERE ROWNUM <= 5;
    END PRC_TOP5_POR_EMPLEADO;

    PROCEDURE PRC_TOP5_GENERAL (
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT * FROM (
                SELECT s.*, e.nombres, e.apellidos, e.dni, e.email as emp_email
                FROM solicitud_comision s
                JOIN empleados e ON s.id_empleado = e.id_empleado
                ORDER BY s.fecha_crea DESC
            ) WHERE ROWNUM <= 5;
    END PRC_TOP5_GENERAL;

    PROCEDURE PRC_OBTENER_ITINERARIO (
        p_id_comision IN NUMBER,
        p_cursor OUT SYS_REFCURSOR
    ) AS
    BEGIN
        OPEN p_cursor FOR
            SELECT i.*, z.nombre_zona
            FROM itinerario_viaje i
            JOIN zonas_geograficas z ON i.id_zona_destino = z.id_zona
            WHERE i.id_comision = p_id_comision;
    END PRC_OBTENER_ITINERARIO;

    FUNCTION FNC_CONTAR_SOLICITUDES (
        p_id_empleado IN NUMBER DEFAULT NULL,
        p_estado IN VARCHAR2 DEFAULT NULL
    ) RETURN NUMBER AS
        v_count NUMBER;
    BEGIN
        SELECT COUNT(*) INTO v_count
        FROM solicitud_comision
        WHERE (p_id_empleado IS NULL OR id_empleado = p_id_empleado)
          AND (p_estado IS NULL OR estado = p_estado);
        RETURN v_count;
    END FNC_CONTAR_SOLICITUDES;

END PKG_SOLICITUDES;
/