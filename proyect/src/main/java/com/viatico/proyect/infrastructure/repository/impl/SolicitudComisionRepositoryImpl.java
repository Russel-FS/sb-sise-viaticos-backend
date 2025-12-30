package com.viatico.proyect.infrastructure.repository.impl;

import com.viatico.proyect.domain.entity.Empleado;
import com.viatico.proyect.domain.entity.ItinerarioViaje;
import com.viatico.proyect.domain.entity.SolicitudComision;
import com.viatico.proyect.domain.entity.ZonaGeografica;
import com.viatico.proyect.domain.enums.EstadoSolicitud;
import com.viatico.proyect.domain.repositories.SolicitudComisionRepository;

import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import oracle.jdbc.OracleConnection;

@Repository
public class SolicitudComisionRepositoryImpl implements SolicitudComisionRepository {

    private final JdbcTemplate jdbcTemplate;

    public SolicitudComisionRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SolicitudComision save(SolicitudComision solicitud) {
        if (solicitud.getId() == null) {
            throw new UnsupportedOperationException("Use registrarSolicitudFull para nuevas solicitudes");
        }

        jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{call PKG_SOLICITUDES.PRC_ACTUALIZAR_ESTADO(?, ?, ?)}")) {
                cs.setLong(1, solicitud.getId());
                cs.setString(2, solicitud.getEstado().name());
                cs.setString(3, solicitud.getComentarioRechazo());
                cs.execute();
                return null;
            }
        });

        return findById(solicitud.getId()).orElse(solicitud);
    }

    @Override
    public Optional<SolicitudComision> findById(Long id) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_SOLICITUDES.FNC_OBTENER_POR_ID(?)}")) {
                cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
                cs.setLong(2, id);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    if (rs.next()) {
                        SolicitudComision sol = mapSolicitud(rs, 1);
                        sol.setItinerarios(obtenerItinerarios(sol.getId()));
                        return Optional.of(sol);
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.execute((Connection con) -> {
            try (CallableStatement stmt = con.prepareCall("{call PKG_SOLICITUDES.PRC_ELIMINAR_SOLICITUD(?)}")) {
                stmt.setLong(1, id);
                stmt.execute();
                return null;
            }
        });
    }

    @Override
    public void delete(SolicitudComision solicitud) {
        if (solicitud.getId() != null)
            deleteById(solicitud.getId());
    }

    @Override
    public List<SolicitudComision> findAll() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<SolicitudComision> lista = new java.util.ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_SOLICITUDES.PRC_LISTAR_TODAS(?)}")) {
                cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        lista.add(mapSolicitud(rs, 1));
                    }
                }
            }
            return lista;
        });
    }

    @Override
    public long count() {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_SOLICITUDES.FNC_CONTAR_SOLICITUDES(?, ?)}")) {
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.setNull(2, Types.NUMERIC);
                cs.setNull(3, Types.VARCHAR);
                cs.execute();
                return cs.getLong(1);
            }
        });
    }

    @Override
    public List<SolicitudComision> findByEmpleadoIdOrderByFechaCreaDesc(Long empleadoId) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<SolicitudComision> lista = new java.util.ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_SOLICITUDES.PRC_LISTAR_POR_EMPLEADO(?, ?)}")) {
                cs.setLong(1, empleadoId);
                cs.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        lista.add(mapSolicitud(rs, 1));
                    }
                }
            }
            return lista;
        });
    }

    @Override
    public List<SolicitudComision> findByEstadoOrderByFechaCreaDesc(EstadoSolicitud estado) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<SolicitudComision> lista = new java.util.ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_SOLICITUDES.PRC_LISTAR_POR_ESTADO(?, ?)}")) {
                cs.setString(1, estado.name());
                cs.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        lista.add(mapSolicitud(rs, 1));
                    }
                }
            }
            return lista;
        });
    }

    @Override
    public List<SolicitudComision> findByEmpleadoIdAndEstadoOrderByFechaCreaDesc(Long empleadoId,
            EstadoSolicitud estado) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<SolicitudComision> lista = new java.util.ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_SOLICITUDES.PRC_LISTAR_POR_EMP_Y_EST(?, ?, ?)}")) {
                cs.setLong(1, empleadoId);
                cs.setString(2, estado.name());
                cs.registerOutParameter(3, oracle.jdbc.OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                    while (rs.next()) {
                        lista.add(mapSolicitud(rs, 1));
                    }
                }
            }
            return lista;
        });
    }

    @Override
    public List<SolicitudComision> findTop5ByEmpleadoIdOrderByFechaCreaDesc(Long empleadoId) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<SolicitudComision> lista = new java.util.ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_SOLICITUDES.PRC_TOP5_POR_EMPLEADO(?, ?)}")) {
                cs.setLong(1, empleadoId);
                cs.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        lista.add(mapSolicitud(rs, 1));
                    }
                }
            }
            return lista;
        });
    }

    @Override
    public List<SolicitudComision> findTop5ByOrderByFechaCreaDesc() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<SolicitudComision> lista = new java.util.ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_SOLICITUDES.PRC_TOP5_GENERAL(?)}")) {
                cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        lista.add(mapSolicitud(rs, 1));
                    }
                }
            }
            return lista;
        });
    }

    @Override
    public long countByEstado(EstadoSolicitud estado) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_SOLICITUDES.FNC_CONTAR_SOLICITUDES(?, ?)}")) {
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.setNull(2, Types.NUMERIC);
                cs.setString(3, estado.name());
                cs.execute();
                return cs.getLong(1);
            }
        });
    }

    @Override
    public long countByEmpleadoIdAndEstado(Long empleadoId, EstadoSolicitud estado) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_SOLICITUDES.FNC_CONTAR_SOLICITUDES(?, ?)}")) {
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.setLong(2, empleadoId);
                cs.setString(3, estado.name());
                cs.execute();
                return cs.getLong(1);
            }
        });
    }

    private SolicitudComision mapSolicitud(ResultSet rs, int rowNum) throws SQLException {
        SolicitudComision sol = new SolicitudComision();
        sol.setId(rs.getLong("id_comision"));
        sol.setMotivoViaje(rs.getString("motivo_viaje"));
        sol.setFechaSolicitud(
                rs.getDate("fecha_solicitud") != null ? rs.getDate("fecha_solicitud").toLocalDate() : null);
        sol.setFechaInicio(rs.getDate("fecha_inicio").toLocalDate());
        sol.setFechaFin(rs.getDate("fecha_fin").toLocalDate());
        sol.setEstado(EstadoSolicitud.valueOf(rs.getString("estado")));
        sol.setMontoTotal(rs.getBigDecimal("monto_total"));
        sol.setUserCrea(rs.getString("user_crea"));
        sol.setFechaCrea(
                rs.getTimestamp("fecha_crea") != null ? rs.getTimestamp("fecha_crea").toLocalDateTime() : null);
        sol.setComentarioRechazo(rs.getString("comentario_rechazo"));

        Empleado emp = new Empleado();
        emp.setId(rs.getLong("id_empleado"));
        emp.setNombres(rs.getString("nombres"));
        emp.setApellidos(rs.getString("apellidos"));
        emp.setDni(rs.getString("dni"));
        emp.setEmail(rs.getString("emp_email"));
        sol.setEmpleado(emp);

        return sol;
    }

    private List<ItinerarioViaje> obtenerItinerarios(Long idComision) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<ItinerarioViaje> lista = new java.util.ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_SOLICITUDES.PRC_OBTENER_ITINERARIO(?, ?)}")) {
                cs.setLong(1, idComision);
                cs.registerOutParameter(2, oracle.jdbc.OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        ItinerarioViaje iti = new ItinerarioViaje();
                        iti.setId(rs.getLong("id_itinerario"));
                        iti.setCiudadEspecifica(rs.getString("ciudad_especifica"));
                        iti.setNoches(rs.getInt("noches"));

                        ZonaGeografica zona = new ZonaGeografica();
                        zona.setId(rs.getLong("id_zona_destino"));
                        zona.setNombre(rs.getString("nombre_zona"));
                        iti.setZonaDestino(zona);
                        lista.add(iti);
                    }
                }
            }
            return lista;
        });
    }

    @Override
    public Long registrarSolicitudFull(Long empleadoId, String motivo, LocalDate fechaInicio,
            List<Long> idZonas, List<String> ciudades, List<Integer> noches, String userCrea) {
        return jdbcTemplate.execute((Connection conn) -> {
            OracleConnection oracleConn = conn.unwrap(OracleConnection.class);

            try (CallableStatement cs = oracleConn
                    .prepareCall("{call PKG_SOLICITUDES.PRC_REGISTRAR_SOLICITUD(?, ?, ?, ?, ?, ?)}")) {
                cs.setLong(1, empleadoId);
                cs.setString(2, motivo);
                cs.setDate(3, Date.valueOf(fechaInicio));

                Object[] structArray = new Object[idZonas.size()];
                for (int i = 0; i < idZonas.size(); i++) {
                    structArray[i] = oracleConn.createStruct("T_ITINERARIO_REC", new Object[] {
                            idZonas.get(i),
                            ciudades.get(i),
                            noches.get(i)
                    });
                }

                Array oracleArray = oracleConn.createOracleArray("T_ITINERARIO_TAB", structArray);
                cs.setArray(4, oracleArray);
                cs.setString(5, userCrea);
                cs.registerOutParameter(6, Types.NUMERIC);

                cs.execute();
                return cs.getLong(6);
            }
        });
    }
}
