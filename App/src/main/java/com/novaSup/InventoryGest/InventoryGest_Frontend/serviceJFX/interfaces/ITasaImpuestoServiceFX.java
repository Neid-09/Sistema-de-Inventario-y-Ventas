package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TasaImpuestoFX;
import javafx.collections.ObservableList;

public interface ITasaImpuestoServiceFX {

    /**
     * Obtiene todas las tasas de impuestos del backend.
     * @return Una lista observable de TasaImpuestoFX.
     * @throws Exception Si ocurre un error durante la comunicación HTTP.
     */
    ObservableList<TasaImpuestoFX> getAllTasasImpuestos() throws Exception;

    /**
     * Obtiene una tasa de impuesto específica por su ID.
     * @param id El ID de la tasa de impuesto.
     * @return El objeto TasaImpuestoFX si se encuentra, o null/lanza excepción si no.
     * @throws Exception Si ocurre un error durante la comunicación HTTP o si no se encuentra.
     */
    TasaImpuestoFX getTasaImpuestoById(int id) throws Exception;

    /**
     * Crea una nueva tasa de impuesto en el backend.
     * @param tasaImpuesto El objeto TasaImpuestoFX a crear.
     * @return El objeto TasaImpuestoFX creado con su ID asignado por el backend.
     * @throws Exception Si ocurre un error durante la comunicación HTTP o la validación.
     */
    TasaImpuestoFX createTasaImpuesto(TasaImpuestoFX tasaImpuesto) throws Exception;

    /**
     * Actualiza una tasa de impuesto existente en el backend.
     * @param id El ID de la tasa de impuesto a actualizar.
     * @param tasaImpuesto El objeto TasaImpuestoFX con los datos actualizados.
     * @return El objeto TasaImpuestoFX actualizado.
     * @throws Exception Si ocurre un error durante la comunicación HTTP, la validación o si no se encuentra.
     */
    TasaImpuestoFX updateTasaImpuesto(int id, TasaImpuestoFX tasaImpuesto) throws Exception;

    /**
     * Obtiene todas las tasas de impuestos asociadas a un tipo de impuesto específico.
     * @param idTipoImpuesto El ID del tipo de impuesto.
     * @return Una lista observable de TasaImpuestoFX.
     * @throws Exception Si ocurre un error durante la comunicación HTTP o si el tipo de impuesto no se encuentra.
     */
    ObservableList<TasaImpuestoFX> getTasasImpuestosByTipo(int idTipoImpuesto) throws Exception;
} 