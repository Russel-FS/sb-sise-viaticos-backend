package com.viatico.proyect.repository.jpa;

import com.viatico.proyect.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface RolJpaRepository extends JpaRepository<Rol, Long> {
    Optional<Rol> findByCodigo(String codigo);
}
