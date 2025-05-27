package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.NotificacionFX;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;

/**
 * Interfaz para el controlador de notificaciones del menú principal.
 * Define las operaciones básicas para manejar el sistema de notificaciones.
 */
public interface INotificacionCtrl {
    
    /**
     * Maneja el evento del botón de notificaciones
     * @param event Evento del botón
     */
    void irNotificaciones(ActionEvent event);
    
    /**
     * Muestra el popup de notificaciones
     */
    void mostrarPopupNotificaciones();
    
    /**
     * Marca una notificación como leída/no leída
     * @param notificacion Notificación a marcar
     */
    void marcarNotificacion(NotificacionFX notificacion);
    
    /**
     * Elimina una notificación
     * @param notificacion Notificación a eliminar
     */
    void eliminarNotificacion(NotificacionFX notificacion);
    
    /**
     * Detiene los timers activos
     */
    void detenerTimers();
    
    /**
     * Inicializa el contador de notificaciones en el botón
     */
    void inicializarContadorNotificaciones();
    
    /**
     * Programa la actualización automática de notificaciones
     */
    void programarActualizacionNotificaciones();
    
    /**
     * Actualiza el contador de notificaciones
     */
    void actualizarNotificaciones();
    
    /**
     * Carga la lista de notificaciones
     */
    void cargarNotificaciones();
    
    /**
     * Obtiene la lista observable de notificaciones
     * @return Lista observable de notificaciones
     */
    ObservableList<NotificacionFX> getListaNotificaciones();
    
    /**
     * Realiza la limpieza necesaria al cerrar
     */
    void cleanup();
}
