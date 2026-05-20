package com.foodstore.htmeleros.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    private String apellido;

    private String celular;
}
