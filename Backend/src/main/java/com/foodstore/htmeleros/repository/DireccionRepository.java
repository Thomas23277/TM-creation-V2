package com.foodstore.htmeleros.repository;

import com.foodstore.htmeleros.entity.Direccion;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface DireccionRepository extends JpaRepository<Direccion, Long> {
    List<Direccion> findByUsuarioId(Long usuarioId);
    Optional<Direccion> findByIdAndUsuarioId(Long id, Long usuarioId);
}
