package com.viatico.proyect.controller;

import com.viatico.proyect.entity.RendicionCuentas;
import com.viatico.proyect.entity.SolicitudComision;
import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.RendicionService;
import com.viatico.proyect.service.SolicitudService;
import com.viatico.proyect.service.TipoGastoService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@Controller
@RequestMapping("/rendiciones")
@RequiredArgsConstructor
public class RendicionController {

    private final RendicionService rendicionService;
    private final SolicitudService solicitudService;
    private final TipoGastoService tipoGastoService;

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
            @RequestParam BigDecimal monto,
            @AuthenticationPrincipal UsuarioPrincipal user) {

        rendicionService.agregarComprobante(rendicionId, tipoGastoId, ruc, razonSocial, serie, fecha, monto,
                user.getUsername());

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
}
