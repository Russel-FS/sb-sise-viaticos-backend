package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.Empleado;
import com.viatico.proyect.entity.NivelJerarquico;
import com.viatico.proyect.repository.interfaces.EmpleadoRepository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoRepositoryImpl implements EmpleadoRepository {

    private final JdbcTemplate jdbcTemplate;

    public EmpleadoRepositoryImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Empleado> listarTodos() {
        return jdbcTemplate.execute((Connection conn) -> {
            List<Empleado> empleados = new ArrayList<>();

            try (CallableStatement cs = conn.prepareCall("{call PKG_EMPLEADOS.PRC_LISTAR_EMPLEADOS(?)}")) {
                cs.registerOutParameter(1, Types.REF_CURSOR);
                cs.execute();

                try (ResultSet rs = (ResultSet) cs.getObject(1)) {
                    while (rs.next()) {
                        Empleado emp = new Empleado();
                        emp.setId(rs.getLong("id_empleado"));
                        emp.setNombres(rs.getString("nombres"));
                        emp.setApellidos(rs.getString("apellidos"));
                        emp.setDni(rs.getString("dni"));
                        emp.setEmail(rs.getString("email"));

                        NivelJerarquico nivel = new NivelJerarquico();
                        nivel.setId(rs.getLong("id_nivel"));
                        emp.setNivel(nivel);

                        emp.setCuentaBancaria(rs.getString("cuenta_bancaria"));
                        emp.setUserCrea(rs.getString("user_crea"));

                        Timestamp ts = rs.getTimestamp("fecha_crea");
                        if (ts != null) {
                            emp.setFechaCrea(ts.toLocalDateTime());
                        }

                        empleados.add(emp);
                    }
                }
            }

            return empleados;
        });
    }

    @Override
    public Long guardarEmpleado(Empleado empleado, String userCrea) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall(
                    "{call PKG_EMPLEADOS.PRC_GUARDAR_EMPLEADO(?, ?, ?, ?, ?, ?, ?, ?, ?)}")) {

                if (empleado.getId() != null) {
                    cs.setLong(1, empleado.getId());
                } else {
                    cs.setNull(1, Types.NUMERIC);
                }
                cs.setString(2, empleado.getNombres());
                cs.setString(3, empleado.getApellidos());
                cs.setString(4, empleado.getDni());
                cs.setString(5, empleado.getEmail());
                cs.setLong(6, empleado.getNivel().getId());
                cs.setString(7, empleado.getCuentaBancaria());
                cs.setString(8, userCrea);
                cs.registerOutParameter(9, Types.NUMERIC);

                cs.execute();

                return cs.getLong(9);
            }
        });
    }

    @Override
    public void eliminarEmpleado(Long id) {
        jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall("{call PKG_EMPLEADOS.PRC_ELIMINAR_EMPLEADO(?)}")) {
                cs.setLong(1, id);
                cs.execute();
            }
            return null;
        });
    }

    @Override
    public Optional<Empleado> obtenerPorId(Long id) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall(
                    "{ ? = call PKG_EMPLEADOS.FNC_OBTENER_EMPLEADO(?) }")) {

                cs.registerOutParameter(1, Types.STRUCT, "T_EMPLEADO_REC");
                cs.setLong(2, id);
                cs.execute();

                Object result = cs.getObject(1);
                if (result == null) {
                    return Optional.empty();
                }

                Struct struct = (Struct) result;
                Object[] attrs = struct.getAttributes();

                Empleado emp = new Empleado();
                emp.setId(((Number) attrs[0]).longValue());
                emp.setNombres((String) attrs[1]);
                emp.setApellidos((String) attrs[2]);
                emp.setDni((String) attrs[3]);
                emp.setEmail((String) attrs[4]);

                NivelJerarquico nivel = new NivelJerarquico();
                nivel.setId(((Number) attrs[5]).longValue());
                emp.setNivel(nivel);

                emp.setCuentaBancaria((String) attrs[6]);
                emp.setUserCrea((String) attrs[7]);

                if (attrs[8] != null) {
                    emp.setFechaCrea(((Timestamp) attrs[8]).toLocalDateTime());
                }

                return Optional.of(emp);

            } catch (SQLException e) {
                if (e.getErrorCode() == 20004) {
                    return Optional.empty();
                }
                throw e;
            }
        });
    }

    @Override
    public boolean existeDni(String dni, Long idActual) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall(
                    "{ ? = call PKG_EMPLEADOS.FNC_VALIDAR_DNI(?, ?) }")) {

                cs.registerOutParameter(1, Types.NUMERIC);
                cs.setString(2, dni);

                if (idActual != null) {
                    cs.setLong(3, idActual);
                } else {
                    cs.setNull(3, Types.NUMERIC);
                }

                cs.execute();
                return cs.getInt(1) == 1;
            }
        });
    }

    @Override
    public boolean existeEmail(String email, Long idActual) {
        return jdbcTemplate.execute((Connection conn) -> {
            try (CallableStatement cs = conn.prepareCall(
                    "{ ? = call PKG_EMPLEADOS.FNC_VALIDAR_EMAIL(?, ?) }")) {

                cs.registerOutParameter(1, Types.NUMERIC);
                cs.setString(2, email);

                if (idActual != null) {
                    cs.setLong(3, idActual);
                } else {
                    cs.setNull(3, Types.NUMERIC);
                }

                cs.execute();
                return cs.getInt(1) == 1;
            }
        });
    }

}
