package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.Empleado;
import com.viatico.proyect.repository.interfaces.EmpleadoRepository;
import com.viatico.proyect.repository.jpa.EmpleadoJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class EmpleadoRepositoryImpl implements EmpleadoRepository {

    private final EmpleadoJpaRepository jpaRepository;

    public EmpleadoRepositoryImpl(EmpleadoJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Empleado save(Empleado empleado) {
        return jpaRepository.save(empleado);
    }

    @Override
    public Optional<Empleado> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(Empleado empleado) {
        jpaRepository.delete(empleado);
    }

    @Override
    public List<Empleado> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Optional<Empleado> findByDni(String dni) {
        return jpaRepository.findByDni(dni);
    }

    @Override
    public Optional<Empleado> findByEmail(String email) {
        return jpaRepository.findByEmail(email);
    }
}
