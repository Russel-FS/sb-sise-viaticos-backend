package com.viatico.proyect.infrastructure.controller;

import com.viatico.proyect.application.service.interfaces.RendicionService;
import com.viatico.proyect.application.service.interfaces.SolicitudService;
import com.viatico.proyect.application.service.interfaces.TipoGastoService;
import com.viatico.proyect.config.UsuarioPrincipal;
import com.viatico.proyect.domain.entity.RendicionCuentas;
import com.viatico.proyect.domain.entity.SolicitudComision;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.math.BigDecimal;

@Controller
@RequestMapping("/rendiciones")
@RequiredArgsConstructor
public class RendicionController {

    private final RendicionService rendicionService;
    private final SolicitudService solicitudService;
    private final TipoGastoService tipoGastoService;
    private final com.viatico.proyect.application.service.interfaces.StorageService storageService;

    @GetMapping("/solicitud/{id}")
    public String verRendicion(@PathVariable Long id, Model model, @AuthenticationPrincipal UsuarioPrincipal user) {
        SolicitudComision sol = solicitudService.obtenerPorId(id);
        RendicionCuentas ren = rendicionService.obtenerOCrearPorSolicitud(id, user.getUsername());

        model.addAttribute("sol", sol);
        model.addAttribute("ren", ren);
        model.addAttribute("tiposGasto", tipoGastoService.listarTodos());

        return "rendiciones/formulario";
    }

    @PostMapping("/comprobante/guardar")
    public String guardarComprobante(@RequestParam Long rendicionId,
            @RequestParam Long solicitudId,
            @RequestParam Long tipoGastoId,
            @RequestParam String ruc,
            @RequestParam String razonSocial,
            @RequestParam String serie,
            @RequestParam String fecha,
            @RequestParam BigDecimal montoTotal,
            @RequestParam(value = "archivo", required = false) MultipartFile archivo,
            @AuthenticationPrincipal UsuarioPrincipal user) {

        String fotoUrl = null;
        if (archivo != null && !archivo.isEmpty()) {
            fotoUrl = storageService.store(archivo);
        }

        rendicionService.agregarComprobante(rendicionId, tipoGastoId, ruc, razonSocial, serie, fecha,
                montoTotal, fotoUrl, user.getUsername());

        return "redirect:/rendiciones/solicitud/" + solicitudId + "?success";
    }

    @GetMapping("/comprobante/eliminar/{id}")
    public String eliminarComprobante(@PathVariable Long id, @RequestParam Long solicitudId) {
        rendicionService.eliminarComprobante(id);
        return "redirect:/rendiciones/solicitud/" + solicitudId + "?deleted";
    }

    @PostMapping("/finalizar")
    public String finalizarRendicion(@RequestParam Long rendicionId) {
        rendicionService.finalizarRendicion(rendicionId);
        return "redirect:/solicitudes?rendered";
    }

    @GetMapping("/evidencias/{filename:.+}")
    @ResponseBody
    public ResponseEntity<Resource> verArchivo(@PathVariable String filename) {
        Resource file = storageService.loadAsResource(filename);
        String contentType = "application/octet-stream";

        String lowerName = filename.toLowerCase();
        if (lowerName.endsWith(".pdf"))
            contentType = "application/pdf";
        else if (lowerName.endsWith(".png"))
            contentType = "image/png";
        else if (lowerName.endsWith(".jpg") || lowerName.endsWith(".jpeg"))
            contentType = "image/jpeg";
        else if (lowerName.endsWith(".gif"))
            contentType = "image/gif";

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .contentType(MediaType.parseMediaType(contentType))
                .body(file);
    }
}
