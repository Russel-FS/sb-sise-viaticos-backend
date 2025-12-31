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
                    "    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; margin: 0; padding: 0; background-color: #f5f5f7; color: #1d1d1f; }"
                    +
                    "    .container { max-width: 600px; margin: 40px auto; padding: 40px; background-color: #ffffff; border-radius: 20px; box-shadow: 0 4px 24px rgba(0,0,0,0.04); }"
                    +
                    "    .header { text-align: center; margin-bottom: 32px; }" +
                    "    .logo-text { font-size: 24px; font-weight: 600; letter-spacing: -0.02em; color: #000000; }" +
                    "    .content { line-height: 1.6; font-size: 16px; }" +
                    "    .title { font-size: 28px; font-weight: 700; letter-spacing: -0.01em; margin-bottom: 16px; text-align: center; }"
                    +
                    "    .text { color: #515154; margin-bottom: 32px; text-align: center; }" +
                    "    .button-container { text-align: center; margin: 40px 0; }" +
                    "    .button { background-color: #0071e3; color: #ffffff !important; padding: 12px 32px; text-decoration: none; border-radius: 980px; font-weight: 600; font-size: 14px; transition: background-color 0.2s; display: inline-block; }"
                    +
                    "    .footer { margin-top: 48px; border-top: 1px solid #d2d2d7; padding-top: 24px; font-size: 12px; color: #86868b; text-align: center; }"
                    +
                    "    .expiry { color: #ff3b30; font-weight: 500; }" +
                    "  </style>" +
                    "</head>" +
                    "<body>" +
                    "  <div class='container'>" +
                    "    <div class='header'><span class='logo-text'>ISS Viáticos</span></div>" +
                    "    <div class='content'>" +
                    "      <h1 class='title'>Restablecer Contraseña</h1>" +
                    "      <p class='text'>Has solicitado restablecer la contraseña de tu cuenta. Haz clic en el siguiente enlace para continuar:</p>"
                    +
                    "      <div class='button-container'>" +
                    "        <a href='" + resetUrl + "' class='button'>Restablecer ahora</a>" +
                    "      </div>" +
                    "      <p class='text' style='font-size: 14px;'>Este enlace <span class='expiry'>expirará en 30 minutos</span> por motivos de seguridad.</p>"
                    +
                    "    </div>" +
                    "    <div class='footer'>" +
                    "      <p>Si no solicitaste este cambio, puedes ignorar este correo de forma segura.</p>" +
                    "      <p>&copy; 2025 ISS Viáticos. Todos los derechos reservados.</p>" +
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
