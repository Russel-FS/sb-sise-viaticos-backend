package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.Usuario;
import java.util.Optional;
import java.util.List;

public interface UsuarioRepository {
    Usuario save(Usuario usuario);

    Optional<Usuario> findById(Long id);

    void deleteById(Long id);

    void delete(Usuario usuario);

    List<Usuario> findAll();

    long count();

    Optional<Usuario> findByUsername(String username);
}
