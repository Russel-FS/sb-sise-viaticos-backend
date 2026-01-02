package com.viatico.proyect.domain.repositories;

import com.viatico.proyect.domain.entity.SolicitudComision;
import com.viatico.proyect.domain.enums.EstadoSolicitud;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface SolicitudComisionRepository {
    SolicitudComision save(SolicitudComision solicitud);

    Optional<SolicitudComision> findById(Long id);

    void deleteById(Long id);

    void delete(SolicitudComision solicitud);

    List<SolicitudComision> findAll();

    long count();

    List<SolicitudComision> findByEmpleadoIdOrderByFechaCreaDesc(Long empleadoId);

    List<SolicitudComision> findByEstadoOrderByFechaCreaDesc(EstadoSolicitud estado);

    List<SolicitudComision> findByEmpleadoIdAndEstadoOrderByFechaCreaDesc(Long empleadoId, EstadoSolicitud estado);

    List<SolicitudComision> findTop5ByEmpleadoIdOrderByFechaCreaDesc(Long empleadoId);

    List<SolicitudComision> findTop5ByOrderByFechaCreaDesc();

    long countByEstado(EstadoSolicitud estado);

    long countByEmpleadoIdAndEstado(Long empleadoId, EstadoSolicitud estado);

    Long registrarSolicitudFull(Long empleadoId, String motivo, java.time.LocalDate fechaInicio,
            List<Long> idZonas, List<String> ciudades, List<Integer> noches, String userCrea);

    BigDecimal estimarMonto(Long idNivel, List<Long> idZonas, List<String> ciudades, List<Integer> noches);
}
