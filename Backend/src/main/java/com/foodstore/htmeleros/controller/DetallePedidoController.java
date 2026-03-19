package com.foodstore.htmeleros.controller;

import java.util.List;

import com.foodstore.htmeleros.dto.DetallePedidoDTO;
import com.foodstore.htmeleros.service.DetallePedidoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/detalles")
@CrossOrigin(origins = {
        "http://localhost:5173",
        "http://localhost:5174",
        "http://localhost:5175",
        "http://localhost:5176",
        "http://localhost:8081",
        "http://localhost:8080"
})
public class DetallePedidoController {

    private final DetallePedidoService detalleService;

    public DetallePedidoController(DetallePedidoService detalleService) {
        this.detalleService = detalleService;
    }

    @PostMapping
    public ResponseEntity<DetallePedidoDTO> save(@RequestBody DetallePedidoDTO dto) {
        return ResponseEntity.ok(detalleService.save(dto));
    }

    @GetMapping
    public ResponseEntity<List<DetallePedidoDTO>> findAll() {
        return ResponseEntity.ok(detalleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DetallePedidoDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(detalleService.findById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<DetallePedidoDTO> update(@PathVariable Long id, @RequestBody DetallePedidoDTO dto) {
        return ResponseEntity.ok(detalleService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        detalleService.deleteById(id);
    }
}
