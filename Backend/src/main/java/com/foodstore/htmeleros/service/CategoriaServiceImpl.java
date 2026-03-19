package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.dto.CategoriaDTO;
import com.foodstore.htmeleros.entity.Categoria;
import com.foodstore.htmeleros.entity.Producto;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.mappers.CategoriaMapper;
import com.foodstore.htmeleros.repository.CategoriaRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final String UPLOAD_DIR = "uploads/categorias";

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    private String guardarImagen(MultipartFile imagen) {
        if (imagen == null || imagen.isEmpty()) {
            return null;
        }

        try {
            String original = imagen.getOriginalFilename();
            if (original == null || original.isBlank()) {
                original = "imagen.jpg";
            }

            String sanitized = original.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            String filename = System.currentTimeMillis() + "_" + sanitized;

            Path basePath = Paths.get(System.getProperty("user.dir"));
            Path uploadPath = basePath.resolve(UPLOAD_DIR).toAbsolutePath().normalize();

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);
            Files.copy(imagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return "categorias/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Error guardando imagen de categoría", e);
        }
    }

    private void eliminarImagen(String urlImagen) {
        if (urlImagen == null || urlImagen.isBlank()) {
            return;
        }

        try {
            Path basePath = Paths.get(System.getProperty("user.dir"));
            Path imagePath = basePath.resolve("uploads").resolve(urlImagen).toAbsolutePath().normalize();
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            System.err.println("No se pudo eliminar imagen anterior: " + urlImagen);
        }
    }

    @Override
    public CategoriaDTO save(CategoriaDTO dto, MultipartFile imagen) {
        Categoria categoria = CategoriaMapper.toEntity(dto);
        categoria.setDisponible(true);

        String url = guardarImagen(imagen);
        if (url != null) {
            categoria.setUrlImagen(url);
        }

        Categoria saved = categoriaRepository.save(categoria);
        return CategoriaMapper.toDTO(saved);
    }

    @Override
    public CategoriaDTO update(Long id, CategoriaDTO dto, MultipartFile imagen) {
        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        existente.setNombre(dto.getNombre());

        String nuevaUrl = guardarImagen(imagen);
        if (nuevaUrl != null) {
            eliminarImagen(existente.getUrlImagen());
            existente.setUrlImagen(nuevaUrl);
        }

        Categoria updated = categoriaRepository.save(existente);
        return CategoriaMapper.toDTO(updated);
    }

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
                .map(CategoriaMapper::toDTO)
                .toList();
    }

    // 🔥 INTENTO DE DESTRUCCIÓN TOTAL (Plan A)
    @Override
    @Transactional
    public void deleteById(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        categoriaRepository.delete(categoria);
        categoriaRepository.flush(); // Obligamos a ejecutarlo AHORA para ver si la BD se queja

        // Si sobrevive al flush (se borró con éxito), limpiamos el disco duro
        eliminarImagen(categoria.getUrlImagen());
    }

    // 🔥 BORRADO LÓGICO Y CASCADA (Plan B)
    @Override
    @Transactional
    public void ocultarCategoria(Long id) {
        Categoria categoria = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        categoria.setDisponible(false);

        // Ocultamos todos sus productos hijos para que no se sigan vendiendo
        if (categoria.getProductos() != null) {
            for (Producto producto : categoria.getProductos()) {
                producto.setDisponible(false);
            }
        }
        categoriaRepository.save(categoria);
    }
}