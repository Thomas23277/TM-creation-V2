package com.foodstore.htmeleros.mappers;

import java.util.Collections;
import java.util.List;
import com.foodstore.htmeleros.dto.ColorDisponibleDTO;
import com.foodstore.htmeleros.dto.EtiquetaDTO;
import com.foodstore.htmeleros.dto.ProductoDTO;
import com.foodstore.htmeleros.dto.ProductoImagenDTO;
import com.foodstore.htmeleros.dto.VarianteDTO;
import com.foodstore.htmeleros.entity.ColorDisponible;
import com.foodstore.htmeleros.entity.Etiqueta;
import com.foodstore.htmeleros.entity.Producto;
import com.foodstore.htmeleros.entity.Categoria;
import com.foodstore.htmeleros.entity.ProductoImagen;
import com.foodstore.htmeleros.entity.Variante;

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
        dto.setColoresActivo(producto.isColoresActivo());
        dto.setStockControl(producto.isStockControl());

        String urlNormalizada = normalizarUrl(producto.getUrlImagen());
        dto.setUrlImagen(urlNormalizada);

        if (producto.getCategoria() != null) {
            dto.setCategoria(CategoriaMapper.toDTO(producto.getCategoria()));
            dto.setCategoriaId(producto.getCategoria().getId());
        }

        if (producto.getVariantes() != null) {
            dto.setVariantes(producto.getVariantes().stream()
                    .map(v -> new VarianteDTO(v.getId(), v.getNombre(), v.getPrecio(), v.getColorHex()))
                    .toList());
        }

        if (producto.getColores() != null) {
            dto.setColores(producto.getColores().stream()
                    .map(c -> new ColorDisponibleDTO(c.getId(), c.getNombre(), c.getColorHex(), c.getUrlImagen()))
                    .toList());
        }

        if (producto.getImagenes() != null) {
            dto.setImagenes(producto.getImagenes().stream()
                    .map(img -> new ProductoImagenDTO(img.getId(), normalizarUrl(img.getUrlImagen()), img.getOrden()))
                    .toList());
        }

        if (producto.getEtiquetas() != null) {
            dto.setEtiquetas(producto.getEtiquetas().stream()
                    .map(e -> new EtiquetaDTO(e.getId(), e.getNombre(), e.getColorHex(), e.isVisible(), e.isInterna()))
                    .collect(java.util.stream.Collectors.toSet()));
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

    private static String normalizarUrl(String url) {
        if (url == null || url.isBlank()) return null;
        url = url.replace("\\", "/");
        if (url.startsWith("productos/")) {
            return "/uploads/" + url;
        } else if (!url.startsWith("/uploads/")) {
            return "/uploads/productos/" + url;
        }
        return url;
    }
}