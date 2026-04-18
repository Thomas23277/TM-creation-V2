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

    // ✅ Cargamos la API Key desde Render (Environment Variables) por seguridad
    @Value("${resend.api.key}")
    private String apiKey;

    private final String emailFrom = "onboarding@resend.dev";
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
            // Instanciamos Resend con la API Key cargada
            Resend resend = new Resend(apiKey);

            System.out.println("Enviando comprobante de pedido #" + pedido.getId() + " a Admin...");

            CreateEmailOptions sendEmailOptions = CreateEmailOptions.builder()
                    .from("TMCreation <" + emailFrom + ">")
                    .to(tuCorreoAdmin)
                    .subject("✅ Copia de Pedido #" + pedido.getId() + " - " + nombreCliente)
                    .html(generarHtmlClienteProfesional(pedido, nombreCliente, emailCliente))
                    .build();

            CreateEmailResponse data = resend.emails().send(sendEmailOptions);
            System.out.println("Copia enviada. ID: " + data.getId());

        } catch (ResendException e) {
            System.err.println("Error Resend Cliente: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void enviarNotificacionAdmin(Pedido pedido, String nombreFormulario, String telefonoFormulario, String emailFormulario) {
        try {
            Resend resend = new Resend(apiKey);

            System.out.println("Enviando notificación de venta...");

            CreateEmailOptions sendEmailOptions = CreateEmailOptions.builder()
                    .from("Venta Web <" + emailFrom + ">")
                    .to(tuCorreoAdmin)
                    .subject("📦 NUEVA VENTA #" + pedido.getId() + " - " + nombreFormulario)
                    .html(generarHtmlAdminProfesional(pedido, nombreFormulario, telefonoFormulario, emailFormulario))
                    .build();

            CreateEmailResponse data = resend.emails().send(sendEmailOptions);
            System.out.println("Notificación enviada. ID: " + data.getId());

        } catch (ResendException e) {
            System.err.println("Error Resend Admin: " + e.getMessage());
        }
    }

    private String generarHtmlClienteProfesional(Pedido pedido, String nombre, String emailOriginal) {
        String html = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; color: #1f2937; max-width: 650px; margin: auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden;\">\n" +
                "    <div style=\"background: linear-gradient(135deg, #0f172a, #1e293b); padding: 30px; text-align: center; color: white;\">\n" +
                "        <h1 style=\"margin: 0; font-size: 26px;\">TM CREATION</h1>\n" +
                "        <p style=\"margin-top: 10px;\">¡Hola %s, recibimos tu pedido!</p>\n" +
                "    </div>\n" +
                "    <div style=\"padding: 30px; line-height: 1.6;\">\n" +
                "        <h2 style=\"color: #0f766e; border-bottom: 2px solid #2dd4bf; padding-bottom: 10px; font-size: 20px;\">Pedido #%d</h2>\n" +
                "        <p>Estamos procesando tu solicitud. Nos contactaremos al correo <i>%s</i>.</p>\n" +
                "        <div style=\"background-color: #f0fdfa; border-left: 4px solid #2dd4bf; padding: 20px; margin: 25px 0;\">\n" +
                "            <h3 style=\"margin-top: 0; color: #134e4a;\">💳 Datos para Transferencia</h3>\n" +
                "            <p><b>Titular:</b> %s<br><b>CVU:</b> %s<br><b>Alias:</b> %s</p>\n" +
                "            <a href=\"%s\" style=\"display: inline-block; background-color: #25d366; color: white; padding: 10px 20px; text-decoration: none; border-radius: 6px; font-weight: bold;\">Enviar Comprobante por WhatsApp</a>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "</div>";

        return String.format(html, nombre, pedido.getId(), emailOriginal, titular, cvu, alias, WHATSAPP_LINK);
    }

    private String generarHtmlAdminProfesional(Pedido pedido, String nombre, String telefono, String emailFormulario) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder filas = new StringBuilder();

        for (DetallePedido det : pedido.getDetalles()) {
            filas.append(String.format("<tr><td style='padding:10px; border-bottom:1px solid #eee;'>%s</td><td style='text-align:center;'>%d</td><td style='text-align:right;'>$%.2f</td></tr>",
                    det.getProducto().getNombre(), det.getCantidad(), det.getSubtotal()));
        }

        String html = "<div style=\"font-family: sans-serif; padding: 20px;\">\n" +
                "    <div style=\"background: white; padding: 25px; border-radius: 12px; max-width: 600px; margin: auto; border: 1px solid #e2e8f0;\">\n" +
                "        <h2 style=\"color: #1e293b; border-bottom: 3px solid #6366f1;\">📦 NUEVA VENTA #%d</h2>\n" +
                "        <p><b>Fecha:</b> %s</p>\n" +
                "        <div style=\"background-color: #f8fafc; padding: 15px; margin: 20px 0;\">\n" +
                "            <p><b>Cliente:</b> %s</p>\n" +
                "            <p><b>WhatsApp:</b> %s</p>\n" +
                "            <p><b>Dirección:</b> %s</p>\n" +
                "        </div>\n" +
                "        <table style=\"width: 100%%; border-collapse: collapse;\"><thead><tr><th align='left'>Prod</th><th>Cant</th><th align='right'>Sub</th></tr></thead>\n" +
                "        <tbody>%s</tbody></table>\n" +
                "        <h3 style=\"text-align: right; margin-top: 20px;\">TOTAL: $%.2f</h3>\n" +
                "    </div>\n" +
                "</div>";

        return String.format(html, pedido.getId(), pedido.getFecha().format(formatter), nombre, telefono, pedido.getDireccionEntrega(), filas.toString(), pedido.getTotal());
    }
}