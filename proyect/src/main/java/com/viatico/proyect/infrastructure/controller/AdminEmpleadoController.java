package com.viatico.proyect.infrastructure.controller;

import com.viatico.proyect.application.service.interfaces.EmpleadoService;
import com.viatico.proyect.application.service.interfaces.NivelJerarquicoService;
import com.viatico.proyect.config.UsuarioPrincipal;
import com.viatico.proyect.domain.entity.Empleado;
import com.viatico.proyect.domain.repositories.RolRepository;

import lombok.RequiredArgsConstructor;

import java.util.List;

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

    @GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        Empleado empleado = empleadoService.obtenerPorId(id);
        com.viatico.proyect.domain.entity.Usuario usuario = empleadoService.obtenerUsuarioPorEmpleado(id);

        if (usuario != null) {

            java.util.List<Long> rolIds = usuario.getRoles().stream()
                    .map(rol -> rol.getId())
                    .collect(java.util.stream.Collectors.toList());
            model.addAttribute("rolIds", rolIds);
            model.addAttribute("currentUsername", usuario.getUsername());
        }

        model.addAttribute("empleado", empleado);
        model.addAttribute("niveles", nivelService.listarTodos());
        model.addAttribute("roles", rolRepository.findAll());
        return "admin/empleados/formulario";
    }

    @PostMapping("/guardar")
    public String guardar(@ModelAttribute Empleado empleado,
            @RequestParam String password,
            @RequestParam(required = false) List<Long> rolIds,
            @RequestParam(required = false) String username,
            @AuthenticationPrincipal UsuarioPrincipal user,
            RedirectAttributes ra) {
        try {
            empleadoService.guardar(empleado, password, rolIds, username, user.getUsername());
            ra.addFlashAttribute("success",
                    "Empleado " + (empleado.getId() != null ? "actualizado" : "registrado") + " correctamente");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Error al " + (empleado.getId() != null ? "actualizar" : "registrar")
                    + " empleado: " + e.getMessage());
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
