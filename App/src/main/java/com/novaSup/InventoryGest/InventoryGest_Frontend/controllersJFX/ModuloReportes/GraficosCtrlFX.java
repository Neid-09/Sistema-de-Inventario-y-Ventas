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
                "Análisis por Producto",
                "Análisis por Vendedor",
                "Análisis por Cliente" // Nueva opción
                // Añadir más tipos de análisis aquí en el futuro
        );
        cmbTipoGrafico.setItems(opciones);
    }

    private void actualizarGraficos() {
        String seleccion = cmbTipoGrafico.getValue();

        limpiarContenedor(contenedorGrafico1);
        lblTituloGrafico1.setText("");
        limpiarContenedor(contenedorGrafico2);
        lblTituloGrafico2.setText("");

        if (ventaService == null) {
            System.err.println("VentaService no está inicializado.");
            lblTituloGrafico1.setText("Error: Servicio no disponible.");
            return;
        }

        if (seleccion == null) {
            lblTituloGrafico1.setText("Por favor, seleccione un tipo de análisis.");
            return;
        }
        
        List<VentaFX> todasLasVentas;
        try {
            // Asumiendo que tienes un método para obtener todas las ventas.
            // Si necesitas detalles para todos los análisis, asegúrate que el método los cargue.
            // Por ejemplo: ventaService.obtenerTodasLasVentasConDetalles();
            // Si `obtenerTodasLasVentasConDetalles()` no existe, usa el método disponible,
            // y asegúrate que los detalles se carguen si son necesarios para los gráficos de productos.
            todasLasVentas = ventaService.listarVentas(); // O el método que tengas disponible
            if (todasLasVentas.isEmpty() && ("Análisis por Producto".equals(seleccion) || "Análisis de Ventas Generales".equals(seleccion) )) {
                 // Si se requiere análisis de productos, y las ventas no traen detalles, se podrían cargar aquí explícitamente si es necesario y posible.
                 // Esto es solo un ejemplo, la lógica exacta dependerá de tu servicio.
                 // todasLasVentas = ventaService.obtenerTodasLasVentasConDetalles();
            }

        } catch (Exception e) {
            System.err.println("Error al obtener ventas: " + e.getMessage());
            e.printStackTrace();
            lblTituloGrafico1.setText("Error al cargar datos de ventas.");
            return;
        }
        
        if (todasLasVentas == null || todasLasVentas.isEmpty()) {
            lblTituloGrafico1.setText("No hay datos de ventas disponibles para mostrar.");
            return;
        }

        switch (seleccion) {
            case "Análisis de Ventas Generales":
                lblTituloGrafico1.setText("Tendencia de Ventas en el Tiempo");
                LineChart<String, Number> tendenciaVentas = crearGraficoTendenciaVentasTiempo(todasLasVentas);
                agregarGraficoAContenedor(tendenciaVentas, contenedorGrafico1);

                lblTituloGrafico2.setText("Distribución por Tipo de Pago");
                PieChart distribucionPago = crearGraficoDistribucionTipoPago(todasLasVentas);
                agregarGraficoAContenedor(distribucionPago, contenedorGrafico2);
                break;

            case "Análisis por Producto":
                // Asegurarse de que los detalles de la venta estén cargados para este análisis
                // Si `todasLasVentas` no incluye detalles, necesitarías obtenerlos.
                // Esta es una simplificación; idealmente, `ventaService.obtenerTodasLasVentas()`
                // ya debería traer los detalles si son comúnmente necesarios, o tener un método específico.
                boolean requiereDetalles = todasLasVentas.stream().anyMatch(v -> v.getDetalles() == null || v.getDetalles().isEmpty());
                if(requiereDetalles){
                    // Aquí podrías intentar cargar los detalles para todasLasVentas si tu servicio lo permite
                    // o mostrar un mensaje indicando que los detalles no están disponibles.
                    // Por ahora, asumimos que los detalles están presentes o los métodos de gráficos los manejan.
                    System.out.println("Advertencia: Algunas ventas podrían no tener detalles cargados para el análisis de productos.");
                }

                lblTituloGrafico1.setText("Top 5 Productos Más Vendidos por Ingresos");
                BarChart<String, Number> productosMasVendidos = crearGraficoProductosMasVendidos(todasLasVentas, 5);
                agregarGraficoAContenedor(productosMasVendidos, contenedorGrafico1);

                lblTituloGrafico2.setText("Contribución de Productos a Ingresos");
                PieChart contribucionProductos = crearGraficoContribucionProductosIngresos(todasLasVentas);
                agregarGraficoAContenedor(contribucionProductos, contenedorGrafico2);
                break;

            case "Análisis por Vendedor":
                lblTituloGrafico1.setText("Rendimiento de Vendedores (Top 5)"); // Asumiendo Top 5 por defecto
                BarChart<String, Number> rendimientoVendedores = crearGraficoRendimientoVendedores(todasLasVentas, 5);
                agregarGraficoAContenedor(rendimientoVendedores, contenedorGrafico1);

                lblTituloGrafico2.setText("Tendencia de Ventas por Vendedor");
                LineChart<String, Number> tendenciaVentasVendedor = crearGraficoTendenciaVentasPorVendedor(todasLasVentas);
                agregarGraficoAContenedor(tendenciaVentasVendedor, contenedorGrafico2);
                break;
            
            case "Análisis por Cliente": // Nuevo caso
                lblTituloGrafico1.setText("Clientes Principales (Top 5)");
                BarChart<String, Number> clientesPrincipales = crearGraficoClientesPrincipales(todasLasVentas, 5);
                agregarGraficoAContenedor(clientesPrincipales, contenedorGrafico1);

                lblTituloGrafico2.setText(""); // Limpiar el título del segundo gráfico
                limpiarContenedor(contenedorGrafico2); // Limpiar el segundo contenedor si no se usa
                break;
            // Puedes añadir más casos aquí si expandes las opciones del ComboBox
            default:
                lblTituloGrafico1.setText("Tipo de análisis no reconocido.");
                break;
        }
    }
    
    private void limpiarContenedor(Pane contenedor) {
        contenedor.getChildren().clear();
    }

    private void agregarGraficoAContenedor(Node grafico, Pane contenedor) {
        if (grafico != null && contenedor != null) {
            contenedor.getChildren().add(grafico);
            // Opcional: Hacer que el gráfico se ajuste al tamaño del contenedor.
            // Esto funciona bien para Charts, pero puede necesitar ajustes para otros Nodes.
            if (grafico instanceof javafx.scene.chart.Chart) {
                ((javafx.scene.chart.Chart) grafico).prefWidthProperty().bind(contenedor.widthProperty());
                ((javafx.scene.chart.Chart) grafico).prefHeightProperty().bind(contenedor.heightProperty());
            } else if (grafico instanceof Pane) { // Si el gráfico en sí es un Pane que contiene el Chart
                 ((Pane) grafico).prefWidthProperty().bind(contenedor.widthProperty());
                 ((Pane) grafico).prefHeightProperty().bind(contenedor.heightProperty());
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

    // --- Métodos para Análisis por Vendedor ---

    private BarChart<String, Number> crearGraficoRendimientoVendedores(List<VentaFX> ventas, int topN) {
        Map<String, BigDecimal> ventasPorVendedor = ventas.stream()
                .filter(venta -> venta.getNombreVendedor() != null && !venta.getNombreVendedor().isEmpty() && venta.getTotal() != null)
                .collect(Collectors.groupingBy(
                        VentaFX::getNombreVendedor,
                        Collectors.reducing(BigDecimal.ZERO, VentaFX::getTotal, BigDecimal::add)
                ));

        // Ordenar por ventas descendentes y tomar topN
        Map<String, BigDecimal> topVendedores = ventasPorVendedor.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new
                ));

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Vendedor");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Ventas");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Ventas por Vendedor");

        topVendedores.forEach((nombreVendedor, totalVentas) -> {
            series.getData().add(new XYChart.Data<>(nombreVendedor, totalVentas));
        });

        barChart.getData().add(series);
        barChart.setLegendVisible(false);
        return barChart;
    }

    private LineChart<String, Number> crearGraficoTendenciaVentasPorVendedor(List<VentaFX> ventas) {
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Mes");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Ingresos Totales");

        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setLegendVisible(true);

        // Agrupar ventas por vendedor y luego por mes
        Map<String, Map<Month, BigDecimal>> ventasPorVendedorYMes = ventas.stream()
            .filter(venta -> venta.getNombreVendedor() != null && !venta.getNombreVendedor().isEmpty() &&
                             venta.getFecha() != null && venta.getTotal() != null)
            .collect(Collectors.groupingBy(
                VentaFX::getNombreVendedor,
                Collectors.groupingBy(
                    venta -> venta.getFecha().getMonth(),
                    Collectors.reducing(BigDecimal.ZERO, VentaFX::getTotal, BigDecimal::add)
                )
            ));

        // Crear una serie por cada vendedor
        ventasPorVendedorYMes.forEach((nombreVendedor, ventasMes) -> {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(nombreVendedor);

            java.util.Arrays.stream(Month.values()).forEach(month -> {
                BigDecimal totalMes = ventasMes.getOrDefault(month, BigDecimal.ZERO);
                series.getData().add(new XYChart.Data<>(month.getDisplayName(TextStyle.SHORT, Locale.getDefault()), totalMes));
            });
            lineChart.getData().add(series);
        });
        
        // Asegurar que todos los meses estén presentes en el eje X, incluso si no hay datos para algunos
        ObservableList<String> meses = FXCollections.observableArrayList();
        java.util.Arrays.stream(Month.values()).forEach(month -> {
            meses.add(month.getDisplayName(TextStyle.SHORT, Locale.getDefault()));
        });
        xAxis.setCategories(meses);


        return lineChart;
    }

    // --- Métodos para Análisis por Cliente ---
    private BarChart<String, Number> crearGraficoClientesPrincipales(List<VentaFX> ventas, int topN) {
        Map<String, BigDecimal> ventasPorCliente = ventas.stream()
                .filter(venta -> venta.getNombreCliente() != null && !venta.getNombreCliente().isEmpty() && venta.getTotal() != null)
                .collect(Collectors.groupingBy(
                        VentaFX::getNombreCliente,
                        Collectors.reducing(BigDecimal.ZERO, VentaFX::getTotal, BigDecimal::add)
                ));

        // Ordenar por ventas descendentes y tomar topN
        Map<String, BigDecimal> topClientes = ventasPorCliente.entrySet().stream()
                .sorted(Map.Entry.<String, BigDecimal>comparingByValue().reversed())
                .limit(topN)
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1, // En caso de colisión de claves (no debería pasar aquí)
                        LinkedHashMap::new // Para mantener el orden de inserción (ordenado)
                ));

        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Cliente");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Total Compras");

        BarChart<String, Number> barChart = new BarChart<>(xAxis, yAxis);
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Compras por Cliente");

        topClientes.forEach((nombreCliente, totalCompras) -> {
            series.getData().add(new XYChart.Data<>(nombreCliente, totalCompras));
        });

        barChart.getData().add(series);
        barChart.setLegendVisible(false); // La leyenda puede ser redundante para un solo BarChart
        return barChart;
    }


    // Método para el botón de exportar (a implementar)
    @FXML
    private void handleExportarGraficos() {
        System.out.println("Exportar gráficos presionado. Funcionalidad pendiente.");
        // Aquí iría la lógica para tomar snapshots de los contenedores o generar reportes.
    }
}
