package com.viatico.proyect.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Entidad Empleado
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Empleado {
    private Long id;
    private String nombres;
    private String apellidos;
    private String dni;
    private String email;
    private NivelJerarquico nivel = new NivelJerarquico();
    private String cuentaBancaria;
    private String userCrea;
    private LocalDateTime fechaCrea;
}
