package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor;

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de gestión de Vendedores.
 * Define las operaciones de negocio relacionadas con los vendedores.
 */
public interface VendedorService {

    /**
     * Obtiene todos los vendedores.
     * @return Lista de todos los vendedores.
     */
    List<Vendedor> obtenerTodos();

    /**
     * Obtiene un vendedor por su ID.
     * @param id ID del vendedor.
     * @return Optional con el vendedor si se encuentra, vacío si no.
     */
    Optional<Vendedor> obtenerPorId(Integer id);

    /**
     * Guarda un nuevo vendedor o actualiza uno existente.
     * @param vendedor El vendedor a guardar.
     * @return El vendedor guardado.
     */
    Vendedor guardar(Vendedor vendedor);

    /**
     * Elimina un vendedor por su ID.
     * Considera la lógica de negocio (ej. desactivar en lugar de borrar si tiene ventas).
     * @param id ID del vendedor a eliminar.
     */
    void eliminar(Integer id);

    /**
     * Busca vendedores por nombre.
     * @param nombre Término de búsqueda para el nombre.
     * @return Lista de vendedores que coinciden con el nombre.
     */
    List<Vendedor> buscarPorNombre(String nombre);

    /**
     * Obtiene todos los vendedores activos.
     * @return Lista de vendedores activos.
     */
    List<Vendedor> obtenerActivos();

    /**
     * Desactiva un vendedor.
     * @param id ID del vendedor a desactivar.
     * @return Optional con el vendedor desactivado si se encuentra.
     */
    Optional<Vendedor> desactivarVendedor(Integer id);

    /**
     * Activa un vendedor.
     * @param id ID del vendedor a activar.
     * @return Optional con el vendedor activado si se encuentra.
     */
    Optional<Vendedor> activarVendedor(Integer id);

    /**
     * Obtiene un vendedor por su ID, asegurando que la entidad Usuario asociada esté cargada.
     * @param id ID del vendedor.
     * @return Optional con el vendedor y su usuario si se encuentra, vacío si no.
     */
    Optional<Vendedor> obtenerVendedorConUsuario(Integer id);
}
