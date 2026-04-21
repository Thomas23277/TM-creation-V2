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

import java.util.List;

@Service
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UploadService uploadService;

    public ProductoServiceImpl(ProductoRepository productoRepository, CategoriaRepository categoriaRepository, UploadService uploadService) {
        this.productoRepository = productoRepository;
        this.categoriaRepository = categoriaRepository;
        this.uploadService = uploadService;
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

        if (imagen != null && !imagen.isEmpty()) {
            String url = uploadService.uploadProductoImage(imagen);
            producto.setUrlImagen(url);
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

    @Override
    @Transactional
    public void deleteById(Long id) {
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

        if (imagen != null && !imagen.isEmpty()) {
            String nuevaUrl = uploadService.uploadProductoImage(imagen);
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