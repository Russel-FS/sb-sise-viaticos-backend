package com.viatico.proyect.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Rol {
    private Long id;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String userCrea;
    private LocalDateTime fechaCrea;
}
