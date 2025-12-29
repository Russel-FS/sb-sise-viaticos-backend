package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.ZonaGeografica;
import com.viatico.proyect.repository.interfaces.ZonaGeograficaRepository;
import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ZonaGeograficaRepositoryImpl implements ZonaGeograficaRepository {

    private final JdbcTemplate jdbcTemplate;

    public ZonaGeograficaRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public ZonaGeografica save(ZonaGeografica zona) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{call PKG_ZONAS.PRC_GUARDAR_ZONA(?, ?, ?, ?, ?)}")) {
                if (zona.getId() != null) {
                    cs.setLong(1, zona.getId());
                } else {
                    cs.setNull(1, Types.NUMERIC);
                }
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.setString(2, zona.getNombre());
                cs.setString(3, zona.getDescripcion());
                cs.setInt(4, zona.getActivo() != null ? zona.getActivo() : 1);
                cs.setString(5, zona.getUserCrea());

                cs.execute();

                long newId = cs.getLong(1);
                if (zona.getId() == null) {
                    zona.setId(newId);
                }
                return zona;
            }
        });
    }

    @Override
    public Optional<ZonaGeografica> findById(Long id) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_ZONAS.FNC_OBTENER_POR_ID(?)}")) {
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
            try (CallableStatement stmt = con.prepareCall("{call PKG_ZONAS.PRC_ELIMINAR_ZONA(?)}")) {
                stmt.setLong(1, id);
                stmt.execute();
                return null;
            }
        });
    }

    @Override
    public void delete(ZonaGeografica zona) {
        if (zona.getId() != null)
            deleteById(zona.getId());
    }

    @Override
    public List<ZonaGeografica> findAll() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<ZonaGeografica> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_ZONAS.PRC_LISTAR_ZONAS(?)}")) {
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
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_ZONAS.FNC_CONTAR_ZONAS()}")) {
                cs.registerOutParameter(1, Types.NUMERIC);
                cs.execute();
                return cs.getLong(1);
            }
        });
    }

    @Override
    public Optional<ZonaGeografica> findByNombre(String nombre) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_ZONAS.FNC_OBTENER_POR_NOMBRE(?)}")) {
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
    public List<ZonaGeografica> findByActivo(Integer activo) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<ZonaGeografica> lista = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_ZONAS.PRC_LISTAR_POR_ACTIVO(?, ?)}")) {
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

    private ZonaGeografica mapFromResultSet(ResultSet rs) throws SQLException {
        ZonaGeografica zona = new ZonaGeografica();
        zona.setId(rs.getLong("id_zona"));
        zona.setNombre(rs.getString("nombre_zona"));
        zona.setDescripcion(rs.getString("descripcion"));
        zona.setUserCrea(rs.getString("user_crea"));
        zona.setActivo(rs.getInt("activo"));
        Timestamp ts = rs.getTimestamp("fecha_crea");
        if (ts != null) {
            zona.setFechaCrea(ts.toLocalDateTime());
        }
        return zona;
    }
}
