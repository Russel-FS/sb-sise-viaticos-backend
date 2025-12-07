-- PROYECTO: SISTEMA DE GESTIÓN DE VIÁTICOS
-- EMPRESA: Ingeniería y Servicios del Sur S.A.C.
-- DESCRIPCIÓN: Modelo optimizado para escalabilidad de conceptos de gasto.

-- ZONAS_GEOGRAFICAS
CREATE TABLE zonas_geograficas (
    id_zona INT PRIMARY KEY AUTO_INCREMENT,
    nombre_zona VARCHAR(50) NOT NULL, -- Ej: Zona A (Lima), Zona B (Provincia)
    descripcion VARCHAR(200)
);

-- NIVELES_JERARQUICOS
CREATE TABLE niveles_jerarquicos (
    id_nivel INT PRIMARY KEY AUTO_INCREMENT,
    nombre_nivel VARCHAR(50) NOT NULL, -- Ej: Gerente, Supervisor, Técnico
    descripcion VARCHAR(200)
);

-- TIPOS_GASTO - CONCEPTOS
CREATE TABLE tipos_gasto (
    id_tipo INT PRIMARY KEY AUTO_INCREMENT,
    nombre_gasto VARCHAR(50) NOT NULL, -- Ej: Alimentación, Hospedaje, Movilidad, Lavandería
    requiere_factura BOOLEAN DEFAULT TRUE,
    cuenta_contable VARCHAR(20), -- Ej: 63.1.1
    es_asignable_por_dia BOOLEAN DEFAULT TRUE -- Indica si este concepto entra en el cálculo diario automático
);

-- TARIFARIO_VIATICOS - ESCALA DE VIÁTICOS
-- CAMBIO IMPORTANTE: Ahora es vertical. Define el tope por cada concepto específico.
-- Ejemplo Fila 1: Zona A | Gerente | Alimentación | 100.00
-- Ejemplo Fila 2: Zona A | Gerente | Hospedaje    | 300.00
CREATE TABLE tarifario_viaticos (
    id_tarifa INT PRIMARY KEY AUTO_INCREMENT,
    id_zona INT NOT NULL,
    id_nivel INT NOT NULL,
    id_tipo_gasto INT NOT NULL, -- Relación con el concepto específico
    monto_limite_diario DECIMAL(10, 2) NOT NULL, -- El tope asignado
    moneda CHAR(3) DEFAULT 'PEN',
    FOREIGN KEY (id_zona) REFERENCES zonas_geograficas (id_zona),
    FOREIGN KEY (id_nivel) REFERENCES niveles_jerarquicos (id_nivel),
    FOREIGN KEY (id_tipo_gasto) REFERENCES tipos_gasto (id_tipo)
);

-- EMPLEADOS
CREATE TABLE empleados (
    id_empleado INT PRIMARY KEY AUTO_INCREMENT,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    dni CHAR(8) NOT NULL UNIQUE,
    email VARCHAR(100) UNIQUE,
    id_nivel INT NOT NULL,
    cuenta_bancaria VARCHAR(20),
    FOREIGN KEY (id_nivel) REFERENCES niveles_jerarquicos (id_nivel)
);

-- SOLICITUD_COMISION
CREATE TABLE solicitud_comision (
    id_comision INT PRIMARY KEY AUTO_INCREMENT,
    id_empleado INT NOT NULL,
    motivo_viaje VARCHAR(255) NOT NULL,
    fecha_solicitud DATE DEFAULT(CURRENT_DATE),
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado ENUM(
        'Borrador',
        'Solicitado',
        'Aprobado',
        'Rechazado',
        'Rendido',
        'Liquidado'
    ) DEFAULT 'Borrador',
    FOREIGN KEY (id_empleado) REFERENCES empleados (id_empleado)
);

-- ITINERARIO_VIAJE
CREATE TABLE itinerario_viaje (
    id_itinerario INT PRIMARY KEY AUTO_INCREMENT,
    id_comision INT NOT NULL,
    id_zona_destino INT NOT NULL,
    ciudad_especifica VARCHAR(100),
    dias_pernocte INT NOT NULL,
    FOREIGN KEY (id_comision) REFERENCES solicitud_comision (id_comision),
    FOREIGN KEY (id_zona_destino) REFERENCES zonas_geograficas (id_zona)
);

-- ASIGNACION_DINERO
CREATE TABLE asignacion_dinero (
    id_asignacion INT PRIMARY KEY AUTO_INCREMENT,
    id_comision INT NOT NULL UNIQUE,
    monto_calculado_sistema DECIMAL(10, 2) NOT NULL,
    monto_real_otorgado DECIMAL(10, 2) NOT NULL,
    fecha_deposito DATE,
    numero_operacion_bancaria VARCHAR(50),
    estado_transferencia ENUM('Pendiente', 'Completado') DEFAULT 'Pendiente',
    FOREIGN KEY (id_comision) REFERENCES solicitud_comision (id_comision)
);

-- RENDICION_CUENTAS
CREATE TABLE rendicion_cuentas (
    id_rendicion INT PRIMARY KEY AUTO_INCREMENT,
    id_comision INT NOT NULL UNIQUE,
    fecha_presentacion DATETIME DEFAULT CURRENT_TIMESTAMP,
    total_gastado_bruto DECIMAL(10, 2) DEFAULT 0.00,
    comentarios_empleado TEXT,
    FOREIGN KEY (id_comision) REFERENCES solicitud_comision (id_comision)
);

-- DETALLE_COMPROBANTES
CREATE TABLE detalle_comprobantes (
    id_detalle INT PRIMARY KEY AUTO_INCREMENT,
    id_rendicion INT NOT NULL,
    id_tipo_gasto INT NOT NULL, -- Aquí indicas qué concepto fue (Alimentación, etc.)
    ruc_proveedor CHAR(11),
    razon_social_proveedor VARCHAR(150),
    serie_numero_comprobante VARCHAR(50),
    fecha_emision DATE NOT NULL,
    monto_total DECIMAL(10, 2) NOT NULL,
    foto_evidencia_url VARCHAR(255),
    estado_validacion ENUM(
        'Pendiente',
        'Aceptado',
        'Rechazado'
    ) DEFAULT 'Pendiente',
    motivo_rechazo VARCHAR(255),
    FOREIGN KEY (id_rendicion) REFERENCES rendicion_cuentas (id_rendicion),
    FOREIGN KEY (id_tipo_gasto) REFERENCES tipos_gasto (id_tipo)
);

-- LIQUIDACION_FINAL
CREATE TABLE liquidacion_final (
    id_liquidacion INT PRIMARY KEY AUTO_INCREMENT,
    id_comision INT NOT NULL UNIQUE,
    monto_asignado DECIMAL(10, 2) NOT NULL,
    monto_rendido_validado DECIMAL(10, 2) NOT NULL,
    saldo_a_favor_empresa DECIMAL(10, 2) DEFAULT 0.00,
    saldo_a_favor_empleado DECIMAL(10, 2) DEFAULT 0.00,
    fecha_cierre DATE,
    estado_cierre ENUM(
        'Abierto',
        'Cerrado',
        'Reembolsado'
    ) DEFAULT 'Abierto',
    FOREIGN KEY (id_comision) REFERENCES solicitud_comision (id_comision)
);