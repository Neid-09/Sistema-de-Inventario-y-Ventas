package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.components;

import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces.INavigationController;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces.ISidebarController;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces.ICajaController;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta.VenderControllerFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;

import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Callback;
import javafx.util.Duration;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * Controlador delegado para la navegación entre módulos en el panel dinámico.
 * Implementa el patrón de delegación para separar la responsabilidad de navegación
 * del controlador principal MenuPrincipalControllerFX.
 * 
 * @author Sistema de Inventario
 * @version 1.0
 */
public class NavigationController implements INavigationController {
    
    // Referencias a componentes FXML necesarios
    private final StackPane modulosDinamicos;
    private final Callback<Class<?>, Object> controllerFactory;
    
    // Referencias a otros controladores delegados
    private final ISidebarController sidebarController;
    private final ICajaController cajaController;
    
    // Callback para mostrar alertas
    private final BiConsumer<Alert.AlertType, String> alertCallback;
    
    // Mapeo de botones para actualizar estado activo
    private final Button btnInicio;
    private final Button btnVender;
    private final Button btnInventario;
    private final Button btnCreditoClientes;
    private final Button btnEntradasSalidas;
    private final Button btnConfiguracion;
    private final Button btnReporteVentas;
    
    // Clase interna para encapsular resultado de carga de módulo
    private static class ModuleLoadResult {
        Parent root;
        Object controller;

        ModuleLoadResult(Parent root, Object controller) {
            this.root = root;
            this.controller = controller;
        }
    }
    
    /**
     * Constructor del NavigationController.
     * 
     * @param modulosDinamicos Panel dinámico donde se cargan los módulos
     * @param controllerFactory Factory para crear controladores con inyección de dependencias
     * @param sidebarController Referencia al controlador del sidebar
     * @param cajaController Referencia al controlador de caja
     * @param alertCallback Callback para mostrar alertas
     * @param btnInicio Botón de inicio
     * @param btnVender Botón de ventas
     * @param btnInventario Botón de inventario
     * @param btnCreditoClientes Botón de clientes
     * @param btnEntradasSalidas Botón de entradas/salidas
     * @param btnConfiguracion Botón de configuración
     * @param btnReporteVentas Botón de reportes
     */
    public NavigationController(
            StackPane modulosDinamicos,
            Callback<Class<?>, Object> controllerFactory,
            ISidebarController sidebarController,
            ICajaController cajaController,
            BiConsumer<Alert.AlertType, String> alertCallback,
            Button btnInicio,
            Button btnVender,
            Button btnInventario,
            Button btnCreditoClientes,
            Button btnEntradasSalidas,
            Button btnConfiguracion,
            Button btnReporteVentas) {
        
        this.modulosDinamicos = modulosDinamicos;
        this.controllerFactory = controllerFactory;
        this.sidebarController = sidebarController;
        this.cajaController = cajaController;
        this.alertCallback = alertCallback;
        this.btnInicio = btnInicio;
        this.btnVender = btnVender;
        this.btnInventario = btnInventario;
        this.btnCreditoClientes = btnCreditoClientes;
        this.btnEntradasSalidas = btnEntradasSalidas;
        this.btnConfiguracion = btnConfiguracion;
        this.btnReporteVentas = btnReporteVentas;
    }
    
