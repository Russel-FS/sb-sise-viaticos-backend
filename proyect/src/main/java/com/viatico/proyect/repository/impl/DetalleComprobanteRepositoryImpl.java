package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.DetalleComprobante;
import com.viatico.proyect.entity.RendicionCuentas;
import com.viatico.proyect.entity.TipoGasto;
import com.viatico.proyect.repository.interfaces.DetalleComprobanteRepository;
import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DetalleComprobanteRepositoryImpl implements DetalleComprobanteRepository {

    private final JdbcTemplate jdbcTemplate;

    public DetalleComprobanteRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public DetalleComprobante save(DetalleComprobante d) {
        return jdbcTemplate.execute((Connection con) -> {
            String sql = "{call PKG_COMPROBANTES.PRC_GUARDAR_COMPROBANTE(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                if (d.getId() == null) {
                    stmt.setNull(1, Types.NUMERIC);
                } else {
                    stmt.setLong(1, d.getId());
                }
                stmt.registerOutParameter(1, Types.NUMERIC);
                stmt.setLong(2, d.getRendicion().getId());
                stmt.setLong(3, d.getTipoGasto().getId());
                stmt.setTimestamp(4, d.getFechaEmision() != null ? Timestamp.valueOf(d.getFechaEmision()) : null);
                stmt.setString(5, d.getTipoComprobante());
                stmt.setString(6, d.getSerieComprobante());
                stmt.setString(7, d.getNumeroComprobante());
                stmt.setString(8, d.getRucEmisor());
                stmt.setString(9, d.getRazonSocialEmisor());
                stmt.setBigDecimal(10, d.getMontoBruto());
                stmt.setBigDecimal(11, d.getMontoIgp());
                stmt.setBigDecimal(12, d.getMontoTotal());
                stmt.setString(13, d.getImagenComprobanteUrl());
                stmt.setInt(14, d.isValidado() ? 1 : 0);
                stmt.setString(15, d.getMotivoRechazo());
                stmt.setString(16, d.getUserCrea());

                stmt.execute();

                long newId = stmt.getLong(1);
                if (d.getId() == null) {
                    d.setId(newId);
                }
                return d;
            }
        });
    }

    @Override
    public Optional<DetalleComprobante> findById(Long id) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{call PKG_COMPROBANTES.PRC_OBTENER_COMPROBANTE(?, ?)}")) {
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
            String sql = "{call PKG_COMPROBANTES.PRC_ELIMINAR_COMPROBANTE(?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                stmt.setLong(1, id);
                stmt.execute();
                return null;
            }
        });
    }

    @Override
    public void delete(DetalleComprobante detalle) {
        if (detalle.getId() != null)
            deleteById(detalle.getId());
    }

    @Override
    public List<DetalleComprobante> findAll() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<DetalleComprobante> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_COMPROBANTES.PRC_LISTAR_TODOS(?)}")) {
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
    public List<DetalleComprobante> findByRendicionId(Long rendicionId) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<DetalleComprobante> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_COMPROBANTES.PRC_LISTAR_POR_RENDICION(?, ?)}")) {
                cs.setLong(1, rendicionId);
                cs.registerOutParameter(2, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        lista.add(mapFromResultSet(rs));
                    }
                }
            }
            return lista;
        });
    }

    private DetalleComprobante mapFromResultSet(ResultSet rs) throws SQLException {
        DetalleComprobante d = new DetalleComprobante();
        d.setId(rs.getLong("id_detalle"));

        RendicionCuentas ren = new RendicionCuentas();
        ren.setId(rs.getLong("id_rendicion"));
        d.setRendicion(ren);

        TipoGasto tg = new TipoGasto();
        tg.setId(rs.getLong("id_tipo_gasto"));
        d.setTipoGasto(tg);

        Timestamp ts = rs.getTimestamp("fecha_emision");
        if (ts != null)
            d.setFechaEmision(ts.toLocalDateTime());

        d.setTipoComprobante(rs.getString("tipo_comprobante"));
        d.setSerieComprobante(rs.getString("serie_comprobante"));
        d.setNumeroComprobante(rs.getString("numero_comprobante"));
        d.setRucEmisor(rs.getString("ruc_emisor"));
        d.setRazonSocialEmisor(rs.getString("razon_social_emisor"));
        d.setMontoBruto(rs.getBigDecimal("monto_bruto"));
        d.setMontoIgp(rs.getBigDecimal("monto_igv"));
        d.setMontoTotal(rs.getBigDecimal("monto_total"));
        d.setImagenComprobanteUrl(rs.getString("imagen_url"));
        d.setValidado(rs.getInt("validado") == 1);
        d.setMotivoRechazo(rs.getString("motivo_rechazo"));
        d.setUserCrea(rs.getString("user_crea"));

        Timestamp tsCrea = rs.getTimestamp("fecha_crea");
        if (tsCrea != null)
            d.setFechaCrea(tsCrea.toLocalDateTime());

        return d;
    }
}
