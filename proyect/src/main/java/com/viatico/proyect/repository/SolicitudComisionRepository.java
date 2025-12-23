package com.viatico.proyect.repository;

import com.viatico.proyect.entity.EstadoSolicitud;
import com.viatico.proyect.entity.SolicitudComision;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SolicitudComisionRepository extends JpaRepository<SolicitudComision, Long> {
    List<SolicitudComision> findByEmpleadoIdOrderByFechaCreaDesc(Long empleadoId);

    List<SolicitudComision> findByEstadoOrderByFechaCreaDesc(EstadoSolicitud estado);
}
