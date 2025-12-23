package com.viatico.proyect.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "LIQUIDACION_FINAL")
@Getter
@Setter
public class LiquidacionFinal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID_LIQUIDACION")
    private Long id;

    @OneToOne
    @JoinColumn(name = "ID_COMISION", unique = true, nullable = false)
    private SolicitudComision solicitud;

    @Column(name = "MONTO_ASIGNADO", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoAsignado = BigDecimal.ZERO;

    @Column(name = "MONTO_RENDIDO_VALIDADO", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoRendidoValidado = BigDecimal.ZERO;

    @Column(name = "SALDO_A_FAVOR_EMPRESA", precision = 10, scale = 2)
    private BigDecimal saldoAfavorEmpresa = BigDecimal.ZERO;

    @Column(name = "SALDO_A_FAVOR_EMPLEADO", precision = 10, scale = 2)
    private BigDecimal saldoAfavorEmpleado = BigDecimal.ZERO;

    @Column(name = "FECHA_CIERRE")
    private LocalDateTime fechaCierre = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "ESTADO_CIERRE", length = 20)
    private EstadoLiquidacion estadoCierre = EstadoLiquidacion.PENDIENTE;

    @Column(name = "USER_CREA")
    private String userCrea;

    @Column(name = "FECHA_CREA")
    private LocalDateTime fechaCrea = LocalDateTime.now();

    @Column(name = "USER_MOD")
    private String userMod;

    @Column(name = "FECHA_MOD")
    private LocalDateTime fechaMod;
}
