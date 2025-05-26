package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.NotificacionFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICajaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.INotificacionService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent; // Import MouseEvent
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Duration; // Import Duration

// Imports para carga asíncrona y tareas
import javafx.concurrent.Task;
import javafx.scene.control.ProgressIndicator; // Ya estaba, pero para confirmar

import java.io.IOException;
import java.net.URL;
import java.util.*;
import javafx.scene.shape.Rectangle; // Import para Rectangle

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CajaResponseFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CajaReporteConsolidadoFX;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Optional;

// Importar VenderControllerFX
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta.VenderControllerFX;

public class MenuPrincipalControllerFX implements Initializable {

    @FXML private HBox topBar;

    @FXML private Label lblUsuario;

    @FXML private Button btnCerrarSesion;

    @FXML private Button btnNotificaciones;

    @FXML private StackPane modulosDinamicos;

    @FXML private Label lblRol;

    private Popup notificacionesPopup;
    private Label contadorNotificaciones;

    // Declare services as final fields, injected via constructor
    private final INotificacionService notificacionService;
    private final ICajaService cajaService; // Inyectar el servicio de caja
    private final Callback<Class<?>, Object> controllerFactory; // To load nested FXMLs

    private Timer timerNotificaciones;
    private ObservableList<NotificacionFX> listaNotificaciones = FXCollections.observableArrayList();

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

    private Timeline expandTimeline;
    private Timeline collapseTimeline;
    private final double collapsedWidth = 60.0;
    private final double expandedWidth = 200.0;
    private boolean isSidebarExpanded = false; // Track sidebar state
    private Button currentActiveButton = null; // Track active button

    // --- Icons for Toggle Button ---
    // Make sure these paths are correct relative to the resources folder
    private final Image iconCollapse = new Image(getClass().getResourceAsStream("/img/menuPrincipal/flecha-izquierda.png"));
    private final Image iconExpand = new Image(getClass().getResourceAsStream("/img/menuPrincipal/flecha-derecha.png"));
    // --- Active Button Style Classes ---
    private final String styleClassModuleActive = "module-button-active";
    private final String styleClassInicioActive = "inicio-button-active";

    // FXML fields para la información de la caja en el footer
    @FXML private Label lblDineroInicial;
    @FXML private Label lblTotalEsperadoCaja;
    @FXML private Label lblTotalVentas;
    @FXML private Label lblProductosVendidos;
    @FXML private Button btnCaja;

    private CajaResponseFX cajaAbiertaActual = null; // Para guardar la referencia de la caja abierta

    // Formato para mostrar valores monetarios
    private static final DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");

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

        inicializarContadorNotificaciones();
        programarActualizacionNotificaciones(); // Las llamadas internas son asíncronas
        configurarPermisos();
        setupAnimations();
        setSidebarState(false, false);
        setActiveButton(btnInicio); // Marcar el botón de inicio como activo por defecto

        // --- Cargar información inicial de la caja ---
        cargarInformacionCajaAbierta();
        // --- Configurar acción del botón de caja ---
        btnCaja.setOnAction(event -> handleBotonCaja());

        System.out.println("MenuPrincipalControllerFX.initialize() completado (sin configuración de stage ni carga de módulo inicial aquí).");
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

