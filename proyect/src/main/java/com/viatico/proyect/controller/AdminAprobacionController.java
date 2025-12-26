package com.viatico.proyect.controller;

import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.interfaces.ExcelService;
import com.viatico.proyect.service.interfaces.SolicitudService;

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
    private final ExcelService excelService;

    @GetMapping
    public String listarPendientes(Model model) {
        model.addAttribute("pendientes", solicitudService.listarPendientes());
        model.addAttribute("rechazadas", solicitudService.listarRechazadas());
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

    @GetMapping("/exportar")
    public org.springframework.http.ResponseEntity<org.springframework.core.io.InputStreamResource> exportarExcel() {
        java.io.ByteArrayInputStream in = excelService.generarReporteSolicitudes();

        org.springframework.http.HttpHeaders headers = new org.springframework.http.HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=reporte_solicitudes.xlsx");

        return org.springframework.http.ResponseEntity
                .ok()
                .headers(headers)
                .contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM)
                .body(new org.springframework.core.io.InputStreamResource(in));
    }
}
