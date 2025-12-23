package com.viatico.proyect.service;

import com.viatico.proyect.entity.LiquidacionFinal;

public interface LiquidacionService {
    LiquidacionFinal generarLiquidacion(Long solicitudId);

    LiquidacionFinal obtenerPorSolicitud(Long solicitudId);
}
