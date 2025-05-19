package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.ModuloReportes;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.DetalleVentaFX; // Asegurarse de que esta importación exista

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart; // Importar BarChart
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.Node;


import java.math.BigDecimal;
import java.time.Month;
import java.time.format.TextStyle;
import java.util.LinkedHashMap; // Para mantener el orden de inserción después de ordenar
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

public class GraficosCtrlFX {

    @FXML
    private Button btnExportarGraficos;

    @FXML
    private ComboBox<String> cmbTipoGrafico; // Cambiado de ComboBox<?>

    @FXML
    private Pane contenedorGrafico1;

    @FXML
    private Pane contenedorGrafico2;

    @FXML
    private Label lblTituloGrafico1;

    @FXML
    private Label lblTituloGrafico2;

    private IVentaSerivice ventaService; // Renombrado para seguir convención

    public void setService(IVentaSerivice iVentaSerivice) {
        this.ventaService = iVentaSerivice;
        // Cargar datos o inicializar gráficos si el servicio ya está disponible y es necesario
        if (cmbTipoGrafico.getItems().isEmpty()) { // Evitar recargar si ya tiene items
            cargarOpcionesAnalisis();
        }
        // Seleccionar el primer item por defecto y cargar gráficos
        if (!cmbTipoGrafico.getItems().isEmpty() && cmbTipoGrafico.getSelectionModel().getSelectedItem() == null) {
            cmbTipoGrafico.getSelectionModel().selectFirst();
            // actualizarGraficos(); // El listener se encargará de esto si la selección cambia
        } else if (cmbTipoGrafico.getSelectionModel().getSelectedItem() != null) {
            actualizarGraficos(); // Si ya hay algo seleccionado (ej. por FXML o persistencia)
        }
    }

    @FXML
    public void initialize() {
        // Este método se llama después de que los campos @FXML han sido inyectados.
        if (cmbTipoGrafico.getItems().isEmpty()) {
            cargarOpcionesAnalisis();
        }

        cmbTipoGrafico.setOnAction(event -> actualizarGraficos());

        // No llamar a actualizarGraficos() aquí directamente si setService() lo va a hacer
        // o si queremos esperar a que el servicio esté disponible.
        // La lógica en setService y el listener de setOnAction deberían cubrir la carga inicial.
        if (cmbTipoGrafico.getItems().isEmpty()) { // Asegurar que las opciones se carguen si el servicio no se setea inmediatamente
            cargarOpcionesAnalisis();
        }
         // Si hay items y nada seleccionado, seleccionar el primero. El listener se activará.
        if (!cmbTipoGrafico.getItems().isEmpty() && cmbTipoGrafico.getSelectionModel().getSelectedItem() == null) {
            cmbTipoGrafico.getSelectionModel().selectFirst();
        }

    }

    private void cargarOpcionesAnalisis() {
        ObservableList<String> opciones = FXCollections.observableArrayList(
                "Análisis de Ventas Generales",
                "Análisis por Producto"
                // Añadir más tipos de análisis aquí en el futuro
                // "Análisis por Vendedor",
                // "Análisis por Cliente"
        );
        cmbTipoGrafico.setItems(opciones);
    }

    private void actualizarGraficos() {
        String seleccion = cmbTipoGrafico.getValue();

        // Limpiar contenedores y títulos
        limpiarContenedor(contenedorGrafico1);
        lblTituloGrafico1.setText("");
        limpiarContenedor(contenedorGrafico2);
        lblTituloGrafico2.setText("");

        if (ventaService == null) {
            lblTituloGrafico1.setText("Servicio de ventas no disponible.");
            return;
        }

        if (seleccion == null) {
            lblTituloGrafico1.setText("Seleccione un tipo de análisis.");
            return;
        }
        
        List<VentaFX> todasLasVentas;
        try {
            todasLasVentas = ventaService.listarVentas();
        } catch (Exception e) {
            lblTituloGrafico1.setText("Error al cargar datos de ventas.");
            // Log the exception e.g., e.printStackTrace(); or use a logger
            return;
        }
        
        if (todasLasVentas == null || todasLasVentas.isEmpty()) {
            lblTituloGrafico1.setText("No hay datos de ventas para mostrar.");
            return;
        }


        switch (seleccion) {
            case "Análisis de Ventas Generales":
                lblTituloGrafico1.setText("Tendencia de Ingresos Mensuales");
                LineChart<String, Number> tendenciaChart = crearGraficoTendenciaVentasTiempo(todasLasVentas);
                agregarGraficoAContenedor(tendenciaChart, contenedorGrafico1);

                lblTituloGrafico2.setText("Distribución de Ventas por Método de Pago");
                PieChart distribucionPagoChart = crearGraficoDistribucionTipoPago(todasLasVentas);
                agregarGraficoAContenedor(distribucionPagoChart, contenedorGrafico2);
                break;
            case "Análisis por Producto":
                lblTituloGrafico1.setText("Top 10 Productos por Ingresos");
                BarChart<String, Number> topProductosChart = crearGraficoProductosMasVendidos(todasLasVentas, 10);
                agregarGraficoAContenedor(topProductosChart, contenedorGrafico1);

                lblTituloGrafico2.setText("Contribución de Productos a Ingresos");
                PieChart contribucionProductosChart = crearGraficoContribucionProductosIngresos(todasLasVentas);
                agregarGraficoAContenedor(contribucionProductosChart, contenedorGrafico2);
                break;
            // Otros casos para diferentes análisis
        }
    }
    
    private void limpiarContenedor(Pane contenedor) {
        contenedor.getChildren().clear();
    }

