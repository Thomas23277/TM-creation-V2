package com.foodstore.htmeleros.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.foodstore.htmeleros.dto.EtiquetaDTO;
import com.foodstore.htmeleros.entity.Etiqueta;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.repository.EtiquetaRepository;

@Service
@Transactional
public class EtiquetaService {

    private final EtiquetaRepository etiquetaRepository;

    public EtiquetaService(EtiquetaRepository etiquetaRepository) {
        this.etiquetaRepository = etiquetaRepository;
    }

    @Transactional(readOnly = true)
    public List<EtiquetaDTO> findAll() {
        return etiquetaRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public EtiquetaDTO findById(Long id) {
        return toDTO(etiquetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada")));
    }

    public EtiquetaDTO create(EtiquetaDTO dto) {
        if (etiquetaRepository.existsByNombre(dto.getNombre())) {
            throw new IllegalArgumentException("Ya existe una etiqueta con ese nombre");
        }
        Etiqueta e = toEntity(dto);
        return toDTO(etiquetaRepository.save(e));
    }

    public EtiquetaDTO update(Long id, EtiquetaDTO dto) {
        Etiqueta e = etiquetaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Etiqueta no encontrada"));
        e.setNombre(dto.getNombre());
        e.setColorHex(dto.getColorHex());
        e.setVisible(dto.isVisible());
        e.setInterna(dto.isInterna());
        return toDTO(etiquetaRepository.save(e));
    }

    public void delete(Long id) {
        if (!etiquetaRepository.existsById(id)) {
            throw new ResourceNotFoundException("Etiqueta no encontrada");
        }
        etiquetaRepository.deleteById(id);
    }

    private EtiquetaDTO toDTO(Etiqueta e) {
        return new EtiquetaDTO(e.getId(), e.getNombre(), e.getColorHex(), e.isVisible(), e.isInterna());
    }

    private Etiqueta toEntity(EtiquetaDTO dto) {
        Etiqueta e = new Etiqueta();
        e.setNombre(dto.getNombre());
        e.setColorHex(dto.getColorHex());
        e.setVisible(dto.isVisible());
        e.setInterna(dto.isInterna());
        return e;
    }
}
