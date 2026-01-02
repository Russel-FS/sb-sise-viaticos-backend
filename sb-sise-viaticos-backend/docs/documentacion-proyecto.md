# Documentación del Proyecto: Sistema de Gestión de Viáticos y Liquidación

## 1. Objetivo del Proyecto

Definir un proceso de negocio crítico para la empresa **"Ingeniería y Servicios del Sur S.A.C."**, identificar todas las entidades necesarias e implementar un modelado, diseño y gestión de base de datos en Oracle con integración a un Sistema Web.

El proyecto busca aplicar conocimientos avanzados en:

- Creación y administración de bases de datos.
- Manejo de lógica de negocio mediante **PL/SQL** (Triggers y Procedimientos Almacenados para el cálculo de tarifas y liquidaciones).
- Asegurar la correcta implementación de mecanismos de seguridad y auditoría de la información financiera.

---

## 2. Definición del Proceso de Negocio

### 2.1. Selección del Proceso

Se ha seleccionado el proceso de **"Gestión de Comisiones de Servicio, Asignación de Viáticos y Liquidación de Gastos"**. Este proceso se aleja de los clásicos ejemplos de ventas o inventarios, enfocándose en un flujo financiero interno de control de gastos y tesorería.

### 2.2. Contexto Empresarial (Caso de Negocio)

- **Empresa:** Ingeniería y Servicios del Sur S.A.C.
- **Rubro:** Mantenimiento de infraestructura y telecomunicaciones.
- **Problemática:**
  - La empresa cuenta con 80 técnicos que viajan constantemente a provincias.
  - Actualmente, la asignación de dinero es subjetiva (sin tarifario fijo), lo que genera un **exceso de gastos del 15% anual**.
  - La rendición de cuentas es manual, provocando pérdida de crédito fiscal por facturas mal emitidas y demoras de hasta 30 días en el cierre contable.

### 2.3. Descripción del Flujo del Proceso (To-Be)

El nuevo sistema automatizará el ciclo completo de vida del viático:

1.  **Solicitud y Cálculo Automático:** El empleado registra su "Comisión de Servicio" indicando fechas y destinos. El sistema, mediante procedimientos almacenados, consulta la Matriz de Tarifarios (Zona Geográfica vs. Nivel Jerárquico) y calcula automáticamente el monto a otorgar, eliminando la subjetividad.
2.  **Aprobación y Desembolso:** El Jefe de Área revisa la solicitud digital. Al aprobarse, Tesorería registra la transferencia bancaria vinculada a esa comisión.
3.  **Rendición y Validación "High-End":** Durante el viaje, el empleado carga evidencias de sus comprobantes vía web/móvil. El sistema ofrece una interfaz prolija de inspección para Tesorería, con visor de PDF integrado y lightbox de imágenes, permitiendo una validación precisa contra reglas de negocio (ej. RUC válido, topes por zona).
4.  **Liquidación y Cierre Inteligente:** Al finalizar, el sistema cruza lo Asignado vs. lo Rendido Validado mediante cálculos en PL/SQL. Genera un dashboard financiero minimalista indicando el balance final: "Saldo a Devolver" o "Reembolso".

---

## 3. Diagrama de Caso de Uso del Sistema

### 3.1. Actores del Sistema

- **Solicitante (Empleado):** Técnico o administrativo que realiza el viaje.
- **Aprobador (Jefe de Área):** Responsable de autorizar el viaje y el presupuesto.
- **Analista Contable:** Encargado de validar la legalidad de los comprobantes (SUNAT).
- **Tesorería:** Encargado de registrar los depósitos y confirmar devoluciones.
- **Administrador del Sistema:** Gestiona los usuarios y la configuración de tarifas.

### 3.2. Casos de Uso Principales

#### Módulo de Planificación

- **CU-01 Registrar Solicitud de Comisión:** El Solicitante ingresa fechas y destinos.
- **CU-02 Calcular Viático Estimado:** El Sistema calcula el monto según la zona y nivel (Automático).
- **CU-03 Aprobar Solicitud:** El Aprobador valida la pertinencia del viaje.

#### Módulo de Tesorería

- **CU-04 Registrar Asignación de Dinero:** Tesorería ingresa el nro. de operación del depósito inicial.

#### Módulo de Ejecución y Rendición

- **CU-05 Registrar Comprobante de Gasto:** El Solicitante sube foto y datos de la factura.
- **CU-06 Validar Comprobante:** El Analista Contable acepta o rechaza un gasto específico (ej. "Gasto no permitido").

