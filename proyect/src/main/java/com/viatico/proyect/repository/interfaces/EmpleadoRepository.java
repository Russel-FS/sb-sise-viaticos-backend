package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.Empleado;
import java.util.Optional;
import java.util.List;

public interface EmpleadoRepository {
    Empleado save(Empleado empleado);

    Optional<Empleado> findById(Long id);

    void deleteById(Long id);

    void delete(Empleado empleado);

    List<Empleado> findAll();

    long count();

    Optional<Empleado> findByDni(String dni);

    Optional<Empleado> findByEmail(String email);
}
