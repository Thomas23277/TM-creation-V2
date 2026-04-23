package com.foodstore.htmeleros.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.foodstore.htmeleros.enums.Estado;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "pedidos")
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Fecha de creación del pedido
    @Column(nullable = false)
    private LocalDateTime fecha;

    // Última actualización automática
    @Column(nullable = false)
    private LocalDateTime fechaActualizacion;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    @JsonIgnore
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Estado estado = Estado.PENDIENTE;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY
    )
    private List<DetallePedido> detalles = new ArrayList<>();

    @Column(nullable = false)
    private double total;

    @Column(length = 500)
    private String direccionEntrega;

    /* ======================================================
       MÉTODOS HELPER PARA RELACIÓN BIDIRECCIONAL SEGURA
    ====================================================== */

    public void addDetalle(DetallePedido detalle) {
        if (detalle == null) return;

        detalles.add(detalle);
        detalle.setPedido(this);
    }

    public void removeDetalle(DetallePedido detalle) {
        if (detalle == null) return;

        detalles.remove(detalle);
        detalle.setPedido(null);
    }

    /* ==============================
       AUTO FECHAS
    ============================== */

    @PrePersist
    public void prePersist() {

        LocalDateTime now = LocalDateTime.now();

        if (fecha == null) {
            fecha = now;
        }

        if (estado == null) {
            estado = Estado.PENDIENTE;
        }

        fechaActualizacion = now;
    }

    @PreUpdate
    public void preUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }
}