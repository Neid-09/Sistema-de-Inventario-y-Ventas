package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.geometry.Pos;

import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.text.NumberFormat;
import java.util.Locale;
import java.math.BigDecimal;

import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ResumenInventarioDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ProductoStockCriticoDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ProductoSobrestockDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IReporteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.ReporteServiceImplFX;

public class inicioMenuPControllerFX implements Initializable { // Elementos de la interfaz
    @FXML
    private Label lblFecha;
    @FXML
    private Label lblUltimaActualizacion;
    @FXML
    private Button btnActualizar;

    // Métricas principales
    @FXML
    private Label lblTotalProductos;
    @FXML
    private Label lblProductosActivos;
    @FXML
    private Label lblTotalCategorias;

    // Valoración del inventario
    @FXML
    private Label lblValorTotalCosto;
    @FXML
    private Label lblValorTotalVenta;
    @FXML
    private Label lblMargenGanancia;

    // Alertas de stock
    @FXML
    private Label lblStockCritico;
    @FXML
    private Label lblSobrestock;
    @FXML
    private Label lblDetalleStockCritico;
    @FXML
    private Label lblDetalleSobrestock;
    @FXML
    private ProgressBar progressStockCritico;    @FXML
    private ProgressBar progressSobrestock;
    
    // Listas de productos problemáticos - ScrollPanes
    @FXML
    private ScrollPane scrollStockCritico;
    @FXML
    private ScrollPane scrollSobrestock;
    @FXML
    private VBox vboxStockCritico;
    @FXML
    private VBox vboxSobrestock;
    // Estado del sistema
    @FXML
    private Label lblEstadoSistemaHeader;    private final IReporteService reporteService;
    private volatile boolean actualizandoDatos = false;
    private volatile boolean sistemaEnLinea = true;
    
    // Formatter para moneda colombiana
    private final NumberFormat formatoMonedaColombiana;

    public inicioMenuPControllerFX() {
        this.reporteService = new ReporteServiceImplFX();
        
        // Configurar formato de moneda colombiana
        this.formatoMonedaColombiana = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
        this.formatoMonedaColombiana.setMaximumFractionDigits(0); // Sin decimales para pesos colombianos
    }
    
