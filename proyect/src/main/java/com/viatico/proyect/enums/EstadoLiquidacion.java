package com.viatico.proyect.enums;

public enum EstadoLiquidacion {
    PENDIENTE, // El cálculo está hecho, esperando acción de pago/devolución
    FINALIZADO, // El proceso se cerró no hay saldos pendientes
    REEMBOLSADO // Específico para cuando se devolvió dinero al empleado
}
