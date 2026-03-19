package com.foodstore.htmeleros.repository;

import com.foodstore.htmeleros.entity.Pedido;
import com.foodstore.htmeleros.enums.Estado;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {

    /**
     * Buscar pedidos por id de usuario
     */
    List<Pedido> findByUsuarioId(Long usuarioId);

    /**
     * Buscar pedidos por estado y fecha de creación anterior a un límite
     * (Usado para cancelación automática de pedidos vencidos)
     */
    List<Pedido> findByEstadoAndFechaBefore(
            Estado estado,
            LocalDateTime fechaLimite
    );

}