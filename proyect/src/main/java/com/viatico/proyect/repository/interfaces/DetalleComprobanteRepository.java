package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.DetalleComprobante;
import java.util.List;
import java.util.Optional;

public interface DetalleComprobanteRepository {
    DetalleComprobante save(DetalleComprobante detalle);

    Optional<DetalleComprobante> findById(Long id);

    void deleteById(Long id);

    void delete(DetalleComprobante detalle);

    List<DetalleComprobante> findAll();

    long count();

    List<DetalleComprobante> findByRendicionId(Long rendicionId);
}
