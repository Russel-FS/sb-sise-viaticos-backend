package com.viatico.proyect.domain.repositories;

import java.util.List;
import java.util.Optional;

import com.viatico.proyect.domain.entity.Empleado;

public interface EmpleadoRepository {

    List<Empleado> listarTodos();

    Long guardarEmpleado(Empleado empleado, String userCrea);

    void eliminarEmpleado(Long id);

    Optional<Empleado> obtenerPorId(Long id);

    boolean existeDni(String dni, Long idActual);

    boolean existeEmail(String email, Long idActual);
}
