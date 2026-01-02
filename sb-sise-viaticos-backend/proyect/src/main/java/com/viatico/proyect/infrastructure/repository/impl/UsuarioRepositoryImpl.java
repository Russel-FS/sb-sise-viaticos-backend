package com.viatico.proyect.infrastructure.repository.impl;

import com.viatico.proyect.domain.entity.Empleado;
import com.viatico.proyect.domain.entity.Rol;
import com.viatico.proyect.domain.entity.Usuario;
import com.viatico.proyect.domain.repositories.UsuarioRepository;

import oracle.jdbc.OracleConnection;
import oracle.jdbc.OracleTypes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final JdbcTemplate jdbcTemplate;

    public UsuarioRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Long crearUsuario(Long idEmpleado, String username, String email,
            String password, String userCrea) {
        return jdbcTemplate.execute((Connection con) -> {
            String sql = "{call PKG_USUARIOS.PRC_CREAR_USUARIO(?, ?, ?, ?, ?, ?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                stmt.setLong(1, idEmpleado);
                stmt.setString(2, username);
                stmt.setString(3, email);
                stmt.setString(4, password);
                stmt.setString(5, userCrea);
                stmt.registerOutParameter(6, Types.NUMERIC);
                stmt.execute();
                return stmt.getLong(6);
            }
        });
    }

    @Override
    public void asignarRoles(Long idUsuario, List<Long> rolIds, String userCrea) {
        jdbcTemplate.execute((Connection con) -> {

            BigDecimal[] rolesArray = rolIds.stream()
                    .map(BigDecimal::valueOf)
                    .toArray(BigDecimal[]::new);

            OracleConnection oraConn = con.unwrap(OracleConnection.class);
            Array sqlArray = oraConn.createOracleArray("T_ID_TAB", rolesArray);

            String sql = "{call PKG_USUARIOS.PRC_ASIGNAR_ROLES(?, ?, ?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                stmt.setLong(1, idUsuario);
                stmt.setArray(2, sqlArray);
                stmt.setString(3, userCrea);
                stmt.execute();
                return null;
            }
        });
    }

    @Override
    public List<Rol> obtenerRolesUsuario(Long idUsuario) {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Rol> roles = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_USUARIOS.PRC_OBTENER_ROLES_USUARIO(?, ?)}")) {
                cs.setLong(1, idUsuario);
                cs.registerOutParameter(2, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(2)) {
                    while (rs.next()) {
                        Rol rol = new Rol();
                        rol.setId(rs.getLong("id_rol"));
                        rol.setCodigo(rs.getString("codigo_rol"));
                        rol.setNombre(rs.getString("nombre_rol"));
                        roles.add(rol);
                    }
                }
            }
            return roles;
        });
    }

    @Override
    public void eliminarPorEmpleado(Long idEmpleado) {
        jdbcTemplate.execute((Connection con) -> {
            String sql = "{call PKG_USUARIOS.PRC_ELIMINAR_USUARIO_POR_EMPLEADO(?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                stmt.setLong(1, idEmpleado);
                stmt.execute();
                return null;
            }
        });
    }

    @Override
    public Long obtenerIdPorEmpleado(Long idEmpleado) {
        return jdbcTemplate.execute((Connection con) -> {
            String sql = "{? = call PKG_USUARIOS.FNC_OBTENER_USUARIO_POR_EMPLEADO(?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                stmt.registerOutParameter(1, Types.NUMERIC);
                stmt.setLong(2, idEmpleado);
                stmt.execute();

                Long result = stmt.getLong(1);
                return stmt.wasNull() ? null : result;
            }
        });
    }

    @Override
    public List<Usuario> listarTodos() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Usuario> usuarios = new ArrayList<>();
            try (CallableStatement cs = conn.prepareCall("{call PKG_USUARIOS.PRC_LISTAR_USUARIOS(?)}")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        Usuario usuario = mapUsuarioFromResultSet(rs);

                        usuario.setRoles(obtenerRolesUsuario(usuario.getId()));
                        usuarios.add(usuario);
                    }
                }
            }
            return usuarios;
        });
    }

    @Override
    public Optional<Usuario> obtenerPorUsername(String username) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_USUARIOS.FNC_OBTENER_POR_USERNAME(?)}")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.setString(2, username);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    if (rs.next()) {
                        Usuario usuario = mapUsuarioFromResultSet(rs);

                        usuario.setRoles(obtenerRolesUsuario(usuario.getId()));
                        return Optional.of(usuario);
                    }
                }
            }
            return Optional.empty();
        });
    }

    @Override
    public Optional<Usuario> obtenerPorEmail(String email) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{? = call PKG_USUARIOS.FNC_OBTENER_POR_EMAIL(?)}")) {
                cs.registerOutParameter(1, OracleTypes.CURSOR);
                cs.setString(2, email);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    if (rs.next()) {
                        Usuario usuario = mapUsuarioFromResultSet(rs);

                        usuario.setRoles(obtenerRolesUsuario(usuario.getId()));
                        return Optional.of(usuario);
                    }
                }
            }
            return Optional.empty();
        });
    }

    private Usuario mapUsuarioFromResultSet(ResultSet rs) throws SQLException {
        Usuario usuario = new Usuario();
        usuario.setId(rs.getLong("id_usuario"));

        Long idEmpleado = rs.getLong("id_empleado");
        if (!rs.wasNull()) {
            Empleado emp = new Empleado();
            emp.setId(idEmpleado);
            emp.setNombres(rs.getString("nombres_empleado"));
            emp.setApellidos(rs.getString("apellidos_empleado"));
            usuario.setEmpleado(emp);
        }

        usuario.setUsername(rs.getString("username"));
        usuario.setEmail(rs.getString("email"));
        usuario.setPassword(rs.getString("password"));
        usuario.setActivo(rs.getInt("activo"));
        usuario.setUserCrea(rs.getString("user_crea"));

        Timestamp ts = rs.getTimestamp("fecha_crea");
        if (ts != null) {
            usuario.setFechaCrea(ts.toLocalDateTime());
        }

        return usuario;
    }

    @Override
    public void actualizarPassword(Long idUsuario, String password) {
        jdbcTemplate.execute((Connection con) -> {
            String sql = "{call PKG_USUARIOS.PRC_ACTUALIZAR_PASSWORD(?, ?)}";
            try (CallableStatement stmt = con.prepareCall(sql)) {
                stmt.setLong(1, idUsuario);
                stmt.setString(2, password);
                stmt.execute();
                return null;
            }
        });
    }
}
