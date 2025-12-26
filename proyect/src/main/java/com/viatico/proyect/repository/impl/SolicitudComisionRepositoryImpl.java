package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.EstadoSolicitud;
import com.viatico.proyect.entity.SolicitudComision;
import com.viatico.proyect.repository.interfaces.SolicitudComisionRepository;
import com.viatico.proyect.repository.jpa.SolicitudComisionJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class SolicitudComisionRepositoryImpl implements SolicitudComisionRepository {

    private final SolicitudComisionJpaRepository jpaRepository;

    public SolicitudComisionRepositoryImpl(SolicitudComisionJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
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
}
