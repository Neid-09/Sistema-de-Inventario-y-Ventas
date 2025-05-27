package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICajaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.INotificacionService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
// Ya estaba, pero para confirmar
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent; // Import MouseEvent
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration; // Import Duration

// Imports para carga asíncrona y tareas
import javafx.concurrent.Task;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.scene.shape.Rectangle; // Import para Rectangle

// Importar VenderControllerFX
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta.VenderControllerFX;

// Importar controladores de componentes
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces.INotificacionCtrl;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.components.NotificacionController;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces.ISidebarController;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.components.SidebarController;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces.ICajaController;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.components.CajaController;

public class MenuPrincipalControllerFX implements Initializable {

    @FXML private HBox topBar;

    @FXML private Label lblUsuario;

    @FXML private Button btnCerrarSesion;

    @FXML private Button btnNotificaciones;

    @FXML private StackPane modulosDinamicos;

    @FXML private Label lblRol;

    // Declare services as final fields, injected via constructor
    private final INotificacionService notificacionService;
    private final ICajaService cajaService; // Inyectar el servicio de caja
    private final Callback<Class<?>, Object> controllerFactory; // To load nested FXMLs

    // Controlador de Notificaciones - reemplaza todas las variables relacionadas con notificaciones
    private INotificacionCtrl notificacionController;

    @FXML
    private VBox sidePanelVBox; // Added for the side panel

    // Added FXML fields for the labels inside buttons
    @FXML private Label lblVender;
    @FXML private Label lblReporteVentas;
    @FXML private Label lblInventario;
    @FXML private Label lblConsulta;
    @FXML private Label lblCreditoClientes;
    @FXML private Label lblMasVendido;
    @FXML private Label lblEntradasSalidas;
    @FXML private Label lblGarantiasServicios;
    @FXML private Label lblConfigurar;

    // Added FXML field for the Inicio button
    @FXML private Button btnInicio;

    // Added FXML field for the Inicio button's label
    @FXML private Label lblInicio;

    @FXML private Button btnToggleSidebar; // Added toggle button
    @FXML private ImageView toggleIcon; // Added icon for toggle button

    //buttons for modules
    @FXML private Button btnVender;
    @FXML private Button btnReporteVentas;
    @FXML private Button btnInventario;
    @FXML private Button btnConsulta;
    @FXML private Button btnCreditoClientes;
    @FXML private Button btnMasVendido;
    @FXML private Button btnEntradasSalidas;
    @FXML private Button btnGarantiasServicios;
    @FXML private Button btnConfiguracion;

    // Controlador del Sidebar - reemplaza todas las variables relacionadas con sidebar
    private ISidebarController sidebarController;

    // Controlador de Caja - reemplaza todas las variables y métodos relacionados con caja
    private ICajaController cajaController;

    // FXML fields para la información de la caja en el footer
    @FXML private Label lblDineroInicial;
    @FXML private Label lblTotalEsperadoCaja;
    @FXML private Label lblTotalVentas;
    @FXML private Label lblProductosVendidos;
    @FXML private Button btnCaja;

    // Dentro de MenuPrincipalControllerFX.java, fuera de cualquier método, pero dentro de la clase:
    private static class ModuleLoadResult {
        Parent root;
        Object controller;

        ModuleLoadResult(Parent root, Object controller) {
            this.root = root;
            this.controller = controller;
        }
    }

