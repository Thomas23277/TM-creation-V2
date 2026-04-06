package com.foodstore.htmeleros.controller;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final Cloudinary cloudinary;

    public UploadController(
            @Value("${cloudinary.cloud_name}") String cloudName,
            @Value("${cloudinary.api_key}") String apiKey,
            @Value("${cloudinary.api_secret}") String apiSecret
    ) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret,
                "secure", true
        ));
    }

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            // Usamos comodines <?, ?> para evitar la advertencia "Raw use of Map"
            Map<?, ?> uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.asMap(
                    "folder", "tmcreation_productos"
            ));

            String url = uploadResult.get("secure_url").toString();

            return ResponseEntity.ok(url);

        } catch (IOException e) {
            // Reemplazamos printStackTrace() por un log de consola más limpio
            System.err.println("Error al subir imagen a Cloudinary: " + e.getMessage());
            return ResponseEntity.internalServerError().body("Error al subir la imagen a Cloudinary");
        }
    }
}