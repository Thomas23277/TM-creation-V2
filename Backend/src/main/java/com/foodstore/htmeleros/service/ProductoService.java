package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.dto.ProductoDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductoService {

    // ================================
    // CRUD
    // ================================
    List<ProductoDTO> findAll();

    ProductoDTO findById(Long id);

    ProductoDTO save(ProductoDTO dto, MultipartFile imagen);

    ProductoDTO update(ProductoDTO dto, MultipartFile imagen);

    void deleteById(Long id);

    // 🔥 NUEVO MÉTODO: Transacción limpia para ocultar
    void ocultarProducto(Long id);

    // ================================
    // FILTROS
    // ================================
    List<ProductoDTO> findByCategoria(Long categoriaId);

    // ================================
    // STOCK
    // ================================
    ProductoDTO venderProducto(Long productoId, int cantidad);

    ProductoDTO agregarStock(Long productoId, int cantidad);
}