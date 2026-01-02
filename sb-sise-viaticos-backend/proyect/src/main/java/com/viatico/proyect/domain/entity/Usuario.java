package com.viatico.proyect.domain.entity;

import lombok.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private List<Rol> roles = new ArrayList<>();
    private Integer activo;
    private String userCrea;
    private LocalDateTime fechaCrea;
}
