package com.viatico.proyect.repository.impl;

import com.viatico.proyect.entity.LiquidacionFinal;
import com.viatico.proyect.repository.interfaces.LiquidacionFinalRepository;
import com.viatico.proyect.repository.jpa.LiquidacionFinalJpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public class LiquidacionFinalRepositoryImpl implements LiquidacionFinalRepository {

    private final LiquidacionFinalJpaRepository jpaRepository;

    public LiquidacionFinalRepositoryImpl(LiquidacionFinalJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public LiquidacionFinal save(LiquidacionFinal liquidacion) {
        return jpaRepository.save(liquidacion);
    }

    @Override
    public Optional<LiquidacionFinal> findById(Long id) {
        return jpaRepository.findById(id);
    }

    @Override
    public void deleteById(Long id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void delete(LiquidacionFinal liquidacion) {
        jpaRepository.delete(liquidacion);
    }

    @Override
    public List<LiquidacionFinal> findAll() {
        return jpaRepository.findAll();
    }

    @Override
    public long count() {
        return jpaRepository.count();
    }

    @Override
    public Optional<LiquidacionFinal> findBySolicitudId(Long solicitudId) {
        return jpaRepository.findBySolicitudId(solicitudId);
    }
}
