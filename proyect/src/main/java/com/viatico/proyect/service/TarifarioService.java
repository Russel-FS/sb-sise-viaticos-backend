package com.viatico.proyect.service;

import com.viatico.proyect.entity.Tarifario;
import java.util.List;

public interface TarifarioService {
    List<Tarifario> listarTodos();

    Tarifario guardar(Tarifario tarifario, String username);

    void eliminar(Long id);
}
