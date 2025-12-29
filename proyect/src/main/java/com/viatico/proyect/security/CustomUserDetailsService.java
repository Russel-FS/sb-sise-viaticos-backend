package com.viatico.proyect.security;

import com.viatico.proyect.entity.Usuario;
import com.viatico.proyect.repository.interfaces.UsuarioRepository;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@lombok.RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.obtenerPorEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        if (usuario.getActivo() == 0) {
            throw new UsernameNotFoundException("Usuario inactivo");
        }

        return new UsuarioPrincipal(usuario);
    }
}
