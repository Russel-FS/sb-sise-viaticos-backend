package com.viatico.proyect.domain.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.viatico.proyect.domain.enums.EstadoLiquidacion;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LiquidacionFinal {
    private Long id;
    private SolicitudComision solicitud;
    private BigDecimal montoAsignado = BigDecimal.ZERO;
    private BigDecimal montoRendidoValidado = BigDecimal.ZERO;
    private BigDecimal saldoAfavorEmpresa = BigDecimal.ZERO;
    private BigDecimal saldoAfavorEmpleado = BigDecimal.ZERO;
    private LocalDateTime fechaCierre = LocalDateTime.now();
    private EstadoLiquidacion estadoCierre = EstadoLiquidacion.EQUILIBRADA;
    private String mensajeLiquidacion;
    private String userCrea;
    private LocalDateTime fechaCrea = LocalDateTime.now();
    private String userMod;
    private LocalDateTime fechaMod;
}
