package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.dto.CategoriaDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoriaService {

    CategoriaDTO save(CategoriaDTO dto, MultipartFile imagen);

    CategoriaDTO update(Long id, CategoriaDTO dto, MultipartFile imagen);

    CategoriaDTO findById(Long id);

    List<CategoriaDTO> findAll();

    void deleteById(Long id);
}