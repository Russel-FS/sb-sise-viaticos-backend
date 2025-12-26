package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.NivelJerarquico;
import com.viatico.proyect.entity.Tarifario;
import com.viatico.proyect.entity.ZonaGeografica;
import java.util.List;
import java.util.Optional;

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
