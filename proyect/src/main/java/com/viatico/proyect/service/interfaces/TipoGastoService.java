package com.viatico.proyect.service.interfaces;

import com.viatico.proyect.entity.TipoGasto;
import java.util.List;

public interface TipoGastoService {
    List<TipoGasto> listarTodos();

    TipoGasto guardar(TipoGasto tipoGasto, String username);

    void eliminar(Long id);
}
