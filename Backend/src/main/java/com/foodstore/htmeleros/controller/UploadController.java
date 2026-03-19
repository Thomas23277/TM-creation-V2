package com.foodstore.htmeleros.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    private final String UPLOAD_DIR = "uploads/otros/";

    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {

        File folder = new File(UPLOAD_DIR);
        if (!folder.exists()) folder.mkdirs();

        String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
        File dest = new File(UPLOAD_DIR + fileName);

        file.transferTo(dest);

        // URL pública
        String url = "/uploads/otros/" + fileName;

        return ResponseEntity.ok(url);
    }
}
