package com.foodstore.htmeleros.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.foodstore.htmeleros.dto.EtiquetaDTO;
import com.foodstore.htmeleros.service.EtiquetaService;

@RestController
@RequestMapping("/api/etiquetas")
public class EtiquetaController {

    private final EtiquetaService etiquetaService;

    public EtiquetaController(EtiquetaService etiquetaService) {
        this.etiquetaService = etiquetaService;
    }

    @GetMapping
    public ResponseEntity<List<EtiquetaDTO>> findAll() {
        return ResponseEntity.ok(etiquetaService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EtiquetaDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(etiquetaService.findById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EtiquetaDTO> create(@RequestBody EtiquetaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(etiquetaService.create(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<EtiquetaDTO> update(@PathVariable Long id, @RequestBody EtiquetaDTO dto) {
        return ResponseEntity.ok(etiquetaService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        etiquetaService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
