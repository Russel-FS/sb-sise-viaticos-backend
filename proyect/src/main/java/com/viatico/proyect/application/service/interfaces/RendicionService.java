package com.viatico.proyect.application.service.interfaces;

import java.math.BigDecimal;

import com.viatico.proyect.domain.entity.RendicionCuentas;

public interface RendicionService {
    RendicionCuentas obtenerOCrearPorSolicitud(Long solicitudId, String username);

    void agregarComprobante(Long rendicionId, Long tipoGastoId, String ruc, String razonSocial,
            String serie, String fecha, BigDecimal montoTotal,
            String fotoUrl, String username);

    void eliminarComprobante(Long detalleId);

    RendicionCuentas obtenerPorSolicitud(Long solicitudId);

    void finalizarRendicion(Long rendicionId);

    void validarComprobante(Long detalleId, String estado, String motivo);

    void finalizarValidacion(Long solicitudId);
}
