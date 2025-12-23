package com.viatico.proyect.controller;

import com.viatico.proyect.entity.SolicitudComision;
import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @GetMapping
    public String listarSolicitudes(Model model, @AuthenticationPrincipal UsuarioPrincipal user) {
        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("currentUser", user);

        if (isAdmin) {
            model.addAttribute("solicitudes", solicitudService.listarTodas());
        } else {
            model.addAttribute("solicitudes", solicitudService.listarPorEmpleado(user.getIdEmpleado()));
        }

        return "solicitudes/lista";
    }

    @GetMapping("/nueva")
    public String nuevaSolicitudForm(Model model) {
        model.addAttribute("zonas", solicitudService.listarZonas());
        return "solicitudes/nueva";
    }

    @PostMapping("/nueva")
    public String guardarSolicitud(
            @RequestParam String motivoViaje,
            @RequestParam String fechaInicio,
            @RequestParam List<Long> idZonas,
            @RequestParam List<String> ciudades,
            @RequestParam List<Integer> noches,
            @AuthenticationPrincipal UsuarioPrincipal user) {

        solicitudService.guardarNuevaSolicitud(
                motivoViaje,
                fechaInicio,
                idZonas,
                ciudades,
                noches,
                user);

        return "redirect:/?success";
    }

    @PostMapping("/enviar/{id}")
    public String enviarAprobacion(@PathVariable Long id) {
        solicitudService.enviarAprobacion(id);
        return "redirect:/?sent";
    }

    @GetMapping("/{id}")
    public String verDetalle(@PathVariable Long id, Model model, @AuthenticationPrincipal UsuarioPrincipal user) {
        SolicitudComision sol = solicitudService.obtenerPorId(id);
        model.addAttribute("sol", sol);

        boolean isAdmin = user.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "solicitudes/detalle";
    }
}
