package com.viatico.proyect.controller;

import com.viatico.proyect.entity.TipoGasto;
import com.viatico.proyect.service.TipoGastoService;
import com.viatico.proyect.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/conceptos")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminTipoGastoController {

    private final TipoGastoService tipoGastoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("conceptos", tipoGastoService.listarTodos());
        return "admin/conceptos/lista";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute TipoGasto tipoGasto,
            @AuthenticationPrincipal UsuarioPrincipal user) {
        tipoGastoService.guardar(tipoGasto, user.getUsername());
        return "redirect:/admin/conceptos?success";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        try {
            tipoGastoService.eliminar(id);
            return "redirect:/admin/conceptos?deleted";
        } catch (Exception e) {
            return "redirect:/admin/conceptos?error=relacionado";
        }
    }
}
