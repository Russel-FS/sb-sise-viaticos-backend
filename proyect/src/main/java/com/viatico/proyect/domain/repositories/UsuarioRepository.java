package com.viatico.proyect.domain.repositories;

import java.util.Optional;

import com.viatico.proyect.domain.entity.Usuario;

import java.util.List;

public interface UsuarioRepository {

    void crearUsuario(Long idEmpleado, String username, String email,
            String password, Long rolId, String userCrea);

    void eliminarPorEmpleado(Long idEmpleado);

    Long obtenerIdPorEmpleado(Long idEmpleado);

    List<Usuario> listarTodos();

    Optional<Usuario> obtenerPorUsername(String username);

    Optional<Usuario> obtenerPorEmail(String email);

    void actualizarPassword(Long idUsuario, String password);

    void actualizarRol(Long idUsuario, Long rolId);
}
