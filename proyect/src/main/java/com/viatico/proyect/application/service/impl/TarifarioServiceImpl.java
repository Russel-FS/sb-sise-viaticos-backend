package com.viatico.proyect.application.service.impl;

import com.viatico.proyect.application.service.interfaces.TarifarioService;
import com.viatico.proyect.domain.entity.Tarifario;
import com.viatico.proyect.domain.repositories.TarifarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TarifarioServiceImpl implements TarifarioService {

    private final TarifarioRepository tarifarioRepository;

    @Override
    public List<Tarifario> listarTodos() {
        return tarifarioRepository.findByActivo(1);
    }

    @Override
    @Transactional
    public Tarifario guardar(Tarifario tarifario, String username) {
        if (tarifario.getId() == null) {
            tarifario.setUserCrea(username);
            tarifario.setFechaCrea(LocalDateTime.now());
            if (tarifario.getMoneda() == null) {
                tarifario.setMoneda("PEN");
            }
        }
        return tarifarioRepository.save(tarifario);
    }

    @Override
    public Tarifario obtenerPorId(Long id) {
        return tarifarioRepository.findById(id).orElseThrow(() -> new RuntimeException("Tarifa no encontrada"));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Tarifario t = tarifarioRepository.findById(id).orElseThrow();
        t.setActivo(0);
        tarifarioRepository.save(t);
    }
}
