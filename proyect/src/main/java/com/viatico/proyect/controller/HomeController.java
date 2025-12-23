package com.viatico.proyect.controller;

import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {

    private final SolicitudService solicitudService;

    @GetMapping("/")
    public String index(Model model, @AuthenticationPrincipal UsuarioPrincipal user) {
        if (user != null) {
            model.addAttribute("currentUser", user);
            boolean isAdmin = user.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            model.addAttribute("isAdmin", isAdmin);

            if (isAdmin) {
                model.addAttribute("solicitudes", solicitudService.listarTodas());
                model.addAttribute("viewTitle", "Todas las Solicitudes (Admin)");
            } else {
                model.addAttribute("solicitudes", solicitudService.listarPorEmpleado(user.getIdEmpleado()));
                model.addAttribute("viewTitle", "Mis Solicitudes Recientes");
            }
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
