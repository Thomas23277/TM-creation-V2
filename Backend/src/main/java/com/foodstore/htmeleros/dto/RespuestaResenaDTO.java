package com.foodstore.htmeleros.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RespuestaResenaDTO {
    private Long id;
    private Long resenaId;
    private String respuesta;
    private String respondidoPor;
    private String fecha;
}