package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.LiquidacionFinal;
import java.util.Optional;
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
