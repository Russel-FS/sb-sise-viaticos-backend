package com.viatico.proyect.entity;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Tarifario {
    private Long id;
    private ZonaGeografica zonaGeografica;
    private NivelJerarquico nivelJerarquico;
    private TipoGasto tipoGasto;
    private BigDecimal montoDiario;
    private String moneda;
    private String userCrea;
    private LocalDateTime fechaCrea;
    private Integer activo = 1;
}
