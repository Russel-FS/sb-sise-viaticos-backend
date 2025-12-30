package com.viatico.proyect.application.service.impl;

import com.viatico.proyect.application.service.interfaces.LiquidacionService;
import com.viatico.proyect.domain.entity.LiquidacionFinal;
import com.viatico.proyect.domain.entity.RendicionCuentas;
import com.viatico.proyect.domain.entity.SolicitudComision;
import com.viatico.proyect.domain.enums.EstadoLiquidacion;
import com.viatico.proyect.domain.repositories.LiquidacionFinalRepository;
import com.viatico.proyect.domain.repositories.RendicionCuentasRepository;
import com.viatico.proyect.domain.repositories.SolicitudComisionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LiquidacionServiceImpl implements LiquidacionService {

    private final LiquidacionFinalRepository liquidacionRepository;
    private final SolicitudComisionRepository solicitudRepository;
    private final RendicionCuentasRepository rendicionRepository;

    @Override
    @Transactional
    public LiquidacionFinal generarLiquidacion(Long solicitudId) {
        SolicitudComision sol = solicitudRepository.findById(solicitudId)
                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

        RendicionCuentas ren = rendicionRepository.findBySolicitudId(solicitudId)
                .orElseThrow(() -> new RuntimeException("Rendición no encontrada"));

        LiquidacionFinal liq = liquidacionRepository.findBySolicitudId(solicitudId)
                .orElse(new LiquidacionFinal());

        liq.setSolicitud(sol);
        liq.setMontoAsignado(sol.getMontoTotal());
        liq.setMontoRendidoValidado(ren.getTotalAceptado());

        // Cálculo de saldos
        BigDecimal diferencia = sol.getMontoTotal().subtract(ren.getTotalAceptado());

        if (diferencia.compareTo(BigDecimal.ZERO) > 0) {
            // Sobró dinero -> A favor de la Empresa
            liq.setSaldoAfavorEmpresa(diferencia);
            liq.setSaldoAfavorEmpleado(BigDecimal.ZERO);
        } else if (diferencia.compareTo(BigDecimal.ZERO) < 0) {
            // Faltó dinero -> A favor del Empleado reembolso
            liq.setSaldoAfavorEmpleado(diferencia.abs());
            liq.setSaldoAfavorEmpresa(BigDecimal.ZERO);
        } else {
            // Exacto
            liq.setSaldoAfavorEmpresa(BigDecimal.ZERO);
            liq.setSaldoAfavorEmpleado(BigDecimal.ZERO);
        }

        liq.setFechaCierre(LocalDateTime.now());
        liq.setEstadoCierre(EstadoLiquidacion.FINALIZADO);
        return liquidacionRepository.save(liq);
    }

    @Override
    public LiquidacionFinal obtenerPorSolicitud(Long solicitudId) {
        return liquidacionRepository.findBySolicitudId(solicitudId).orElse(null);
    }
}
