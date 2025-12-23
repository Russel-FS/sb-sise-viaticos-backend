package com.viatico.proyect.service.impl;

import com.viatico.proyect.entity.TipoGasto;
import com.viatico.proyect.repository.TipoGastoRepository;
import com.viatico.proyect.service.TipoGastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TipoGastoServiceImpl implements TipoGastoService {

    private final TipoGastoRepository tipoGastoRepository;

    @Override
    public List<TipoGasto> listarTodos() {
        return tipoGastoRepository.findByActivo(1);
    }

    @Override
    @Transactional
    public TipoGasto guardar(TipoGasto tipoGasto, String username) {
        if (tipoGasto.getId() == null) {
            tipoGasto.setUserCrea(username);
            tipoGasto.setFechaCrea(LocalDateTime.now());
        }

        if (tipoGasto.getRequiereFactura() == null)
            tipoGasto.setRequiereFactura(0);
        if (tipoGasto.getEsAsignablePorDia() == null)
            tipoGasto.setEsAsignablePorDia(0);

        return tipoGastoRepository.save(tipoGasto);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        TipoGasto tg = tipoGastoRepository.findById(id).orElseThrow();
        tg.setActivo(0);
        tipoGastoRepository.save(tg);
    }
}
