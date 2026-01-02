package com.viatico.proyect.infrastructure.controller;

import com.viatico.proyect.config.UsuarioPrincipal;
import com.viatico.proyect.domain.entity.Empleado;
import com.viatico.proyect.domain.repositories.EmpleadoRepository;
import com.viatico.proyect.domain.repositories.SolicitudComisionRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudRestController {

        private final SolicitudComisionRepository solicitudRepository;
        private final EmpleadoRepository empleadoRepository;

        @GetMapping("/estimar")
        public ResponseEntity<Map<String, Object>> estimarViaticos(
                        @RequestParam List<Long> idZonas,
                        @RequestParam List<Integer> noches,
                        @AuthenticationPrincipal UsuarioPrincipal user) {

                try {
                        if (idZonas == null || idZonas.isEmpty()) {
                                return ResponseEntity.badRequest()
                                                .body(Map.of("error", "Debe incluir al menos un destino"));
                        }

                        Empleado empleado = empleadoRepository.obtenerPorId(user.getIdEmpleado())
                                        .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                        BigDecimal montoTotal = solicitudRepository.estimarMonto(
                                        empleado.getNivel().getId(),
                                        idZonas,
                                        null,
                                        noches);

                        int totalDias = noches.stream().reduce(0, Integer::sum) + 1;

                        Map<String, Object> response = new HashMap<>();
                        response.put("dias", totalDias);
                        response.put("montoTotal", montoTotal);
                        response.put("nivel", empleado.getNivel().getNombre());

                        return ResponseEntity.ok(response);
                } catch (Exception e) {
                        e.printStackTrace();
                        return ResponseEntity.badRequest().body(
                                        Map.of("error", e.getMessage() != null ? e.getMessage() : "Error desconocido"));
                }
        }
}
