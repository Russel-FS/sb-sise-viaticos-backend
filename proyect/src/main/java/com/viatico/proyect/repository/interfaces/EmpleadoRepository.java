package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.Empleado;
import java.util.List;
import java.util.Optional;

public interface EmpleadoRepository {

    List<Empleado> listarTodos();

    Long guardarEmpleado(Empleado empleado, String userCrea);

    void eliminarEmpleado(Long id);

    Optional<Empleado> obtenerPorId(Long id);

    boolean existeDni(String dni, Long idActual);

    boolean existeEmail(String email, Long idActual);
}
