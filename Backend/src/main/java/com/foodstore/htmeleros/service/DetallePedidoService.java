package com.foodstore.htmeleros.service;

import java.util.List;

import com.foodstore.htmeleros.dto.DetallePedidoDTO;

public interface DetallePedidoService {

    DetallePedidoDTO save(DetallePedidoDTO detalle);

    List<DetallePedidoDTO> findAll();

    DetallePedidoDTO findById(Long id);

    void deleteById(Long id);

    DetallePedidoDTO update(Long id, DetallePedidoDTO nuevo);
}
