package com.viatico.proyect.config;

import com.viatico.proyect.application.service.interfaces.EmpleadoService;
import com.viatico.proyect.domain.entity.Empleado;
import com.viatico.proyect.domain.entity.NivelJerarquico;
import com.viatico.proyect.domain.entity.Rol;
import com.viatico.proyect.domain.entity.ZonaGeografica;
import com.viatico.proyect.domain.enums.RolNombre;
import com.viatico.proyect.domain.repositories.NivelJerarquicoRepository;
import com.viatico.proyect.domain.repositories.RolRepository;
import com.viatico.proyect.domain.repositories.UsuarioRepository;
import com.viatico.proyect.domain.repositories.ZonaGeograficaRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ZonaGeograficaRepository zonaRepository;
    private final RolRepository rolRepository;
    private final NivelJerarquicoRepository nivelRepository;
    private final UsuarioRepository usuarioRepository;
    private final EmpleadoService empleadoService;

    @Override
    public void run(String... args) throws Exception {
        log.info("Iniciando inicialización de datos...");

        // zonas geograficas
        if (zonaRepository.count() == 0) {
            String[][] zonasData = {
                    { "Costa Norte", "Piura, Tumbes, Lambayeque" },
                    { "Costa Centro", "Lima, Ica, Ancash" },
                    { "Costa Sur", "Arequipa, Moquegua, Tacna" },
                    { "Sierra", "Cusco, Puno, Junín" },
                    { "Selva", "Loreto, San Martín, Ucayali" }
            };
            for (String[] data : zonasData) {
                ZonaGeografica z = new ZonaGeografica();
                z.setNombre(data[0]);
                z.setDescripcion(data[1]);
                z.setFechaCrea(LocalDateTime.now());
                z.setUserCrea("SYSTEM");
                zonaRepository.save(z);
            }
            log.info("Zonas geográficas base creadas.");
        }

        for (RolNombre rolEnum : RolNombre.values()) {
            if (rolRepository.findByCodigo(rolEnum.getCodigo()).isEmpty()) {
                Rol nuevoRol = new Rol();
                nuevoRol.setCodigo(rolEnum.getCodigo());
                nuevoRol.setNombre(rolEnum.getNombre());
                nuevoRol.setFechaCrea(LocalDateTime.now());
                nuevoRol.setUserCrea("SYSTEM");
                rolRepository.save(nuevoRol);
                log.info("Rol creado: {}", rolEnum.getCodigo());
            }
        }

        NivelJerarquico nivelAdmin = nivelRepository.findByNombre("GERENCIA")
                .orElseGet(() -> {
                    NivelJerarquico n = new NivelJerarquico();
                    n.setNombre("GERENCIA");
                    n.setDescripcion("Nivel máximo de autoridad");
                    n.setFechaCrea(LocalDateTime.now());
                    n.setUserCrea("SYSTEM");
                    return nivelRepository.save(n);
                });

        boolean existeAdmin = usuarioRepository.obtenerPorEmail("admin@iss.com.pe").isPresent();

        if (!existeAdmin) {
            Empleado e = new Empleado();
            e.setNombres("Administrador");
            e.setApellidos("Del Sistema");
            e.setDni("00000000");
            e.setEmail("admin@iss.com.pe");
            e.setNivel(nivelAdmin);

            Rol rolAdmin = rolRepository.findByCodigo(RolNombre.ADMIN.getCodigo()).get();
            empleadoService.guardar(e, "admin123", rolAdmin.getId(), null, "SYSTEM");

            log.info("Empleado y usuario administrador creado: admin@iss.com.pe / admin123");
        } else {
            log.info("Empleado admin ya existe");
        }

        log.info("Inicialización de datos completada.");
    }
}
