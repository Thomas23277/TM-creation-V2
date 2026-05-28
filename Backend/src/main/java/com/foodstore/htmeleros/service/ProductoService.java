package com.foodstore.htmeleros.service;

import java.util.List;
import com.foodstore.htmeleros.dto.ProductoDTO;
import com.foodstore.htmeleros.dto.ColorDisponibleDTO;
import com.foodstore.htmeleros.dto.VarianteDTO;
import org.springframework.web.multipart.MultipartFile;

public interface ProductoService {

    List<ProductoDTO> findAll();

    ProductoDTO findById(Long id);

    ProductoDTO save(ProductoDTO dto, MultipartFile imagen, List<MultipartFile> imagenesAdicionales, List<VarianteDTO> variantes, List<ColorDisponibleDTO> colores);

    ProductoDTO update(ProductoDTO dto, MultipartFile imagen, List<MultipartFile> imagenesAdicionales, List<VarianteDTO> variantes, List<ColorDisponibleDTO> colores, List<Long> imagenesEliminarIds);

    void deleteById(Long id);

    List<ProductoDTO> findByCategoria(Long categoriaId);

    List<ProductoDTO> search(String term);

    List<ProductoDTO> findRecomendados();

    List<ProductoDTO> findDestacados();

    ProductoDTO venderProducto(Long productoId, int cantidad);

    ProductoDTO agregarStock(Long productoId, int cantidad);
}
