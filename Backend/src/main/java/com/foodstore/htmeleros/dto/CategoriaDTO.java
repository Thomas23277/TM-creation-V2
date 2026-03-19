package com.foodstore.htmeleros.dto;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoriaDTO {
    private Long id;
    private String nombre;
    private String urlImagen;

    // 🔥 CAMBIO CLAVE: Ahora el mensajero puede transportar el estado de la categoría
    private Boolean disponible;
}