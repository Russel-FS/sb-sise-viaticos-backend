package com.viatico.proyect.application.service.impl;

import com.viatico.proyect.application.service.interfaces.EmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.url:http://localhost:8080}")
    private String appUrl;

    @Override
    public void enviarEmailRecuperacion(String to, String token) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            String resetUrl = appUrl + "/reset-password?token=" + token;

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject("Recuperación de Contraseña - ISS Viáticos");

            String content = "<!DOCTYPE html>" +
                    "<html>" +
                    "<head>" +
                    "  <meta charset='UTF-8'>" +
                    "  <style>" +
                    "    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; margin: 0; padding: 0; background-color: #ffffff; color: #1d1d1f; }"
                    +
                    "    .container { max-width: 600px; margin: 60px auto; padding: 0 20px; text-align: center; }" +
                    "    .title { font-size: 32px; font-weight: 600; letter-spacing: -0.02em; margin-bottom: 16px; color: #000000; }"
                    +
                    "    .text { font-size: 19px; line-height: 1.5; color: #86868b; margin-bottom: 40px; }" +
                    "    .button { background-color: #0066cc; color: #ffffff !important; padding: 14px 32px; text-decoration: none; border-radius: 12px; font-weight: 600; font-size: 17px; display: inline-block; }"
                    +
                    "    .footer { margin-top: 80px; padding-top: 20px; border-top: 1px solid #d2d2d7; font-size: 12px; color: #86868b; }"
                    +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='container'>" +
                    "    <h1 class='title'>Restablecer contraseña.</h1>" +
                    "    <p class='text'>Para volver a tener acceso a tu cuenta de ISS Viáticos, haz clic en el siguiente botón.</p>"
                    +
                    "    <div style='margin: 40px 0;'>" +
                    "      <a href='" + resetUrl + "' class='button'>Continuar</a>" +
                    "    </div>" +
                    "    <p class='text' style='font-size: 14px;'>Este enlace expirará en 30 minutos.</p>" +
                    "    <div class='footer'>" +
                    "      <p>Copyright © 2025 ISS Viáticos. Todos los derechos reservados.</p>" +
                    "    </div>" +
                    "  </div>" +
                    "</body>" +
                    "</html>";

            helper.setText(content, true);
            mailSender.send(message);
            log.info("Email de recuperación enviado a: {}", to);

        } catch (MessagingException e) {
            log.error("Error al enviar email a {}: {}", to, e.getMessage());
            throw new RuntimeException("No se pudo enviar el correo de recuperación.");
        }
    }
}
