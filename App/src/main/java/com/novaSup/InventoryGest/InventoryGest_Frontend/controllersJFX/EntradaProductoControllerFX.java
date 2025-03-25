package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX.TipoMovimiento;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IEntradaProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

@Component
public class EntradaProductoControllerFX implements Initializable {

    @Autowired
    private IEntradaProductoService entradaProductoService;

    @Autowired
    private IProductoService productoService;

    @FXML
    private Button btnCerrarHistorial;

    @FXML
    private Button btnFiltrar;

    @FXML
    private Button btnLimpiarFiltros;

    @FXML
    private ComboBox<ProductoFX> cmbProducto;

    @FXML
    private ComboBox<TipoMovimiento> cmbTipoMovimiento;

    @FXML
    private TableColumn<EntradaProductoFX, Integer> colCantidad;

    @FXML
    private TableColumn<EntradaProductoFX, LocalDateTime> colFecha;

    @FXML
    private TableColumn<EntradaProductoFX, Integer> colIdEntrada;

/*    @FXML
    private TableColumn<EntradaProductoFX, Integer> colIdProducto;*/

    @FXML
    private TableColumn<EntradaProductoFX, String> colNombreP;

    @FXML
    private TableColumn<EntradaProductoFX, BigDecimal> colPrecioUnitario;

    @FXML
    private TableColumn<EntradaProductoFX, TipoMovimiento> colTipoMovimiento;

    @FXML
    private DatePicker dpFechaDesde;

    @FXML
    private DatePicker dpFechaHasta;

    @FXML
    private TableView<EntradaProductoFX> tablaHistorial;

    private ObservableList<EntradaProductoFX> listaHistorial = FXCollections.observableArrayList();
    private ObservableList<ProductoFX> listaProductos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        Platform.runLater(() -> {
            Stage stage = (Stage) tablaHistorial.getScene().getWindow();
            stage.setResizable(true);
        });
        configurarColumnas();
        configurarComboBoxes();
        cargarProductos();
        cargarHistorial();
    }

    private void configurarColumnas() {
        colIdEntrada.setCellValueFactory(new PropertyValueFactory<>("idEntrada"));
        //colIdProducto.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombreP.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colTipoMovimiento.setCellValueFactory(new PropertyValueFactory<>("tipoMovimiento"));

        // Formatear la columna de fecha
        colFecha.setCellFactory(column -> {
            return new TableCell<EntradaProductoFX, LocalDateTime>() {
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
            };
        });

        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitario"));
        colPrecioUnitario.setCellFactory(column -> {
            return new TableCell<EntradaProductoFX, BigDecimal>() {
                @Override
                protected void updateItem(BigDecimal item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("$ " + item.toString());
                    }
                }
            };
        });

    }

    private void configurarComboBoxes() {
        // Configurar ComboBox de productos
        cmbProducto.setConverter(new StringConverter<ProductoFX>() {
            @Override
            public String toString(ProductoFX producto) {
                return producto != null ? producto.getNombre() : "";
            }

            @Override
            public ProductoFX fromString(String string) {
                return null;
            }
        });

        // Configurar ComboBox de tipo de movimiento
        cmbTipoMovimiento.getItems().addAll(TipoMovimiento.values());
    }

    private void cargarProductos() {
        try {
            listaProductos.clear();
            List<ProductoFX> productos = productoService.obtenerTodos();
            listaProductos.addAll(productos);
            cmbProducto.setItems(listaProductos);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los productos: " + e.getMessage());
        }
    }

    private void cargarHistorial() {
        try {
            listaHistorial.clear();
            List<EntradaProductoFX> historial = entradaProductoService.obtenerTodos();
            listaHistorial.addAll(historial);
            tablaHistorial.setItems(listaHistorial);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo cargar el historial: " + e.getMessage());
        }
    }

    @FXML
    void filtrarHistorial(ActionEvent event) {
        try {
            ProductoFX productoSeleccionado = cmbProducto.getValue();
            LocalDate fechaDesde = dpFechaDesde.getValue();
            LocalDate fechaHasta = dpFechaHasta.getValue();
            TipoMovimiento tipoMovimiento = cmbTipoMovimiento.getValue();

            Integer idProducto = productoSeleccionado != null ? productoSeleccionado.getIdProducto() : null;

            List<EntradaProductoFX> resultado = entradaProductoService.obtenerFiltrados(
                    idProducto, fechaDesde, fechaHasta, tipoMovimiento);

            listaHistorial.clear();
            listaHistorial.addAll(resultado);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al filtrar: " + e.getMessage());
        }
    }

    @FXML
    void limpiarFiltros(ActionEvent event) {
        cmbProducto.setValue(null);
        dpFechaDesde.setValue(null);
        dpFechaHasta.setValue(null);
        cmbTipoMovimiento.setValue(null);
        cargarHistorial();
    }

    @FXML
    void cerrarHistorial(ActionEvent event) {
        Stage stage = (Stage) btnCerrarHistorial.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
