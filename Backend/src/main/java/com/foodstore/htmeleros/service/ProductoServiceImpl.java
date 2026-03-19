package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.dto.ProductoDTO;
import com.foodstore.htmeleros.entity.Categoria;
import com.foodstore.htmeleros.entity.Producto;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.mappers.ProductoMapper;
import com.foodstore.htmeleros.repository.CategoriaRepository;
import com.foodstore.htmeleros.repository.ProductoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final String UPLOAD_DIR = "uploads/productos";

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
    }

    private String guardarImagen(MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) return null;
        try {
            String original = imagen.getOriginalFilename();
            String sanitized = (original != null ? original : "prod.jpg").replaceAll("[^a-zA-Z0-9.\\-]", "_");
            String filename = System.currentTimeMillis() + "_" + sanitized;
            Path basePath = Paths.get(System.getProperty("user.dir"));
            Path uploadPath = basePath.resolve(UPLOAD_DIR).toAbsolutePath().normalize();
            if (!Files.exists(uploadPath)) Files.createDirectories(uploadPath);
            Path filePath = uploadPath.resolve(filename);
            Files.copy(imagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
            return "productos/" + filename;
        } catch (IOException e) {
            throw new RuntimeException("Error guardando imagen del producto", e);
        }
    }

    @Override
    @Transactional
    public ProductoDTO save(ProductoDTO dto, MultipartFile imagen) {
        if (dto.getCategoria() == null && dto.getCategoriaId() == null) {
            throw new IllegalArgumentException("El ID de la categoría es obligatorio");
        }

        Long catId = dto.getCategoriaId() != null ? dto.getCategoriaId() : dto.getCategoria().getId();

        Categoria categoria = categoriaRepository.findById(catId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada ID: " + catId));

        Producto producto = new Producto();
        producto.setNombre(dto.getNombre());
        producto.setPrecio(dto.getPrecio());
        producto.setStock(dto.getStock());
        producto.setDescripcion(dto.getDescripcion());
        producto.setDisponible(dto.getDisponible() != null ? dto.getDisponible() : true);
        producto.setCategoria(categoria);

        String urlImagen = guardarImagen(imagen);
        if (urlImagen != null) {
            producto.setUrlImagen(urlImagen);
        }

        Producto guardado = productoRepository.save(producto);
        return ProductoMapper.toDTO(guardado);
    }

    @Override
    public List<ProductoDTO> findAll() {
        return productoRepository.findAll().stream()
                .map(ProductoMapper::toDTO)
                .toList();
    }

    @Override
    public ProductoDTO findById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        return ProductoMapper.toDTO(producto);
    }

    // 🔥 INTENTO DE DESTRUCCIÓN TOTAL
    @Override
    @Transactional
    public void deleteById(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        productoRepository.delete(producto);
        productoRepository.flush(); // Obligamos a la BD a intentar borrarlo AHORA para atrapar el error
    }

    // 🔥 PLAN B: OCULTARLO (Transacción nueva y limpia)
    @Override
    @Transactional
    public void ocultarProducto(Long id) {
        Producto producto = productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        producto.setDisponible(false);
        productoRepository.save(producto);
    }

    @Override
    @Transactional
    public ProductoDTO update(ProductoDTO dto, MultipartFile imagen) {
        Producto existente = productoRepository.findById(dto.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Long catId = dto.getCategoriaId() != null ? dto.getCategoriaId() : dto.getCategoria().getId();
        Categoria categoria = categoriaRepository.findById(catId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        existente.setNombre(dto.getNombre());
        existente.setPrecio(dto.getPrecio());
        existente.setStock(dto.getStock());
        existente.setDescripcion(dto.getDescripcion());

        if (dto.getDisponible() != null) {
            existente.setDisponible(dto.getDisponible());
        }

        existente.setCategoria(categoria);

        String nuevaUrl = guardarImagen(imagen);
        if (nuevaUrl != null) {
            existente.setUrlImagen(nuevaUrl);
        }

        return ProductoMapper.toDTO(productoRepository.save(existente));
    }

    @Override
    @Transactional
    public ProductoDTO venderProducto(Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        producto.reducirStock(cantidad);
        return ProductoMapper.toDTO(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public ProductoDTO agregarStock(Long productoId, int cantidad) {
        Producto producto = productoRepository.findById(productoId)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));
        producto.aumentarStock(cantidad);
        return ProductoMapper.toDTO(productoRepository.save(producto));
    }

    @Override
    public List<ProductoDTO> findByCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
        return productoRepository.findByCategoria(categoria).stream()
                .map(ProductoMapper::toDTO)
                .toList();
    }
}