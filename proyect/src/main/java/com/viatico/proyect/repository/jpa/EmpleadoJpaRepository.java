package com.viatico.proyect.repository.jpa;

import com.viatico.proyect.entity.Empleado;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface EmpleadoJpaRepository extends JpaRepository<Empleado, Long> {
    Optional<Empleado> findByDni(String dni);
}
