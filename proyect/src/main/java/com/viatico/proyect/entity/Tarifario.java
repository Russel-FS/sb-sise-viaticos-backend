package com.viatico.proyect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TARIFARIO_VIATICOS")
public class Tarifario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TARIFA")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_ZONA")
    private ZonaGeografica zonaGeografica;

    @ManyToOne
    @JoinColumn(name = "ID_NIVEL")
    private NivelJerarquico nivelJerarquico;

    @ManyToOne
    @JoinColumn(name = "ID_TIPO_GASTO")
    private TipoGasto tipoGasto;

    @Column(name = "MONTO_LIMITE_DIARIO", precision = 10, scale = 2)
    private BigDecimal montoDiario;

    private String moneda;

    @Column(name = "USER_CREA")
    private String userCrea;

    @Column(name = "FECHA_CREA")
    private LocalDateTime fechaCrea;
}
