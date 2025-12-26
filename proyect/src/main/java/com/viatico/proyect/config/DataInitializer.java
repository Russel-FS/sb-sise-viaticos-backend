package com.viatico.proyect.config;

import com.viatico.proyect.entity.Empleado;
import com.viatico.proyect.entity.NivelJerarquico;
import com.viatico.proyect.entity.Rol;
import com.viatico.proyect.entity.Usuario;
import com.viatico.proyect.entity.ZonaGeografica;
import com.viatico.proyect.enums.RolNombre;
import com.viatico.proyect.repository.interfaces.EmpleadoRepository;
import com.viatico.proyect.repository.interfaces.NivelJerarquicoRepository;
import com.viatico.proyect.repository.interfaces.RolRepository;
import com.viatico.proyect.repository.interfaces.UsuarioRepository;
import com.viatico.proyect.repository.interfaces.ZonaGeograficaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final ZonaGeograficaRepository zonaRepository;
    private final RolRepository rolRepository;
    private final NivelJerarquicoRepository nivelRepository;
    private final EmpleadoRepository empleadoRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

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

        Empleado empleadoAdmin = empleadoRepository.findByDni("00000000")
                .orElseGet(() -> {
                    Empleado e = new Empleado();
                    e.setNombres("Administrador");
                    e.setApellidos("Del Sistema");
                    e.setDni("00000000");
                    e.setEmail("admin@iss.com.pe");
                    e.setNivel(nivelAdmin);
                    e.setFechaCrea(LocalDateTime.now());
                    e.setUserCrea("SYSTEM");
                    return empleadoRepository.save(e);
                });

        if (usuarioRepository.findByUsername("admin").isEmpty()) {
            Rol rolAdmin = rolRepository.findByCodigo(RolNombre.ADMIN.getCodigo()).get();

            Usuario userAdmin = new Usuario();
            userAdmin.setEmpleado(empleadoAdmin);
            userAdmin.setUsername("admin");
            userAdmin.setPassword(passwordEncoder.encode("admin123"));
            userAdmin.setRol(rolAdmin);
            userAdmin.setActivo(1);
            userAdmin.setFechaCrea(LocalDateTime.now());
            userAdmin.setUserCrea("SYSTEM");

            usuarioRepository.save(userAdmin);
            log.info("Usuario administrador por defecto creado: admin / admin123");
        }

        log.info("Inicialización de datos completada.");
    }
}
