package com.foodstore.htmeleros.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class VarianteDTO {

    private Long id;
    private String nombre;
    private Double precio;
    private String colorHex;
}
