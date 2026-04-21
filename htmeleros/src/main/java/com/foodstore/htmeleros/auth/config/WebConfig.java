package com.foodstore.htmeleros.auth.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {

        // 📂 Carpeta uploads en la raíz del proyecto
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();

        try {
            // Crear carpeta si no existe
            if (!Files.exists(uploadDir)) {
                Files.createDirectories(uploadDir);
                System.out.println("📁 Carpeta 'uploads' creada automáticamente.");
            }
        } catch (IOException e) {
            throw new RuntimeException("❌ No se pudo crear la carpeta uploads", e);
        }

        // Spring Boot EXIGE que las rutas de directorios terminen en "/"
        String uploadPath = uploadDir.toUri().toString();
        if (!uploadPath.endsWith("/")) {
            uploadPath += "/";
        }

        System.out.println("📂 Servir imágenes desde:");
        System.out.println("➡ " + uploadPath);

        registry.addResourceHandler("/uploads/**")
                .addResourceLocations(uploadPath);
    }
}