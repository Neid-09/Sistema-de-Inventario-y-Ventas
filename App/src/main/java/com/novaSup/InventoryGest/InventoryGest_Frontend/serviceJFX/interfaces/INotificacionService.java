package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.NotificacionFX;
import java.util.List;

public interface INotificacionService {
    /**
     * Obtiene todas las notificaciones del usuario actual
     * @return Lista de notificaciones
     * @throws Exception Si ocurre un error en la comunicación
     */
    List<NotificacionFX> obtenerNotificaciones() throws Exception;

    /**
     * Obtiene las notificaciones no leídas del usuario actual
     * @return Lista de notificaciones no leídas
     * @throws Exception Si ocurre un error en la comunicación
     */
    List<NotificacionFX> obtenerNotificacionesNoLeidas() throws Exception;

    /**
     * Obtiene el conteo de notificaciones no leídas
     * @return Número de notificaciones no leídas
     * @throws Exception Si ocurre un error en la comunicación
     */
    int contarNotificacionesNoLeidas() throws Exception;

    /**
     * Marca una notificación como leída o no leida
     * @param idNotificacion ID de la notificación
     * @return La notificación actualizada
     * @throws Exception Si ocurre un error en la comunicación
     */
    NotificacionFX marcarComoLeida(Integer idNotificacion) throws Exception;
    void marcarComoNoLeida(Integer idNotificacion) throws Exception;
    /**
     * Elimina una notificación
     * @param idNotificacion ID de la notificación a eliminar
     * @throws Exception Si ocurre un error en la comunicación
     */
    void eliminarNotificacion(Integer idNotificacion) throws Exception;
}