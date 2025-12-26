package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.RendicionCuentas;
import java.util.Optional;
import java.util.List;

public interface RendicionCuentasRepository {
    RendicionCuentas save(RendicionCuentas rendicion);

    Optional<RendicionCuentas> findById(Long id);

    void deleteById(Long id);

    void delete(RendicionCuentas rendicion);

    List<RendicionCuentas> findAll();

    long count();

    Optional<RendicionCuentas> findBySolicitudId(Long solicitudId);
}
