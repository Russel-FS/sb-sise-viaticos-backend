package com.viatico.proyect.service;

import com.viatico.proyect.entity.NivelJerarquico;
import java.util.List;

public interface NivelJerarquicoService {
    List<NivelJerarquico> listarTodos();

    NivelJerarquico guardar(NivelJerarquico nivel, String username);

    void eliminar(Long id);
}
