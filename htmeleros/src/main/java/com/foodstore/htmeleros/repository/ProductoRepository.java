package com.foodstore.htmeleros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.foodstore.htmeleros.entity.Categoria;
import com.foodstore.htmeleros.entity.Producto;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria(Categoria categoria);
    List<Producto> findByDisponibleTrue();


}
