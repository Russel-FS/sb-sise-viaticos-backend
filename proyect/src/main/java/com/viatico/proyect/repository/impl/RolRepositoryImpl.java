package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.Rol;
import com.viatico.proyect.repository.interfaces.RolRepository;
import com.viatico.proyect.repository.jpa.RolJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class RolRepositoryImpl implements RolRepository {

    private final RolJpaRepository jpaRepository;

    public RolRepositoryImpl(RolJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Rol save(Rol rol) {
        return jpaRepository.save(rol);
    }

    @Override
    public Optional<Rol> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(Rol rol) {
        jpaRepository.delete(rol);
    }

    @Override
    public List<Rol> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Optional<Rol> findByCodigo(String codigo) {
        return jpaRepository.findByCodigo(codigo);
    }
}
