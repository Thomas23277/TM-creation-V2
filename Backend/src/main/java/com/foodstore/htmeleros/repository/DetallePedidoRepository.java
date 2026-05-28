package com.foodstore.htmeleros.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.foodstore.htmeleros.entity.DetallePedido;

public interface DetallePedidoRepository extends JpaRepository<DetallePedido, Long> {

    boolean existsByProductoId(Long productoId);

    @Query("SELECT d.producto.id FROM DetallePedido d GROUP BY d.producto.id ORDER BY SUM(d.cantidad) DESC")
    List<Long> findTopProductoIdsByCantidadTotal();
}

