package com.foodstore.htmeleros.controller;

import com.foodstore.htmeleros.dto.CategoriaDTO;
import com.foodstore.htmeleros.dto.ProductoDTO;
import com.foodstore.htmeleros.service.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    // =====================================================
    // LIST ALL (Para el Admin: debe devolver TODO)
    // =====================================================
    @GetMapping
    public ResponseEntity<List<ProductoDTO>> getAll() {
        return ResponseEntity.ok(productoService.findAll());
    }

    // =====================================================
    // GET BY ID
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productoService.findById(id));
    }

    // =====================================================
    // CREATE (Optimizado para Multipart)
    // =====================================================
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoDTO> create(
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "disponible", required = false, defaultValue = "true") Boolean disponible, // 🔥 Recibimos el estado
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        System.out.println("\n=== 🟢 NUEVA PETICIÓN CREATE PRODUCTO ===");
        System.out.println("➡ Nombre: " + nombre + " | Disponible: " + disponible);

        ProductoDTO dto = new ProductoDTO();
        dto.setNombre(nombre);
        dto.setPrecio(precio);
        dto.setStock(stock);
        dto.setDescripcion(descripcion);
        dto.setDisponible(disponible != null ? disponible : true); // Fallback de seguridad

        // Mapeo de categoría
        CategoriaDTO catDto = new CategoriaDTO();
        catDto.setId(categoriaId);
        dto.setCategoria(catDto);

        ProductoDTO saved = productoService.save(dto, imagen);

        System.out.println("✅ Producto guardado con éxito. Estado: " + (saved.getDisponible() ? "DISPONIBLE" : "OCULTO"));
        System.out.println("==========================================\n");

        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    // =====================================================
    // UPDATE (Optimizado para Multipart)
    // =====================================================
    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoDTO> update(
            @PathVariable Long id,
            @RequestParam("nombre") String nombre,
            @RequestParam("precio") Double precio,
            @RequestParam("stock") Integer stock,
            @RequestParam("categoriaId") Long categoriaId,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "disponible", required = false) Boolean disponible, // 🔥 Capturamos el cambio de switch
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
    ) {
        System.out.println("\n=== 🟡 NUEVA PETICIÓN UPDATE PRODUCTO ===");
        System.out.println("➡ Editando ID: " + id + " | Nuevo Estado Disponible: " + disponible);

        ProductoDTO dto = new ProductoDTO();
        dto.setId(id);
        dto.setNombre(nombre);
        dto.setPrecio(precio);
        dto.setStock(stock);
        dto.setDescripcion(descripcion);

        // Es vital que si 'disponible' llega nulo, se mantenga el valor que el Service determine
        dto.setDisponible(disponible);

        CategoriaDTO catDto = new CategoriaDTO();
        catDto.setId(categoriaId);
        dto.setCategoria(catDto);

        ProductoDTO updated = productoService.update(dto, imagen);
        return ResponseEntity.ok(updated);
    }

    // =====================================================
    // DELETE
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productoService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}