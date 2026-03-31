package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.entity.DetallePedido;
import com.foodstore.htmeleros.entity.Pedido;
import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    // 🔥 LA LLAVE MAESTRA DE RESEND (Reemplazá por la tuya que empieza con re_...)
    private final Resend resend = new Resend("re_MBSWj5Dm_7fdVpUsJ9JfvUSLbYmb57crW");

    // ⚠️ IMPORTANTE: Resend en plan gratis solo deja mandar DESDE este mail "onboarding@resend.dev"
    private final String emailFrom = "onboarding@resend.dev";

    // OJO: En el plan gratis, Resend solo te deja mandar correos HACIA el correo con el que te registraste.
    // O sea, solo te van a llegar a tmcreation233@gmail.com por ahora.
    private final String tuCorreoAdmin = "tmcreation233@gmail.com";

    @Value("${app.pagos.cvu:}")
    private String cvu;

    @Value("${app.pagos.alias:}")
    private String alias;

    @Value("${app.pagos.titular:}")
    private String titular;

    private static final String WHATSAPP_LINK = "https://wa.me/5492616524913";

    @Override
    @Async
    public void enviarConfirmacionCliente(Pedido pedido, String emailCliente, String nombreCliente) {
        try {
            System.out.println("Intentando enviar correo HTTP (Resend) al cliente...");

            CreateEmailOptions sendEmailOptions = CreateEmailOptions.builder()
                    .from("TMCreation <" + emailFrom + ">")
                    // 👇 Forzamos que vaya a tu correo porque Resend Gratis no manda a correos de terceros
                    .to(tuCorreoAdmin)
                    .subject("✅ Confirmación de Pedido #" + pedido.getId() + " - TM Creation (Para: " + emailCliente + ")")
                    .html(generarHtmlClienteProfesional(pedido, nombreCliente))
                    .build();

            CreateEmailResponse data = resend.emails().send(sendEmailOptions);
            System.out.println("Correo enviado con éxito. ID: " + data.getId());

        } catch (ResendException e) {
            System.err.println("Error Resend Cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    @Async
    public void enviarNotificacionAdmin(Pedido pedido, String nombreFormulario, String telefonoFormulario, String emailFormulario) {
        try {
            System.out.println("Intentando enviar correo HTTP (Resend) al admin...");

            CreateEmailOptions sendEmailOptions = CreateEmailOptions.builder()
                    .from("TMCreation Admin <" + emailFrom + ">")
                    .to(tuCorreoAdmin)
                    .subject("📦 NUEVO PEDIDO #" + pedido.getId() + " - " + nombreFormulario)
                    .html(generarHtmlAdminProfesional(pedido, nombreFormulario, telefonoFormulario, emailFormulario))
                    .build();

            CreateEmailResponse data = resend.emails().send(sendEmailOptions);
            System.out.println("Correo de Admin enviado con éxito. ID: " + data.getId());

        } catch (ResendException e) {
            System.err.println("Error Resend Admin: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String generarHtmlClienteProfesional(Pedido pedido, String nombre) {
        String html = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; color: #1f2937; max-width: 650px; margin: auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);\">\n" +
                "    <div style=\"background: linear-gradient(135deg, #0f172a, #1e293b); padding: 30px; text-align: center; color: white;\">\n" +
                "        <h1 style=\"margin: 0; font-size: 26px; letter-spacing: 1px;\">TM CREATION</h1>\n" +
                "        <p style=\"margin-top: 10px; opacity: 0.9;\">¡Gracias por tu compra, %s!</p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"padding: 30px; line-height: 1.6;\">\n" +
                "        <h2 style=\"color: #0f766e; border-bottom: 2px solid #2dd4bf; padding-bottom: 10px; font-size: 20px;\">Confirmación de Pedido #%d</h2>\n" +
                "        <p>Tu pedido ha sido recibido correctamente y se encuentra en estado <b>PENDIENTE</b> de validación de pago.</p>\n" +
                "\n" +
                "        <div style=\"background-color: #f0fdfa; border-left: 4px solid #2dd4bf; padding: 20px; margin: 25px 0; border-radius: 0 8px 8px 0;\">\n" +
                "            <h3 style=\"margin-top: 0; color: #134e4a; font-size: 16px;\">💳 Información para Transferencia</h3>\n" +
                "            <p style=\"margin-bottom: 8px;\">Realiza el pago para comenzar con la producción:</p>\n" +
                "            <ul style=\"list-style: none; padding: 0; margin: 0;\">\n" +
                "                <li><b>Titular:</b> %s</li>\n" +
                "                <li><b>CVU:</b> <code style=\"background: #e2e8f0; padding: 2px 5px; border-radius: 4px;\">%s</code></li>\n" +
                "                <li><b>Alias:</b> <i>%s</i></li>\n" +
                "            </ul>\n" +
                "            <p style=\"margin-top: 15px; font-weight: bold;\">\n" +
                "                👉 Envía el comprobante por WhatsApp: \n" +
                "                <a href=\"%s\" style=\"color: #25d366; text-decoration: none; font-size: 16px;\">Click aquí para enviar comprobante</a>\n" +
                "            </p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div style=\"background-color: #f9fafb; padding: 20px; text-align: center; font-size: 12px; color: #9ca3af;\">\n" +
                "        TM Creation © 2026 – Todos los derechos reservados\n" +
                "    </div>\n" +
                "</div>";

        return String.format(html, nombre, pedido.getId(), titular, cvu, alias, WHATSAPP_LINK);
    }

    private String generarHtmlAdminProfesional(Pedido pedido, String nombre, String telefono, String emailFormulario) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder filas = new StringBuilder();

        for (DetallePedido det : pedido.getDetalles()) {
            String filaHtml = "<tr>\n" +
                    "    <td style=\"padding:10px; border-bottom: 1px solid #eee;\">%s</td>\n" +
                    "    <td style=\"padding:10px; border-bottom: 1px solid #eee; text-align:center;\">%d</td>\n" +
                    "    <td style=\"padding:10px; border-bottom: 1px solid #eee; text-align:right;\">$%.2f</td>\n" +
                    "</tr>\n";
            filas.append(String.format(filaHtml, det.getProducto().getNombre(), det.getCantidad(), det.getSubtotal()));
        }

        String html = "<div style=\"font-family: sans-serif; padding: 20px; background-color: #f3f4f6;\">\n" +
                "    <div style=\"background: white; padding: 25px; border-radius: 12px; max-width: 600px; margin: auto; border: 1px solid #e2e8f0;\">\n" +
                "        <h2 style=\"color: #1e293b; border-bottom: 3px solid #6366f1; padding-bottom: 10px; margin-top: 0;\">📦 Nuevo Pedido #%d</h2>\n" +
                "        <p style=\"color: #64748b;\"><b>Fecha:</b> %s</p>\n" +
                "        \n" +
                "        <div style=\"background-color: #f8fafc; padding: 15px; border-radius: 8px; margin: 20px 0;\">\n" +
                "            <h3 style=\"color: #4f46e5; font-size: 16px; margin-top: 0;\">👤 Datos del Cliente</h3>\n" +
                "            <p style=\"margin: 5px 0;\"><b>Nombre:</b> %s</p>\n" +
                "            <p style=\"margin: 5px 0;\"><b>WhatsApp:</b> <a href=\"https://wa.me/%s\" style=\"color: #10b981; text-decoration: none; font-weight: bold;\">%s</a></p>\n" +
                "            <p style=\"margin: 5px 0;\"><b>Email Contacto:</b> %s</p>\n" +
                "            <p style=\"margin: 5px 0;\"><b>Dirección:</b> %s</p>\n" +
                "        </div>\n" +
                "\n" +
                "        <h3 style=\"color: #1e293b; font-size: 16px;\">🛒 Detalle de Productos</h3>\n" +
                "        <table style=\"width: 100%%; border-collapse: collapse;\">\n" +
                "            <thead>\n" +
                "                <tr style=\"text-align: left; font-size: 12px; color: #9ca3af; text-transform: uppercase;\">\n" +
                "                    <th style=\"padding: 10px;\">Producto</th>\n" +
                "                    <th style=\"padding: 10px; text-align:center;\">Cant.</th>\n" +
                "                    <th style=\"padding: 10px; text-align:right;\">Subtotal</th>\n" +
                "                </tr>\n" +
                "            </thead>\n" +
                "            <tbody>\n" +
                "                %s\n" +
                "            </tbody>\n" +
                "        </table>\n" +
                "\n" +
                "        <div style=\"text-align: right; margin-top: 20px; padding-top: 15px; border-top: 2px solid #f1f5f9;\">\n" +
                "            <span style=\"font-size: 18px; color: #1e293b; font-weight: bold;\">TOTAL: $%.2f</span>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>";

        return String.format(html,
                pedido.getId(),
                pedido.getFecha().format(formatter),
                nombre,
                telefono.replaceAll("[^0-9]", ""), telefono,
                emailFormulario,
                pedido.getDireccionEntrega(),
                filas.toString(),
                pedido.getTotal()
        );
    }
}