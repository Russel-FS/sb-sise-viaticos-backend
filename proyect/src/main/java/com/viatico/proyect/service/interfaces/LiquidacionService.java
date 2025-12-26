package com.viatico.proyect.service.interfaces;

import com.viatico.proyect.entity.LiquidacionFinal;

public interface LiquidacionService {
    LiquidacionFinal generarLiquidacion(Long solicitudId);

    LiquidacionFinal obtenerPorSolicitud(Long solicitudId);
}
