package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.NotificacionFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.INotificacionService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
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
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.net.URL;
import java.util.*;

public class MenuPrincipalControllerFX implements Initializable {

    @FXML
    private HBox topBar;

    @FXML
    private Label lblUsuario;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnVender;

    @FXML
    private Button btnInventario;

    @FXML
    private Button btnNotificaciones;

    @FXML
    private Button btnEntradasSalidas;

    @FXML
    private Button btnConfiguracion;

    @FXML
    private StackPane modulosDinamicos;

    @FXML
    private Label lblRol;

    private Popup notificacionesPopup;
    private Label contadorNotificaciones;

    // Declare services as final fields, injected via constructor
    private final INotificacionService notificacionService;
    private final Callback<Class<?>, Object> controllerFactory; // To load nested FXMLs

    private Timer timerNotificaciones;
    private ObservableList<NotificacionFX> listaNotificaciones = FXCollections.observableArrayList();

    // Constructor for dependency injection
    public MenuPrincipalControllerFX(INotificacionService notificacionService, Callback<Class<?>, Object> controllerFactory) {
        this.notificacionService = notificacionService;
        this.controllerFactory = controllerFactory;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        System.out.println("Iniciando MenuPrincipalControllerFX.initialize()");

        // Remove direct instantiation: notificacionService = new NotificacionServiceImplFX();
        // DependencyManager and CustomControllerFactory handle this now.

        // Diagnóstico de permisos
        PermisosUIUtil.imprimirTodosLosPermisos();

        try {
            // Configurar la ventana después de que todo esté cargado
            Platform.runLater(() -> {
                // Obtener la ventana actual
                Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();

                // Configurar botones nativos de la ventana
                stage.setResizable(true);

                // Establecer pantalla completa
                stage.setMaximized(true);

                // Manejar el cierre de la ventana con alerta
                stage.setOnCloseRequest(event -> {
                    event.consume(); // Prevenir cierre automático
                    confirmarCierre();
                });

                // Guardar referencia al controlador
                stage.setUserData(this);
            });

            // Establecer datos de usuario en la interfaz
            String nombreUsuario = LoginServiceImplFX.getNombreUsuario();
            if (nombreUsuario != null) {
                lblUsuario.setText(nombreUsuario);
                System.out.println("Usuario establecido: " + nombreUsuario);

                // Mostrar el rol del usuario
                LoginServiceImplFX.getPermisos().stream()
                        .filter(permiso -> permiso.startsWith("ROLE_"))
                        .findFirst()
                        .ifPresent(rol -> {
                            lblRol.setText("ROL: " + rol.substring(5));
                            System.out.println("Rol establecido: " + rol);
                        });
            }

            // Ensure service is available before using it
            if (this.notificacionService == null) {
                System.err.println("Error crítico: INotificacionService no fue inyectado.");
                // Consider showing an error to the user or disabling notification features
                return; // Prevent further execution that depends on the service
            }

            // Inicializar el badge contador de notificaciones
            inicializarContadorNotificaciones();

            // Programar actualización periódica de notificaciones
            programarActualizacionNotificaciones();

            // Configurar permisos de botones
            configurarPermisos();

            // Cargar contenido inicial
            Platform.runLater(() -> {
                try {
                    volverAlInicio();
                    System.out.println("Módulo inicial cargado correctamente");
                } catch (Exception e) {
                    System.err.println("Error al cargar módulo inicial: " + e.getMessage());
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("Error general en initialize: " + e.getMessage());
            e.printStackTrace();
        }
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
        contenido.setPrefHeight(400);

        // Crear el título
        Label titulo = new Label("Notificaciones");
        titulo.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
        contenido.getChildren().add(titulo);

        // Cargar notificaciones
        cargarNotificaciones();

        if (listaNotificaciones.isEmpty()) {
            Label sinNotificaciones = new Label("No tienes notificaciones");
            sinNotificaciones.setStyle("-fx-padding: 20; -fx-text-fill: #888888;");
            contenido.getChildren().add(sinNotificaciones);
        } else {
            ScrollPane scrollPane = new ScrollPane();
            scrollPane.setFitToWidth(true);
            scrollPane.setStyle("-fx-background-color: transparent;");

            VBox listaItems = new VBox(5);

            for (NotificacionFX notificacion : listaNotificaciones) {
                VBox itemNotificacion = crearItemNotificacion(notificacion);
                listaItems.getChildren().add(itemNotificacion);
            }

            scrollPane.setContent(listaItems);
            scrollPane.setPrefHeight(300);
            contenido.getChildren().add(scrollPane);
        }

        // Botón para ver todas las notificaciones (podría abrir una vista completa)
        Button btnVerTodas = new Button("Ver todas");
        btnVerTodas.setStyle("-fx-background-color: #083671; -fx-text-fill: white;");
        btnVerTodas.setOnAction(e -> {
            notificacionesPopup.hide();
            // Aquí podrías cargar una vista completa de notificaciones
        });
        contenido.getChildren().add(btnVerTodas);

        // Crear y mostrar el popup
        notificacionesPopup = new Popup();
        notificacionesPopup.getContent().add(contenido);
        notificacionesPopup.setAutoHide(true);

        // Mostrar el popup debajo del botón de notificaciones
        notificacionesPopup.show(btnNotificaciones.getScene().getWindow(),
                btnNotificaciones.localToScene(0, 0).getX() + btnNotificaciones.getScene().getX() + btnNotificaciones.getScene().getWindow().getX(),
                btnNotificaciones.localToScene(0, 0).getY() + btnNotificaciones.getScene().getY() + btnNotificaciones.getScene().getWindow().getY() + btnNotificaciones.getHeight());
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
        try {
            if (notificacion.getLeida()) {
                // Si está leída, marcarla como no leída
                notificacionService.marcarComoNoLeida(notificacion.getIdNotificacion());
            } else {
                // Si no está leída, marcarla como leída
                notificacionService.marcarComoLeida(notificacion.getIdNotificacion());
            }

            // Actualizar inmediatamente la lista y el contador
            actualizarDespuesDeAccion();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo actualizar la notificación: " + e.getMessage());
        }
    }

    private void eliminarNotificacion(NotificacionFX notificacion) {
        if (this.notificacionService == null) { // Check injected service
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Servicio de notificaciones no inicializado.");
            return;
        }
        try {
            notificacionService.eliminarNotificacion(notificacion.getIdNotificacion());

            // Actualizar inmediatamente la lista y el contador
            actualizarDespuesDeAccion();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo eliminar la notificación: " + e.getMessage());
        }
    }

    /**
     * Método común para actualizar después de una acción (eliminar o marcar)
     */
    private void actualizarDespuesDeAccion() {
        if (this.notificacionService == null) return; // Check injected service
        // Actualizar el contador de notificaciones
        actualizarNotificaciones();

        // Recargar la lista de notificaciones
        cargarNotificaciones();

        // Actualizar el popup si está visible
        if (notificacionesPopup != null && notificacionesPopup.isShowing()) {
            notificacionesPopup.hide();
            mostrarPopupNotificaciones();
        }
    }

    public void detenerTimers() {
        if (timerNotificaciones != null) {
            timerNotificaciones.cancel();
        }
    }

    private void inicializarContadorNotificaciones() {
        contadorNotificaciones = new Label("0");
        contadorNotificaciones.setStyle("-fx-background-color: red; -fx-text-fill: white; " +
                "-fx-background-radius: 10; -fx-padding: 2 6 2 6; -fx-font-size: 10px;");
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
                Platform.runLater(() -> actualizarNotificaciones());
            }
        }, 0, 60000);
    }

