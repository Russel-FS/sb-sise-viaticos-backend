package com.viatico.proyect.repository;

import com.viatico.proyect.entity.TipoGasto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TipoGastoRepository extends JpaRepository<TipoGasto, Long> {
    List<TipoGasto> findByActivo(Integer activo);
}
