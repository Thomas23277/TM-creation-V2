package com.foodstore.htmeleros.mappers;

import com.foodstore.htmeleros.dto.UsuarioDTO;
import com.foodstore.htmeleros.entity.Usuario;

public class UsuarioMapper {

    public static Usuario toEntity(UsuarioDTO dto) {
        if (dto == null) {
            return null;
        }

        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setCelular(dto.getCelular());
        usuario.setContrasenia(dto.getContrasenia());
        usuario.setRol(dto.getRol());

        return usuario;
    }

    public static UsuarioDTO toDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        UsuarioDTO dto = new UsuarioDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setApellido(usuario.getApellido());
        dto.setEmail(usuario.getEmail());
        dto.setCelular(usuario.getCelular());
        dto.setContrasenia(usuario.getContrasenia());
        dto.setRol(usuario.getRol());

        return dto;
    }
}
