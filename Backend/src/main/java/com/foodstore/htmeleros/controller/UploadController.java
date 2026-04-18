package com.foodstore.htmeleros.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
// Agregamos CrossOrigin para evitar bloqueos del navegador si pruebas desde otro dominio
@CrossOrigin(origins = "*")
public class UploadController {

    private final Cloudinary cloudinary;

    public UploadController(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        // Inicialización de Cloudinary con las variables de entorno
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @PostMapping
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        // 1. Validación de seguridad: ¿El archivo está vacío?
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("No se ha seleccionado ningún archivo para subir.");
        }

        try {
            // 2. Subida a Cloudinary
            // Enviamos los bytes directamente para evitar crear archivos temporales en el disco de Render
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "tmcreation_productos",
                    "resource_type", "auto" // Detecta si es imagen, video, etc. automáticamente
            ));

            // 3. Extraemos la URL segura
            String url = uploadResult.get("secure_url").toString();

            // Devolvemos la URL en un pequeño mapa JSON para que el frontend la lea fácilmente
            return ResponseEntity.ok(Map.of("url", url));

        } catch (IOException e) {
            System.err.println("Error crítico al subir a Cloudinary: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al procesar la subida: " + e.getMessage());
        }
    }
}