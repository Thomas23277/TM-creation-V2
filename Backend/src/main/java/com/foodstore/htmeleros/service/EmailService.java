package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.entity.Pedido;

public interface EmailService {

    /**
     * Envía un correo profesional al cliente con los detalles de su compra,
     * instrucciones de pago y políticas de envío/garantía.
     */
    void enviarConfirmacionCliente(Pedido pedido, String emailCliente, String nombreCliente);

    /**
     * Envía una notificación al administrador con los datos capturados
     * específicamente en el formulario de checkout.
     */
    void enviarNotificacionAdmin(Pedido pedido, String nombreFormulario, String telefonoFormulario, String emailFormulario);
}