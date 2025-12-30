package com.viatico.proyect.domain.enums;

import lombok.Getter;

@Getter
public enum RolNombre {
    ADMIN("ROLE_ADMIN", "Administrador del Sistema"),
    SOLICITANTE("ROLE_SOLICITANTE", "Solicitante"),
    APROBADOR("ROLE_APROBADOR", "Jefe Aprobador"),
    CONTABILIDAD("ROLE_CONTABILIDAD", "Analista Contable"),
    TESORERIA("ROLE_TESORERIA", "Tesorería");

    private final String codigo;
    private final String nombre;

    RolNombre(String codigo, String nombre) {
        this.codigo = codigo;
        this.nombre = nombre;
    }
}
