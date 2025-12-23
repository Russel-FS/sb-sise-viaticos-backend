package com.viatico.proyect.service.impl;

import com.viatico.proyect.entity.*;
import com.viatico.proyect.repository.*;
import com.viatico.proyect.service.RendicionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class RendicionServiceImpl implements RendicionService {

        private final RendicionCuentasRepository rendicionRepository;
        private final DetalleComprobanteRepository detalleRepository;
        private final SolicitudComisionRepository solicitudRepository;
        private final TipoGastoRepository tipoGastoRepository;

        @Override
        @Transactional
        public RendicionCuentas obtenerOCrearPorSolicitud(Long solicitudId, String username) {
                return rendicionRepository.findBySolicitudId(solicitudId)
                                .orElseGet(() -> {
                                        SolicitudComision sol = solicitudRepository.findById(solicitudId)
                                                        .orElseThrow(() -> new RuntimeException(
                                                                        "Solicitud no encontrada"));

                                        RendicionCuentas ren = new RendicionCuentas();
                                        ren.setSolicitud(sol);
                                        ren.setFechaCrea(LocalDateTime.now());
                                        ren.setUserCrea(username);
                                        ren.setTotalGastadoBruto(BigDecimal.ZERO);
                                        return rendicionRepository.save(ren);
                                });
        }

        @Override
        @Transactional
        public void agregarComprobante(Long rendicionId, Long tipoGastoId, String ruc, String razonSocial,
                        String serie, String fecha, BigDecimal monto, String username) {

                RendicionCuentas ren = rendicionRepository.findById(rendicionId)
                                .orElseThrow(() -> new RuntimeException("Rendicion no encontrada"));

                TipoGasto tipo = tipoGastoRepository.findById(tipoGastoId)
                                .orElseThrow(() -> new RuntimeException("Tipo de gasto no encontrado"));

                DetalleComprobante det = new DetalleComprobante();
                det.setRendicion(ren);
                det.setTipoGasto(tipo);
                det.setRucProveedor(ruc);
                det.setRazonSocialProveedor(razonSocial);
                det.setSerieNumeroComprobante(serie);
                det.setFechaEmision(LocalDate.parse(fecha));
                det.setMontoTotal(monto);
                det.setEstadoValidacion(EstadoComprobante.PENDIENTE);
                det.setFechaCrea(LocalDateTime.now());
                det.setUserCrea(username);

                detalleRepository.save(det);

                // Actualizar total de la rendición
                BigDecimal nuevoTotal = ren.getTotalGastadoBruto().add(monto);
                ren.setTotalGastadoBruto(nuevoTotal);
                rendicionRepository.save(ren);
        }

        @Override
        @Transactional
        public void eliminarComprobante(Long detalleId) {
                DetalleComprobante det = detalleRepository.findById(detalleId)
                                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));

                RendicionCuentas ren = det.getRendicion();
                ren.setTotalGastadoBruto(ren.getTotalGastadoBruto().subtract(det.getMontoTotal()));

                detalleRepository.delete(det);
                rendicionRepository.save(ren);
        }

        @Override
        public RendicionCuentas obtenerPorSolicitud(Long solicitudId) {
                return rendicionRepository.findBySolicitudId(solicitudId).orElse(null);
        }

        @Override
        @Transactional
        public void finalizarRendicion(Long rendicionId) {
                RendicionCuentas ren = rendicionRepository.findById(rendicionId)
                                .orElseThrow(() -> new RuntimeException("Rendicion no encontrada"));

                SolicitudComision sol = ren.getSolicitud();
                sol.setEstado(EstadoSolicitud.RENDIDO);
                ren.setFechaPresentacion(LocalDateTime.now());

                solicitudRepository.save(sol);
                rendicionRepository.save(ren);
        }

        @Override
        @Transactional
        public void validarComprobante(Long detalleId, String estado, String motivo) {
                DetalleComprobante det = detalleRepository.findById(detalleId)
                                .orElseThrow(() -> new RuntimeException("Comprobante no encontrado"));

                det.setEstadoValidacion(EstadoComprobante.valueOf(estado));
                det.setMotivoRechazo(motivo);

                detalleRepository.save(det);
        }

        @Override
        @Transactional
        public void finalizarValidacion(Long solicitudId) {
                RendicionCuentas ren = rendicionRepository.findBySolicitudId(solicitudId)
                                .orElseThrow(() -> new RuntimeException("Rendición no encontrada"));

                // comprobantes con estado ACEPTADO
                BigDecimal totalAceptado = ren.getDetalles().stream()
                                .filter(d -> d.getEstadoValidacion() == EstadoComprobante.ACEPTADO)
                                .map(DetalleComprobante::getMontoTotal)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                ren.setTotalAceptado(totalAceptado);
                rendicionRepository.save(ren);

                SolicitudComision sol = ren.getSolicitud();
                sol.setEstado(EstadoSolicitud.LIQUIDADO);
                solicitudRepository.save(sol);
        }
}
