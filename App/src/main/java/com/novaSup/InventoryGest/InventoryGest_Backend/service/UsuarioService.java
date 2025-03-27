package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import java.util.List;

public interface UsuarioService {
    Usuario guardarUsuario(Usuario usuario);
    List<Usuario> listarUsuarios();
    Usuario actualizarUsuario(int id, Usuario usuario);
    void eliminarUsuario(int id);
    Usuario autenticarUsuario(String correo, String contraseña);
    Usuario obtenerUsuarioPorId(int id);
}
