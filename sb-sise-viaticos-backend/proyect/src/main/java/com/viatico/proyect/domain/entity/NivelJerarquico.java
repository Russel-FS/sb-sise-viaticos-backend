package com.viatico.proyect.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NivelJerarquico {
    private Long id;
    private String nombre;
    private String descripcion;
    private String userCrea;
    private LocalDateTime fechaCrea;
    private Integer activo = 1;
}
