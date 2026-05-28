package com.foodstore.htmeleros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.foodstore.htmeleros.entity.Categoria;
import com.foodstore.htmeleros.entity.Producto;


@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {
    List<Producto> findByCategoria(Categoria categoria);
    List<Producto> findByDisponibleTrue();
    List<Producto> findTop6ByDisponibleTrueOrderByIdDesc();

    @Query("SELECT DISTINCT p FROM Producto p LEFT JOIN p.etiquetas e " +
           "WHERE LOWER(p.nombre) LIKE LOWER(CONCAT('%', :term, '%')) " +
           "OR LOWER(e.nombre) LIKE LOWER(CONCAT('%', :term, '%'))")
    List<Producto> search(@Param("term") String term);
}
