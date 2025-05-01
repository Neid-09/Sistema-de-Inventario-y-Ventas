package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.LoteFX;

import java.util.Date;
import java.util.List;

public interface ILoteService {
    /**
     * Obtiene todos los lotes disponibles
     * @return Lista de lotes
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    List<LoteFX> obtenerTodos() throws Exception;

    /**
     * Obtiene un lote por su ID
     * @param id ID del lote
     * @return El lote encontrado o null si no existe
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    LoteFX obtenerPorId(Integer id) throws Exception;

    /**
     * Obtiene todos los lotes de un producto específico
     * @param idProducto ID del producto
     * @return Lista de lotes del producto
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    List<LoteFX> obtenerPorProducto(Integer idProducto) throws Exception;

    /**
     * Obtiene lotes próximos a vencer
     * @param diasAlerta Días para la alerta de vencimiento
     * @return Lista de lotes próximos a vencer
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    List<LoteFX> obtenerProximosAVencer(Integer diasAlerta) throws Exception;

    /**
     * Obtiene lotes por rango de fecha de entrada
     * @param fechaInicio Fecha inicial del rango
     * @param fechaFin Fecha final del rango
     * @return Lista de lotes en el rango de fechas
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    List<LoteFX> obtenerPorRangoFechaEntrada(Date fechaInicio, Date fechaFin) throws Exception;

    /**
     * Guarda un nuevo lote
     * @param lote Lote a guardar
     * @return El lote creado con su ID asignado
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    LoteFX crear(LoteFX lote) throws Exception;

    /**
     * Actualiza un lote existente
     * @param lote Lote con los datos actualizados
     * @return El lote actualizado
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    LoteFX actualizar(LoteFX lote) throws Exception;

    /**
     * Elimina (desactiva) un lote
     * @param id ID del lote a eliminar
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    void eliminar(Integer id) throws Exception;

    /**
     * Activa un lote previamente desactivado
     * @param id ID del lote a activar
     * @return El lote activado
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    LoteFX activar(Integer id) throws Exception;

    /**
     * Desactiva un lote
     * @param id ID del lote a desactivar
     * @return El lote desactivado
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    LoteFX desactivar(Integer id) throws Exception;

    /**
     * Obtiene todos los lotes inactivos
     * @return Lista de lotes inactivos
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    List<LoteFX> obtenerInactivos() throws Exception;


    List<LoteFX> obtenerActivos() throws Exception;
    /**
     * Procesa una devolución para un lote específico
     * @param idLote ID del lote al que se le va a devolver producto
     * @param cantidad Cantidad a devolver (debe ser mayor a cero)
     * @return El lote actualizado con la nueva cantidad
     * @throws Exception Si ocurre un error en la comunicación con la API o la cantidad es inválida
     * De momento no
     */
    LoteFX procesarDevolucion(Integer idLote, Integer cantidad) throws Exception;

    LoteFX reducirCantidadLote(Integer idLote, Integer cantidad) throws Exception;
    void reducirCantidadDeLotes(Integer idProducto, Integer cantidadTotal) throws Exception;
}