package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.ModuloReportes;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaFX; // Importar VentaFX
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.DetalleVentaFX; // Importar DetalleVentaFX

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.List;
// Eliminados ArrayList, ya que FXCollections.observableArrayList se usa donde es necesario.
import java.util.Map;
import java.util.HashMap;
import java.math.RoundingMode;
import java.util.Set; // Importar Set
import java.util.HashSet; // Importar HashSet
import java.time.LocalDate; // Importar LocalDate

public class ResumenCtrlFX {

    @FXML
    private Button btnExportarResumen;

    @FXML
    private TableColumn<ProductoResumenRow, String> colCodProductos;

    @FXML
    private TableColumn<ProductoResumenRow, Integer> colProdCantidad;

    @FXML
    private TableColumn<ProductoResumenRow, String> colProdName;

    @FXML
    private TableColumn<ProductoResumenRow, BigDecimal> colProdPromedio;

    @FXML
    private TableColumn<ProductoResumenRow, BigDecimal> colProdTotal;

    @FXML
    private Label lblTotalEfectivo;

    @FXML
    private Label lblTotalProductos;

    @FXML
    private Label lblTotalTarjeta;

    @FXML
    private Label lblTotalVendido;

    @FXML
    private TableView<ProductoResumenRow> tablaResumen;

    private IVentaSerivice ventaService;

    public static class ProductoResumenRow {
        private final String codigo;
        private final String nombre;
        private final int cantidad;
        private final BigDecimal total;
        private final BigDecimal promedioVenta;

        public ProductoResumenRow(String codigo, String nombre, int cantidad, BigDecimal total, BigDecimal promedioVenta) {
            this.codigo = codigo;
            this.nombre = nombre;
            this.cantidad = cantidad;
            this.total = total;
            this.promedioVenta = promedioVenta;
        }

        public String getCodigo() { return codigo; }
        public String getNombre() { return nombre; }
        public int getCantidad() { return cantidad; }
        public BigDecimal getTotal() { return total; }
        public BigDecimal getPromedioVenta() { return promedioVenta; }
    }

    @FXML
    private void initialize() {
        colCodProductos.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colProdName.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colProdCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colProdTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colProdPromedio.setCellValueFactory(new PropertyValueFactory<>("promedioVenta"));
        btnExportarResumen.setOnAction(event -> exportarResumen());
    }

