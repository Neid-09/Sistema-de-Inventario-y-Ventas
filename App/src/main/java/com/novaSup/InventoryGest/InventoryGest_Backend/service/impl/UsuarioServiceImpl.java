package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Permiso;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.RolRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.UsuarioRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.UsuarioService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // Importar Transactional

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

    private final RolRepository rolRepository;

    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository usuarioRepository, RolRepository rolRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.rolRepository = rolRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional // Añadir Transactional para asegurar atomicidad
    public Usuario guardarUsuario(Usuario usuario) {
        if (usuario.getIdRol() != null) {
            usuario.setRol(rolRepository.findById(usuario.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
        }

        // Si es un nuevo usuario (sin ID)
        if (usuario.getIdUsuario() == null) {
            // Encriptar contraseña
            usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
            // Establecer estado como activo por defecto
            if (usuario.getEstado() == null) { // Solo si no se proporciona explícitamente
                usuario.setEstado(true);
            }
        } else {
            // Si es una actualización, asegurarse de que el estado no sea null si no se proporciona
            // (Esto previene que una actualización sin el campo estado lo ponga a null)
            Usuario usuarioExistente = obtenerUsuarioPorId(usuario.getIdUsuario());
            if (usuario.getEstado() == null) {
                usuario.setEstado(usuarioExistente.getEstado()); // Mantener estado existente si no se especifica
            }
            // Asegurarse de no sobrescribir la contraseña si no se proporciona una nueva
             if (usuario.getContraseña() == null || usuario.getContraseña().isEmpty()) {
                 usuario.setContraseña(usuarioExistente.getContraseña());
             } else if (!passwordEncoder.matches(usuario.getContraseña(), usuarioExistente.getContraseña())) {
                 // Solo encriptar si la contraseña es nueva y diferente
                 usuario.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
             }
        }


        return usuarioRepository.save(usuario);
    }

    @Override
    public List<Usuario> listarUsuarios() {
        return usuarioRepository.findAll();
    }

    // Nuevo método para listar solo usuarios activos
    @Override
    public List<Usuario> listarUsuariosActivos() {
        return usuarioRepository.findByEstadoTrue();
    }

    // Nuevo método para listar solo usuarios inactivos
    @Override
    public List<Usuario> listarUsuariosInactivos() {
        return usuarioRepository.findByEstadoFalse();
    }


    @Override
    @Transactional // Añadir Transactional
    public Usuario actualizarUsuario(int id, Usuario usuario) {
        Usuario usuarioExistente = obtenerUsuarioPorId(id);
        usuarioExistente.setNombre(usuario.getNombre());
        usuarioExistente.setCorreo(usuario.getCorreo());
        usuarioExistente.setTelefono(usuario.getTelefono());

        // Actualizar estado si se proporciona
        if (usuario.getEstado() != null) {
            usuarioExistente.setEstado(usuario.getEstado());
        }

        // Solo actualizar contraseña si viene en la petición y es diferente
        if (usuario.getContraseña() != null && !usuario.getContraseña().isEmpty() &&
            !passwordEncoder.matches(usuario.getContraseña(), usuarioExistente.getContraseña())) {
             // Considerar si realmente se debe permitir cambiar la contraseña aquí o en un endpoint dedicado
            usuarioExistente.setContraseña(passwordEncoder.encode(usuario.getContraseña()));
        }

        // Actualizar rol si se proporciona
        if (usuario.getIdRol() != null && !usuario.getIdRol().equals(usuarioExistente.getRol().getIdRol())) {
            usuarioExistente.setRol(rolRepository.findById(usuario.getIdRol())
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
        }

        // Actualizar permisos personalizados si se proporcionan (asumiendo que vienen en el objeto usuario)
        // Nota: La asignación de permisos se maneja en un endpoint separado en UsuarioController,
        // quizás no sea necesario actualizar aquí directamente. Si se necesita, descomentar y ajustar.
        /*
        if (usuario.getPermisosPersonalizados() != null) {
            usuarioExistente.setPermisosPersonalizados(usuario.getPermisosPersonalizados());
        }
        */

        return usuarioRepository.save(usuarioExistente);
    }

    // Implementación de desactivarUsuario (soft delete)
    @Override
    @Transactional // Añadir Transactional
    public void desactivarUsuario(int id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setEstado(false);
        usuarioRepository.save(usuario);
    }

    // Implementación de activarUsuario
    @Override
    @Transactional // Añadir Transactional
    public void activarUsuario(int id) {
        Usuario usuario = obtenerUsuarioPorId(id);
        usuario.setEstado(true);
        usuarioRepository.save(usuario);
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
    public boolean tienePermiso(Usuario usuario, String nombrePermiso) {
        return !usuarioRepository.findByPermisoEffective(nombrePermiso).stream()
                .filter(u -> u.getIdUsuario().equals(usuario.getIdUsuario()))
                .collect(Collectors.toList())
                .isEmpty();
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