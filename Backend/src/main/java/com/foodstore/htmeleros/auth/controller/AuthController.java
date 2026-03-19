package com.foodstore.htmeleros.auth.controller;

import com.foodstore.htmeleros.auth.dto.LoginRequest;
import com.foodstore.htmeleros.auth.dto.RegisterRequest;
import com.foodstore.htmeleros.auth.dto.UserResponse;
import com.foodstore.htmeleros.auth.service.AuthService;
import com.foodstore.htmeleros.entity.Usuario;
import com.foodstore.htmeleros.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;
    private final AuthenticationManager authenticationManager;

    public AuthController(AuthService authService,
                          UsuarioRepository usuarioRepository,
                          AuthenticationManager authenticationManager) {
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
        this.authenticationManager = authenticationManager;
    }

    // ==========================
    // 📝 REGISTER
    // ==========================
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {

        try {
            UserResponse user = authService.register(request);
            return ResponseEntity.status(201).body(user);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(
                    Map.of("message", e.getMessage())
            );

        } catch (Exception e) {
            return ResponseEntity.internalServerError().body(
                    Map.of("message", "Server error")
            );
        }
    }

    // ==========================
    // 🔐 LOGIN CORREGIDO REAL
    // ==========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {

        try {

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail().trim().toLowerCase(),
                            request.getContrasenia()
                    )
            );

            // 🔥 Crear contexto limpio
            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(authentication);

            // 🔥 Guardarlo en el holder
            SecurityContextHolder.setContext(context);

            // 🔥 Crear sesión
            HttpSession session = httpRequest.getSession(true);

            // 🔥 GUARDAR CONTEXTO EN SESIÓN (ESTO ES LO QUE FALTABA)
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context
            );

            Optional<Usuario> usuarioOptional =
                    usuarioRepository.findByEmail(
                            request.getEmail().trim().toLowerCase()
                    );

            if (usuarioOptional.isEmpty()) {
                return ResponseEntity.status(401).body(
                        Map.of("message", "Usuario no encontrado")
                );
            }

            Usuario usuario = usuarioOptional.get();

            return ResponseEntity.ok(Map.of(
                    "id", usuario.getId(),
                    "email", usuario.getEmail(),
                    "nombre", usuario.getNombre(),
                    "rol", usuario.getRol().name()
            ));

        } catch (Exception e) {
            return ResponseEntity.status(401).body(
                    Map.of("message", "Credenciales inválidas")
            );
        }
    }

    // ==========================
    // 👤 ME
    // ==========================
    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication authentication) {

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body(
                    Map.of("authenticated", false)
            );
        }

        String email;

        if (authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            email = oAuth2User.getAttribute("email");
        } else {
            email = authentication.getName();
        }

        if (email == null) {
            return ResponseEntity.status(401).body(
                    Map.of("authenticated", false)
            );
        }

        Optional<Usuario> usuarioOptional =
                usuarioRepository.findByEmail(email.trim().toLowerCase());

        if (usuarioOptional.isEmpty()) {
            return ResponseEntity.status(401).body(
                    Map.of("authenticated", false)
            );
        }

        Usuario usuario = usuarioOptional.get();

        return ResponseEntity.ok(Map.of(
                "authenticated", true,
                "id", usuario.getId(),
                "email", usuario.getEmail(),
                "nombre", usuario.getNombre(),
                "rol", usuario.getRol().name()
        ));
    }

    // ==========================
    // 🚪 LOGOUT
    // ==========================
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {

        HttpSession session = request.getSession(false);

        if (session != null) {
            session.invalidate();
        }

        SecurityContextHolder.clearContext();

        return ResponseEntity.ok(
                Map.of(
                        "message", "Sesión cerrada correctamente",
                        "authenticated", false
                )
        );
    }
}
