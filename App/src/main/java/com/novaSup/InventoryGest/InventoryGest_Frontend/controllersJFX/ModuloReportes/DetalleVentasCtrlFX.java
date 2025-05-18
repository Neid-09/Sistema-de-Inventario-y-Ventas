package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.ModuloReportes;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.DetalleVentaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaFX;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
// import java.time.LocalDateTime; // Eliminada importación no utilizada
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DetalleVentasCtrlFX {

    @FXML
    private Button btnCerrar;

    @FXML
    private Button btnImprimirFactura;

    @FXML
    private Button btnModificarVenta;

    @FXML
    private Label cantidadTotalProductosLabel;

    @FXML
    private TableColumn<DetalleVentaFX, Integer> colCantidad;

    @FXML
    private TableColumn<DetalleVentaFX, BigDecimal> colPrecioUnitario;

    @FXML
    private TableColumn<DetalleVentaFX, String> colProducto;

    @FXML
    private TableColumn<DetalleVentaFX, BigDecimal> colSubtotalProducto;

    @FXML
    private Label estadoVentaLabel;

    @FXML
    private Label fechaVentaLabel;

    @FXML
    private Label idVentaLabel;

    @FXML
    private Label nombreClienteLabel;

    @FXML
    private Label subtotalGeneralLabel;

    @FXML
    private TableView<DetalleVentaFX> tablaDetallesVenta;

    @FXML
    private Label tipoPagoLabel;

    @FXML
    private Label totalFinalLabel;

    @FXML
    private Label totalImpuestosLabel;

    @FXML
    private Label totalVentaSuperiorLabel;

    private VentaFX ventaActual;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final NumberFormat BIG_DECIMAL_FORMATTER = createBigDecimalFormatter();

    private static NumberFormat createBigDecimalFormatter() {
        NumberFormat formatter = NumberFormat.getNumberInstance(Locale.US); // Usar Locale.US para punto decimal
        formatter.setMinimumFractionDigits(2);
        formatter.setMaximumFractionDigits(2);
        formatter.setGroupingUsed(false); // Para evitar comas como separadores de miles, ej: 1000.00
        return formatter;
    }

    private String formatBigDecimal(BigDecimal value) {
        if (value == null) {
            return "0.00"; // O un valor por defecto apropiado
        }
        return BIG_DECIMAL_FORMATTER.format(value);
    }

    @FXML
    public void initialize() {
        btnCerrar.setOnAction(event -> cerrarVentana());
        btnImprimirFactura.setOnAction(event -> imprimirFactura());
        btnModificarVenta.setOnAction(event -> modificarVenta());
        configurarColumnasTabla();
    }

    public void initData(VentaFX venta) {
        this.ventaActual = venta;
        if (ventaActual == null) {
            // Manejar el caso de que la venta sea nula, quizás cerrar la ventana o mostrar un error.
            System.err.println("Error: No se proporcionaron datos de la venta.");
            // Opcionalmente, limpiar los labels o mostrar un mensaje en la UI.
            idVentaLabel.setText("Detalles de la Venta #N/A");
            return;
        }

        idVentaLabel.setText("Detalles de la Venta #" + ventaActual.getNumeroVenta());
        if (ventaActual.getFecha() != null) {
            fechaVentaLabel.setText("Venta realizada el " + DATE_TIME_FORMATTER.format(ventaActual.getFecha()));
        } else {
            fechaVentaLabel.setText("Fecha no disponible");
        }
        nombreClienteLabel.setText(ventaActual.getNombreCliente() != null ? ventaActual.getNombreCliente() : "N/A");
        
        // TODO: Reemplazar "Completada" con ventaActual.getEstadoVenta() o similar cuando esté disponible en VentaFX.
        // Ejemplo: estadoVentaLabel.setText(ventaActual.getEstadoVentaString());
        estadoVentaLabel.setText("Completada"); 

        tipoPagoLabel.setText(ventaActual.getTipoPago() != null ? ventaActual.getTipoPago() : "N/A");
        totalVentaSuperiorLabel.setText(formatBigDecimal(ventaActual.getTotal())); // Corregido: getTotal() en lugar de getTotalVenta()
        
         if (ventaActual.getCantidadTotalProductos() != null) { // Corregido: getCantidadTotalProductos() en lugar de getCantidadTotalItems()
            cantidadTotalProductosLabel.setText("Productos (" + ventaActual.getCantidadTotalProductos() + ")");
        } else if (ventaActual.getDetalles() != null) {
            cantidadTotalProductosLabel.setText("Productos (" + ventaActual.getDetalles().size() + ")");
        } else {
            cantidadTotalProductosLabel.setText("Productos (0)");
        }


        subtotalGeneralLabel.setText(formatBigDecimal(ventaActual.getSubtotal()));
        totalImpuestosLabel.setText(formatBigDecimal(ventaActual.getTotalImpuestos()));
        totalFinalLabel.setText(formatBigDecimal(ventaActual.getTotal())); // Corregido: getTotal() en lugar de getTotalVenta()

        if (ventaActual.getDetalles() != null) {
            tablaDetallesVenta.setItems(FXCollections.observableArrayList(ventaActual.getDetalles()));
        } else {
            tablaDetallesVenta.setItems(FXCollections.emptyObservableList());
        }
    }

    private void configurarColumnasTabla() {
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombreProducto"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        colPrecioUnitario.setCellValueFactory(new PropertyValueFactory<>("precioUnitarioFinal"));
        colPrecioUnitario.setCellFactory(column -> new TableCell<DetalleVentaFX, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatBigDecimal(item));
                }
            }
        });

        colSubtotalProducto.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        colSubtotalProducto.setCellFactory(column -> new TableCell<DetalleVentaFX, BigDecimal>() {
            @Override
            protected void updateItem(BigDecimal item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(formatBigDecimal(item));
                }
            }
        });
    }

    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) btnCerrar.getScene().getWindow();
        stage.close();
    }

    @FXML
    private void imprimirFactura() {
        if (ventaActual != null) {
            System.out.println("Placeholder: Imprimir factura para venta #" + ventaActual.getNumeroVenta());
            // Aquí iría la lógica para generar e imprimir la factura.
        } else {
            System.out.println("Placeholder: Imprimir factura - Venta no cargada.");
        }
    }

    @FXML
    private void modificarVenta() {
        if (ventaActual != null) {
            System.out.println("Placeholder: Modificar detalles de venta #" + ventaActual.getNumeroVenta());
            // Aquí iría la lógica para abrir una ventana de modificación o permitir edición.
        } else {
            System.out.println("Placeholder: Modificar venta - Venta no cargada.");
        }
    }
}
