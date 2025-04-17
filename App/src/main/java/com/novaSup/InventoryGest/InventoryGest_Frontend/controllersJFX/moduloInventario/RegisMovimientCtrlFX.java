package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloInventario;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProveedorFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IEntradaProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProveedorService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
public class RegisMovimientCtrlFX implements Initializable {

    @FXML private ComboBox<ProductoFX> cmbProducto;
    @FXML private DatePicker dpFechaDesde;
    @FXML private DatePicker dpFechaHasta;
    @FXML private ComboBox<String> cmbTipoMovimiento;
    @FXML private Button btnFiltrar;
    @FXML private Button btnLimpiarFiltros;
    @FXML private Button btnRegistrarEntrada;
    @FXML private Button btnRegistrarSalida;
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
    @FXML private VBox formularioMovimiento;
    @FXML private Label lblTituloFormulario;
    @FXML private ComboBox<ProductoFX> cmbFormProducto;
    @FXML private ComboBox<ProveedorFX> cmbFormProveedor;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecioUnitario;
    @FXML private TextField txtMotivo;
    @FXML private Button btnCancelarRegistro;
    @FXML private Button btnGuardarMovimiento;

    private ObservableList<EntradaProductoFX> listaHistorial = FXCollections.observableArrayList();
    private ObservableList<ProductoFX> listaProductos = FXCollections.observableArrayList();
    private ObservableList<ProveedorFX> listaProveedores = FXCollections.observableArrayList();
    private String tipoMovimientoActual = null;

    @Autowired
    private IEntradaProductoService entradaProductoService;

    @Autowired
    private IProductoService productoService;

    @Autowired
    private IProveedorService proveedorService;

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

