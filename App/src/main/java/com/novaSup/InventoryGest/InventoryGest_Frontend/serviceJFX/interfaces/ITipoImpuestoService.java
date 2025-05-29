package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TipoImpuestoFX;
import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de gestión de Tipos de Impuesto en JavaFX.
 */
public interface ITipoImpuestoService {

    /**
     * Obtiene todos los tipos de impuestos desde el backend.
     *
     * @return Una lista de TipoImpuestoFX.
     * @throws Exception Si ocurre un error durante la comunicación con el backend.
     */
    List<TipoImpuestoFX> getAllTiposImpuestos() throws Exception;

    /**
     * Obtiene un tipo de impuesto específico por su ID.
     *
     * @param id El ID del tipo de impuesto a obtener.
     * @return Un Optional conteniendo el TipoImpuestoFX si se encuentra, o vacío si no.
     * @throws Exception Si ocurre un error durante la comunicación con el backend.
     */
    Optional<TipoImpuestoFX> getTipoImpuestoById(int id) throws Exception;

    /**
     * Crea un nuevo tipo de impuesto en el backend.
     *
     * @param tipoImpuesto El TipoImpuestoFX a crear.
     * @return El TipoImpuestoFX creado (puede incluir el ID asignado por el backend).
     * @throws Exception Si ocurre un error durante la creación o comunicación.
     */
    TipoImpuestoFX createTipoImpuesto(TipoImpuestoFX tipoImpuesto) throws Exception;

    /**
     * Actualiza un tipo de impuesto existente en el backend.
     *
     * @param id El ID del tipo de impuesto a actualizar.
     * @param tipoImpuesto El TipoImpuestoFX con los datos actualizados.
     * @return El TipoImpuestoFX actualizado.
     * @throws Exception Si ocurre un error durante la actualización o comunicación.
     */
    TipoImpuestoFX updateTipoImpuesto(int id, TipoImpuestoFX tipoImpuesto) throws Exception;

    /**
     * Cambia el estado (activo/inactivo) de un tipo de impuesto.
     *
     * @param id El ID del tipo de impuesto cuyo estado se va a cambiar.
     * @return El TipoImpuestoFX con el estado actualizado.
     * @throws Exception Si ocurre un error durante la operación o comunicación.
     */
    TipoImpuestoFX changeTipoImpuestoEstado(int id) throws Exception;
} 