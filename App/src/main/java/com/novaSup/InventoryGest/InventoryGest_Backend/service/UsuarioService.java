package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Rol;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.RolRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private RolRepository rolRepository; // Repositorio para validar roles

    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getIdRol() != null) {
            // Verificar que el rol existe antes de guardar el usuario
            Rol rol = rolRepository.findById(usuario.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + usuario.getIdRol()));
            usuario.setRol(rol);
        } else {
            throw new RuntimeException("El ID del rol no puede ser null");
        }
        return usuarioRepository.save(usuario);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    public Usuario actualizarUsuario(int id, Usuario usuario) {
        return usuarioRepository.findById(id)
                .map(u -> {
                    u.setNombre(usuario.getNombre());
                    u.setCorreo(usuario.getCorreo());
                    u.setTelefono(usuario.getTelefono());
                    u.setContraseña(usuario.getContraseña());

                    // Buscar y asignar el rol por su ID
                    if (usuario.getIdRol() != null) {
                        Rol rol = rolRepository.findById(usuario.getIdRol())
                                .orElseThrow(() -> new RuntimeException("Rol no encontrado con ID: " + usuario.getIdRol()));
                        u.setRol(rol);
                    }

                    return usuarioRepository.save(u);
                })
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    public void eliminarUsuario(int id) {
        usuarioRepository.deleteById(id);
    }

    public Usuario autenticarUsuario(String correo, String contraseña) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con correo: " + correo));

        if (!contraseña.equals(usuario.getContraseña())) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        return usuario;
    }

}
