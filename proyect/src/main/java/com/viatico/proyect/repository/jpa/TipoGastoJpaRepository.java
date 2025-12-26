package com.viatico.proyect.repository.jpa;

import com.viatico.proyect.entity.TipoGasto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface TipoGastoJpaRepository extends JpaRepository<TipoGasto, Long> {
    List<TipoGasto> findByActivo(Integer activo);
}
