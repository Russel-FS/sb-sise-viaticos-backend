package com.viatico.proyect.repository.jpa;

import com.viatico.proyect.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioJpaRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByUsername(String username);
}
