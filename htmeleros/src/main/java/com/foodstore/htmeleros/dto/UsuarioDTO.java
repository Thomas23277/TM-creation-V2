package com.foodstore.htmeleros.dto;

import com.foodstore.htmeleros.enums.Rol;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

@Data
public class UsuarioDTO {
    private Long id;

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    
    private String apellido;

    @Email(message = "Email inválido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    // Celular opcional
    private int celular;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres o una mayuscula.")
    private String contrasenia;

    private Rol rol;
    private List<PedidoDTO> pedidos;
}
