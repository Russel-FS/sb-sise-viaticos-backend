package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.NivelJerarquico;
import com.viatico.proyect.repository.interfaces.NivelJerarquicoRepository;
import com.viatico.proyect.repository.jpa.NivelJerarquicoJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class NivelJerarquicoRepositoryImpl implements NivelJerarquicoRepository {

    private final NivelJerarquicoJpaRepository jpaRepository;

    public NivelJerarquicoRepositoryImpl(NivelJerarquicoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public NivelJerarquico save(NivelJerarquico nivel) {
        return jpaRepository.save(nivel);
    }

    @Override
    public Optional<NivelJerarquico> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(NivelJerarquico nivel) {
        jpaRepository.delete(nivel);
    }

    @Override
    public List<NivelJerarquico> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Optional<NivelJerarquico> findByNombre(String nombre) {
        return jpaRepository.findByNombre(nombre);
    }

    @Override
    public List<NivelJerarquico> findByActivo(Integer activo) {
        return jpaRepository.findByActivo(activo);
    }
}
