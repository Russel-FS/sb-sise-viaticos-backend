package com.viatico.proyect.application.service.interfaces;

public interface EmailService {
    void enviarEmailRecuperacion(String to, String token);
}
