package com.viatico.proyect.repository.jpa;

import com.viatico.proyect.entity.ZonaGeografica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ZonaGeograficaJpaRepository extends JpaRepository<ZonaGeografica, Long> {
    List<ZonaGeografica> findByActivo(Integer activo);
}
