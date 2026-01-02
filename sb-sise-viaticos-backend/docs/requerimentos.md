# Diseño e Implementación de una Base de Datos en Oracle con Integración a un Sistema Web

## Objetivo del Proyecto

Definir un proceso de negocio, identificar todas las entidades necesarias e implementar un modelado, diseño y gestión de base de datos en Oracle con integración a un Sistema Web. Aplicar conocimientos en creación y administración de bases de datos, manejo de datos mediante PL/SQL, y asegurar la correcta implementación de mecanismos de seguridad y auditoría de la información.

## Requerimientos y Entregables

### 1. Definición del Proceso de Negocio

- Los estudiantes deben seleccionar un proceso de negocio excluyendo procesos comunes como ventas, inventarios, matrícula, etc.

### 2. Diagrama de Caso de Uso del Sistema

- Incluir actores y casos de uso del sistema.

### 3. Lista de Entidades

- Listar las entidades que tendrá el esquema de BD, sin incluir campos, con relación a las funcionalidades del sistema.

### 4. Modelo Físico

- Crear sus entidades hasta la **Tercera Forma Normal (3FN)**.
- Incluir campos de auditoría.

### 5. Desarrollo de CRUD

- Los métodos de insertar, actualizar y eliminar que estén en la capa de modelo deben ser llamados por **Paquetes** o **Procedimientos Almacenados**.

### 6. Triggers

- Los disparadores solo se inicializarán cuando sean entidades **Core** del negocio.

### 7. Lógica del Sistema

- Aplicar cálculos por el lado de **PL/SQL**, no por el backend.
- Ejemplos de implementaciones:
  - Objetos
  - Cursores implícitos o explícitos
  - Colecciones
