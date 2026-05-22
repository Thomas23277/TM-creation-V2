package com.foodstore.htmeleros.auth.controller;

import com.foodstore.htmeleros.auth.dto.LoginRequest;
import com.foodstore.htmeleros.auth.dto.RegisterRequest;
import com.foodstore.htmeleros.auth.dto.UserResponse;
import com.foodstore.htmeleros.auth.service.AuthService;
import com.foodstore.htmeleros.auth.util.Sha256Util;
import com.foodstore.htmeleros.entity.Usuario;
import com.foodstore.htmeleros.repository.UsuarioRepository;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UsuarioRepository usuarioRepository;

    public AuthController(AuthService authService,
                          UsuarioRepository usuarioRepository) {
        this.authService = authService;
        this.usuarioRepository = usuarioRepository;
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
    // 🔐 LOGIN (ULTRA SIMPLE)
    // ==========================
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request,
                                   HttpServletRequest httpRequest) {

        try {
            String email = request.getEmail().trim().toLowerCase();
            String password = request.getContrasenia();

            Optional<Usuario> usuarioOptional = usuarioRepository.findByEmail(email);

            if (usuarioOptional.isEmpty()) {
                return ResponseEntity.status(401).body(
                        Map.of("message", "Credenciales inválidas", "code", "USER_NOT_FOUND")
                );
            }

            Usuario usuario = usuarioOptional.get();

            String storedHash = usuario.getContrasenia();
            boolean esBcrypt = storedHash != null && storedHash.startsWith("$2");
            boolean passwordOk;

            if (esBcrypt) {
                passwordOk = new BCryptPasswordEncoder().matches(password, storedHash);
            } else {
                String hash = Sha256Util.hash(password);
                passwordOk = hash.equals(storedHash);
            }

            if (!passwordOk) {
                return ResponseEntity.status(401).body(
                        Map.of("message", "Credenciales inválidas", "code", "WRONG_PASSWORD")
                );
            }

            String role = "ROLE_" + usuario.getRol().name();

            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(
                            email, null, List.of(new SimpleGrantedAuthority(role))
                    );

            SecurityContext context = SecurityContextHolder.createEmptyContext();
            context.setAuthentication(auth);
            SecurityContextHolder.setContext(context);

            HttpSession session = httpRequest.getSession(true);
            session.setAttribute(
                    HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY,
                    context
            );

            Map<String, Object> response = new HashMap<>();
            response.put("id", usuario.getId());
            response.put("email", usuario.getEmail());
            response.put("nombre", usuario.getNombre());
            response.put("rol", usuario.getRol().name());
            response.put("fotoPerfil", usuario.getFotoPerfil());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            System.out.println("===== LOGIN ERROR =====");
            System.out.println(e.getClass().getName() + ": " + e.getMessage());
            for (StackTraceElement el : e.getStackTrace()) {
                if (el.getClassName().contains("foodstore")) {
                    System.out.println("  at " + el);
                }
            }
            return ResponseEntity.status(500).body(
                    Map.of("message", "Error interno del servidor", "code", "INTERNAL_ERROR")
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

        Map<String, Object> userData = new HashMap<>();
        userData.put("authenticated", true);
        userData.put("id", usuario.getId());
        userData.put("email", usuario.getEmail());
        userData.put("nombre", usuario.getNombre());
        userData.put("rol", usuario.getRol().name());
        userData.put("fotoPerfil", usuario.getFotoPerfil());

        return ResponseEntity.ok(userData);
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
