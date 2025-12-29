package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.EstadoSolicitud;
import com.viatico.proyect.entity.SolicitudComision;
import com.viatico.proyect.repository.interfaces.SolicitudComisionRepository;
import com.viatico.proyect.repository.jpa.SolicitudComisionJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.JdbcTemplate;
import java.sql.*;
import oracle.jdbc.OracleConnection;
import java.time.LocalDate;

@Repository
public class SolicitudComisionRepositoryImpl implements SolicitudComisionRepository {

    private final SolicitudComisionJpaRepository jpaRepository;
    private final JdbcTemplate jdbcTemplate;

    public SolicitudComisionRepositoryImpl(SolicitudComisionJpaRepository jpaRepository, JdbcTemplate jdbcTemplate) {
        this.jpaRepository = jpaRepository;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public SolicitudComision save(SolicitudComision solicitud) {
        return jpaRepository.save(solicitud);
    }

    @Override
    public Optional<SolicitudComision> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(SolicitudComision solicitud) {
        jpaRepository.delete(solicitud);
    }

    @Override
    public List<SolicitudComision> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<SolicitudComision> findByEmpleadoIdOrderByFechaCreaDesc(Long empleadoId) {
        return jpaRepository.findByEmpleadoIdOrderByFechaCreaDesc(empleadoId);
    }

    @Override
    public List<SolicitudComision> findByEstadoOrderByFechaCreaDesc(EstadoSolicitud estado) {
        return jpaRepository.findByEstadoOrderByFechaCreaDesc(estado);
    }

    @Override
    public List<SolicitudComision> findByEmpleadoIdAndEstadoOrderByFechaCreaDesc(Long empleadoId,
            EstadoSolicitud estado) {
        return jpaRepository.findByEmpleadoIdAndEstadoOrderByFechaCreaDesc(empleadoId, estado);
    }

    @Override
    public List<SolicitudComision> findTop5ByEmpleadoIdOrderByFechaCreaDesc(Long empleadoId) {
        return jpaRepository.findTop5ByEmpleadoIdOrderByFechaCreaDesc(empleadoId);
    }

    @Override
    public List<SolicitudComision> findTop5ByOrderByFechaCreaDesc() {
        return jpaRepository.findTop5ByOrderByFechaCreaDesc();
    }

    @Override
    public long countByEstado(EstadoSolicitud estado) {
        return jpaRepository.countByEstado(estado);
    }

    @Override
    public long countByEmpleadoIdAndEstado(Long empleadoId, EstadoSolicitud estado) {
        return jpaRepository.countByEmpleadoIdAndEstado(empleadoId, estado);
    }

    @Override
    public Long registrarSolicitudFull(Long empleadoId, String motivo, LocalDate fechaInicio,
            List<Long> idZonas, List<String> ciudades, List<Integer> noches, String userCrea) {
        return jdbcTemplate.execute((Connection conn) -> {
            OracleConnection oracleConn = conn.unwrap(OracleConnection.class);

            try (CallableStatement cs = oracleConn
                    .prepareCall("{call PKG_SOLICITUDES.PRC_REGISTRAR_SOLICITUD(?, ?, ?, ?, ?, ?)}")) {
                cs.setLong(1, empleadoId);
                cs.setString(2, motivo);
                cs.setDate(3, Date.valueOf(fechaInicio));

                Object[] structArray = new Object[idZonas.size()];
                for (int i = 0; i < idZonas.size(); i++) {
                    structArray[i] = oracleConn.createStruct("T_ITINERARIO_REC", new Object[] {
                            idZonas.get(i),
                            ciudades.get(i),
                            noches.get(i)
                    });
                }

                Array oracleArray = oracleConn.createOracleArray("T_ITINERARIO_TAB", structArray);
                cs.setArray(4, oracleArray);
                cs.setString(5, userCrea);
                cs.registerOutParameter(6, Types.NUMERIC);

                cs.execute();
                return cs.getLong(6);
            }
        });
    }
}
