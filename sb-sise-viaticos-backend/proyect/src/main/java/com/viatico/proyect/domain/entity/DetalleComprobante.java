package com.viatico.proyect.domain.entity;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.viatico.proyect.domain.enums.EstadoComprobante;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleComprobante {
    private Long id;
    private RendicionCuentas rendicion;
    private TipoGasto tipoGasto;
    private LocalDateTime fechaEmision;
    private String tipoComprobante;
    private String serieComprobante;
    private String numeroComprobante;
    private String rucEmisor;
    private String razonSocialEmisor;
    private BigDecimal montoBruto = BigDecimal.ZERO;
    private BigDecimal montoIgv = BigDecimal.ZERO;
    private BigDecimal montoTotal = BigDecimal.ZERO;
    private String archivoComprobanteUrl;
    private EstadoComprobante estadoValidacion = EstadoComprobante.PENDIENTE;
    private String motivoRechazo;
    private String userCrea;
    private LocalDateTime fechaCrea;
}
