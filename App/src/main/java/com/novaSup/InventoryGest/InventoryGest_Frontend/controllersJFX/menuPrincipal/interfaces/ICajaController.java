package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICajaService;

/**
 * Interfaz para el controlador delegado de gestión de caja.
 * Define las operaciones relacionadas con el sistema de caja en el menú principal.
 */
public interface ICajaController {
    
    /**
     * Inicializa el controlador de caja con las referencias FXML necesarias.
     * @param cajaService Servicio de caja inyectado
     * @param btnCaja Botón de abrir/cerrar caja
     * @param lblDineroInicial Label para mostrar dinero inicial
     * @param lblTotalEsperadoCaja Label para mostrar total esperado en caja
     * @param lblTotalVentas Label para mostrar total de ventas
     * @param lblProductosVendidos Label para mostrar productos vendidos
     * @param alertCallback Callback para mostrar alertas
     */
    void inicializar(ICajaService cajaService, Button btnCaja, Label lblDineroInicial, 
                    Label lblTotalEsperadoCaja, Label lblTotalVentas, Label lblProductosVendidos,
                    AlertCallback alertCallback);
    
    /**
     * Carga la información de la caja abierta para el usuario actual.
     * Actualiza los labels en el footer y el texto del botón de caja.
     */
    void cargarInformacionCajaAbierta();
    
    /**
     * Maneja la acción del botón Abrir/Cerrar Caja.
     * @param event Evento del botón
     */
    void handleBotonCaja(ActionEvent event);
    
    /**
     * Limpia los recursos utilizados por el controlador.
     */
    void cleanup();
    
    /**
     * Interfaz funcional para callback de alertas.
     */
    @FunctionalInterface
    interface AlertCallback {
        void mostrarAlerta(javafx.scene.control.Alert.AlertType tipo, String titulo, String mensaje);
    }
}
