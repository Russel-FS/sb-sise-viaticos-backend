package com.viatico.proyect.infrastructure.repository.impl;

import com.viatico.proyect.domain.entity.RendicionCuentas;
import com.viatico.proyect.domain.entity.SolicitudComision;
import com.viatico.proyect.domain.repositories.RendicionCuentasRepository;

import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class RendicionCuentasRepositoryImpl implements RendicionCuentasRepository {

    private final JdbcTemplate jdbcTemplate;

    public RendicionCuentasRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public RendicionCuentas save(RendicionCuentas r) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn
                    .prepareCall("{call PKG_RENDICIONES.PRC_GUARDAR_RENDICION(?, ?, ?, ?, ?, ?, ?)}")) {
                if (r.getId() != null) {
                    cs.setLong(1, r.getId());
                } else {
                    cs.setNull(1, Types.NUMERIC);
                }
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.setLong(2, r.getSolicitud().getId());
                cs.setTimestamp(3,
                        r.getFechaPresentacion() != null ? Timestamp.valueOf(r.getFechaPresentacion()) : null);
                cs.setBigDecimal(4, r.getTotalGastadoBruto());
                cs.setBigDecimal(5, r.getTotalAceptado());
                cs.setString(6, r.getComentariosEmpleado());
                cs.setString(7, r.getUserCrea());

                cs.execute();

                long newId = cs.getLong(1);
                if (r.getId() == null) {
                    r.setId(newId);
                }
                return r;
            }
        });
    }

    @Override
    public Optional<RendicionCuentas> findById(Long id) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_RENDICIONES.FNC_OBTENER_POR_ID(?)}")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.setLong(2, id);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    if (rs.next()) {
                        return Optional.of(mapFromResultSet(rs));
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public void deleteById(Long id) {
        jdbcTemplate.execute((Connection con) -> {
            try (CallableStatement stmt = con.prepareCall("{call PKG_RENDICIONES.PRC_ELIMINAR_RENDICION(?)}")) {
                stmt.setLong(1, id);
                stmt.execute();
                return null;
            }
        });
    }

    @Override
    public void delete(RendicionCuentas r) {
        if (r.getId() != null)
            deleteById(r.getId());
    }

    @Override
    public List<RendicionCuentas> findAll() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<RendicionCuentas> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_RENDICIONES.PRC_LISTAR_TODAS(?)}")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        lista.add(mapFromResultSet(rs));
                    }
                }
            }
            return lista;
        });
    }

    @Override
    public long count() {
        return jdbcTemplate.queryForObject("SELECT count(*) FROM rendiciones_cuentas", Long.class);
    }

    @Override
    public Optional<RendicionCuentas> findBySolicitudId(Long solicitudId) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_RENDICIONES.FNC_OBTENER_POR_SOLICITUD(?)}")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.setLong(2, solicitudId);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    if (rs.next()) {
                        return Optional.of(mapFromResultSet(rs));
                    }
                }
            }
            return Optional.empty();
        });
    }

    private RendicionCuentas mapFromResultSet(ResultSet rs) throws SQLException {
        RendicionCuentas r = new RendicionCuentas();
        r.setId(rs.getLong("id_rendicion"));

        SolicitudComision sol = new SolicitudComision();
        sol.setId(rs.getLong("id_solicitud"));
        r.setSolicitud(sol);

        r.setTotalGastadoBruto(rs.getBigDecimal("total_gastado_bruto"));
        r.setTotalAceptado(rs.getBigDecimal("total_aceptado"));
        r.setComentariosEmpleado(rs.getString("comentarios_empleado"));
        r.setUserCrea(rs.getString("user_crea"));

        Timestamp tsPres = rs.getTimestamp("fecha_presentacion");
        if (tsPres != null)
            r.setFechaPresentacion(tsPres.toLocalDateTime());

        Timestamp tsCrea = rs.getTimestamp("fecha_crea");
        if (tsCrea != null)
            r.setFechaCrea(tsCrea.toLocalDateTime());

        return r;
    }
}
