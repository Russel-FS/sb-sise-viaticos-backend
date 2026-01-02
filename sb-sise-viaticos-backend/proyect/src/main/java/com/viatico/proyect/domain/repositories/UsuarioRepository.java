package com.viatico.proyect.domain.repositories;

import java.util.Optional;

import com.viatico.proyect.domain.entity.Rol;
import com.viatico.proyect.domain.entity.Usuario;

import java.util.List;

public interface UsuarioRepository {

    Long crearUsuario(Long idEmpleado, String username, String email,
            String password, String userCrea);

    void asignarRoles(Long idUsuario, List<Long> rolIds, String userCrea);

    void eliminarPorEmpleado(Long idEmpleado);

    Long obtenerIdPorEmpleado(Long idEmpleado);

    List<Usuario> listarTodos();

    Optional<Usuario> obtenerPorUsername(String username);

    Optional<Usuario> obtenerPorEmail(String email);

    void actualizarPassword(Long idUsuario, String password);

    List<Rol> obtenerRolesUsuario(Long idUsuario);
}
