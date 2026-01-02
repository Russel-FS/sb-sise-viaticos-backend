package com.viatico.proyect.infrastructure.controller;

import com.viatico.proyect.application.service.interfaces.EmailService;
import com.viatico.proyect.domain.repositories.PasswordResetTokenRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class PasswordResetController {

    private final PasswordResetTokenRepository tokenRepository;
    private final EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPassword() {
        return "auth/forgot-password";
    }

    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam String email, RedirectAttributes ra) {
        String token = UUID.randomUUID().toString();
        try {
            Long idUsuario = tokenRepository.crearToken(email, token, 1);

            if (idUsuario != null) {
                emailService.enviarEmailRecuperacion(email, token);
                ra.addFlashAttribute("info",
                        "Si el correo electrónico existe en nuestro sistema, recibirás instrucciones para restablecer tu contraseña.");
            } else {
                ra.addFlashAttribute("info",
                        "Si el correo electrónico existe en nuestro sistema, recibirás instrucciones para restablecer tu contraseña.");
            }
        } catch (Exception e) {
            ra.addFlashAttribute("error",
                    "Hubo un problema al enviar el correo técnico. Por favor, inténtalo más tarde.");
        }

        return "redirect:/forgot-password";
    }

    @GetMapping("/reset-password")
    public String showResetPassword(@RequestParam String token, Model model) {
        Long idUsuario = tokenRepository.validarToken(token);
        if (idUsuario == null) {
            model.addAttribute("error", "El enlace de recuperación no es válido o ha expirado.");
            return "auth/forgot-password";
        }
        model.addAttribute("token", token);
        return "auth/reset-password";
    }

    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam String token,
            @RequestParam String password,
            @RequestParam String confirmPassword,
            RedirectAttributes ra,
            Model model) {
        if (!password.equals(confirmPassword)) {
            model.addAttribute("error", "Las contraseñas no coinciden.");
            model.addAttribute("token", token);
            return "auth/reset-password";
        }

        Long idUsuario = tokenRepository.validarToken(token);
        if (idUsuario == null) {
            ra.addFlashAttribute("error", "El enlace de recuperación no es válido o ha expirado.");
            return "redirect:/forgot-password";
        }

        tokenRepository.actualizarPassword(idUsuario, password);
        tokenRepository.usarToken(token);

        ra.addFlashAttribute("success", "Tu contraseña ha sido actualizada correctamente. Ya puedes iniciar sesión.");
        return "redirect:/login";
    }
}
