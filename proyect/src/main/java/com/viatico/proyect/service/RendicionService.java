package com.viatico.proyect.service;

import com.viatico.proyect.entity.RendicionCuentas;
import java.math.BigDecimal;

public interface RendicionService {
    RendicionCuentas obtenerOCrearPorSolicitud(Long solicitudId, String username);

    void agregarComprobante(Long rendicionId, Long tipoGastoId, String ruc, String razonSocial,
            String serie, String fecha, BigDecimal monto, String username);

    void eliminarComprobante(Long detalleId);

    RendicionCuentas obtenerPorSolicitud(Long solicitudId);

    void finalizarRendicion(Long rendicionId);

    void validarComprobante(Long detalleId, String estado, String motivo);

    void finalizarValidacion(Long solicitudId);
}
