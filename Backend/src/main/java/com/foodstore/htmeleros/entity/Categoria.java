package com.foodstore.htmeleros.entity;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.annotation.JsonIgnore;
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
@Table(name = "categorias") // 👈 PLURAL: Coincide con la tabla que dejamos en MySQL
public class Categoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nombre", unique = true, nullable = false, length = 100)
    private String nombre;

    @Column(name = "disponible", nullable = false)
    private boolean disponible = true;

    @Column(name = "url_imagen", length = 500)
    private String urlImagen;

    @JsonIgnore
    @OneToMany(
            mappedBy = "categoria",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<Producto> productos = new ArrayList<>();
}