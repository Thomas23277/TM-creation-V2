package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.dto.DetallePedidoDTO;
import com.foodstore.htmeleros.entity.DetallePedido;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.mappers.DetallePedidoMapper;
import com.foodstore.htmeleros.repository.DetallePedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DetallePedidoServiceImpl implements DetallePedidoService {

    @Autowired
    private DetallePedidoRepository repository;

    @Override
    public DetallePedidoDTO save(DetallePedidoDTO dto) {
        DetallePedido detalle = DetallePedidoMapper.toEntity(dto);
        detalle.setSubtotal(detalle.getCantidad() * detalle.getPrecioUnitario());
        DetallePedido guardado = repository.save(detalle);
        return DetallePedidoMapper.toDTO(guardado);
    }

    @Override
    public List<DetallePedidoDTO> findAll() {
        return repository.findAll().stream()
                .map(DetallePedidoMapper::toDTO)
                .toList();
    }

    @Override
    public DetallePedidoDTO findById(Long id) {
        DetallePedido detalle = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetallePedido con id:" + id + " no encontrado"));
        return DetallePedidoMapper.toDTO(detalle);
    }

    @Override
    public void deleteById(Long id) {
        DetallePedido detalle = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetallePedido con id:" + id + " no encontrado"));
        repository.delete(detalle);
    }

    @Override
    public DetallePedidoDTO update(Long id, DetallePedidoDTO nuevo) {
        DetallePedido actual = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("DetallePedido con id:" + id + " no encontrado"));

        actual.setCantidad(nuevo.getCantidad());
        actual.setPrecioUnitario(nuevo.getPrecioUnitario());
        actual.setSubtotal(nuevo.getCantidad() * nuevo.getPrecioUnitario());

        DetallePedido actualizado = repository.save(actual);
        return DetallePedidoMapper.toDTO(actualizado);
    }
}
