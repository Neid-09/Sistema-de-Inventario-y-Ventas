package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.ImpuestoAplicable;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface ImpuestoAplicableService {
    /**
     * Obtiene impuestos aplicables para un producto o categoría en una fecha determinada.
     * @param idProducto ID del producto (puede ser null)
     * @param idCategoria ID de la categoría (puede ser null)
     * @param fechaActual Fecha actual para la cual se verifica la vigencia del impuesto
     * @return Lista de impuestos aplicables
     */
    List<ImpuestoAplicable> obtenerImpuestosAplicables(Integer idProducto, Integer idCategoria, Date fechaActual);
    
    /**
     * Obtiene todos los impuestos aplicables.
     * @return Lista de todos los impuestos aplicables
     */
    List<ImpuestoAplicable> findAll();
    
    /**
     * Obtiene un impuesto aplicable por su ID.
     * @param id ID del impuesto aplicable
     * @return Optional con el impuesto aplicable si se encuentra
     */
    Optional<ImpuestoAplicable> findById(Integer id);
    
    /**
     * Guarda o actualiza un impuesto aplicable.
     * @param impuestoAplicable El impuesto aplicable a guardar o actualizar
     * @return El impuesto aplicable guardado o actualizado
     */
    ImpuestoAplicable save(ImpuestoAplicable impuestoAplicable);
    
    /**
     * Elimina un impuesto aplicable por su ID.
     * @param id ID del impuesto aplicable a eliminar
     */
    void deleteById(Integer id);
} 