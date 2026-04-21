package com.foodstore.htmeleros.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "promociones")
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(length = 500)
    private String descripcion;

    @Column(name = "url_imagen", nullable = false)
    private String urlImagen;

    @Column(name = "url_enlace")
    private String urlEnlace;

    @Column(nullable = false)
    private boolean activo = true;

    @Column(name = "orden")
    private int orden = 0;
}