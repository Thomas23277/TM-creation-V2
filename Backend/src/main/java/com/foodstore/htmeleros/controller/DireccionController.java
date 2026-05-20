package com.foodstore.htmeleros.controller;

import com.foodstore.htmeleros.dto.DireccionDTO;
import com.foodstore.htmeleros.service.DireccionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/usuarios/{usuarioId}/direcciones")
public class DireccionController {

    private final DireccionService direccionService;

    public DireccionController(DireccionService direccionService) {
        this.direccionService = direccionService;
    }

    @GetMapping
    public ResponseEntity<List<DireccionDTO>> listar(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(direccionService.findByUsuarioId(usuarioId));
    }

    @PostMapping
    public ResponseEntity<DireccionDTO> crear(@PathVariable Long usuarioId, @Valid @RequestBody DireccionDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(direccionService.create(usuarioId, dto));
    }

    @PutMapping("/{direccionId}")
    public ResponseEntity<DireccionDTO> actualizar(@PathVariable Long usuarioId, @PathVariable Long direccionId, @Valid @RequestBody DireccionDTO dto) {
        return ResponseEntity.ok(direccionService.update(usuarioId, direccionId, dto));
    }

    @DeleteMapping("/{direccionId}")
    public ResponseEntity<Void> eliminar(@PathVariable Long usuarioId, @PathVariable Long direccionId) {
        direccionService.delete(usuarioId, direccionId);
        return ResponseEntity.noContent().build();
    }
}
