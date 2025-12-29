package com.viatico.proyect.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZonaGeografica {
    private Long id;
    private String nombre;
    private String descripcion;
    private String userCrea;
    private LocalDateTime fechaCrea;
    private Integer activo = 1;
}
