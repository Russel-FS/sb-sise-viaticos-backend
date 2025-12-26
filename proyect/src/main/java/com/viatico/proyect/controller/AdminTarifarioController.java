package com.viatico.proyect.controller;

import com.viatico.proyect.entity.Tarifario;
import com.viatico.proyect.repository.interfaces.NivelJerarquicoRepository;
import com.viatico.proyect.repository.interfaces.ZonaGeograficaRepository;
import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.interfaces.TarifarioService;
import com.viatico.proyect.service.interfaces.TipoGastoService;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/tarifarios")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminTarifarioController {

    private final TarifarioService tarifarioService;
    private final ZonaGeograficaRepository zonaRepository;
    private final NivelJerarquicoRepository nivelRepository;
    private final TipoGastoService tipoGastoService;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("tarifarios", tarifarioService.listarTodos());
        model.addAttribute("zonas", zonaRepository.findAll());
        model.addAttribute("niveles", nivelRepository.findAll());
        model.addAttribute("tiposGasto", tipoGastoService.listarTodos());
        return "admin/tarifarios/lista";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Tarifario tarifario,
            @AuthenticationPrincipal UsuarioPrincipal user) {
        tarifarioService.guardar(tarifario, user.getUsername());
        return "redirect:/admin/tarifarios?success";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id) {
        tarifarioService.eliminar(id);
        return "redirect:/admin/tarifarios?deleted";
    }
}
