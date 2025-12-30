package com.viatico.proyect.application.service.interfaces;

import com.viatico.proyect.domain.entity.LiquidacionFinal;

public interface LiquidacionService {
    LiquidacionFinal generarLiquidacion(Long solicitudId);

    LiquidacionFinal obtenerPorSolicitud(Long solicitudId);
}
