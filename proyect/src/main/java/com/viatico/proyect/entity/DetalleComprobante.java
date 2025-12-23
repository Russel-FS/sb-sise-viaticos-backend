package com.viatico.proyect.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "DETALLE_COMPROBANTES")
public class DetalleComprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_DETALLE")
    private Long id;

    @ManyToOne
    @JoinColumn(name = "ID_RENDICION", nullable = false)
    private RendicionCuentas rendicion;

    @ManyToOne
    @JoinColumn(name = "ID_TIPO_GASTO", nullable = false)
    private TipoGasto tipoGasto;

    @Column(name = "RUC_PROVEEDOR", length = 11)
    private String rucProveedor;

    @Column(name = "RAZON_SOCIAL_PROVEEDOR", length = 150)
    private String razonSocialProveedor;

    @Column(name = "SERIE_NUMERO_COMPROBANTE", length = 50)
    private String serieNumeroComprobante;

    @Column(name = "FECHA_EMISION", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "MONTO_TOTAL", nullable = false)
    private BigDecimal montoTotal;

    @Column(name = "FOTO_EVIDENCIA_URL", length = 255)
    private String fotoEvidenciaUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_VALIDACION", length = 20)
    private EstadoComprobante estadoValidacion = EstadoComprobante.PENDIENTE;

    @Column(name = "MOTIVO_RECHAZO", length = 255)
    private String motivoRechazo;

    // Auditoría
    @Column(name = "USER_CREA")
    private String userCrea;

    @Column(name = "FECHA_CREA")
    private LocalDateTime fechaCrea;
}
