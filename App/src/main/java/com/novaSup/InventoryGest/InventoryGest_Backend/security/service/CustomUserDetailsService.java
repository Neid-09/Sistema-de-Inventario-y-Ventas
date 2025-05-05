package com.novaSup.InventoryGest.InventoryGest_Backend.security.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));

        Set<GrantedAuthority> authorities = new HashSet<>();

        // Añadir rol
        if (usuario.getRol() != null) {
            authorities.add(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().getNombre().toUpperCase()));
        }

        // Añadir permisos del rol
        if (usuario.getRol() != null && usuario.getRol().getPermisos() != null) {
            usuario.getRol().getPermisos().forEach(permiso -> {
                authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
            });
        }

        // Añadir permisos personalizados
        if (usuario.getPermisosPersonalizados() != null) {
            usuario.getPermisosPersonalizados().forEach(permiso -> {
                authorities.add(new SimpleGrantedAuthority(permiso.getNombre()));
            });
        }

        // Log para verificar permisos
        System.out.println("Usuario: " + usuario.getNombre() + " tiene los siguientes permisos:");
        authorities.forEach(auth -> System.out.println(auth.getAuthority()));

        // Retornar CustomUserDetails en lugar de User
        return new CustomUserDetails(
                usuario.getCorreo(),
                usuario.getContraseña(),
                authorities,
                usuario.getIdUsuario(),
                usuario.getEstado() != null && usuario.getEstado() // Usar el estado del usuario
        );
    }

    public Usuario getUserByEmail(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con correo: " + correo));
    }
}