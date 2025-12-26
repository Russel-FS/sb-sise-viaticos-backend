package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.EstadoSolicitud;
import com.viatico.proyect.entity.SolicitudComision;
import java.util.List;
import java.util.Optional;

public interface SolicitudComisionRepository {
    SolicitudComision save(SolicitudComision solicitud);

    Optional<SolicitudComision> findById(Long id);

    void deleteById(Long id);

    void delete(SolicitudComision solicitud);

    List<SolicitudComision> findAll();

    long count();

    // Custom methods
    List<SolicitudComision> findByEmpleadoIdOrderByFechaCreaDesc(Long empleadoId);

    List<SolicitudComision> findByEstadoOrderByFechaCreaDesc(EstadoSolicitud estado);

    List<SolicitudComision> findByEmpleadoIdAndEstadoOrderByFechaCreaDesc(Long empleadoId, EstadoSolicitud estado);

    List<SolicitudComision> findTop5ByEmpleadoIdOrderByFechaCreaDesc(Long empleadoId);

    List<SolicitudComision> findTop5ByOrderByFechaCreaDesc();

    long countByEstado(EstadoSolicitud estado);

    long countByEmpleadoIdAndEstado(Long empleadoId, EstadoSolicitud estado);
}
