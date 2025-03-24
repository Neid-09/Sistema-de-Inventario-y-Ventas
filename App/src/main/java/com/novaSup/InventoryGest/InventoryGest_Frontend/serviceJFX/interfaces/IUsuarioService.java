package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;

import java.util.List;

public interface IUsuarioService {
    List<RolFX> obtenerRoles() throws Exception;
    List<UsuarioFX> obtenerUsuarios() throws Exception;
    UsuarioFX registrarUsuario(UsuarioFX usuario) throws Exception;
    UsuarioFX actualizarUsuario(Integer id, UsuarioFX usuario) throws Exception;
    void eliminarUsuario(Integer id) throws Exception;
}