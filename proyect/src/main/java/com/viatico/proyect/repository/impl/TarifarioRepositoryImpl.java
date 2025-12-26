package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.NivelJerarquico;
import com.viatico.proyect.entity.Tarifario;
import com.viatico.proyect.entity.ZonaGeografica;
import com.viatico.proyect.repository.interfaces.TarifarioRepository;
import com.viatico.proyect.repository.jpa.TarifarioJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class TarifarioRepositoryImpl implements TarifarioRepository {

    private final TarifarioJpaRepository jpaRepository;

    public TarifarioRepositoryImpl(TarifarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Tarifario save(Tarifario tarifario) {
        return jpaRepository.save(tarifario);
    }

    @Override
    public Optional<Tarifario> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(Tarifario tarifario) {
        jpaRepository.delete(tarifario);
    }

    @Override
    public List<Tarifario> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<Tarifario> findAllByNivelJerarquicoAndZonaGeografica(NivelJerarquico nivel, ZonaGeografica zona) {
        return jpaRepository.findAllByNivelJerarquicoAndZonaGeografica(nivel, zona);
    }

    @Override
    public List<Tarifario> findByActivo(Integer activo) {
        return jpaRepository.findByActivo(activo);
    }
}
