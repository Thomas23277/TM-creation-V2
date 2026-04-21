package com.foodstore.htmeleros.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data                   // genera getters, setters, toString, equals, hashCode
@AllArgsConstructor     // genera constructor con todos los campos
@NoArgsConstructor      // genera constructor vacío
public class UserResponse {
    private Long id;
    private String nombre;
    private String apellido;
    private String email;
    @JsonProperty("role")
    private String rol;
}
