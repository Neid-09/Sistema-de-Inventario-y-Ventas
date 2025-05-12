package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar.impuestos;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TasaImpuestoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TipoImpuestoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ITasaImpuestoServiceFX;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class TasaImptsCtrlFX {

    //<editor-fold desc="FXML Fields">
    @FXML private TableView<TasaImpuestoFX> tableViewTasasImpuesto;
    @FXML private TableColumn<TasaImpuestoFX, String> colTasaTipoImpuesto;
    @FXML private TableColumn<TasaImpuestoFX, Double> colTasaValor;
    @FXML private TableColumn<TasaImpuestoFX, String> colTasaFechaInicio;
    @FXML private TableColumn<TasaImpuestoFX, String> colTasaFechaFin;
    @FXML private TableColumn<TasaImpuestoFX, String> colTasaDescripcion;
    @FXML private Button btnNuevaTasa;
    @FXML private Button btnEditarTasa;
    @FXML private Button btnEliminarTasa;
    @FXML private VBox formTasaImpuestoContainer;
    @FXML private ComboBox<TipoImpuestoFX> cmbTasaTipoImpuesto;
    @FXML private TextField txtTasaValor;
    @FXML private DatePicker txtTasaFechaInicio;
    @FXML private DatePicker txtTasaFechaFin;
    @FXML private TextArea txtTasaDescripcion;
    @FXML private Button btnGuardarTasa;
    @FXML private Button btnCancelarTasa;
    //</editor-fold>

    //<editor-fold desc="Servicios y Datos">
    private ITasaImpuestoServiceFX tasaImpuestoService;
    private final ObservableList<TasaImpuestoFX> tasasImpuestoData = FXCollections.observableArrayList();
    // Lista de tipos activos (inyectada por el controlador principal)
    private ObservableList<TipoImpuestoFX> tiposImpuestoActivosData = FXCollections.observableArrayList();
    //</editor-fold>

    //<editor-fold desc="Estado">
    private TasaImpuestoFX tasaImpuestoSeleccionadaParaEdicion;
    private boolean modoEdicionTasa = false;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // Formato YYYY-MM-DD
    //</editor-fold>

    // Inyección de servicio (llamado por el controlador principal)
    public void setServicio(ITasaImpuestoServiceFX tasaImpuestoService) {
        this.tasaImpuestoService = tasaImpuestoService;
        // Cargar datos una vez que el servicio está disponible
        loadTasasImpuestos();
    }

    // Inyección de la lista de tipos activos (llamado por el controlador principal)
    public void setTiposImpuestoActivos(ObservableList<TipoImpuestoFX> tiposActivos) {
        this.tiposImpuestoActivosData = tiposActivos;
        // Configurar el ComboBox ahora que tenemos los datos
        if (cmbTasaTipoImpuesto != null) {
            cmbTasaTipoImpuesto.setItems(this.tiposImpuestoActivosData);
        }
    }

    @FXML
    public void initialize() {
        System.out.println("TasaImpuestoTabController inicializado.");
        setupTasaImpuestoTable();
        configureComboBox();
        formTasaImpuestoContainer.setVisible(false);
        formTasaImpuestoContainer.setManaged(false);

        // Deshabilitar botón Eliminar (sin funcionalidad backend)
        if (btnEliminarTasa != null) {
            btnEliminarTasa.setDisable(true);
            btnEliminarTasa.setTooltip(new Tooltip("La eliminación directa de tasas no está implementada actualmente."));
        }
        // Los datos se cargan cuando se inyecta el servicio
        // Los tipos activos se inyectan después de la inicialización
    }

    private void configureComboBox() {
        if (cmbTasaTipoImpuesto != null) {
            // La asignación de items se hace en setTiposImpuestoActivos
            cmbTasaTipoImpuesto.setConverter(new javafx.util.StringConverter<TipoImpuestoFX>() {
                @Override
                public String toString(TipoImpuestoFX tipo) {
                    return tipo == null ? null : tipo.getNombre();
                }

                @Override
                public TipoImpuestoFX fromString(String string) {
                    return null; // No necesario para ComboBox no editable
                }
            });
        }
    }

    //<editor-fold desc="Lógica de la Pestaña">
    private void setupTasaImpuestoTable() {
        colTasaTipoImpuesto.setCellValueFactory(cellData -> {
            TipoImpuestoFX tipo = cellData.getValue().getTipoImpuesto();
            return new SimpleStringProperty(tipo != null ? tipo.getNombre() : "N/A");
        });
        colTasaValor.setCellValueFactory(new PropertyValueFactory<>("tasa"));
        colTasaFechaInicio.setCellValueFactory(new PropertyValueFactory<>("fechaInicio"));
        colTasaFechaFin.setCellValueFactory(new PropertyValueFactory<>("fechaFin"));
        colTasaDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        tableViewTasasImpuesto.setItems(tasasImpuestoData);
    }

    private void loadTasasImpuestos() {
        if (tasaImpuestoService == null) {
             System.err.println("Error: TasaImpuestoService no inyectado en TasaImpuestoTabController.");
             mostrarAlertaError("Error Interno", "El servicio de tasas de impuesto no está disponible.");
            return;
        }
        new Thread(() -> {
            try {
                List<TasaImpuestoFX> listaTasas = tasaImpuestoService.getAllTasasImpuestos();
                Platform.runLater(() -> {
                    tasasImpuestoData.setAll(listaTasas);
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlertaError("Error al cargar tasas", "No se pudieron cargar las tasas de impuesto: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleNuevaTasa(ActionEvent event) {
        if (tiposImpuestoActivosData == null || tiposImpuestoActivosData.isEmpty()) {
            mostrarAlertaAdvertencia("Sin Tipos de Impuesto", "Debe crear al menos un Tipo de Impuesto activo antes de añadir una tasa.");
            return;
        }
        modoEdicionTasa = false;
        tasaImpuestoSeleccionadaParaEdicion = null;
        limpiarFormularioTasaImpuesto();
        formTasaImpuestoContainer.setVisible(true);
        formTasaImpuestoContainer.setManaged(true);
        cmbTasaTipoImpuesto.requestFocus();
    }

    @FXML
    private void handleEditarTasa(ActionEvent event) {
        TasaImpuestoFX seleccionada = tableViewTasasImpuesto.getSelectionModel().getSelectedItem();
        if (seleccionada != null) {
            if ((tiposImpuestoActivosData == null || tiposImpuestoActivosData.isEmpty()) && seleccionada.getTipoImpuesto() != null) {
                mostrarAlertaAdvertencia("Sin Tipos de Impuesto Activos", "No hay Tipos de Impuesto activos disponibles para asignar o el tipo asociado está inactivo.");
                // Considerar permitir la edición si el tipo original existe pero está inactivo?
            }
            modoEdicionTasa = true;
            tasaImpuestoSeleccionadaParaEdicion = seleccionada;
            popularFormularioTasaImpuesto(seleccionada);
            formTasaImpuestoContainer.setVisible(true);
            formTasaImpuestoContainer.setManaged(true);
            cmbTasaTipoImpuesto.requestFocus();
        } else {
            mostrarAlertaAdvertencia("Sin selección", "Por favor, selecciona una tasa de impuesto para editar.");
        }
    }

    @FXML
    private void handleEliminarTasa(ActionEvent event) {
        mostrarAlertaInformacion("Funcionalidad no disponible", "La eliminación directa de tasas aún no está implementada.");
    }

    @FXML
    private void handleGuardarTasa(ActionEvent event) {
        TipoImpuestoFX tipoSeleccionado = cmbTasaTipoImpuesto.getValue();
        String valorStr = txtTasaValor.getText();
        LocalDate fechaInicioLD = txtTasaFechaInicio.getValue();
        LocalDate fechaFinLD = txtTasaFechaFin.getValue();
        String descripcion = txtTasaDescripcion.getText();

        if (tipoSeleccionado == null) {
            mostrarAlertaAdvertencia("Campo requerido", "Debe seleccionar un tipo de impuesto.");
            return;
        }
        if (valorStr == null || valorStr.trim().isEmpty()) {
            mostrarAlertaAdvertencia("Campo requerido", "El valor de la tasa no puede estar vacío.");
            return;
        }
        double valorTasa;
        try {
            valorTasa = Double.parseDouble(valorStr.trim());
            if (valorTasa < 0) {
                mostrarAlertaAdvertencia("Valor inválido", "La tasa no puede ser negativa.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlertaAdvertencia("Formato inválido", "El valor de la tasa debe ser un número.");
            return;
        }

        if (fechaInicioLD == null) {
            mostrarAlertaAdvertencia("Campo requerido", "La fecha de inicio es obligatoria.");
            txtTasaFechaInicio.requestFocus();
            return;
        }

        String fechaInicioStr = fechaInicioLD != null ? fechaInicioLD.format(dateFormatter) : null;
        String fechaFinStr = fechaFinLD != null ? fechaFinLD.format(dateFormatter) : null;

        TasaImpuestoFX tasaAGuardar = new TasaImpuestoFX();
        TipoImpuestoFX tipoParaGuardar = new TipoImpuestoFX();
        tipoParaGuardar.setIdTipoImpuesto(tipoSeleccionado.getIdTipoImpuesto());
        tasaAGuardar.setTipoImpuesto(tipoParaGuardar);
        tasaAGuardar.setTasa(valorTasa);
        tasaAGuardar.setFechaInicio(fechaInicioStr);
        tasaAGuardar.setFechaFin(fechaFinStr);
        tasaAGuardar.setDescripcion(descripcion != null ? descripcion.trim() : null);

        new Thread(() -> {
            try {
                String mensajeExito;
                if (modoEdicionTasa && tasaImpuestoSeleccionadaParaEdicion != null) {
                    tasaAGuardar.setIdTasa(tasaImpuestoSeleccionadaParaEdicion.getIdTasa());
                    tasaImpuestoService.updateTasaImpuesto(tasaImpuestoSeleccionadaParaEdicion.getIdTasa(), tasaAGuardar);
                    mensajeExito = "Tasa de impuesto actualizada correctamente.";
                } else {
                    tasaImpuestoService.createTasaImpuesto(tasaAGuardar);
                    mensajeExito = "Tasa de impuesto creada correctamente.";
                }
                Platform.runLater(() -> {
                    mostrarAlertaInformacion("Guardado exitoso", mensajeExito);
                    loadTasasImpuestos();
                    handleCancelarTasa(null);
                });
            } catch (Exception e) {
                Platform.runLater(() -> mostrarAlertaError("Error al guardar", "No se pudo guardar la tasa de impuesto: " + e.getMessage()));
                e.printStackTrace();
            }
        }).start();
    }

    @FXML
    private void handleCancelarTasa(ActionEvent event) {
        if (formTasaImpuestoContainer != null) {
            formTasaImpuestoContainer.setVisible(false);
            formTasaImpuestoContainer.setManaged(false);
        }
        limpiarFormularioTasaImpuesto();
        modoEdicionTasa = false;
        tasaImpuestoSeleccionadaParaEdicion = null;
    }

    private void limpiarFormularioTasaImpuesto() {
        if (cmbTasaTipoImpuesto != null) cmbTasaTipoImpuesto.getSelectionModel().clearSelection();
        if (txtTasaValor != null) txtTasaValor.clear();
        if (txtTasaFechaInicio != null) txtTasaFechaInicio.setValue(null);
        if (txtTasaFechaFin != null) txtTasaFechaFin.setValue(null);
        if (txtTasaDescripcion != null) txtTasaDescripcion.clear();
    }

    private void popularFormularioTasaImpuesto(TasaImpuestoFX tasa) {
        TipoImpuestoFX tipoSeleccionado = null;
        if (tasa.getTipoImpuesto() != null && tiposImpuestoActivosData != null) {
             tipoSeleccionado = tiposImpuestoActivosData.stream()
                .filter(tipo -> tipo.getIdTipoImpuesto() == tasa.getTipoImpuesto().getIdTipoImpuesto())
                .findFirst()
                .orElse(null);
        }


        if (cmbTasaTipoImpuesto != null) {
            cmbTasaTipoImpuesto.setValue(tipoSeleccionado);
            if (tipoSeleccionado == null && tasa.getTipoImpuesto() != null) {
                System.out.println("Advertencia: El tipo de impuesto asociado a esta tasa ("+ tasa.getTipoImpuesto().getIdTipoImpuesto() + ") está inactivo o no se encontró en la lista activa.");
                // Podrías añadir un item temporal o mostrar mensaje
            }
        }

        if (txtTasaValor != null) txtTasaValor.setText(String.valueOf(tasa.getTasa()));
        if (txtTasaDescripcion != null) txtTasaDescripcion.setText(tasa.getDescripcion());

        LocalDate fechaInicio = null;
        if (tasa.getFechaInicio() != null && !tasa.getFechaInicio().trim().isEmpty()) {
            try {
                fechaInicio = LocalDate.parse(tasa.getFechaInicio().trim(), dateFormatter);
            } catch (DateTimeParseException e) {
                System.err.println("Error al parsear fecha de inicio: " + tasa.getFechaInicio() + " - " + e.getMessage());
                // Opcional: mostrar alerta al usuario
            }
        }
        if (txtTasaFechaInicio != null) {
            txtTasaFechaInicio.setValue(fechaInicio);
        }

        LocalDate fechaFin = null;
        if (tasa.getFechaFin() != null && !tasa.getFechaFin().trim().isEmpty()) {
            try {
                fechaFin = LocalDate.parse(tasa.getFechaFin().trim(), dateFormatter);
            } catch (DateTimeParseException e) {
                System.err.println("Error al parsear fecha de fin: " + tasa.getFechaFin() + " - " + e.getMessage());
            }
        }
         if (txtTasaFechaFin != null) {
            txtTasaFechaFin.setValue(fechaFin);
        }
    }
    //</editor-fold>

    //<editor-fold desc="Métodos de Alerta (Duplicados temporalmente)">
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