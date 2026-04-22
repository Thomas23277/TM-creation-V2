package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.entity.RespuestaResena;
import com.foodstore.htmeleros.entity.Resena;
import com.foodstore.htmeleros.repository.RespuestaResenaRepository;
import com.foodstore.htmeleros.repository.ResenaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class RespuestaResenaServiceImpl implements RespuestaResenaService {

    private final RespuestaResenaRepository respuestaRepository;
    private final ResenaRepository resenaRepository;

    public RespuestaResenaServiceImpl(RespuestaResenaRepository respuestaRepository, ResenaRepository resenaRepository) {
        this.respuestaRepository = respuestaRepository;
        this.resenaRepository = resenaRepository;
    }

    @Override
    @Transactional
    public RespuestaResena guardar(RespuestaResena respuesta) {
        Resena resena = resenaRepository.findById(respuesta.getResena().getId())
            .orElseThrow(() -> new RuntimeException("Reseña no encontrada"));
        respuesta.setResena(resena);
        return respuestaRepository.save(respuesta);
    }

    @Override
    public List<RespuestaResena> findByResenaId(Long resenaId) {
        return respuestaRepository.findByResenaIdOrderByFechaDesc(resenaId);
    }
}