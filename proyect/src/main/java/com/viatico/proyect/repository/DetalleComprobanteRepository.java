package com.viatico.proyect.repository;

import com.viatico.proyect.entity.DetalleComprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DetalleComprobanteRepository extends JpaRepository<DetalleComprobante, Long> {
    List<DetalleComprobante> findByRendicionId(Long rendicionId);
}
