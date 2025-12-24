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
                var lista = solicitudService.listarPorEmpleado(user.getIdEmpleado());
                model.addAttribute("solicitudes", lista);
                model.addAttribute("viewTitle", "Mis Solicitudes Recientes");

                // estadisticas
                model.addAttribute("totalPendientes",
                        lista.stream().filter(s -> s.getEstado().name().equals("PENDIENTE")).count());
                model.addAttribute("totalAprobados",
                        lista.stream().filter(s -> s.getEstado().name().equals("APROBADO")).count());
                model.addAttribute("totalLiquidados",
                        lista.stream().filter(s -> s.getEstado().name().equals("LIQUIDADO")).count());
            }
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
