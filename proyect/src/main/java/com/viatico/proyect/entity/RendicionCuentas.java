package com.viatico.proyect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "RENDICION_CUENTAS")
public class RendicionCuentas {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_RENDICION")
    private Long id;

    @OneToOne
    @JoinColumn(name = "ID_COMISION", unique = true)
    private SolicitudComision solicitud;

    @Column(name = "FECHA_PRESENTACION")
    private LocalDateTime fechaPresentacion;

    @Column(name = "TOTAL_GASTADO_BRUTO")
    private BigDecimal totalGastadoBruto = BigDecimal.ZERO;

    @Column(name = "COMENTARIOS_EMPLEADO")
    private String comentariosEmpleado;

    @OneToMany(mappedBy = "rendicion", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DetalleComprobante> detalles;

    // Auditoría
    @Column(name = "USER_CREA")
    private String userCrea;

    @Column(name = "FECHA_CREA")
    private LocalDateTime fechaCrea;
}