    private void agregarGraficoAContenedor(Node grafico, Pane contenedor) {
        if (grafico != null && contenedor != null) {
            contenedor.getChildren().add(grafico);
            // Ajustar tamaño del gráfico al contenedor
            if (grafico instanceof javafx.scene.chart.Chart) {
                ((javafx.scene.chart.Chart) grafico).prefWidthProperty().bind(contenedor.widthProperty());
                ((javafx.scene.chart.Chart) grafico).prefHeightProperty().bind(contenedor.heightProperty());
            }
        }
    }


    private LineChart<String, Number> crearGraficoTendenciaVentasTiempo(List<VentaFX> ventas) {
        // Agrupar ventas por mes y sumar totales
        Map<Month, BigDecimal> ventasPorMes = ventas.stream()
                .filter(venta -> venta.getFecha() != null && venta.getTotal() != null)
                .collect(Collectors.groupingBy(
                        venta -> venta.getFecha().getMonth(),
                        Collectors.reducing(BigDecimal.ZERO, VentaFX::getTotal, BigDecimal::add)
                ));

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mes");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ingresos Totales");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos Mensuales");

        // Ordenar por mes para el gráfico
        java.util.Arrays.stream(Month.values()).forEach(month -> {
            BigDecimal totalMes = ventasPorMes.getOrDefault(month, BigDecimal.ZERO);
            // Usar el nombre completo del mes para mayor claridad si el espacio lo permite
            series.getData().add(new XYChart.Data<>(month.getDisplayName(TextStyle.FULL, Locale.of("es", "ES")), totalMes));
        });
        
        lineChart.getData().add(series);
        lineChart.setLegendVisible(true);
        return lineChart;
    }

    private PieChart crearGraficoDistribucionTipoPago(List<VentaFX> ventas) {
        Map<String, Long> conteoPorTipoPago = ventas.stream()
                .filter(venta -> venta.getTipoPago() != null && !venta.getTipoPago().isEmpty())
                .collect(Collectors.groupingBy(
                        VentaFX::getTipoPago,
                        Collectors.counting() // Contando número de transacciones por tipo de pago
                ));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        conteoPorTipoPago.forEach((tipo, count) -> {
            pieChartData.add(new PieChart.Data(tipo + " (" + count + ")", count));
        });

        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setLegendVisible(true); // Mostrar leyenda
        // pieChart.setLabelsVisible(false); // Ocultar etiquetas en sectores si son muchas
        return pieChart;
    }
    
    // --- Métodos para Análisis por Producto ---

    private BarChart<String, Number> crearGraficoProductosMasVendidos(List<VentaFX> ventas, int topN) {
        Map<String, BigDecimal> ingresosPorProducto = ventas.stream()
                .flatMap(venta -> venta.getDetalles().stream()) // Aplanar la lista de detalles de todas las ventas
                .filter(detalle -> detalle.getNombreProducto() != null && detalle.getSubtotal() != null)
                .collect(Collectors.groupingBy(
                        DetalleVentaFX::getNombreProducto,
                        Collectors.reducing(BigDecimal.ZERO, DetalleVentaFX::getSubtotal, BigDecimal::add)
                ));

        // Ordenar por ingresos descendentes y tomar topN
        Map<String, BigDecimal> topProductos = ingresosPorProducto.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // En caso de colisión de claves (no debería pasar aquí)
                        LinkedHashMap::new // Para mantener el orden de inserción (ordenado)
                ));

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Producto");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ingresos");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        // barChart.setTitle("Top " + topN + " Productos por Ingresos"); // El título se maneja con Label

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ingresos por Producto");

        topProductos.forEach((nombreProducto, ingresoTotal) -> {
            series.getData().add(new XYChart.Data<>(nombreProducto, ingresoTotal));
        });

        barChart.getData().add(series);
        barChart.setLegendVisible(false); // La serie ya tiene nombre, la leyenda puede ser redundante para un solo BarChart
        return barChart;
    }

    private PieChart crearGraficoContribucionProductosIngresos(List<VentaFX> ventas) {
        Map<String, BigDecimal> ingresosPorProducto = ventas.stream()
                .flatMap(venta -> venta.getDetalles().stream())
                .filter(detalle -> detalle.getNombreProducto() != null && detalle.getSubtotal() != null)
                .collect(Collectors.groupingBy(
                        DetalleVentaFX::getNombreProducto,
                        Collectors.reducing(BigDecimal.ZERO, DetalleVentaFX::getSubtotal, BigDecimal::add)
                ));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        ingresosPorProducto.forEach((nombreProducto, ingresoTotal) -> {
            // Solo añadir si el ingreso es mayor que cero para evitar sectores vacíos
            if (ingresoTotal.compareTo(BigDecimal.ZERO) > 0) {
                 // Mostrar nombre y valor en la etiqueta del PieChart para mejor lectura
                pieChartData.add(new PieChart.Data(nombreProducto + " (" + ingresoTotal.toString() + ")", ingresoTotal.doubleValue()));
            }
        });
        
        PieChart pieChart = new PieChart(pieChartData);
        // pieChart.setTitle("Contribución de Productos a Ingresos"); // El título se maneja con Label
        pieChart.setLegendVisible(true); // Puede ser útil si hay muchos productos
        // Considerar ocultar etiquetas de sectores si son demasiados y se superponen
        // pieChart.setLabelsVisible(false); 
        return pieChart;
    }


    // Método para el botón de exportar (a implementar)
    @FXML
    private void handleExportarGraficos() {
        System.out.println("Exportar gráficos presionado. Funcionalidad pendiente.");
        // Aquí iría la lógica para tomar snapshots de los contenedores o generar reportes.
    }
}
