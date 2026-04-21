package com.foodstore.htmeleros.dto;

import com.foodstore.htmeleros.enums.Estado;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PedidoDTO {

    private Long id;
    private LocalDateTime fecha;
    private Estado estado;
    private double total;

    private List<DetallePedidoDTO> detalles;

    // ID del usuario
    private Long usuarioId;

    // 🔥 Nuevo: nombre del usuario (para mostrarlo en el frontend)
    private String usuarioNombre;

    private String direccionEntrega;
}
