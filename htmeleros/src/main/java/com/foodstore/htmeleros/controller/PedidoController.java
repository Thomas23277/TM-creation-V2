package com.foodstore.htmeleros.controller;

import java.util.List;
import java.util.Map;

import com.foodstore.htmeleros.dto.CheckoutDTO;
import com.foodstore.htmeleros.dto.PedidoDTO;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.service.PedidoService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = "*") // Simplificado para pruebas, luego puedes volver a poner tu lista
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    // ============================================================
    //                   CREAR PEDIDO
    // ============================================================
    @PostMapping("/producto")
    public ResponseEntity<?> crearDesdeProducto(@RequestBody PedidoDTO pedidoDTO) {
        try {
            PedidoDTO creado = pedidoService.save(pedidoDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(creado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // ============================================================
    //                   CHECKOUT FINAL
    // ============================================================
    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(@RequestBody CheckoutDTO checkoutDTO) {
        try {
            PedidoDTO pedido = pedidoService.checkout(checkoutDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(pedido);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ============================================================
    //                   LISTAR TODOS
    // ============================================================
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.findAll());
    }

    // ============================================================
    //                   OBTENER POR ID
    // ============================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> obtener(@PathVariable Long id) {
        try {
            PedidoDTO pedido = pedidoService.findById(id);
            return ResponseEntity.ok(pedido);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No existe el pedido");
        }
    }

    // ============================================================
    //           ACTUALIZAR ESTADO (MÉTODO CORREGIDO)
    // ============================================================
    @PutMapping("/{id}/estado")
    public ResponseEntity<?> actualizarEstado(@PathVariable Long id, @RequestBody Map<String, String> body) {
        try {
            String nuevoEstado = body.get("estado");
            if (nuevoEstado == null) return ResponseEntity.badRequest().body("Falta el campo estado");

            // 🔥 CAMBIO CLAVE: Llamamos a 'updateEstado' que es el nombre en tu Service
            PedidoDTO actualizado = pedidoService.updateEstado(id, nuevoEstado);

            return ResponseEntity.ok(actualizado);
        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ============================================================
    //                   ACTUALIZAR COMPLETO
    // ============================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> actualizar(@PathVariable Long id, @RequestBody PedidoDTO pedidoDTO) {
        try {
            PedidoDTO actualizado = pedidoService.update(id, pedidoDTO);
            return ResponseEntity.ok(actualizado);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ============================================================
    //                   ELIMINAR
    // ============================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Long id) {
        try {
            pedidoService.deleteById(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    // ============================================================
    //                   LISTAR POR USUARIO
    // ============================================================
    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<?> listarPorUsuario(@PathVariable Long usuarioId) {
        try {
            return ResponseEntity.ok(pedidoService.findByUsuario(usuarioId));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }
}