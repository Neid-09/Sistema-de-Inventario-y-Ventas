package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloInventario;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.LoteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;
import java.util.ResourceBundle.Control;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

@Component
public class LoteControllerFX implements Initializable {

    @Autowired
    private ILoteService loteService;

    @Autowired
    private IProductoService productoService;

    // Variables de datos
    private ObservableList<LoteFX> listaLotes = FXCollections.observableArrayList();
    private ObservableList<ProductoFX> listaProductos = FXCollections.observableArrayList();
    private LoteFX loteSeleccionado;
    private boolean modoEdicion = false;

    // Elementos de la tabla
    @FXML
    private TableView<LoteFX> lotesTableView;

    @FXML
    private TableColumn<LoteFX, Integer> idLoteColumn;

    @FXML
    private TableColumn<LoteFX, String> productoColumn;

    @FXML
    private TableColumn<LoteFX, String> numeroLoteColumn;

    @FXML
    private TableColumn<LoteFX, LocalDate> fechaEntradaColumn;

    @FXML
    private TableColumn<LoteFX, LocalDate> fechaVencimientoColumn;

    @FXML
    private TableColumn<LoteFX, Integer> diasRestantesColumn;

    @FXML
    private TableColumn<LoteFX, Integer> cantidadColumn;

    @FXML
    private TableColumn<LoteFX, Boolean> estadoColumn;

    @FXML
    private TableColumn<LoteFX, String> accionesColumn;

    // Filtros y controles superiores
    @FXML
    private ComboBox<ProductoFX> productoComboBox;

    @FXML
    private ComboBox<String> estadoComboBox;

    @FXML
    private TextField diasAlertaField;

    @FXML
    private ComboBox<String> tipoFechaComboBox;

    @FXML
    private DatePicker fechaInicioDatePicker;

    @FXML
    private DatePicker fechaFinDatePicker;

    @FXML
    private Button filtrarButton;

    @FXML
    private Button limpiarFiltrosButton;

    @FXML
    private Button nuevoLoteButton;

    // Estadísticas
    @FXML
    private Label totalLotesLabel;

    @FXML
    private Label proximosVencerLabel;

    @FXML
    private Label vencidosLabel;

    // Panel de detalle
    @FXML
    private VBox detallePane;

    @FXML
    private Label detalleIdLabel;

    @FXML
    private TextField txtDetalleNumeroLote;

    @FXML
    private ComboBox<ProductoFX> cmbDetalleProducto;

    @FXML
    private TextField txtDetalleCantidad;

    @FXML
    private CheckBox chkDetalleEstado;

    @FXML
    private DatePicker dpFechaEntrada;

    @FXML
    private DatePicker dpDetalleFechaVencimiento;

    @FXML
    private Label detalleDiasRestantesLabel;

    @FXML
    private Label detalleIdEntradaLabel;

    @FXML
    private Label detalleFechaRegistroLabel;

    // Botones de acción
    @FXML
    private Button btnCambiarEstado;

    @FXML
    private Button btnCancelarEdicion;

    @FXML
    private Button btnEditarLote;

    @FXML
    private Button btnGuardarCambios;

    @FXML
    private Button verEntradaButton;

    // Barra de estado
    @FXML
    private Label statusLabel;

