package com.viatico.proyect.domain.repositories;

import java.util.Optional;

import com.viatico.proyect.domain.entity.LiquidacionFinal;

import java.util.List;

public interface LiquidacionFinalRepository {
    LiquidacionFinal save(LiquidacionFinal liquidacion);

    Optional<LiquidacionFinal> findById(Long id);

    void deleteById(Long id);

    void delete(LiquidacionFinal liquidacion);

    List<LiquidacionFinal> findAll();

    long count();

    Optional<LiquidacionFinal> findBySolicitudId(Long solicitudId);
}
