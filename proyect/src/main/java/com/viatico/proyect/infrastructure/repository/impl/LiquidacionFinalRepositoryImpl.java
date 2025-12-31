package com.viatico.proyect.infrastructure.repository.impl;

import com.viatico.proyect.domain.entity.LiquidacionFinal;
import com.viatico.proyect.domain.entity.SolicitudComision;
import com.viatico.proyect.domain.repositories.LiquidacionFinalRepository;

import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class LiquidacionFinalRepositoryImpl implements LiquidacionFinalRepository {

    private final JdbcTemplate jdbcTemplate;

    public LiquidacionFinalRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public LiquidacionFinal save(LiquidacionFinal l) {
        return jdbcTemplate.execute((Connection con) -> {
            String sql = "{call PKG_LIQUIDACIONES.PRC_GUARDAR_LIQUIDACION(?, ?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                if (l.getId() == null) {
                    stmt.setNull(1, Types.NUMERIC);
                } else {
                    stmt.setLong(1, l.getId());
                }
                stmt.registerOutParameter(1, Types.NUMERIC);
                stmt.setLong(2, l.getSolicitud().getId());
                stmt.setBigDecimal(3, l.getMontoAsignado());
                stmt.setBigDecimal(4, l.getMontoRendidoValidado());
                stmt.setDate(5, l.getFechaCierre() != null ? Date.valueOf(l.getFechaCierre().toLocalDate()) : null);
                stmt.setString(6, l.getUserCrea());

                stmt.execute();

                long newId = stmt.getLong(1);
                if (l.getId() == null) {
                    l.setId(newId);
                }
                return l;
            }
        });
    }

    @Override
    public Optional<LiquidacionFinal> findById(Long id) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{call PKG_LIQUIDACIONES.PRC_OBTENER_POR_ID(?, ?)}")) {
                cs.setLong(1, id);
                cs.registerOutParameter(2, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
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
            String sql = "{call PKG_LIQUIDACIONES.PRC_ELIMINAR_LIQUIDACION(?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                stmt.setLong(1, id);
                stmt.execute();
                return null;
            }
        });
    }

    @Override
    public void delete(LiquidacionFinal l) {
        if (l.getId() != null)
            deleteById(l.getId());
    }

    @Override
    public List<LiquidacionFinal> findAll() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<LiquidacionFinal> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_LIQUIDACIONES.PRC_LISTAR_TODAS(?)}")) {
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
        return findAll().size();
    }

    @Override
    public Optional<LiquidacionFinal> findBySolicitudId(Long solicitudId) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_LIQUIDACIONES.FNC_OBTENER_POR_SOLICITUD(?)}")) {
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

    private LiquidacionFinal mapFromResultSet(ResultSet rs) throws SQLException {
        LiquidacionFinal l = new LiquidacionFinal();
        l.setId(rs.getLong("id_liquidacion"));

        SolicitudComision sol = new SolicitudComision();
        sol.setId(rs.getLong("id_comision"));
        l.setSolicitud(sol);

        l.setMontoAsignado(rs.getBigDecimal("monto_asignado"));
        l.setMontoRendidoValidado(rs.getBigDecimal("monto_rendido_validado"));
        l.setSaldoAfavorEmpresa(rs.getBigDecimal("saldo_a_favor_empresa"));
        l.setSaldoAfavorEmpleado(rs.getBigDecimal("saldo_a_favor_empleado"));

        Timestamp tsCierre = rs.getTimestamp("fecha_cierre");
        if (tsCierre != null)
            l.setFechaCierre(tsCierre.toLocalDateTime());

        l.setEstadoCierre(com.viatico.proyect.domain.enums.EstadoLiquidacion.valueOf(rs.getString("estado_cierre")));
        l.setMensajeLiquidacion(rs.getString("mensaje_liquidacion"));
        l.setUserCrea(rs.getString("user_crea"));

        Timestamp tsCrea = rs.getTimestamp("fecha_crea");
        if (tsCrea != null)
            l.setFechaCrea(tsCrea.toLocalDateTime());

        return l;
    }
}
