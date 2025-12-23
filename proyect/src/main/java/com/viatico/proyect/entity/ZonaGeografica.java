package com.viatico.proyect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ZONAS_GEOGRAFICAS")
public class ZonaGeografica {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ZONA")
    private Long id;

    @Column(name = "NOMBRE_ZONA", nullable = false)
    private String nombre;

    private String descripcion;

    @Column(name = "USER_CREA")
    private String userCrea;

    @Column(name = "FECHA_CREA")
    private LocalDateTime fechaCrea;

    @Column(name = "ACTIVO")
    private Integer activo = 1;
}
