package com.foodstore.htmeleros.dto;

import lombok.Data;
import java.util.List;

@Data
public class CheckoutDTO {

    private Long usuarioId;

    // 🔥 Campos nuevos para capturar los datos del formulario de compra
    private String nombreCompleto;
    private String telefono;

    private String direccionEntrega;
    private String emailCliente;

    private List<DetallePedidoDTO> detalles;
}