    @FXML
    private ProgressIndicator progressIndicator;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        configurarComboBoxes();
        configurarListeners();
        cargarDatos();
        configurarFormularioDetalle(false);
    }

    private void configurarTabla() {
        // Configurar columnas
        idLoteColumn.setCellValueFactory(new PropertyValueFactory<>("idLote"));

        productoColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getProducto() != null) {
                return new SimpleStringProperty(cellData.getValue().getProducto().getNombre());
            } else {
                return new SimpleStringProperty("N/A");
            }
        });

        numeroLoteColumn.setCellValueFactory(new PropertyValueFactory<>("numeroLote"));
        fechaEntradaColumn.setCellValueFactory(new PropertyValueFactory<>("fechaEntrada"));
        fechaVencimientoColumn.setCellValueFactory(new PropertyValueFactory<>("fechaVencimiento"));

        diasRestantesColumn.setCellValueFactory(cellData -> {
            LoteFX lote = cellData.getValue();
            if (lote.getFechaVencimiento() != null) {
                long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), lote.getFechaVencimiento());
                return new javafx.beans.property.SimpleIntegerProperty((int)diasRestantes).asObject();
            } else {
                return new javafx.beans.property.SimpleIntegerProperty(0).asObject();
            }
        });

        cantidadColumn.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        estadoColumn.setCellValueFactory(new PropertyValueFactory<>("activo"));
        estadoColumn.setCellFactory(col -> new TableCell<LoteFX, Boolean>() {
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item ? "Activo" : "Inactivo");
                }
            }
        });
        // Configurar columna de acciones con botones
        accionesColumn.setCellFactory(col -> new TableCell<LoteFX, String>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");

            {
                btnEditar.setOnAction(event -> {
                    LoteFX lote = getTableView().getItems().get(getIndex());
                    cargarLoteParaEditar(lote);
                });

                btnEliminar.setOnAction(event -> {
                    LoteFX lote = getTableView().getItems().get(getIndex());
                    confirmarEliminarLote(lote);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEditar);
                }
            }
        });

        // Vincular tabla con lista de datos
        lotesTableView.setItems(listaLotes);

        // Manejar selección en la tabla
        lotesTableView.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                mostrarDetalleLote(newSelection);
            }
        });
    }

    private void configurarComboBoxes() {
        // Configurar ComboBox de productos
        productoComboBox.setConverter(new StringConverter<ProductoFX>() {
            @Override
            public String toString(ProductoFX producto) {
                return producto != null ? producto.getNombre() : "Todos los productos";
            }

            @Override
            public ProductoFX fromString(String string) {
                return null; // No es necesario para este caso
            }
        });

        // Limpiar opciones existentes antes de agregar nuevas
        estadoComboBox.getItems().clear();
        estadoComboBox.getItems().addAll("Todos", "Activos", "Inactivos", "Próximos a vencer");
        estadoComboBox.setValue("Activos");

        // Limpiar y configurar ComboBox de tipo de fecha
        tipoFechaComboBox.getItems().clear();
        tipoFechaComboBox.getItems().addAll("Fecha entrada", "Fecha vencimiento");
        tipoFechaComboBox.setValue("Fecha entrada");

        // Configurar autocompletado para ambos ComboBox
        configurarAutocompletadoComboBox();
    }



    private void configurarAutocompletadoComboBox() {
        // Configurar el convertidor
        StringConverter<ProductoFX> converter = new StringConverter<>() {
            @Override
            public String toString(ProductoFX producto) {
                if (producto == null) return "";
                return "[" + producto.getCodigo() + "] " + producto.getNombre();
            }

            @Override
            public ProductoFX fromString(String string) {
                for (ProductoFX producto : listaProductos) {
                    String texto = toString(producto);
                    if (texto.equalsIgnoreCase(string)) {
                        return producto;
                    }
                }
                return null;
            }
        };

        // Aplicar configuración a ambos ComboBox
        configurarComboBoxProducto(productoComboBox, converter);
        configurarComboBoxProducto(cmbDetalleProducto, converter);
    }

    private void configurarComboBoxProducto(ComboBox<ProductoFX> comboBox, StringConverter<ProductoFX> converter) {
        // Asignar convertidor al ComboBox
        comboBox.setConverter(converter);
        comboBox.setEditable(true);

        // Crear una lista filtrada propia para cada ComboBox
        FilteredList<ProductoFX> productosFiltrados = new FilteredList<>(listaProductos, p -> true);
        comboBox.setItems(productosFiltrados);

        // Bandera para evitar ciclos infinitos entre listeners
        final AtomicBoolean updatingFromValueChange = new AtomicBoolean(false);
        final AtomicBoolean updatingFromTextChange = new AtomicBoolean(false);

        // Agregar manejador para teclas especiales
        comboBox.getEditor().setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER || event.getCode() == KeyCode.SPACE) {
                // Evitar que se borre el texto
                event.consume();

                String texto = comboBox.getEditor().getText().trim();
                if (!texto.isEmpty()) {
                    // Buscar primera coincidencia
                    for (ProductoFX producto : productosFiltrados) {
                        if (converter.toString(producto).toLowerCase().contains(texto.toLowerCase())) {
                            updatingFromTextChange.set(true);
                            comboBox.setValue(producto);
                            updatingFromTextChange.set(false);
                            break;
                        }
                    }
                }
            }
        });

        // Filtro automático al escribir
        comboBox.getEditor().textProperty().addListener((obs, oldValue, newValue) -> {
            // Evitar procesamiento si el cambio viene del listener de valor
            if (updatingFromValueChange.get()) return;
            
            String texto = newValue.toLowerCase();

            productosFiltrados.setPredicate(producto -> {
                if (texto == null || texto.isEmpty()) return true;

                String productoTexto = converter.toString(producto).toLowerCase();
                return productoTexto.contains(texto);
            });
        });

        // Manejar selección: sincronizar editor con el objeto
        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            // Evitar procesamiento si el cambio viene del listener de texto
            if (updatingFromTextChange.get()) return;
            
            if (newVal != null) {
                updatingFromValueChange.set(true);
                comboBox.getEditor().setText(converter.toString(newVal));
                updatingFromValueChange.set(false);
            }
        });
    }




    private void configurarListeners() {
        // Configurar botones y otros controles
        btnGuardarCambios.setDisable(true);

        // Verificar cambios en formulario
        txtDetalleNumeroLote.textProperty().addListener((obs, oldValue, newValue) ->
                btnGuardarCambios.setDisable(false));

        txtDetalleCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null && !newValue.matches("\\d*")) {
                txtDetalleCantidad.setText(newValue.replaceAll("[^\\d]", ""));
            }
            btnGuardarCambios.setDisable(false);
        });

        dpFechaEntrada.valueProperty().addListener((obs, oldValue, newValue) ->
                btnGuardarCambios.setDisable(false));

        dpDetalleFechaVencimiento.valueProperty().addListener((obs, oldValue, newValue) -> {
            actualizarDiasRestantes();
            btnGuardarCambios.setDisable(false);
        });

        chkDetalleEstado.selectedProperty().addListener((obs, oldValue, newValue) ->
                btnGuardarCambios.setDisable(false));
    }

    private void cargarDatos() {
        try {
            statusLabel.setText("Cargando datos...");
            progressIndicator.setVisible(true);

            // Cargar TODOS los productos en lugar de solo los activos
            List<ProductoFX> productos = productoService.obtenerTodos();
            listaProductos.setAll(productos);

            // Cargar lotes según el estado seleccionado
            cargarLotesSegunFiltro();

            // Actualizar estadísticas
            actualizarEstadisticas();

            statusLabel.setText("Datos cargados correctamente");
        } catch (Exception e) {
            mostrarAlerta("Error al cargar datos: " + e.getMessage(), Alert.AlertType.ERROR);
            statusLabel.setText("Error al cargar datos");
        } finally {
            progressIndicator.setVisible(false);
        }
    }

    private void cargarLotesSegunFiltro() throws Exception {
        // Obtener el estado seleccionado
        String estadoSeleccionado = estadoComboBox.getValue();
        List<LoteFX> lotes;

        switch (estadoSeleccionado) {
            case "Todos":
                lotes = loteService.obtenerTodos();
                break;
            case "Activos":
                lotes = loteService.obtenerActivos();
                break;
            case "Inactivos":
                lotes = loteService.obtenerInactivos();
                break;
            case "Próximos a vencer":
                int diasAlerta = 30;
                try {
                    diasAlerta = Integer.parseInt(diasAlertaField.getText());
                } catch (NumberFormatException e) {
                    diasAlerta = 30;
                }
                lotes = loteService.obtenerProximosAVencer(diasAlerta);
                break;
            default:
                lotes = loteService.obtenerActivos();
        }

        // Aplicar filtro de producto si está seleccionado
        ProductoFX productoSeleccionado = productoComboBox.getValue();
        if (productoSeleccionado != null) {
            lotes = lotes.stream()
                    .filter(lote -> lote.getIdProducto() != null &&
                            lote.getIdProducto().equals(productoSeleccionado.getIdProducto()))
                    .collect(Collectors.toList());
        }

        listaLotes.setAll(lotes);
    }

    private void cargarLotesActivos() {
        try {
            listaLotes.clear();
            listaLotes.addAll(loteService.obtenerActivos());
            actualizarEstadisticas(); // Este método ya actualiza totalLotesLabel
        } catch (Exception e) {
            mostrarError("Error al cargar lotes", e);
        }
    }

    private void actualizarEstadisticas() {
        int proximosVencer = 0;
        int vencidos = 0;
        LocalDate hoy = LocalDate.now();

        // Obtener valor de días alerta
        int diasAlerta = 30;
        try {
            diasAlerta = Integer.parseInt(diasAlertaField.getText());
        } catch (NumberFormatException e) {
            // Usar valor por defecto
        }

        for (LoteFX lote : listaLotes) {
            if (lote.getFechaVencimiento() != null) {
                long diasRestantes = ChronoUnit.DAYS.between(hoy, lote.getFechaVencimiento());
                if (diasRestantes < 0) {
                    vencidos++;
                } else if (diasRestantes <= diasAlerta) {
                    proximosVencer++;
                }
            }
        }

        // Actualizar los tres indicadores estadísticos
        totalLotesLabel.setText(String.valueOf(listaLotes.size()));
        proximosVencerLabel.setText(String.valueOf(proximosVencer));
        vencidosLabel.setText(String.valueOf(vencidos));
    }

    private void mostrarDetalleLote(LoteFX lote) {
        if (lote != null) {

            // Asignar el lote seleccionado - LÍNEA FALTANTE
            this.loteSeleccionado = lote;
            detalleIdLabel.setText(lote.getIdLote().toString());
            txtDetalleNumeroLote.setText(lote.getNumeroLote());

            // Usar cmbDetalleProducto en lugar de txtDetalleProducto
            if (lote.getProducto() != null) {
                cmbDetalleProducto.setValue(lote.getProducto());
            } else if (lote.getIdProducto() > 0) {
                ProductoFX producto = listaProductos.stream()
                        .filter(p -> p.getIdProducto().equals(lote.getIdProducto()))
                        .findFirst().orElse(null);
                cmbDetalleProducto.setValue(producto);
            } else {
                cmbDetalleProducto.setValue(null);
            }

            txtDetalleCantidad.setText(lote.getCantidad().toString());
            chkDetalleEstado.setSelected(lote.getActivo());
            dpFechaEntrada.setValue(lote.getFechaEntrada());
            dpDetalleFechaVencimiento.setValue(lote.getFechaVencimiento());

            // Actualizar días restantes
            actualizarDiasRestantes(lote.getFechaVencimiento());

            // Información adicional
            detalleIdEntradaLabel.setText(lote.getIdEntrada() > 0 ?
                    lote.getIdEntrada().toString() : "No asignado");

            // Configurar botones según el modo y estado
            configurarFormularioDetalle(false);

            // Actualizar texto del botón cambiar estado según el estado actual del lote
            btnCambiarEstado.setText(lote.getActivo() ? "Desactivar" : "Activar");
            btnCambiarEstado.getStyleClass().removeAll("button-warning", "button-success");
            btnCambiarEstado.getStyleClass().add(lote.getActivo() ? "button-warning" : "button-success");
        }
    }

    // Método original con parámetro
    private void actualizarDiasRestantes(LocalDate fechaVencimiento) {
        if (fechaVencimiento != null) {
            long diasRestantes = ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
            detalleDiasRestantesLabel.setText(String.valueOf(diasRestantes));
        } else {
            detalleDiasRestantesLabel.setText("N/A");
        }
    }

    // Método sobrecargado sin parámetros
    private void actualizarDiasRestantes() {
        LocalDate fechaVencimiento = dpDetalleFechaVencimiento.getValue();
        actualizarDiasRestantes(fechaVencimiento);
    }


    // Método para configurar el formulario
    private void configurarFormularioDetalle(boolean editable) {
        // Habilitar/deshabilitar controles
        txtDetalleNumeroLote.setEditable(editable);
        cmbDetalleProducto.setDisable(!editable);
        txtDetalleCantidad.setEditable(editable);
        chkDetalleEstado.setDisable(!editable);
        dpFechaEntrada.setDisable(!editable);
        dpDetalleFechaVencimiento.setDisable(!editable);

        // Configurar visibilidad de botones según el modo
        btnGuardarCambios.setVisible(editable);
        btnCancelarEdicion.setVisible(editable);
        //btnEditarLote.setVisible(!editable);
        // Verificar que esta línea esté correctamente implementada
        btnCambiarEstado.setVisible(!editable && loteSeleccionado != null);
        verEntradaButton.setVisible(!editable && loteSeleccionado != null && loteSeleccionado.getIdEntrada() > 0);

        modoEdicion = editable;
    }

    @FXML
    void editarLote(ActionEvent event) {
        if (loteSeleccionado != null) {
            configurarFormularioDetalle(true);
            statusLabel.setText("Editando lote: " + loteSeleccionado.getNumeroLote());
        }
    }

    @FXML
    void filtrarLotes(ActionEvent event) {
        try {
            statusLabel.setText("Aplicando filtros...");
            progressIndicator.setVisible(true);

            cargarLotesSegunFiltro();

            // Aplicar filtro adicional por fechas si corresponde
            if (fechaInicioDatePicker.getValue() != null && fechaFinDatePicker.getValue() != null) {
                filtrarPorFechas();
            }

            actualizarEstadisticas();
            statusLabel.setText("Filtros aplicados correctamente");
        } catch (Exception e) {
            mostrarError("Error al aplicar filtros", e);
        } finally {
            progressIndicator.setVisible(false);
        }
    }

    private void filtrarPorFechas() {
        if (fechaInicioDatePicker.getValue() != null && fechaFinDatePicker.getValue() != null) {
            try {
                Date fechaInicio = Date.from(fechaInicioDatePicker.getValue()
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());
                Date fechaFin = Date.from(fechaFinDatePicker.getValue()
                        .atStartOfDay(ZoneId.systemDefault()).toInstant());

                if ("Fecha entrada".equals(tipoFechaComboBox.getValue())) {
                    listaLotes.setAll(loteService.obtenerPorRangoFechaEntrada(fechaInicio, fechaFin));
                }
                // Implementar filtro por fecha vencimiento si es necesario
            } catch (Exception e) {
                mostrarError("Error al filtrar por fechas", e);
            }
        }
    }


    // Método para guardar cambios
    @FXML
    void guardarCambios(ActionEvent event) {
        try {
            if (loteSeleccionado == null) {
                mostrarAlerta("No hay lote seleccionado para guardar", Alert.AlertType.WARNING);
                return;
            }

            // Validar campos
            if (txtDetalleNumeroLote.getText().trim().isEmpty()) {
                mostrarAlerta("El número de lote es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            // Obtener producto seleccionado con validación mejorada
            ProductoFX productoSeleccionado = cmbDetalleProducto.getValue();
            if (productoSeleccionado == null) {
                // Intentar buscar el producto por el texto ingresado
                String textoProducto = cmbDetalleProducto.getEditor().getText().trim();
                if (!textoProducto.isEmpty()) {
                    // Buscar en la lista de productos
                    for (ProductoFX producto : listaProductos) {
                        String productoStr = "[" + producto.getCodigo() + "] " + producto.getNombre();
                        if (productoStr.equalsIgnoreCase(textoProducto)) {
                            productoSeleccionado = producto;
                            break;
                        }
                    }
                }

                if (productoSeleccionado == null) {
                    mostrarAlerta("Debe seleccionar un producto válido", Alert.AlertType.WARNING);
                    return;
                }
            }

            if (txtDetalleCantidad.getText().trim().isEmpty() ||
                    Integer.parseInt(txtDetalleCantidad.getText().trim()) <= 0) {
                mostrarAlerta("La cantidad debe ser un número positivo", Alert.AlertType.WARNING);
                return;
            }

            if (dpFechaEntrada.getValue() == null) {
                mostrarAlerta("La fecha de entrada es obligatoria", Alert.AlertType.WARNING);
                return;
            }

            // Guardar cantidad original para verificar cambios (para depuración)
            int cantidadOriginal = loteSeleccionado.getCantidad();
            int nuevaCantidad = Integer.parseInt(txtDetalleCantidad.getText().trim());
            Integer idProductoOriginal = loteSeleccionado.getIdProducto();

            // Actualizar objeto lote con los datos del formulario
            loteSeleccionado.setNumeroLote(txtDetalleNumeroLote.getText().trim());
            loteSeleccionado.setProducto(productoSeleccionado);
            loteSeleccionado.setIdProducto(productoSeleccionado.getIdProducto());
            loteSeleccionado.setCantidad(nuevaCantidad);
            loteSeleccionado.setActivo(chkDetalleEstado.isSelected());
            loteSeleccionado.setFechaEntrada(dpFechaEntrada.getValue());
            loteSeleccionado.setFechaVencimiento(dpDetalleFechaVencimiento.getValue());

            // Guardar cambios en la base de datos
            progressIndicator.setVisible(true);
            statusLabel.setText("Guardando cambios...");

            LoteFX loteActualizado = loteService.actualizar(loteSeleccionado);

            // Actualizar la lista y la tabla
            int index = listaLotes.indexOf(loteSeleccionado);
            if (index >= 0) {
                listaLotes.set(index, loteActualizado);
            } else {
                listaLotes.add(loteActualizado);
            }

            // Actualizar selección y UI
            lotesTableView.getSelectionModel().select(loteActualizado);

            // Mostrar información sobre el cambio de stock (opcional, para verificar)
            if (cantidadOriginal != nuevaCantidad || !idProductoOriginal.equals(productoSeleccionado.getIdProducto())) {
                statusLabel.setText("Lote actualizado y stock ajustado correctamente");
            } else {
                statusLabel.setText("Lote actualizado correctamente");
            }

            // Deshabilitar edición
            configurarFormularioDetalle(false);
            progressIndicator.setVisible(false);

        } catch (NumberFormatException e) {
            progressIndicator.setVisible(false);
            mostrarAlerta("Error en formato numérico: asegúrese de ingresar números válidos", Alert.AlertType.ERROR);
        } catch (Exception e) {
            progressIndicator.setVisible(false);
            mostrarAlerta("Error al guardar cambios: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    void limpiarFiltros(ActionEvent event) {
        productoComboBox.setValue(null);
        estadoComboBox.setValue("Activos");
        fechaInicioDatePicker.setValue(null);
        fechaFinDatePicker.setValue(null);
        diasAlertaField.setText("30");

        try {
            cargarLotesActivos();
            statusLabel.setText("Filtros limpiados. Mostrando lotes activos.");
        } catch (Exception e) {
            mostrarError("Error al limpiar filtros", e);
        }
    }


    @FXML
    void mostrarFormularioNuevoLote(ActionEvent event) {
        try {
            limpiarFormularioDetalle();
            configurarFormularioDetalle(true);

            // Establecer fecha de entrada predeterminada
            dpFechaEntrada.setValue(LocalDate.now());

            // Configurar botón para crear nuevo lote
            btnGuardarCambios.setText("Crear Lote");
            btnGuardarCambios.setOnAction(e -> crearNuevoLote());

            loteSeleccionado = null;
            statusLabel.setText("Creando nuevo lote");
        } catch (Exception e) {
            mostrarAlerta("Error al preparar el formulario: " + e.getMessage(),
                    Alert.AlertType.ERROR);
        }
    }

    @FXML
    void cambiarEstadoLote(ActionEvent event) {
        if (loteSeleccionado == null) {
            mostrarAlerta("No hay lote seleccionado", Alert.AlertType.WARNING);
            return;
        }

        boolean nuevoEstado = !loteSeleccionado.getActivo();
        String accion = nuevoEstado ? "activar" : "desactivar";

        // Si estamos intentando activar un lote, verificar primero si está vencido
        if (nuevoEstado && loteSeleccionado.getFechaVencimiento() != null) {
            LocalDate fechaActual = LocalDate.now();

            // Verificar si el lote está vencido antes de enviar la solicitud
            if (loteSeleccionado.getFechaVencimiento().isBefore(fechaActual)) {
                mostrarAlerta("No se puede activar un lote vencido. La fecha de vencimiento es " +
                        loteSeleccionado.getFechaVencimiento(), Alert.AlertType.ERROR);
                return;
            }
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar operación");
        confirmacion.setHeaderText("¿Está seguro que desea " + accion + " este lote?");
        confirmacion.setContentText("Lote: " + loteSeleccionado.getNumeroLote());

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    progressIndicator.setVisible(true);
                    LoteFX loteActualizado;

                    if (nuevoEstado) {
                        loteActualizado = loteService.activar(loteSeleccionado.getIdLote());
                    } else {
                        loteActualizado = loteService.desactivar(loteSeleccionado.getIdLote());
                    }

                    // Actualizar en la lista
                    int index = listaLotes.indexOf(loteSeleccionado);
                    if (index >= 0) {
                        listaLotes.set(index, loteActualizado);
                    }

                    loteSeleccionado = loteActualizado;
                    mostrarDetalleLote(loteActualizado);
                    actualizarEstadisticas();

                    statusLabel.setText("Lote " + accion + "do correctamente");
                    progressIndicator.setVisible(false);
                } catch (Exception e) {
                    progressIndicator.setVisible(false);

                    // Verificar si es un error específico de lote vencido
                    if (e.getMessage().contains("vencido")) {
                        mostrarAlerta("No se puede activar un lote vencido", Alert.AlertType.ERROR);
                    } else {
                        mostrarAlerta("Error al " + accion + " el lote: " + e.getMessage(),
                                Alert.AlertType.ERROR);
                    }
                }
            }
        });
    }

    @FXML
    void cancelarEdicion(ActionEvent event) {
        if (loteSeleccionado != null) {
            // Volver a cargar los datos del lote seleccionado
            mostrarDetalleLote(loteSeleccionado);
        } else {
            // Si estábamos creando un lote nuevo, limpiar el formulario
            limpiarFormularioDetalle();
        }

        // Volver al modo visualización
        configurarFormularioDetalle(false);
        statusLabel.setText("Operación cancelada");
    }

    private void limpiarFormularioDetalle() {
        detalleIdLabel.setText("");
        txtDetalleNumeroLote.clear();
        cmbDetalleProducto.setValue(null);
        txtDetalleCantidad.clear();
        chkDetalleEstado.setSelected(true);
        dpFechaEntrada.setValue(null);
        dpDetalleFechaVencimiento.setValue(null);
        detalleDiasRestantesLabel.setText("");
        detalleIdEntradaLabel.setText("No asignado");
    }

    private void crearNuevoLote() {
        try {
            // Validar campos
            if (txtDetalleNumeroLote.getText().trim().isEmpty()) {
                mostrarAlerta("El número de lote es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            if (cmbDetalleProducto.getValue() == null) {
                mostrarAlerta("Debe seleccionar un producto", Alert.AlertType.WARNING);
                return;
            }

            if (txtDetalleCantidad.getText().trim().isEmpty() ||
                    Integer.parseInt(txtDetalleCantidad.getText().trim()) <= 0) {
                mostrarAlerta("La cantidad debe ser un número positivo", Alert.AlertType.WARNING);
                return;
            }

            if (dpFechaEntrada.getValue() == null) {
                mostrarAlerta("La fecha de entrada es obligatoria", Alert.AlertType.WARNING);
                return;
            }

            // Crear objeto lote con los datos del formulario
            LoteFX nuevoLote = new LoteFX();
            nuevoLote.setNumeroLote(txtDetalleNumeroLote.getText().trim());

            // Asignar el producto seleccionado
            ProductoFX productoSeleccionado = cmbDetalleProducto.getValue();
            nuevoLote.setProducto(productoSeleccionado);
            nuevoLote.setIdProducto(productoSeleccionado.getIdProducto());

            nuevoLote.setCantidad(Integer.parseInt(txtDetalleCantidad.getText().trim()));
            nuevoLote.setActivo(chkDetalleEstado.isSelected());
            nuevoLote.setFechaEntrada(dpFechaEntrada.getValue());
            nuevoLote.setFechaVencimiento(dpDetalleFechaVencimiento.getValue());

            // Guardar en la base de datos
            progressIndicator.setVisible(true);
            LoteFX loteCreado = loteService.crear(nuevoLote);

            // Actualizar la lista y seleccionar el nuevo lote
            listaLotes.add(loteCreado);
            lotesTableView.getSelectionModel().select(loteCreado);
            actualizarEstadisticas();

            // Volver al modo visualización
            configurarFormularioDetalle(false);
            btnGuardarCambios.setText("Guardar");
            btnGuardarCambios.setOnAction(e -> guardarCambios(e));

            progressIndicator.setVisible(false);
            statusLabel.setText("Lote creado correctamente");
        } catch (Exception e) {
            progressIndicator.setVisible(false);
            mostrarAlerta("Error al crear el lote: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void cargarLoteParaEditar(LoteFX lote) {
        mostrarDetalleLote(lote);
        configurarFormularioDetalle(true);
        statusLabel.setText("Editando lote: " + lote.getNumeroLote());
    }

    private void confirmarEliminarLote(LoteFX lote) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro de eliminar este lote?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                try {
                    loteService.eliminar(lote.getIdLote());
                    cargarLotesActivos();
                    statusLabel.setText("Lote eliminado correctamente");
                } catch (Exception e) {
                    mostrarError("Error al eliminar lote", e);
                }
            }
        });
    }

    private void mostrarError(String mensaje, Exception e) {
        statusLabel.setText(mensaje + ": " + e.getMessage());
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(mensaje);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.WARNING ? "Advertencia" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}