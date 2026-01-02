package com.viatico.proyect.application.service.interfaces;

import java.util.List;

import com.viatico.proyect.domain.entity.NivelJerarquico;

public interface NivelJerarquicoService {
    List<NivelJerarquico> listarTodos();

    NivelJerarquico guardar(NivelJerarquico nivel, String username);

    void eliminar(Long id);

    NivelJerarquico obtenerPorId(Long id);
}
