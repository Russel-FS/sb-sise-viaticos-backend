package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.ZonaGeografica;
import com.viatico.proyect.repository.interfaces.ZonaGeograficaRepository;
import com.viatico.proyect.repository.jpa.ZonaGeograficaJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class ZonaGeograficaRepositoryImpl implements ZonaGeograficaRepository {

    private final ZonaGeograficaJpaRepository jpaRepository;

    public ZonaGeograficaRepositoryImpl(ZonaGeograficaJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public ZonaGeografica save(ZonaGeografica zona) {
        return jpaRepository.save(zona);
    }

    @Override
    public Optional<ZonaGeografica> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(ZonaGeografica zona) {
        jpaRepository.delete(zona);
    }

    @Override
    public List<ZonaGeografica> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<ZonaGeografica> findByActivo(Integer activo) {
        return jpaRepository.findByActivo(activo);
    }
}
