package com.viatico.proyect.controller;

import com.viatico.proyect.entity.ZonaGeografica;
import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.interfaces.ZonaGeograficaService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/zonas")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminZonaController {

    private final ZonaGeograficaService zonaService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("zonas", zonaService.listarTodas());
        return "admin/zonas/lista";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute ZonaGeografica zona,
            @AuthenticationPrincipal UsuarioPrincipal user) {
        zonaService.guardar(zona, user.getUsername());
        return "redirect:/admin/zonas?success";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        try {
            zonaService.eliminar(id);
            return "redirect:/admin/zonas?deleted";
        } catch (Exception e) {
            return "redirect:/admin/zonas?error=relacionado";
        }
    }
}
