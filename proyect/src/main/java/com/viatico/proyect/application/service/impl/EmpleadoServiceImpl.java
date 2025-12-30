package com.viatico.proyect.application.service.impl;

import com.viatico.proyect.application.service.interfaces.EmpleadoService;
import com.viatico.proyect.domain.entity.Empleado;
import com.viatico.proyect.domain.entity.Usuario;
import com.viatico.proyect.domain.repositories.EmpleadoRepository;
import com.viatico.proyect.domain.repositories.UsuarioRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Empleado> listarTodos() {
        return empleadoRepository.listarTodos();
    }

    @Override
    @Transactional
    public Empleado guardar(Empleado empleado, String password, Long rolId, String usernameCrea) {
        boolean esNuevo = (empleado.getId() == null);

        Long idEmpleado = empleadoRepository.guardarEmpleado(empleado, usernameCrea);

        if (esNuevo) {
            String passwordEncriptado = passwordEncoder.encode(password);
            usuarioRepository.crearUsuario(
                    idEmpleado,
                    empleado.getEmail(),
                    empleado.getEmail(),
                    passwordEncriptado,
                    rolId,
                    usernameCrea);
        }

        return empleadoRepository.obtenerPorId(idEmpleado)
                .orElseThrow(() -> new RuntimeException("Error al recuperar empleado guardado"));
    }

    @Override
    public Empleado obtenerPorId(Long id) {
        return empleadoRepository.obtenerPorId(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        usuarioRepository.eliminarPorEmpleado(id);
        empleadoRepository.eliminarEmpleado(id);
    }

    @Override
    public Usuario obtenerUsuarioPorEmpleado(Long empleadoId) {
        return usuarioRepository.listarTodos().stream()
                .filter(u -> u.getEmpleado().getId().equals(empleadoId))
                .findFirst().orElse(null);
    }
}
