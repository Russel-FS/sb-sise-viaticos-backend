package com.viatico.proyect.service.impl;

import com.viatico.proyect.entity.ZonaGeografica;
import com.viatico.proyect.repository.ZonaGeograficaRepository;
import com.viatico.proyect.service.ZonaGeograficaService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ZonaGeograficaServiceImpl implements ZonaGeograficaService {

    private final ZonaGeograficaRepository zonaRepository;

    @Override
    public List<ZonaGeografica> listarTodas() {
        return zonaRepository.findByActivo(1);
    }

    @Override
    @Transactional
    public ZonaGeografica guardar(ZonaGeografica zona, String username) {
        if (zona.getId() == null) {
            zona.setUserCrea(username);
            zona.setFechaCrea(LocalDateTime.now());
        }
        return zonaRepository.save(zona);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        ZonaGeografica zg = zonaRepository.findById(id).orElseThrow();
        zg.setActivo(0);
        zonaRepository.save(zg);
    }
}