    // Constructor for dependency injection
    public MenuPrincipalControllerFX(INotificacionService notificacionService, ICajaService cajaService, Callback<Class<?>, Object> controllerFactory) {
        this.notificacionService = notificacionService;
        this.cajaService = cajaService;
        this.controllerFactory = controllerFactory;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Iniciando MenuPrincipalControllerFX.initialize()");

        // Configurar Clip para modulosDinamicos para la animación de deslizamiento
        Rectangle clipRect = new Rectangle();
        clipRect.widthProperty().bind(modulosDinamicos.widthProperty());
        clipRect.heightProperty().bind(modulosDinamicos.heightProperty());
        modulosDinamicos.setClip(clipRect);

        // Diagnóstico de permisos
        PermisosUIUtil.imprimirTodosLosPermisos();

        // Establecer datos de usuario en la interfaz
        String nombreUsuario = LoginServiceImplFX.getNombreUsuario();
        if (nombreUsuario != null) {
            lblUsuario.setText(nombreUsuario);
            System.out.println("Usuario establecido: " + nombreUsuario);

            LoginServiceImplFX.getPermisos().stream()
                    .filter(permiso -> permiso.startsWith("ROLE_"))
                    .findFirst()
                    .ifPresent(rol -> {
                        lblRol.setText("ROL: " + rol.substring(5));
                        System.out.println("Rol establecido: " + rol);
                    });
        }

        if (this.notificacionService == null) {
            System.err.println("Error crítico: INotificacionService no fue inyectado.");
            mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "Servicio de notificaciones no disponible. Algunas funciones pueden fallar.");
            // No retornar aquí necesariamente, pero algunas funcionalidades pueden estar degradadas.
        }
        if (this.cajaService == null) {
             System.err.println("Error crítico: ICajaService no fue inyectado.");
            mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "Servicio de caja no disponible. Algunas funciones pueden fallar.");
        }

        inicializarNotificaciones();
        configurarPermisos();
        inicializarSidebar(); // Inicializar el controlador del sidebar
        inicializarCaja(); // Inicializar el controlador de caja

        System.out.println("MenuPrincipalControllerFX.initialize() completado (sin configuración de stage ni carga de módulo inicial aquí).");
    }

    /**
     * Inicializa el controlador del sidebar con todas las referencias FXML necesarias.
     */
    private void inicializarSidebar() {
        // Crear el controlador del sidebar con todas las referencias FXML
        sidebarController = new SidebarController(
            sidePanelVBox, toggleIcon, btnInicio,
            lblInicio, lblVender, lblReporteVentas, lblInventario, 
            lblConsulta, lblCreditoClientes, lblMasVendido, 
            lblEntradasSalidas, lblGarantiasServicios, lblConfigurar
        );
        
        // Inicializar el controlador
        sidebarController.initialize();
    }

    /**
     * Inicializa el controlador de notificaciones.
     */
    private void inicializarNotificaciones() {
        // Crear el controlador de notificaciones
        notificacionController = new NotificacionController(
            notificacionService, 
            btnNotificaciones, 
            mensaje -> mostrarAlerta(Alert.AlertType.WARNING, "Notificaciones", mensaje)
        );
    }

    /**
     * Inicializa el controlador de caja con el patrón de delegación.
     */
    private void inicializarCaja() {
        // Crear el controlador de caja
        cajaController = new CajaController();
        
        // Inicializar el controlador con todas las referencias FXML y el callback de alertas
        cajaController.inicializar(
            cajaService,
            btnCaja,
            lblDineroInicial,
            lblTotalEsperadoCaja,
            lblTotalVentas,
            lblProductosVendidos,
            this::mostrarAlerta // Callback para mostrar alertas
        );
        
        // Cargar información inicial de la caja
        cajaController.cargarInformacionCajaAbierta();
        
        // Configurar acción del botón de caja
        btnCaja.setOnAction(cajaController::handleBotonCaja);
    }

    public void postDisplaySetup(Stage stage) {
        // Este método es llamado por LoginControllerFX DESPUÉS de que el stage se ha mostrado.
        // Se asume que se llama desde el FX Application Thread.
        if (stage == null) {
            System.err.println("MenuPrincipalControllerFX.postDisplaySetup: Error - Stage es null.");
            mostrarAlerta(Alert.AlertType.ERROR, "Error Interno", "No se pudo configurar la ventana principal (stage nulo).");
            return;
        }

        // Evitar reconfigurar si este controlador ya configuró esta instancia de stage.
        // Útil si este método pudiera ser llamado múltiples veces por error.
        if (this.equals(stage.getUserData())) {
             System.out.println("MenuPrincipalControllerFX.postDisplaySetup: Stage ya fue configurada por este controlador. Se procederá a cargar/refrescar el módulo de inicio.");
        } else {
            System.out.println("MenuPrincipalControllerFX.postDisplaySetup: Configurando Stage por primera vez para este controlador.");
            stage.setResizable(true);
            // La maximización debe ocurrir después de que la escena esté en el stage y el stage sea visible.
            // LoginControllerFX llama a stage.show() antes de postDisplaySetup.
            // Si el stage ya está maximizado por LoginControllerFX, esto es redundante pero inofensivo.
            // Si no, esto lo asegura.
            stage.setMaximized(true);

            stage.setOnCloseRequest(event -> {
                event.consume(); // Prevenir cierre automático
                confirmarCierre(); // Método existente para diálogo de confirmación
            });
            stage.setUserData(this); // Guardar referencia de este controlador en el stage.
        }

        // Cargar el contenido inicial (volverAlInicio es asíncrono)
        try {
            cargarModuloInicioInicial();
            System.out.println("MenuPrincipalControllerFX.postDisplaySetup: Llamada a cargarModuloInicioInicial() (asíncrono) realizada.");
        } catch (Exception e) {
            System.err.println("MenuPrincipalControllerFX.postDisplaySetup: Error al iniciar la carga del módulo de inicio: " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", "No se pudo iniciar la carga del módulo de inicio: " + e.getMessage());
        }
    }

    @FXML
    void irNotificaciones(ActionEvent event) {
        if (notificacionController != null) {
            notificacionController.irNotificaciones(event);
        }
    }
    /**
     * Muestra diálogo de confirmación antes de cerrar la aplicación
     */
    private void confirmarCierre() {
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmación");
        alerta.setHeaderText("¿Está seguro que desea salir?");
        alerta.setContentText("Se cerrará la aplicación completamente.");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            stage.close();
        }
    }

    private void configurarPermisos() {
        // Verificar si el usuario es administrador
        boolean esAdmin = PermisosUIUtil.tienePermiso("ROLE_ADMINISTRADOR");
        System.out.println("¿Es admin? " + esAdmin);

        // Si es admin, habilitar todos los botones independientemente de otros permisos
        if (esAdmin) {
            btnVender.setDisable(false);
            btnInventario.setDisable(false);
            btnEntradasSalidas.setDisable(false);
            btnConfiguracion.setDisable(false);
            btnNotificaciones.setDisable(false);
        } else {
            // Configurar según permisos específicos con los nombres correctos
            PermisosUIUtil.configurarBoton(btnVender, "crear_venta");
            PermisosUIUtil.configurarBoton(btnInventario, "acces_mod_inventario");
            PermisosUIUtil.configurarBoton(btnEntradasSalidas, "acces_mod_EntradasSalidas"); // O el permiso más adecuado
            PermisosUIUtil.configurarBoton(btnConfiguracion, "acces_mod_configurar"); // O el permiso más adecuado
            PermisosUIUtil.configurarBoton(btnNotificaciones, "ver_notificaciones");
        }

        // Configurar visibilidad de la etiqueta de rol
        lblRol.setVisible(true); // Siempre mostrar el rol
    }

    /**
     * Establece el nombre de usuario en la interfaz
     * @param nombre Nombre del usuario a mostrar
     */
    public void establecerUsuario(String nombre) {
        if (lblUsuario != null) {
            lblUsuario.setText(nombre);
        }
    }

    /**
     * Carga un módulo específico en el panel central usando la controller factory
     * @param rutaFXML Ruta del archivo FXML a cargar
     * @throws IOException Si hay un error al cargar el módulo
     */
    public void cargarModuloEnPanel(String rutaFXML) throws IOException {
        if (this.controllerFactory == null) {
            throw new IllegalStateException("ControllerFactory no fue inyectada en MenuPrincipalControllerFX");
        }

        Node contenidoActual = modulosDinamicos.getChildren().isEmpty() ? null : modulosDinamicos.getChildren().get(0);
        double containerWidth = modulosDinamicos.getWidth(); // Ancho del contenedor para el deslizamiento
        if (containerWidth <= 0) { // Fallback si el ancho no está disponible aún (raro, pero por seguridad)
            containerWidth = 800; // Un valor por defecto razonable, ajusta si es necesario
            System.err.println("Advertencia: Ancho de modulosDinamicos no disponible, usando fallback: " + containerWidth);
        }
        final double slideInDistance = containerWidth;

        // Función para cargar y mostrar el nuevo módulo con animación de deslizamiento y fundido
        Runnable cargarNuevoModuloAnimado = () -> {
            Task<ModuleLoadResult> loadModuleTask = new Task<>() {
                @Override
                protected ModuleLoadResult call() throws Exception {
                    if (controllerFactory == null) {
                        throw new IllegalStateException("ControllerFactory no fue inyectada en MenuPrincipalControllerFX");
                    }
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
                    loader.setControllerFactory(controllerFactory);
                    Parent root = loader.load();
                    Object controller = loader.getController(); // Obtener el controlador aquí
                    return new ModuleLoadResult(root, controller); // Devolver un objeto que contenga ambos
                }
            };

            loadModuleTask.setOnSucceeded(event -> {
                ModuleLoadResult result = loadModuleTask.getValue();
                Parent nuevoRoot = result.root; // Obtener el root del resultado
                Object controller = result.controller; // Obtener el controller del resultado

                // Configuración inicial para la animación de entrada
                nuevoRoot.setOpacity(0.0);
                nuevoRoot.setTranslateX(-slideInDistance); // Posicionar a la izquierda, fuera de la vista

                modulosDinamicos.getChildren().setAll(nuevoRoot); // Añadir el nuevo módulo al contenedor

                // --- Configurar callback si es el controlador de Ventas ---
                if (rutaFXML.equals(PathsFXML.VENDER_FXML)) {
                    if (controller instanceof VenderControllerFX) {
                        VenderControllerFX venderController = (VenderControllerFX) controller;
                        // --- Usar el nuevo método setOnVentaExitosa para pasar el callback ---
                        // El callback usa el controlador delegado de caja para actualizar la información
                        // Esta llamada setOnVentaExitosa NO modifica UI, así que no necesita Platform.runLater.
                        // El callback *mismo* (cargarInformacionCajaAbierta) sí debe manejar su asincronía interna
                        // si llama a servicios o modifica UI.
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
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga",
                        "No se pudo cargar el módulo " + rutaFXML + ": " + e.getMessage());
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

    /**
     * Helper method to find the button associated with a given FXML path.
     * This needs to be maintained if FXML paths or button IDs change.
     * @param rutaFXML The path to the FXML file.
     * @return The corresponding Button, or null if not found.
     */
    private Button findButtonForFXML(String rutaFXML) {
        if (rutaFXML == null) return null;

        switch (rutaFXML) {
            case PathsFXML.INICIO_FXML: return btnInicio;
            case PathsFXML.VENDER_FXML: return btnVender;
            case PathsFXML.MOD_INVENTARIO: return btnInventario;
            case PathsFXML.MOD_CLIENTES_MENU: return btnCreditoClientes; // Assuming this is the correct FXML for the button
            case PathsFXML.CONTROLSTOCK_FXML: return btnEntradasSalidas; // Assuming this is the correct FXML for the button
            case PathsFXML.CONFIGURACION_FXML: return btnConfiguracion;
            case PathsFXML.MODULO_REPORTES: return btnReporteVentas; // Assuming this is the correct FXML for the button
            // Add cases for other buttons/FXMLs:
            // case PathsFXML.REPORTE_VENTAS_FXML: return btnReporteVentas;
            // case PathsFXML.CONSULTA_FXML: return btnConsulta;
            // case PathsFXML.MAS_VENDIDO_FXML: return btnMasVendido;
            // case PathsFXML.GARANTIAS_SERVICIOS_FXML: return btnGarantiasServicios;
            default: return null; // Or return btnInicio as a fallback?
        }
    }

    @FXML
    void cerrarSesion() {
        try {
            // Limpiar recursos del controlador de notificaciones
            if (notificacionController != null) notificacionController.cleanup();
            // Limpiar recursos del sidebar
            if (sidebarController != null) sidebarController.cleanup();

            // Limpiar datos de sesión
            LoginServiceImplFX.cerrarSesion();

            // Cargar la ventana de inicio de sesión
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();

            // Restaurar tamaño normal antes de cargar login
            stage.setMaximized(false);

            // Establecer un tamaño adecuado para la ventana de login
            stage.setWidth(923.0);  // Ajusta este valor según el tamaño de tu login
            stage.setHeight(708.0); // Ajusta este valor según el tamaño de tu login

            // Centrar en pantalla
            stage.centerOnScreen();

            // Cargar el login USANDO LA FACTORY
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.LOGIN_FXML));
            if (this.controllerFactory != null) { // Asegurarse que la factory existe
                loader.setControllerFactory(this.controllerFactory);
            } else {
                System.err.println("Advertencia: No se pudo obtener la ControllerFactory al cerrar sesión. El login podría no tener dependencias inyectadas.");
            }
            Parent root = loader.load();

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cerrar la sesión: " + e.getMessage());
            e.printStackTrace(); // Print stack trace for debugging
        }
    }

    @FXML
    void volverAlInicio() {
        modulosDinamicos.getChildren().clear(); // Limpiar contenido actual
        // Ya no se muestra un ProgressIndicator aquí, la carga es asíncrona igualmente.

        Task<Parent> loadInicioTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                if (controllerFactory == null) { // Asegurarse que la factory está disponible
                    throw new IllegalStateException("ControllerFactory no fue inyectada en MenuPrincipalControllerFX o es nula.");
                }
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.INICIO_FXML));
                loader.setControllerFactory(controllerFactory); // Usar la factory global
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga",
                    "No se pudo cargar la página de inicio: " + e.getMessage());
            e.printStackTrace();
        });

        Thread thread = new Thread(loadInicioTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Carga el módulo de inicio INICIALMENTE sin mostrar un ProgressIndicator explícito en modulosDinamicos.
     * La carga del FXML sigue siendo asíncrona.
     */
    private void cargarModuloInicioInicial() {
        modulosDinamicos.getChildren().clear(); // Limpiar contenido actual
        // No se muestra un ProgressIndicator aquí para la carga inicial.

        Task<Parent> loadInicioTask = new Task<>() {
            @Override
            protected Parent call() throws Exception {
                if (controllerFactory == null) {
                    throw new IllegalStateException("ControllerFactory no fue inyectada en MenuPrincipalControllerFX o es nula.");
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
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga",
                    "No se pudo cargar la página de inicio: " + e.getMessage());
            e.printStackTrace();
        });

        Thread thread = new Thread(loadInicioTask);
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    void irVender() {
        try {
            if (PermisosUIUtil.verificarPermisoConAlerta("crear_venta")) {
                cargarModuloEnPanel(PathsFXML.VENDER_FXML);
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el módulo de ventas: " + e.getMessage());
        }
    }

    @FXML
    void irReporteVentas() {
        try {
            if (PermisosUIUtil.verificarPermisoConAlerta("acces_mod_reporte_ventas")) { //TODO: VERIFICAR PERMISO
                cargarModuloEnPanel(PathsFXML.MODULO_REPORTES);
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el módulo de reporte de ventas: " + e.getMessage());
        }
    }

    @FXML
    void irModuloInventario() {
        try {
            if (PermisosUIUtil.verificarPermisoConAlerta("acces_mod_inventario")) {
                cargarModuloEnPanel(PathsFXML.MOD_INVENTARIO);
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el módulo de inventario: " + e.getMessage());
        }
    }

    @FXML
    void irModuloClientes(){
        try {
            // Añadir verificación de permisos
            if (PermisosUIUtil.verificarPermisoConAlerta("acces_mod_clientes")) { // Asumiendo permiso 'acces_mod_clientes'
                cargarModuloEnPanel(PathsFXML.MOD_CLIENTES_MENU);
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el módulo de clientes: " + e.getMessage());
            // Imprimir stack trace para más detalles en la consola
            e.printStackTrace();
        }
    }

    @FXML
    void irModuloEntradasSalidas() {
        try {
            if (PermisosUIUtil.verificarPermisoConAlerta("acces_mod_EntradasSalidas")) {
                cargarModuloEnPanel(PathsFXML.CONTROLSTOCK_FXML);
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el historial de stock: " + e.getMessage());
        }
    }

    @FXML
    void irModuloConfiguracion() {
        try {
            if (PermisosUIUtil.verificarPermisoConAlerta("acces_mod_configurar")) {
                cargarModuloEnPanel(PathsFXML.CONFIGURACION_FXML);
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el módulo de configuración: " + e.getMessage());
        }
    }

    // Métodos @FXML para delegar al sidebar controller
    
    /**
     * Maneja el evento de toggle (alternar) de la barra lateral.
     * Delega la operación al controlador del sidebar.
     */
    @FXML
    void handleToggleSidebar(ActionEvent event) {
        if (sidebarController != null) {
            sidebarController.handleToggleSidebar(event);
        }
    }
    
    /**
     * Maneja el evento cuando el mouse entra en la barra lateral.
     * Delega la operación al controlador del sidebar.
     */
    @FXML
    void handleSidePanelMouseEnter(MouseEvent event) {
        if (sidebarController != null) {
            sidebarController.handleSidePanelMouseEnter(event);
        }
    }
    
    /**
     * Maneja el evento cuando el mouse sale de la barra lateral.
     * Delega la operación al controlador del sidebar.
     */
    @FXML
    void handleSidePanelMouseExit(MouseEvent event) {
        if (sidebarController != null) {
            sidebarController.handleSidePanelMouseExit(event);
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}