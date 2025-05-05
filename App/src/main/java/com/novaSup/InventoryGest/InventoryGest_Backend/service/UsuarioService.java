package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Permiso;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario guardarUsuario(Usuario usuario);
    List<Usuario> listarUsuarios(); // Lista todos (activos e inactivos)
    List<Usuario> listarUsuariosActivos(); // Lista solo activos
    List<Usuario> listarUsuariosInactivos(); // Lista solo inactivos
    Usuario actualizarUsuario(int id, Usuario usuario);
    void desactivarUsuario(int id); // Cambia estado a false (soft delete)
    void activarUsuario(int id); // Cambia estado a true
    // Usuario autenticarUsuario(String correo, String contraseña); // Comentado: La autenticación la maneja Spring Security
    Usuario obtenerUsuarioPorId(int id);
    boolean tienePermiso(Usuario usuario, String nombrePermiso);
    List<Permiso> obtenerTodosLosPermisos(Usuario usuario);
    boolean existenUsuariosConRol(Integer idRol);

}