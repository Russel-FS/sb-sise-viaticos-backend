package com.viatico.proyect.controller;

import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/aprobaciones")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminAprobacionController {

    private final SolicitudService solicitudService;

    @GetMapping
    public String listarPendientes(Model model) {
        model.addAttribute("pendientes", solicitudService.listarPendientes());
        return "admin/aprobaciones/lista";
    }

    @PostMapping("/aprobar/{id}")
    public String aprobar(@PathVariable Long id, @AuthenticationPrincipal UsuarioPrincipal user) {
        solicitudService.aprobar(id, user.getUsername());
        return "redirect:/admin/aprobaciones?approved";
    }

    @PostMapping("/rechazar/{id}")
    public String rechazar(@PathVariable Long id,
            @RequestParam(required = false) String comentario,
            @AuthenticationPrincipal UsuarioPrincipal user) {
        solicitudService.rechazar(id, comentario, user.getUsername());
        return "redirect:/admin/aprobaciones?rejected";
    }
}
