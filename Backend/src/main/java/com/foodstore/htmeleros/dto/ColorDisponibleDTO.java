package com.foodstore.htmeleros.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ColorDisponibleDTO {

    private Long id;
    private String nombre;
    private String colorHex;
    private String urlImagen;
}
