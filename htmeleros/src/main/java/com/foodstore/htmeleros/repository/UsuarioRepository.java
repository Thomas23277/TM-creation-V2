package com.foodstore.htmeleros.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.foodstore.htmeleros.entity.Usuario;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByEmail(String email);
    boolean existsByEmail(String email);
}
