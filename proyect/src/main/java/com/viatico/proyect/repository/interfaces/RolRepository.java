package com.viatico.proyect.repository.interfaces;

import com.viatico.proyect.entity.Rol;
import java.util.Optional;
import java.util.List;

public interface RolRepository {
    Rol save(Rol rol);

    Optional<Rol> findById(Long id);

    void deleteById(Long id);

    void delete(Rol rol);

    List<Rol> findAll();

    long count();

    Optional<Rol> findByCodigo(String codigo);
}
