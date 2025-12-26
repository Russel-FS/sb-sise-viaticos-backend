package com.viatico.proyect.controller;

import com.viatico.proyect.entity.NivelJerarquico;
import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.interfaces.NivelJerarquicoService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/niveles")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminNivelController {

    private final NivelJerarquicoService nivelService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("niveles", nivelService.listarTodos());
        return "admin/niveles/lista";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute NivelJerarquico nivel,
            @AuthenticationPrincipal UsuarioPrincipal user) {
        nivelService.guardar(nivel, user.getUsername());
        return "redirect:/admin/niveles?success";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        try {
            nivelService.eliminar(id);
            return "redirect:/admin/niveles?deleted";
        } catch (Exception e) {
            return "redirect:/admin/niveles?error=relacionado";
        }
    }
}
