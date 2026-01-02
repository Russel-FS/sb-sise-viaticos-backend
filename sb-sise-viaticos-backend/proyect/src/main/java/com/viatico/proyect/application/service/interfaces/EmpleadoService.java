package com.viatico.proyect.application.service.interfaces;

import java.util.List;

import com.viatico.proyect.domain.entity.Empleado;
import com.viatico.proyect.domain.entity.Usuario;

public interface EmpleadoService {
    List<Empleado> listarTodos();

    Empleado guardar(Empleado empleado, String password, List<Long> rolIds, String username, String usernameCrea);

    Empleado obtenerPorId(Long id);

    void eliminar(Long id);

    Usuario obtenerUsuarioPorEmpleado(Long empleadoId);
}
