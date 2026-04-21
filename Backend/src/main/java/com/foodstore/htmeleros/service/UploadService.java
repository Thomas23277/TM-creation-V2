package com.foodstore.htmeleros.service;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Service
public class UploadService {

    private final Cloudinary cloudinary;

    public UploadService(
            @Value("${cloudinary.cloud.name}") String cloudName,
            @Value("${cloudinary.api.key}") String apiKey,
            @Value("${cloudinary.api.secret}") String apiSecret) {

        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String uploadImage(MultipartFile file, String folder) {
        try {
            String publicId = folder + "_" + UUID.randomUUID().toString();

            Map params = ObjectUtils.asMap(
                    "public_id", publicId,
                    "overwrite", true,
                    "folder", "tmcreation/" + folder
            );

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);

            return (String) uploadResult.get("secure_url");
        } catch (IOException e) {
            throw new RuntimeException("Error uploading to Cloudinary: " + e.getMessage(), e);
        }
    }

    public String uploadCategoriaImage(MultipartFile file) {
        return uploadImage(file, "categorias");
    }

    public String uploadProductoImage(MultipartFile file) {
        return uploadImage(file, "productos");
    }

    public void deleteImage(String imageUrl) {
        if (imageUrl == null || imageUrl.isEmpty()) return;

        try {
            String publicId = extractPublicId(imageUrl);
            if (publicId != null) {
                cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            }
        } catch (Exception e) {
            System.err.println("Error deleting from Cloudinary: " + e.getMessage());
        }
    }

    private String extractPublicId(String imageUrl) {
        if (imageUrl == null || !imageUrl.contains("cloudinary")) {
            return null;
        }

        int uploadIndex = imageUrl.indexOf("/upload/");
        if (uploadIndex == -1) return null;

        String path = imageUrl.substring(uploadIndex + 8);
        path = path.replaceFirst("/v\\d+/", "/");
        path = path.replaceFirst("\\.[^.]+$", "");

        return path;
    }
}