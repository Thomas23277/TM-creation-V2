package com.foodstore.htmeleros.dto;

public class EtiquetaDTO {
    private Long id;
    private String nombre;
    private String colorHex;
    private boolean visible;
    private boolean interna;

    public EtiquetaDTO() {}

    public EtiquetaDTO(Long id, String nombre, String colorHex, boolean visible, boolean interna) {
        this.id = id;
        this.nombre = nombre;
        this.colorHex = colorHex;
        this.visible = visible;
        this.interna = interna;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getColorHex() { return colorHex; }
    public void setColorHex(String colorHex) { this.colorHex = colorHex; }

    public boolean isVisible() { return visible; }
    public void setVisible(boolean visible) { this.visible = visible; }

    public boolean isInterna() { return interna; }
    public void setInterna(boolean interna) { this.interna = interna; }
}
