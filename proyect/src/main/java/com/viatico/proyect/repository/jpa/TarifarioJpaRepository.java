package com.viatico.proyect.repository.jpa;

import com.viatico.proyect.entity.NivelJerarquico;
import com.viatico.proyect.entity.Tarifario;
import com.viatico.proyect.entity.ZonaGeografica;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TarifarioJpaRepository extends JpaRepository<Tarifario, Long> {
    List<Tarifario> findAllByNivelJerarquicoAndZonaGeografica(NivelJerarquico nivel, ZonaGeografica zona);

    List<Tarifario> findByActivo(Integer activo);
}