    private void actualizarNotificaciones() {
        if (this.notificacionService == null) { // Check injected service
            System.err.println("Servicio de notificaciones no disponible para actualizar contador.");
            return;
        }
        try {
            int cantidad = notificacionService.contarNotificacionesNoLeidas();
            Platform.runLater(() -> {
                contadorNotificaciones.setText(String.valueOf(cantidad));
                contadorNotificaciones.setVisible(cantidad > 0);
            });

            // Actualizar la lista de notificaciones si el popup está visible
            if (notificacionesPopup != null && notificacionesPopup.isShowing()) {
                cargarNotificaciones();
            }
        } catch (Exception e) {
            System.err.println("Error al actualizar notificaciones: " + e.getMessage());
        }
    }

    private void cargarNotificaciones() {
        if (this.notificacionService == null) { // Check injected service
            System.err.println("Servicio de notificaciones no disponible para cargar lista.");
            listaNotificaciones.clear();
            return;
        }
        try {
            List<NotificacionFX> notificaciones = notificacionService.obtenerNotificaciones();
            listaNotificaciones.setAll(notificaciones);
        } catch (Exception e) {
            System.err.println("Error al cargar notificaciones: " + e.getMessage());
            // Vaciar la lista en caso de error para evitar acceder a elementos eliminados
            listaNotificaciones.clear();
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
        FXMLLoader loader = new FXMLLoader(getClass().getResource(rutaFXML));
        loader.setControllerFactory(this.controllerFactory); // <<< USE THE FACTORY
        Parent root = loader.load();

        // Limpiar el panel y agregar el nuevo módulo
        modulosDinamicos.getChildren().clear();
        modulosDinamicos.getChildren().add(root);
    }

    @FXML
    void cerrarSesion() {
        try {
            detenerTimers();

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
        // Limpiar el panel central para mostrar la página de inicio
        modulosDinamicos.getChildren().clear();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.INICIO_FXML));
            loader.setControllerFactory(this.controllerFactory); // <<< USE THE FACTORY
            Parent root = loader.load();
            modulosDinamicos.getChildren().add(root);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la página de inicio: " + e.getMessage());
        }
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

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}