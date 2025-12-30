package com.viatico.proyect.domain.repositories;

import java.util.Optional;

import com.viatico.proyect.domain.entity.RendicionCuentas;

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
