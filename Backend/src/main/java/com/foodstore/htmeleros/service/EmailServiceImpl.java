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
import java.net.URLEncoder;
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
                log.info("✅ Email enviado a {}", to);
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
                """
                <div style="font-family: sans-serif; text-align: center; padding: 40px;">
                    <h1 style="color: #0f172a;">Si recib\u00eds esto, la API de Brevo funciona correctamente!</h1>
                    <p style="color: #64748b; font-size: 16px;">Email enviado desde la REST API de Brevo.</p>
                </div>
                """);
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
                "Nuevo Pedido #" + pedido.getId() + " - " + nombreFormulario,
                generarHtmlAdminProfesional(pedido, nombreFormulario, telefonoFormulario, emailFormulario));
    }

    @Override
    public void enviarNotificacionNuevaResena(String nombreUsuario, String nombreProducto, int estrellas, String comentario) {
        sendEmail(adminEmail,
                "Nueva Rese\u00f1a - " + nombreProducto + " - " + nombreUsuario,
                generarHtmlNuevaResena(nombreUsuario, nombreProducto, estrellas, comentario));
    }

    private String generarGoogleMapsLink(String direccion) {
        if (direccion == null || direccion.isBlank()) return null;
        try {
            return "https://www.google.com/maps/search/?api=1&query="
                    + URLEncoder.encode(direccion, "UTF-8");
        } catch (Exception e) {
            return null;
        }
    }

    private String formatearMetodoPago(String metodo) {
        if (metodo == null) return "No especificado";
        return switch (metodo.toLowerCase()) {
            case "transferencia" -> "Transferencia Bancaria";
            case "efectivo" -> "Efectivo";
            case "mercadopago" -> "Mercado Pago";
            case "tarjeta" -> "Tarjeta de D\u00e9bito/Cr\u00e9dito";
            default -> metodo;
        };
    }

    // ============================================================
    // EMAIL CLIENTE
    // ============================================================
    private String generarHtmlClienteProfesional(Pedido pedido, String nombre) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = pedido.getFecha().format(formatter);
        String mapsLink = generarGoogleMapsLink(pedido.getDireccionEntrega());
        String metodoPagoFormateado = formatearMetodoPago(pedido.getMetodoPago());
        boolean esTransferencia = pedido.getMetodoPago() != null
                && pedido.getMetodoPago().equalsIgnoreCase("transferencia");

        StringBuilder itemsHtml = new StringBuilder();
        for (DetallePedido det : pedido.getDetalles()) {
            String talleHtml = "";
            if (det.getNombreTamano() != null && !det.getNombreTamano().isBlank()) {
                talleHtml = """
                    <span style="background-color: #f1f5f9; color: #475569; padding: 2px 10px; border-radius: 12px; font-size: 12px; font-weight: 600; display: inline-block;">
                        %s
                    </span>
                    """.formatted(det.getNombreTamano());
            }
            itemsHtml.append("""
                <tr>
                    <td style="padding: 12px 8px; border-bottom: 1px solid #e2e8f0;">
                        <div style="font-weight: 600; color: #0f172a;">%s</div>
                        %s
                    </td>
                    <td style="padding: 12px 8px; border-bottom: 1px solid #e2e8f0; text-align: center; color: #475569;">%d</td>
                    <td style="padding: 12px 8px; border-bottom: 1px solid #e2e8f0; text-align: right; color: #0f172a; font-weight: 500;">$%.2f</td>
                </tr>
                """.formatted(
                    det.getProducto().getNombre(),
                    talleHtml,
                    det.getCantidad(),
                    det.getSubtotal()
            ));
        }

        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="margin: 0; padding: 0; background-color: #f1f5f9; font-family: 'Segoe UI', Arial, sans-serif;">
                <table align="center" width="100%%" cellpadding="0" cellspacing="0">
                <tr><td align="center" style="padding: 30px 15px;">
                    <table width="600" cellpadding="0" cellspacing="0" style="max-width: 600px; width: 100%%;">

                        <!-- HEADER -->
                        <tr>
                            <td style="background: linear-gradient(135deg, #0f172a, #1e293b); padding: 35px 30px; text-align: center; border-radius: 16px 16px 0 0;">
                                <h1 style="margin: 0; color: #ffffff; font-size: 28px; letter-spacing: 2px; font-weight: 800;">TM CREATION</h1>
                                <p style="margin: 12px 0 0; color: #94a3b8; font-size: 16px;">Gracias por tu compra, %s!</p>
                            </td>
                        </tr>

                        <!-- BODY -->
                        <tr>
                            <td style="background: #ffffff; padding: 35px 30px;">

                                <!-- ORDER STATUS -->
                                <div style="background: linear-gradient(135deg, #fef3c7, #fde68a); border-radius: 12px; padding: 20px; margin-bottom: 25px; text-align: center;">
                                    <div style="font-size: 14px; color: #92400e; margin-bottom: 4px;">Pedido #%d</div>
                                    <div style="font-size: 22px; font-weight: 700; color: #78350f;">Estado: Pendiente</div>
                                    <div style="font-size: 13px; color: #92400e; margin-top: 4px;">%s</div>
                                </div>

                                <!-- DELIVERY INFO -->
                                <h3 style="color: #0f172a; font-size: 15px; text-transform: uppercase; letter-spacing: 1px; margin: 25px 0 12px;">Direcci\u00f3n de Entrega</h3>
                                <div style="background: #f8fafc; border-radius: 10px; padding: 16px; margin-bottom: 25px;">
                                    <p style="margin: 0 0 8px; color: #334155; font-size: 15px;">
                                        %s
                                    </p>
                                    %s
                                    <p style="margin: 8px 0 0; color: #64748b; font-size: 14px;">
                                        Tel\u00e9fono de contacto: <strong>%s</strong>
                                    </p>
                                    <p style="margin: 4px 0 0; color: #64748b; font-size: 14px;">
                                        M\u00e9todo de pago: <strong>%s</strong>
                                    </p>
                                </div>

                                %s

                                <!-- PRODUCTS TABLE -->
                                <h3 style="color: #0f172a; font-size: 15px; text-transform: uppercase; letter-spacing: 1px; margin: 25px 0 12px;">Productos</h3>
                                <table width="100%%" cellpadding="0" cellspacing="0" style="border-collapse: collapse;">
                                    <thead>
                                        <tr style="background: #f1f5f9;">
                                            <th style="padding: 10px 8px; text-align: left; font-size: 12px; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px;">Producto</th>
                                            <th style="padding: 10px 8px; text-align: center; font-size: 12px; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px;">Cant.</th>
                                            <th style="padding: 10px 8px; text-align: right; font-size: 12px; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px;">Subtotal</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        %s
                                    </tbody>
                                    <tfoot>
                                        <tr>
                                            <td colspan="2" style="padding: 14px 8px; text-align: right; font-size: 16px; font-weight: 700; color: #0f172a; border-top: 2px solid #1e293b;">TOTAL</td>
                                            <td style="padding: 14px 8px; text-align: right; font-size: 18px; font-weight: 800; color: #0f172a; border-top: 2px solid #1e293b;">$%.2f</td>
                                        </tr>
                                    </tfoot>
                                </table>

                                <!-- PAYMENT INFO -->
                                %s

                                <!-- PROCESS -->
                                <h3 style="color: #0f172a; font-size: 15px; text-transform: uppercase; letter-spacing: 1px; margin: 30px 0 15px;">Proceso de Confirmaci\u00f3n</h3>
                                <table width="100%%" cellpadding="0" cellspacing="0">
                                    <tr>
                                        <td style="padding: 10px 0; border-bottom: 1px solid #f1f5f9;">
                                            <span style="display: inline-block; width: 26px; height: 26px; background: #2dd4bf; color: #0f172a; border-radius: 50%%; text-align: center; line-height: 26px; font-size: 13px; font-weight: 700; margin-right: 10px;">1</span>
                                            Verificamos tu pago
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 10px 0; border-bottom: 1px solid #f1f5f9;">
                                            <span style="display: inline-block; width: 26px; height: 26px; background: #2dd4bf; color: #0f172a; border-radius: 50%%; text-align: center; line-height: 26px; font-size: 13px; font-weight: 700; margin-right: 10px;">2</span>
                                            Confirmamos tu direcci\u00f3n de entrega
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 10px 0; border-bottom: 1px solid #f1f5f9;">
                                            <span style="display: inline-block; width: 26px; height: 26px; background: #2dd4bf; color: #0f172a; border-radius: 50%%; text-align: center; line-height: 26px; font-size: 13px; font-weight: 700; margin-right: 10px;">3</span>
                                            Coordinamos el env\u00edo
                                        </td>
                                    </tr>
                                    <tr>
                                        <td style="padding: 10px 0;">
                                            <span style="display: inline-block; width: 26px; height: 26px; background: #2dd4bf; color: #0f172a; border-radius: 50%%; text-align: center; line-height: 26px; font-size: 13px; font-weight: 700; margin-right: 10px;">4</span>
                                            Recib\u00eds confirmaci\u00f3n final
                                        </td>
                                    </tr>
                                </table>

                                <!-- SHIPPING -->
                                <h3 style="color: #0f172a; font-size: 15px; text-transform: uppercase; letter-spacing: 1px; margin: 30px 0 15px;">Pol\u00edtica de Env\u00edos</h3>
                                <div style="background: #f8fafc; border-radius: 10px; padding: 16px; margin-bottom: 20px;">
                                    <ul style="margin: 0; padding-left: 20px; color: #475569; line-height: 1.8;">
                                        <li><strong>Gran Mendoza:</strong> Tarifa fija de $3.500</li>
                                        <li><strong>Zonas fuera del radio:</strong> Coordinamos punto intermedio</li>
                                        <li><strong>Resto del pa\u00eds/mundo:</strong> Costo acordado seg\u00fan destino</li>
                                    </ul>
                                </div>

                                <!-- GUARANTEE -->
                                <div style="background: #f0fdfa; border: 1px solid #2dd4bf; border-radius: 10px; padding: 16px; margin: 20px 0;">
                                    <p style="margin: 0; color: #0f766e; font-size: 14px;">
                                        30 d\u00edas de garant\u00eda por fallas de fabricaci\u00f3n.
                                    </p>
                                </div>

                                <!-- WHATSAPP CTA -->
                                <div style="text-align: center; margin: 30px 0 10px;">
                                    <p style="color: #64748b; font-size: 14px; margin-bottom: 14px;">Tienes alguna duda?</p>
                                    <a href="%s" style="display: inline-block; background: #25d366; color: #ffffff; padding: 14px 30px; text-decoration: none; border-radius: 30px; font-weight: 700; font-size: 15px;">
                                        Consultar por WhatsApp
                                    </a>
                                </div>

                            </td>
                        </tr>

                        <!-- FOOTER -->
                        <tr>
                            <td style="background: #0f172a; padding: 20px 30px; text-align: center; border-radius: 0 0 16px 16px;">
                                <p style="margin: 0; color: #64748b; font-size: 12px;">TM Creation \u00a9 2026 \u2013 Todos los derechos reservados</p>
                            </td>
                        </tr>

                    </table>
                </td></tr>
                </table>
            </body>
            </html>
            """.formatted(
                nombre,
                pedido.getId(),
                fechaFormateada,
                pedido.getDireccionEntrega() != null ? pedido.getDireccionEntrega() : "No especificada",
                mapsLink != null
                    ? "<a href=\"" + mapsLink + "\" style=\"color: #2563eb; font-size: 13px; text-decoration: none;\">Ver en Google Maps</a>"
                    : "",
                pedido.getTelefonoContacto() != null ? pedido.getTelefonoContacto() : "No especificado",
                metodoPagoFormateado,
                esTransferencia ? generarBloquePago() : "",
                itemsHtml.toString(),
                pedido.getTotal(),
                esTransferencia ? "" : "",
                WHATSAPP_LINK
        );
    }

    private String generarBloquePago() {
        return """
            <div style="background: #f0fdfa; border: 1px solid #2dd4bf; border-radius: 10px; padding: 20px; margin: 25px 0;">
                <h3 style="color: #0f766e; font-size: 15px; margin: 0 0 12px;">Datos para Transferencia</h3>
                <table width="100%%" cellpadding="4" cellspacing="0">
                    <tr>
                        <td style="color: #475569; font-size: 13px; width: 80px;">Titular:</td>
                        <td style="font-weight: 600; color: #0f172a; font-size: 14px;">%s</td>
                    </tr>
                    <tr>
                        <td style="color: #475569; font-size: 13px;">CVU:</td>
                        <td><code style="background: #e2e8f0; padding: 3px 8px; border-radius: 4px; font-size: 13px;">%s</code></td>
                    </tr>
                    <tr>
                        <td style="color: #475569; font-size: 13px;">Alias:</td>
                        <td style="font-weight: 600; color: #0f172a; font-size: 14px;">%s</td>
                    </tr>
                </table>
                <div style="text-align: center; margin-top: 15px;">
                    <p style="color: #475569; font-size: 13px; margin: 0 0 10px;">Envi\u00e1 el comprobante por WhatsApp:</p>
                    <a href="%s" style="display: inline-block; background: #25d366; color: #ffffff; padding: 12px 24px; text-decoration: none; border-radius: 25px; font-weight: 600; font-size: 14px;">
                        Enviar comprobante
                    </a>
                </div>
            </div>
            """.formatted(titular, cvu, alias, WHATSAPP_LINK);
    }

    // ============================================================
    // EMAIL ADMIN
    // ============================================================
    private String generarHtmlAdminProfesional(Pedido pedido, String nombre, String telefono, String emailFormulario) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaFormateada = pedido.getFecha().format(formatter);
        String mapsLink = generarGoogleMapsLink(pedido.getDireccionEntrega());
        String metodoPagoFormateado = formatearMetodoPago(pedido.getMetodoPago());

        StringBuilder filas = new StringBuilder();
        for (DetallePedido det : pedido.getDetalles()) {
            String talleInfo = "";
            if (det.getNombreTamano() != null && !det.getNombreTamano().isBlank()) {
                talleInfo = " <span style=\"color: #64748b; font-size: 12px;\">(" + det.getNombreTamano() + ")</span>";
            }
            filas.append("""
                <tr>
                    <td style="padding: 12px 8px; border-bottom: 1px solid #e2e8f0;">
                        <div style="font-weight: 600; color: #0f172a;">%s%s</div>
                    </td>
                    <td style="padding: 12px 8px; border-bottom: 1px solid #e2e8f0; text-align: center; color: #475569;">%d</td>
                    <td style="padding: 12px 8px; border-bottom: 1px solid #e2e8f0; text-align: right; color: #475569;">$%.2f</td>
                </tr>
                """.formatted(
                    det.getProducto().getNombre(),
                    talleInfo,
                    det.getCantidad(),
                    det.getSubtotal()
            ));
        }

        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="margin: 0; padding: 0; background-color: #f1f5f9; font-family: 'Segoe UI', Arial, sans-serif;">
                <table align="center" width="100%%" cellpadding="0" cellspacing="0">
                <tr><td align="center" style="padding: 30px 15px;">
                    <table width="600" cellpadding="0" cellspacing="0" style="max-width: 600px; width: 100%%;">

                        <!-- HEADER -->
                        <tr>
                            <td style="background: linear-gradient(135deg, #4f46e5, #6366f1); padding: 30px; text-align: center; border-radius: 16px 16px 0 0;">
                                <h1 style="margin: 0; color: #ffffff; font-size: 24px; letter-spacing: 1px;">Nuevo Pedido #%d</h1>
                                <p style="margin: 8px 0 0; color: #c7d2fe; font-size: 14px;">%s</p>
                            </td>
                        </tr>

                        <!-- BODY -->
                        <tr>
                            <td style="background: #ffffff; padding: 30px;">

                                <!-- STATUS BADGE -->
                                <div style="background: #fef3c7; border-left: 4px solid #f59e0b; border-radius: 8px; padding: 14px 18px; margin-bottom: 25px;">
                                    <div style="font-size: 13px; color: #92400e; text-transform: uppercase; letter-spacing: 0.5px; font-weight: 600;">Estado</div>
                                    <div style="font-size: 18px; font-weight: 700; color: #78350f;">%s</div>
                                </div>

                                <!-- CUSTOMER INFO -->
                                <h3 style="color: #4f46e5; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; margin: 0 0 12px;">Datos del Cliente</h3>
                                <div style="background: #f8fafc; border-radius: 10px; padding: 18px; margin-bottom: 25px;">
                                    <table width="100%%" cellpadding="3" cellspacing="0">
                                        <tr>
                                            <td style="color: #64748b; font-size: 13px; width: 100px;">Nombre:</td>
                                            <td style="font-weight: 600; color: #0f172a; font-size: 14px;">%s</td>
                                        </tr>
                                        <tr>
                                            <td style="color: #64748b; font-size: 13px;">WhatsApp:</td>
                                            <td>
                                                <a href="https://wa.me/%s" style="color: #10b981; font-weight: 600; text-decoration: none;">%s</a>
                                            </td>
                                        </tr>
                                        <tr>
                                            <td style="color: #64748b; font-size: 13px;">Email:</td>
                                            <td><a href="mailto:%s" style="color: #6366f1; text-decoration: none;">%s</a></td>
                                        </tr>
                                        <tr>
                                            <td style="color: #64748b; font-size: 13px;">Tel\u00e9fono:</td>
                                            <td style="color: #0f172a; font-weight: 500;">%s</td>
                                        </tr>
                                    </table>
                                </div>

                                <!-- DELIVERY -->
                                <h3 style="color: #4f46e5; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; margin: 0 0 12px;">Direcci\u00f3n de Entrega</h3>
                                <div style="background: #f8fafc; border-radius: 10px; padding: 16px; margin-bottom: 25px;">
                                    <p style="margin: 0 0 6px; color: #334155; font-size: 14px;">
                                        %s
                                    </p>
                                    %s
                                </div>

                                <!-- PAYMENT -->
                                <h3 style="color: #4f46e5; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; margin: 0 0 12px;">Pago</h3>
                                <div style="background: #f0fdfa; border: 1px solid #2dd4bf; border-radius: 10px; padding: 14px 18px; margin-bottom: 25px;">
                                    <table width="100%%" cellpadding="3" cellspacing="0">
                                        <tr>
                                            <td style="color: #64748b; font-size: 13px; width: 120px;">M\u00e9todo:</td>
                                            <td style="font-weight: 600; color: #0f766e; font-size: 14px;">%s</td>
                                        </tr>
                                        <tr>
                                            <td style="color: #64748b; font-size: 13px;">Total:</td>
                                            <td style="font-weight: 700; color: #0f172a; font-size: 16px;">$%.2f</td>
                                        </tr>
                                    </table>
                                </div>

                                <!-- PRODUCTS -->
                                <h3 style="color: #4f46e5; font-size: 14px; text-transform: uppercase; letter-spacing: 1px; margin: 0 0 12px;">Productos</h3>
                                <table width="100%%" cellpadding="0" cellspacing="0" style="border-collapse: collapse;">
                                    <thead>
                                        <tr style="background: #f1f5f9;">
                                            <th style="padding: 10px 8px; text-align: left; font-size: 11px; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px;">Producto</th>
                                            <th style="padding: 10px 8px; text-align: center; font-size: 11px; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px;">Cant.</th>
                                            <th style="padding: 10px 8px; text-align: right; font-size: 11px; color: #64748b; text-transform: uppercase; letter-spacing: 0.5px;">Subtotal</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        %s
                                    </tbody>
                                    <tfoot>
                                        <tr>
                                            <td colspan="2" style="padding: 14px 8px; text-align: right; font-size: 15px; font-weight: 700; color: #0f172a; border-top: 2px solid #4f46e5;">TOTAL</td>
                                            <td style="padding: 14px 8px; text-align: right; font-size: 17px; font-weight: 800; color: #0f172a; border-top: 2px solid #4f46e5;">$%.2f</td>
                                        </tr>
                                    </tfoot>
                                </table>

                                <!-- ACTIONS -->
                                <div style="text-align: center; margin: 30px 0 10px;">
                                    <a href="https://tmcreattion.netlify.app/src/pages/admin/pedidos/pedidos.html" style="display: inline-block; background: #4f46e5; color: #ffffff; padding: 14px 28px; text-decoration: none; border-radius: 30px; font-weight: 700; font-size: 14px;">
                                        Gestionar Pedido en el Panel
                                    </a>
                                </div>

                            </td>
                        </tr>

                        <!-- FOOTER -->
                        <tr>
                            <td style="background: #0f172a; padding: 20px 30px; text-align: center; border-radius: 0 0 16px 16px;">
                                <p style="margin: 0; color: #64748b; font-size: 12px;">TM Creation \u00a9 2026 \u2013 Panel de Administraci\u00f3n</p>
                            </td>
                        </tr>

                    </table>
                </td></tr>
                </table>
            </body>
            </html>
            """.formatted(
                pedido.getId(),
                fechaFormateada,
                pedido.getEstado().toString(),
                nombre,
                telefono.replaceAll("[^0-9]", ""), telefono,
                emailFormulario, emailFormulario,
                pedido.getTelefonoContacto() != null ? pedido.getTelefonoContacto() : "No especificado",
                pedido.getDireccionEntrega() != null ? pedido.getDireccionEntrega() : "No especificada",
                mapsLink != null
                    ? "<a href=\"" + mapsLink + "\" style=\"color: #2563eb; font-size: 13px; text-decoration: none;\">Ver en Google Maps</a>"
                    : "",
                metodoPagoFormateado,
                pedido.getTotal(),
                filas.toString(),
                pedido.getTotal()
        );
    }

    // ============================================================
    // EMAIL RESENA
    // ============================================================
    private String generarHtmlNuevaResena(String nombreUsuario, String nombreProducto, int estrellas, String comentario) {
        String estrellasHtml = "\u2605".repeat(estrellas) + "\u2606".repeat(5 - estrellas);

        return """
            <!DOCTYPE html>
            <html>
            <head><meta charset="UTF-8"></head>
            <body style="margin: 0; padding: 0; background-color: #f1f5f9; font-family: 'Segoe UI', Arial, sans-serif;">
                <table align="center" width="100%%" cellpadding="0" cellspacing="0">
                <tr><td align="center" style="padding: 30px 15px;">
                    <table width="600" cellpadding="0" cellspacing="0" style="max-width: 600px; width: 100%%;">

                        <!-- HEADER -->
                        <tr>
                            <td style="background: linear-gradient(135deg, #f59e0b, #fbbf24); padding: 30px; text-align: center; border-radius: 16px 16px 0 0;">
                                <h1 style="margin: 0; color: #78350f; font-size: 24px;">Nueva Rese\u00f1a Recibida</h1>
                                <p style="margin: 8px 0 0; color: #92400e; font-size: 14px;">%s %s %s</p>
                            </td>
                        </tr>

                        <!-- BODY -->
                        <tr>
                            <td style="background: #ffffff; padding: 30px;">

                                <div style="background: #fffbeb; border: 1px solid #fde68a; border-radius: 10px; padding: 20px; margin-bottom: 25px;">
                                    <table width="100%%" cellpadding="4" cellspacing="0">
                                        <tr>
                                            <td style="color: #64748b; font-size: 13px; width: 100px;">Usuario:</td>
                                            <td style="font-weight: 600; color: #0f172a; font-size: 14px;">%s</td>
                                        </tr>
                                        <tr>
                                            <td style="color: #64748b; font-size: 13px;">Producto:</td>
                                            <td style="font-weight: 600; color: #0f172a; font-size: 14px;">%s</td>
                                        </tr>
                                        <tr>
                                            <td style="color: #64748b; font-size: 13px;">Puntuaci\u00f3n:</td>
                                            <td style="color: #f59e0b; font-size: 20px;">%s</td>
                                        </tr>
                                    </table>
                                </div>

                                %s

                                <div style="text-align: center; margin: 30px 0 10px;">
                                    <a href="https://tmcreattion.netlify.app/src/pages/admin/reviews/reviews.html" style="display: inline-block; background: #2dd4bf; color: #0f172a; padding: 14px 28px; text-decoration: none; border-radius: 30px; font-weight: 700; font-size: 14px;">
                                        Ver Todas las Rese\u00f1as
                                    </a>
                                </div>

                            </td>
                        </tr>

                        <!-- FOOTER -->
                        <tr>
                            <td style="background: #0f172a; padding: 20px 30px; text-align: center; border-radius: 0 0 16px 16px;">
                                <p style="margin: 0; color: #64748b; font-size: 12px;">TM Creation \u00a9 2026</p>
                            </td>
                        </tr>

                    </table>
                </td></tr>
                </table>
            </body>
            </html>
            """.formatted(
                nombreUsuario, "rese\u00f1\u00f3", nombreProducto,
                nombreUsuario,
                nombreProducto,
                estrellasHtml,
                comentario != null && !comentario.isEmpty()
                    ? """
                    <h3 style="color: #1e293b; font-size: 15px; margin: 0 0 10px;">Comentario</h3>
                    <div style="background: #f8fafc; border-left: 4px solid #f59e0b; border-radius: 8px; padding: 16px; margin-bottom: 10px;">
                        <p style="margin: 0; color: #475569; font-style: italic; line-height: 1.6;">%s</p>
                    </div>
                    """.formatted(comentario)
                    : ""
        );
    }
}
