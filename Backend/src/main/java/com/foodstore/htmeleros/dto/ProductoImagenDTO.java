package com.foodstore.htmeleros.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ProductoImagenDTO {

    private Long id;
    private String urlImagen;
    private Integer orden;
}
