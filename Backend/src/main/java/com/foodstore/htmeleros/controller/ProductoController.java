package com.foodstore.htmeleros.controller;

import com.foodstore.htmeleros.dto.CategoriaDTO;
import com.foodstore.htmeleros.dto.ProductoDTO;
import com.foodstore.htmeleros.service.ProductoService;
import org.springframework.dao.DataIntegrityViolationException; // 🔥 Importante para atrapar el error
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
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
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

        ProductoDTO saved = productoService.save(dto, imagen);
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
            @RequestPart(value = "imagen", required = false) MultipartFile imagen
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

        ProductoDTO updated = productoService.update(dto, imagen);
        return ResponseEntity.ok(updated);
    }

    // 🔥 LA INTELIGENCIA ESTÁ AQUÍ
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        try {
            // Plan A: Intentar destruir de verdad
            productoService.deleteById(id);
            return ResponseEntity.noContent().build();

        } catch (DataIntegrityViolationException e) {
            // Plan B: Si la BD se queja, lo ocultamos en una transacción nueva
            System.out.println("⚠️ Producto " + id + " tiene ventas. Ejecutando Ocultamiento seguro.");
            productoService.ocultarProducto(id);

            // Retornamos OK (200) para que el Frontend (tu panel Admin) no lance error rojo,
            // sino que recargue la tabla tranquilamente y veamos visualmente el cambio a "Oculto".
            return ResponseEntity.ok().build();
        }
    }
}