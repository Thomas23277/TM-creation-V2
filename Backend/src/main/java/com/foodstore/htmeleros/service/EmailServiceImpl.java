package com.foodstore.htmeleros.service;

import com.foodstore.htmeleros.entity.DetallePedido;
import com.foodstore.htmeleros.entity.Pedido;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import java.time.format.DateTimeFormatter;

@Service
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String emailFrom;

    @Value("${app.pagos.cvu:}")
    private String cvu;

    @Value("${app.pagos.alias:}")
    private String alias;

    @Value("${app.pagos.titular:}")
    private String titular;

    // Enlace directo de WhatsApp
    private static final String WHATSAPP_LINK = "https://wa.me/5492616524913";

    @Override
    public void enviarConfirmacionCliente(Pedido pedido, String emailCliente, String nombreCliente) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(emailCliente);
            helper.setSubject("✅ Confirmación de Pedido #" + pedido.getId() + " - TM Creation");

            helper.setText(generarHtmlClienteProfesional(pedido, nombreCliente), true);
            mailSender.send(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void enviarNotificacionAdmin(Pedido pedido, String nombreFormulario, String telefonoFormulario, String emailFormulario) {
        try {
            MimeMessage mensaje = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mensaje, true, "UTF-8");

            helper.setFrom(emailFrom);
            helper.setTo(emailFrom);
            helper.setSubject("📦 NUEVO PEDIDO #" + pedido.getId() + " - " + nombreFormulario);

            helper.setText(generarHtmlAdminProfesional(pedido, nombreFormulario, telefonoFormulario, emailFormulario), true);
            mailSender.send(mensaje);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generarHtmlClienteProfesional(Pedido pedido, String nombre) {
        return """
            <div style="font-family: 'Segoe UI', Arial, sans-serif; color: #1f2937; max-width: 650px; margin: auto; border: 1px solid #e5e7eb; border-radius: 12px; overflow: hidden; box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1);">
                <div style="background: linear-gradient(135deg, #0f172a, #1e293b); padding: 30px; text-align: center; color: white;">
                    <h1 style="margin: 0; font-size: 26px; letter-spacing: 1px;">TM CREATION</h1>
                    <p style="margin-top: 10px; opacity: 0.9;">¡Gracias por tu compra, %s!</p>
                </div>

                <div style="padding: 30px; line-height: 1.6;">
                    <h2 style="color: #0f766e; border-bottom: 2px solid #2dd4bf; padding-bottom: 10px; font-size: 20px;">Confirmación de Pedido #%d</h2>
                    <p>Tu pedido ha sido recibido correctamente y se encuentra en estado <b>PENDIENTE</b> de validación de pago.</p>

                    <div style="background-color: #f0fdfa; border-left: 4px solid #2dd4bf; padding: 20px; margin: 25px 0; border-radius: 0 8px 8px 0;">
                        <h3 style="margin-top: 0; color: #134e4a; font-size: 16px;">💳 Información para Transferencia</h3>
                        <p style="margin-bottom: 8px;">Realiza el pago para comenzar con la producción:</p>
                        <ul style="list-style: none; padding: 0; margin: 0;">
                            <li><b>Titular:</b> %s</li>
                            <li><b>CVU:</b> <code style="background: #e2e8f0; padding: 2px 5px; border-radius: 4px;">%s</code></li>
                            <li><b>Alias:</b> <i>%s</i></li>
                        </ul>
                        <p style="margin-top: 15px; font-weight: bold;">
                            👉 Envía el comprobante por WhatsApp: 
                            <a href="%s" style="color: #25d366; text-decoration: none; font-size: 16px;">Click aquí para enviar comprobante</a>
                        </p>
                    </div>

                    <h3 style="color: #111827; font-size: 17px; margin-top: 25px;">🔄 Proceso de confirmación y envío</h3>
                    <ul style="padding-left: 20px; color: #4b5563;">
                        <li>Al recibir tu comprobante verificaremos el pago.</li>
                        <li>Te solicitaremos confirmación detallada de tu dirección de entrega.</li>
                        <li>Una vez validado todo, coordinaremos el método y tiempos de envío.</li>
                        <li>Recibirás confirmación final con detalles logísticos.</li>
                    </ul>

                    <h3 style="color: #111827; font-size: 17px; margin-top: 25px;">🚚 Política de envíos</h3>
                    <ul style="padding-left: 20px; color: #4b5563;">
                        <li><b>Envíos en Mendoza (Gran Mendoza):</b> Tarifa fija de $3.500.</li>
                        <li><b>Zonas fuera del radio habitual:</b> Podremos coordinar un punto intermedio.</li>
                        <li><b>Resto del país/mundo:</b> Costo acordado según destino y logística.</li>
                    </ul>

                    <h3 style="color: #111827; font-size: 17px; margin-top: 25px;">🛡️ Garantía</h3>
                    <p style="color: #4b5563; font-size: 14px;">
                        Garantía de <b>30 días</b> por fallas de fabricación. No cubre daños por mal uso, golpes o manipulación indebida posterior a la entrega.
                    </p>

                    <div style="text-align: center; margin-top: 40px; padding: 20px; border-top: 1px solid #eee;">
                        <p style="font-size: 14px; color: #6b7280; margin-bottom: 15px;">¿Tienes alguna duda adicional?</p>
                        <a href="%s" style="background-color: #2dd4bf; color: white; padding: 12px 25px; text-decoration: none; border-radius: 30px; font-weight: bold; display: inline-block;">Consultar por WhatsApp</a>
                    </div>
                </div>
                <div style="background-color: #f9fafb; padding: 20px; text-align: center; font-size: 12px; color: #9ca3af;">
                    TM Creation © 2026 – Todos los derechos reservados
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
                    <h2 style="color: #1e293b; border-bottom: 3px solid #6366f1; padding-bottom: 10px; margin-top: 0;">📦 Nuevo Pedido #%d</h2>
                    <p style="color: #64748b;"><b>Fecha:</b> %s</p>
                    
                    <div style="background-color: #f8fafc; padding: 15px; border-radius: 8px; margin: 20px 0;">
                        <h3 style="color: #4f46e5; font-size: 16px; margin-top: 0;">👤 Datos del Cliente (Formulario)</h3>
                        <p style="margin: 5px 0;"><b>Nombre:</b> %s</p>
                        <p style="margin: 5px 0;"><b>WhatsApp:</b> <a href="https://wa.me/%s" style="color: #10b981; text-decoration: none; font-weight: bold;">%s (Chat)</a></p>
                        <p style="margin: 5px 0;"><b>Email Contacto:</b> <a href="mailto:%s" style="color: #6366f1;">%s</a></p>
                        <p style="margin: 5px 0;"><b>Dirección:</b> %s</p>
                    </div>

                    <h3 style="color: #1e293b; font-size: 16px;">🛒 Detalle de Productos</h3>
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