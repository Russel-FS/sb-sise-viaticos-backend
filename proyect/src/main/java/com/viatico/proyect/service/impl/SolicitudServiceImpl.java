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
        public SolicitudComision guardarNuevaSolicitud(String motivo, String fechaInicio,
                        List<Long> idZonas, List<String> ciudades, List<Integer> noches, UsuarioPrincipal user) {

                // empleado
                Empleado empleado = empleadoRepository.findById(user.getIdEmpleado())
                                .orElseThrow(() -> new RuntimeException("Empleado no encontrado"));

                // Cálculo de fechas basado en noches
                LocalDate start = LocalDate.parse(fechaInicio);
                int totalNoches = (noches != null) ? noches.stream().mapToInt(v -> v).sum() : 0;
                LocalDate end = start.plusDays(totalNoches);

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

                BigDecimal montoTotal = BigDecimal.ZERO;
                List<ItinerarioViaje> itinerarios = new ArrayList<>();

                for (int i = 0; i < idZonas.size(); i++) {
                        Long idZona = idZonas.get(i);
                        String ciudad = ciudades.get(i);
                        Integer n = (noches != null && noches.size() > i) ? noches.get(i) : 0;

                        ZonaGeografica zona = zonaRepository.findById(idZona)
                                        .orElseThrow(() -> new RuntimeException("Zona no encontrada: " + idZona));

                        // Cálculo por tramo
                        List<Tarifario> tarifarios = tarifarioRepository.findAllByNivelJerarquicoAndZonaGeografica(
                                        empleado.getNivel(), zona);

                        BigDecimal montoDiarioZona = tarifarios.stream()
                                        .map(Tarifario::getMontoDiario)
                                        .reduce(BigDecimal.ZERO, BigDecimal::add);

                        // Si es el último tramo, sumamos 1 día (el de retorno)
                        int diasACalcular = (i == idZonas.size() - 1) ? n + 1 : n;

                        montoTotal = montoTotal.add(montoDiarioZona.multiply(new BigDecimal(diasACalcular)));

                        ItinerarioViaje itinerario = new ItinerarioViaje();
                        itinerario.setSolicitud(solicitud);
                        itinerario.setZonaDestino(zona);
                        itinerario.setCiudadEspecifica(ciudad);
                        itinerario.setNoches(n);
                        itinerario.setUserCrea(user.getUsername());
                        itinerario.setFechaCrea(LocalDateTime.now());
                        itinerarios.add(itinerario);
                }

                solicitud.setMontoTotal(montoTotal);
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

        @Override
        @Transactional
        public void cancelar(Long id) {
                SolicitudComision solicitud = solicitudRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

                if (solicitud.getEstado() != EstadoSolicitud.BORRADOR
                                && solicitud.getEstado() != EstadoSolicitud.PENDIENTE) {
                        throw new RuntimeException(
                                        "No se puede cancelar una solicitud que ya ha sido aprobada o procesada.");
                }

                solicitud.setEstado(EstadoSolicitud.CANCELADO);
                solicitudRepository.save(solicitud);
        }

        @Override
        public List<SolicitudComision> listarRecientes(Long empleadoId) {
                if (empleadoId == null) {
                        return solicitudRepository.findTop5ByOrderByFechaCreaDesc();
                }
                return solicitudRepository.findTop5ByEmpleadoIdOrderByFechaCreaDesc(empleadoId);
        }

        @Override
        public long contarPorEstado(String estado, Long empleadoId) {
                EstadoSolicitud estadoEnum = EstadoSolicitud.valueOf(estado.toUpperCase());
                if (empleadoId == null) {
                        return solicitudRepository.countByEstado(estadoEnum);
                }
                return solicitudRepository.countByEmpleadoIdAndEstado(empleadoId, estadoEnum);
        }
}