    @Override
    public void cargarModuloEnPanel(String rutaFXML) throws IOException {
        if (this.controllerFactory == null) {
            throw new IllegalStateException("ControllerFactory no fue inyectada en NavigationController");
        }

        Node contenidoActual = modulosDinamicos.getChildren().isEmpty() ? null : modulosDinamicos.getChildren().get(0);
        double containerWidth = modulosDinamicos.getWidth(); // Ancho del contenedor para el deslizamiento
        if (containerWidth <= 0) { // Fallback si el ancho no está disponible aún
            containerWidth = 800; // Un valor por defecto razonable
            System.err.println("Advertencia: Ancho de modulosDinamicos no disponible, usando fallback: " + containerWidth);
        }
        final double slideInDistance = containerWidth;

        // Función para cargar y mostrar el nuevo módulo con animación de deslizamiento y fundido
        Runnable cargarNuevoModuloAnimado = () -> {
            Task<ModuleLoadResult> loadModuleTask = new Task<>() {
                @Override
                protected ModuleLoadResult call() throws Exception {
                    if (controllerFactory == null) {
                        throw new IllegalStateException("ControllerFactory no fue inyectada en NavigationController");
                    }
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
                    loader.setControllerFactory(controllerFactory);
                    Parent root = loader.load();
                    Object controller = loader.getController();
                    return new ModuleLoadResult(root, controller);
                }
            };

            loadModuleTask.setOnSucceeded(event -> {
                ModuleLoadResult result = loadModuleTask.getValue();
                Parent nuevoRoot = result.root;
                Object controller = result.controller;

                // Configuración inicial para la animación de entrada
                nuevoRoot.setOpacity(0.0);
                nuevoRoot.setTranslateX(-slideInDistance); // Posicionar a la izquierda, fuera de la vista

                modulosDinamicos.getChildren().setAll(nuevoRoot); // Añadir el nuevo módulo al contenedor

                // --- Configurar callback si es el controlador de Ventas ---
                if (rutaFXML.equals(PathsFXML.VENDER_FXML)) {
                    if (controller instanceof VenderControllerFX) {
                        VenderControllerFX venderController = (VenderControllerFX) controller;
                        // Configurar callback de venta exitosa para actualizar información de caja
                        venderController.setOnVentaExitosa(cajaController::cargarInformacionCajaAbierta);
                        System.out.println("Callback de venta exitosa configurado para VenderControllerFX.");
                    } else {
                        System.err.println("El controlador cargado para " + rutaFXML + " no es una instancia de VenderControllerFX. Es: " + (controller != null ? controller.getClass().getName() : "null"));
                    }
                }
                // ----------------------------------------------------------

                // Crear las animaciones
                FadeTransition fadeIn = new FadeTransition(Duration.millis(200), nuevoRoot);
                fadeIn.setFromValue(0.0);
                fadeIn.setToValue(1.0);

                TranslateTransition slideIn = new TranslateTransition(Duration.millis(200), nuevoRoot);
                slideIn.setFromX(-slideInDistance);
                slideIn.setToX(0);

                // Combinar animaciones para que se ejecuten en paralelo
                ParallelTransition parallelTransition = new ParallelTransition(nuevoRoot, fadeIn, slideIn);
                parallelTransition.setOnFinished(e -> {
                    // Después de que el nuevo módulo aparece y se desliza, actualiza el botón activo
                    Button targetButton = findButtonForFXML(rutaFXML);
                    if (sidebarController != null) {
                        sidebarController.setActiveButton(targetButton);
                    }
                    nuevoRoot.setTranslateX(0); // Asegurar que la posición final sea exactamente 0
                });
                parallelTransition.play();
            });

            loadModuleTask.setOnFailed(event -> {
                Throwable e = loadModuleTask.getException();
                e.printStackTrace();
                modulosDinamicos.getChildren().clear();
                Label errorLabel = new Label("Error al cargar módulo: " + rutaFXML + "\n" + e.getMessage());
                errorLabel.setWrapText(true);
                errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 20px;");
                modulosDinamicos.getChildren().add(errorLabel);
                if (alertCallback != null) {
                    alertCallback.accept(Alert.AlertType.ERROR, "No se pudo cargar el módulo " + rutaFXML + ": " + e.getMessage());
                }
            });

            Thread thread = new Thread(loadModuleTask);
            thread.setDaemon(true);
            thread.start();
        };

        if (contenidoActual != null) {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(300), contenidoActual);
            fadeOut.setFromValue(1.0);
            fadeOut.setToValue(0.0);
            fadeOut.setOnFinished(event -> {
                modulosDinamicos.getChildren().clear(); // Limpiar después del fade-out
                cargarNuevoModuloAnimado.run(); // Cargar el nuevo módulo con la nueva animación
            });
            fadeOut.play();
        } else {
            modulosDinamicos.getChildren().clear();
            cargarNuevoModuloAnimado.run();
        }
    }
    
    @Override
    public void volverAlInicio() {
        modulosDinamicos.getChildren().clear(); // Limpiar contenido actual

        Task<Parent> loadInicioTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                if (controllerFactory == null) {
                    throw new IllegalStateException("ControllerFactory no fue inyectada en NavigationController o es nula.");
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.INICIO_FXML));
                loader.setControllerFactory(controllerFactory);
                return loader.load();
            }
        };

        loadInicioTask.setOnSucceeded(event -> {
            Parent root = loadInicioTask.getValue();
            modulosDinamicos.getChildren().setAll(root); // Reemplazar con el contenido cargado
            if (sidebarController != null) {
                sidebarController.setActiveButton(btnInicio); // Marcar el botón de Inicio como activo
            }
            System.out.println("Módulo de inicio (desde botón) cargado y mostrado.");
        });

        loadInicioTask.setOnFailed(event -> {
            Throwable e = loadInicioTask.getException();
            modulosDinamicos.getChildren().clear();
            Label errorLabel = new Label("Error al cargar la página de inicio: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 20px;");
            modulosDinamicos.getChildren().add(errorLabel); // Mostrar mensaje de error
            if (alertCallback != null) {
                alertCallback.accept(Alert.AlertType.ERROR, "No se pudo cargar la página de inicio: " + e.getMessage());
            }
            e.printStackTrace();
        });

        Thread thread = new Thread(loadInicioTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    @Override
    public void cargarModuloInicioInicial() {
        modulosDinamicos.getChildren().clear(); // Limpiar contenido actual

        Task<Parent> loadInicioTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                if (controllerFactory == null) {
                    throw new IllegalStateException("ControllerFactory no fue inyectada en NavigationController o es nula.");
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.INICIO_FXML));
                loader.setControllerFactory(controllerFactory);
                return loader.load();
            }
        };

        loadInicioTask.setOnSucceeded(event -> {
            Parent root = loadInicioTask.getValue();
            modulosDinamicos.getChildren().setAll(root); // Reemplazar con el contenido cargado
            if (sidebarController != null) {
                sidebarController.setActiveButton(btnInicio); // Marcar el botón de Inicio como activo
            }
            System.out.println("Módulo de inicio (inicial) cargado y mostrado.");
        });

        loadInicioTask.setOnFailed(event -> {
            Throwable e = loadInicioTask.getException();
            modulosDinamicos.getChildren().clear();
            Label errorLabel = new Label("Error al cargar la página de inicio: " + e.getMessage());
            errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 20px;");
            modulosDinamicos.getChildren().add(errorLabel);
            if (alertCallback != null) {
                alertCallback.accept(Alert.AlertType.ERROR, "No se pudo cargar la página de inicio: " + e.getMessage());
            }
            e.printStackTrace();
        });

        Thread thread = new Thread(loadInicioTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    @Override
    public void irVender() {
        try {
            if (PermisosUIUtil.verificarPermisoConAlerta("crear_venta")) {
                cargarModuloEnPanel(PathsFXML.VENDER_FXML);
            }
        } catch (IOException e) {
            if (alertCallback != null) {
                alertCallback.accept(Alert.AlertType.ERROR, "No se pudo cargar el módulo de ventas: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void irReporteVentas() {
        try {
            if (PermisosUIUtil.verificarPermisoConAlerta("acces_mod_reporte_ventas")) {
                cargarModuloEnPanel(PathsFXML.MODULO_REPORTES);
            }
        } catch (IOException e) {
            if (alertCallback != null) {
                alertCallback.accept(Alert.AlertType.ERROR, "No se pudo cargar el módulo de reporte de ventas: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void irModuloInventario() {
        try {
            if (PermisosUIUtil.verificarPermisoConAlerta("acces_mod_inventario")) {
                cargarModuloEnPanel(PathsFXML.MOD_INVENTARIO);
            }
        } catch (IOException e) {
            if (alertCallback != null) {
                alertCallback.accept(Alert.AlertType.ERROR, "No se pudo cargar el módulo de inventario: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void irModuloClientes() {
        try {
            if (PermisosUIUtil.verificarPermisoConAlerta("acces_mod_clientes")) {
                cargarModuloEnPanel(PathsFXML.MOD_CLIENTES_MENU);
            }
        } catch (IOException e) {
            if (alertCallback != null) {
                alertCallback.accept(Alert.AlertType.ERROR, "No se pudo cargar el módulo de clientes: " + e.getMessage());
            }
        }
    }
    
    /**
     * Encuentra el botón asociado con una ruta FXML específica.
     * Este método necesita mantenerse si las rutas FXML o IDs de botones cambian.
     * 
     * @param rutaFXML La ruta al archivo FXML.
     * @return El Button correspondiente, o null si no se encuentra.
     */
    private Button findButtonForFXML(String rutaFXML) {
        if (rutaFXML == null) return null;

        switch (rutaFXML) {
            case PathsFXML.INICIO_FXML: return btnInicio;
            case PathsFXML.VENDER_FXML: return btnVender;
            case PathsFXML.MOD_INVENTARIO: return btnInventario;
            case PathsFXML.MOD_CLIENTES_MENU: return btnCreditoClientes;
            case PathsFXML.CONTROLSTOCK_FXML: return btnEntradasSalidas;
            case PathsFXML.CONFIGURACION_FXML: return btnConfiguracion;
            case PathsFXML.MODULO_REPORTES: return btnReporteVentas;
            default: return null;
        }
    }
    
    @Override
    public void cleanup() {
        // Limpiar recursos si es necesario
        // En este caso, las referencias son manejadas por el controlador principal
        System.out.println("NavigationController: Limpieza de recursos completada.");
    }
}
