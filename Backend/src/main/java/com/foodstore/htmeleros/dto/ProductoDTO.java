package com.foodstore.htmeleros.dto;

import java.util.List;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.fasterxml.jackson.annotation.JsonInclude;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductoDTO {

    private Long id;
    private String nombre;
    private double precio;
    private int stock;

    private String descripcion;
    private String urlImagen;

    // 🔥 Cambiado de 'boolean' a 'Boolean' para que Lombok
    // genere getDisponible() y setDisponible() correctamente.
    private Boolean disponible = true;

    private Boolean coloresActivo = false;

    private Boolean stockControl = true;

    private CategoriaDTO categoria;

    // Campo extra necesario para capturar el ID desde el FormData del Admin
    private Long categoriaId;

    // Campos para promedio de reseñas
    private Double promedioResenas = 0.0;
    private Long totalResenas = 0L;

    private List<VarianteDTO> variantes;
    private List<ColorDisponibleDTO> colores;
    private List<ProductoImagenDTO> imagenes;
    private Set<EtiquetaDTO> etiquetas;
    private List<Long> etiquetaIds;

    /**
     * Helper manual por si tu IDE o compilador sigue
     * teniendo problemas con Lombok y los booleanos.
     */
    public Boolean getDisponible() {
        return disponible;
    }

    public void setDisponible(Boolean disponible) {
        this.disponible = disponible;
    }
}