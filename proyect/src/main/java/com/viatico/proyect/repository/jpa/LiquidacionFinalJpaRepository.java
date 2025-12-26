package com.viatico.proyect.repository.jpa;

import com.viatico.proyect.entity.LiquidacionFinal;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface LiquidacionFinalJpaRepository extends JpaRepository<LiquidacionFinal, Long> {
    Optional<LiquidacionFinal> findBySolicitudId(Long solicitudId);
}
