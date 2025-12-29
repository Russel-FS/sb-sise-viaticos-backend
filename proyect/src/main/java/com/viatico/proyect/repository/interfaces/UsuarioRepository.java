package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.Usuario;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepository {

    void crearUsuario(Long idEmpleado, String username, String email,
            String password, Long rolId, String userCrea);

    void eliminarPorEmpleado(Long idEmpleado);

    Long obtenerIdPorEmpleado(Long idEmpleado);

    List<Usuario> listarTodos();

    Optional<Usuario> obtenerPorUsername(String username);

    Optional<Usuario> obtenerPorEmail(String email);
}
