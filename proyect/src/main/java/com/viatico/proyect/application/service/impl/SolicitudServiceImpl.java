package com.viatico.proyect.application.service.impl;

import com.viatico.proyect.application.service.interfaces.SolicitudService;
import com.viatico.proyect.config.UsuarioPrincipal;
import com.viatico.proyect.domain.entity.*;
import com.viatico.proyect.domain.enums.EstadoSolicitud;
import com.viatico.proyect.domain.repositories.SolicitudComisionRepository;
import com.viatico.proyect.domain.repositories.TarifarioRepository;
import com.viatico.proyect.domain.repositories.ZonaGeograficaRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SolicitudServiceImpl implements SolicitudService {

        private final ZonaGeograficaRepository zonaRepository;
        private final SolicitudComisionRepository solicitudRepository;
        private final TarifarioRepository tarifarioRepository;

        @Override
        public List<ZonaGeografica> listarZonas() {
                return zonaRepository.findAll();
        }

        @Override
        @Transactional
        public SolicitudComision guardarNuevaSolicitud(String motivo, String fechaInicio,
                        List<Long> idZonas, List<String> ciudades, List<Integer> noches, UsuarioPrincipal user) {

                LocalDate start = LocalDate.parse(fechaInicio);

                Long idSolicitud = solicitudRepository.registrarSolicitudFull(
                                user.getIdEmpleado(),
                                motivo,
                                start,
                                idZonas,
                                ciudades,
                                noches,
                                user.getUsername());

                return solicitudRepository.findById(idSolicitud)
                                .orElseThrow(() -> new RuntimeException(
                                                "Error al recuperar la solicitud creada por PL/SQL"));
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
                SolicitudComision solicitud = solicitudRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Solicitud no encontrada"));

                if (solicitud.getEmpleado() != null && solicitud.getEmpleado().getNivel() != null) {
                        for (ItinerarioViaje iti : solicitud.getItinerarios()) {
                                List<Tarifario> tarifas = tarifarioRepository.findAllByNivelJerarquicoAndZonaGeografica(
                                                solicitud.getEmpleado().getNivel(),
                                                iti.getZonaDestino());
                                iti.setTarifas(tarifas);
                        }
                }

                return solicitud;
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

        @Override
        public Double calcularPromedioDiasLiquidacion(Long empleadoId) {
                List<SolicitudComision> liquidadas;
                if (empleadoId == null) {
                        liquidadas = solicitudRepository.findByEstadoOrderByFechaCreaDesc(EstadoSolicitud.LIQUIDADO);
                } else {
                        liquidadas = solicitudRepository.findByEmpleadoIdAndEstadoOrderByFechaCreaDesc(empleadoId,
                                        EstadoSolicitud.LIQUIDADO);
                }

                if (liquidadas.isEmpty())
                        return 0.0;

                long totalDias = 0;
                int count = 0;

                for (SolicitudComision sol : liquidadas) {
                        if (sol.getFechaLiquidacion() != null && sol.getFechaFin() != null) {
                                long dias = ChronoUnit.DAYS.between(sol.getFechaFin(),
                                                sol.getFechaLiquidacion().toLocalDate());
                                totalDias += Math.max(0, dias);
                                count++;
                        }
                }

                return count == 0 ? 0.0 : (double) totalDias / count;
        }
}
