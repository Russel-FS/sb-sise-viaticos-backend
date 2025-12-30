package com.viatico.proyect.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.viatico.proyect.domain.entity.DetalleComprobante;

public interface DetalleComprobanteRepository {
    DetalleComprobante save(DetalleComprobante detalle);

    Optional<DetalleComprobante> findById(Long id);

    void deleteById(Long id);

    void delete(DetalleComprobante detalle);

    List<DetalleComprobante> findAll();

    long count();

    List<DetalleComprobante> findByRendicionId(Long rendicionId);
}
