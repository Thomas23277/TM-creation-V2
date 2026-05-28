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
@Table(name = "colores_disponibles")
public class ColorDisponible {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = 100)
  private String nombre;

  @Column(name = "color_hex", nullable = false, length = 7)
  private String colorHex;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "producto_id", nullable = false)
  private Producto producto;
}
