package com.viatico.proyect.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "SOLICITUD_COMISION")
public class SolicitudComision {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_COMISION")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_EMPLEADO")
    private Empleado empleado;

    @Column(name = "MOTIVO_VIAJE", nullable = false)
    private String motivoViaje;

    @Column(name = "FECHA_SOLICITUD")
    private LocalDate fechaSolicitud;

    @Column(name = "FECHA_INICIO", nullable = false)
    private LocalDate fechaInicio;

    @Column(name = "FECHA_FIN", nullable = false)
    private LocalDate fechaFin;

    private String estado; // Borrador, Solicitado, Aprobado, etc.

    @OneToMany(mappedBy = "solicitud", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ItinerarioViaje> itinerarios;

    @Column(name = "MONTO_TOTAL", precision = 12, scale = 2)
    private BigDecimal montoTotal;

    @Column(name = "USER_CREA")
    private String userCrea;

    @Column(name = "FECHA_CREA")
    private LocalDateTime fechaCrea;
}
