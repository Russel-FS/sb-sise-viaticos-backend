package com.viatico.proyect.domain.enums;

public enum EstadoLiquidacion {
    EQUILIBRADA, // Montos exactamente iguales, sin saldos pendientes
    DEVOLUCION_EMPLEADO, // Empleado gastó menos, debe devolver a la empresa
    PAGO_EMPLEADO // Empleado gastó más, empresa debe reembolsar
}
