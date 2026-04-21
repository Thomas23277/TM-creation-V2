package com.foodstore.htmeleros.auth.service;

import com.foodstore.htmeleros.auth.dto.RegisterRequest;
import com.foodstore.htmeleros.auth.dto.UserResponse;
import com.foodstore.htmeleros.entity.Usuario;
import com.foodstore.htmeleros.enums.Rol;
import com.foodstore.htmeleros.repository.UsuarioRepository;
import com.foodstore.htmeleros.security.CustomUserDetails;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UsuarioRepository usuarioRepository,
                       AuthenticationManager authenticationManager,
                       PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
        this.passwordEncoder = passwordEncoder;
    }

    // ===============================
    // REGISTRO
    // ===============================

    @Transactional
    public UserResponse register(RegisterRequest req) {

        String emailNormalizado = req.getEmail().trim().toLowerCase();

        if (usuarioRepository.existsByEmail(emailNormalizado)) {
            throw new IllegalArgumentException("Email ya registrado");
        }

        Usuario usuario = new Usuario();
        usuario.setNombre(req.getNombre().trim());
        usuario.setApellido(req.getApellido().trim());
        usuario.setEmail(emailNormalizado);
        usuario.setContrasenia(passwordEncoder.encode(req.getContrasenia()));
        usuario.setRol(req.getRol() != null ? Rol.valueOf(req.getRol()) : Rol.USUARIO);

        Usuario saved = usuarioRepository.save(usuario);

        return new UserResponse(
                saved.getId(),
                saved.getNombre(),
                saved.getApellido(),
                saved.getEmail(),
                saved.getRol().name()
        );
    }

    // ===============================
    // LOGIN TRADICIONAL
    // ===============================

    public UserResponse login(String email, String password) {

        try {

            String emailNormalizado = email.trim().toLowerCase();

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(emailNormalizado, password)
            );

            // 🔥 Esto es CLAVE para que Spring cree la sesión
            SecurityContextHolder.getContext().setAuthentication(authentication);

            CustomUserDetails userDetails =
                    (CustomUserDetails) authentication.getPrincipal();

            Usuario usuario = userDetails.getUsuario();

            return new UserResponse(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    usuario.getRol().name()
            );

        } catch (BadCredentialsException ex) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
    }
}
