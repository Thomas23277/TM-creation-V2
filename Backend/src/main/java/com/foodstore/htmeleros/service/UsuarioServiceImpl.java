package com.foodstore.htmeleros.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.foodstore.htmeleros.entity.Usuario;
import com.foodstore.htmeleros.dto.UsuarioDTO;

import com.foodstore.htmeleros.enums.Rol;
import com.foodstore.htmeleros.exception.ResourceNotFoundException;
import com.foodstore.htmeleros.mappers.UsuarioMapper;
import com.foodstore.htmeleros.repository.UsuarioRepository;
import com.foodstore.htmeleros.auth.util.Sha256Util;

@Service
public class UsuarioServiceImpl implements UsuarioService {


    @Autowired
    private UsuarioRepository usuariorepository;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository) {
    }


    @Override
    public UsuarioDTO save(UsuarioDTO usuarioDTO) {
        // Validar unicidad de email
        if (usuariorepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new IllegalArgumentException("El email ya está registrado");
        }

        Usuario usuario = UsuarioMapper.toEntity(usuarioDTO);

        // Rol por defecto: usuario
        Rol rolFinal = usuarioDTO.getRol() != null ? usuarioDTO.getRol() : Rol.USUARIO;
        usuario.setRol(rolFinal);

        // Hash de contraseña
        usuario.setContrasenia(Sha256Util.hash(usuarioDTO.getContrasenia()));

        Usuario guardado = usuariorepository.save(usuario);
        return UsuarioMapper.toDTO(guardado);
    }

    @Override
    public List<UsuarioDTO> findAll() {
        return usuariorepository.findAll().stream()
                .map(UsuarioMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UsuarioDTO findById(Long id) {
        Usuario usuario = usuariorepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));
        return UsuarioMapper.toDTO(usuario);
    }

    @Override
    public void deleteById(Long id) {
        usuariorepository.deleteById(id);
    }

    @Override
    public UsuarioDTO update(Long id, UsuarioDTO nuevo) {
        Usuario actual = usuariorepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        actual.setNombre(nuevo.getNombre());
        actual.setApellido(nuevo.getApellido());
        actual.setEmail(nuevo.getEmail());
        actual.setCelular(nuevo.getCelular());

        // Si viene nueva contraseña, la hasheamos
        if (nuevo.getContrasenia() != null && !nuevo.getContrasenia().isBlank()) {
            actual.setContrasenia(Sha256Util.hash(nuevo.getContrasenia()));
        }

        // Si no viene rol, mantenemos el actual
        actual.setRol(nuevo.getRol() != null ? nuevo.getRol() : actual.getRol());

        Usuario actualizado = usuariorepository.save(actual);
        return UsuarioMapper.toDTO(actualizado);
    }

    @Override
    public UsuarioDTO findByEmail(String email) {
        Usuario usuario = usuariorepository.findByEmail(email).orElse(null);
        return UsuarioMapper.toDTO(usuario);
    }

    // Nuevo: login con comparación de hash
    @Override
    public UsuarioDTO login(String email, String contraseniaPlano) {
        Usuario usuario = usuariorepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Credenciales inválidas"));

        String hash = Sha256Util.hash(contraseniaPlano);
        if (!hash.equals(usuario.getContrasenia())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        return UsuarioMapper.toDTO(usuario);
    }
}




