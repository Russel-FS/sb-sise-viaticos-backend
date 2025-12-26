package com.viatico.proyect.repository.jpa;

import com.viatico.proyect.entity.NivelJerarquico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface NivelJerarquicoJpaRepository extends JpaRepository<NivelJerarquico, Long> {
    Optional<NivelJerarquico> findByNombre(String nombre);

    List<NivelJerarquico> findByActivo(Integer activo);
}
