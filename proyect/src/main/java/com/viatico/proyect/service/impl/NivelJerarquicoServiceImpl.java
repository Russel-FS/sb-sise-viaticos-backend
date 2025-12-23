package com.viatico.proyect.service.impl;

import com.viatico.proyect.entity.NivelJerarquico;
import com.viatico.proyect.repository.NivelJerarquicoRepository;
import com.viatico.proyect.service.NivelJerarquicoService;
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
}
