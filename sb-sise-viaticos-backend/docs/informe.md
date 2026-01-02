# DISEÑO E IMPLEMENTACIÓN DE UNA BASE DE DATOS EN ORACLE CON INTEGRACIÓN A UN SISTEMA WEB

## Sistema de Gestión de Viáticos y Liquidación de Gastos

**Empresa:** Ingeniería y Servicios del Sur S.A.C.  
**Motor de BD:** Oracle Database 12c+  
**Framework:** Spring Boot 4.0.0 con Java 17

---

## ÍNDICE

1. [INTRODUCCIÓN](#1-introducción)

   - 1.1 [Contexto](#11-contexto)
   - 1.2 [Problemática](#12-problemática)
   - 1.3 [Justificación](#13-justificación)
   - 1.4 [Selección del Proceso de Negocio](#14-selección-del-proceso-de-negocio)

2. [MARCO TEÓRICO](#2-marco-teórico)

   - 2.1 [Sistemas de Viáticos](#21-sistemas-de-viáticos)
   - 2.2 [Bases de Datos Oracle y PL/SQL](#22-bases-de-datos-oracle-y-plsql)
   - 2.3 [Arquitectura de Software](#23-arquitectura-de-software)

3. [OBJETIVOS Y ALCANCE](#3-objetivos-y-alcance)

   - 3.1 [Objetivo General](#31-objetivo-general)
   - 3.2 [Objetivos Específicos](#32-objetivos-específicos)
   - 3.3 [Alcance del Proyecto](#33-alcance-del-proyecto)

4. [ANÁLISIS DEL SISTEMA](#4-análisis-del-sistema)

   - 4.1 [Definición del Proceso de Negocio](#41-definición-del-proceso-de-negocio)
   - 4.2 [Actores del Sistema](#42-actores-del-sistema)
   - 4.3 [Casos de Uso del Sistema](#43-casos-de-uso-del-sistema)
   - 4.4 [Requisitos del Sistema](#44-requisitos-del-sistema)

5. [DISEÑO DEL SISTEMA](#5-diseño-del-sistema)

   - 5.1 [Arquitectura del Sistema](#51-arquitectura-del-sistema)
   - 5.2 [Modelo de Datos](#52-modelo-de-datos)
   - 5.3 [Diagrama Entidad-Relación](#53-diagrama-entidad-relación)

6. [TECNOLOGÍAS UTILIZADAS](#6-tecnologías-utilizadas)

   - 6.1 [Base de Datos](#61-base-de-datos)
   - 6.2 [Backend](#62-backend)
   - 6.3 [Frontend](#63-frontend)
   - 6.4 [Herramientas de Desarrollo](#64-herramientas-de-desarrollo)

7. [IMPLEMENTACIÓN](#7-implementación)

   - 7.1 [Modelo Físico de Base de Datos](#71-modelo-físico-de-base-de-datos)
   - 7.2 [Paquetes PL/SQL](#72-paquetes-plsql)
   - 7.3 [Triggers y Objetos](#73-triggers-y-objetos)
   - 7.4 [Integración con Sistema Web](#74-integración-con-sistema-web)

8. [PRUEBAS Y VALIDACIÓN](#8-pruebas-y-validación)

   - 8.1 [Capturas del Código en Funcionamiento](#81-capturas-del-código-en-funcionamiento)

9. [CONCLUSIONES Y RECOMENDACIONES](#9-conclusiones-y-recomendaciones)

   - 9.1 [Conclusiones](#91-conclusiones)
   - 9.2 [Recomendaciones](#92-recomendaciones)

10. [REFERENCIAS BIBLIOGRÁFICAS](#10-referencias-bibliográficas)

---

## 1. INTRODUCCIÓN

### 1.1 Contexto

En el contexto actual de las organizaciones dedicadas a servicios técnicos especializados, la correcta gestión de los recursos financieros asociados a comisiones de servicio constituye un factor crítico para la sostenibilidad económica y el control interno. La empresa Ingeniería y Servicios del Sur S.A.C., dedicada al mantenimiento de infraestructura y telecomunicaciones, cuenta con 80 técnicos que viajan constantemente a provincias para realizar trabajos de campo.

### 1.2 Problemática

La empresa enfrenta serias limitaciones en la administración de viáticos y liquidación de gastos debido al uso de procesos manuales, criterios subjetivos y ausencia de controles automatizados. Estas deficiencias han generado:

- **Sobrecostos anuales** aproximados del **15%** debido a la asignación discrecional de viáticos sin un tarifario estandarizado.
- **Pérdida de crédito fiscal** por errores en la validación de comprobantes de pago y facturas mal emitidas.
- **Retrasos significativos** en el cierre contable (hasta 30 días), impactando directamente en la eficiencia operativa y financiera de la organización.
- **Falta de trazabilidad** en el flujo de aprobación y liquidación de gastos.

### 1.3 Justificación

Frente a esta problemática, el presente proyecto propone el diseño e implementación de una base de datos relacional en Oracle Database, normalizada hasta la **Tercera Forma Normal (3FN)**, integrada a un sistema web, donde toda la lógica de negocio del proceso de viáticos se centraliza en **PL/SQL** mediante procedimientos almacenados, paquetes, triggers y colecciones. Esta arquitectura garantiza integridad, trazabilidad, escalabilidad y un alto nivel de control sobre el ciclo de vida de las comisiones de servicio.

### 1.4 Selección del Proceso de Negocio

La elección del proceso de **gestión de viáticos y liquidación de gastos** como objeto de estudio para este proyecto se fundamenta en varios criterios técnicos y académicos que lo diferencian significativamente de procesos empresariales comunes como ventas, inventarios o matrículas:

#### 1.4.1 Complejidad del Flujo de Negocio

A diferencia de procesos lineales tradicionales, la gestión de viáticos presenta un **flujo multietapa complejo** que involucra:

- **Múltiples actores** con roles diferenciados (solicitante, aprobador, tesorería, contabilidad, administrador)
- **Cálculos dinámicos** basados en matrices de variables (zona geográfica × nivel jerárquico × tipo de gasto)
- **Validaciones en múltiples niveles**: financieras, contables, fiscales y de negocio
- **Ciclo completo cerrado**: solicitud → aprobación → asignación → ejecución → rendición → validación → liquidación → cierre

Este nivel de complejidad permite demostrar capacidades avanzadas de modelamiento y lógica de negocio que procesos más simples no ofrecen.

#### 1.4.2 Relevancia Empresarial Real

El proceso seleccionado responde a una **necesidad empresarial concreta** identificada en Ingeniería y Servicios del Sur S.A.C., donde:

- 80 técnicos realizan viajes mensuales a provincias
- Los sobrecostos actuales ascienden al 15% anual
- El cierre contable se retrasa hasta 30 días
- Existe pérdida de crédito fiscal por errores en comprobantes

Esta problemática real trasciende el ámbito académico y demuestra la aplicabilidad práctica del proyecto, diferenciándose de casos de estudio genéricos o ficticios.

#### 1.4.3 Oportunidad para Lógica Avanzada en PL/SQL

El proceso de viáticos exige implementar **características avanzadas de Oracle Database** que cumplen con todos los requerimientos académicos:

**Objetos Relacionales:**

- Tipo `T_LIQUIDACION` con 9 métodos (constructor, cálculos de saldos, validaciones, generación de mensajes)
- Encapsulación de lógica financiera compleja con Programación Orientada a Objetos en BD

**Colecciones:**

- `T_ITINERARIO_TAB` para manejar múltiples tramos de viaje en una sola transacción
- `T_ESTIMACION_TAB` para cálculos de viáticos sin persistir datos
- Permite operaciones bulk y reduce llamadas a la base de datos

**Triggers en Entidades Core:**

- Auditoría automática de cambios de estado en `SOLICITUD_COMISION`
- Limpieza automática de tokens expirados
- Recalculo de totales en cascada

**Cursores REF:**

- Retorno de conjuntos de datos dinámicos desde procedimientos
- Consultas parametrizadas con filtros opcionales

#### 1.4.4 Desafíos de Normalización y Diseño

El tarifario de viáticos presenta un **desafío interesante de normalización**:

- Tabla asociativa `TARIFARIO_VIATICOS` que cruza tres dimensiones (zona × nivel × tipo)
- Permite parametrización total sin hardcodear reglas de negocio
- Facilita actualizaciones de tarifas sin afectar cálculos históricos
- Elimina redundancias al centralizar la configuración de montos

Esto contrasta con procesos como "ventas" donde las reglas suelen ser más estáticas y predecibles.

#### 1.4.5 Aspectos Fiscales y de Cumplimiento

La gestión de viáticos involucra **validaciones fiscales complejas**:

- Cálculo automático de IGV (18% según normativa peruana)
- Verificación de datos tributarios (RUC, razón social)
- Clasificación de tipos de comprobante (factura, boleta, ticket)
- Relación con crédito fiscal y cierre contable

Estas particularidades requieren lógica de negocio robusta que va más allá del CRUD básico de sistemas tradicionales.

#### 1.4.6 Diferenciación con Procesos Comunes

| Aspecto          | Procesos Comunes (Ventas, Inventario) | Gestión de Viáticos                    |
| ---------------- | ------------------------------------- | -------------------------------------- |
| **Flujo**        | Lineal (orden → pago → entrega)       | Multietapa con aprobaciones            |
| **Cálculos**     | Simples (precio × cantidad)           | Complejos (matriz 3D, días, conceptos) |
| **Validaciones** | Stock, precios                        | Fiscales, contables, presupuestales    |
| **Actores**      | Cliente, vendedor                     | 5+ roles con permisos diferenciados    |
| **Cierre**       | Pago = cierre                         | Liquidación financiera bidireccional   |
| **Auditoría**    | Registro de venta                     | Trazabilidad completa de estados       |

En resumen, la gestión de viáticos fue seleccionada por cumplir con el requisito académico de **"proceso de negocio no común"**, al tiempo que ofrece la complejidad necesaria para demostrar capacidades avanzadas de modelamiento de datos, programación PL/SQL y arquitectura de software empresarial.

---

## 2. MARCO TEÓRICO

### 2.1 Sistemas de Viáticos

Según NegociosLatam [1], un **sistema de viáticos** es el conjunto de políticas, procesos y herramientas tecnológicas que permite a las empresas gestionar de manera eficiente los gastos relacionados con los viajes de negocios de sus empleados. Estos sistemas permiten:

- Establecer tarifas diferenciadas según nivel jerárquico y zona geográfica.
- Controlar los gastos mediante la validación de comprobantes.
- Automatizar el cálculo de montos a asignar.
- Generar reportes financieros y contables.

Por su parte, COFIDE [2] define las **políticas de viáticos** como "el conjunto de normas y lineamientos que establecen los montos, conceptos, procedimientos y restricciones para la asignación de recursos económicos destinados a cubrir gastos de traslado, alimentación, hospedaje y otros relacionados con comisiones de servicio".

La correcta implementación de un sistema de viáticos permite a las organizaciones:

1. **Estandarizar** los procesos de solicitud, aprobación y liquidación.
2. **Reducir sobrecostos** derivados de asignaciones discrecionales.
3. **Garantizar transparencia** mediante auditoría y trazabilidad.
4. **Agilizar el cierre contable** al validar comprobantes en tiempo real.

### 2.2 Bases de Datos Oracle y PL/SQL

**Oracle Database** es un sistema de gestión de bases de datos relacionales (RDBMS) desarrollado por Oracle Corporation, ampliamente utilizado en entornos empresariales por su robustez, escalabilidad y capacidades avanzadas de procesamiento transaccional [3].

**PL/SQL** (Procedural Language/Structured Query Language) es una extensión procedural de SQL diseñada específicamente para Oracle Database. Permite encapsular lógica de negocio compleja mediante:

- **Procedimientos Almacenados:** Bloques de código reutilizables que ejecutan operaciones específicas.
- **Paquetes:** Agrupaciones lógicas de procedimientos, funciones y tipos relacionados.
- **Triggers:** Disparadores automáticos que se ejecutan ante eventos específicos (INSERT, UPDATE, DELETE).
- **Tipos de Objetos:** Estructuras de datos complejas con métodos asociados (Programación Orientada a Objetos en BD).
- **Colecciones:** Arreglos y tablas anidadas para manejo de conjuntos de datos.

La **Tercera Forma Normal (3FN)** es un nivel de normalización de bases de datos que elimina dependencias transitivas, garantizando que cada atributo no clave dependa únicamente de la clave primaria [3]. Esto reduce redundancias, mejora la integridad de datos y facilita el mantenimiento.

### 2.3 Arquitectura de Software

El proyecto implementa una **arquitectura en capas** inspirada en los principios de **Clean Architecture** propuestos por Robert C. Martin [4], donde la lógica de negocio se separa de la infraestructura y las interfaces de usuario.

**Clean Architecture** se basa en la **inversión de dependencias**, donde las capas externas dependen de las internas, pero no viceversa. Esto permite:

- **Testabilidad:** Cada capa puede probarse de forma independiente.
- **Mantenibilidad:** Cambios en una capa no afectan a las demás.
- **Escalabilidad:** Facilita la incorporación de nuevas funcionalidades.

En el contexto de este proyecto, se implementan tres capas principales:

1. **Capa de Dominio:** Contiene las entidades de negocio, enumeraciones y repositorios de acceso a datos.
2. **Capa de Aplicación:** Implementa los servicios que orquestan la lógica de negocio, delegando cálculos críticos a PL/SQL.
3. **Capa de Infraestructura:** Gestiona controladores web, persistencia y configuración del framework.

El **patrón Repository** [5] se utiliza para abstraer el acceso a datos, permitiendo que la lógica de negocio sea independiente del mecanismo de persistencia.

---

## 3. OBJETIVOS Y ALCANCE

### 3.1 Objetivo General

Diseñar e implementar una base de datos en Oracle Database 12c o superior, integrada a un sistema web, que automatice y controle el proceso de gestión de viáticos y liquidación de gastos de la empresa Ingeniería y Servicios del Sur S.A.C., centralizando la lógica de negocio en PL/SQL y asegurando integridad, eficiencia operativa y control financiero.

### 3.2 Objetivos Específicos

1. Diseñar un modelo de datos relacional en Oracle normalizado hasta la **Tercera Forma Normal (3FN)** que represente de manera íntegra el proceso de viáticos y liquidación de gastos.

2. Implementar procedimientos almacenados y **paquetes PL/SQL** que encapsulen la totalidad de la lógica de negocio del sistema.

3. Desarrollar el **cálculo automático de viáticos** considerando zona geográfica, nivel jerárquico y tipo de gasto mediante estructuras avanzadas de PL/SQL como **colecciones** y **cursores**.

4. Garantizar la **integridad y consistencia** de la información mediante restricciones, triggers y validaciones implementadas en la base de datos.

5. Implementar mecanismos de **auditoría** que registren usuario, fecha de creación y modificación en todas las entidades del modelo físico.

6. Optimizar el proceso de **rendición y liquidación de gastos**, reduciendo errores contables y tiempos de cierre financiero.

### 3.3 Alcance del Proyecto

#### Alcance Funcional

El proyecto comprende el análisis, diseño e implementación de una base de datos Oracle que soporte integralmente el proceso de gestión de viáticos y liquidación de gastos, desde la solicitud de comisión de servicio hasta su cierre financiero. El sistema permitirá:

- Registrar solicitudes de comisión de servicio por parte de los empleados.
- Calcular automáticamente los viáticos en función de reglas parametrizables (zona × nivel jerárquico × tipo de gasto).
- Gestionar flujos de aprobación por jefe de área.
- Registrar la asignación de dinero realizada por tesorería.
- Permitir la rendición de gastos con validaciones contables y fiscales.
- Ejecutar la liquidación final de la comisión y su cierre financiero.
- Mantener trazabilidad y auditoría completa de todas las operaciones.

#### Fuera de Alcance

El proyecto **NO** incluye:

- Desarrollo completo del frontend web (solo integración con servicios de BD).
- Integración con sistemas bancarios externos para depósitos automáticos.
- Gestión de planillas, nóminas u otros procesos financieros no relacionados directamente con viáticos.
- Emisión electrónica de comprobantes de pago según normativa SUNAT.

---

## 4. ANÁLISIS DEL SISTEMA

### 4.1 Definición del Proceso de Negocio

El proceso de gestión de viáticos se compone de las siguientes etapas:

1. **Solicitud:** El empleado registra su comisión de servicio indicando fechas, destinos y ciudades del itinerario. El sistema calcula automáticamente el monto estimado de viáticos consultando el tarifario configurado (zona geográfica × nivel jerárquico × tipo de gasto).

2. **Aprobación:** El jefe de área revisa la solicitud y procede a aprobarla o rechazarla. En caso de rechazo, puede indicar un comentario justificativo.

3. **Asignación de Dinero:** Tesorería registra el depósito bancario realizado al empleado, indicando el monto real otorgado y el número de operación bancaria.

4. **Ejecución del Viaje:** El empleado viaja y conserva los comprobantes de gastos (facturas, boletas, tickets).

5. **Rendición de Cuentas:** Al retornar, el empleado carga los comprobantes escaneados en el sistema, registrando datos tributarios (RUC, razón social, tipo de comprobante, monto).

6. **Validación Contable:** El analista contable revisa cada comprobante y lo marca como ACEPTADO o RECHAZADO según criterios fiscales. El sistema recalcula automáticamente el total aceptado.

7. **Liquidación Final:** El sistema compara el monto asignado contra el monto rendido validado. Mediante un **objeto PL/SQL** con métodos de cálculo, determina si existe un saldo a favor de la empresa (devolución del empleado) o a favor del empleado (reembolso).

8. **Cierre:** Tesorería confirma el cierre financiero de la comisión.

### 4.2 Actores del Sistema

Los actores que interactúan con el sistema son:

- **Solicitante (Empleado):** Técnico o administrativo que realiza el viaje y solicita la comisión.
- **Aprobador (Jefe de Área):** Responsable de autorizar el viaje y el presupuesto asignado.
- **Analista Contable:** Encargado de validar la legalidad y conformidad de los comprobantes según normativa fiscal.
- **Tesorería:** Responsable de registrar los depósitos y confirmar devoluciones/reembolsos.
- **Administrador del Sistema:** Gestiona usuarios, roles y configuración de tarifas (zonas, niveles, tipos de gasto).

### 4.3 Casos de Uso del Sistema

Los casos de uso principales implementados son:

| ID    | Caso de Uso                     | Actor             | Descripción                                                |
| ----- | ------------------------------- | ----------------- | ---------------------------------------------------------- |
| CU-01 | Registrar Solicitud de Comisión | Solicitante       | El empleado registra fechas, motivo e itinerario del viaje |
| CU-02 | Calcular Viático Estimado       | Sistema           | Cálculo automático en PL/SQL según tarifario               |
| CU-03 | Aprobar/Rechazar Solicitud      | Aprobador         | El jefe valida la pertinencia del viaje                    |
| CU-04 | Registrar Asignación de Dinero  | Tesorería         | Registro del depósito bancario inicial                     |
| CU-05 | Registrar Comprobante de Gasto  | Solicitante       | Carga de foto/PDF y datos de factura/boleta                |
| CU-06 | Validar Comprobante             | Analista Contable | Aceptación o rechazo con motivo                            |
| CU-07 | Generar Liquidación Final       | Sistema           | Cálculo automático de saldos usando objeto PL/SQL          |
| CU-08 | Cerrar Comisión                 | Tesorería         | Confirmación de cierre financiero                          |
| CU-09 | Gestionar Tarifario             | Administrador     | CRUD de zonas, niveles, tipos de gasto                     |

### 4.4 Requisitos del Sistema

#### 4.4.1 Requisitos Funcionales

- **RF-01:** El sistema debe permitir registrar solicitudes de comisión de servicio mediante procedimientos almacenados en PL/SQL.
- **RF-02:** El sistema debe calcular automáticamente el monto de viáticos utilizando el tarifario configurado en la base de datos.
- **RF-03:** El sistema debe validar los comprobantes rendidos según criterios contables y fiscales (RUC válido, montos coherentes).
- **RF-04:** El sistema debe permitir la liquidación final automática de la comisión de servicio mediante objetos PL/SQL.
- **RF-05:** El sistema debe registrar auditoría completa de todas las operaciones realizadas (usuario, fecha, acción).
- **RF-06:** El sistema debe soportar múltiples roles de usuario (ADMIN, USER) con permisos diferenciados.

#### 4.4.2 Requisitos No Funcionales

- **RNF-01:** La base de datos debe implementarse en **Oracle Database 12c o superior**.
- **RNF-02:** El modelo de datos debe estar normalizado hasta **3FN** (Tercera Forma Normal).
- **RNF-03:** Toda la lógica de negocio debe residir exclusivamente en **PL/SQL** (paquetes y procedimientos).
- **RNF-04:** El sistema debe garantizar **integridad referencial** mediante foreign keys y **consistencia transaccional** mediante commits/rollbacks.
- **RNF-05:** El diseño debe permitir **escalabilidad** (soporte para más de 1000 solicitudes/mes) y **mantenibilidad**.
- **RNF-06:** El sistema debe implementar **seguridad** mediante autenticación (Spring Security) y encriptación de contraseñas (BCrypt).

---

## 5. DISEÑO DEL SISTEMA

### 5.1 Arquitectura del Sistema

El sistema implementa una **arquitectura en capas** basada en los principios de **Clean Architecture**, con clara separación de responsabilidades:

```
┌─────────────────────────────────────────────────┐
│         CAPA DE PRESENTACIÓN (Frontend)         │
│    Thymeleaf + Tailwind CSS + Lucide Icons     │
└─────────────────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────┐
│    CAPA DE INFRAESTRUCTURA (Controllers)        │
│         13 Controladores REST/Web               │
└─────────────────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────┐
│      CAPA DE APLICACIÓN (Services)              │
│    Lógica de Orquestación - 13 Servicios       │
└─────────────────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────┐
│    CAPA DE DOMINIO (Entities + Repositories)    │
│   12 Entidades JPA + 4 Enums + 12 Repositories  │
└─────────────────────────────────────────────────┘
                      ▼
┌─────────────────────────────────────────────────┐
│         CAPA DE PERSISTENCIA (Oracle)           │
│  13 Tablas + 12 Paquetes PL/SQL + 2 Triggers   │
│        LÓGICA DE NEGOCIO EN PL/SQL              │
└─────────────────────────────────────────────────┘
```

#### Características de la Arquitectura

**1. Inversión de Dependencias:**

- Las capas externas dependen de las internas
- El dominio no conoce detalles de infraestructura

**2. Separación de Responsabilidades:**

- **Controllers:** Manejo de peticiones HTTP y renderizado de vistas
- **Services:** Orquestación y delegación a PL/SQL
- **Repositories:** Abstracción del acceso a datos
- **Entities:** Representación del modelo de dominio

**3. Lógica Centralizada en BD:**

- **100% de cálculos** en PL/SQL (viáticos, liquidaciones, IGV)
- **Validaciones** en constraints y procedimientos
- **Auditoría automática** mediante triggers

### 5.2 Modelo de Datos

El modelo de datos se organiza en **cuatro grupos** de entidades:

#### A. Tablas Maestras (Configuración)

1. **ZONAS_GEOGRAFICAS:** Catálogo de destinos (Lima, Zona Norte, Zona Sur, etc.)
2. **NIVELES_JERARQUICOS:** Clasificación de empleados (Gerente, Técnico, Jefe de Área)
3. **TIPOS_GASTO:** Conceptos permitidos (Alimentación, Hospedaje, Movilidad, Pasajes)
4. **TARIFARIO_VIATICOS:** Matriz de tarifas (zona × nivel × tipo_gasto → monto)

#### B. Tablas de Actores

5. **EMPLEADOS:** Información del personal viajero
6. **USUARIOS:** Credenciales y autenticación
7. **ROLES:** Roles del sistema (ROLE_ADMIN, ROLE_USER)
8. **USUARIOS_ROLES:** Asignación de roles (many-to-many)

#### C. Tablas Transaccionales (Flujo Core)

9. **SOLICITUD_COMISION:** Cabecera de la solicitud de viaje
10. **ITINERARIO_VIAJE:** Detalle de tramos del viaje
11. **ASIGNACION_DINERO:** Registro del depósito inicial
12. **RENDICION_CUENTAS:** Agrupador de gastos presentados
13. **DETALLE_COMPROBANTES:** Registro individual de facturas/boletas

#### D. Tabla de Cierre

14. **LIQUIDACION_FINAL:** Balance financiero y cierre

#### Normalización a 3FN

Todas las tablas cumplen con la **Tercera Forma Normal**:

- **1FN:** No hay grupos repetitivos, solo valores atómicos
- **2FN:** No hay dependencias parciales (todos los atributos dependen de la PK completa)
- **3FN:** No hay dependencias transitivas (atributos no clave solo dependen de la PK)

#### Campos de Auditoría

Todas las tablas maestras incluyen:

- `user_crea`: Usuario que creó el registro
- `fecha_crea`: Timestamp de creación
- `user_mod`: Usuario que modificó por última vez
- `fecha_mod`: Timestamp de última modificación
- `activo`: Flag para soft delete (1 = activo, 0 = inactivo)

### 5.3 Diagrama Entidad-Relación

```
┌──────────────────┐       ┌──────────────────┐
│ ZONAS_GEOGRAFICAS│       │NIVELES_JERARQUICOS│
└────────┬─────────┘       └─────────┬────────┘
         │                           │
         │         ┌─────────────┐   │
         └────────►│   TARIFARIO │◄──┘
                   │   VIATICOS  │
                   └──────┬──────┘
                          │
                          │ ┌──────────┐
                          └►│TIPOS_GASTO│
                            └──────────┘

┌──────────────┐           ┌──────────┐
│   EMPLEADOS  │◄─────────►│ USUARIOS │
└──────┬───────┘           └────┬─────┘
       │                        │
       │                   ┌────┴─────┐
       │                   │  ROLES   │
       │                   └──────────┘
       │
       │      ┌────────────────────┐
       └─────►│ SOLICITUD_COMISION │
              └──────┬─────────────┘
                     │
          ┌──────────┼──────────┐
          │          │          │
   ┌──────▼──┐  ┌───▼────┐  ┌──▼──────────┐
   │ITINERARIO│  │ASIGNACION│ │RENDICION   │
   │  VIAJE   │  │ DINERO  │ │  CUENTAS   │
   └──────────┘  └─────────┘ └──┬──────────┘
                                 │
                         ┌───────▼─────────┐
                         │DETALLE_         │
                         │COMPROBANTES     │
                         └─────────────────┘
                                 │
                         ┌───────▼─────────┐
                         │LIQUIDACION_FINAL│
                         └─────────────────┘
```

---

## 6. TECNOLOGÍAS UTILIZADAS

### 6.1 Base de Datos

- **Oracle Database 12c+**

  - Sistema de gestión de bases de datos relacional (RDBMS)
  - Soporte nativo para PL/SQL, triggers y objetos
  - Transacciones ACID garantizadas

- **PL/SQL**
  - Lenguaje procedural para lógica de negocio
  - 12 paquetes implementados
  - Tipos de objetos (T_LIQUIDACION, T_ITINERARIO_REC)
  - Colecciones (T_ITINERARIO_TAB, T_ESTIMACION_TAB)
  - Cursores REF para consultas dinámicas

### 6.2 Backend

- **Spring Boot 4.0.0**

  - Framework de desarrollo Java empresarial
  - Configuración automática y convención sobre configuración

- **Java 17**

  - Versión LTS (Long Term Support)
  - Records, Pattern Matching, Text Blocks

- **Spring Data JPA**

  - Abstracción de persistencia
  - Repositorios con métodos derivados de nombres
  - Mapeo objeto-relacional (ORM) con Hibernate

- **Spring Security 6**

  - Autenticación y autorización
  - Protección CSRF
  - Encriptación de contraseñas con BCrypt

- **Spring Boot Mail**
  - Envío de correos para recuperación de contraseña

### 6.3 Frontend

- **Thymeleaf**

  - Motor de plantillas server-side
  - Integración nativa con Spring Security
  - Fragmentos reutilizables

- **Tailwind CSS**

  - Framework CSS utility-first
  - Diseño responsivo mobile-first
  - Soporte para dark mode

- **Lucide Icons**
  - Biblioteca de íconos moderna y minimalista
  - Compatible con diseño Apple-like

### 6.4 Herramientas de Desarrollo

- **Maven**

  - Gestión de dependencias
  - Build y empaquetado

- **Git**

  - Control de versiones distribuido

- **Spring Boot DevTools**

  - Recarga automática en desarrollo
  - Live reload de plantillas

- **OpenPDF 2.0.3**

  - Generación de reportes PDF

- **Apache POI 5.2.5**
  - Generación de reportes Excel

---

## 7. IMPLEMENTACIÓN

### 7.1 Modelo Físico de Base de Datos

El modelo físico implementado consta de **13 tablas** normalizadas a 3FN:

#### Tabla: SOLICITUD_COMISION (Core del negocio)

```sql
CREATE TABLE solicitud_comision (
    id_comision NUMBER GENERATED BY DEFAULT ON NULL AS IDENTITY PRIMARY KEY,
    id_empleado NUMBER NOT NULL,
    motivo_viaje VARCHAR2(255) NOT NULL,
    fecha_solicitud DATE DEFAULT SYSDATE,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    estado VARCHAR2(20) DEFAULT 'BORRADOR',
    monto_total NUMBER(12, 2),
    comentario_rechazo VARCHAR2(500),
    -- Auditoría
    user_crea VARCHAR2(30) DEFAULT USER,
    fecha_crea TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    user_mod VARCHAR2(30),
    fecha_mod TIMESTAMP,
    -- Constraints
    CONSTRAINT ck_sol_estado CHECK (
        estado IN ('BORRADOR', 'PENDIENTE', 'APROBADO', 'RECHAZADO',
                   'RENDIDO', 'LIQUIDADO', 'CANCELADO', 'OBSERVADO')
    ),
    CONSTRAINT fk_sol_empleado FOREIGN KEY (id_empleado)
        REFERENCES empleados (id_empleado)
);
```

#### Características Implementadas

1. **Identity Columns:** Generación automática de claves primarias
2. **Default Values:** Valores por defecto para estado, fecha y usuario
3. **Check Constraints:** Validación de estados permitidos
4. **Foreign Keys:** Integridad referencial estricta
5. **Campos de Auditoría:** Trazabilidad completa de cambios

### 7.2 Paquetes PL/SQL

Se implementaron **12 paquetes PL/SQL** que encapsulan toda la lógica de negocio:

#### PKG_SOLICITUDES (Core)

**Tipos Personalizados:**

```sql
CREATE OR REPLACE TYPE T_ITINERARIO_REC AS OBJECT (
    id_zona NUMBER,
    ciudad_especifica VARCHAR2(100),
    noches NUMBER
);

CREATE OR REPLACE TYPE T_ITINERARIO_TAB AS TABLE OF T_ITINERARIO_REC;
```

**Procedimiento Principal:**

```sql
PROCEDURE PRC_REGISTRAR_SOLICITUD (
    p_id_empleado IN NUMBER,
    p_motivo IN VARCHAR2,
    p_fecha_inicio IN DATE,
    p_itinerario IN T_ITINERARIO_TAB,  -- COLECCIÓN
    p_user_crea IN VARCHAR2,
    p_id_comision_out OUT NUMBER
);
```

**Lógica de Cálculo Automático:**

```sql
-- Itera sobre cada tramo del itinerario
FOR i IN 1 .. p_itinerario.COUNT LOOP
    -- Calcula días: último tramo incluye día de retorno
    IF i = p_itinerario.COUNT THEN
        v_dias_tramo := p_itinerario(i).noches + 1;
    ELSE
        v_dias_tramo := p_itinerario(i).noches;
    END IF;

    -- Suma gastos "por día"
    SELECT NVL(SUM(t.monto), 0) INTO v_monto_parcial
    FROM tarifario_viaticos t
    JOIN tipos_gasto g ON t.id_tipo_gasto = g.id_tipo
    WHERE t.id_zona = p_itinerario(i).id_zona
      AND t.id_nivel = v_id_nivel
      AND g.es_asignable_por_dia = 1;

    v_monto_total := v_monto_total + (v_monto_parcial * v_dias_tramo);

    -- Suma gastos "únicos"
    SELECT NVL(SUM(t.monto), 0) INTO v_monto_parcial
    FROM tarifario_viaticos t
    JOIN tipos_gasto g ON t.id_tipo_gasto = g.id_tipo
    WHERE t.id_zona = p_itinerario(i).id_zona
      AND t.id_nivel = v_id_nivel
      AND g.es_asignable_por_dia = 0;

    v_monto_total := v_monto_total + v_monto_parcial;
END LOOP;
```

#### PKG_LIQUIDACIONES (OOP en PL/SQL)

**Objeto con Métodos:**

```sql
CREATE OR REPLACE TYPE T_LIQUIDACION AS OBJECT (
    monto_asignado NUMBER(12,2),
    monto_rendido_validado NUMBER(12,2),

    -- Métodos de Cálculo
    MEMBER FUNCTION calcular_saldo_empresa RETURN NUMBER,
    MEMBER FUNCTION calcular_saldo_empleado RETURN NUMBER,
    MEMBER FUNCTION obtener_tipo_liquidacion RETURN VARCHAR2,

    -- Métodos de Validación
    MEMBER FUNCTION validar_liquidacion RETURN VARCHAR2,
    MEMBER FUNCTION generar_mensaje_liquidacion RETURN VARCHAR2,

    -- Procedimientos
    MEMBER PROCEDURE generar_resumen(
        p_tipo_liq OUT VARCHAR2,
        p_saldo_empresa OUT NUMBER,
        p_saldo_empleado OUT NUMBER,
        p_mensaje OUT VARCHAR2
    )
);
```

**Implementación de Método:**

```sql
MEMBER FUNCTION calcular_saldo_empresa RETURN NUMBER IS
BEGIN
    IF monto_asignado > monto_rendido_validado THEN
        RETURN monto_asignado - monto_rendido_validado;
    ELSE
        RETURN 0;
    END IF;
END;
```

#### PKG_COMPROBANTES (Cálculos Automáticos)

**Cálculo de IGV:**

```sql
PROCEDURE PRC_GUARDAR_COMPROBANTE (...) AS
    v_monto_bruto NUMBER(12,2);
    v_monto_igv NUMBER(12,2);
BEGIN
    -- Cálculo automático según normativa peruana (18% IGV)
    v_monto_bruto := ROUND(p_monto_total / 1.18, 2);
    v_monto_igv := p_monto_total - v_monto_bruto;

    INSERT INTO detalle_comprobantes (
        monto_bruto, monto_igv, monto_total, ...
    ) VALUES (
        v_monto_bruto, v_monto_igv, p_monto_total, ...
    );

    -- Recalcula total aceptado en rendición
    P_ACTUALIZAR_TOTAL_ACEPTADO(p_id_rendicion);
END;
```

#### Lista Completa de Paquetes

| Paquete           | Líneas | Descripción                                       |
| ----------------- | ------ | ------------------------------------------------- |
| PKG_SOLICITUDES   | 357    | Gestión de solicitudes y cálculo de viáticos      |
| PKG_LIQUIDACIONES | 312    | Liquidación final con objeto T_LIQUIDACION        |
| PKG_COMPROBANTES  | 180    | Validación de comprobantes y cálculo de IGV       |
| PKG_EMPLEADOS     | 259    | CRUD de empleados con validaciones                |
| PKG_USUARIOS      | 218    | Autenticación y gestión de usuarios               |
| PKG_RENDICIONES   | 142    | Gestión de rendiciones de cuentas                 |
| PKG_TARIFARIO     | 193    | CRUD del tarifario (matriz de tarifas)            |
| PKG_ZONAS         | 127    | CRUD de zonas geográficas                         |
| PKG_NIVELES       | 128    | CRUD de niveles jerárquicos                       |
| PKG_TIPOS_GASTO   | 149    | CRUD de tipos de gasto                            |
| PKG_ROLES         | 93     | CRUD de roles de seguridad                        |
| PKG-PASSWORD      | 69     | Generación y validación de tokens de recuperación |

### 7.3 Triggers y Objetos

#### Trigger de Auditoría

```sql
CREATE OR REPLACE TRIGGER TRG_AUDITORIA_SOLICITUDES
AFTER UPDATE OF estado ON SOLICITUD_COMISION
FOR EACH ROW
BEGIN
    IF :OLD.estado != :NEW.estado THEN
        INSERT INTO AUDITORIA_SOLICITUDES (
            id_comision, id_empleado,
            estado_anterior, estado_nuevo
        ) VALUES (
            :OLD.id_comision, :OLD.id_empleado,
            :OLD.estado, :NEW.estado
        );
    END IF;
END;
```

#### Trigger de Limpieza

```sql
CREATE OR REPLACE TRIGGER TRG_LIMPIAR_TOKENS_USADOS
AFTER INSERT ON PASSWORD_RESET_TOKENS
BEGIN
    DELETE FROM PASSWORD_RESET_TOKENS
    WHERE usado = 1 OR fecha_expiracion < SYSTIMESTAMP;
    COMMIT;
END;
```

### 7.4 Integración con Sistema Web

#### Capa de Servicios (Java)

```java
@Service
public class SolicitudServiceImpl implements SolicitudService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Transactional
    public Long guardarNuevaSolicitud(GuardarSolicitudDTO dto) {
        // Mapear itinerario Java → Array Oracle
        Array itinerarioArray = jdbcTemplate.execute(
            (Connection con) -> {
                Struct[] structs = new Struct[dto.getItinerario().size()];
                for (int i = 0; i < dto.getItinerario().size(); i++) {
                    Object[] attrs = new Object[] {
                        dto.getItinerario().get(i).getIdZona(),
                        dto.getItinerario().get(i).getCiudad(),
                        dto.getItinerario().get(i).getNoches()
                    };
                    structs[i] = con.createStruct("T_ITINERARIO_REC", attrs);
                }
                return con.createArrayOf("T_ITINERARIO_TAB", structs);
            }
        );

        // Llamar procedimiento almacenado
        Long idComision = jdbcTemplate.execute(
            (CallableStatement cs) -> {
                cs.setLong(1, dto.getIdEmpleado());
                cs.setString(2, dto.getMotivo());
                cs.setDate(3, new java.sql.Date(dto.getFechaInicio().getTime()));
                cs.setArray(4, itinerarioArray);
                cs.setString(5, SecurityContextHolder.getContext()
                    .getAuthentication().getName());
                cs.registerOutParameter(6, Types.NUMERIC);

                cs.execute();
                return cs.getLong(6);
            }
        );

        return idComision;
    }
}
```

#### Controlador Web (Java)

```java
@Controller
@RequestMapping("/solicitudes")
public class SolicitudController {

    @Autowired
    private SolicitudService solicitudService;

    @PostMapping("/guardar")
    public String guardarSolicitud(
        @ModelAttribute GuardarSolicitudDTO dto,
        RedirectAttributes redirectAttributes
    ) {
        try {
            Long idComision = solicitudService.guardarNuevaSolicitud(dto);
            redirectAttributes.addFlashAttribute("success",
                "Solicitud creada con ID: " + idComision);
            return "redirect:/solicitudes/detalle/" + idComision;
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error",
                "Error al crear solicitud: " + e.getMessage());
            return "redirect:/solicitudes/nueva";
        }
    }
}
```

---

## 8. PRUEBAS Y VALIDACIÓN

### 8.1 Capturas del Código en Funcionamiento

**(Sección reservada para capturas de pantalla del sistema en ejecución)**

_Aquí se incluirán:_

- Vista de creación de solicitud con cálculo automático de viáticos
- Dashboard de aprobaciones con listado de solicitudes pendientes
- Formulario de carga de comprobantes con preview de archivos
- Vista de liquidación final con saldos calculados
- Reporte de auditoría de cambios de estado

---

## 9. CONCLUSIONES Y RECOMENDACIONES

### 9.1 Conclusiones

1. **Centralización de Lógica en PL/SQL:** La centralización de la lógica de negocio en PL/SQL permite un mayor control, seguridad y consistencia de los procesos críticos de gestión de viáticos, reduciendo la dependencia del backend de la aplicación web. Esto garantiza que las reglas de negocio se ejecuten siempre de manera consistente, independientemente del cliente que acceda al sistema.

2. **Normalización y Calidad de Datos:** La implementación de un modelo de datos normalizado hasta la Tercera Forma Normal garantiza integridad, elimina redundancias y facilita la escalabilidad del sistema frente al crecimiento operativo de la empresa. Los campos de auditoría en todas las tablas maestras aseguran trazabilidad completa.

3. **Programación Avanzada en PL/SQL:** El uso de paquetes, procedimientos almacenados, triggers, objetos relacionales (T_LIQUIDACION) y colecciones (T_ITINERARIO_TAB) demuestra que Oracle Database no solo actúa como repositorio de datos, sino como una plataforma robusta de procesamiento transaccional, capaz de soportar reglas de negocio complejas y procesos financieros críticos.

4. **Arquitectura Escalable:** La arquitectura en capas implementada, basada en principios de Clean Architecture, permite la separación de responsabilidades y facilita el mantenimiento evolutivo del sistema. La inversión de dependencias garantiza que la lógica de dominio sea independiente de la infraestructura.

5. **Reducción de Errores y Costos:** La automatización del cálculo de viáticos mediante tarifarios parametrizables elimina la subjetividad y reduce sobrecostos. La validación automática de comprobantes y el recalculo de totales aceptados minimizan errores contables y aceleran el cierre financiero.

6. **Cumplimiento de Requerimientos Académicos:** El proyecto cumple al 100% con los requerimientos establecidos: proceso de negocio no común, modelo normalizado a 3FN, lógica en PL/SQL (no en backend), uso de paquetes para CRUD, triggers en entidades core, e implementación de objetos, cursores y colecciones.

### 9.2 Recomendaciones

1. **Implementación de Pruebas Automatizadas:** Se recomienda desarrollar un conjunto de pruebas unitarias y de integración para validar los paquetes PL/SQL y los servicios Java. Esto garantizará la estabilidad del sistema ante cambios futuros.

2. **Optimización de Índices:** A medida que el volumen de datos crezca, será necesario crear índices en columnas de búsqueda frecuente (estado, fecha_crea, id_empleado) para mejorar el rendimiento de las consultas.

3. **Implementación de Caché:** Considerar la implementación de un sistema de caché (Redis, Caffeine) para almacenar temporalmente los resultados de consultas frecuentes al tarifario y reducir la carga en la base de datos.

4. **Dashboard de Reportes:** Desarrollar un módulo de reportes gerenciales con indicadores clave: total de viáticos por zona, promedio de gastos por nivel jerárquico, tasa de aprobación, tiempo promedio de liquidación, etc.

5. **Integración con SUNAT:** Para una futura fase, se recomienda integrar el sistema con los servicios web de SUNAT para validar automáticamente RUCs y verificar la autenticidad de comprobantes electrónicos.

6. **Notificaciones Automáticas:** Implementar un sistema de notificaciones por correo electrónico y/o push para alertar a los usuarios sobre cambios de estado en sus solicitudes (aprobado, rechazado, en validación, etc.).

7. **Backup y Recuperación:** Establecer una política de respaldos automáticos diarios de la base de datos Oracle y un plan de recuperación ante desastres (Disaster Recovery Plan).

---

## 10. REFERENCIAS BIBLIOGRÁFICAS

[1] NegociosLatam. (s.f.). _Sistema de viáticos: cómo aplicarlo en la empresa_. Recuperado de https://negocioslatam.com/sistema-de-viaticos-como-aplicarlo-en-la-empresa/

[2] COFIDE. (s.f.). _Políticas de viáticos: qué son, cómo funcionan y cómo establecerlas_. Recuperado de https://www.cofide.mx/blog/politicas-de-viaticos-que-son-como-funcionan-y-como-establecerlas

[3] Oracle Corporation. (2023). _Oracle Database PL/SQL Language Reference 12c Release 2_. Oracle Corporation.

[4] Martin, R. C. (2017). _Clean Architecture: A Craftsman's Guide to Software Structure and Design_. Prentice Hall.

[5] Fowler, M. (2002). _Patterns of Enterprise Application Architecture_. Addison-Wesley Professional.

[6] Silberschatz, A., Korth, H. F., & Sudarshan, S. (2019). _Database System Concepts_ (7th ed.). McGraw-Hill Education.

[7] Oracle Corporation. (2023). _Oracle Database Concepts 12c Release 2_. Oracle Corporation.

[8] Spring Boot Documentation. (2023). _Spring Framework Reference Documentation_. Recuperado de https://docs.spring.io/spring-boot/docs/current/reference/html/

---

**FIN DEL INFORME**
