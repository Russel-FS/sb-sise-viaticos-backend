package com.viatico.proyect.repository;

import com.viatico.proyect.entity.RendicionCuentas;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RendicionCuentasRepository extends JpaRepository<RendicionCuentas, Long> {
    Optional<RendicionCuentas> findBySolicitudId(Long solicitudId);
}
