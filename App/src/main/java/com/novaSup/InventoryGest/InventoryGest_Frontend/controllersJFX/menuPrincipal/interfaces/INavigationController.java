package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces;

import java.io.IOException;

/**
 * Interfaz para el controlador de navegación entre módulos.
 * Define las operaciones de navegación y carga de módulos en el panel dinámico.
 * 
 * @author Sistema de Inventario
 * @version 1.0
 */
public interface INavigationController {
    
    /**
     * Carga un módulo específico en el panel central usando la controller factory.
     * Incluye animaciones de transición y manejo de errores.
     * 
     * @param rutaFXML Ruta del archivo FXML a cargar
     * @throws IOException Si hay un error al cargar el módulo
     */
    void cargarModuloEnPanel(String rutaFXML) throws IOException;
    
    /**
     * Navega al módulo de inicio con animaciones de transición.
     * Método utilizado cuando se hace clic en el botón de inicio.
     */
    void volverAlInicio();
    
    /**
     * Carga el módulo de inicio inicialmente sin mostrar indicadores de progreso.
     * Utilizado durante la inicialización de la aplicación.
     */
    void cargarModuloInicioInicial();
    
    /**
     * Navega al módulo de ventas.
     */
    void irVender();
    
    /**
     * Navega al módulo de reportes de ventas.
     */
    void irReporteVentas();
    
    /**
     * Navega al módulo de inventario.
     */
    void irModuloInventario();
    
    /**
     * Navega al módulo de clientes.
     */
    void irModuloClientes();
    
    /**
     * Limpia recursos y referencias utilizadas por el controlador de navegación.
     * Debe ser llamado al cerrar la sesión o destruir el controlador principal.
     */
    void cleanup();
}
