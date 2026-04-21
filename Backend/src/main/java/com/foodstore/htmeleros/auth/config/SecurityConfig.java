package com.foodstore.htmeleros.auth.config;

import com.foodstore.htmeleros.security.CustomOAuth2UserService;
import com.foodstore.htmeleros.security.CustomUserDetailsService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import org.springframework.http.HttpMethod;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Autowired
    private CustomOAuth2UserService customOAuth2UserService;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    /* ============================================================
       🔐 AUTHENTICATION MANAGER
    ============================================================ */

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /* ============================================================
       🔐 PASSWORD ENCODER
    ============================================================ */

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /* ============================================================
       🌍 CORS CONFIG (SESIONES + FRONT DESACOPLADO)
    ============================================================ */

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {

        CorsConfiguration config = new CorsConfiguration();

        // IMPORTANTE: necesario para JSESSIONID
        config.setAllowCredentials(true);

        // Frontend Vite + Producción (Netlify)
        config.setAllowedOrigins(List.of(
                "http://localhost:5173",
                "https://tmcreattion.netlify.app",
                "https://tmcreation.netlify.app",
                "https://tm-creation-v2.onrender.com"
        ));

        config.setAllowedMethods(
                List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")
        );

        config.setAllowedHeaders(List.of("*"));

        UrlBasedCorsConfigurationSource source =
                new UrlBasedCorsConfigurationSource();

        source.registerCorsConfiguration("/**", config);

        return source;
    }

    /* ============================================================
       🛡️ SECURITY FILTER CHAIN
    ============================================================ */

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                // CORS
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // CSRF deshabilitado (API REST con sesión controlada)
                .csrf(csrf -> csrf.disable())

                // 🔥 SESIÓN ACTIVA (NO STATELESS)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                // =====================================================
                // AUTORIZACIÓN
                // =====================================================
                .authorizeHttpRequests(auth -> auth

                        // 🔓 Recursos estáticos backend
                        .requestMatchers(
                                "/",
                                "/index.html",
                                "/assets/**",
                                "/favicon.ico",
                                "/*.js",
                                "/*.css",
                                "/*.png",
                                "/*.svg"
                        ).permitAll()

                        // 🔓 Imágenes subidas (CRÍTICO)
                        .requestMatchers("/uploads/**").permitAll()

                        // 🔓 Auth público
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/register",
                                "/oauth2/**",
                                "/login/**"
                        ).permitAll()

                        // 🔓 Lectura pública de productos, categorías, promociones y reseñas
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/productos/**",
                                "/api/categoria/**",
                                "/api/promociones/**",
                                "/api/resenas/**"
                        ).permitAll()

                        // 🔓 Crear reseñas y promociones (requiere autenticación)
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/resenas/**",
                                "/api/promociones/**"
                        ).authenticated()

                        // =================================================
                        // 👤 USER AUTENTICADO
                        // =================================================

                        .requestMatchers("/api/auth/me").authenticated()

                        .requestMatchers(HttpMethod.POST, "/api/pedidos")
                        .authenticated()

                        .requestMatchers(HttpMethod.GET, "/api/pedidos/mis-pedidos")
                        .authenticated()

                        // =================================================
                        // 👑 ADMIN
                        // =================================================

                        // Protegemos POST, PUT, DELETE de categorias y productos
                        .requestMatchers("/api/categoria/**", "/api/productos/**")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/api/pedidos")
                        .hasRole("ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/pedidos/*/estado")
                        .hasRole("ADMIN")

                        .requestMatchers("/api/admin/**")
                        .hasRole("ADMIN")

                        // Cualquier otra petición requiere autenticación
                        .anyRequest().authenticated()
                )

                // UserDetails
                .userDetailsService(customUserDetailsService)

                // =====================================================
                // 🔵 OAUTH2 LOGIN (GOOGLE)
                // =====================================================
                .oauth2Login(oauth -> oauth
                        .userInfoEndpoint(userInfo ->
                                userInfo.userService(customOAuth2UserService)
                        )
                        .defaultSuccessUrl("http://localhost:5173/", true)
                )

                // =====================================================
                // 🔴 LOGOUT
                // =====================================================
                .logout(logout -> logout
                        .logoutUrl("/api/auth/logout")
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setStatus(200);
                        })
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )

                // =====================================================
                // ❌ MANEJO DE 401
                // =====================================================
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.setStatus(401);
                            response.getWriter().write("No autenticado");
                        })
                );

        return http.build();
    }
}