package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.entity.Promocion;
import java.util.List;
import java.util.Optional;

public interface PromocionService {
    Promocion guardar(Promocion promocion);
    List<Promocion> findActivas();
    List<Promocion> findAll();
    Optional<Promocion> findById(Long id);
    void eliminar(Long id);
    Promocion toggleActivo(Long id);
}