package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.dto.CategoriaDTO;
import com.foodstore.htmeleros.entity.Categoria;
import com.foodstore.htmeleros.entity.Producto;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.mappers.CategoriaMapper;
import com.foodstore.htmeleros.repository.CategoriaRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final UploadService uploadService;

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository, UploadService uploadService) {
        this.categoriaRepository = categoriaRepository;
        this.uploadService = uploadService;
    }

    // =====================================================
    // CREATE
    // =====================================================
    @Override
    public CategoriaDTO save(CategoriaDTO dto, MultipartFile imagen) {

        Categoria categoria = CategoriaMapper.toEntity(dto);
        categoria.setDisponible(true);

        if (imagen != null && !imagen.isEmpty()) {
            String url = uploadService.uploadCategoriaImage(imagen);
            categoria.setUrlImagen(url);
        }

        Categoria saved = categoriaRepository.save(categoria);

        return CategoriaMapper.toDTO(saved);
    }

    // =====================================================
    // UPDATE
    // =====================================================
    @Override
    public CategoriaDTO update(Long id, CategoriaDTO dto, MultipartFile imagen) {

        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        existente.setNombre(dto.getNombre());

        if (imagen != null && !imagen.isEmpty()) {
            String nuevaUrl = uploadService.uploadCategoriaImage(imagen);
            existente.setUrlImagen(nuevaUrl);
        }

        Categoria updated = categoriaRepository.save(existente);

        return CategoriaMapper.toDTO(updated);
    }

    // =====================================================
    // READ
    // =====================================================
    @Override
    public CategoriaDTO findById(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        return CategoriaMapper.toDTO(categoria);
    }

    @Override
    public List<CategoriaDTO> findAll() {

        return categoriaRepository.findAll()
                .stream()
                .filter(Categoria::isDisponible)
                .map(CategoriaMapper::toDTO)
                .toList();
    }

    // =====================================================
    // SOFT DELETE + CASCADA LÓGICA
    // =====================================================
    @Override
    public void deleteById(Long id) {

        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        categoria.setDisponible(false);

        if (categoria.getProductos() != null) {
            for (Producto producto : categoria.getProductos()) {
                producto.setDisponible(false);
            }
        }

        categoriaRepository.save(categoria);
    }
}