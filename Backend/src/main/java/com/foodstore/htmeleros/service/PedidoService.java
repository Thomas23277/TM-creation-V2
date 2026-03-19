package com.foodstore.htmeleros.service;

import java.util.List;
import com.foodstore.htmeleros.dto.CheckoutDTO;
import com.foodstore.htmeleros.dto.PedidoDTO;
import com.foodstore.htmeleros.enums.Estado;

public interface PedidoService {

    // ============================
    // CRUD PRINCIPAL
    // ============================
    PedidoDTO save(PedidoDTO dto);

    PedidoDTO findById(Long id);

    List<PedidoDTO> findAll();

    void deleteById(Long id);

    PedidoDTO update(Long id, PedidoDTO pedidoDTO);

    List<PedidoDTO> findByUsuario(Long usuarioId);

    // ============================
    // CHECKOUT PROFESIONAL
    // ============================
    PedidoDTO checkout(CheckoutDTO checkoutDTO);

    // ============================
    // CAMBIAR ESTADO (Sincronizado con Controller)
    // ============================
    // Cambiamos el nombre a 'updateEstado' para que coincida con tu implementación
    PedidoDTO updateEstado(Long pedidoId, String nuevoEstado);

    // ============================
    // 🔥 NUEVO: Cancelación automática
    // ============================
    void cancelarPedidosVencidos();
}
