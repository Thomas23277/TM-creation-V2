package com.foodstore.htmeleros.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
@Table(name = "productos")
public class Producto {

 @Id
 @GeneratedValue(strategy = GenerationType.IDENTITY)
 private Long id;

 @Column(unique = true, nullable = false)
 private String nombre;

 @Column(nullable = false)
 private double precio;

 @Column(nullable = false)
 private int stock;

 @Column(length = 1000)
 private String descripcion;

 @Column(name = "url_imagen")
 private String urlImagen;

 @Transient
 private Double promedioResenas;

 @Transient
 private Long totalResenas;

 // 🔥 Este campo ya está perfecto para el Toggle Switch del Admin
 @Column(nullable = false)
 private boolean disponible = true;

 @Column(name = "colores_activo", nullable = false)
 private boolean coloresActivo = false;

 @Column(name = "stock_control", nullable = false)
 private boolean stockControl = true;

 @ManyToOne(fetch = FetchType.EAGER)
 @JoinColumn(name = "categoria_id", referencedColumnName = "id", nullable = false)
 private Categoria categoria;

 @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
 @OrderBy("orden ASC")
 private List<ProductoImagen> imagenes = new ArrayList<>();

 @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
 private List<Variante> variantes = new ArrayList<>();

 @OneToMany(mappedBy = "producto", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
 private List<ColorDisponible> colores = new ArrayList<>();

 @ManyToMany(fetch = FetchType.LAZY)
 @JoinTable(
  name = "producto_etiqueta",
  joinColumns = @JoinColumn(name = "producto_id"),
  inverseJoinColumns = @JoinColumn(name = "etiqueta_id")
 )
 private Set<Etiqueta> etiquetas = new HashSet<>();

 // ============================================================
 // LÓGICA DE NEGOCIO (STOCK)
 // ============================================================

  public void reducirStock(int cantidad) {
   if (cantidad <= 0) {
    throw new IllegalArgumentException("La cantidad a reducir debe ser mayor a 0");
   }
   if (stockControl && this.stock < cantidad) {
    throw new IllegalStateException("Stock insuficiente para el producto: " + this.nombre);
   }
   this.stock -= cantidad;
  }

 public void aumentarStock(int cantidad) {
  if (cantidad <= 0) {
   throw new IllegalArgumentException("La cantidad a aumentar debe ser mayor a 0");
  }
  this.stock += cantidad;
 }

 // ============================================================
 // VALIDACIONES AUTOMÁTICAS
 // ============================================================

 @PrePersist
 @PreUpdate
 public void validarConsistencia() {
  if (stockControl && this.stock < 0) {
   throw new IllegalStateException("El stock no puede ser un valor negativo");
  }
  if (this.precio < 0) {
   throw new IllegalStateException("El precio no puede ser un valor negativo");
  }
  // Si el stock llega a 0, podríamos forzar disponible a false,
  // pero es mejor dejarlo en manos del Admin para cuando reponga.
 }
}