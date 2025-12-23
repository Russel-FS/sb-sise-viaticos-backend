package com.viatico.proyect.controller;

import com.viatico.proyect.entity.Empleado;
import com.viatico.proyect.entity.Tarifario;
import com.viatico.proyect.entity.ZonaGeografica;
import com.viatico.proyect.repository.EmpleadoRepository;
import com.viatico.proyect.repository.TarifarioRepository;
import com.viatico.proyect.repository.ZonaGeograficaRepository;
import com.viatico.proyect.security.UsuarioPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/solicitudes")
@RequiredArgsConstructor
public class SolicitudRestController {

    private final TarifarioRepository tarifarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final ZonaGeograficaRepository zonaRepository;

    @GetMapping("/estimar")
    public ResponseEntity<Map<String, Object>> estimarViaticos(
            @RequestParam String fechaInicio,
            @RequestParam String fechaFin,
            @RequestParam Long idZona,
            @AuthenticationPrincipal UsuarioPrincipal user) {

        try {
            LocalDate start = LocalDate.parse(fechaInicio);
            LocalDate end = LocalDate.parse(fechaFin);
            long dias = ChronoUnit.DAYS.between(start, end) + 1;

            if (dias <= 0) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "La fecha de fin debe ser posterior a la de inicio"));
            }

            Empleado empleado = empleadoRepository.findById(user.getIdEmpleado())
                    .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

            ZonaGeografica zona = zonaRepository.findById(idZona)
                    .orElseThrow(() -> new RuntimeException("Zona no encontrada"));

            List<Tarifario> tarifarios = tarifarioRepository.findAllByNivelJerarquicoAndZonaGeografica(
                    empleado.getNivel(), zona);

            BigDecimal montoDiarioTotal = tarifarios.stream()
                    .map(Tarifario::getMontoDiario)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            BigDecimal montoTotal = montoDiarioTotal.multiply(new BigDecimal(dias));

            Map<String, Object> response = new HashMap<>();
            response.put("dias", dias);
            response.put("montoDiario", montoDiarioTotal);
            response.put("montoTotal", montoTotal);
            response.put("nivel", empleado.getNivel().getNombre());
            response.put("zona", zona.getNombre());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", "Datos inválidos"));
        }
    }
}
