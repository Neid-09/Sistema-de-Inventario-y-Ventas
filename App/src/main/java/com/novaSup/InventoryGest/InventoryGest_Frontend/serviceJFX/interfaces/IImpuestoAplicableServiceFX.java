package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ImpuestoAplicableFX;
import javafx.collections.ObservableList;

// import java.time.LocalDate; // Cambiado a String
// import java.util.concurrent.CompletableFuture; // Eliminado

public interface IImpuestoAplicableServiceFX {

    /**
     * Obtiene una lista de todos los impuestos aplicables.
     * @return Una lista observable de ImpuestoAplicableFX.
     * @throws Exception Si ocurre un error durante la llamada API.
     */
    ObservableList<ImpuestoAplicableFX> listarImpuestosAplicables() throws Exception;

    /**
     * Obtiene un impuesto aplicable por su ID.
     * @param id El ID del impuesto aplicable.
     * @return El ImpuestoAplicableFX o null si no se encuentra (o lanza excepción).
     * @throws Exception Si ocurre un error durante la llamada API o no se encuentra.
     */
    ImpuestoAplicableFX obtenerImpuestoAplicablePorId(int id) throws Exception;

    /**
     * Obtiene los impuestos aplicables a un producto en una fecha específica.
     * @param idProducto El ID del producto.
     * @param fecha La fecha (en formato ISO YYYY-MM-DD) para la cual consultar los impuestos.
     * @return Una lista observable de ImpuestoAplicableFX.
     * @throws Exception Si ocurre un error durante la llamada API.
     */
    ObservableList<ImpuestoAplicableFX> obtenerImpuestosPorProductoYFecha(int idProducto, String fecha) throws Exception;

    /**
     * Obtiene los impuestos aplicables a una categoría en una fecha específica.
     * @param idCategoria El ID de la categoría.
     * @param fecha La fecha (en formato ISO YYYY-MM-DD) para la cual consultar los impuestos.
     * @return Una lista observable de ImpuestoAplicableFX.
     * @throws Exception Si ocurre un error durante la llamada API.
     */
    ObservableList<ImpuestoAplicableFX> obtenerImpuestosPorCategoriaYFecha(int idCategoria, String fecha) throws Exception;

    /**
     * Crea un nuevo impuesto aplicable.
     * @param impuestoAplicable El ImpuestoAplicableFX a crear.
     * @return El ImpuestoAplicableFX creado.
     * @throws Exception Si ocurre un error durante la llamada API.
     */
    ImpuestoAplicableFX crearImpuestoAplicable(ImpuestoAplicableFX impuestoAplicable) throws Exception;

    /**
     * Actualiza un impuesto aplicable existente.
     * @param id El ID del impuesto aplicable a actualizar.
     * @param impuestoAplicable El ImpuestoAplicableFX con los datos actualizados.
     * @return El ImpuestoAplicableFX actualizado.
     * @throws Exception Si ocurre un error durante la llamada API.
     */
    ImpuestoAplicableFX actualizarImpuestoAplicable(int id, ImpuestoAplicableFX impuestoAplicable) throws Exception;

    /**
     * Cambia el estado de aplicabilidad (activo/inactivo) de un impuesto aplicable.
     * @param id El ID del impuesto aplicable.
     * @return El ImpuestoAplicableFX con el estado actualizado.
     * @throws Exception Si ocurre un error durante la llamada API.
     */
    ImpuestoAplicableFX cambiarAplicabilidadImpuesto(int id) throws Exception;

} 