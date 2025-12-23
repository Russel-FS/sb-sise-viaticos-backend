package com.viatico.proyect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "TIPOS_GASTO")
public class TipoGasto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_TIPO")
    private Long id;

    @Column(name = "NOMBRE_GASTO", nullable = false)
    private String nombre;

    @Column(name = "REQUIERE_FACTURA")
    private Integer requiereFactura; // 0 o 1

    @Column(name = "CUENTA_CONTABLE")
    private String cuentaContable;

    @Column(name = "ES_ASIGNABLE_POR_DIA")
    private Integer esAsignablePorDia; // 0 o 1

    @Column(name = "USER_CREA")
    private String userCrea;

    @Column(name = "FECHA_CREA")
    private LocalDateTime fechaCrea;
}
