package com.viatico.proyect.infrastructure.repository.impl;

import com.viatico.proyect.domain.entity.Rol;
import com.viatico.proyect.domain.repositories.RolRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class RolRepositoryImpl implements RolRepository {

    private final JdbcTemplate jdbcTemplate;

    public RolRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Rol save(Rol rol) {
        return jdbcTemplate.execute((java.sql.Connection conn) -> {
            try (java.sql.CallableStatement cs = conn.prepareCall("{call PKG_ROLES.PRC_GUARDAR_ROL(?, ?, ?)}")) {
                if (rol.getId() != null) {
                    cs.setLong(1, rol.getId());
                } else {
                    cs.setNull(1, java.sql.Types.NUMERIC);
                }
                cs.registerOutParameter(1, java.sql.Types.NUMERIC);
                cs.setString(2, rol.getNombre());
                cs.setString(3, rol.getCodigo());

                cs.execute();

                long newId = cs.getLong(1);
                if (rol.getId() == null) {
                    rol.setId(newId);
                }
                return rol;
            }
        });
    }

    @Override
    public Optional<Rol> findById(Long id) {
        return jdbcTemplate.execute((java.sql.Connection conn) -> {
            try (java.sql.CallableStatement cs = conn.prepareCall("{? = call PKG_ROLES.FNC_OBTENER_POR_ID(?)}")) {
                cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
                cs.setLong(2, id);
                cs.execute();

                try (java.sql.ResultSet rs = (java.sql.ResultSet) cs.getObject(1)) {
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
        jdbcTemplate.execute((java.sql.Connection con) -> {
            try (java.sql.CallableStatement stmt = con.prepareCall("{call PKG_ROLES.PRC_ELIMINAR_ROL(?)}")) {
                stmt.setLong(1, id);
                stmt.execute();
                return null;
            }
        });
    }

    @Override
    public void delete(Rol rol) {
        if (rol.getId() != null)
            deleteById(rol.getId());
    }

    @Override
    public List<Rol> findAll() {
        return jdbcTemplate.execute((java.sql.Connection conn) -> {
            List<Rol> lista = new java.util.ArrayList<>();
            try (java.sql.CallableStatement cs = conn.prepareCall("{call PKG_ROLES.PRC_LISTAR_ROLES(?)}")) {
                cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
                cs.execute();

                try (java.sql.ResultSet rs = (java.sql.ResultSet) cs.getObject(1)) {
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
        return jdbcTemplate.execute((java.sql.Connection conn) -> {
            try (java.sql.CallableStatement cs = conn.prepareCall("{? = call PKG_ROLES.FNC_CONTAR_ROLES()}")) {
                cs.registerOutParameter(1, java.sql.Types.NUMERIC);
                cs.execute();
                return cs.getLong(1);
            }
        });
    }

    @Override
    public Optional<Rol> findByCodigo(String codigo) {
        return jdbcTemplate.execute((java.sql.Connection conn) -> {
            try (java.sql.CallableStatement cs = conn.prepareCall("{? = call PKG_ROLES.FNC_OBTENER_POR_CODIGO(?)}")) {
                cs.registerOutParameter(1, oracle.jdbc.OracleTypes.CURSOR);
                cs.setString(2, codigo);
                cs.execute();

                try (java.sql.ResultSet rs = (java.sql.ResultSet) cs.getObject(1)) {
                    if (rs.next()) {
                        return Optional.of(mapFromResultSet(rs));
                    }
                }
            }
            return Optional.empty();
        });
    }

    private Rol mapFromResultSet(java.sql.ResultSet rs) throws java.sql.SQLException {
        Rol rol = new Rol();
        rol.setId(rs.getLong("id_rol"));
        rol.setNombre(rs.getString("nombre_rol"));
        rol.setCodigo(rs.getString("codigo"));
        return rol;
    }
}
