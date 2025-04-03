package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Permiso;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.PermisoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.RolRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.UsuarioRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getIdRol() != null) {
            usuario.setRol(rolRepository.findById(usuario.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
        }

        // Encriptar contraseña si se está creando un nuevo usuario
        if (usuario.getIdUsuario() == null) {
            usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        }

        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    @Override
    public Usuario actualizarUsuario(int id, Usuario usuario) {
        Usuario usuarioExistente = obtenerUsuarioPorId(id);
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setCorreo(usuario.getCorreo());
        usuarioExistente.setTelefono(usuario.getTelefono());

        // Solo actualizar contraseña si viene en la petición
        if (usuario.getContraseña() != null && !usuario.getContraseña().isEmpty()) {
            usuarioExistente.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        }

        // Actualizar rol si se proporciona
        if (usuario.getIdRol() != null) {
            usuarioExistente.setRol(rolRepository.findById(usuario.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
        }

        return usuarioRepository.save(usuarioExistente);
    }

    @Override
    public void eliminarUsuario(int id) {
        if (!usuarioRepository.existsById(id)) {
            throw new NoSuchElementException("Usuario no encontrado con ID: " + id);
        }
        usuarioRepository.deleteById(id);
    }

    @Override
    public Usuario autenticarUsuario(String correo, String contraseña) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new RuntimeException("Credenciales inválidas"));

        if (!passwordEncoder.matches(contraseña, usuario.getContraseña())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return usuario;
    }

    @Override
    public Usuario obtenerUsuarioPorId(int id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Usuario no encontrado con ID: " + id));
    }

    /**
     * Verifica si un usuario tiene un permiso específico
     * @param usuario El usuario a verificar
     * @param nombrePermiso El nombre del permiso a verificar
     * @return true si tiene el permiso, false en caso contrario
     */
    @Override
    public boolean tienePermiso(Usuario usuario, String nombrePermiso) {
        // 1. Verificar primero en permisos personalizados (tienen prioridad)
        boolean tienePermisoPersonalizado = usuario.getPermisosPersonalizados().stream()
                .anyMatch(p -> p.getNombre().equals(nombrePermiso));

        if (tienePermisoPersonalizado) {
            return true; // Si está explícitamente asignado, tiene el permiso
        }

        // 2. Si no tiene un permiso personalizado, verificar permisos del rol
        if (usuario.getRol() != null) {
            return usuario.getRol().getPermisos().stream()
                    .anyMatch(p -> p.getNombre().equals(nombrePermiso));
        }

        return false;
    }

    public boolean existenUsuariosConRol(Integer idRol) {
        // Implementar lógica para verificar si hay usuarios con el rol especificado
        return usuarioRepository.countByRolIdRol(idRol) > 0;
    }

    /**
     * Obtiene todos los permisos de un usuario (rol + personalizados)
     * @param usuario El usuario
     * @return Lista de permisos únicos
     */
    public List<Permiso> obtenerTodosLosPermisos(Usuario usuario) {
        List<Permiso> todosLosPermisos = usuario.getPermisosPersonalizados().stream().collect(Collectors.toList());

        if (usuario.getRol() != null && usuario.getRol().getPermisos() != null) {
            todosLosPermisos.addAll(usuario.getRol().getPermisos());
        }

        // Eliminar duplicados
        return todosLosPermisos.stream().distinct().collect(Collectors.toList());
    }


}