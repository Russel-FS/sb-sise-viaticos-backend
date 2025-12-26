package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.RendicionCuentas;
import com.viatico.proyect.repository.interfaces.RendicionCuentasRepository;
import com.viatico.proyect.repository.jpa.RendicionCuentasJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class RendicionCuentasRepositoryImpl implements RendicionCuentasRepository {

    private final RendicionCuentasJpaRepository jpaRepository;

    public RendicionCuentasRepositoryImpl(RendicionCuentasJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public RendicionCuentas save(RendicionCuentas rendicion) {
        return jpaRepository.save(rendicion);
    }

    @Override
    public Optional<RendicionCuentas> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(RendicionCuentas rendicion) {
        jpaRepository.delete(rendicion);
    }

    @Override
    public List<RendicionCuentas> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Optional<RendicionCuentas> findBySolicitudId(Long solicitudId) {
        return jpaRepository.findBySolicitudId(solicitudId);
    }
}
