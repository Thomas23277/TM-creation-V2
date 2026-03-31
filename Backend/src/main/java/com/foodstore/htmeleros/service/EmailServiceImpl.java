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

    // ✅ Tu llave de Resend ya integrada
    private final Resend resend = new Resend("re_MBSWj5Dm_7fdVpUsJ9JfvUSLbYmb57crW");

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
            System.out.println("Intentando enviar copia de confirmación a Admin (Resend)...");

            CreateEmailOptions sendEmailOptions = CreateEmailOptions.builder()
                    .from("TMCreation <" + emailFrom + ">")
                    .to(tuCorreoAdmin) // Se envía a vos para que tengas el comprobante listo para reenviar
                    .subject("✅ Copia de Pedido #" + pedido.getId() + " - " + nombreCliente)
                    .html(generarHtmlClienteProfesional(pedido, nombreCliente, emailCliente))
                    .build();

            CreateEmailResponse data = resend.emails().send(sendEmailOptions);
            System.out.println("Copia enviada con éxito. ID: " + data.getId());

        } catch (ResendException e) {
            System.err.println("Error Resend Cliente: " + e.getMessage());
        }
    }

    @Override
    @Async
    public void enviarNotificacionAdmin(Pedido pedido, String nombreFormulario, String telefonoFormulario, String emailFormulario) {
        try {
            System.out.println("Intentando enviar notificación de venta a Admin...");

            CreateEmailOptions sendEmailOptions = CreateEmailOptions.builder()
                    .from("Venta Web <" + emailFrom + ">")
                    .to(tuCorreoAdmin)
                    .subject("📦 NUEVA VENTA #" + pedido.getId() + " - " + nombreFormulario)
                    .html(generarHtmlAdminProfesional(pedido, nombreFormulario, telefonoFormulario, emailFormulario))
                    .build();

            CreateEmailResponse data = resend.emails().send(sendEmailOptions);
            System.out.println("Notificación de venta enviada. ID: " + data.getId());

        } catch (ResendException e) {
            System.err.println("Error Resend Admin: " + e.getMessage());
        }
    }

    private String generarHtmlClienteProfesional(Pedido pedido, String nombre, String emailOriginal) {
        String html = "<div style=\"font-family: 'Segoe UI', Arial, sans-serif; color: #1f2937; max-width: 650px; margin: auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);\">\n" +
                "    <div style=\"background: linear-gradient(135deg, #0f172a, #1e293b); padding: 30px; text-align: center; color: white;\">\n" +
                "        <h1 style=\"margin: 0; font-size: 26px; letter-spacing: 1px;\">TM CREATION</h1>\n" +
                "        <p style=\"margin-top: 10px; opacity: 0.9;\">¡Hola %s, recibimos tu pedido!</p>\n" +
                "    </div>\n" +
                "\n" +
                "    <div style=\"padding: 30px; line-height: 1.6;\">\n" +
                "        <h2 style=\"color: #0f766e; border-bottom: 2px solid #2dd4bf; padding-bottom: 10px; font-size: 20px;\">Pedido #%d - Próximos pasos</h2>\n" +
                "        <p>Estamos procesando tu solicitud. <b>En breve nos pondremos en contacto con vos</b> a través de WhatsApp o al correo <i>%s</i> para coordinar la entrega y el pago.</p>\n" +
                "\n" +
                "        <div style=\"background-color: #f0fdfa; border-left: 4px solid #2dd4bf; padding: 20px; margin: 25px 0; border-radius: 0 8px 8px 0;\">\n" +
                "            <h3 style=\"margin-top: 0; color: #134e4a; font-size: 16px;\">💳 Datos para Transferencia</h3>\n" +
                "            <ul style=\"list-style: none; padding: 0; margin: 0;\">\n" +
                "                <li><b>Titular:</b> %s</li>\n" +
                "                <li><b>CVU:</b> <code style=\"background: #e2e8f0; padding: 2px 5px; border-radius: 4px;\">%s</code></li>\n" +
                "                <li><b>Alias:</b> <i>%s</i></li>\n" +
                "            </ul>\n" +
                "            <p style=\"margin-top: 15px;\">\n" +
                "                <b>¿Querés agilizar el proceso?</b> Enviamos el comprobante aquí:\n" +
                "                <br><a href=\"%s\" style=\"display: inline-block; margin-top: 10px; background-color: #25d366; color: white; padding: 10px 20px; text-decoration: none; border-radius: 6px; font-weight: bold;\">Enviar WhatsApp ahora</a>\n" +
                "            </p>\n" +
                "        </div>\n" +
                "    </div>\n" +
                "    <div style=\"background-color: #f9fafb; padding: 20px; text-align: center; font-size: 12px; color: #9ca3af;\">\n" +
                "        Este es un comprobante interno de TM Creation (Cliente: %s)\n" +
                "    </div>\n" +
                "</div>";

        return String.format(html, nombre, pedido.getId(), emailOriginal, titular, cvu, alias, WHATSAPP_LINK, emailOriginal);
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
                "        <h2 style=\"color: #1e293b; border-bottom: 3px solid #6366f1; padding-bottom: 10px; margin-top: 0;\">📦 NUEVA VENTA #%d</h2>\n" +
                "        <p style=\"color: #64748b;\"><b>Fecha:</b> %s</p>\n" +
                "        \n" +
                "        <div style=\"background-color: #f8fafc; padding: 15px; border-radius: 8px; margin: 20px 0;\">\n" +
                "            <h3 style=\"color: #4f46e5; font-size: 16px; margin-top: 0;\">👤 Datos del Cliente</h3>\n" +
                "            <p style=\"margin: 5px 0;\"><b>Nombre:</b> %s</p>\n" +
                "            <p style=\"margin: 5px 0;\"><b>WhatsApp:</b> <a href=\"https://wa.me/%s\" style=\"color: #10b981; text-decoration: none; font-weight: bold;\">%s</a></p>\n" +
                "            <p style=\"margin: 5px 0;\"><b>Email:</b> %s</p>\n" +
                "            <p style=\"margin: 5px 0;\"><b>Dirección:</b> %s</p>\n" +
                "        </div>\n" +
                "\n" +
                "        <h3 style=\"color: #1e293b; font-size: 16px;\">🛒 Productos</h3>\n" +
                "        <table style=\"width: 100%%; border-collapse: collapse;\">\n" +
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