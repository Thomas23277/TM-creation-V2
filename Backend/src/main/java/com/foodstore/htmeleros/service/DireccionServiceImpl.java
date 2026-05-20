package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.dto.DireccionDTO;
import com.foodstore.htmeleros.entity.Direccion;
import com.foodstore.htmeleros.entity.Usuario;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.mappers.DireccionMapper;
import com.foodstore.htmeleros.repository.DireccionRepository;
import com.foodstore.htmeleros.repository.UsuarioRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DireccionServiceImpl implements DireccionService {

    private final DireccionRepository direccionRepository;
    private final UsuarioRepository usuarioRepository;

    public DireccionServiceImpl(DireccionRepository direccionRepository, UsuarioRepository usuarioRepository) {
        this.direccionRepository = direccionRepository;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    public List<DireccionDTO> findByUsuarioId(Long usuarioId) {
        return direccionRepository.findByUsuarioId(usuarioId)
                .stream()
                .map(DireccionMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public DireccionDTO create(Long usuarioId, DireccionDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (dto.isPrincipal()) {
            clearPrincipalFlag(usuarioId);
        }

        Direccion direccion = DireccionMapper.toEntity(dto, usuario);
        return DireccionMapper.toDTO(direccionRepository.save(direccion));
    }

    @Override
    @Transactional
    public DireccionDTO update(Long usuarioId, Long direccionId, DireccionDTO dto) {
        Direccion existing = direccionRepository.findByIdAndUsuarioId(direccionId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion no encontrada"));

        if (dto.isPrincipal() && !existing.isPrincipal()) {
            clearPrincipalFlag(usuarioId);
        }

        existing.setAlias(dto.getAlias());
        existing.setCalle(dto.getCalle());
        existing.setNumero(dto.getNumero());
        existing.setCiudad(dto.getCiudad());
        existing.setProvincia(dto.getProvincia());
        existing.setCodigoPostal(dto.getCodigoPostal());
        existing.setPrincipal(dto.isPrincipal());

        return DireccionMapper.toDTO(direccionRepository.save(existing));
    }

    @Override
    @Transactional
    public void delete(Long usuarioId, Long direccionId) {
        Direccion existing = direccionRepository.findByIdAndUsuarioId(direccionId, usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Direccion no encontrada"));
        direccionRepository.delete(existing);
    }

    private void clearPrincipalFlag(Long usuarioId) {
        List<Direccion> direcciones = direccionRepository.findByUsuarioId(usuarioId);
        for (Direccion d : direcciones) {
            if (d.isPrincipal()) {
                d.setPrincipal(false);
                direccionRepository.save(d);
            }
        }
    }
}
