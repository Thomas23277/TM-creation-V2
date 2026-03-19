package com.foodstore.htmeleros.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
public class UploadService {

    private final String CATEGORY_DIR = "uploads/categorias";

    public String saveCategoriaImage(MultipartFile file) {
        try {
            File dir = new File(CATEGORY_DIR);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Path.of(CATEGORY_DIR, fileName);

            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return CATEGORY_DIR + "/" + fileName;

        } catch (Exception e) {
            throw new RuntimeException("Error guardando imagen de categoría", e);
        }
    }
}
