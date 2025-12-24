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
                // Lista solo las últimas 5
                model.addAttribute("solicitudes", solicitudService.listarRecientes(null));
                model.addAttribute("viewTitle", "Actividad Reciente (Admin)");

                // Conteos directos de BD
                long pendientes = solicitudService.contarPorEstado("PENDIENTE", null);
                long aprobados = solicitudService.contarPorEstado("APROBADO", null);
                long rechazados = solicitudService.contarPorEstado("RECHAZADO", null);
                long liquidados = solicitudService.contarPorEstado("LIQUIDADO", null);

                // Estadísticas para tarjetas ADMIN (si no existen en HTML admin, no rompe nada)
                model.addAttribute("totalPendientes", pendientes);
                model.addAttribute("totalAprobados", aprobados);
                model.addAttribute("totalLiquidados", liquidados);

                model.addAttribute("chartLabels", List.of("Pendientes", "Aprobados", "Rechazados", "Liquidados"));
                model.addAttribute("chartData", List.of(pendientes, aprobados, rechazados, liquidados));

            } else {
                Long empId = user.getIdEmpleado();
                // Lista solo las últimas 5
                model.addAttribute("solicitudes", solicitudService.listarRecientes(empId));
                model.addAttribute("viewTitle", "Recientes");

                // Conteos directos de BD
                long pendientes = solicitudService.contarPorEstado("PENDIENTE", empId);
                long aprobados = solicitudService.contarPorEstado("APROBADO", empId);
                long rechazados = solicitudService.contarPorEstado("RECHAZADO", empId);
                long liquidados = solicitudService.contarPorEstado("LIQUIDADO", empId);

                // Estadísticas para tarjetas
                model.addAttribute("totalPendientes", pendientes);
                model.addAttribute("totalAprobados", aprobados);
                model.addAttribute("totalLiquidados", liquidados);

                model.addAttribute("chartLabels", List.of("Pendientes", "Aprobados", "Rechazados", "Liquidados"));
                model.addAttribute("chartData", List.of(pendientes, aprobados, rechazados, liquidados));
            }
        }
        return "index";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
