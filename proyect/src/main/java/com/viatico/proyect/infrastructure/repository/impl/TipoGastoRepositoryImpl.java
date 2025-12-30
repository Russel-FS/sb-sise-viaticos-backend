package com.viatico.proyect.infrastructure.repository.impl;

import com.viatico.proyect.domain.entity.TipoGasto;
import com.viatico.proyect.domain.repositories.TipoGastoRepository;

import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class TipoGastoRepositoryImpl implements TipoGastoRepository {

    private final JdbcTemplate jdbcTemplate;

    public TipoGastoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public TipoGasto save(TipoGasto tipo) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn
                    .prepareCall("{call PKG_TIPOS_GASTO.PRC_GUARDAR_TIPO(?, ?, ?, ?, ?, ?, ?)}")) {
                if (tipo.getId() != null) {
                    cs.setLong(1, tipo.getId());
                } else {
                    cs.setNull(1, Types.NUMERIC);
                }
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.setString(2, tipo.getNombre());
                cs.setInt(3, tipo.isRequiereFactura() ? 1 : 0);
                cs.setString(4, tipo.getCuentaContable());
                cs.setInt(5, tipo.isEsAsignablePorDia() ? 1 : 0);
                cs.setInt(6, tipo.getActivo() != null ? tipo.getActivo() : 1);
                cs.setString(7, tipo.getUserCrea());

                cs.execute();

                long newId = cs.getLong(1);
                if (tipo.getId() == null) {
                    tipo.setId(newId);
                }
                return tipo;
            }
        });
    }

    @Override
    public Optional<TipoGasto> findById(Long id) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_TIPOS_GASTO.FNC_OBTENER_POR_ID(?)}")) {
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
            try (CallableStatement stmt = con.prepareCall("{call PKG_TIPOS_GASTO.PRC_ELIMINAR_TIPO(?)}")) {
                stmt.setLong(1, id);
                stmt.execute();
                return null;
            }
        });
    }

    @Override
    public void delete(TipoGasto tipo) {
        if (tipo.getId() != null)
            deleteById(tipo.getId());
    }

    @Override
    public List<TipoGasto> findAll() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<TipoGasto> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_TIPOS_GASTO.PRC_LISTAR_TIPOS(?)}")) {
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
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_TIPOS_GASTO.FNC_CONTAR_TIPOS()}")) {
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.execute();
                return cs.getLong(1);
            }
        });
    }

    @Override
    public Optional<TipoGasto> findByNombre(String nombre) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_TIPOS_GASTO.FNC_OBTENER_POR_NOMBRE(?)}")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.setString(2, nombre);
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
    public List<TipoGasto> findByActivo(Integer activo) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<TipoGasto> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_TIPOS_GASTO.PRC_LISTAR_POR_ACTIVO(?, ?)}")) {
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

    private TipoGasto mapFromResultSet(ResultSet rs) throws SQLException {
        TipoGasto tipo = new TipoGasto();
        tipo.setId(rs.getLong("id_tipo"));
        tipo.setNombre(rs.getString("nombre_gasto"));
        tipo.setRequiereFactura(rs.getInt("requiere_factura") == 1);
        tipo.setCuentaContable(rs.getString("cuenta_contable"));
        tipo.setEsAsignablePorDia(rs.getInt("es_asignable_por_dia") == 1);
        tipo.setUserCrea(rs.getString("user_crea"));
        tipo.setActivo(rs.getInt("activo"));
        Timestamp ts = rs.getTimestamp("fecha_crea");
        if (ts != null) {
            tipo.setFechaCrea(ts.toLocalDateTime());
        }
        return tipo;
    }
}
