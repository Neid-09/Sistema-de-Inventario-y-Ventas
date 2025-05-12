package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar.impuestos;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TipoImpuestoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ITipoImpuestoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.application.Platform;
// import com.novaSup.InventoryGest.InventoryGest_Frontend.models.TipoImpuesto; // Asumiendo que tienes un modelo
// import com.novaSup.InventoryGest.InventoryGest_Frontend.models.TasaImpuesto; // Asumiendo que tienes un modelo
// import com.novaSup.InventoryGest.InventoryGest_Frontend.models.ImpuestoAplicable; // Asumiendo que tienes un modelo

import java.util.List;
import java.util.Optional;

public class ConfigImptsCtrlFX {

    @FXML
    private TabPane tabPaneImpuestos;

    // Pestaña: Tipos de Impuesto
    @FXML
    private Tab tabTiposImpuesto;
    @FXML
    private TableView<TipoImpuestoFX> tableViewTiposImpuesto;
    @FXML
    private TableColumn<TipoImpuestoFX, String> colTipoNombre;
    @FXML
    private TableColumn<TipoImpuestoFX, String> colTipoDescripcion;
    @FXML
    private TableColumn<TipoImpuestoFX, Boolean> colTipoEsPorcentual;
    @FXML
    private TableColumn<TipoImpuestoFX, Boolean> colTipoActivo;
    @FXML
    private Button btnNuevoTipoImpuesto;
    @FXML
    private Button btnEditarTipoImpuesto;
    @FXML
    private Button btnEliminarTipoImpuesto;
    @FXML
    private VBox formTipoImpuestoContainer;
    @FXML
    private TextField txtTipoNombre;
    @FXML
    private TextArea txtTipoDescripcion;
    @FXML
    private CheckBox chkTipoEsPorcentual;
    @FXML
    private CheckBox chkTipoActivo;
    @FXML
    private Button btnGuardarTipoImpuesto;
    @FXML
    private Button btnCancelarTipoImpuesto;

    // Pestaña: Tasas de Impuesto
    @FXML
    private Tab tabTasasImpuesto;
    @FXML
    private TableView<Object> tableViewTasasImpuesto; // Cambiar Object al modelo real: TableView<TasaImpuesto>
    @FXML
    private TableColumn<Object, String> colTasaTipoImpuesto; // Idealmente mostrar el nombre del TipoImpuesto
    @FXML
    private TableColumn<Object, Number> colTasaValor; // O Double/BigDecimal
    @FXML
    private TableColumn<Object, String> colTasaFechaInicio; // O LocalDate
    @FXML
    private TableColumn<Object, String> colTasaFechaFin; // O LocalDate
    @FXML
    private TableColumn<Object, String> colTasaDescripcion;
    @FXML
    private Button btnNuevaTasa;
    @FXML
    private Button btnEditarTasa;
    @FXML
    private Button btnEliminarTasa;
    // Aquí irían los @FXML para el formulario de Tasas

    // Pestaña: Impuestos Aplicables
    @FXML
    private Tab tabImpuestosAplicables;
    @FXML
    private TableView<Object> tableViewImpuestosAplicables; // Cambiar Object al modelo real: TableView<ImpuestoAplicable>
    @FXML
    private TableColumn<Object, String> colApTasaImpuesto; // Idealmente mostrar info de la TasaImpuesto
    @FXML
    private TableColumn<Object, String> colApAplicaA; // "Producto" o "Categoría"
    @FXML
    private TableColumn<Object, String> colApNombreAplicable; // Nombre del producto o categoría
    @FXML
    private TableColumn<Object, String> colApFechaInicio;
    @FXML
    private TableColumn<Object, String> colApFechaFin;
    @FXML
    private TableColumn<Object, Boolean> colApAplica;
    @FXML
    private Button btnNuevoImpuestoAplicable;
    @FXML
    private Button btnEditarImpuestoAplicable;
    @FXML
    private Button btnEliminarImpuestoAplicable;
    // Aquí irían los @FXML para el formulario de Impuestos Aplicables

    private final ITipoImpuestoService tipoImpuestoService;
    private ObservableList<TipoImpuestoFX> tiposImpuestoData = FXCollections.observableArrayList();
    private TipoImpuestoFX tipoImpuestoSeleccionadoParaEdicion;
    private boolean modoEdicion = false;

    // Constructor para inyección de dependencias
    public ConfigImptsCtrlFX(ITipoImpuestoService tipoImpuestoService) {
        this.tipoImpuestoService = tipoImpuestoService;
    }

