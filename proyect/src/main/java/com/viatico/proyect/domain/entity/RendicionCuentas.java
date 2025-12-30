package com.viatico.proyect.domain.entity;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RendicionCuentas {
    private Long id;
    private SolicitudComision solicitud;
    private LocalDateTime fechaPresentacion;
    private BigDecimal totalGastadoBruto = BigDecimal.ZERO;
    private BigDecimal totalAceptado = BigDecimal.ZERO;
    private String comentariosEmpleado;
    private List<DetalleComprobante> detalles = new ArrayList<>();
    private String userCrea;
    private LocalDateTime fechaCrea;
}
