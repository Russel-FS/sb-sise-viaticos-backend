package com.viatico.proyect.service.impl;

import com.viatico.proyect.entity.*;
import com.viatico.proyect.repository.*;
import com.viatico.proyect.security.UsuarioPrincipal;
import com.viatico.proyect.service.SolicitudService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

        private final ZonaGeograficaRepository zonaRepository;
        private final SolicitudComisionRepository solicitudRepository;
        private final EmpleadoRepository empleadoRepository;
        private final TarifarioRepository tarifarioRepository;

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

                // Fechas
                LocalDate start = LocalDate.parse(fechaInicio);
                LocalDate end = LocalDate.parse(fechaFin);
                long diasComision = ChronoUnit.DAYS.between(start, end) + 1;

                // Solicitud
                SolicitudComision solicitud = new SolicitudComision();
                solicitud.setEmpleado(empleado);
                solicitud.setMotivoViaje(motivo);
                solicitud.setFechaSolicitud(LocalDate.now());
                solicitud.setFechaInicio(start);
                solicitud.setFechaFin(end);
                solicitud.setEstado(EstadoSolicitud.BORRADOR);
                solicitud.setUserCrea(user.getUsername());
                solicitud.setFechaCrea(LocalDateTime.now());

                // Cálculo de Viáticos
                ZonaGeografica zona = zonaRepository.findById(idZona)
                                .orElseThrow(() -> new RuntimeException("Zona no encontrada"));

                List<Tarifario> tarifarios = tarifarioRepository.findAllByNivelJerarquicoAndZonaGeografica(
                                empleado.getNivel(), zona);

                BigDecimal montoDiarioTotal = tarifarios.stream()
                                .map(Tarifario::getMontoDiario)
                                .reduce(BigDecimal.ZERO, BigDecimal::add);

                BigDecimal montoTotal = montoDiarioTotal.multiply(new BigDecimal(diasComision));
                solicitud.setMontoTotal(montoTotal);

                // Itinerario base
                ItinerarioViaje itinerario = new ItinerarioViaje();
                itinerario.setSolicitud(solicitud);
                itinerario.setZonaDestino(zona);
                itinerario.setCiudadEspecifica(ciudad);
                itinerario.setNoches((int) (diasComision - 1));
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

        @Override
        public List<SolicitudComision> listarPendientes() {
                return solicitudRepository.findByEstadoOrderByFechaCreaDesc(EstadoSolicitud.PENDIENTE);
        }

        @Override
        public List<SolicitudComision> listarRechazadas() {
                return solicitudRepository.findByEstadoOrderByFechaCreaDesc(EstadoSolicitud.RECHAZADO);
        }

        @Override
        @Transactional
        public void enviarAprobacion(Long id) {
                SolicitudComision solicitud = solicitudRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
                if (solicitud.getEstado() != EstadoSolicitud.BORRADOR) {
                        throw new RuntimeException("Solo se pueden enviar solicitudes en estado Borrador");
                }
                solicitud.setEstado(EstadoSolicitud.PENDIENTE);
                solicitudRepository.save(solicitud);
        }

        @Override
        @Transactional
        public void aprobar(Long id, String username) {
                SolicitudComision solicitud = solicitudRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
                solicitud.setEstado(EstadoSolicitud.APROBADO);
                solicitudRepository.save(solicitud);
        }

        @Override
        @Transactional
        public void rechazar(Long id, String comentario, String username) {
                SolicitudComision solicitud = solicitudRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
                solicitud.setEstado(EstadoSolicitud.RECHAZADO);
                solicitud.setComentarioRechazo(comentario);
                solicitudRepository.save(solicitud);
        }

        @Override
        public SolicitudComision obtenerPorId(Long id) {
                return solicitudRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));
        }
}
