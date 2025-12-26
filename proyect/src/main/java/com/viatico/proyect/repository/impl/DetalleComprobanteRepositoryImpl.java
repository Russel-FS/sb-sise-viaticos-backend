package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.DetalleComprobante;
import com.viatico.proyect.repository.interfaces.DetalleComprobanteRepository;
import com.viatico.proyect.repository.jpa.DetalleComprobanteJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class DetalleComprobanteRepositoryImpl implements DetalleComprobanteRepository {

    private final DetalleComprobanteJpaRepository jpaRepository;

    public DetalleComprobanteRepositoryImpl(DetalleComprobanteJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DetalleComprobante save(DetalleComprobante detalle) {
        return jpaRepository.save(detalle);
    }

    @Override
    public Optional<DetalleComprobante> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(DetalleComprobante detalle) {
        jpaRepository.delete(detalle);
    }

    @Override
    public List<DetalleComprobante> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<DetalleComprobante> findByRendicionId(Long rendicionId) {
        return jpaRepository.findByRendicionId(rendicionId);
    }
}
