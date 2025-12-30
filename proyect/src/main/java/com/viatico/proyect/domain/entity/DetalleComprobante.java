package com.viatico.proyect.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

import com.viatico.proyect.domain.enums.EstadoComprobante;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleComprobante {
    private Long id;
    private RendicionCuentas rendicion;
    private TipoGasto tipoGasto;
    private java.time.LocalDateTime fechaEmision;
    private String tipoComprobante;
    private String serieComprobante;
    private String numeroComprobante;
    private String rucEmisor;
    private String razonSocialEmisor;
    private java.math.BigDecimal montoBruto = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal montoIgp = java.math.BigDecimal.ZERO;
    private java.math.BigDecimal montoTotal = java.math.BigDecimal.ZERO;
    private String imagenComprobanteUrl;
    private boolean validado = false;
    private EstadoComprobante estadoValidacion = EstadoComprobante.PENDIENTE;
    private String motivoRechazo;
    private String userCrea;
    private LocalDateTime fechaCrea;
}
