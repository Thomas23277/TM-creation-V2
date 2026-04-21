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

 // Usamos EAGER para que al listar productos en el Home/Admin
 // la categoría cargue siempre sin errores de sesión de Hibernate
 @ManyToOne(fetch = FetchType.EAGER)
 @JoinColumn(name = "categoria_id", referencedColumnName = "id", nullable = false)
 private Categoria categoria;

 // ============================================================
 // LÓGICA DE NEGOCIO (STOCK)
 // ============================================================

 public void reducirStock(int cantidad) {
  if (cantidad <= 0) {
   throw new IllegalArgumentException("La cantidad a reducir debe ser mayor a 0");
  }
  if (this.stock < cantidad) {
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
  if (this.stock < 0) {
   throw new IllegalStateException("El stock no puede ser un valor negativo");
  }
  if (this.precio < 0) {
   throw new IllegalStateException("El precio no puede ser un valor negativo");
  }
  // Si el stock llega a 0, podríamos forzar disponible a false,
  // pero es mejor dejarlo en manos del Admin para cuando reponga.
 }
}