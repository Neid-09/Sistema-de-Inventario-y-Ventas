package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.PermisoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;

import java.util.List;
import java.util.Set;

public interface IUsuarioService {
    List<RolFX> obtenerRoles() throws Exception;
    List<UsuarioFX> obtenerUsuarios() throws Exception;
    UsuarioFX registrarUsuario(UsuarioFX usuario) throws Exception;
    UsuarioFX actualizarUsuario(Integer id, UsuarioFX usuario) throws Exception;
    void desactivarUsuario(Integer id) throws Exception;
    void activarUsuario(Integer id) throws Exception;

    /**
     * Obtiene los permisos específicos asignados a un usuario
     */
    Set<PermisoFX> obtenerPermisosEspecificos(Integer idUsuario) throws Exception;

    /**
     * Asigna permisos específicos a un usuario
     */
    void asignarPermisosEspecificos(Integer idUsuario, List<Integer> idsPermisos) throws Exception;

    Set<PermisoFX> obtenerPermisosRol(Integer idRol) throws Exception;
}