package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.entity.Resena;
import java.util.List;
import java.util.Optional;

public interface ResenaService {
    Resena guardar(Resena resena, Long productoId, Long usuarioId);
    List<Resena> findAll();
    List<Resena> findByProductoId(Long productoId);
    Optional<Resena> findById(Long id);
    void eliminar(Long id);
    Double getPromedioEstrellas(Long productoId);
    Long countByProductoId(Long productoId);
    List<Resena> findByUsuarioId(Long usuarioId);
    boolean usuarioYaReseno(Long usuarioId, Long productoId);
}