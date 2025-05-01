package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.PermisoFX;

import java.util.List;

/**
 * Interfaz para gestionar permisos en el sistema
 */
public interface IPermisoService {

    /**
     * Obtiene la lista de todos los permisos disponibles
     * @return Lista de permisos
     * @throws Exception Si ocurre algún error en la comunicación con el servidor
     */
    List<PermisoFX> obtenerPermisos() throws Exception;

    /**
     * Obtiene un permiso específico por su ID
     * @param id ID del permiso a obtener
     * @return Permiso encontrado
     * @throws Exception Si ocurre algún error o el permiso no existe
     */
    PermisoFX obtenerPermiso(Integer id) throws Exception;

    /**
     * Crea un nuevo permiso en el sistema
     * @param permiso Permiso a crear
     * @return Permiso creado con su ID asignado
     * @throws Exception Si ocurre algún error durante la creación
     */
    PermisoFX crearPermiso(PermisoFX permiso) throws Exception;

    /**
     * Actualiza un permiso existente
     * @param id ID del permiso a actualizar
     * @param permiso Permiso con los datos actualizados
     * @return Permiso actualizado
     * @throws Exception Si ocurre algún error durante la actualización
     */
    PermisoFX actualizarPermiso(Integer id, PermisoFX permiso) throws Exception;

    /**
     * Elimina un permiso del sistema
     * @param id ID del permiso a eliminar
     * @throws Exception Si ocurre algún error durante la eliminación o el permiso no existe
     */
    void eliminarPermiso(Integer id) throws Exception;
}