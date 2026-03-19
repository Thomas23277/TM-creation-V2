package com.foodstore.htmeleros.security;

import com.foodstore.htmeleros.entity.Usuario;
import com.foodstore.htmeleros.enums.Rol;
import com.foodstore.htmeleros.repository.UsuarioRepository;
import com.foodstore.htmeleros.auth.util.Sha256Util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest)
            throws OAuth2AuthenticationException {

        OAuth2User oAuth2User = super.loadUser(userRequest);

        String email = oAuth2User.getAttribute("email");
        String givenName = oAuth2User.getAttribute("given_name");
        String familyName = oAuth2User.getAttribute("family_name");
        String fullName = oAuth2User.getAttribute("name");

        if (email == null) {
            throw new OAuth2AuthenticationException("Email no encontrado en Google");
        }

        Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

        // ==============================
        // 🆕 SI EL USUARIO NO EXISTE
        // ==============================
        if (usuarioOptional.isEmpty()) {

            Usuario nuevoUsuario = new Usuario();
            nuevoUsuario.setEmail(email);

            // Prioridad para nombre
            if (givenName != null && !givenName.isBlank()) {
                nuevoUsuario.setNombre(givenName);
            } else if (fullName != null && !fullName.isBlank()) {
                nuevoUsuario.setNombre(fullName);
            } else {
                nuevoUsuario.setNombre("Usuario");
            }

            nuevoUsuario.setApellido(familyName);
            nuevoUsuario.setRol(Rol.USUARIO);
            nuevoUsuario.setCelular(0);

            String randomPassword = UUID.randomUUID().toString();
            nuevoUsuario.setContrasenia(Sha256Util.hash(randomPassword));

            usuarioRepository.save(nuevoUsuario);
        }

        // ==========================================
        // 🔄 SI YA EXISTE PERO NOMBRE ESTÁ MAL
        // ==========================================
        else {

            Usuario usuario = usuarioOptional.get();

            if (usuario.getNombre() == null
                    || usuario.getNombre().isBlank()
                    || usuario.getNombre().contains("@")) {

                if (givenName != null && !givenName.isBlank()) {
                    usuario.setNombre(givenName);
                } else if (fullName != null && !fullName.isBlank()) {
                    usuario.setNombre(fullName);
                }

                usuarioRepository.save(usuario);
            }
        }

        return oAuth2User;
    }
}
