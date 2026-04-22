package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.entity.Pedido;

public interface EmailService {

    void enviarConfirmacionCliente(Pedido pedido, String emailCliente, String nombreCliente);

    void enviarNotificacionAdmin(Pedido pedido, String nombreFormulario, String telefonoFormulario, String emailFormulario);

    void enviarNotificacionNuevaResena(String nombreUsuario, String nombreProducto, int estrellas, String comentario);
}