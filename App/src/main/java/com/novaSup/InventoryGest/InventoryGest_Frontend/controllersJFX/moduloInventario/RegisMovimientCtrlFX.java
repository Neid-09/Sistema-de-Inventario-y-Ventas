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
import java.util.Optional;
import java.util.ResourceBundle;

@Component
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
    @FXML private VBox formularioMovimiento;
    @FXML private Label lblTituloFormulario;
    @FXML private ComboBox<ProductoFX> cmbFormProducto;
    @FXML private ComboBox<ProveedorFX> cmbFormProveedor;
    @FXML private TextField txtCantidad;
    @FXML private TextField txtPrecioUnitario;
    @FXML private TextField txtMotivo;
    @FXML private Button btnEditarMovimiento;
    @FXML private Button btnEliminarMovimiento;

    private ObservableList<EntradaProductoFX> listaHistorial = FXCollections.observableArrayList();
    private ObservableList<ProductoFX> listaProductos = FXCollections.observableArrayList();
    private ObservableList<ProveedorFX> listaProveedores = FXCollections.observableArrayList();
    private EntradaProductoFX movimientoSeleccionado;

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

        // Habilitar selección de fila
        tablaHistorial.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            btnEditarMovimiento.setDisable(newSelection == null);
            btnEliminarMovimiento.setDisable(newSelection == null);
        });

        // Configurar combobox de tipos
        cmbTipoMovimiento.getItems().addAll("", EntradaProductoFX.TIPO_ENTRADA, EntradaProductoFX.TIPO_SALIDA);

        // Deshabilitar botones de edición/eliminación inicialmente
        btnEditarMovimiento.setDisable(true);
        btnEliminarMovimiento.setDisable(true);
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
        // Verificar permisos para editar y eliminar movimientos
        PermisosUIUtil.configurarBoton(btnEditarMovimiento, "editar_entrada_producto");
        PermisosUIUtil.configurarBoton(btnEliminarMovimiento, "eliminar_entrada_producto");
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
    void editarMovimientoSeleccionado(ActionEvent event) {
        // Verificar permisos
        if (!PermisosUIUtil.verificarPermisoConAlerta("editar_entrada_producto")) {
            return;
        }

        movimientoSeleccionado = tablaHistorial.getSelectionModel().getSelectedItem();
        if (movimientoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Debe seleccionar un movimiento para editar.");
            return;
        }

        cargarDatosParaEdicion();
        formularioMovimiento.setVisible(true);
        formularioMovimiento.setManaged(true);
    }

    @FXML
    void eliminarMovimientoSeleccionado(ActionEvent event) {
/*        // Verificar permisos
        if (!PermisosUIUtil.verificarPermisoConAlerta("eliminar_entrada_producto")) {
            return;
        }

        EntradaProductoFX movimientoAEliminar = tablaHistorial.getSelectionModel().getSelectedItem();
        if (movimientoAEliminar == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección requerida", "Debe seleccionar un movimiento para eliminar.");
            return;
        }

        // Confirmar eliminación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar este movimiento?");
        confirmacion.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                progressIndicator.setVisible(true);
                statusLabel.setText("Eliminando movimiento...");

                // Llamar al servicio para eliminar
                entradaProductoService.eliminar(movimientoAEliminar.getIdEntrada());

                // Actualizar lista
                listaHistorial.remove(movimientoAEliminar);

                progressIndicator.setVisible(false);
                statusLabel.setText("Movimiento eliminado correctamente.");
            } catch (Exception e) {
                progressIndicator.setVisible(false);
                mostrarError("Error al eliminar el movimiento", e);
            }
        }*/
    }

    private void cargarDatosParaEdicion() {
        lblTituloFormulario.setText("Modificar Movimiento");

        // Cargar producto
        for (ProductoFX p : cmbFormProducto.getItems()) {
            if (p.getNombre().equals(movimientoSeleccionado.getNombreProducto())) {
                cmbFormProducto.setValue(p);
                break;
            }
        }

        // Cargar proveedor si existe
        if (movimientoSeleccionado.getIdProveedor() != null) {
            cmbFormProveedor.setDisable(false);
            for (ProveedorFX p : cmbFormProveedor.getItems()) {
                if (p.getIdProveedor().equals(movimientoSeleccionado.getIdProveedor())) {
                    cmbFormProveedor.setValue(p);
                    break;
                }
            }
        } else {
            cmbFormProveedor.setDisable(true);
            cmbFormProveedor.setValue(null);
        }

        // Cargar resto de datos
        txtCantidad.setText(String.valueOf(movimientoSeleccionado.getCantidad()));
        txtPrecioUnitario.setText(movimientoSeleccionado.getPrecioUnitario().toString());
        txtMotivo.setText(movimientoSeleccionado.getMotivo());
    }

    @FXML
    void guardarMovimiento(ActionEvent event) {
/*        if (!validarFormulario()) {
            return;
        }

        try {
            progressIndicator.setVisible(true);
            statusLabel.setText("Guardando cambios...");

            ProductoFX producto = cmbFormProducto.getValue();
            Integer cantidad = Integer.parseInt(txtCantidad.getText().trim());
            BigDecimal precioUnitario = new BigDecimal(txtPrecioUnitario.getText().trim());
            String motivo = txtMotivo.getText().trim();

            // Obtener id de proveedor si está disponible
            Integer idProveedor = null;
            if (cmbFormProveedor.getValue() != null && !cmbFormProveedor.isDisable()) {
                idProveedor = cmbFormProveedor.getValue().getIdProveedor();
            }

            // Actualizar el movimiento
            EntradaProductoFX movimientoActualizado = entradaProductoService.actualizar(
                    movimientoSeleccionado.getIdEntrada(),
                    producto.getIdProducto(),
                    cantidad,
                    movimientoSeleccionado.getTipoMovimiento(),
                    precioUnitario,
                    idProveedor,
                    motivo);

            // Actualizar la tabla
            int indice = listaHistorial.indexOf(movimientoSeleccionado);
            if (indice >= 0) {
                listaHistorial.set(indice, movimientoActualizado);
            }

            formularioMovimiento.setVisible(false);
            formularioMovimiento.setManaged(false);
            movimientoSeleccionado = null;

            progressIndicator.setVisible(false);
            statusLabel.setText("Movimiento actualizado correctamente.");
        } catch (Exception e) {
            progressIndicator.setVisible(false);
            mostrarError("Error al actualizar movimiento", e);
        }*/
    }

    @FXML
    void cancelarRegistroMovimiento(ActionEvent event) {
        formularioMovimiento.setVisible(false);
        formularioMovimiento.setManaged(false);
        movimientoSeleccionado = null;
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

        if (!cmbFormProveedor.isDisable() && cmbFormProveedor.getValue() == null) {
            errores.append("- Debe seleccionar un proveedor para las entradas.\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Por favor corrija los siguientes errores:\n" + errores.toString());
            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
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