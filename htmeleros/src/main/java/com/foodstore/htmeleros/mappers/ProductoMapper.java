package com.foodstore.htmeleros.mappers;

import com.foodstore.htmeleros.dto.ProductoDTO;
import com.foodstore.htmeleros.entity.Producto;
import com.foodstore.htmeleros.entity.Categoria;

public class ProductoMapper {

    // ============================================
    // ENTITY → DTO
    // ============================================
    public static ProductoDTO toDTO(Producto producto) {
        if (producto == null) return null;

        ProductoDTO dto = new ProductoDTO();

        dto.setId(producto.getId());
        dto.setNombre(producto.getNombre());
        dto.setPrecio(producto.getPrecio());
        dto.setStock(producto.getStock());
        dto.setDescripcion(producto.getDescripcion());

        // 🔥 En la Entity el método sigue siendo isDisponible() (porque es boolean primitivo)
        dto.setDisponible(producto.isDisponible());

        // Normalizar URL para frontend
        if (producto.getUrlImagen() != null && !producto.getUrlImagen().isBlank()) {
            String url = producto.getUrlImagen().replace("\\", "/");
            // Limpiamos prefijos duplicados para que el frontend reciba una ruta limpia
            if (url.startsWith("productos/")) {
                url = "/uploads/" + url;
            } else if (!url.startsWith("/uploads/")) {
                url = "/uploads/productos/" + url;
            }
            dto.setUrlImagen(url);
        } else {
            dto.setUrlImagen(null);
        }

        if (producto.getCategoria() != null) {
            // Usamos el mapper de categoría para traer el objeto completo al DTO
            dto.setCategoria(CategoriaMapper.toDTO(producto.getCategoria()));
            dto.setCategoriaId(producto.getCategoria().getId());
        }

        return dto;
    }

    // ============================================
    // DTO → ENTITY
    // ============================================
    public static Producto toEntity(ProductoDTO dto) {
        if (dto == null) return null;

        Producto producto = new Producto();

        producto.setId(dto.getId());
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setDescripcion(dto.getDescripcion());

        // 🔥 CORRECCIÓN CLAVE: Ahora usamos getDisponible() porque el DTO es Boolean (objeto)
        // Añadimos un chequeo de nulidad para evitar sorpresas
        if (dto.getDisponible() != null) {
            producto.setDisponible(dto.getDisponible());
        } else {
            producto.setDisponible(true); // Default si viene nulo
        }

        // Limpiar URL antes de persistir (solo guardamos la ruta relativa)
        if (dto.getUrlImagen() != null && !dto.getUrlImagen().isBlank()) {
            String url = dto.getUrlImagen().replace("\\", "/");

            if (url.startsWith("/uploads/productos/")) {
                url = url.substring("/uploads/".length());
            } else if (url.startsWith("/uploads/")) {
                url = url.substring("/uploads/".length());
            } else if (url.startsWith("/")) {
                url = url.substring(1);
            }

            producto.setUrlImagen(url);
        } else {
            producto.setUrlImagen(null);
        }

        // Seteamos categoría por ID (entidad liviana para JPA)
        if (dto.getCategoria() != null && dto.getCategoria().getId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(dto.getCategoria().getId());
            producto.setCategoria(categoria);
        } else if (dto.getCategoriaId() != null) {
            Categoria categoria = new Categoria();
            categoria.setId(dto.getCategoriaId());
            producto.setCategoria(categoria);
        }

        return producto;
    }

    // ============================================
    // ENTITY liviana solo con ID
    // ============================================
    public static Producto toEntityId(Long id) {
        if (id == null) return null;

        Producto p = new Producto();
        p.setId(id);
        return p;
    }
}