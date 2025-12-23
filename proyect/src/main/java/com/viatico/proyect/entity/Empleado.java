package com.viatico.proyect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "EMPLEADOS")
public class Empleado {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_EMPLEADO")
    private Long id;

    private String nombres;
    private String apellidos;
    private String dni;
    private String email;

    @ManyToOne
    @JoinColumn(name = "ID_NIVEL")
    private NivelJerarquico nivel;

    @Column(name = "CUENTA_BANCARIA")
    private String cuentaBancaria;

    @Column(name = "USER_CREA")
    private String userCrea;

    @Column(name = "FECHA_CREA")
    private LocalDateTime fechaCrea;
}
