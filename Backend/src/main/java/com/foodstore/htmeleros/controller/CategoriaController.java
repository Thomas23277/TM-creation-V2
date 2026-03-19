package com.foodstore.htmeleros.controller;

import com.foodstore.htmeleros.dto.CategoriaDTO;
import com.foodstore.htmeleros.service.CategoriaService;

import org.springframework.dao.DataIntegrityViolationException; // 🔥 Importación clave
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/categoria")
public class CategoriaController {

    private final CategoriaService categoriaService;

    public CategoriaController(CategoriaService categoriaService) {
        this.categoriaService = categoriaService;
    }

    // =====================================================
    // CREATE
    // =====================================================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoriaDTO> save(
            @RequestParam("nombre") String nombre,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {

        System.out.println("\n=== 🟢 NUEVA PETICIÓN CREATE CATEGORÍA ===");
        System.out.println("➡ Nombre recibido: " + nombre);

        if (imagen != null && !imagen.isEmpty()) {
            System.out.println("➡ Imagen recibida: " + imagen.getOriginalFilename() + " | Tamaño: " + imagen.getSize() + " bytes");
        } else {
            System.out.println("❌ ATENCIÓN: La imagen llegó a Spring Boot como NULL o vacía.");
        }

        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre(nombre);

        CategoriaDTO saved = categoriaService.save(dto, imagen);

        System.out.println("✅ Guardado en DB -> URL Imagen: " + saved.getUrlImagen());
        System.out.println("==========================================\n");

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(saved);
    }

    // =====================================================
    // READ BY ID
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<CategoriaDTO> findById(@PathVariable Long id) {
        CategoriaDTO categoria = categoriaService.findById(id);
        return ResponseEntity.ok(categoria);
    }

    // =====================================================
    // LIST ALL
    // =====================================================
    @GetMapping
    public ResponseEntity<List<CategoriaDTO>> findAll() {
        List<CategoriaDTO> categorias = categoriaService.findAll();
        return ResponseEntity.ok(categorias);
    }

    // =====================================================
    // UPDATE
    // =====================================================
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<CategoriaDTO> update(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {

        System.out.println("\n=== 🟡 NUEVA PETICIÓN UPDATE CATEGORÍA ===");
        System.out.println("➡ ID: " + id + " | Nombre: " + nombre);

        if (imagen != null && !imagen.isEmpty()) {
            System.out.println("➡ Imagen recibida: " + imagen.getOriginalFilename());
        } else {
            System.out.println("➡ No se envió nueva imagen (NULL).");
        }

        CategoriaDTO dto = new CategoriaDTO();
        dto.setNombre(nombre);

        CategoriaDTO updated = categoriaService.update(id, dto, imagen);

        System.out.println("==========================================\n");

        return ResponseEntity.ok(updated);
    }

    // =====================================================
    // DELETE INTELIGENTE
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            // Plan A: Intentar destruir de verdad
            categoriaService.deleteById(id);
            return ResponseEntity.noContent().build();

        } catch (DataIntegrityViolationException e) {
            // Plan B: Si la BD se queja (porque tiene productos), la ocultamos en una transacción nueva
            System.out.println("⚠️ Categoría " + id + " tiene productos. Ejecutando Ocultamiento seguro.");
            categoriaService.ocultarCategoria(id);

            // Retornamos OK (200) para que el Frontend recargue la tabla visualmente sin error
            return ResponseEntity.ok().build();
        }
    }
}