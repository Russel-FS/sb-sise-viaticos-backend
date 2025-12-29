package com.viatico.proyect.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItinerarioViaje {
    private Long id;
    private SolicitudComision solicitud;
    private ZonaGeografica zonaDestino;
    private String ciudadEspecifica;
    private Integer noches;
    private String userCrea;
    private LocalDateTime fechaCrea;
}
