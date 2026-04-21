package com.foodstore.htmeleros.repository;

import com.foodstore.htmeleros.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByProductoIdOrderByFechaDesc(Long productoId);

    @Query("SELECT AVG(r.estrellas) FROM Resena r WHERE r.producto.id = :productoId")
    Double getPromedioEstrellasByProductoId(Long productoId);

    @Query("SELECT COUNT(r) FROM Resena r WHERE r.producto.id = :productoId")
    Long countByProductoId(Long productoId);

    List<Resena> findByUsuarioIdOrderByFechaDesc(Long usuarioId);
}