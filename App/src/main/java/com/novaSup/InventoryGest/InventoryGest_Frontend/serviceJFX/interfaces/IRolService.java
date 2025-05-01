package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.PermisoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import java.util.List;
import java.util.Set;

public interface IRolService {

    /**
     * Obtiene todos los roles del sistema
     */
    List<RolFX> obtenerRoles() throws Exception;

    /**
     * Obtiene un rol específico por su ID
     */
    RolFX obtenerRol(Integer id) throws Exception;

    /**
     * Crea un nuevo rol en el sistema
     */
    RolFX crearRol(RolFX rol) throws Exception;

    /**
     * Actualiza un rol existente
     */
    RolFX actualizarRol(Integer id, RolFX rol) throws Exception;

    /**
     * Elimina un rol del sistema
     */
    void eliminarRol(Integer id) throws Exception;

    /**
     * Obtiene los permisos asignados a un rol
     */
    Set<PermisoFX> obtenerPermisosRol(Integer idRol) throws Exception;

    /**
     * Asigna permisos a un rol
     */
    RolFX asignarPermisosRol(Integer idRol, List<Integer> permisosIds) throws Exception;
}