package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.Usuario;
import com.viatico.proyect.repository.interfaces.UsuarioRepository;
import com.viatico.proyect.repository.jpa.UsuarioJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class UsuarioRepositoryImpl implements UsuarioRepository {

    private final UsuarioJpaRepository jpaRepository;

    public UsuarioRepositoryImpl(UsuarioJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Usuario save(Usuario usuario) {
        return jpaRepository.save(usuario);
    }

    @Override
    public Optional<Usuario> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(Usuario usuario) {
        jpaRepository.delete(usuario);
    }

    @Override
    public List<Usuario> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Optional<Usuario> findByUsername(String username) {
        return jpaRepository.findByUsername(username);
    }
}
