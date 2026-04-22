package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.entity.RespuestaResena;
import java.util.List;

public interface RespuestaResenaService {
    RespuestaResena guardar(RespuestaResena respuesta);
    List<RespuestaResena> findByResenaId(Long resenaId);
}