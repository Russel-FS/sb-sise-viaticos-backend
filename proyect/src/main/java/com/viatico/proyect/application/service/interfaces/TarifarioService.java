package com.viatico.proyect.application.service.interfaces;

import java.util.List;

import com.viatico.proyect.domain.entity.Tarifario;

public interface TarifarioService {
    List<Tarifario> listarTodos();

    Tarifario guardar(Tarifario tarifario, String username);

    void eliminar(Long id);
}
