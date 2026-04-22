package com.foodstore.htmeleros.controller;

import com.foodstore.htmeleros.entity.Resena;
import com.foodstore.htmeleros.dto.ProductoDTO;
import com.foodstore.htmeleros.dto.UsuarioDTO;
import com.foodstore.htmeleros.service.EmailService;
import com.foodstore.htmeleros.service.ProductoService;
import com.foodstore.htmeleros.service.ResenaService;
import com.foodstore.htmeleros.service.UsuarioService;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@RestController
@RequestMapping("/api/resenas")
@CrossOrigin(origins = "*")
public class ResenaController {

    private final ResenaService resenaService;
    private final ProductoService productoService;
    private final UsuarioService usuarioService;
    private final EmailService emailService;

    @Autowired
    public ResenaController(ResenaService resenaService, ProductoService productoService, UsuarioService usuarioService, EmailService emailService) {
        this.resenaService = resenaService;
        this.productoService = productoService;
        this.usuarioService = usuarioService;
        this.emailService = emailService;
    }

    @GetMapping
    @Transactional(readOnly = true)
    public ResponseEntity<List<Resena>> getAll() {
        return ResponseEntity.ok(resenaService.findAll());
    }

    @GetMapping("/producto/{productoId}")
    @Transactional(readOnly = true)
    public ResponseEntity<Map<String, Object>> getByProducto(@PathVariable Long productoId) {
        List<Resena> resenas = resenaService.findByProductoId(productoId);
        Double promedio = resenaService.getPromedioEstrellas(productoId);
        Long total = resenaService.countByProductoId(productoId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("resenas", resenas);
        response.put("promedio", promedio);
        response.put("total", total);
        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<Resena> crear(@RequestBody Map<String, Object> request) {
        Long productoId = Long.parseLong(request.get("productoId").toString());
        Long usuarioId = Long.parseLong(request.get("usuarioId").toString());
        Integer estrellas = request.get("estrellas") != null ? Integer.parseInt(request.get("estrellas").toString()) : 5;
        String comentario = request.get("comentario") != null ? request.get("comentario").toString() : "";

        if (resenaService.usuarioYaReseno(usuarioId, productoId)) {
            return ResponseEntity.badRequest().build();
        }

        ProductoDTO productoDTO = productoService.findById(productoId);
        UsuarioDTO usuarioDTO = usuarioService.findById(usuarioId);

        if (productoDTO == null || usuarioDTO == null) {
            return ResponseEntity.badRequest().build();
        }

        Resena resena = new Resena();
        resena.setEstrellas(estrellas);
        resena.setComentario(comentario);

        Resena saved = resenaService.guardar(resena, productoId, usuarioId);
        
        emailService.enviarNotificacionNuevaResena(usuarioDTO.getNombre(), productoDTO.getNombre(), estrellas, comentario);
        
        return ResponseEntity.ok(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/usuario/{usuarioId}")
    public ResponseEntity<List<Resena>> getByUsuario(@PathVariable Long usuarioId) {
        return ResponseEntity.ok(resenaService.findByUsuarioId(usuarioId));
    }
}