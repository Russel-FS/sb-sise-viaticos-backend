package com.viatico.proyect.domain.entity;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import com.viatico.proyect.domain.enums.EstadoSolicitud;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SolicitudComision {
    private Long id;
    private Empleado empleado;
    private String motivoViaje;
    private LocalDate fechaSolicitud;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private EstadoSolicitud estado;
    private List<ItinerarioViaje> itinerarios;
    private BigDecimal montoTotal;
    private String userCrea;
    private LocalDateTime fechaLiquidacion;
    private LocalDateTime fechaCrea;
    private String comentarioRechazo;
}
