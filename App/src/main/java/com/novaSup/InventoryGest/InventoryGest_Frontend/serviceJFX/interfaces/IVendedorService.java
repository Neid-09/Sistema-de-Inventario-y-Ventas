package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VendedorFX;
import javafx.collections.ObservableList;

/**
 * Interfaz para el servicio de vendedores en JavaFX.
 * Define las operaciones CRUD y funcionalidades específicas para la gestión de vendedores.
 */
public interface IVendedorService {
    
    /**
     * Obtiene todos los vendedores.
     * @return ObservableList de vendedores
     */
    ObservableList<VendedorFX> obtenerTodosLosVendedores();
    
    /**
     * Obtiene solo los vendedores activos.
     * @return ObservableList de vendedores activos
     */
    ObservableList<VendedorFX> obtenerVendedoresActivos();
    
    /**
     * Obtiene un vendedor por su ID.
     * @param id ID del vendedor
     * @return Vendedor encontrado o null si no existe
     */
    VendedorFX obtenerVendedorPorId(Integer id);
    
    /**
     * Obtiene un vendedor con información completa del usuario.
     * @param id ID del vendedor
     * @return Vendedor con información del usuario o null si no existe
     */
    VendedorFX obtenerVendedorConUsuario(Integer id);
    
    /**
     * Crea un nuevo vendedor.
     * @param vendedor Vendedor a crear
     * @return Vendedor creado con ID asignado
     */
    VendedorFX crearVendedor(VendedorFX vendedor);
    
    /**
     * Actualiza un vendedor existente.
     * @param id ID del vendedor a actualizar
     * @param vendedor Datos actualizados del vendedor
     * @return Vendedor actualizado
     */
    VendedorFX actualizarVendedor(Integer id, VendedorFX vendedor);
    
    /**
     * Elimina un vendedor.
     * @param id ID del vendedor a eliminar
     * @return true si la eliminación fue exitosa
     */
    boolean eliminarVendedor(Integer id);
    
    /**
     * Activa un vendedor.
     * @param id ID del vendedor a activar
     * @return true si la activación fue exitosa
     */
    boolean activarVendedor(Integer id);
    
    /**
     * Desactiva un vendedor.
     * @param id ID del vendedor a desactivar
     * @return true si la desactivación fue exitosa
     */
    boolean desactivarVendedor(Integer id);
}
