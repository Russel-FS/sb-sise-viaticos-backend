package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.TipoGasto;
import com.viatico.proyect.repository.interfaces.TipoGastoRepository;
import com.viatico.proyect.repository.jpa.TipoGastoJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class TipoGastoRepositoryImpl implements TipoGastoRepository {

    private final TipoGastoJpaRepository jpaRepository;

    public TipoGastoRepositoryImpl(TipoGastoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public TipoGasto save(TipoGasto tipoGasto) {
        return jpaRepository.save(tipoGasto);
    }

    @Override
    public Optional<TipoGasto> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(TipoGasto tipoGasto) {
        jpaRepository.delete(tipoGasto);
    }

    @Override
    public List<TipoGasto> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public List<TipoGasto> findByActivo(Integer activo) {
        return jpaRepository.findByActivo(activo);
    }
}
