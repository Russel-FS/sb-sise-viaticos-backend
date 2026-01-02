package com.viatico.proyect.domain.repositories;

import java.util.Optional;

import com.viatico.proyect.domain.entity.NivelJerarquico;

import java.util.List;

public interface NivelJerarquicoRepository {
    NivelJerarquico save(NivelJerarquico nivel);

    Optional<NivelJerarquico> findById(Long id);

    void deleteById(Long id);

    void delete(NivelJerarquico nivel);

    List<NivelJerarquico> findAll();

    long count();

    Optional<NivelJerarquico> findByNombre(String nombre);

    List<NivelJerarquico> findByActivo(Integer activo);
}
