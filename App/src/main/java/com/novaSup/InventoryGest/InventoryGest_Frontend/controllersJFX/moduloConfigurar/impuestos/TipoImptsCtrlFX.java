package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar.impuestos;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TipoImpuestoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ITipoImpuestoService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TipoImptsCtrlFX {

    //<editor-fold desc="FXML Fields">
    @FXML private TableView<TipoImpuestoFX> tableViewTiposImpuesto;
    @FXML private TableColumn<TipoImpuestoFX, String> colTipoNombre;
    @FXML private TableColumn<TipoImpuestoFX, String> colTipoDescripcion;
    @FXML private TableColumn<TipoImpuestoFX, Boolean> colTipoEsPorcentual;
    @FXML private TableColumn<TipoImpuestoFX, Boolean> colTipoActivo;
    @FXML private Button btnNuevoTipoImpuesto;
    @FXML private Button btnEditarTipoImpuesto;
    @FXML private Button btnEliminarTipoImpuesto;
    @FXML private VBox formTipoImpuestoContainer;
    @FXML private TextField txtTipoNombre;
    @FXML private TextArea txtTipoDescripcion;
    @FXML private CheckBox chkTipoEsPorcentual;
    @FXML private CheckBox chkTipoActivo;
    @FXML private Button btnGuardarTipoImpuesto;
    @FXML private Button btnCancelarTipoImpuesto;
    //</editor-fold>

    //<editor-fold desc="Servicios y Datos">
    private ITipoImpuestoService tipoImpuestoService;
    private final ObservableList<TipoImpuestoFX> tiposImpuestoData = FXCollections.observableArrayList();
    private final ObservableList<TipoImpuestoFX> tiposImpuestoActivosData = FXCollections.observableArrayList();
    //</editor-fold>

    //<editor-fold desc="Estado">
    private TipoImpuestoFX tipoImpuestoSeleccionadoParaEdicion;
    private boolean modoEdicionTipo = false;
    //</editor-fold>

    // Inyección de servicios (llamado por el controlador principal)
    public void setServicio(ITipoImpuestoService tipoImpuestoService) {
        this.tipoImpuestoService = tipoImpuestoService;
        // Cargar datos una vez que el servicio está disponible
        loadTiposImpuestos();
    }

    // Método para que el controlador principal obtenga los tipos activos
    public ObservableList<TipoImpuestoFX> getTiposImpuestoActivos() {
        return tiposImpuestoActivosData;
    }

    @FXML
    public void initialize() {
        System.out.println("TipoImpuestoTabController inicializado.");
        setupTipoImpuestoTable();
        formTipoImpuestoContainer.setVisible(false);
        formTipoImpuestoContainer.setManaged(false);
        // Los datos se cargan cuando se inyecta el servicio
    }

    //<editor-fold desc="Lógica de la Pestaña">
    private void setupTipoImpuestoTable() {
        colTipoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipoDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colTipoEsPorcentual.setCellValueFactory(new PropertyValueFactory<>("esPorcentual"));
        colTipoActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));
        tableViewTiposImpuesto.setItems(tiposImpuestoData);
    }

    private void loadTiposImpuestos() {
        if (tipoImpuestoService == null) {
            System.err.println("Error: TipoImpuestoService no inyectado en TipoImpuestoTabController.");
            mostrarAlertaError("Error Interno", "El servicio de tipos de impuesto no está disponible.");
            return;
        }
        new Thread(() -> {
            try {
                List<TipoImpuestoFX> listaTipos = tipoImpuestoService.getAllTiposImpuestos();
                List<TipoImpuestoFX> listaTiposActivos = listaTipos.stream()
                        .filter(TipoImpuestoFX::isActivo)
                        .collect(Collectors.toList());
                Platform.runLater(() -> {
                    tiposImpuestoData.setAll(listaTipos);
                    tiposImpuestoActivosData.setAll(listaTiposActivos); // Actualizar lista para ComboBox
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlertaError("Error al cargar tipos de impuesto", "No se pudieron cargar los datos: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleNuevoTipoImpuesto(ActionEvent event) {
        modoEdicionTipo = false;
        tipoImpuestoSeleccionadoParaEdicion = null;
        limpiarFormularioTipoImpuesto();
        chkTipoActivo.setSelected(true);
        chkTipoEsPorcentual.setSelected(true);
        formTipoImpuestoContainer.setVisible(true);
        formTipoImpuestoContainer.setManaged(true);
        txtTipoNombre.requestFocus();
    }

    @FXML
    private void handleEditarTipoImpuesto(ActionEvent event) {
        TipoImpuestoFX seleccionado = tableViewTiposImpuesto.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            modoEdicionTipo = true;
            tipoImpuestoSeleccionadoParaEdicion = seleccionado;
            popularFormularioTipoImpuesto(seleccionado);
            formTipoImpuestoContainer.setVisible(true);
            formTipoImpuestoContainer.setManaged(true);
            txtTipoNombre.requestFocus();
        } else {
            mostrarAlertaAdvertencia("Sin selección", "Por favor, selecciona un tipo de impuesto para editar.");
        }
    }

    @FXML
    private void handleEliminarTipoImpuesto(ActionEvent event) {
        TipoImpuestoFX seleccionado = tableViewTiposImpuesto.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar cambio de estado");
            confirmacion.setHeaderText("¿Realmente deseas cambiar el estado del tipo de impuesto '" + seleccionado.getNombre() + "'?");
            confirmacion.setContentText("Esto cambiará su estado a " + (seleccionado.isActivo() ? "Inactivo" : "Activo") + ".");

            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                new Thread(() -> {
                    try {
                        tipoImpuestoService.changeTipoImpuestoEstado(seleccionado.getIdTipoImpuesto());
                        Platform.runLater(() -> {
                            mostrarAlertaInformacion("Estado cambiado", "El estado del tipo de impuesto ha sido modificado.");
                            loadTiposImpuestos(); // Recargar para ver el cambio y actualizar ComboBox de tasas (indirectamente)
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> mostrarAlertaError("Error al cambiar estado", "No se pudo cambiar el estado: " + e.getMessage()));
                        e.printStackTrace();
                    }
                }).start();
            }
        } else {
            mostrarAlertaAdvertencia("Sin selección", "Por favor, selecciona un tipo de impuesto para cambiar su estado.");
        }
    }

    @FXML
    private void handleGuardarTipoImpuesto(ActionEvent event) {
        String nombre = txtTipoNombre.getText();
        String descripcion = txtTipoDescripcion.getText();
        boolean esPorcentual = chkTipoEsPorcentual.isSelected();
        boolean activo = chkTipoActivo.isSelected();

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlertaAdvertencia("Campo requerido", "El nombre del tipo de impuesto no puede estar vacío.");
            return;
        }

        TipoImpuestoFX tipoImpuestoAGuardar = new TipoImpuestoFX();
        tipoImpuestoAGuardar.setNombre(nombre.trim());
        tipoImpuestoAGuardar.setDescripcion(descripcion != null ? descripcion.trim() : null);
        tipoImpuestoAGuardar.setEsPorcentual(esPorcentual);
        tipoImpuestoAGuardar.setActivo(activo);

        new Thread(() -> {
            try {
                String mensajeExito;
                if (modoEdicionTipo && tipoImpuestoSeleccionadoParaEdicion != null) {
                    tipoImpuestoAGuardar.setIdTipoImpuesto(tipoImpuestoSeleccionadoParaEdicion.getIdTipoImpuesto());
                    tipoImpuestoService.updateTipoImpuesto(tipoImpuestoSeleccionadoParaEdicion.getIdTipoImpuesto(), tipoImpuestoAGuardar);
                    mensajeExito = "Tipo de impuesto actualizado correctamente.";
                } else {
                    tipoImpuestoService.createTipoImpuesto(tipoImpuestoAGuardar);
                    mensajeExito = "Tipo de impuesto creado correctamente.";
                }
                Platform.runLater(() -> {
                    mostrarAlertaInformacion("Guardado exitoso", mensajeExito);
                    loadTiposImpuestos(); // Recargar tabla y ComboBox (indirectamente)
                    handleCancelarTipoImpuesto(null); // Ocultar y limpiar formulario
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlertaError("Error al guardar", "No se pudo guardar el tipo de impuesto: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleCancelarTipoImpuesto(ActionEvent event) {
        formTipoImpuestoContainer.setVisible(false);
        formTipoImpuestoContainer.setManaged(false);
        limpiarFormularioTipoImpuesto();
        modoEdicionTipo = false;
        tipoImpuestoSeleccionadoParaEdicion = null;
    }

    private void limpiarFormularioTipoImpuesto() {
        txtTipoNombre.clear();
        txtTipoDescripcion.clear();
        chkTipoEsPorcentual.setSelected(false);
        chkTipoActivo.setSelected(false);
    }

    private void popularFormularioTipoImpuesto(TipoImpuestoFX tipoImpuesto) {
        txtTipoNombre.setText(tipoImpuesto.getNombre());
        txtTipoDescripcion.setText(tipoImpuesto.getDescripcion());
        chkTipoEsPorcentual.setSelected(tipoImpuesto.isEsPorcentual());
        chkTipoActivo.setSelected(tipoImpuesto.isActivo());
    }
    //</editor-fold>

    //<editor-fold desc="Métodos de Alerta (Duplicados temporalmente, idealmente mover a una clase Util)">
    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarAlertaAdvertencia(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    private void mostrarAlertaInformacion(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    //</editor-fold>
} 