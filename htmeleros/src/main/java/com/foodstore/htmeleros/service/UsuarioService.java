package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.dto.UsuarioDTO;
import java.util.List;

public interface UsuarioService {
    UsuarioDTO save(UsuarioDTO usuarioDTO);
    List<UsuarioDTO> findAll();
    UsuarioDTO findById(Long id);
    void deleteById(Long id);
    UsuarioDTO update(Long id, UsuarioDTO nuevo);
    UsuarioDTO findByEmail(String email);
    UsuarioDTO login(String email, String contraseniaPlano);
}
