package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.ZonaGeografica;
import java.util.List;
import java.util.Optional;

public interface ZonaGeograficaRepository {
    ZonaGeografica save(ZonaGeografica zona);

    Optional<ZonaGeografica> findById(Long id);

    void deleteById(Long id);

    void delete(ZonaGeografica zona);

    List<ZonaGeografica> findAll();

    long count();

    List<ZonaGeografica> findByActivo(Integer activo);
}
