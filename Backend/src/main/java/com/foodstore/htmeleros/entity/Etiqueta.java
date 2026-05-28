package com.foodstore.htmeleros.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "etiquetas")
public class Etiqueta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String nombre;

    @Column(length = 7)
    private String colorHex;

    @Column(nullable = false)
    private boolean visible = true;

    @Column(nullable = false)
    private boolean interna = false;

    public Etiqueta() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isInterna() { return interna; }
    public void setInterna(boolean interna) { this.interna = interna; }
}
