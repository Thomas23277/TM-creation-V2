package com.foodstore.htmeleros.entity;

import com.foodstore.htmeleros.enums.Rol;
import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = true)
    private String apellido;

    // Celular opcional (no se pide en register)
    @Column(nullable = true)
    private int celular;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String contrasenia; // almacenada con SHA-256

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rol rol;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Pedido> pedidos;
}

