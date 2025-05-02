package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloInventario;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IRegistMovimientService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.RegistMovimientServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.ProductoServiceImplFX;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class RegisMovimientCtrlFX implements Initializable {

    @FXML private ComboBox<ProductoFX> cmbProducto;
    @FXML private DatePicker dpFechaDesde;
    @FXML private DatePicker dpFechaHasta;
    @FXML private ComboBox<String> cmbTipoMovimiento;
    @FXML private ProgressIndicator progressIndicator;
    @FXML private Label statusLabel;
    @FXML private TableView<EntradaProductoFX> tablaHistorial;
    @FXML private TableColumn<EntradaProductoFX, Integer> colIdEntrada;
    @FXML private TableColumn<EntradaProductoFX, String> colNombreProducto;
    @FXML private TableColumn<EntradaProductoFX, String> colNombreProveedor;
    @FXML private TableColumn<EntradaProductoFX, Integer> colCantidad;
    @FXML private TableColumn<EntradaProductoFX, BigDecimal> colPrecioUnitario;
    @FXML private TableColumn<EntradaProductoFX, LocalDateTime> colFecha;
    @FXML private TableColumn<EntradaProductoFX, String> colTipoMovimiento;
    @FXML private TableColumn<EntradaProductoFX, String> colMotivo;
    // Se han eliminado las referencias a los controles de creación/edición de movimientos
    // ya que el controlador RegistMovimientController es solo para consultas

    private ObservableList<EntradaProductoFX> listaHistorial = FXCollections.observableArrayList();
    private ObservableList<ProductoFX> listaProductos = FXCollections.observableArrayList();

    private final IRegistMovimientService registMovimientService;
    private final IProductoService productoService;

    // Añadir constructor para inicializar servicios mediante inyección
    public RegisMovimientCtrlFX(IRegistMovimientService registMovimientService, IProductoService productoService) {
        this.registMovimientService = registMovimientService;
        this.productoService = productoService;
    }

    // Servicio de proveedores eliminado ya que ya no se utiliza en este controlador

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarControlesUI();
        cargarDatosIniciales();
        verificarPermisos();
    }

    private void configurarControlesUI() {
        // Configurar tabla
        colIdEntrada.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getIdEntrada()));
        colNombreProducto.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreProducto()));
        colNombreProveedor.setCellValueFactory(data -> {
            String nombreProveedor = data.getValue().getNombreProveedor();
            return new SimpleStringProperty(nombreProveedor != null ? nombreProveedor : "N/A");
        });
        colCantidad.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getCantidad()));
        colPrecioUnitario.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getPrecioUnitario()));
        colFecha.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFecha()));
        colFecha.setCellFactory(column -> new TableCell<EntradaProductoFX, LocalDateTime>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatter.format(item));
                }
            }
        });
        colTipoMovimiento.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getTipoMovimiento()));
        colMotivo.setCellValueFactory(data -> {
            String motivo = data.getValue().getMotivo();
            return new SimpleStringProperty(motivo != null ? motivo : "");
        });

        tablaHistorial.setItems(listaHistorial);

        // Ya no necesitamos habilitar/deshabilitar botones de acción basados en la selección
        // ya que el controlador RegistMovimientController es solo para consultas

        // Configurar combobox de tipos
        cmbTipoMovimiento.getItems().addAll("", EntradaProductoFX.TIPO_ENTRADA, EntradaProductoFX.TIPO_SALIDA);

        // Ya no hay botones de edición/eliminación que deshabilitar
    }

    private void cargarDatosIniciales() {
        try {
            progressIndicator.setVisible(true);
            statusLabel.setText("Cargando datos...");

            // Cargar historial usando el nuevo servicio que apunta a /api/movimientos
            listaHistorial.clear();
            listaHistorial.addAll(registMovimientService.obtenerTodos());

            // Cargar productos para filtros
            listaProductos.clear();
            listaProductos.addAll(productoService.obtenerTodos());

            ProductoFX itemTodos = new ProductoFX();
            itemTodos.setIdProducto(null);
            itemTodos.setNombre("Todos los productos");
            listaProductos.add(0, itemTodos);

            cmbProducto.setItems(listaProductos);

            progressIndicator.setVisible(false);
            statusLabel.setText("Datos cargados correctamente. " + listaHistorial.size() + " registros encontrados.");
        } catch (Exception e) {
            progressIndicator.setVisible(false);
            mostrarError("Error al cargar datos iniciales", e);
        }
    }

    private void verificarPermisos() {
        // Método mantenido para compatibilidad con la estructura original
        // Ya no hay permisos que verificar ya que el controlador es solo para consultas
    }

    @FXML
    void filtrarHistorial(ActionEvent event) {
        try {
            progressIndicator.setVisible(true);
            statusLabel.setText("Filtrando datos...");

            Integer idProducto = null;
            ProductoFX productoSeleccionado = cmbProducto.getValue();
            if (productoSeleccionado != null && productoSeleccionado.getIdProducto() != null) {
                idProducto = productoSeleccionado.getIdProducto();
            }

            LocalDate desde = dpFechaDesde.getValue();
            LocalDate hasta = dpFechaHasta.getValue();
            String tipo = cmbTipoMovimiento.getValue();

            listaHistorial.clear();
            // Usamos el nuevo método obtenerFiltradosCompleto que permite filtrar también por proveedor
            // Por ahora no estamos filtrando por proveedor desde la UI, pero podría añadirse en el futuro
            listaHistorial.addAll(registMovimientService.obtenerFiltradosCompleto(
                    idProducto, null, desde, hasta, tipo));

            progressIndicator.setVisible(false);
            statusLabel.setText(listaHistorial.size() + " registros encontrados.");
        } catch (Exception e) {
            progressIndicator.setVisible(false);
            mostrarError("Error al filtrar datos", e);
        }
    }

    @FXML
    void limpiarFiltros(ActionEvent event) {
        cmbProducto.setValue(null);
        dpFechaDesde.setValue(null);
        dpFechaHasta.setValue(null);
        cmbTipoMovimiento.setValue(null);

        try {
            listaHistorial.clear();
            listaHistorial.addAll(registMovimientService.obtenerTodos());
            statusLabel.setText(listaHistorial.size() + " registros cargados.");
        } catch (Exception e) {
            mostrarError("Error al cargar datos", e);
        }
    }

    // Método utilizado internamente para mostrar mensajes de error
    private void mostrarError(String mensaje, Exception e) {
        statusLabel.setText(mensaje + ": " + e.getMessage());
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(mensaje);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }


}