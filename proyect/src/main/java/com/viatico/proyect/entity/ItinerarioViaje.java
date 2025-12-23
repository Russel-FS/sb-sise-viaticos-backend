package com.viatico.proyect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "ITINERARIO_VIAJE")
public class ItinerarioViaje {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_ITINERARIO")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_COMISION")
    private SolicitudComision solicitud;

    @ManyToOne
    @JoinColumn(name = "ID_ZONA_DESTINO")
    private ZonaGeografica zonaDestino;

    @Column(name = "CIUDAD_ESPECIFICA")
    private String ciudadEspecifica;

    @Column(name = "NOCHES")
    private Integer noches;

    @Column(name = "USER_CREA")
    private String userCrea;

    @Column(name = "FECHA_CREA")
    private LocalDateTime fechaCrea;
}
