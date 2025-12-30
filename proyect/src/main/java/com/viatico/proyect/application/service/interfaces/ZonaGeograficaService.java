package com.viatico.proyect.application.service.interfaces;

import java.util.List;

import com.viatico.proyect.domain.entity.ZonaGeografica;

public interface ZonaGeograficaService {
    List<ZonaGeografica> listarTodas();

    ZonaGeografica guardar(ZonaGeografica zona, String username);

    void eliminar(Long id);
}
