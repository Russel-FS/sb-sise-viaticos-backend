package com.viatico.proyect.infrastructure.controller;

import com.viatico.proyect.application.service.interfaces.RendicionService;
import com.viatico.proyect.application.service.interfaces.SolicitudService;
import com.viatico.proyect.domain.entity.RendicionCuentas;
import com.viatico.proyect.domain.entity.SolicitudComision;
import com.viatico.proyect.domain.enums.EstadoSolicitud;

import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin/validaciones")
@PreAuthorize("hasRole('ROLE_ADMIN')")
@RequiredArgsConstructor
public class AdminValidacionController {

    private final SolicitudService solicitudService;
    private final RendicionService rendicionService;

    @GetMapping
    public String listarPendientes(Model model) {
        List<SolicitudComision> rendidas = solicitudService.listarTodas().stream()
                .filter(s -> s.getEstado() == EstadoSolicitud.RENDIDO)
                .toList();

        model.addAttribute("solicitudes", rendidas);
        return "admin/validaciones/lista";
    }

    @GetMapping("/detalle/{id}")
    public String verDetalle(@PathVariable Long id, Model model) {
        SolicitudComision sol = solicitudService.obtenerPorId(id);
        RendicionCuentas ren = rendicionService.obtenerPorSolicitud(id);

        model.addAttribute("sol", sol);
        model.addAttribute("ren", ren);

        return "admin/validaciones/detalle";
    }

    @PostMapping("/comprobante/validar")
    public String validarComprobante(@RequestParam Long detalleId,
            @RequestParam Long solicitudId,
            @RequestParam String estado,
            @RequestParam(required = false) String motivo) {
        rendicionService.validarComprobante(detalleId, estado, motivo);
        return "redirect:/admin/validaciones/detalle/" + solicitudId + "?updated";
    }

    @PostMapping("/finalizar")
    public String finalizarValidacion(@RequestParam Long solicitudId) {
        rendicionService.finalizarValidacion(solicitudId);
        return "redirect:/admin/validaciones?success";
    }

    @PostMapping("/observar")
    public String observarRendicion(@RequestParam Long solicitudId,
            @RequestParam(required = false) String comentario) {
        rendicionService.observarRendicion(solicitudId, comentario);
        return "redirect:/admin/validaciones/detalle/" + solicitudId + "?observed";
    }
}
