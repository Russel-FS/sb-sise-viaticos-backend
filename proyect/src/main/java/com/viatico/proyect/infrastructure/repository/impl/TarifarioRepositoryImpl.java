package com.viatico.proyect.infrastructure.repository.impl;

import com.viatico.proyect.domain.entity.NivelJerarquico;
import com.viatico.proyect.domain.entity.Tarifario;
import com.viatico.proyect.domain.entity.TipoGasto;
import com.viatico.proyect.domain.entity.ZonaGeografica;
import com.viatico.proyect.domain.repositories.TarifarioRepository;

import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TarifarioRepositoryImpl implements TarifarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public TarifarioRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Tarifario save(Tarifario t) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn
                    .prepareCall("{call PKG_TARIFARIO.PRC_GUARDAR_TARIFA(?, ?, ?, ?, ?, ?, ?, ?)}")) {
                if (t.getId() != null) {
                    cs.setLong(1, t.getId());
                } else {
                    cs.setNull(1, Types.NUMERIC);
                }
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.setLong(2, t.getZonaGeografica().getId());
                cs.setLong(3, t.getNivelJerarquico().getId());
                cs.setLong(4, t.getTipoGasto().getId());
                cs.setBigDecimal(5, t.getMontoDiario());
                cs.setString(6, t.getMoneda());
                cs.setInt(7, t.getActivo() != null ? t.getActivo() : 1);
                cs.setString(8, t.getUserCrea());

                cs.execute();

                long newId = cs.getLong(1);
                if (t.getId() == null) {
                    t.setId(newId);
                }
                return t;
            }
        });
    }

    @Override
    public Optional<Tarifario> findById(Long id) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_TARIFARIO.FNC_OBTENER_POR_ID(?)}")) {
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
        jdbcTemplate.update("{call PKG_TARIFARIO.PRC_ELIMINAR_TARIFA(?)}", id);
    }

    @Override
    public void delete(Tarifario t) {
        if (t.getId() != null)
            deleteById(t.getId());
    }

    @Override
    public List<Tarifario> findAll() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Tarifario> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_TARIFARIO.PRC_LISTAR_TARIFARIO(?)}")) {
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
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_TARIFARIO.FNC_CONTAR_TARIFAS()}")) {
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.execute();
                return cs.getLong(1);
            }
        });
    }

    @Override
    public List<Tarifario> findAllByNivelJerarquicoAndZonaGeografica(NivelJerarquico nivel, ZonaGeografica zona) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Tarifario> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_TARIFARIO.PRC_LISTAR_POR_NIVEL_ZONA(?, ?, ?)}")) {
                cs.setLong(1, nivel.getId());
                cs.setLong(2, zona.getId());
                cs.registerOutParameter(3, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(3)) {
                    while (rs.next()) {
                        lista.add(mapFromResultSet(rs));
                    }
                }
            }
            return lista;
        });
    }

    @Override
    public List<Tarifario> findByActivo(Integer activo) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Tarifario> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_TARIFARIO.PRC_LISTAR_POR_ACTIVO(?, ?)}")) {
                cs.setInt(1, activo);
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

    private Tarifario mapFromResultSet(ResultSet rs) throws SQLException {
        Tarifario t = new Tarifario();
        t.setId(rs.getLong("id_tarifa"));

        ZonaGeografica z = new ZonaGeografica();
        z.setId(rs.getLong("id_zona"));
        z.setNombre(rs.getString("nombre_zona"));
        t.setZonaGeografica(z);

        NivelJerarquico n = new NivelJerarquico();
        n.setId(rs.getLong("id_nivel"));
        n.setNombre(rs.getString("nombre_nivel"));
        t.setNivelJerarquico(n);

        TipoGasto tg = new TipoGasto();
        tg.setId(rs.getLong("id_tipo_gasto"));
        tg.setNombre(rs.getString("nombre_gasto"));
        tg.setEsAsignablePorDia(rs.getBoolean("es_asignable_por_dia"));
        t.setTipoGasto(tg);

        t.setMontoDiario(rs.getBigDecimal("monto_limite_diario"));
        t.setMoneda(rs.getString("moneda"));
        t.setActivo(rs.getInt("activo"));
        t.setUserCrea(rs.getString("user_crea"));

        Timestamp ts = rs.getTimestamp("fecha_crea");
        if (ts != null) {
            t.setFechaCrea(ts.toLocalDateTime());
        }
        return t;
    }
}
