package com.viatico.proyect.application.service.impl;

import com.viatico.proyect.application.service.interfaces.NivelJerarquicoService;
import com.viatico.proyect.domain.entity.NivelJerarquico;
import com.viatico.proyect.domain.repositories.NivelJerarquicoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NivelJerarquicoServiceImpl implements NivelJerarquicoService {

    private final NivelJerarquicoRepository nivelRepository;

    @Override
    public List<NivelJerarquico> listarTodos() {
        return nivelRepository.findByActivo(1);
    }

    @Override
    @Transactional
    public NivelJerarquico guardar(NivelJerarquico nivel, String username) {
        if (nivel.getId() == null) {
            nivel.setUserCrea(username);
            nivel.setFechaCrea(LocalDateTime.now());
        }
        return nivelRepository.save(nivel);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        NivelJerarquico nj = nivelRepository.findById(id).orElseThrow();
        nj.setActivo(0);
        nivelRepository.save(nj);
    }

    @Override
    public NivelJerarquico obtenerPorId(Long id) {
        return nivelRepository.findById(id).orElseThrow(() -> new RuntimeException("Nivel no encontrado"));
    }
}
