package com.viatico.proyect.repository.jpa;

import com.viatico.proyect.entity.RendicionCuentas;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RendicionCuentasJpaRepository extends JpaRepository<RendicionCuentas, Long> {
    Optional<RendicionCuentas> findBySolicitudId(Long solicitudId);
}
