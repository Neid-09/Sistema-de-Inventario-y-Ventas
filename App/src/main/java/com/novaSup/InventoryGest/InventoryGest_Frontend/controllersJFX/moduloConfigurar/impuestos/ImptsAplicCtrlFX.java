package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar.impuestos;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ImpuestoAplicableFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TasaImpuestoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICategoriaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IImpuestoAplicableServiceFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ITasaImpuestoServiceFX;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ImptsAplicCtrlFX implements Initializable {

    // Servicios Inyectados
    private IImpuestoAplicableServiceFX impuestoAplicableService;
    private ITasaImpuestoServiceFX tasaImpuestoService; // Servicio para tasas
    private IProductoService productoService;         // Servicio para productos
    private ICategoriaService categoriaService;       // Servicio para categorias

    // Componentes FXML Tabla y Botones Principales
    @FXML private TableView<ImpuestoAplicableFX> tableViewImpuestosAplicables;
    @FXML private TableColumn<ImpuestoAplicableFX, String> colApTasaImpuesto;
    @FXML private TableColumn<ImpuestoAplicableFX, String> colApAplicaA;
    @FXML private TableColumn<ImpuestoAplicableFX, String> colApNombreAplicable;
    @FXML private TableColumn<ImpuestoAplicableFX, String> colApFechaInicio;
    @FXML private TableColumn<ImpuestoAplicableFX, String> colApFechaFin;
    @FXML private TableColumn<ImpuestoAplicableFX, Boolean> colApAplica;
    @FXML private Button btnNuevoImpuestoAplicable;
    @FXML private Button btnEditarImpuestoAplicable;
    @FXML private Button btnEliminarImpuestoAplicable;

    // Componentes FXML Formulario
    @FXML private GridPane formGridPane;
    @FXML private ComboBox<TasaImpuestoFX> comboTasaImpuesto;
    @FXML private ToggleGroup toggleGroupAplicableA;
    @FXML private RadioButton radioProducto;
    @FXML private RadioButton radioCategoria;
    @FXML private ComboBox<ProductoFX> comboProducto;
    @FXML private ComboBox<CategoriaFX> comboCategoria;
    @FXML private DatePicker datePickerFechaInicio;
    @FXML private DatePicker datePickerFechaFin;
    @FXML private CheckBox checkBoxAplica;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    // Datos
    private ObservableList<ImpuestoAplicableFX> listaImpuestosAplicables;
    private ObservableList<TasaImpuestoFX> listaTasasImpuesto;
    private ObservableList<ProductoFX> listaProductos;
    private ObservableList<CategoriaFX> listaCategorias;

    private ImpuestoAplicableFX impuestoActual = null; // Para saber si estamos editando
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE; // YYYY-MM-DD

    /**
     * Método para inyectar las dependencias de los servicios.
     */
    public void setServicios(IImpuestoAplicableServiceFX impuestoService, ITasaImpuestoServiceFX tasaService,
                             IProductoService prodService, ICategoriaService catService) {
        this.impuestoAplicableService = impuestoService;
        this.tasaImpuestoService = tasaService;
        this.productoService = prodService;
        this.categoriaService = catService;

        // Cargar datos ahora que tenemos los servicios
        cargarDatosTabla();
        cargarCombos();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        listaImpuestosAplicables = FXCollections.observableArrayList();
        listaTasasImpuesto = FXCollections.observableArrayList();
        listaProductos = FXCollections.observableArrayList();
        listaCategorias = FXCollections.observableArrayList();

        // Crear e inicializar el ToggleGroup aquí
        toggleGroupAplicableA = new ToggleGroup();
        radioProducto.setToggleGroup(toggleGroupAplicableA);
        radioCategoria.setToggleGroup(toggleGroupAplicableA);

        configurarTabla();
        configurarFormulario();

        tableViewImpuestosAplicables.setItems(listaImpuestosAplicables);
        comboTasaImpuesto.setItems(listaTasasImpuesto);
        comboProducto.setItems(listaProductos);
        comboCategoria.setItems(listaCategorias);

        // Deshabilitar botones inicialmente
        btnEditarImpuestoAplicable.setDisable(true);
        btnEliminarImpuestoAplicable.setDisable(true);

        // Listener para habilitar/deshabilitar botones según selección tabla
        tableViewImpuestosAplicables.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            boolean seleccionado = newSelection != null;
            btnEditarImpuestoAplicable.setDisable(!seleccionado);
            btnEliminarImpuestoAplicable.setDisable(!seleccionado);
            // Ocultar formulario si se deselecciona
            if (!seleccionado && formGridPane.isVisible()) {
                 ocultarFormulario();
            }
        });
    }

    private void configurarTabla() {
        colApTasaImpuesto.setCellValueFactory(cellData -> {
            TasaImpuestoFX tasa = cellData.getValue().getTasaImpuesto();
            return new SimpleStringProperty(tasa != null ? tasa.toString() : "N/A");
        });
        colApAplicaA.setCellValueFactory(cellData -> {
            ImpuestoAplicableFX impuesto = cellData.getValue();
            if (impuesto.getProducto() != null) return new SimpleStringProperty("Producto");
            if (impuesto.getCategoria() != null) return new SimpleStringProperty("Categoría");
            return new SimpleStringProperty("N/A");
        });
        colApNombreAplicable.setCellValueFactory(cellData -> {
            ImpuestoAplicableFX impuesto = cellData.getValue();
            if (impuesto.getProducto() != null) return new SimpleStringProperty(impuesto.getProducto().getNombre());
            if (impuesto.getCategoria() != null) return impuesto.getCategoria().nombreProperty();
            return new SimpleStringProperty("N/A");
        });
        colApFechaInicio.setCellValueFactory(cellData -> cellData.getValue().fechaInicioProperty());
        colApFechaFin.setCellValueFactory(cellData -> cellData.getValue().fechaFinProperty());
        colApAplica.setCellValueFactory(cellData -> cellData.getValue().aplicaProperty());
        colApAplica.setCellFactory(column -> new TableCell<ImpuestoAplicableFX, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item ? "Sí" : "No"));
            }
        });
    }

    private void configurarFormulario() {
        // Visibilidad condicional de combos Producto/Categoría
        comboProducto.disableProperty().bind(radioProducto.selectedProperty().not());
        comboCategoria.disableProperty().bind(radioCategoria.selectedProperty().not());

        // Limpiar selección del otro combo cuando se selecciona un radio button
        radioProducto.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) comboCategoria.getSelectionModel().clearSelection();
        });
        radioCategoria.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) comboProducto.getSelectionModel().clearSelection();
        });
    }

    private void cargarDatosTabla() {
        if (impuestoAplicableService == null) return; // Servicio aún no inyectado
        try {
            listaImpuestosAplicables.setAll(impuestoAplicableService.listarImpuestosAplicables());
        } catch (Exception e) {
            mostrarAlertaError("Error al cargar impuestos aplicables", e);
        }
    }

    private void cargarCombos() {
        if (tasaImpuestoService == null || productoService == null || categoriaService == null) return;
        try {
            listaTasasImpuesto.setAll(tasaImpuestoService.getAllTasasImpuestos());
        } catch (Exception e) {
             mostrarAlertaError("Error al cargar tasas de impuesto", e);
        }
        try {
            // Cargar solo productos activos para selección?
            listaProductos.setAll(productoService.obtenerActivos());
        } catch (Exception e) {
             mostrarAlertaError("Error al cargar productos", e);
        }
         try {
             // Cargar solo categorías activas?
            listaCategorias.setAll(categoriaService.obtenerTodas().stream().filter(CategoriaFX::getEstado).collect(Collectors.toList()));
        } catch (Exception e) {
            mostrarAlertaError("Error al cargar categorías", e);
        }
    }

    private void mostrarFormulario() {
        formGridPane.setVisible(true);
        formGridPane.setManaged(true);
    }

    private void ocultarFormulario() {
        limpiarFormulario();
        formGridPane.setVisible(false);
        formGridPane.setManaged(false);
        tableViewImpuestosAplicables.getSelectionModel().clearSelection(); // Deseleccionar tabla
        impuestoActual = null; // Resetear impuesto actual
    }

    private void limpiarFormulario() {
        comboTasaImpuesto.getSelectionModel().clearSelection();
        // Limpiar la selección del ToggleGroup
        toggleGroupAplicableA.selectToggle(null);
        comboProducto.getSelectionModel().clearSelection();
        comboCategoria.getSelectionModel().clearSelection();
        datePickerFechaInicio.setValue(null);
        datePickerFechaFin.setValue(null);
        checkBoxAplica.setSelected(true); // Por defecto activo?
    }

    @FXML
    private void handleNuevoImpuestoAplicable(ActionEvent event) {
        limpiarFormulario();
        impuestoActual = null; // Asegurar que estamos creando uno nuevo
        datePickerFechaInicio.setValue(LocalDate.now()); // Fecha inicio por defecto hoy
        checkBoxAplica.setSelected(true);
        radioProducto.setSelected(true); // Por defecto aplicar a producto?
        mostrarFormulario();
    }

    @FXML
    private void handleEditarImpuestoAplicable(ActionEvent event) {
        impuestoActual = tableViewImpuestosAplicables.getSelectionModel().getSelectedItem();
        if (impuestoActual != null) {
            llenarFormulario(impuestoActual);
            mostrarFormulario();
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, seleccione un impuesto aplicable para editar.");
        }
    }

    private void llenarFormulario(ImpuestoAplicableFX impuesto) {
        comboTasaImpuesto.setValue(impuesto.getTasaImpuesto());
        if (impuesto.getProducto() != null) {
            radioProducto.setSelected(true);
            comboProducto.setValue(impuesto.getProducto());
            comboCategoria.getSelectionModel().clearSelection();
        } else if (impuesto.getCategoria() != null) {
            radioCategoria.setSelected(true);
            comboCategoria.setValue(impuesto.getCategoria());
            comboProducto.getSelectionModel().clearSelection();
        } else {
            // Asegurar que no hay selección si el impuesto no aplica a ninguno
            toggleGroupAplicableA.selectToggle(null);
            comboProducto.getSelectionModel().clearSelection();
            comboCategoria.getSelectionModel().clearSelection();
        }
        datePickerFechaInicio.setValue(parseLocalDate(impuesto.getFechaInicio()));
        datePickerFechaFin.setValue(parseLocalDate(impuesto.getFechaFin()));
        checkBoxAplica.setSelected(impuesto.isAplica());
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }

        try {
            TasaImpuestoFX tasaSeleccionada = comboTasaImpuesto.getValue();
            ProductoFX productoSeleccionado = radioProducto.isSelected() ? comboProducto.getValue() : null;
            CategoriaFX categoriaSeleccionada = radioCategoria.isSelected() ? comboCategoria.getValue() : null;
            LocalDate fechaInicio = datePickerFechaInicio.getValue();
            LocalDate fechaFin = datePickerFechaFin.getValue();
            boolean aplica = checkBoxAplica.isSelected();

            if (impuestoActual == null) { // Creando nuevo
                impuestoActual = new ImpuestoAplicableFX();
            }

            // Actualizar el objeto FX
            impuestoActual.setTasaImpuesto(tasaSeleccionada);
            impuestoActual.setProducto(productoSeleccionado);
            impuestoActual.setCategoria(categoriaSeleccionada);
            impuestoActual.setFechaInicio(formatLocalDate(fechaInicio));
            impuestoActual.setFechaFin(formatLocalDate(fechaFin)); // Puede ser null
            impuestoActual.setAplica(aplica);

            // Llamar al servicio
            if (impuestoActual.getIdImpuestoAplicable() == 0) { // Es nuevo
                impuestoAplicableService.crearImpuestoAplicable(impuestoActual);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Impuesto aplicable creado correctamente.");
            } else { // Actualizando
                impuestoAplicableService.actualizarImpuestoAplicable(impuestoActual.getIdImpuestoAplicable(), impuestoActual);
                // Si el checkbox 'aplica' fue modificado, llamar también a cambiarAplicabilidad?
                // O asumir que 'actualizar' ya maneja el estado 'aplica'. Depende del backend.
                // Si la lógica es separada:
                // if (impuestoActual.isAplica() != checkBoxAplica.isSelected()) {
                //     impuestoAplicableService.cambiarAplicabilidadImpuesto(impuestoActual.getIdImpuestoAplicable());
                // }
                 mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Impuesto aplicable actualizado correctamente.");
            }

            ocultarFormulario();
            cargarDatosTabla(); // Recargar tabla

        } catch (Exception e) {
            mostrarAlertaError("Error al guardar impuesto aplicable", e);
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        ocultarFormulario();
    }

    @FXML
    private void handleEliminarImpuestoAplicable(ActionEvent event) {
        ImpuestoAplicableFX seleccionado = tableViewImpuestosAplicables.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("Eliminar Impuesto Aplicable");
            confirmacion.setContentText("¿Está seguro de que desea eliminar esta configuración de impuesto?");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // TODO: Añadir y llamar al método de eliminación real en el servicio
                        // impuestoAplicableService.eliminarImpuestoAplicable(seleccionado.getIdImpuestoAplicable());
                        mostrarAlerta(Alert.AlertType.WARNING, "Función no implementada", "La eliminación aún no está conectada al servicio.");
                        // cargarDatosTabla(); // Recargar tabla si la eliminación funciona
                    } catch (Exception e) {
                        mostrarAlertaError("Error al eliminar impuesto aplicable", e);
                    }
                }
            });
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Por favor, seleccione un impuesto aplicable para eliminar.");
        }
    }

    // --- Métodos Auxiliares ---
    private boolean validarFormulario() {
        if (comboTasaImpuesto.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Debe seleccionar una Tasa de Impuesto.");
            return false;
        }
        if (!radioProducto.isSelected() && !radioCategoria.isSelected()) {
             mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Debe seleccionar si aplica a Producto o Categoría.");
            return false;
        }
        if (radioProducto.isSelected() && comboProducto.getValue() == null) {
             mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Debe seleccionar un Producto.");
            return false;
        }
        if (radioCategoria.isSelected() && comboCategoria.getValue() == null) {
             mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Debe seleccionar una Categoría.");
            return false;
        }
        if (datePickerFechaInicio.getValue() == null) {
             mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La Fecha de Inicio es obligatoria.");
            return false;
        }
        // Validación de fechas (fin no puede ser antes que inicio)
        if (datePickerFechaInicio.getValue() != null && datePickerFechaFin.getValue() != null &&
            datePickerFechaFin.getValue().isBefore(datePickerFechaInicio.getValue())) {
             mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La Fecha de Fin no puede ser anterior a la Fecha de Inicio.");
            return false;
        }
        return true;
    }

    private LocalDate parseLocalDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty() || dateString.equals("null")) {
            return null;
        }
        try {
            return LocalDate.parse(dateString, dateFormatter);
        } catch (Exception e) {
            System.err.println("Error parseando fecha: " + dateString + " - " + e.getMessage());
            return null;
        }
    }

    private String formatLocalDate(LocalDate date) {
        return date != null ? date.format(dateFormatter) : null;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Platform.runLater(() -> {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null);
            alerta.setContentText(mensaje);
            alerta.showAndWait();
        });
    }

    private void mostrarAlertaError(String cabecera, Exception ex) {
        // Imprimir el error en consola siempre, independientemente del hilo
        System.err.println(cabecera + ": " + ex.getMessage());
        ex.printStackTrace();

        Platform.runLater(() -> {
            Alert alerta = new Alert(Alert.AlertType.ERROR);
            alerta.setTitle("Error");
            alerta.setHeaderText(cabecera);
            alerta.setContentText("Ocurrió un error: " + ex.getMessage());
            // Considerar mostrar ex.getStackTrace() en un TextArea expandible
            alerta.showAndWait();
        });
    }
} 