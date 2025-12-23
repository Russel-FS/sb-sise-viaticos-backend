package com.viatico.proyect.controller;

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

@Controller
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;

    @GetMapping("/nueva")
    public String nuevaSolicitudForm(Model model) {
        model.addAttribute("zonas", solicitudService.listarZonas());
        return "solicitudes/nueva";
    }

    @PostMapping("/nueva")
    public String guardarSolicitud(
            @RequestParam String motivoViaje,
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam Long idZona,
            @RequestParam(required = false) String ciudad,
            @AuthenticationPrincipal UsuarioPrincipal user) {

        solicitudService.guardarNuevaSolicitud(
                motivoViaje,
                fechaInicio,
                fechaFin,
                idZona,
                ciudad,
                user);

        return "redirect:/?success";
    }

    @PostMapping("/enviar/{id}")
    public String enviarAprobacion(@PathVariable Long id) {
        solicitudService.enviarAprobacion(id);
        return "redirect:/?sent";
    }
}
