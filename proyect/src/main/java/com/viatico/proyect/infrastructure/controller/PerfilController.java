package com.viatico.proyect.infrastructure.controller;

import com.viatico.proyect.application.service.interfaces.EmpleadoService;
import com.viatico.proyect.config.UsuarioPrincipal;
import com.viatico.proyect.domain.entity.Empleado;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/perfil")
@RequiredArgsConstructor
public class PerfilController {

    private final EmpleadoService empleadoService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping
    public String verPerfil(Model model, @AuthenticationPrincipal UsuarioPrincipal user) {
        Empleado empleado = empleadoService.obtenerPorId(user.getIdEmpleado());
        model.addAttribute("empleado", empleado);
        model.addAttribute("currentUser", user);

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "perfil";
    }

    @PostMapping("/actualizar")
    public String actualizarPerfil(
            @AuthenticationPrincipal UsuarioPrincipal user,
            @RequestParam String email,
            @RequestParam String cuentaBancaria,
            @RequestParam(required = false) String currentPassword,
            @RequestParam(required = false) String newPassword,
            @RequestParam(required = false) String confirmPassword,
            RedirectAttributes redirectAttributes) {

        try {
            Empleado empleado = empleadoService.obtenerPorId(user.getIdEmpleado());

            empleado.setEmail(email);
            empleado.setCuentaBancaria(cuentaBancaria);

            String passwordToUpdate = null;
            if (newPassword != null && !newPassword.trim().isEmpty()) {
                if (currentPassword == null || currentPassword.trim().isEmpty()) {
                    redirectAttributes.addFlashAttribute("error",
                            "Debe ingresar su contraseña actual para realizar cambios de seguridad.");
                    return "redirect:/perfil";
                }

                if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
                    redirectAttributes.addFlashAttribute("error", "La contraseña actual es incorrecta.");
                    return "redirect:/perfil";
                }

                if (!newPassword.equals(confirmPassword)) {
                    redirectAttributes.addFlashAttribute("error", "Las nuevas contraseñas no coinciden.");
                    return "redirect:/perfil";
                }

                passwordToUpdate = newPassword;
            }

            empleadoService.guardar(empleado, passwordToUpdate, null, user.getUsername(), user.getUsername());

            redirectAttributes.addFlashAttribute("success", "Perfil actualizado correctamente.");

        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al actualizar el perfil: " + e.getMessage());
        }

        return "redirect:/perfil";
    }
}
