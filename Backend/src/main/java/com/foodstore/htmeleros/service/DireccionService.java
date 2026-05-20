package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.dto.DireccionDTO;
import java.util.List;

public interface DireccionService {
    List<DireccionDTO> findByUsuarioId(Long usuarioId);
    DireccionDTO create(Long usuarioId, DireccionDTO dto);
    DireccionDTO update(Long usuarioId, Long direccionId, DireccionDTO dto);
    void delete(Long usuarioId, Long direccionId);
}
