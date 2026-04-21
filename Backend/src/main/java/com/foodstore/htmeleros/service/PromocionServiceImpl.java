package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.entity.Promocion;
import com.foodstore.htmeleros.repository.PromocionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;

@Service
public class PromocionServiceImpl implements PromocionService {

    private final PromocionRepository promocionRepository;

    public PromocionServiceImpl(PromocionRepository promocionRepository) {
        this.promocionRepository = promocionRepository;
    }

    @Override
    @Transactional
    public Promocion guardar(Promocion promocion) {
        return promocionRepository.save(promocion);
    }

    @Override
    public List<Promocion> findActivas() {
        return promocionRepository.findByActivoTrueOrderByOrdenAsc();
    }

    @Override
    public List<Promocion> findAll() {
        return promocionRepository.findAllByOrderByOrdenAsc();
    }

    @Override
    public Optional<Promocion> findById(Long id) {
        return promocionRepository.findById(id);
    }

    @Override
    @Transactional
    public void eliminar(Long id) {
        promocionRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Promocion toggleActivo(Long id) {
        Optional<Promocion> opt = promocionRepository.findById(id);
        if (opt.isPresent()) {
            Promocion p = opt.get();
            p.setActivo(!p.isActivo());
            return promocionRepository.save(p);
        }
        return null;
    }
}