    /**
     * Formatea un valor BigDecimal como moneda colombiana
     * @param valor El valor a formatear
     * @return String formateado como "$1.234.567"
     */
    private String formatearMonedaColombiana(BigDecimal valor) {
        if (valor == null) {
            return "$0";
        }
        return formatoMonedaColombiana.format(valor);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarEventos();
        actualizarFecha();
        configurarListasProductos();
        cargarDatosIniciales();

        // Verificar conexión cada 30 segundos de forma ligera
        Task<Void> monitoreoTask = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                while (!isCancelled()) {
                    try {
                        Thread.sleep(30000); // 30 segundos
                        if (!actualizandoDatos) { // Solo verificar si no estamos actualizando
                            verificarConexionSistema();
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
                return null;
            }
        };

        Thread monitoreoThread = new Thread(monitoreoTask);
        monitoreoThread.setDaemon(true);
        monitoreoThread.start();
    }

    private void configurarListasProductos() {
        // Asegurar que los VBox contenedores estén correctamente configurados
        if (vboxStockCritico != null) {
            vboxStockCritico.setStyle("-fx-padding: 5; -fx-spacing: 6;");
        }

        if (vboxSobrestock != null) {
            vboxSobrestock.setStyle("-fx-padding: 5; -fx-spacing: 6;");
        }
    }

    private void configurarEventos() {
        // Configurar evento del botón actualizar
        btnActualizar.setOnAction(event -> actualizarDatos());

        // Configurar estilo hover para el botón
        btnActualizar.setOnMouseEntered(e -> btnActualizar.setStyle(
                "-fx-background-color: #0a4d8c; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 16; -fx-font-size: 13; -fx-font-weight: 500; -fx-cursor: hand;"));

        btnActualizar.setOnMouseExited(e -> btnActualizar.setStyle(
                "-fx-background-color: #083671; -fx-text-fill: white; -fx-background-radius: 8; -fx-padding: 10 16; -fx-font-size: 13; -fx-font-weight: 500; -fx-cursor: hand;"));
    }

    private void actualizarFecha() {
        LocalDateTime ahora = LocalDateTime.now();
        DateTimeFormatter formatterFecha = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter formatterHora = DateTimeFormatter.ofPattern("HH:mm");

        Platform.runLater(() -> {
            lblFecha.setText("Fecha: " + ahora.format(formatterFecha) + " - " + ahora.format(formatterHora));
        });
    }

    private void cargarDatosIniciales() {
        // Mostrar datos de carga inicial
        Platform.runLater(() -> {
            lblTotalProductos.setText("...");
            lblProductosActivos.setText("...");
            lblTotalCategorias.setText("...");
            lblValorTotalCosto.setText("Cargando...");
            lblValorTotalVenta.setText("Cargando...");
            lblMargenGanancia.setText("Cargando...");
            lblStockCritico.setText("...");
            lblSobrestock.setText("...");

            // Mostrar estado inicial del sistema
            actualizarEstadoSistema();
        });

        // Cargar datos reales
        actualizarDatos();
    }

    private void actualizarDatos() {
        if (actualizandoDatos) {
            return; // Evitar múltiples actualizaciones simultáneas
        }

        actualizandoDatos = true;
        Platform.runLater(() -> {
            btnActualizar.setText("🔄 Actualizando...");
            btnActualizar.setDisable(true);
        });

        // Crear task para cargar datos en background
        Task<Void> task = new Task<Void>() {
            @Override
            protected Void call() throws Exception {
                try {
                    // Cargar datos del resumen del inventario
                    cargarResumenInventario();

                    // Cargar productos con stock crítico
                    cargarProductosStockCritico();

                    // Cargar productos con sobrestock
                    cargarProductosSobrestock();

                    // Actualizar timestamp
                    Platform.runLater(() -> {
                        LocalDateTime ahora = LocalDateTime.now();
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
                        lblUltimaActualizacion.setText("Última actualización: " + ahora.format(formatter));
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        mostrarErrorEnUI("Error al cargar datos: " + e.getMessage());
                    });
                } finally {
                    Platform.runLater(() -> {
                        btnActualizar.setText("🔄 Actualizar");
                        btnActualizar.setDisable(false);
                        actualizandoDatos = false;
                    });
                }
                return null;
            }
        };

        // Ejecutar task en background
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.start();
    }

    private void cargarResumenInventario() {
        try {
            CompletableFuture<ResumenInventarioDTO> future = reporteService.obtenerResumenInventario();
            ResumenInventarioDTO resumen = future.get(); // Bloquear hasta obtener resultado

            sistemaEnLinea = true; // Conexión exitosa

            Platform.runLater(() -> {
                if (resumen != null) {
                    // Actualizar métricas principales
                    lblTotalProductos.setText(String.valueOf(resumen.getTotalProductos()));
                    lblProductosActivos.setText(String.valueOf(resumen.getProductosActivos()));
                    lblTotalCategorias.setText(String.valueOf(resumen.getTotalCategorias()));                    // Actualizar valoración financiera
                    lblValorTotalCosto.setText(formatearMonedaColombiana(resumen.getValorTotalCosto()));
                    lblValorTotalVenta.setText(formatearMonedaColombiana(resumen.getValorTotalVenta()));
                    lblMargenGanancia.setText(formatearMonedaColombiana(
                            resumen.getValorTotalVenta().subtract(resumen.getValorTotalCosto())));

                    // Actualizar estado del sistema
                    actualizarEstadoSistema();
                } else {
                    sistemaEnLinea = false;
                    mostrarErrorEnUI("No se pudieron cargar los datos del resumen");
                    actualizarEstadoSistema();
                }
            });
        } catch (Exception e) {
            sistemaEnLinea = false; // Error de conexión
            Platform.runLater(() -> {
                mostrarErrorEnUI("Error al cargar resumen: " + e.getMessage());
                actualizarEstadoSistema();
            });
        }
    }

    private void cargarProductosStockCritico() {
        try {
            CompletableFuture<List<ProductoStockCriticoDTO>> future = reporteService.obtenerProductosStockCritico();

            List<ProductoStockCriticoDTO> productos = future.get();

            Platform.runLater(() -> {
                if (productos != null) {
                    int totalProductosCriticos = productos.size();
                    lblStockCritico.setText(String.valueOf(totalProductosCriticos));

                    // Calcular progreso basado en urgencia (ejemplo: si hay más de 10 productos
                    // críticos = 100%)
                    double progreso = Math.min(1.0, totalProductosCriticos / 10.0);
                    progressStockCritico.setProgress(progreso);

                    lblDetalleStockCritico.setText(
                            totalProductosCriticos > 0
                                    ? totalProductosCriticos + " productos requieren reabastecimiento"
                                    : "No hay productos con stock crítico");

                    // Poblar lista de productos críticos
                    poblarListaStockCritico(productos);
                } else {
                    lblStockCritico.setText("0");
                    lblDetalleStockCritico.setText("Error al cargar datos");
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                lblStockCritico.setText("Error");
                lblDetalleStockCritico.setText("Error: " + e.getMessage());
            });
        }
    }

    private void cargarProductosSobrestock() {
        try {
            CompletableFuture<List<ProductoSobrestockDTO>> future = reporteService.obtenerProductosSobrestock();

            List<ProductoSobrestockDTO> productos = future.get();

            Platform.runLater(() -> {
                if (productos != null) {
                    int totalProductosSobrestock = productos.size();
                    lblSobrestock.setText(String.valueOf(totalProductosSobrestock));

                    // Calcular progreso basado en exceso
                    double progreso = Math.min(1.0, totalProductosSobrestock / 15.0);
                    progressSobrestock.setProgress(progreso);

                    lblDetalleSobrestock.setText(
                            totalProductosSobrestock > 0
                                    ? totalProductosSobrestock + " productos en exceso de inventario"
                                    : "No hay productos en sobrestock");

                    // Poblar lista de productos con sobrestock
                    poblarListaSobrestock(productos);
                } else {
                    lblSobrestock.setText("0");
                    lblDetalleSobrestock.setText("Error al cargar datos");
                }
            });
        } catch (Exception e) {
            Platform.runLater(() -> {
                lblSobrestock.setText("Error");
                lblDetalleSobrestock.setText("Error: " + e.getMessage());
            });
        }
    }

    private void poblarListaStockCritico(List<ProductoStockCriticoDTO> productos) {
        vboxStockCritico.getChildren().clear();

        if (productos.isEmpty()) {
            Label sinDatos = new Label("✅ No hay productos con stock crítico");
            sinDatos.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 13; -fx-padding: 10;");
            vboxStockCritico.getChildren().add(sinDatos);
            return;
        } // Mostrar máximo 4 productos para optimizar espacio sin scroll
        List<ProductoStockCriticoDTO> productosLimitados = productos.stream()
                .limit(4)
                .toList();

        for (ProductoStockCriticoDTO producto : productosLimitados) {
            VBox itemProducto = crearItemStockCritico(producto);
            vboxStockCritico.getChildren().add(itemProducto);
        }

        if (productos.size() > 4) {
            HBox masProductos = new HBox();
            masProductos.setAlignment(Pos.CENTER);
            masProductos.setStyle(
                    "-fx-padding: 8; -fx-background-color: #f8fafc; -fx-background-radius: 6; -fx-border-color: #e2e8f0; -fx-border-radius: 6; -fx-border-width: 1;");

            Label textoMas = new Label("+" + (productos.size() - 4) + " productos más");
            textoMas.setStyle("-fx-text-fill: #64748b; -fx-font-size: 11; -fx-font-weight: 500;");

            masProductos.getChildren().add(textoMas);
            vboxStockCritico.getChildren().add(masProductos);
        }
    }

    private void poblarListaSobrestock(List<ProductoSobrestockDTO> productos) {
        vboxSobrestock.getChildren().clear();

        if (productos.isEmpty()) {
            Label sinDatos = new Label("✅ No hay productos en sobrestock");
            sinDatos.setStyle("-fx-text-fill: #22c55e; -fx-font-size: 13; -fx-padding: 10;");
            vboxSobrestock.getChildren().add(sinDatos);
            return;
        } // Mostrar máximo 4 productos para optimizar espacio
        List<ProductoSobrestockDTO> productosLimitados = productos.stream()
                .limit(4)
                .toList();

        for (ProductoSobrestockDTO producto : productosLimitados) {
            VBox itemProducto = crearItemSobrestock(producto);
            vboxSobrestock.getChildren().add(itemProducto);
        }

        if (productos.size() > 4) {
            HBox masProductos = new HBox();
            masProductos.setAlignment(Pos.CENTER);
            masProductos.setStyle(
                    "-fx-padding: 8; -fx-background-color: #fffbf0; -fx-background-radius: 6; -fx-border-color: #fed7aa; -fx-border-radius: 6; -fx-border-width: 1;");

            Label textoMas = new Label("+" + (productos.size() - 4) + " productos más");
            textoMas.setStyle("-fx-text-fill: #d97706; -fx-font-size: 11; -fx-font-weight: 500;");

            masProductos.getChildren().add(textoMas);
            vboxSobrestock.getChildren().add(masProductos);
        }
    }    private VBox crearItemStockCritico(ProductoStockCriticoDTO producto) {
        VBox item = new VBox(5);
        item.setStyle(
                "-fx-background-color: #fef2f2; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #fecaca; -fx-border-radius: 8; -fx-border-width: 1;");

        // Línea superior: código y nombre del producto, estado crítico
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label codigo = new Label("[" + producto.getCodigo() + "]");
        codigo.setStyle("-fx-font-size: 11; -fx-text-fill: #64748b; -fx-font-weight: 500;");

        Label nombre = new Label(producto.getNombre());
        nombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-text-fill: #dc2626;");

        Label estadoCritico = new Label("CRÍTICO");
        estadoCritico.setStyle(
                "-fx-background-color: #dc2626; -fx-text-fill: white; -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 10; -fx-font-weight: bold;");

        header.getChildren().addAll(codigo, nombre, estadoCritico);
        
        // Línea inferior: stock actual vs mínimo
        HBox detalles = new HBox(15);
        detalles.setAlignment(Pos.CENTER_LEFT);

        Label stockActual = new Label("Stock: " + producto.getStock());
        stockActual.setStyle("-fx-font-size: 11; -fx-text-fill: #dc2626; -fx-font-weight: 500;");

        Label stockMinimo = new Label("Mínimo: " + producto.getStockMinimo());
        stockMinimo.setStyle("-fx-font-size: 11; -fx-text-fill: #64748b;");

        detalles.getChildren().addAll(stockActual, stockMinimo);

        item.getChildren().addAll(header, detalles);
        return item;
    }    private VBox crearItemSobrestock(ProductoSobrestockDTO producto) {
        VBox item = new VBox(5);
        item.setStyle(
                "-fx-background-color: #fffbeb; -fx-padding: 12; -fx-background-radius: 8; -fx-border-color: #fed7aa; -fx-border-radius: 8; -fx-border-width: 1;");

        // Línea superior: código y nombre del producto, estado exceso
        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label codigo = new Label("[" + producto.getCodigo() + "]");
        codigo.setStyle("-fx-font-size: 11; -fx-text-fill: #64748b; -fx-font-weight: 500;");

        Label nombre = new Label(producto.getNombre());
        nombre.setStyle("-fx-font-weight: bold; -fx-font-size: 13; -fx-text-fill: #d97706;");

        Label estadoExceso = new Label("EXCESO");
        estadoExceso.setStyle(
                "-fx-background-color: #d97706; -fx-text-fill: white; -fx-padding: 2 6; -fx-background-radius: 4; -fx-font-size: 10; -fx-font-weight: bold;");

        header.getChildren().addAll(codigo, nombre, estadoExceso);
        
        // Línea inferior: stock actual vs máximo
        HBox detalles = new HBox(15);
        detalles.setAlignment(Pos.CENTER_LEFT);

        Label stockActual = new Label("Stock: " + producto.getStock());
        stockActual.setStyle("-fx-font-size: 11; -fx-text-fill: #d97706; -fx-font-weight: 500;");

        Label stockMaximo = new Label("Máximo: " + producto.getStockMaximo());
        stockMaximo.setStyle("-fx-font-size: 11; -fx-text-fill: #64748b;");

        detalles.getChildren().addAll(stockActual, stockMaximo);

        item.getChildren().addAll(header, detalles);
        return item;
    }

    private void mostrarErrorEnUI(String mensaje) {
        // Actualizar etiquetas con estado de error
        lblTotalProductos.setText("Error");
        lblProductosActivos.setText("Error");
        lblTotalCategorias.setText("Error");
        lblValorTotalCosto.setText("Error");
        lblValorTotalVenta.setText("Error");
        lblMargenGanancia.setText("Error");

        // Actualizar estado del sistema a desconectado
        sistemaEnLinea = false;
        actualizarEstadoSistema();

        // Mostrar mensaje en consola (en producción sería un logger)
        System.err.println("Dashboard Error: " + mensaje);

        // Mostrar información de error en las listas de productos
        mostrarErrorEnListas();
    }

    private void mostrarErrorEnListas() {
        // Limpiar y mostrar error en lista de stock crítico
        if (vboxStockCritico != null) {
            vboxStockCritico.getChildren().clear();
            Label errorCritico = new Label("❌ Error al cargar productos críticos");
            errorCritico.setStyle("-fx-text-fill: #ef4444; -fx-font-size: 13; -fx-padding: 10; -fx-font-weight: 500;");
            vboxStockCritico.getChildren().add(errorCritico);
        }

        // Limpiar y mostrar error en lista de sobrestock
        if (vboxSobrestock != null) {
            vboxSobrestock.getChildren().clear();
            Label errorSobrestock = new Label("❌ Error al cargar productos en sobrestock");
            errorSobrestock
                    .setStyle("-fx-text-fill: #ef4444; -fx-font-size: 13; -fx-padding: 10; -fx-font-weight: 500;");
            vboxSobrestock.getChildren().add(errorSobrestock);
        }
    } // Método público para refrescar datos desde fuera del controlador

    public void refrescarDatos() {
        actualizarDatos();
    }

    // Método para actualizar el estado del sistema
    private void actualizarEstadoSistema() {
        if (lblEstadoSistemaHeader != null) {
            if (sistemaEnLinea) {
                lblEstadoSistemaHeader.setText("🟢 Sistema en línea");
                lblEstadoSistemaHeader.setStyle("-fx-font-size: 13; -fx-text-fill: #22c55e;");
            } else {
                lblEstadoSistemaHeader.setText("🔴 Sistema desconectado");
                lblEstadoSistemaHeader.setStyle("-fx-font-size: 13; -fx-text-fill: #ef4444;");
            }
        }
    }

    // Método para verificar la conexión del sistema de forma ligera
    private void verificarConexionSistema() {
        CompletableFuture.runAsync(() -> {
            try {
                // Intentar una verificación rápida del servicio
                CompletableFuture<ResumenInventarioDTO> future = reporteService.obtenerResumenInventario();
                // Timeout corto para verificación rápida
                future.get(5, java.util.concurrent.TimeUnit.SECONDS);

                // Solo actualizar si hay cambio en el estado
                boolean estadoAnterior = sistemaEnLinea;
                sistemaEnLinea = true;

                Platform.runLater(() -> {
                    if (sistemaEnLinea != estadoAnterior) {
                        actualizarEstadoSistema();
                    }
                });

            } catch (Exception e) {
                boolean estadoAnterior = sistemaEnLinea;
                sistemaEnLinea = false;

                Platform.runLater(() -> {
                    if (estadoAnterior != sistemaEnLinea) {
                        actualizarEstadoSistema();
                    }
                });
            }
        });
    }
}