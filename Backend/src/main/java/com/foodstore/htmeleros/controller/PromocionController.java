package com.foodstore.htmeleros.controller;

import com.foodstore.htmeleros.entity.Promocion;
import com.foodstore.htmeleros.service.PromocionService;
import com.foodstore.htmeleros.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "*")
public class PromocionController {

    private final PromocionService promocionService;
    private final UploadService uploadService;

    public PromocionController(PromocionService promocionService, UploadService uploadService) {
        this.promocionService = promocionService;
        this.uploadService = uploadService;
    }

    @GetMapping("/activas")
    public ResponseEntity<List<Promocion>> getActivas() {
        return ResponseEntity.ok(promocionService.findActivas());
    }

    @GetMapping
    public ResponseEntity<List<Promocion>> getAll() {
        return ResponseEntity.ok(promocionService.findAll());
    }

    @PostMapping
    public ResponseEntity<Promocion> crear(
            @RequestParam("titulo") String titulo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam(value = "urlEnlace", required = false) String urlEnlace,
            @RequestParam(value = "orden", defaultValue = "0") int orden) {

        String urlImagen = null;
        if (imagen != null && !imagen.isEmpty()) {
            urlImagen = uploadService.subirImagen(imagen, "promociones");
        }

        Promocion promocion = new Promocion();
        promocion.setTitulo(titulo);
        promocion.setDescripcion(descripcion);
        promocion.setUrlImagen(urlImagen);
        promocion.setUrlEnlace(urlEnlace);
        promocion.setOrden(orden);
        promocion.setActivo(true);

        return ResponseEntity.ok(promocionService.guardar(promocion));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Promocion> actualizar(
            @PathVariable Long id,
            @RequestParam("titulo") String titulo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "imagen", required = false) MultipartFile imagen,
            @RequestParam(value = "urlEnlace", required = false) String urlEnlace,
            @RequestParam(value = "orden", defaultValue = "0") int orden) {

        return promocionService.findById(id).map(p -> {
            p.setTitulo(titulo);
            p.setDescripcion(descripcion);
            p.setUrlEnlace(urlEnlace);
            p.setOrden(orden);

            if (imagen != null && !imagen.isEmpty()) {
                String urlImagen = uploadService.subirImagen(imagen, "promociones");
                p.setUrlImagen(urlImagen);
            }

            return ResponseEntity.ok(promocionService.guardar(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        promocionService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @PatchMapping("/{id}/toggle")
    public ResponseEntity<Promocion> toggle(@PathVariable Long id) {
        return ResponseEntity.ok(promocionService.toggleActivo(id));
    }
}