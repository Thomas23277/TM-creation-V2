package com.foodstore.htmeleros.mappers;

import com.foodstore.htmeleros.dto.CategoriaDTO;
import com.foodstore.htmeleros.entity.Categoria;

public class CategoriaMapper {

    // ============================================
    // ENTITY → DTO
    // ============================================
    public static CategoriaDTO toDTO(Categoria categoria) {
        if (categoria == null) return null;

        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(categoria.getId());
        dto.setNombre(categoria.getNombre());

        // normalizamos para frontend
        if (categoria.getUrlImagen() != null && !categoria.getUrlImagen().isBlank()) {
            String url = categoria.getUrlImagen().replace("\\", "/");

            if (!url.startsWith("/uploads/")) {
                url = "/uploads/" + url;
            }

            dto.setUrlImagen(url);
        } else {
            dto.setUrlImagen(null);
        }

        return dto;
    }

    // ============================================
    // DTO → ENTITY
    // ============================================
    public static Categoria toEntity(CategoriaDTO dto) {
        if (dto == null) return null;

        Categoria categoria = new Categoria();
        categoria.setId(dto.getId());
        categoria.setNombre(dto.getNombre());

        // limpiamos antes de persistir
        if (dto.getUrlImagen() != null && !dto.getUrlImagen().isBlank()) {
            String url = dto.getUrlImagen().replace("\\", "/");

            if (url.startsWith("/uploads/")) {
                url = url.substring("/uploads/".length());
            } else if (url.startsWith("/")) {
                url = url.substring(1);
            }

            categoria.setUrlImagen(url);
        } else {
            categoria.setUrlImagen(null);
        }

        return categoria;
    }
}
