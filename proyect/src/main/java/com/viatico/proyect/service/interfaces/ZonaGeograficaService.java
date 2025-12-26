package com.viatico.proyect.service.interfaces;

import com.viatico.proyect.entity.ZonaGeografica;
import java.util.List;

public interface ZonaGeograficaService {
    List<ZonaGeografica> listarTodas();

    ZonaGeografica guardar(ZonaGeografica zona, String username);

    void eliminar(Long id);
}
