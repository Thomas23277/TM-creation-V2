package com.foodstore.htmeleros.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodstore.htmeleros.entity.Variante;

public interface VarianteRepository extends JpaRepository<Variante, Long> {

    void deleteByProductoId(Long productoId);
}
