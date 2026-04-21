package com.foodstore.htmeleros.controller;

import com.foodstore.htmeleros.service.UploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final UploadService uploadService;

    public UploadController(UploadService uploadService) {
        this.uploadService = uploadService;
    }

    @PostMapping("/categoria")
    public ResponseEntity<String> uploadCategoria(@RequestParam("file") MultipartFile file) {
        String url = uploadService.uploadCategoriaImage(file);
        return ResponseEntity.ok(url);
    }

    @PostMapping("/producto")
    public ResponseEntity<String> uploadProducto(@RequestParam("file") MultipartFile file) {
        String url = uploadService.uploadProductoImage(file);
        return ResponseEntity.ok(url);
    }

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        String url = uploadService.uploadImage(file, "otros");
        return ResponseEntity.ok(url);
    }
}