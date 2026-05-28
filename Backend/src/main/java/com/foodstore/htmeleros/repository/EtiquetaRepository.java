package com.foodstore.htmeleros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.foodstore.htmeleros.entity.Etiqueta;

public interface EtiquetaRepository extends JpaRepository<Etiqueta, Long> {
    boolean existsByNombre(String nombre);
}
