package com.viatico.proyect.domain.entity;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItinerarioViaje {
    private Long id;
    private SolicitudComision solicitud;
    private ZonaGeografica zonaDestino;
    private String ciudadEspecifica;
    private Integer noches;
    private List<Tarifario> tarifas;
    private String userCrea;
    private LocalDateTime fechaCrea;
}
