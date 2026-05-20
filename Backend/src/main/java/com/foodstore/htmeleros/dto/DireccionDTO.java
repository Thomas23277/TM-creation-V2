package com.foodstore.htmeleros.dto;

import lombok.Data;

@Data
public class DireccionDTO {
    private Long id;
    private String alias;
    private String calle;
    private String numero;
    private String ciudad;
    private String provincia;
    private String codigoPostal;
    private boolean principal;
}
