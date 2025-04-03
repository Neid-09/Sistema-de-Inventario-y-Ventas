package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Permiso;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario guardarUsuario(Usuario usuario);
    List<Usuario> listarUsuarios();
    Usuario actualizarUsuario(int id, Usuario usuario);
    void eliminarUsuario(int id);
    Usuario autenticarUsuario(String correo, String contraseña);
    Usuario obtenerUsuarioPorId(int id);
    boolean tienePermiso(Usuario usuario, String nombrePermiso);
    List<Permiso> obtenerTodosLosPermisos(Usuario usuario);
}