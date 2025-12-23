package com.viatico.proyect.repository;

import com.viatico.proyect.entity.ZonaGeografica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ZonaGeograficaRepository extends JpaRepository<ZonaGeografica, Long> {
    List<ZonaGeografica> findByActivo(Integer activo);
}
