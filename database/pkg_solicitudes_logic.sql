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

-- Paquete de Lógica de Negocio para Solicitudes
CREATE OR REPLACE PACKAGE PKG_SOLICITUDES AS
    PROCEDURE PRC_REGISTRAR_SOLICITUD (
        p_id_empleado IN NUMBER,
        p_motivo IN VARCHAR2,
        p_fecha_inicio IN DATE,
        p_itinerario IN T_ITINERARIO_TAB,
        p_user_crea IN VARCHAR2,
        p_id_comision_out OUT NUMBER
    );
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

END PKG_SOLICITUDES;
/