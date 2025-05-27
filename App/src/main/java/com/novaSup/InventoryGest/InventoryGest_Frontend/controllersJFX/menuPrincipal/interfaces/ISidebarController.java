package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

/**
 * Interfaz para el controlador de la barra lateral del menú principal.
 * Define las operaciones disponibles para gestionar el estado y comportamiento
 * de la barra lateral (sidebar).
 */
public interface ISidebarController {
    
    /**
     * Inicializa el controlador de la barra lateral.
     * Configura las animaciones y el estado inicial.
     */
    void initialize();
    
    /**
     * Establece el estado de la barra lateral (expandida o colapsada).
     * @param expand True para expandir, false para colapsar
     * @param animate True para animar la transición, false para cambio inmediato
     */
    void setSidebarState(boolean expand, boolean animate);
    
    /**
     * Obtiene el estado actual de la barra lateral.
     * @return true si está expandida, false si está colapsada
     */
    boolean isSidebarExpanded();
    
    /**
     * Maneja el evento de toggle (alternar) de la barra lateral.
     * @param event Evento de acción del botón toggle
     */
    void handleToggleSidebar(ActionEvent event);
    
    /**
     * Maneja el evento cuando el mouse entra en la barra lateral.
     * @param event Evento de mouse
     */
    void handleSidePanelMouseEnter(MouseEvent event);
    
    /**
     * Maneja el evento cuando el mouse sale de la barra lateral.
     * @param event Evento de mouse
     */
    void handleSidePanelMouseExit(MouseEvent event);
    
    /**
     * Establece qué botón está actualmente activo (resaltado).
     * @param button El botón a marcar como activo
     */
    void setActiveButton(Button button);
    
    /**
     * Obtiene el botón actualmente activo.
     * @return El botón activo actual
     */
    Button getCurrentActiveButton();
    
    /**
     * Libera recursos y detiene animaciones.
     */
    void cleanup();
}
