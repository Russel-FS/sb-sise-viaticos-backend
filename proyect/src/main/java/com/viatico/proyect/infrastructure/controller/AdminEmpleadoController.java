package com.viatico.proyect.infrastructure.controller;

import com.viatico.proyect.application.service.interfaces.EmpleadoService;
import com.viatico.proyect.application.service.interfaces.NivelJerarquicoService;
import com.viatico.proyect.config.UsuarioPrincipal;
import com.viatico.proyect.domain.entity.Empleado;
import com.viatico.proyect.domain.repositories.RolRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin/empleados")
@RequiredArgsConstructor
public class AdminEmpleadoController {

    private final EmpleadoService empleadoService;
    private final NivelJerarquicoService nivelService;
    private final RolRepository rolRepository;

    @GetMapping
    public String listar(Model model) {
        model.addAttribute("empleados", empleadoService.listarTodos());
        return "admin/empleados/lista";
    }

    @GetMapping("/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("empleado", new Empleado());
        model.addAttribute("niveles", nivelService.listarTodos());
        model.addAttribute("roles", rolRepository.findAll());
        return "admin/empleados/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Empleado empleado,
            @RequestParam String password,
            @RequestParam Long rolId,
            @AuthenticationPrincipal UsuarioPrincipal user,
            RedirectAttributes ra) {
        try {
            empleadoService.guardar(empleado, password, rolId, user.getUsername());
            ra.addFlashAttribute("success", "Empleado registrado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al registrar empleado: " + e.getMessage());
        }
        return "redirect:/admin/empleados";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminar(@PathVariable Long id, RedirectAttributes ra) {
        try {
            empleadoService.eliminar(id);
            ra.addFlashAttribute("success", "Empleado eliminado correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al eliminar: " + e.getMessage());
        }
        return "redirect:/admin/empleados";
    }
}
