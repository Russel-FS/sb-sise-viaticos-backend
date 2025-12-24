package com.viatico.proyect.controller;

import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.SolicitudService;
import lombok.RequiredArgsConstructor;

import java.util.List;

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
                var todas = solicitudService.listarTodas();
                model.addAttribute("solicitudes", todas);
                model.addAttribute("viewTitle", "Todas las Solicitudes (Admin)");

                // Datos para Gráfico Admin
                long pendientes = todas.stream().filter(s -> s.getEstado().name().equals("PENDIENTE")).count();
                long aprobados = todas.stream().filter(s -> s.getEstado().name().equals("APROBADO")).count();
                long rechazados = todas.stream().filter(s -> s.getEstado().name().equals("RECHAZADO")).count();
                long liquidados = todas.stream().filter(s -> s.getEstado().name().equals("LIQUIDADO")).count();

                model.addAttribute("chartLabels", List.of("Pendientes", "Aprobados", "Rechazados", "Liquidados"));
                model.addAttribute("chartData", List.of(pendientes, aprobados, rechazados, liquidados));

            } else {
                var lista = solicitudService.listarPorEmpleado(user.getIdEmpleado());
                model.addAttribute("solicitudes", lista);
                model.addAttribute("viewTitle", "Mis Solicitudes Recientes");

                // estadisticas tarjetas
                model.addAttribute("totalPendientes",
                        lista.stream().filter(s -> s.getEstado().name().equals("PENDIENTE")).count());
                model.addAttribute("totalAprobados",
                        lista.stream().filter(s -> s.getEstado().name().equals("APROBADO")).count());
                model.addAttribute("totalLiquidados",
                        lista.stream().filter(s -> s.getEstado().name().equals("LIQUIDADO")).count());

                // Datos para Gráfico Empleado
                long gPendientes = lista.stream().filter(s -> s.getEstado().name().equals("PENDIENTE")).count();
                long gAprobados = lista.stream().filter(s -> s.getEstado().name().equals("APROBADO")).count();
                long gRechazados = lista.stream().filter(s -> s.getEstado().name().equals("RECHAZADO")).count();
                long gLiquidados = lista.stream().filter(s -> s.getEstado().name().equals("LIQUIDADO")).count();

                model.addAttribute("chartLabels", List.of("Pendientes", "Aprobados", "Rechazados", "Liquidados"));
                model.addAttribute("chartData", List.of(gPendientes, gAprobados, gRechazados, gLiquidados));
            }
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
