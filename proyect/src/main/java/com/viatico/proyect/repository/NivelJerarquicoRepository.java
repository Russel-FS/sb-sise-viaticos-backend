package com.viatico.proyect.repository;

import com.viatico.proyect.entity.NivelJerarquico;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface NivelJerarquicoRepository extends JpaRepository<NivelJerarquico, Long> {
    Optional<NivelJerarquico> findByNombre(String nombre);

    List<NivelJerarquico> findByActivo(Integer activo);
}
