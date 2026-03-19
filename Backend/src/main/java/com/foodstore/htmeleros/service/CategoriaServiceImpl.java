package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.dto.CategoriaDTO;
import com.foodstore.htmeleros.entity.Categoria;
import com.foodstore.htmeleros.entity.Producto;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.mappers.CategoriaMapper;
import com.foodstore.htmeleros.repository.CategoriaRepository;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;

@Service
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;

    // 🔥 CORRECCIÓN: Ya no usamos application.properties para evitar el escape al Disco C:
    // Forzamos la ruta estrictamente a esta constante.
    private final String UPLOAD_DIR = "uploads/categorias";

    public CategoriaServiceImpl(CategoriaRepository categoriaRepository) {
        this.categoriaRepository = categoriaRepository;
    }

    // =====================================================
    // GUARDAR IMAGEN
    // =====================================================
    private String guardarImagen(MultipartFile imagen) {

        if (imagen == null || imagen.isEmpty()) {
            return null;
        }

        try {
            String original = imagen.getOriginalFilename();
            if (original == null || original.isBlank()) {
                original = "imagen.jpg";
            }

            // Sanitizar nombre
            String sanitized = original.replaceAll("[^a-zA-Z0-9\\.\\-]", "_");
            String filename = System.currentTimeMillis() + "_" + sanitized;

            // 🔥 CORRECCIÓN CRÍTICA: Anclamos la ruta al directorio raíz del proyecto
            Path basePath = Paths.get(System.getProperty("user.dir"));
            Path uploadPath = basePath.resolve(UPLOAD_DIR).toAbsolutePath().normalize();

            // Crear carpeta si no existe
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            Path filePath = uploadPath.resolve(filename);

            System.out.println("\n==================================================");
            System.out.println("📁 RUTA EXACTA DEL DISCO DONDE SE GUARDÓ LA IMAGEN:");
            System.out.println("➡ " + filePath.toString());
            System.out.println("==================================================\n");

            Files.copy(imagen.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Guardamos SOLO la ruta relativa dentro de /uploads
            return "categorias/" + filename;

        } catch (IOException e) {
            throw new RuntimeException("Error guardando imagen de categoría", e);
        }
    }

    // =====================================================
    // ELIMINAR IMAGEN FÍSICA
    // =====================================================
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

    // =====================================================
    // CREATE
    // =====================================================
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

    // =====================================================
    // UPDATE
    // =====================================================
    @Override
    public CategoriaDTO update(Long id, CategoriaDTO dto, MultipartFile imagen) {

        Categoria existente = categoriaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        existente.setNombre(dto.getNombre());

        String nuevaUrl = guardarImagen(imagen);

        if (nuevaUrl != null) {

            // eliminar imagen anterior
            eliminarImagen(existente.getUrlImagen());

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