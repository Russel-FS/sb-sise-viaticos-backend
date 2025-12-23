package com.viatico.proyect.repository;

import com.viatico.proyect.entity.LiquidacionFinal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface LiquidacionFinalRepository extends JpaRepository<LiquidacionFinal, Long> {
    Optional<LiquidacionFinal> findBySolicitudId(Long solicitudId);
}
