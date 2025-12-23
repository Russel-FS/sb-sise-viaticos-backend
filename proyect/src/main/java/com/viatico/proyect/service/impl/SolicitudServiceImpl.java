package com.viatico.proyect.service.impl;

import com.viatico.proyect.entity.Empleado;
import com.viatico.proyect.entity.ItinerarioViaje;
import com.viatico.proyect.entity.SolicitudComision;
import com.viatico.proyect.entity.ZonaGeografica;
import com.viatico.proyect.repository.EmpleadoRepository;
import com.viatico.proyect.repository.SolicitudComisionRepository;
import com.viatico.proyect.repository.ZonaGeograficaRepository;
import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

    private final ZonaGeograficaRepository zonaRepository;
    private final SolicitudComisionRepository solicitudRepository;
    private final EmpleadoRepository empleadoRepository;

    @Override
    public List<ZonaGeografica> listarZonas() {
        return zonaRepository.findAll();
    }

    @Override
    @Transactional
    public SolicitudComision guardarNuevaSolicitud(String motivo, String fechaInicio, String fechaFin, Long idZona,
            String ciudad, UsuarioPrincipal user) {

        // empleado
        Empleado empleado = empleadoRepository.findById(user.getIdEmpleado())
                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

        // Solicitud
        SolicitudComision solicitud = new SolicitudComision();
        solicitud.setEmpleado(empleado);
        solicitud.setMotivoViaje(motivo);
        solicitud.setFechaSolicitud(LocalDate.now());
        solicitud.setFechaInicio(LocalDate.parse(fechaInicio));
        solicitud.setFechaFin(LocalDate.parse(fechaFin));
        solicitud.setEstado("Borrador");
        solicitud.setUserCrea(user.getUsername());
        solicitud.setFechaCrea(LocalDateTime.now());

        // Itinerario base
        ZonaGeografica zona = zonaRepository.findById(idZona)
                .orElseThrow(() -> new RuntimeException("Zona no encontrada"));

        ItinerarioViaje itinerario = new ItinerarioViaje();
        itinerario.setSolicitud(solicitud);
        itinerario.setZonaDestino(zona);
        itinerario.setCiudadEspecifica(ciudad);
        itinerario.setDiasPernocte(0);
        itinerario.setUserCrea(user.getUsername());
        itinerario.setFechaCrea(LocalDateTime.now());

        List<ItinerarioViaje> itinerarios = new ArrayList<>();
        itinerarios.add(itinerario);
        solicitud.setItinerarios(itinerarios);

        return solicitudRepository.save(solicitud);
    }

    @Override
    public List<SolicitudComision> listarPorEmpleado(Long empleadoId) {
        return solicitudRepository.findByEmpleadoIdOrderByFechaCreaDesc(empleadoId);
    }

    @Override
    public List<SolicitudComision> listarTodas() {
        return solicitudRepository.findAll().stream()
                .sorted((s1, s2) -> s2.getFechaCrea().compareTo(s1.getFechaCrea()))
                .toList();
    }
}
