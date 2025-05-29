package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.components;

import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces.INotificacionCtrl;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.NotificacionFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.INotificacionService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.function.Consumer;

/**
 * Implementación del controlador de notificaciones para el menú principal.
 * Maneja toda la lógica relacionada con el sistema de notificaciones.
 */
public class NotificacionController implements INotificacionCtrl {
    
    private final INotificacionService notificacionService;
    private final Button btnNotificaciones;
    private final Consumer<String> showAlert; // Para mostrar alertas
    
    private Popup notificacionesPopup;
    private Label contadorNotificaciones;
    private Timer timerNotificaciones;
    private ObservableList<NotificacionFX> listaNotificaciones = FXCollections.observableArrayList();
    
    /**
     * Constructor del controlador de notificaciones
     * @param notificacionService Servicio de notificaciones
     * @param btnNotificaciones Botón de notificaciones del FXML
     * @param showAlert Función para mostrar alertas
     */
    public NotificacionController(INotificacionService notificacionService, Button btnNotificaciones, 
                                Consumer<String> showAlert) {
        this.notificacionService = notificacionService;
        this.btnNotificaciones = btnNotificaciones;
        this.showAlert = showAlert;
        
        inicializarContadorNotificaciones();
        programarActualizacionNotificaciones();
    }
    
    @Override
    public void irNotificaciones(ActionEvent event) {
        if (!PermisosUIUtil.verificarPermisoConAlerta("ver_notificaciones")) {
            return;
        }

        if (notificacionesPopup == null || !notificacionesPopup.isShowing()) {
            mostrarPopupNotificaciones();
        } else {
            notificacionesPopup.hide();
        }
    }
    
    @Override
    public void mostrarPopupNotificaciones() {
        // Crear el contenido del popup
        VBox contenido = new VBox(10);
        contenido.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; " +
                "-fx-padding: 10; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);");
        contenido.setPrefWidth(350);

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
                    System.err.println("Error al cargar notificaciones para el popup: " + ex.getMessage());
                    ex.printStackTrace();
                    showAlert.accept("No se pudieron cargar las notificaciones.");
                });
            }
        };
        Thread thread = new Thread(cargarDatosPopupTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Crea un item visual para una notificación
     */
    private VBox crearItemNotificacion(NotificacionFX notificacion) {
        VBox item = new VBox(5);
        item.setStyle("-fx-padding: 10; -fx-border-color: #f0f0f0; -fx-border-width: 0 0 1 0; " +
                "-fx-background-color: " + (notificacion.getLeida() ? "white" : "#f8f9fa") + ";");

        // Título de la notificación
        Label titulo = new Label(notificacion.getTitulo());
        titulo.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");

        // Mensaje
        Label mensaje = new Label(notificacion.getMensaje());
        mensaje.setStyle("-fx-wrap-text: true; -fx-font-size: 11px; -fx-text-fill: #666666;");
        mensaje.setWrapText(true);

        // Fecha y acciones
        HBox footer = new HBox(10);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label fecha = new Label(notificacion.getFecha().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        fecha.setStyle("-fx-font-size: 10px; -fx-text-fill: #999999;");

        // Botones de acción
        HBox botones = new HBox(5);
        botones.setAlignment(Pos.CENTER_RIGHT);

        // Botón marcar como leída/no leída
        Button btnMarcar = new Button();
        if (notificacion.getLeida()) {
            btnMarcar.setText("Marcar como no leída");
            btnMarcar.setStyle("-fx-font-size: 9px; -fx-padding: 2 4;");
        } else {
            btnMarcar.setText("Marcar como leída");
            btnMarcar.setStyle("-fx-font-size: 9px; -fx-padding: 2 4; -fx-background-color: #d4edda; -fx-text-fill: #155724;");
        }
        btnMarcar.setOnAction(e -> marcarNotificacion(notificacion));

        // Botón eliminar
        Button btnEliminar = new Button("Eliminar");
        btnEliminar.setStyle("-fx-font-size: 9px; -fx-padding: 2 4; -fx-background-color: #f8d7da; -fx-text-fill: #721c24;");
        btnEliminar.setOnAction(e -> eliminarNotificacion(notificacion));

        botones.getChildren().addAll(btnMarcar, btnEliminar);
        footer.getChildren().addAll(fecha, new StackPane(), botones);

        item.getChildren().addAll(titulo, mensaje, footer);
        return item;
    }
    
    @Override
    public void marcarNotificacion(NotificacionFX notificacion) {
        if (this.notificacionService == null) {
            showAlert.accept("Servicio de notificaciones no inicializado.");
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
            actualizarDespuesDeAccion();
        });

        marcarTask.setOnFailed(e -> {
            Throwable ex = marcarTask.getException();
            Platform.runLater(() -> showAlert.accept("No se pudo actualizar la notificación: " + ex.getMessage()));
            ex.printStackTrace();
        });
        Thread thread = new Thread(marcarTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    @Override
    public void eliminarNotificacion(NotificacionFX notificacion) {
        if (this.notificacionService == null) {
            showAlert.accept("Servicio de notificaciones no inicializado.");
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
            actualizarDespuesDeAccion();
        });

        eliminarTask.setOnFailed(e -> {
            Throwable ex = eliminarTask.getException();
            Platform.runLater(() -> showAlert.accept("No se pudo eliminar la notificación: " + ex.getMessage()));
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
        actualizarNotificaciones(); // Para el contador.

        // Si el popup está visible, necesitamos que su contenido se actualice.
        if (notificacionesPopup != null && notificacionesPopup.isShowing()) {
            notificacionesPopup.hide(); // Cierra el popup actual
            mostrarPopupNotificaciones(); // Vuelve a abrirlo, lo que recargará su contenido asíncrono
        } else {
            cargarNotificaciones(); // << LLAMADA AÑADIDA AQUÍ
        }
    }
    
    @Override
    public void detenerTimers() {
        if (timerNotificaciones != null) {
            timerNotificaciones.cancel();
        }
    }
    
    @Override
    public void inicializarContadorNotificaciones() {
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
    
    @Override
    public void programarActualizacionNotificaciones() {
        if (this.notificacionService == null) return;
        // Actualizar cada 60 segundos (ajustable según necesidades)
        timerNotificaciones = new Timer(true);
        timerNotificaciones.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                Platform.runLater(() -> {
                    actualizarNotificaciones(); // Actualiza el contador de la insignia
                    cargarNotificaciones();   // Actualiza la lista de datos subyacente
                });
            }
        }, 0, 60000);
    }
    
    @Override
    public void actualizarNotificaciones() {
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
        });

        contarTask.setOnFailed(e -> {
            Throwable ex = contarTask.getException();
            System.err.println("Error al actualizar contador de notificaciones: " + ex.getMessage());
            ex.printStackTrace();
        });

        Thread thread = new Thread(contarTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    @Override
    public void cargarNotificaciones() {
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
    
    @Override
    public ObservableList<NotificacionFX> getListaNotificaciones() {
        return listaNotificaciones;
    }
    
    @Override
    public void cleanup() {
        detenerTimers();
        if (notificacionesPopup != null && notificacionesPopup.isShowing()) {
            notificacionesPopup.hide();
        }
    }
}
