package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Proveedor;

import java.util.List;
import java.util.Optional;

public interface IProveedorService {

    /**
     * Obtiene todos los proveedores disponibles.
     * @return Lista de proveedores
     * @throws Exception Si ocurre un error al comunicarse con el API
     */
    List<Proveedor> obtenerTodos() throws Exception;

    /**
     * Busca proveedores por nombre o correo.
     * @param termino Término de búsqueda (nombre o correo)
     * @return Lista de proveedores que coinciden con el criterio
     * @throws Exception Si ocurre un error al comunicarse con el API
     */
    List<Proveedor> buscarPorNombreOCorreo(String termino) throws Exception;

    /**
     * Obtiene un proveedor por su identificador.
     * @param id Identificador del proveedor
     * @return Proveedor encontrado (opcional)
     * @throws Exception Si ocurre un error al comunicarse con el API
     */
    Optional<Proveedor> obtenerPorId(Integer id) throws Exception;

    /**
     * Guarda un proveedor (nuevo o existente).
     * @param proveedor Proveedor a guardar
     * @return Proveedor guardado con su ID asignado
     * @throws Exception Si ocurre un error al comunicarse con el API
     */
    Proveedor guardar(Proveedor proveedor) throws Exception;

    /**
     * Actualiza un proveedor existente.
     * @param id ID del proveedor a actualizar
     * @param proveedor Datos actualizados del proveedor
     * @return Proveedor actualizado
     * @throws Exception Si ocurre un error al comunicarse con el API
     */
    Proveedor actualizar(Integer id, Proveedor proveedor) throws Exception;

    /**
     * Elimina un proveedor por su identificador.
     * @param id Identificador del proveedor a eliminar
     * @return true si se eliminó correctamente, false en caso contrario
     * @throws Exception Si ocurre un error al comunicarse con el API o si tiene productos asociados
     */
    boolean eliminar(Integer id) throws Exception;

    /**
     * Obtiene la cantidad de productos asociados a un proveedor.
     * @param id ID del proveedor
     * @return Cantidad de productos asociados
     * @throws Exception Si ocurre un error al comunicarse con el API
     */
    int obtenerCantidadProductosAsociados(Integer id) throws Exception;
}