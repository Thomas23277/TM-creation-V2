package com.foodstore.htmeleros.controller;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodstore.htmeleros.dto.CategoriaDTO;
import com.foodstore.htmeleros.dto.ProductoDTO;
import com.foodstore.htmeleros.dto.VarianteDTO;
import com.foodstore.htmeleros.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;
    private final ObjectMapper objectMapper;

    public ProductoController(ProductoService productoService, ObjectMapper objectMapper) {
        this.productoService = productoService;
        this.objectMapper = objectMapper;
    }

    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoDTO> create(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "disponible", required = false, defaultValue = "true") Boolean disponible,
            @RequestParam(value = "variantes", required = false) String variantesJson,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes
    ) {
        ProductoDTO dto = new ProductoDTO();
        dto.setNombre(nombre);
        dto.setPrecio(precio);
        dto.setStock(stock);
        dto.setDescripcion(descripcion);
        dto.setDisponible(disponible != null ? disponible : true);

        CategoriaDTO catDto = new CategoriaDTO();
        catDto.setId(categoriaId);
        dto.setCategoria(catDto);

        List<VarianteDTO> variantes = parseVariantes(variantesJson);
        List<MultipartFile> todasLasImagenes = combinarImagenes(imagen, imagenes);

        ProductoDTO saved = productoService.save(dto, imagen, todasLasImagenes, variantes);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoDTO> update(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "disponible", required = false) Boolean disponible,
            @RequestParam(value = "variantes", required = false) String variantesJson,
            @RequestPart(value = "imagen", required = false) MultipartFile imagen,
            @RequestPart(value = "imagenes", required = false) List<MultipartFile> imagenes
    ) {
        ProductoDTO dto = new ProductoDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setPrecio(precio);
        dto.setStock(stock);
        dto.setDescripcion(descripcion);
        dto.setDisponible(disponible);

        CategoriaDTO catDto = new CategoriaDTO();
        catDto.setId(categoriaId);
        dto.setCategoria(catDto);

        List<VarianteDTO> variantes = parseVariantes(variantesJson);
        List<MultipartFile> todasLasImagenes = combinarImagenes(imagen, imagenes);

        ProductoDTO updated = productoService.update(dto, imagen, todasLasImagenes, variantes);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    private List<VarianteDTO> parseVariantes(String json) {
        if (json == null || json.isBlank()) return new ArrayList<>();
        try {
            return objectMapper.readValue(json, new TypeReference<List<VarianteDTO>>() {});
        } catch (Exception e) {
            throw new IllegalArgumentException("Formato inválido para variantes: " + e.getMessage());
        }
    }

    private List<MultipartFile> combinarImagenes(MultipartFile imagen, List<MultipartFile> imagenes) {
        List<MultipartFile> todas = new ArrayList<>();
        if (imagenes != null) todas.addAll(imagenes);
        return todas;
    }
}