    @FXML
    public void initialize() {
        setupTipoImpuestoTable();
        loadTiposImpuestos();

        formTipoImpuestoContainer.setVisible(false);
        formTipoImpuestoContainer.setManaged(false);
    }

    private void setupTipoImpuestoTable() {
        colTipoNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colTipoDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colTipoEsPorcentual.setCellValueFactory(new PropertyValueFactory<>("esPorcentual"));
        colTipoActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        tableViewTiposImpuesto.setItems(tiposImpuestoData);
    }

    private void loadTiposImpuestos() {
        // Ejecutar la llamada de red en un hilo separado para no bloquear la UI
        new Thread(() -> {
            try {
                List<TipoImpuestoFX> listaTipos = tipoImpuestoService.getAllTiposImpuestos();
                // Una vez obtenidos los datos, actualizar la UI en el FX Application Thread
                Platform.runLater(() -> {
                    tiposImpuestoData.setAll(listaTipos);
                });
            } catch (Exception e) {
                // Mostrar la alerta también en el FX Application Thread
                Platform.runLater(() -> {
                    mostrarAlertaError("Error al cargar tipos de impuesto", "No se pudieron cargar los datos: " + e.getMessage());
                });
                e.printStackTrace(); // Esto está bien aquí, no afecta la UI
            }
        }).start();
    }

    @FXML
    private void handleNuevoTipoImpuesto(ActionEvent event) {
        modoEdicion = false;
        tipoImpuestoSeleccionadoParaEdicion = null;
        limpiarFormularioTipoImpuesto();
        chkTipoActivo.setSelected(true); // Por defecto activo al crear uno nuevo
        chkTipoEsPorcentual.setSelected(true); // Por defecto porcentual
        formTipoImpuestoContainer.setVisible(true);
        formTipoImpuestoContainer.setManaged(true);
        txtTipoNombre.requestFocus();
    }

    @FXML
    private void handleEditarTipoImpuesto(ActionEvent event) {
        TipoImpuestoFX seleccionado = tableViewTiposImpuesto.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            modoEdicion = true;
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
                try {
                    tipoImpuestoService.changeTipoImpuestoEstado(seleccionado.getIdTipoImpuesto());
                    mostrarAlertaInformacion("Estado cambiado", "El estado del tipo de impuesto ha sido modificado.");
                    loadTiposImpuestos(); // Recargar para ver el cambio
                } catch (Exception e) {
                    mostrarAlertaError("Error al cambiar estado", "No se pudo cambiar el estado: " + e.getMessage());
                    e.printStackTrace();
                }
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
        boolean activo = chkTipoActivo.isSelected(); // El estado "activo" se maneja con el checkbox

        if (nombre == null || nombre.trim().isEmpty()) {
            mostrarAlertaAdvertencia("Campo requerido", "El nombre del tipo de impuesto no puede estar vacío.");
            return;
        }

        TipoImpuestoFX tipoImpuestoAGuardar = new TipoImpuestoFX();
        tipoImpuestoAGuardar.setNombre(nombre.trim());
        tipoImpuestoAGuardar.setDescripcion(descripcion != null ? descripcion.trim() : null);
        tipoImpuestoAGuardar.setEsPorcentual(esPorcentual);
        tipoImpuestoAGuardar.setActivo(activo);

        try {
            if (modoEdicion && tipoImpuestoSeleccionadoParaEdicion != null) {
                tipoImpuestoAGuardar.setIdTipoImpuesto(tipoImpuestoSeleccionadoParaEdicion.getIdTipoImpuesto());
                tipoImpuestoService.updateTipoImpuesto(tipoImpuestoSeleccionadoParaEdicion.getIdTipoImpuesto(), tipoImpuestoAGuardar);
                mostrarAlertaInformacion("Actualización exitosa", "Tipo de impuesto actualizado correctamente.");
            } else {
                tipoImpuestoService.createTipoImpuesto(tipoImpuestoAGuardar);
                mostrarAlertaInformacion("Creación exitosa", "Tipo de impuesto creado correctamente.");
            }
            loadTiposImpuestos();
            handleCancelarTipoImpuesto(null); // Ocultar y limpiar formulario
        } catch (Exception e) {
            mostrarAlertaError("Error al guardar", "No se pudo guardar el tipo de impuesto: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelarTipoImpuesto(ActionEvent event) {
        formTipoImpuestoContainer.setVisible(false);
        formTipoImpuestoContainer.setManaged(false);
        limpiarFormularioTipoImpuesto();
        modoEdicion = false;
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

    // --- Métodos auxiliares (ej: setupTables, limpiarFormularios, cargarDatos, etc.) ---
} 