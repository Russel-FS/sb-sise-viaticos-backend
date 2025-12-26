package com.viatico.proyect.service.interfaces;

import com.viatico.proyect.entity.Empleado;
import com.viatico.proyect.entity.Usuario;
import java.util.List;

public interface EmpleadoService {
    List<Empleado> listarTodos();

    Empleado guardar(Empleado empleado, String password, Long rolId, String usernameCrea);

    Empleado obtenerPorId(Long id);

    void eliminar(Long id);

    Usuario obtenerUsuarioPorEmpleado(Long empleadoId);
}
