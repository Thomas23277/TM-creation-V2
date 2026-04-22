package com.foodstore.htmeleros.repository;

import com.foodstore.htmeleros.entity.RespuestaResena;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface RespuestaResenaRepository extends JpaRepository<RespuestaResena, Long> {
    List<RespuestaResena> findByResenaIdOrderByFechaDesc(Long resenaId);
}