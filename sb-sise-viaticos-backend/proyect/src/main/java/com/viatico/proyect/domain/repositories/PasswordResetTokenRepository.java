package com.viatico.proyect.domain.repositories;

public interface PasswordResetTokenRepository {
    Long crearToken(String email, String token, int horasValidez);

    Long validarToken(String token);

    void usarToken(String token);

    void actualizarPassword(Long idUsuario, String nuevaPassword);
}
