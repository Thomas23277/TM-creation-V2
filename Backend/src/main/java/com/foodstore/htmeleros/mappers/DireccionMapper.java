package com.foodstore.htmeleros.mappers;

import com.foodstore.htmeleros.dto.DireccionDTO;
import com.foodstore.htmeleros.entity.Direccion;

public class DireccionMapper {

    public static DireccionDTO toDTO(Direccion d) {
        if (d == null) return null;
        DireccionDTO dto = new DireccionDTO();
        dto.setId(d.getId());
        dto.setAlias(d.getAlias());
        dto.setCalle(d.getCalle());
        dto.setNumero(d.getNumero());
        dto.setCiudad(d.getCiudad());
        dto.setProvincia(d.getProvincia());
        dto.setCodigoPostal(d.getCodigoPostal());
        dto.setPrincipal(d.isPrincipal());
        return dto;
    }

    public static Direccion toEntity(DireccionDTO dto, com.foodstore.htmeleros.entity.Usuario usuario) {
        if (dto == null) return null;
        Direccion d = new Direccion();
        d.setId(dto.getId());
        d.setAlias(dto.getAlias());
        d.setCalle(dto.getCalle());
        d.setNumero(dto.getNumero());
        d.setCiudad(dto.getCiudad());
        d.setProvincia(dto.getProvincia());
        d.setCodigoPostal(dto.getCodigoPostal());
        d.setPrincipal(dto.isPrincipal());
        d.setUsuario(usuario);
        return d;
    }
}
