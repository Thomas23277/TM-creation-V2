package com.foodstore.htmeleros.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.foodstore.htmeleros.entity.DetallePedido;
import com.foodstore.htmeleros.entity.Pedido;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
public class EmailServiceImpl implements EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailServiceImpl.class);

    private static final String BREVO_API_URL = "https://api.brevo.com/v3/smtp/email";
    private static final String WHATSAPP_LINK = "https://wa.me/5492616524913";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(15))
            .build();

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${app.email.from}")
    private String emailFrom;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Value("${app.pagos.cvu:}")
    private String cvu;

    @Value("${app.pagos.alias:}")
    private String alias;

    @Value("${app.pagos.titular:}")
    private String titular;

    private String apiKey;

    @PostConstruct
    public void init() {
        this.apiKey = System.getenv("BREVO_API_KEY");
        log.info("EmailService iniciado (Brevo REST API)");
        log.info("From: {}", emailFrom);
        log.info("Admin: {}", adminEmail);
        log.info("BREVO_API_KEY presente: {}", apiKey != null && !apiKey.isEmpty());
    }

    private boolean sendEmail(String to, String subject, String htmlBody) {
        if (apiKey == null || apiKey.isEmpty()) {
            log.warn("BREVO_API_KEY no configurada, no se puede enviar email");
            return false;
        }

        try {
            Map<String, Object> body = new LinkedHashMap<>();
            body.put("sender", Map.of("name", "TM Creation", "email", emailFrom));
            body.put("to", List.of(Map.of("email", to)));
            body.put("subject", subject);
            body.put("htmlContent", htmlBody);

            String json = objectMapper.writeValueAsString(body);
            log.debug("Enviando email a {} via Brevo API", to);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(BREVO_API_URL))
                    .header("api-key", apiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .timeout(Duration.ofSeconds(15))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 201) {
                log.info("Email enviado a {}", to);
                return true;
            }

            log.error("Error enviando email a {}. Status: {}. Respuesta: {}", to, response.statusCode(), response.body());
        } catch (Exception e) {
            log.error("Error enviando email a {}: {}", to, e.getMessage(), e);
        }

        return false;
    }

    @Override
    public void enviarEmailPrueba() {
        sendEmail(adminEmail,
                "Test de Email - TM Creation",
                "<h1>Si recibis esto, la API de Brevo funciona correctamente!</h1><p>Este es un email de prueba enviado desde la REST API.</p>");
    }

    @Override
    public void enviarConfirmacionCliente(Pedido pedido, String emailCliente, String nombreCliente) {
        sendEmail(emailCliente,
                "Confirmaci\u00f3n de Pedido #" + pedido.getId() + " - TM Creation",
                generarHtmlClienteProfesional(pedido, nombreCliente));
    }

    @Override
    public void enviarNotificacionAdmin(Pedido pedido, String nombreFormulario, String telefonoFormulario, String emailFormulario) {
        sendEmail(adminEmail,
                "NUEVO PEDIDO #" + pedido.getId() + " - " + nombreFormulario,
                generarHtmlAdminProfesional(pedido, nombreFormulario, telefonoFormulario, emailFormulario));
    }

    @Override
    public void enviarNotificacionNuevaResena(String nombreUsuario, String nombreProducto, int estrellas, String comentario) {
        sendEmail(adminEmail,
                "NUEVA RESE\u00d1A - " + nombreProducto + " - " + nombreUsuario,
                generarHtmlNuevaResena(nombreUsuario, nombreProducto, estrellas, comentario));
    }

    private String generarHtmlNuevaResena(String nombreUsuario, String nombreProducto, int estrellas, String comentario) {
        String estrellasHtml = "\u2605".repeat(estrellas) + "\u2606".repeat(5 - estrellas);

        return """
            <div style="font-family: sans-serif; padding: 20px; background-color: #f3f4f6;">
                <div style="background: white; padding: 25px; border-radius: 12px; max-width: 600px; margin: auto; border: 1px solid #e2e8f0;">
                    <h2 style="color: #f59e0b; border-bottom: 3px solid #f59e0b; padding-bottom: 10px; margin-top: 0;">Nueva Rese\u00f1a Recibida</h2>

                    <div style="background-color: #fffbeb; padding: 15px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #f59e0b;">
                        <p style="margin: 5px 0;"><b>Usuario:</b> %s</p>
                        <p style="margin: 5px 0;"><b>Producto:</b> %s</p>
                        <p style="margin: 5px 0;"><b>Calificaci\u00f3n:</b> <span style="color: #f59e0b; font-size: 18px;">%s</span></p>
                    </div>

                    <h3 style="color: #1e293b; font-size: 16px;">Comentario:</h3>
                    <p style="background-color: #f8fafc; padding: 15px; border-radius: 8px; color: #4b5563; font-style: italic;">
                        %s
                    </p>

                    <div style="margin-top: 25px; text-align: center;">
                        <a href="https://tmcreattion.netlify.app/src/pages/admin/reviews/reviews.html" style="background-color: #2dd4bf; color: #0f172a; padding: 12px 25px; text-decoration: none; border-radius: 8px; font-weight: bold;">Ver Todas las Rese\u00f1as</a>
                    </div>
                </div>
            </div>
            """.formatted(nombreUsuario, nombreProducto, estrellasHtml, comentario != null && !comentario.isEmpty() ? comentario : "Sin comentario");
    }

    private String generarHtmlClienteProfesional(Pedido pedido, String nombre) {
        return """
            <div style="font-family: 'Segoe UI', Arial, sans-serif; color: #1f2937; max-width: 650px; margin: auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);">
                <div style="background: linear-gradient(135deg, #0f172a, #1e293b); padding: 30px; text-align: center; color: white;">
                    <h1 style="margin: 0; font-size: 26px; letter-spacing: 1px;">TM CREATION</h1>
                    <p style="margin-top: 10px; opacity: 0.9;">Gracias por tu compra, %s!</p>
                </div>

                <div style="padding: 30px; line-height: 1.6;">
                    <h2 style="color: #0f766e; border-bottom: 2px solid #2dd4bf; padding-bottom: 10px; font-size: 20px;">Confirmaci\u00f3n de Pedido #%d</h2>
                    <p>Tu pedido ha sido recibido correctamente y se encuentra en estado <b>PENDIENTE</b> de validaci\u00f3n de pago.</p>

                    <div style="background-color: #f0fdfa; border-left: 4px solid #2dd4bf; padding: 20px; margin: 25px 0; border-radius: 0 8px 8px 0;">
                        <h3 style="margin-top: 0; color: #134e4a; font-size: 16px;">Informaci\u00f3n para Transferencia</h3>
                        <p style="margin-bottom: 8px;">Realiza el pago para comenzar con la producci\u00f3n:</p>
                        <ul style="list-style: none; padding: 0; margin: 0;">
                            <li><b>Titular:</b> %s</li>
                            <li><b>CVU:</b> <code style="background: #e2e8f0; padding: 2px 5px; border-radius: 4px;">%s</code></li>
                            <li><b>Alias:</b> <i>%s</i></li>
                        </ul>
                        <p style="margin-top: 15px; font-weight: bold;">
                            Env\u00eda el comprobante por WhatsApp:
                            <a href="%s" style="color: #25d366; text-decoration: none; font-size: 16px;">Click aqu\u00ed para enviar comprobante</a>
                        </p>
                    </div>

                    <h3 style="color: #111827; font-size: 17px; margin-top: 25px;">Proceso de confirmaci\u00f3n y env\u00edo</h3>
                    <ul style="padding-left: 20px; color: #4b5563;">
                        <li>Al recibir tu comprobante verificaremos el pago.</li>
                        <li>Te solicitaremos confirmaci\u00f3n detallada de tu direcci\u00f3n de entrega.</li>
                        <li>Una vez validado todo, coordinaremos el m\u00e9todo y tiempos de env\u00edo.</li>
                        <li>Recibir\u00e1s confirmaci\u00f3n final con detalles log\u00edsticos.</li>
                    </ul>

                    <h3 style="color: #111827; font-size: 17px; margin-top: 25px;">Pol\u00edtica de env\u00edos</h3>
                    <ul style="padding-left: 20px; color: #4b5563;">
                        <li><b>Env\u00edos en Mendoza (Gran Mendoza):</b> Tarifa fija de $3.500.</li>
                        <li><b>Zonas fuera del radio habitual:</b> Podremos coordinar un punto intermedio.</li>
                        <li><b>Resto del pa\u00eds/mundo:</b> Costo acordado seg\u00fan destino y log\u00edstica.</li>
                    </ul>

                    <h3 style="color: #111827; font-size: 17px; margin-top: 25px;">Garant\u00eda</h3>
                    <p style="color: #4b5563; font-size: 14px;">
                        Garant\u00eda de <b>30 d\u00edas</b> por fallas de fabricaci\u00f3n. No cubre da\u00f1os por mal uso, golpes o manipulaci\u00f3n indebida posterior a la entrega.
                    </p>

                    <div style="text-align: center; margin-top: 40px; padding: 20px; border-top: 1px solid #eee;">
                        <p style="font-size: 14px; color: #6b7280; margin-bottom: 15px;">Tienes alguna duda adicional?</p>
                        <a href="%s" style="background-color: #2dd4bf; color: white; padding: 12px 25px; text-decoration: none; border-radius: 30px; font-weight: bold; display: inline-block;">Consultar por WhatsApp</a>
                    </div>
                </div>
                <div style="background-color: #f9fafb; padding: 20px; text-align: center; font-size: 12px; color: #9ca3af;">
                    TM Creation 2026 – Todos los derechos reservados
                </div>
            </div>
            """.formatted(nombre, pedido.getId(), titular, cvu, alias, WHATSAPP_LINK, WHATSAPP_LINK);
    }

    private String generarHtmlAdminProfesional(Pedido pedido, String nombre, String telefono, String emailFormulario) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        StringBuilder filas = new StringBuilder();

        for (DetallePedido det : pedido.getDetalles()) {
            filas.append("""
                <tr>
                    <td style="padding:10px; border-bottom: 1px solid #eee;">%s</td>
                    <td style="padding:10px; border-bottom: 1px solid #eee; text-align:center;">%d</td>
                    <td style="padding:10px; border-bottom: 1px solid #eee; text-align:right;">$%.2f</td>
                </tr>
                """.formatted(det.getProducto().getNombre(), det.getCantidad(), det.getSubtotal()));
        }

        return """
            <div style="font-family: sans-serif; padding: 20px; background-color: #f3f4f6;">
                <div style="background: white; padding: 25px; border-radius: 12px; max-width: 600px; margin: auto; border: 1px solid #e2e8f0;">
                    <h2 style="color: #1e293b; border-bottom: 3px solid #6366f1; padding-bottom: 10px; margin-top: 0;">Nuevo Pedido #%d</h2>
                    <p style="color: #64748b;"><b>Fecha:</b> %s</p>

                    <div style="background-color: #f8fafc; padding: 15px; border-radius: 8px; margin: 20px 0;">
                        <h3 style="color: #4f46e5; font-size: 16px; margin-top: 0;">Datos del Cliente (Formulario)</h3>
                        <p style="margin: 5px 0;"><b>Nombre:</b> %s</p>
                        <p style="margin: 5px 0;"><b>WhatsApp:</b> <a href="https://wa.me/%s" style="color: #10b981; text-decoration: none; font-weight: bold;">%s (Chat)</a></p>
                        <p style="margin: 5px 0;"><b>Email Contacto:</b> <a href="mailto:%s" style="color: #6366f1;">%s</a></p>
                        <p style="margin: 5px 0;"><b>Direcci\u00f3n:</b> %s</p>
                    </div>

                    <h3 style="color: #1e293b; font-size: 16px;">Detalle de Productos</h3>
                    <table style="width: 100%%; border-collapse: collapse;">
                        <thead>
                            <tr style="text-align: left; font-size: 12px; color: #9ca3af; text-transform: uppercase;">
                                <th style="padding: 10px;">Producto</th>
                                <th style="padding: 10px; text-align:center;">Cant.</th>
                                <th style="padding: 10px; text-align:right;">Subtotal</th>
                            </tr>
                        </thead>
                        <tbody>
                            %s
                        </tbody>
                    </table>

                    <div style="text-align: right; margin-top: 20px; padding-top: 15px; border-top: 2px solid #f1f5f9;">
                        <span style="font-size: 18px; color: #1e293b; font-weight: bold;">TOTAL: $%.2f</span>
                    </div>
                </div>
            </div>
            """.formatted(
                pedido.getId(),
                pedido.getFecha().format(formatter),
                nombre,
                telefono.replaceAll("[^0-9]", ""), telefono,
                emailFormulario, emailFormulario,
                pedido.getDireccionEntrega(),
                filas.toString(),
                pedido.getTotal()
        );
    }
}
