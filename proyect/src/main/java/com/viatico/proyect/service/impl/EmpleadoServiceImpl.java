package com.viatico.proyect.service.impl;

import com.viatico.proyect.entity.Empleado;
import com.viatico.proyect.entity.Rol;
import com.viatico.proyect.entity.Usuario;
import com.viatico.proyect.repository.interfaces.EmpleadoRepository;
import com.viatico.proyect.repository.interfaces.RolRepository;
import com.viatico.proyect.repository.interfaces.UsuarioRepository;
import com.viatico.proyect.service.interfaces.EmpleadoService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EmpleadoServiceImpl implements EmpleadoService {

    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<Empleado> listarTodos() {
        return empleadoRepository.findAll();
    }

    @Override
    @Transactional
    public Empleado guardar(Empleado empleado, String password, Long rolId, String usernameCrea) {
        boolean isNew = (empleado.getId() == null);

        // validacion de dni
        empleadoRepository.findByDni(empleado.getDni()).ifPresent(existing -> {
            if (empleado.getId() == null || !existing.getId().equals(empleado.getId())) {
                throw new RuntimeException("Ya existe un empleado con el DNI: " + empleado.getDni());
            }
        });

        // validacion de email
        empleadoRepository.findByEmail(empleado.getEmail()).ifPresent(existing -> {
            if (empleado.getId() == null || !existing.getId().equals(empleado.getId())) {
                throw new RuntimeException("Ya existe un empleado con el email: " + empleado.getEmail());
            }
        });

        if (isNew) {
            empleado.setFechaCrea(LocalDateTime.now());
            empleado.setUserCrea(usernameCrea);
        }

        Empleado savedEmpleado = empleadoRepository.save(empleado);

        if (isNew) {
            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            Usuario usuario = new Usuario();
            usuario.setEmpleado(savedEmpleado);
            usuario.setUsername(empleado.getEmail());
            usuario.setPassword(passwordEncoder.encode(password));
            usuario.setRol(rol);
            usuario.setActivo(1);
            usuario.setFechaCrea(LocalDateTime.now());
            usuario.setUserCrea(usernameCrea);

            usuarioRepository.save(usuario);
        }

        return savedEmpleado;
    }

    @Override
    public Empleado obtenerPorId(Long id) {
        return empleadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        Empleado emp = obtenerPorId(id);
        Usuario user = usuarioRepository.findAll().stream()
                .filter(u -> u.getEmpleado().getId().equals(id))
                .findFirst().orElse(null);

        if (user != null) {
            usuarioRepository.delete(user);
        }
        empleadoRepository.delete(emp);
    }

    @Override
    public Usuario obtenerUsuarioPorEmpleado(Long empleadoId) {
        return usuarioRepository.findAll().stream()
                .filter(u -> u.getEmpleado().getId().equals(empleadoId))
                .findFirst().orElse(null);
    }
}
