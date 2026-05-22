package com.foodstore.htmeleros.auth.service;

import com.foodstore.htmeleros.auth.dto.RegisterRequest;
import com.foodstore.htmeleros.auth.dto.UserResponse;
import com.foodstore.htmeleros.entity.Usuario;
import com.foodstore.htmeleros.enums.Rol;
import com.foodstore.htmeleros.repository.UsuarioRepository;
import com.foodstore.htmeleros.security.CustomUserDetails;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       UserDetailsService userDetailsService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.userDetailsService = userDetailsService;
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

        String nombreIngresado = req.getNombre().trim();
        String nombreNormalizado = nombreIngresado.toLowerCase().replace("✅", "").replace("✅", "").replace("✔", "").replace("✔", "").replace("✓", "").replace("✓", "");
        if (nombreNormalizado.equals("tmcreationoficial") || nombreNormalizado.contains("tmcreation oficial") || nombreNormalizado.contains("tmcreationoficial")) {
            throw new IllegalArgumentException("Nombre de usuario reservado. Elegí otro.");
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
                saved.getRol().name(),
                saved.getFotoPerfil()
        );
    }

    // ===============================
    // LOGIN TRADICIONAL
    // ===============================

    public UserResponse login(String email, String password) {

        try {

            String emailNormalizado = email.trim().toLowerCase();

            UserDetails userDetails = userDetailsService.loadUserByUsername(emailNormalizado);

            if (!passwordEncoder.matches(password, userDetails.getPassword())) {
                throw new IllegalArgumentException("Credenciales inválidas");
            }

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities()
                    );

            SecurityContextHolder.getContext().setAuthentication(auth);

            CustomUserDetails customUserDetails =
                    (CustomUserDetails) userDetails;

            Usuario usuario = customUserDetails.getUsuario();

            return new UserResponse(
                    usuario.getId(),
                    usuario.getNombre(),
                    usuario.getApellido(),
                    usuario.getEmail(),
                    usuario.getRol().name(),
                    usuario.getFotoPerfil()
            );

        } catch (IllegalArgumentException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
    }
}
