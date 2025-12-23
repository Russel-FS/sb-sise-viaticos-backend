package com.viatico.proyect.service;

import com.viatico.proyect.entity.SolicitudComision;
import com.viatico.proyect.entity.ZonaGeografica;
import com.viatico.proyect.security.UsuarioPrincipal;
import java.util.List;

public interface SolicitudService {
    List<ZonaGeografica> listarZonas();

    SolicitudComision guardarNuevaSolicitud(String motivo, String fechaInicio,
            List<Long> idZonas, List<String> ciudades, List<Integer> noches, UsuarioPrincipal user);

    List<SolicitudComision> listarPorEmpleado(Long empleadoId);

    List<SolicitudComision> listarTodas();

    List<SolicitudComision> listarPendientes();

    List<SolicitudComision> listarRechazadas();

    void enviarAprobacion(Long id);

    void aprobar(Long id, String username);

    void rechazar(Long id, String comentario, String username);

    SolicitudComision obtenerPorId(Long id);

    void cancelar(Long id);
}
