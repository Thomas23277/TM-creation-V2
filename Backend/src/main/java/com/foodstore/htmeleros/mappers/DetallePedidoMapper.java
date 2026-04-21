package com.foodstore.htmeleros.mappers;

import com.foodstore.htmeleros.dto.DetallePedidoDTO;
import com.foodstore.htmeleros.entity.DetallePedido;
import com.foodstore.htmeleros.entity.Producto;

public class DetallePedidoMapper {

    public static DetallePedidoDTO toDTO(DetallePedido detalle) {
        if (detalle == null) return null;

        DetallePedidoDTO dto = new DetallePedidoDTO();
        dto.setId(detalle.getId());
        dto.setCantidad(detalle.getCantidad());
        dto.setPrecioUnitario(detalle.getPrecioUnitario());
        dto.setSubtotal(detalle.getSubtotal());

        // Enviar *solo* el ID del producto
        if (detalle.getProducto() != null) {
            dto.setProductoId(detalle.getProducto().getId());
        }

        return dto;
    }

    public static DetallePedido toEntity(DetallePedidoDTO dto) {
        if (dto == null) return null;

        DetallePedido detalle = new DetallePedido();
        detalle.setId(dto.getId());
        detalle.setCantidad(dto.getCantidad());
        detalle.setPrecioUnitario(dto.getPrecioUnitario());
        detalle.setSubtotal(dto.getSubtotal());

        // Crear Producto solo con ID
        if (dto.getProductoId() != null) {
            Producto p = new Producto();
            p.setId(dto.getProductoId());
            detalle.setProducto(p);
        }

        return detalle;
    }
}