    private void cargarDatosResumen() {
        if (ventaService == null) {
            System.err.println("VentaService no ha sido inicializado.");
            lblTotalProductos.setText("N/A");
            lblTotalTarjeta.setText("N/A");
            lblTotalEfectivo.setText("N/A");
            lblTotalVendido.setText("N/A");
            if (tablaResumen != null) {
                tablaResumen.getItems().clear();
            }
            return;
        }

        NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
        currencyFormatter.setMaximumFractionDigits(2);

        List<VentaFX> ventas;
        try {
            // TODO: Implementar un método específico en IVentaSerivice para obtener el resumen directamente.
            ventas = ventaService.listarVentas();
        } catch (Exception e) {
            System.err.println("Error al listar ventas: " + e.getMessage());
            e.printStackTrace(); // Es buena idea loguear el stack trace para depuración
            // Opcionalmente, mostrar un mensaje de error al usuario en la UI:
            lblTotalProductos.setText("Error");
            lblTotalTarjeta.setText("Error");
            lblTotalEfectivo.setText("Error");
            lblTotalVendido.setText("Error");
            if (tablaResumen != null) {
                tablaResumen.getItems().clear();
                // Podrías añadir un placeholder a la tabla indicando el error.
            }
            return; // Salir del método si hay un error cargando las ventas
        }

        BigDecimal totalEfectivo = BigDecimal.ZERO;
        BigDecimal totalTarjeta = BigDecimal.ZERO;
        BigDecimal totalVendido = BigDecimal.ZERO;
        long totalItemsVendidos = 0;
        Set<LocalDate> diasUnicosDeVenta = new HashSet<>();

        class TempProductoData {
            String codigo; String nombre; int cantidadTotal = 0; BigDecimal valorTotalVendido = BigDecimal.ZERO;
            TempProductoData(String codigo, String nombre) { this.codigo = codigo; this.nombre = nombre; }
        }
        Map<String, TempProductoData> productosAgregados = new HashMap<>();

        if (ventas != null) {
            for (VentaFX venta : ventas) {
                if (venta.getFecha() != null) {
                    diasUnicosDeVenta.add(venta.getFecha().toLocalDate());
                }

                if (venta.getTotal() != null) { 
                    totalVendido = totalVendido.add(venta.getTotal());
                    if ("EFECTIVO".equalsIgnoreCase(venta.getTipoPago())) {
                        totalEfectivo = totalEfectivo.add(venta.getTotal());
                    } else if ("TARJETA".equalsIgnoreCase(venta.getTipoPago())) {
                        totalTarjeta = totalTarjeta.add(venta.getTotal());
                    }
                }

                if (venta.getDetalles() != null) {
                    for (DetalleVentaFX detalle : venta.getDetalles()) { // Usar DetalleVentaFX
                        totalItemsVendidos += detalle.getCantidad();
                        // Usar getCodProducto() y getNombreProducto() de DetalleVentaFX
                        String productoCodigo = detalle.getCodProducto();
                        String productoNombre = detalle.getNombreProducto();

                        if (productoCodigo != null) { 
                            TempProductoData tempData = productosAgregados.computeIfAbsent(productoCodigo,
                                k -> new TempProductoData(productoCodigo, productoNombre));
                            tempData.cantidadTotal += detalle.getCantidad();
                            if (detalle.getSubtotal() != null) { 
                                tempData.valorTotalVendido = tempData.valorTotalVendido.add(detalle.getSubtotal());
                            }
                        }
                    }
                }
            }
        }

        lblTotalProductos.setText(String.valueOf(totalItemsVendidos));
        lblTotalTarjeta.setText(currencyFormatter.format(totalTarjeta));
        lblTotalEfectivo.setText(currencyFormatter.format(totalEfectivo));
        lblTotalVendido.setText(currencyFormatter.format(totalVendido));

        ObservableList<ProductoResumenRow> datosTabla = FXCollections.observableArrayList();
        int numeroDiasUnicosDeVenta = diasUnicosDeVenta.isEmpty() ? 1 : diasUnicosDeVenta.size(); // Evitar división por cero si no hay fechas

        for (TempProductoData tempData : productosAgregados.values()) {
            BigDecimal promedioVentaPorDia = BigDecimal.ZERO;
            if (tempData.cantidadTotal > 0) {
                promedioVentaPorDia = BigDecimal.valueOf(tempData.cantidadTotal)
                                            .divide(BigDecimal.valueOf(numeroDiasUnicosDeVenta), 2, RoundingMode.HALF_UP);
            }
            datosTabla.add(new ProductoResumenRow(
                tempData.codigo,
                tempData.nombre,
                tempData.cantidadTotal,
                tempData.valorTotalVendido != null ? tempData.valorTotalVendido : BigDecimal.ZERO, 
                promedioVentaPorDia // Usar el nuevo promedio
            ));
        }
        tablaResumen.setItems(datosTabla);

        colProdTotal.setCellFactory(tc -> new FormattedCurrencyCell<>());
        // Se elimina la factoría de celda para colProdPromedio ya que no es moneda
        // colProdPromedio.setCellFactory(tc -> new FormattedCurrencyCell<>()); 
    }

    public void setService(IVentaSerivice ventaService) {
        this.ventaService = ventaService;
        if (this.ventaService != null) {
             cargarDatosResumen();
        } else {
            System.err.println("Intento de configurar un VentaService nulo.");
            lblTotalProductos.setText("N/A");
            lblTotalTarjeta.setText("N/A");
            lblTotalEfectivo.setText("N/A");
            lblTotalVendido.setText("N/A");
            if (tablaResumen != null) {
                tablaResumen.getItems().clear();
            }
        }
    }

    private void exportarResumen() {
        System.out.println("Exportar Resumen presionado. Lógica de exportación pendiente.");
    }

    private static class FormattedCurrencyCell<S, T extends Number> extends TableCell<S, T> {
        private final NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
        public FormattedCurrencyCell() {
            currencyFormatter.setMaximumFractionDigits(2);
        }
        @Override
        protected void updateItem(T item, boolean empty) {
            super.updateItem(item, empty);
            if (empty || item == null) {
                setText(null);
                setGraphic(null);
            } else {
                setText(currencyFormatter.format(item));
            }
        }
    }
}
