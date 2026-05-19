package com.foodstore.htmeleros.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.foodstore.htmeleros.entity.ProductoImagen;

public interface ProductoImagenRepository extends JpaRepository<ProductoImagen, Long> {

    void deleteByProductoId(Long productoId);
}
