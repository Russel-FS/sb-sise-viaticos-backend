package com.viatico.proyect.application.service.interfaces;

import java.util.List;

import com.viatico.proyect.domain.entity.TipoGasto;

public interface TipoGastoService {
    List<TipoGasto> listarTodos();

    TipoGasto guardar(TipoGasto tipoGasto, String username);

    void eliminar(Long id);
}
