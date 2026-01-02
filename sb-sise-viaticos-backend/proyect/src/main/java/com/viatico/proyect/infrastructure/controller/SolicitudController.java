package com.viatico.proyect.infrastructure.controller;

import com.viatico.proyect.application.service.interfaces.LiquidacionService;
import com.viatico.proyect.application.service.interfaces.PdfService;
import com.viatico.proyect.application.service.interfaces.SolicitudService;
import com.viatico.proyect.config.UsuarioPrincipal;
import com.viatico.proyect.domain.entity.SolicitudComision;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;

import java.io.ByteArrayInputStream;

import java.util.List;

@Controller
@RequestMapping("/solicitudes")
@RequiredArgsConstructor
public class SolicitudController {

    private final SolicitudService solicitudService;
    private final LiquidacionService liquidacionService;
    private final PdfService pdfService;

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

        // Si está liquidado, traer la liquidación final
        if (sol.getEstado().name().equals("LIQUIDADO")) {
            model.addAttribute("liq", liquidacionService.obtenerPorSolicitud(id));
        }

        return "solicitudes/detalle";
    }

    @PostMapping("/cancelar/{id}")
    public String cancelarSolicitud(@PathVariable Long id) {
        solicitudService.cancelar(id);
        return "redirect:/solicitudes";
    }

    @GetMapping("/{id}/reporte")
    public ResponseEntity<InputStreamResource> descargarReporte(@PathVariable Long id) {
        ByteArrayInputStream bis = pdfService.generarReporteLiquidacion(id);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "inline; filename=liquidacion_" + id + ".pdf");

        return ResponseEntity
                .ok()
                .headers(headers)
                .contentType(MediaType.APPLICATION_PDF)
                .body(new InputStreamResource(bis));
    }
}
