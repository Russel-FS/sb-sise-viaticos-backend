package com.viatico.proyect.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad Usuario
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {
    private Long id;
    private Empleado empleado;
    private String username;
    private String email;
    private String password;
    private Rol rol;
    private Integer activo;
    private String userCrea;
    private LocalDateTime fechaCrea;
}
