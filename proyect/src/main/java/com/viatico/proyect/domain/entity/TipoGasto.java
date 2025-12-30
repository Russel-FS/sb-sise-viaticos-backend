package com.viatico.proyect.domain.entity;

import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TipoGasto {
    private Long id;
    private String nombre;
    private boolean requiereFactura;
    private String cuentaContable;
    private boolean esAsignablePorDia;
    private String userCrea;
    private LocalDateTime fechaCrea;
    private Integer activo = 1;
}
