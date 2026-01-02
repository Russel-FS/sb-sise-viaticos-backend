package com.viatico.proyect.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.viatico.proyect.domain.entity.NivelJerarquico;
import com.viatico.proyect.domain.entity.Tarifario;
import com.viatico.proyect.domain.entity.ZonaGeografica;

public interface TarifarioRepository {
    Tarifario save(Tarifario tarifario);

    Optional<Tarifario> findById(Long id);

    void deleteById(Long id);

    void delete(Tarifario tarifario);

    List<Tarifario> findAll();

    long count();

    List<Tarifario> findAllByNivelJerarquicoAndZonaGeografica(NivelJerarquico nivel, ZonaGeografica zona);

    List<Tarifario> findByActivo(Integer activo);
}
