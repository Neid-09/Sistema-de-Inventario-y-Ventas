package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Rol;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.RolRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.UsuarioRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository;

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getIdRol() != null) {
            Rol rol = rolRepository.findById(usuario.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + usuario.getIdRol()));
            usuario.setRol(rol);
        } else {
            throw new RuntimeException("El ID del rol no puede ser null");
        }
        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario actualizarUsuario(int id, Usuario usuario) {
        return usuarioRepository.findById(id)
                .map(u -> {
                    u.setNombre(usuario.getNombre());
                    u.setCorreo(usuario.getCorreo());
                    u.setTelefono(usuario.getTelefono());
                    u.setContraseña(usuario.getContraseña());

                    if (usuario.getIdRol() != null) {
                        Rol rol = rolRepository.findById(usuario.getIdRol())
                                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + usuario.getIdRol()));
                        u.setRol(rol);
                    }

                    return usuarioRepository.save(u);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    public void eliminarUsuario(int id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario autenticarUsuario(String correo, String contraseña) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        if (!contraseña.equals(usuario.getContraseña())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Cargar el rol con sus permisos (fetch EAGER hace que no sea necesario código adicional)
        return usuario;
    }

    // Método auxiliar para verificar permisos
    @Override
    public Usuario obtenerUsuarioPorId(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public boolean tienePermiso(Usuario usuario, String nombrePermiso) {
        if (usuario != null && usuario.getRol() != null && usuario.getRol().getPermisos() != null) {
            return usuario.getRol().getPermisos().stream()
                    .anyMatch(permiso -> permiso.getNombre().equals(nombrePermiso));
        }
        return false;
    }

}