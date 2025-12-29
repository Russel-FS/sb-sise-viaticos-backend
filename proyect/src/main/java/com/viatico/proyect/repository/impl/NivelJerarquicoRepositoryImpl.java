package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.NivelJerarquico;
import com.viatico.proyect.repository.interfaces.NivelJerarquicoRepository;
import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class NivelJerarquicoRepositoryImpl implements NivelJerarquicoRepository {

    private final JdbcTemplate jdbcTemplate;

    public NivelJerarquicoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public NivelJerarquico save(NivelJerarquico n) {
        return jdbcTemplate.execute((Connection con) -> {
            String sql = "{call PKG_NIVELES.PRC_GUARDAR_NIVEL(?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                if (n.getId() == null) {
                    stmt.setNull(1, Types.NUMERIC);
                } else {
                    stmt.setLong(1, n.getId());
                }
                stmt.registerOutParameter(1, Types.NUMERIC);
                stmt.setString(2, n.getNombre());
                stmt.setString(3, n.getDescripcion());
                stmt.setInt(4, n.getActivo() != null ? n.getActivo() : 1);
                stmt.setString(5, n.getUserCrea());

                stmt.execute();

                long newId = stmt.getLong(1);
                if (n.getId() == null) {
                    n.setId(newId);
                }
                return n;
            }
        });
    }

    @Override
    public Optional<NivelJerarquico> findById(Long id) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_NIVELES.FNC_OBTENER_POR_ID(?)}")) {
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
            String sql = "{call PKG_NIVELES.PRC_ELIMINAR_NIVEL(?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                stmt.setLong(1, id);
                stmt.execute();
                return null;
            }
        });
    }

    @Override
    public void delete(NivelJerarquico n) {
        if (n.getId() != null)
            deleteById(n.getId());
    }

    @Override
    public List<NivelJerarquico> findAll() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<NivelJerarquico> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_NIVELES.PRC_LISTAR_NIVELES(?)}")) {
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
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_NIVELES.FNC_CONTAR_NIVELES()}")) {
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.execute();
                return cs.getLong(1);
            }
        });
    }

    @Override
    public Optional<NivelJerarquico> findByNombre(String nombre) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_NIVELES.FNC_OBTENER_POR_NOMBRE(?)}")) {
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
    public List<NivelJerarquico> findByActivo(Integer activo) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<NivelJerarquico> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_NIVELES.PRC_LISTAR_POR_ACTIVO(?, ?)}")) {
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

    private NivelJerarquico mapFromResultSet(ResultSet rs) throws SQLException {
        NivelJerarquico n = new NivelJerarquico();
        n.setId(rs.getLong("id_nivel"));
        n.setNombre(rs.getString("nombre_nivel"));
        n.setDescripcion(rs.getString("descripcion"));
        n.setUserCrea(rs.getString("user_crea"));
        n.setActivo(rs.getInt("activo"));
        Timestamp ts = rs.getTimestamp("fecha_crea");
        if (ts != null) {
            n.setFechaCrea(ts.toLocalDateTime());
        }
        return n;
    }
}