        // Configurar combobox de tipos
        cmbTipoMovimiento.getItems().addAll("", EntradaProductoFX.TIPO_ENTRADA, EntradaProductoFX.TIPO_SALIDA);
    }

    private void cargarDatosIniciales() {
        try {
            progressIndicator.setVisible(true);
            statusLabel.setText("Cargando datos...");

            // Cargar historial
            listaHistorial.clear();
            listaHistorial.addAll(entradaProductoService.obtenerTodos());

            // Cargar productos para filtros
            listaProductos.clear();
            listaProductos.addAll(productoService.obtenerTodos());

            ProductoFX itemTodos = new ProductoFX();
            itemTodos.setIdProducto(null);
            itemTodos.setNombre("Todos los productos");
            listaProductos.add(0, itemTodos);

            cmbProducto.setItems(listaProductos);
            cmbFormProducto.setItems(FXCollections.observableArrayList(listaProductos.subList(1, listaProductos.size())));

            // Cargar proveedores
            listaProveedores.clear();
            listaProveedores.addAll(proveedorService.obtenerTodos());
            cmbFormProveedor.setItems(listaProveedores);

            progressIndicator.setVisible(false);
            statusLabel.setText("Datos cargados correctamente. " + listaHistorial.size() + " registros encontrados.");
        } catch (Exception e) {
            progressIndicator.setVisible(false);
            mostrarError("Error al cargar datos iniciales", e);
        }
    }

    private void verificarPermisos() {
        // Verificar permisos para registrar movimientos
        PermisosUIUtil.configurarBoton(btnRegistrarEntrada, "registrar_entrada_producto");
        PermisosUIUtil.configurarBoton(btnRegistrarSalida, "registrar_entrada_producto");
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
            listaHistorial.addAll(entradaProductoService.obtenerFiltrados(
                    idProducto, desde, hasta, tipo));

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
            listaHistorial.addAll(entradaProductoService.obtenerTodos());
            statusLabel.setText(listaHistorial.size() + " registros cargados.");
        } catch (Exception e) {
            mostrarError("Error al cargar datos", e);
        }
    }

    @FXML
    void mostrarFormularioEntrada(ActionEvent event) {
        // Verificar permisos
        if (!PermisosUIUtil.verificarPermisoConAlerta("registrar_entrada_producto")) {
            return;
        }

        lblTituloFormulario.setText("Registrar Entrada de Producto");
        tipoMovimientoActual = EntradaProductoFX.TIPO_ENTRADA;

        // Mostrar y configurar proveedor para entradas
        cmbFormProveedor.setDisable(false);
        cmbFormProveedor.setValue(null);

        limpiarFormulario();
        formularioMovimiento.setVisible(true);
    }

    @FXML
    void mostrarFormularioSalida(ActionEvent event) {
        // Verificar permisos
        if (!PermisosUIUtil.verificarPermisoConAlerta("registrar_entrada_producto")) {
            return;
        }

        lblTituloFormulario.setText("Registrar Salida de Producto");
        tipoMovimientoActual = EntradaProductoFX.TIPO_SALIDA;

        // Ocultar proveedor para salidas
        cmbFormProveedor.setDisable(true);
        cmbFormProveedor.setValue(null);

        limpiarFormulario();
        formularioMovimiento.setVisible(true);
    }

    @FXML
    void guardarMovimiento(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }

        try {
            progressIndicator.setVisible(true);
            statusLabel.setText("Registrando movimiento...");

            ProductoFX producto = cmbFormProducto.getValue();
            Integer cantidad = Integer.parseInt(txtCantidad.getText().trim());
            BigDecimal precioUnitario = new BigDecimal(txtPrecioUnitario.getText().trim());
            String motivo = txtMotivo.getText().trim();

            // Obtener id de proveedor si es una entrada
            Integer idProveedor = null;
            if (EntradaProductoFX.TIPO_ENTRADA.equals(tipoMovimientoActual) && cmbFormProveedor.getValue() != null) {
                idProveedor = cmbFormProveedor.getValue().getIdProveedor();
            }

            // Registrar el movimiento
            EntradaProductoFX nuevaEntrada = entradaProductoService.registrarMovimiento(
                    producto.getIdProducto(), cantidad, tipoMovimientoActual,
                    precioUnitario, idProveedor, motivo);

            // Actualizar la tabla
            listaHistorial.add(0, nuevaEntrada);
            formularioMovimiento.setVisible(false);

            progressIndicator.setVisible(false);
            statusLabel.setText("Movimiento registrado correctamente.");
        } catch (Exception e) {
            progressIndicator.setVisible(false);
            mostrarError("Error al registrar movimiento", e);
        }
    }

    @FXML
    void cancelarRegistroMovimiento(ActionEvent event) {
        formularioMovimiento.setVisible(false);
        statusLabel.setText("Operación cancelada.");
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (cmbFormProducto.getValue() == null) {
            errores.append("- Debe seleccionar un producto.\n");
        }

        if (txtCantidad.getText().trim().isEmpty()) {
            errores.append("- Debe ingresar una cantidad.\n");
        } else {
            try {
                int cantidad = Integer.parseInt(txtCantidad.getText().trim());
                if (cantidad <= 0) {
                    errores.append("- La cantidad debe ser mayor a cero.\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- La cantidad debe ser un número entero válido.\n");
            }
        }

        if (txtPrecioUnitario.getText().trim().isEmpty()) {
            errores.append("- Debe ingresar un precio unitario.\n");
        } else {
            try {
                BigDecimal precio = new BigDecimal(txtPrecioUnitario.getText().trim());
                if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                    errores.append("- El precio unitario debe ser mayor a cero.\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- El precio unitario debe ser un número válido.\n");
            }
        }

        if (EntradaProductoFX.TIPO_ENTRADA.equals(tipoMovimientoActual) && cmbFormProveedor.getValue() == null) {
            errores.append("- Debe seleccionar un proveedor para las entradas.\n");
        }

        if (errores.length() > 0) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Validación");
            alert.setHeaderText("Por favor corrija los siguientes errores:");
            alert.setContentText(errores.toString());
            alert.showAndWait();
            return false;
        }

        return true;
    }

    private void limpiarFormulario() {
        cmbFormProducto.setValue(null);
        txtCantidad.clear();
        txtPrecioUnitario.clear();
        txtMotivo.clear();
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
}