#### Módulo de Cierre

- **CU-07 Generar Liquidación Final:** El Sistema compara Asignado vs. Validado y determina saldos.
- **CU-08 Cerrar Comisión:** Tesorería confirma que se saldó la deuda (devolución o reembolso).

#### Módulo de Configuración

- **CU-09 Gestionar Tarifario de Viáticos:** El Administrador actualiza los montos diarios por zona.

_(Espacio reservado para diagrama visual UML)_

---

## 4. Lista de Entidades (Modelo de Datos)

A continuación, se listan las entidades identificadas para soportar la funcionalidad del sistema, clasificadas por su rol en el negocio.

### A. Entidades de Configuración y Reglas (Maestras)

Estas entidades almacenan la lógica paramétrica del negocio (Tarifarios).

- **`ZONAS_GEOGRAFICAS`**: Catálogo de agrupaciones de destinos (ej. Zona Norte, Zona Sur, Lima) para diferenciar el costo de vida.
- **`NIVELES_JERARQUICOS`**: Clasificación de los empleados (ej. Gerente, Técnico) para asignar diferentes calidades de viático.
- **`TIPOS_GASTO`**: Catálogo de conceptos permitidos (ej. Alimentación, Hospedaje, Movilidad), incluyendo reglas como cuenta contable asociada.
- **`TARIFARIO_VIATICOS`**: Entidad asociativa (Matriz) que cruza Zona, Nivel y Tipo de Gasto para definir el monto límite diario monetario.

### B. Entidades de Actores

- **`EMPLEADOS`**: Información del personal que participa en el flujo (Solicitantes y Aprobadores), vinculados a un Nivel Jerárquico.

### C. Entidades Transaccionales (Flujo Principal)

Estas entidades registran la operación diaria.

- **`SOLICITUD_COMISION`**: La entidad cabecera que representa el evento del viaje. Contiene el estado del flujo (Borrador, Aprobado, Liquidado).
- **`ITINERARIO_VIAJE`**: Detalle de la solicitud que especifica los tramos del viaje. Es fundamental para calcular el viático, ya que vincula la Solicitud con la Zona Geográfica y la Duración.
- **`ASIGNACION_DINERO`**: Registro financiero del dinero entregado anticipadamente al empleado (Salida de Caja).

### D. Entidades de Rendición y Control

Estas entidades soportan la justificación de gastos y el cierre contable.

- **`RENDICION_CUENTAS`**: Agrupador de los gastos presentados por el empleado al retornar del viaje.
- **`DETALLE_COMPROBANTES`**: Registro individual de cada factura o boleta física. Contiene la evidencia (foto/PDF), datos tributarios (RUC) y el estado de validación individual.
- **`LIQUIDACION_FINAL`**: Entidad de cierre que almacena el resultado del balance financiero (Asignado menos Validado), determinando la deuda final entre la empresa y el empleado.

---

## 5. Implementación Tecnológica y UI/UX

El sistema no solo cumple con la lógica de negocio, sino que ha sido diseñado con un enfoque de **Experiencia de Usuario de Grado Empresarial**, inspirado en los ecosistemas modernos (estética Apple).

### 5.1. Stack Tecnológico

- **Base de Datos:** Oracle 21c (PL/SQL para lógica core).
- **Backend:** Spring Boot 3.x (Java 17).
- **Frontend:** Thymeleaf + Tailwind CSS.
- **Iconografía:** Lucide Icons (Proyectos modernos).
- **Tipografía:** SF Pro Display / Text (Apple System).

### 5.2. Características de Diseño "Premium"

- **Apple-like Aesthetic:** Interfaz minimalista, el uso de amplios espacios en blanco, bordes redondeados (28px - 32px) y sombras suaves ("Glassmorphism").
- **Full Dark Mode Support:** Sistema de temas dinámico que detecta la preferencia del sistema y permite el cambio manual, optimizando la legibilidad y reduciendo la fatiga visual.
- **Dashboard Typography-First:** En lugar de saturar con gráficos pesados, el sistema prioriza la tipografía tabular y jerarquías claras para los montos financieros.
- **Visor Multimedia Integrado:** Herramientas de auditoría que permiten previsualizar PDF y fotos de comprobantes sin salir de la plataforma, acelerando el proceso de validación contable.
- **Componentes Reactivos:** Modales centrados, barras de acción "sticky" y transiciones de estado suaves para una navegación fluida.
