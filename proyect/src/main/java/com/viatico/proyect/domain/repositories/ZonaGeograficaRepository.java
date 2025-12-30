package com.viatico.proyect.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.viatico.proyect.domain.entity.ZonaGeografica;

public interface ZonaGeograficaRepository {
    ZonaGeografica save(ZonaGeografica zona);

    Optional<ZonaGeografica> findById(Long id);

    void deleteById(Long id);

    void delete(ZonaGeografica zona);

    List<ZonaGeografica> findAll();

    long count();

    Optional<ZonaGeografica> findByNombre(String nombre);

    List<ZonaGeografica> findByActivo(Integer activo);
}