    private void setupAnimations() {
        // Animation for expanding the side panel
        expandTimeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(sidePanelVBox.prefWidthProperty(), expandedWidth, Interpolator.EASE_BOTH)
                )
        );
        expandTimeline.setOnFinished(event -> {
            setLabelVisibility(true);
            isSidebarExpanded = true;
            updateToggleIcon();
        });

        // Animation for collapsing the side panel
        collapseTimeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(sidePanelVBox.prefWidthProperty(), collapsedWidth, Interpolator.EASE_BOTH)
                )
        );
        collapseTimeline.getKeyFrames().add(0, new KeyFrame(Duration.ZERO, event -> {
            if (!isSidebarExpanded) { // Only hide labels if collapsing
                setLabelVisibility(false);
            }
        }));
        collapseTimeline.setOnFinished(event -> {
            if (!isSidebarExpanded) { // Ensure labels are hidden if state is collapsed
                setLabelVisibility(false);
            }
            updateToggleIcon();
        });
    }

    /**
     * Sets the state of the sidebar (expanded or collapsed).
     * @param expand True to expand, false to collapse.
     * @param animate True to animate the transition, false for immediate change.
     */
    private void setSidebarState(boolean expand, boolean animate) {
        isSidebarExpanded = expand; // Update state first
        updateToggleIcon(); // Update icon based on new state

        if (animate) {
            if (expand) {
                collapseTimeline.stop();
                expandTimeline.playFromStart();
            } else {
                expandTimeline.stop();
                collapseTimeline.playFromStart();
            }
        } else {
            // Set immediately without animation
            sidePanelVBox.setPrefWidth(expand ? expandedWidth : collapsedWidth);
            setLabelVisibility(expand);
        }
    }

    private void setLabelVisibility(boolean visible) {
        // Only change visibility if the state requires it
        if (visible == isSidebarExpanded) {
            // Show/hide all module labels based on the 'visible' parameter
            if (lblInicio != null) { lblInicio.setVisible(visible); lblInicio.setManaged(visible); }
            if (lblVender != null) { lblVender.setVisible(visible); lblVender.setManaged(visible); }
            if (lblReporteVentas != null) { lblReporteVentas.setVisible(visible); lblReporteVentas.setManaged(visible); }
            if (lblInventario != null) { lblInventario.setVisible(visible); lblInventario.setManaged(visible); }
            if (lblConsulta != null) { lblConsulta.setVisible(visible); lblConsulta.setManaged(visible); }
            if (lblCreditoClientes != null) { lblCreditoClientes.setVisible(visible); lblCreditoClientes.setManaged(visible); }
            if (lblMasVendido != null) { lblMasVendido.setVisible(visible); lblMasVendido.setManaged(visible); }
            if (lblEntradasSalidas != null) { lblEntradasSalidas.setVisible(visible); lblEntradasSalidas.setManaged(visible); }
            if (lblGarantiasServicios != null) { lblGarantiasServicios.setVisible(visible); lblGarantiasServicios.setManaged(visible); }
            if (lblConfigurar != null) { lblConfigurar.setVisible(visible); lblConfigurar.setManaged(visible); }
        }
    }

    private void updateToggleIcon() {
        if (toggleIcon != null) {
            toggleIcon.setImage(isSidebarExpanded ? iconCollapse : iconExpand);
        }
    }

    /**
     * Sets the visual style for the active button and removes it from the previous one.
     * @param button The button to mark as active.
     */
    private void setActiveButton(Button button) {
        if (currentActiveButton != null) {
            // Remove active class from the previous button
            currentActiveButton.getStyleClass().remove(styleClassModuleActive);
            currentActiveButton.getStyleClass().remove(styleClassInicioActive);
        }

        currentActiveButton = button;

        if (currentActiveButton != null) {
            // Add the appropriate active class to the new button
            if (currentActiveButton == btnInicio) {
                currentActiveButton.getStyleClass().add(styleClassInicioActive);
            } else {
                currentActiveButton.getStyleClass().add(styleClassModuleActive);
            }
        }
    }

    @FXML
    void handleToggleSidebar(ActionEvent event) {
        setSidebarState(!isSidebarExpanded, true); // Toggle state with animation
    }

    @FXML
    void handleSidePanelMouseEnter(MouseEvent event) {
        // Expand only if not already expanded by the toggle button
        if (!isSidebarExpanded) {
            setSidebarState(true, true); // Expand with animation
        }
    }

    @FXML
    void handleSidePanelMouseExit(MouseEvent event) {
        // Collapse only if it was expanded by hover (not manually by toggle)
        // This logic might need refinement depending on desired interaction.
        // For now, let's keep it simple: if mouse leaves, collapse.
        // A more robust approach might involve checking if the toggle button forced expansion.
        setSidebarState(false, true); // Collapse with animation
    }

    @FXML
    void irNotificaciones(ActionEvent event) {
        if (!PermisosUIUtil.verificarPermisoConAlerta("ver_notificaciones")) {
            return;
        }

        if (notificacionesPopup == null || !notificacionesPopup.isShowing()) {
            mostrarPopupNotificaciones();
        } else {
            notificacionesPopup.hide();
        }
    }

    private void mostrarPopupNotificaciones() {
        // Crear el contenido del popup
        VBox contenido = new VBox(10);
        contenido.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; " +
                "-fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        contenido.setPrefWidth(350);
        // Ajustar altura o hacerla dinámica según el contenido y un máximo.
        // contenido.setPrefHeight(400); // Comentado para permitir que crezca o usar ScrollPane

        // Crear el título
        Label titulo = new Label("Notificaciones");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        contenido.getChildren().add(titulo);

        // Placeholder para la lista de notificaciones y el indicador de carga
        VBox listaItemsContainer = new VBox(5); // Contenedor real de los items
        ScrollPane scrollPane = new ScrollPane(listaItemsContainer);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");
        scrollPane.setPrefHeight(300); // Altura fija para el área de scroll

        ProgressIndicator popupLoadIndicator = new ProgressIndicator();
        popupLoadIndicator.setMaxSize(40, 40); // Tamaño moderado
        StackPane loadingPane = new StackPane(popupLoadIndicator); // Para centrar el indicador
        loadingPane.setPrefHeight(300); // Que ocupe el espacio del scroll mientras carga
        // Inicialmente mostrar el indicador de carga en lugar del scrollpane
        contenido.getChildren().add(loadingPane);


        // Botón para ver todas las notificaciones (podría abrir una vista completa)
        Button btnVerTodas = new Button("Ver todas");
        btnVerTodas.setStyle("-fx-background-color: #083671; -fx-text-fill: white;");
        btnVerTodas.setOnAction(e -> {
            if (notificacionesPopup != null) notificacionesPopup.hide();
            // Aquí podrías cargar una vista completa de notificaciones
            // Ejemplo: cargarModuloEnPanel(PathsFXML.TODAS_NOTIFICACIONES_FXML);
        });
        contenido.getChildren().add(btnVerTodas);

        // Crear y mostrar el popup (si no existe)
        if (notificacionesPopup == null) {
            notificacionesPopup = new Popup();
            notificacionesPopup.setAutoHide(true); // Importante para que se cierre al hacer clic fuera
        }
        notificacionesPopup.getContent().setAll(contenido); // Usar setAll para asegurar que el contenido se actualiza

        // Mostrar el popup
        notificacionesPopup.show(btnNotificaciones.getScene().getWindow(),
                btnNotificaciones.localToScene(0, 0).getX() + btnNotificaciones.getScene().getX() + btnNotificaciones.getScene().getWindow().getX(),
                btnNotificaciones.localToScene(0, 0).getY() + btnNotificaciones.getScene().getY() + btnNotificaciones.getScene().getWindow().getY() + btnNotificaciones.getHeight());

        // Tarea para cargar las notificaciones de forma asíncrona
        Task<List<NotificacionFX>> cargarDatosPopupTask = new Task<>() {
            @Override
            protected List<NotificacionFX> call() throws Exception {
                if (notificacionService == null) {
                    throw new IllegalStateException("Servicio de notificaciones no disponible.");
                }
                // Simular un pequeño retraso para ver el indicador (opcional, para pruebas)
                // Thread.sleep(1000);
                return notificacionService.obtenerNotificaciones();
            }

            @Override
            protected void succeeded() {
                List<NotificacionFX> notificaciones = getValue();
                Platform.runLater(() -> {
                    listaNotificaciones.setAll(notificaciones); // Actualizar la lista observable global
                    listaItemsContainer.getChildren().clear(); // Limpiar items previos del contenedor

                    if (notificaciones.isEmpty()) {
                        Label sinNotificaciones = new Label("No tienes notificaciones nuevas.");
                        sinNotificaciones.setStyle("-fx-padding: 20; -fx-text-fill: #888888; -fx-alignment: center;");
                        listaItemsContainer.getChildren().add(sinNotificaciones);
                    } else {
                        for (NotificacionFX notificacion : notificaciones) {
                            VBox itemNotificacion = crearItemNotificacion(notificacion);
                            listaItemsContainer.getChildren().add(itemNotificacion);
                        }
                    }
                    // Reemplazar el indicador de carga con el ScrollPane que contiene los ítems
                    contenido.getChildren().remove(loadingPane);
                    // Insertar el scrollPane en la posición correcta (antes del botón "Ver todas")
                    if (contenido.getChildren().contains(btnVerTodas)) {
                        int indexBtnVerTodas = contenido.getChildren().indexOf(btnVerTodas);
                        contenido.getChildren().add(indexBtnVerTodas, scrollPane);
                    } else { // Fallback si el botón no está (no debería pasar)
                        contenido.getChildren().add(scrollPane);
                    }
                });
            }

            @Override
            protected void failed() {
                Throwable ex = getException();
                Platform.runLater(() -> {
                    listaItemsContainer.getChildren().clear();
                    Label errorLabel = new Label("Error: " + ex.getMessage());
                    errorLabel.setStyle("-fx-text-fill: red; -fx-padding: 10px;");
                    listaItemsContainer.getChildren().add(errorLabel);

                    // Reemplazar el indicador de carga con el ScrollPane (que ahora muestra el error)
                    contenido.getChildren().remove(loadingPane);
                    if (contenido.getChildren().contains(btnVerTodas)) {
                        int indexBtnVerTodas = contenido.getChildren().indexOf(btnVerTodas);
                        contenido.getChildren().add(indexBtnVerTodas, scrollPane);
                    } else {
                        contenido.getChildren().add(scrollPane);
                    }
                    System.err.println("Error al cargar notificaciones para el popup: " + ex.getMessage());
                    ex.printStackTrace();
                    mostrarAlerta(Alert.AlertType.WARNING, "Notificaciones", "No se pudieron cargar las notificaciones.");
                });
            }
        };
        Thread thread = new Thread(cargarDatosPopupTask);
        thread.setDaemon(true);
        thread.start();
    }

    private VBox crearItemNotificacion(NotificacionFX notificacion) {
        VBox item = new VBox(5);
        item.setStyle("-fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0; -fx-padding: 8;");
        if (!notificacion.getLeida()) {
            item.setStyle(item.getStyle() + "-fx-background-color: #f0f7ff;");
        }

        Label lblTitulo = new Label(notificacion.getTitulo());
        lblTitulo.setStyle("-fx-font-weight: bold;");

        Label lblMensaje = new Label(notificacion.getMensaje());
        lblMensaje.setWrapText(true);

        HBox acciones = new HBox(10);

        Button btnMarcarLeida = new Button(notificacion.getLeida() ? "Marcar no leída" : "Marcar leída");
        btnMarcarLeida.setStyle("-fx-background-color: transparent; -fx-text-fill: #083671;");
        btnMarcarLeida.setOnAction(e -> marcarNotificacion(notificacion));

        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-background-color: transparent; -fx-text-fill: #d32f2f;");
        btnEliminar.setOnAction(e -> eliminarNotificacion(notificacion));

        acciones.getChildren().addAll(btnMarcarLeida, btnEliminar);

        item.getChildren().addAll(lblTitulo, lblMensaje, acciones);
        return item;
    }

    private void marcarNotificacion(NotificacionFX notificacion) {
        if (this.notificacionService == null) { // Check injected service
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Servicio de notificaciones no inicializado.");
            return;
        }

        Task<Void> marcarTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                if (notificacion.getLeida()) {
                    notificacionService.marcarComoNoLeida(notificacion.getIdNotificacion());
                } else {
                    notificacionService.marcarComoLeida(notificacion.getIdNotificacion());
                }
                return null;
            }
        };

        marcarTask.setOnSucceeded(e -> {
            // No es necesario hacer nada aquí si actualizarDespuesDeAccion se encarga
            // Platform.runLater(this::actualizarDespuesDeAccion);
            // Sin embargo, es mejor llamar a actualizarDespuesDeAccion desde aquí
            // para asegurar que se ejecuta después de que la tarea haya tenido éxito.
            // Y actualizarDespuesDeAccion DEBE estar en Platform.runLater si modifica UI directamente.
            // Pero actualizarDespuesDeAccion ya llama a otros métodos asíncronos, así que está bien.
            actualizarDespuesDeAccion();
        });

        marcarTask.setOnFailed(e -> {
            Throwable ex = marcarTask.getException();
            Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo actualizar la notificación: " + ex.getMessage()));
            ex.printStackTrace();
        });
        Thread thread = new Thread(marcarTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void eliminarNotificacion(NotificacionFX notificacion) {
        if (this.notificacionService == null) { // Check injected service
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Servicio de notificaciones no inicializado.");
            return;
        }
        Task<Void> eliminarTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                notificacionService.eliminarNotificacion(notificacion.getIdNotificacion());
                return null;
            }
        };

        eliminarTask.setOnSucceeded(e -> {
            // Similar a marcarNotificacion
            actualizarDespuesDeAccion();
        });

        eliminarTask.setOnFailed(e -> {
            Throwable ex = eliminarTask.getException();
            Platform.runLater(() -> mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo eliminar la notificación: " + ex.getMessage()));
            ex.printStackTrace();
        });
        Thread thread = new Thread(eliminarTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Método común para actualizar después de una acción (eliminar o marcar)
     */
    private void actualizarDespuesDeAccion() {
        // Estas llamadas son ahora asíncronas y actualizarán los datos en segundo plano.
        actualizarNotificaciones(); // Para el contador.

        // Si el popup está visible, necesitamos que su contenido se actualice.
        // La forma más robusta es ocultarlo y volverlo a mostrar,
        // ya que mostrarPopupNotificaciones ahora maneja la carga asíncrona de su contenido.
        if (notificacionesPopup != null && notificacionesPopup.isShowing()) {
            notificacionesPopup.hide(); // Cierra el popup actual
            mostrarPopupNotificaciones(); // Vuelve a abrirlo, lo que recargará su contenido asíncronamente
        } else {
            // Si el popup no está visible, `actualizarNotificaciones` (llamada arriba)
            // ya inició una tarea para obtener el nuevo conteo.
            // También actualizamos la lista global de notificaciones para que esté al día
            // para la próxima vez que se muestre el popup o si otro componente la observa.
            cargarNotificaciones(); // << LLAMADA AÑADIDA AQUÍ
        }
    }

    public void detenerTimers() {
        if (timerNotificaciones != null) {
            timerNotificaciones.cancel();
        }
    }

    private void inicializarContadorNotificaciones() {
        contadorNotificaciones = new Label("0");
        contadorNotificaciones.getStyleClass().add("notification-count-label");
        contadorNotificaciones.setVisible(false);

        // Añadir el contador al botón de notificaciones
        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(btnNotificaciones.getGraphic(), contadorNotificaciones);
        StackPane.setAlignment(contadorNotificaciones, Pos.TOP_RIGHT);
        btnNotificaciones.setGraphic(stackPane);

        // Cargar las notificaciones iniciales
        actualizarNotificaciones();
    }

    private void programarActualizacionNotificaciones() {
        if (this.notificacionService == null) return; // Check injected service
        // Actualizar cada 60 segundos (ajustable según necesidades)
        timerNotificaciones = new Timer(true);
        timerNotificaciones.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    actualizarNotificaciones(); // Actualiza el contador de la insignia
                    cargarNotificaciones();   // Actualiza la lista de datos subyacente << LLAMADA AÑADIDA AQUÍ
                });
            }
        }, 0, 60000);
    }

    private void actualizarNotificaciones() {
        if (this.notificacionService == null) {
            System.err.println("Servicio de notificaciones no disponible para actualizar contador.");
            return;
        }

        Task<Integer> contarTask = new Task<>() {
            @Override
            protected Integer call() throws Exception {
                return notificacionService.contarNotificacionesNoLeidas();
            }
        };

        contarTask.setOnSucceeded(e -> {
            int cantidad = contarTask.getValue();
            Platform.runLater(() -> {
                if (contadorNotificaciones != null) { // Asegurarse que el label existe
                    contadorNotificaciones.setText(String.valueOf(cantidad));
                    contadorNotificaciones.setVisible(cantidad > 0);
                }
            });
            // No se llama a cargarNotificaciones aquí directamente. El popup lo hará si se abre.
            // Si la lista global 'listaNotificaciones' necesitara estar siempre actualizada,
            // podríamos iniciar aquí otra tarea para 'cargarNotificaciones()'.
        });

        contarTask.setOnFailed(e -> {
            Throwable ex = contarTask.getException();
            System.err.println("Error al actualizar contador de notificaciones: " + ex.getMessage());
            // Opcional: Mostrar un estado de error en la UI para el contador, ej. "!"
            // Platform.runLater(() -> {
            //     if (contadorNotificaciones != null) {
            //         contadorNotificaciones.setText("!");
            //         contadorNotificaciones.setVisible(true);
            //     }
            // });
            ex.printStackTrace();
        });

        Thread thread = new Thread(contarTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void cargarNotificaciones() {
        if (this.notificacionService == null) {
            System.err.println("Servicio de notificaciones no disponible para cargar lista global.");
            Platform.runLater(listaNotificaciones::clear);
            return;
        }
        System.out.println("cargarNotificaciones(): Iniciando carga asíncrona para listaNotificaciones global.");

        Task<List<NotificacionFX>> obtenerTask = new Task<>() {
            @Override
            protected List<NotificacionFX> call() throws Exception {
                return notificacionService.obtenerNotificaciones();
            }
        };

        obtenerTask.setOnSucceeded(e -> {
            List<NotificacionFX> notificaciones = obtenerTask.getValue();
            Platform.runLater(() -> {
                listaNotificaciones.setAll(notificaciones);
                System.out.println("cargarNotificaciones(): listaNotificaciones global actualizada con " + notificaciones.size() + " elementos.");
            });
        });

        obtenerTask.setOnFailed(e -> {
            Throwable ex = obtenerTask.getException();
            System.err.println("Error al cargar notificaciones para lista global: " + ex.getMessage());
            Platform.runLater(listaNotificaciones::clear); // Limpiar en caso de error
            ex.printStackTrace();
        });
        Thread thread = new Thread(obtenerTask);
        thread.setDaemon(true);
        thread.start();
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
                        // El callback es una referencia al método cargarInformacionCajaAbierta
                        // de esta instancia de MenuPrincipalControllerFX.
                        // Esta llamada setOnVentaExitosa NO modifica UI, así que no necesita Platform.runLater.
                        // El callback *mismo* (cargarInformacionCajaAbierta) sí debe manejar su asincronía interna
                        // si llama a servicios o modifica UI.
                        venderController.setOnVentaExitosa(this::cargarInformacionCajaAbierta);
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
                    setActiveButton(targetButton);
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
            detenerTimers();
            // Stop animations if running
            if (expandTimeline != null) expandTimeline.stop();
            if (collapseTimeline != null) collapseTimeline.stop();

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
            setActiveButton(btnInicio); // Marcar el botón de Inicio como activo
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
            setActiveButton(btnInicio); // Marcar el botón de Inicio como activo
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

    // --- Lógica para la Caja ---

    /**
     * Carga la información de la caja abierta para el usuario actual y actualiza los labels en el footer.
     * También actualiza el texto del botón de caja (Abrir/Cerrar).
     */
    private void cargarInformacionCajaAbierta() {
        // TODO: Implementar obtención correcta del ID de usuario desde LoginServiceImplFX
        Integer idUsuario = 31; // Temporalmente hardcodeado para compilación/prueba
        Task<Optional<CajaResponseFX>> getCajaTask = new Task<>() {
            @Override
            protected Optional<CajaResponseFX> call() throws Exception {
                if (cajaService == null) {
                     throw new IllegalStateException("Servicio de caja no disponible.");
                }
                return cajaService.getCajaAbiertaPorUsuario(idUsuario);
            }
        };

        getCajaTask.setOnSucceeded(event -> {
            Optional<CajaResponseFX> cajaOpt = getCajaTask.getValue();
            if (cajaOpt.isPresent()) {
                cajaAbiertaActual = cajaOpt.get();
                System.out.println("Caja abierta encontrada con ID: " + cajaAbiertaActual.getIdCaja());
                // Si hay caja abierta, obtener reporte consolidado
                cargarReporteConsolidado(cajaAbiertaActual.getIdCaja());
                 Platform.runLater(() -> btnCaja.setText("Cerrar Caja"));
            } else {
                System.out.println("No hay caja abierta para el usuario.");
                cajaAbiertaActual = null;
                actualizarLabelsCaja(null, null); // Limpiar labels
                 Platform.runLater(() -> btnCaja.setText("Abrir Caja"));
            }
        });

        getCajaTask.setOnFailed(event -> {
            Throwable ex = getCajaTask.getException();
            System.err.println("Error al obtener caja abierta: " + ex.getMessage());
            ex.printStackTrace();
            actualizarLabelsCaja(null, null); // Limpiar labels en caso de error
            Platform.runLater(() -> {
                 mostrarAlerta(Alert.AlertType.ERROR, "Error de Caja", "No se pudo verificar el estado de la caja: " + ex.getMessage());
                 btnCaja.setText("Abrir Caja (Error)"); // Indicar error en el botón si es posible
            });
        });

        Thread thread = new Thread(getCajaTask);
        thread.setDaemon(true);
        thread.start();
    }

     /**
     * Obtiene el reporte consolidado para una caja específica y actualiza los labels.
     * @param idCaja ID de la caja para la que obtener el reporte.
     */
    private void cargarReporteConsolidado(Integer idCaja) {
         if (idCaja == null || cajaService == null) {
             actualizarLabelsCaja(null, null); // Limpiar si no hay ID o servicio
             return;
         }
        Task<Optional<CajaReporteConsolidadoFX>> getReporteTask = new Task<>() {
            @Override
            protected Optional<CajaReporteConsolidadoFX> call() throws Exception {
                 return cajaService.getReporteConsolidadoByCajaId(idCaja);
            }
        };

        getReporteTask.setOnSucceeded(event -> {
            Optional<CajaReporteConsolidadoFX> reporteOpt = getReporteTask.getValue();
            Platform.runLater(() -> {
                if (reporteOpt.isPresent()) {
                    CajaReporteConsolidadoFX reporte = reporteOpt.get();
                    System.out.println("Reporte consolidado obtenido.");
                    actualizarLabelsCaja(reporte, cajaAbiertaActual);
                } else {
                    System.out.println("No se encontró reporte consolidado para la caja ID: " + idCaja);
                    actualizarLabelsCaja(null, cajaAbiertaActual); // Mantener info de caja si se tiene, limpiar reporte
                }
            });
        });

        getReporteTask.setOnFailed(event -> {
            Throwable ex = getReporteTask.getException();
            System.err.println("Error al obtener reporte consolidado: " + ex.getMessage());
            ex.printStackTrace();
            Platform.runLater(() -> {
                actualizarLabelsCaja(null, cajaAbiertaActual); // Limpiar labels de reporte en caso de error
                 mostrarAlerta(Alert.AlertType.ERROR, "Error de Reporte", "No se pudo obtener el reporte consolidado: " + ex.getMessage());
            });
        });

        Thread thread = new Thread(getReporteTask);
        thread.setDaemon(true);
        thread.start();
    }

    /**
     * Actualiza los labels en el footer con la información de la caja y su reporte.
     * @param reporte Reporte consolidado (puede ser null).
     * @param caja Caja abierta (puede ser null).
     */
    private void actualizarLabelsCaja(CajaReporteConsolidadoFX reporte, CajaResponseFX caja) {
        // Asegurarse de que los labels existen antes de actualizar
        if (lblDineroInicial == null || lblTotalEsperadoCaja == null || lblTotalVentas == null || lblProductosVendidos == null) {
            System.err.println("Error: Labels del footer no inicializados en FXML.");
            return;
        }

        if (reporte != null) {
            // Usar los campos del reporte consolidado
            BigDecimal dineroInicial = (reporte.getDineroInicial() != null) ? reporte.getDineroInicial() : BigDecimal.ZERO;
            BigDecimal totalEsperadoCaja = reporte.getTotalEsperadoCaja() != null ? reporte.getTotalEsperadoCaja() : BigDecimal.ZERO;
            BigDecimal totalVentas = reporte.getTotalGeneralVentas() != null ? reporte.getTotalGeneralVentas() : BigDecimal.ZERO;
            int totalProductosVendidos = reporte.getTotalUnidadesVendidas(); // getTotalUnidadesVendidas() devuelve int primitivo, no puede ser null

            lblDineroInicial.setText(formatCurrency(dineroInicial));
            lblTotalEsperadoCaja.setText(formatCurrency(totalEsperadoCaja));
            lblTotalVentas.setText(formatCurrency(totalVentas));
            lblProductosVendidos.setText(String.valueOf(totalProductosVendidos));

        } else if (caja != null) {
            // Si no hay reporte consolidado pero sí información básica de la caja
            BigDecimal dineroInicial = (caja.getDineroInicial() != null) ? caja.getDineroInicial() : BigDecimal.ZERO;

            lblDineroInicial.setText(formatCurrency(dineroInicial));
            // En este caso, mostramos 0.00 para esperado, ventas y productos ya que el reporte no está disponible
            lblTotalEsperadoCaja.setText(formatCurrency(BigDecimal.ZERO));
            lblTotalVentas.setText(formatCurrency(BigDecimal.ZERO));
            lblProductosVendidos.setText("0");

        } else {
            // Si no hay ninguna información de caja disponible
            lblDineroInicial.setText(formatCurrency(BigDecimal.ZERO));
            lblTotalEsperadoCaja.setText(formatCurrency(BigDecimal.ZERO));
            lblTotalVentas.setText(formatCurrency(BigDecimal.ZERO));
            lblProductosVendidos.setText("0");
        }
    }

    /**
     * Formatea un BigDecimal como moneda.
     * @param value El valor a formatear.
     * @return String formateado.
     */
    private String formatCurrency(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        return currencyFormat.format(value);
    }

     /**
     * Maneja la acción del botón Abrir/Cerrar Caja.
     * Dependiendo del estado actual (cajaAbiertaActual), intenta abrir o cerrar la caja.
     */
    private void handleBotonCaja() {
         if (cajaService == null) {
             mostrarAlerta(Alert.AlertType.ERROR, "Error", "Servicio de caja no disponible.");
             return;
         }

         if (cajaAbiertaActual == null) {
             // Lógica para ABRIR caja
             System.out.println("Intentando abrir caja...");
             // Necesitarás un diálogo o forma de obtener dineroInicial y si hereda saldo anterior
             // Por ahora, mostramos una alerta simple. La implementación real requerirá más UI.
             mostrarAlerta(Alert.AlertType.INFORMATION, "Abrir Caja", "Implementar lógica para abrir caja (solicitar dinero inicial, etc.).");
             // TODO: Implementar UI para abrir caja y llamar a cajaService.abrirCaja()
         } else {
             // Lógica para CERRAR caja
             System.out.println("Intentando cerrar caja con ID: " + cajaAbiertaActual.getIdCaja());
              // Necesitarás un diálogo o forma de obtener el dineroReal
             // Por ahora, mostramos una alerta simple. La implementación real requerirá más UI.
             mostrarAlerta(Alert.AlertType.INFORMATION, "Cerrar Caja", "Implementar lógica para cerrar caja (solicitar dinero real).");
             // TODO: Implementar UI para cerrar caja y llamar a cajaService.cerrarCaja()
         }
         // Después de abrir o cerrar exitosamente, deberías llamar a cargarInformacionCajaAbierta() de nuevo
         // para actualizar el estado y los labels.
         // Ejemplo (dentro de los callbacks de éxito de abrir/cerrar): cargarInformacionCajaAbierta();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}