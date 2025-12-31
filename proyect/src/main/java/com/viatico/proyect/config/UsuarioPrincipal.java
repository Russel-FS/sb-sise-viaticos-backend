package com.viatico.proyect.config;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.viatico.proyect.domain.entity.Usuario;

import java.util.Collection;
import java.util.stream.Collectors;

@Getter
public class UsuarioPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final Long idEmpleado;
    private final String nombreCompleto;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean activo;

    public UsuarioPrincipal(Usuario usuario) {
        this.id = usuario.getId();
        this.username = usuario.getUsername();
        this.password = usuario.getPassword();
        this.idEmpleado = usuario.getEmpleado().getId();
        this.nombreCompleto = usuario.getEmpleado().getNombres() + " " + usuario.getEmpleado().getApellidos();

        this.authorities = usuario.getRoles().stream()
                .map(rol -> new SimpleGrantedAuthority(rol.getCodigo()))
                .collect(Collectors.toList());
        this.activo = usuario.getActivo() == 1;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}
