package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.ModuloReportes;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.DetalleVentaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IFacturaService;

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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.awt.Desktop;
import javafx.scene.Cursor;
import javafx.concurrent.Task;

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

    private IFacturaService facturaService;
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

    public void setService(IFacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @FXML
    public void initialize() {
        btnCerrar.setOnAction(event -> cerrarVentana());
        btnImprimirFactura.setOnAction(event -> handleVerDocumentoVenta());
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
        
        // Lógica para cambiar el texto del botón Ver factura/comprobante (Corregido)
        if (ventaActual.getNombreCliente() != null && ventaActual.getNombreCliente().equalsIgnoreCase("Venta General")) {
            btnImprimirFactura.setText("Ver comprobante de pago"); // Para ventas generales
        } else {
            btnImprimirFactura.setText("Ver factura"); // Para clientes específicos (factura real)
        }

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
    private void handleVerDocumentoVenta() {
        if (ventaActual != null) {
            int idVenta = ventaActual.getIdVenta();
            if (idVenta <= 0) {
                System.err.println("Error: ID de venta no válido.");
                // Opcional: Mostrar una alerta al usuario
                return;
            }

            // Validar si el cliente NO es "Venta General" para intentar obtener la factura (funcionalidad implementada).
            // Si es "Venta General", mostrar mensaje de funcionalidad no implementada para comprobante.
            if (ventaActual.getNombreCliente() != null && !ventaActual.getNombreCliente().equalsIgnoreCase("Venta General")) {
                // Deshabilitar botones y mostrar indicador de carga (opcional)
                setControlesDeshabilitados(true);

                // Crear una tarea en segundo plano para llamar al servicio y manejar la respuesta
                Task<byte[]> getDocumentTask = new Task<>() {
                    @Override
                    protected byte[] call() throws Exception {
                        // Llamar al servicio para obtener el PDF de la factura
                        return facturaService.getFacturaPdfByIdVenta(idVenta);
                    }

                    @Override
                    protected void succeeded() {
                        super.succeeded();
                        // Re-habilitar controles y ocultar indicador de carga
                        setControlesDeshabilitados(false);

                        byte[] pdfBytes = getValue();
                        if (pdfBytes != null && pdfBytes.length > 0) {
                            // Guardar y abrir el PDF
                            try {
                                // Usar el idVenta para el nombre del archivo temporal
                                File tempFile = File.createTempFile("factura_venta_" + idVenta + "_", ".pdf");
                                try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                                    fos.write(pdfBytes);
                                }

                                // Abrir el archivo con la aplicación predeterminada del sistema
                                if (Desktop.isDesktopSupported()) {
                                    Desktop.getDesktop().open(tempFile);
                                } else {
                                    System.err.println("Error: La apertura automática de archivos no es compatible con su sistema.");
                                    // Opcional: Mostrar una alerta al usuario
                                }

                                // Opcional: eliminar el archivo temporal al cerrar la aplicación
                                tempFile.deleteOnExit();

                            } catch (IOException e) {
                                System.err.println("Error al abrir PDF: " + e.getMessage());
                                // Opcional: Mostrar una alerta al usuario
                                e.printStackTrace();
                            }
                        } else {
                            System.err.println("Error al obtener PDF: No se recibieron datos de PDF válidos.");
                            // Opcional: Mostrar una alerta al usuario
                        }
                    }

                    @Override
                    protected void failed() {
                        super.failed();
                        // Re-habilitar controles y ocultar indicador de carga
                        setControlesDeshabilitados(false);
                        Throwable cause = getException();
                        System.err.println("Error en el servicio al obtener factura: " + cause.getMessage());
                        // Opcional: Mostrar una alerta al usuario
                        cause.printStackTrace();
                    }
                };

                // Ejecutar la tarea en un hilo separado
                new Thread(getDocumentTask).start();
            } else {
                // Si es "Venta General", la visualización de comprobante no está implementada
                System.out.println("Visualización de comprobante de pago no implementada para ventas con cliente General.");
                // Opcional: Mostrar una alerta al usuario (mensaje más amigable)
            }
        } else {
            System.err.println("Error: Venta no cargada al intentar ver documento.");
            // Opcional: Mostrar una alerta al usuario
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

    /**
     * Habilita o deshabilita los controles principales del diálogo mientras se procesa una operación.
     * @param disabled true para deshabilitar, false para habilitar.
     */
    private void setControlesDeshabilitados(boolean disabled) {
        btnCerrar.setDisable(disabled);
        btnModificarVenta.setDisable(disabled);
        btnImprimirFactura.setDisable(disabled);
        // Puedes agregar otros controles aquí si es necesario deshabilitarlos también

        // Cambiar el cursor para indicar que algo está sucediendo
        if (btnCerrar.getScene() != null) {
            btnCerrar.getScene().setCursor(disabled ? Cursor.WAIT : Cursor.DEFAULT);
        }
    }
}
