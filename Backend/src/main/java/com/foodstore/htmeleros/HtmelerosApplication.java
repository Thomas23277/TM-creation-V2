package com.foodstore.htmeleros;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;
import com.foodstore.htmeleros.entity.Usuario;
import com.foodstore.htmeleros.repository.UsuarioRepository;
import com.foodstore.htmeleros.enums.Rol;
import com.foodstore.htmeleros.auth.util.Sha256Util;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@SpringBootApplication
@ComponentScan(basePackages = {
        "com.foodstore.htmeleros",
        "com.foodstore.htmeleros.auth",
        "com.foodstore.htmeleros.auth.controller",
        "com.foodstore.htmeleros.auth.service",
        "com.foodstore.htmeleros.auth.config"
})
@EnableScheduling
public class HtmelerosApplication {

    public static void main(String[] args) {
        SpringApplication.run(HtmelerosApplication.class, args);
    }

    /**
     * Crea un usuario admin por defecto si no existe.
     * Email: user@admin.com
     * Password: Admin123
     */
    @Bean
    public CommandLineRunner createDefaultAdmin(UsuarioRepository usuarioRepository) {
        return args -> {
            final String adminEmail = "user@admin.com";
            final String adminPass = "Admin123";

            if (!usuarioRepository.existsByEmail(adminEmail)) {
                Usuario admin = new Usuario();
                admin.setNombre("Admin");
                admin.setApellido("System"); // opcional
                admin.setEmail(adminEmail);
                admin.setContrasenia(Sha256Util.hash(adminPass));
                admin.setRol(Rol.ADMIN);

                usuarioRepository.save(admin);
                System.out.println("[FoodStore] Admin creado: " + adminEmail + " / " + adminPass);
            } else {
                System.out.println("[FoodStore] Admin ya existe: " + adminEmail);
            }
        };
    }

    @Bean
    public CommandLineRunner migrateCelularColum(DataSource dataSource) {
        return args -> {
            try (Connection conn = dataSource.getConnection();
                 Statement stmt = conn.createStatement()) {
                stmt.execute("ALTER TABLE usuarios MODIFY COLUMN celular VARCHAR(50)");
                System.out.println("[Migration] Columna celular migrada a VARCHAR(50)");
            } catch (Exception e) {
                System.out.println("[Migration] Columna celular ya está en VARCHAR o no aplica: " + e.getMessage());
            }
        };
    }
}
