package com.viatico.proyect.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.viatico.proyect.domain.entity.TipoGasto;

public interface TipoGastoRepository {
    TipoGasto save(TipoGasto tipoGasto);

    Optional<TipoGasto> findById(Long id);

    void deleteById(Long id);

    void delete(TipoGasto tipoGasto);

    List<TipoGasto> findAll();

    long count();

    Optional<TipoGasto> findByNombre(String nombre);

    List<TipoGasto> findByActivo(Integer activo);
}
