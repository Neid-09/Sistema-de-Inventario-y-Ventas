package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VenderControllerFX implements Initializable {

    private final IProductoService productoService;

    @FXML
    private Button btnProcesarVenta;
    @FXML
    private Button btnProductoComun;
    @FXML
    private Button btnBuscarVenta;
    @FXML
    private Button btnNuevaVenta;

    @FXML
    private TextField txtBusqueda;

    @FXML
    private FlowPane productosBox;

    @FXML
    private VBox carritoBox;

    @FXML
    private Label lblSubtotal;
    @FXML
    private Label lblIVA;
    @FXML
    private Label lblTotalGeneral;
    @FXML
    private Label lblItemsCarrito;
    
    @FXML
    private VBox panelResultadosBusqueda;
    
    @FXML
    private VBox contenedorResultados;
    
    @FXML
    private TableView<ProductoFX> tablaProductos;
    
    @FXML
    private TableColumn<ProductoFX, String> colCodigo;
    
    @FXML
    private TableColumn<ProductoFX, String> colProducto;
    
    @FXML
    private TableColumn<ProductoFX, String> colPrecio;
    
    @FXML
    private TableColumn<ProductoFX, Integer> colCantidad;
    
    @FXML
    private TableColumn<ProductoFX, String> colSubtotal;
    
    @FXML
    private TableColumn<ProductoFX, Void> colAcciones;

    private List<ProductoFX> todosLosProductos;
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    public VenderControllerFX(IProductoService productoService) {
        this.productoService = productoService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        formatoMoneda.setMaximumFractionDigits(2);
        
        // Ocultar el panel de resultados de búsqueda inicialmente
        panelResultadosBusqueda.setVisible(false);
        panelResultadosBusqueda.setManaged(false);
        
        // Configurar la tabla de productos
        configurarTabla();
        
        // Configurar evento para cerrar el panel de resultados al hacer clic fuera de él
        txtBusqueda.focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (!newVal) { // Si pierde el foco
                // Esperar un poco antes de ocultar para permitir la selección
                new Thread(() -> {
                    try {
                        Thread.sleep(200);
                        javafx.application.Platform.runLater(() -> {
                            panelResultadosBusqueda.setVisible(false);
                            panelResultadosBusqueda.setManaged(false);
                        });
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            }
        });
        
        try {
            cargarProductos();
        } catch (Exception e) {
            mostrarError("Error al cargar productos", "No se pudieron cargar los productos: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Configurar acciones de los botones
        if (btnProductoComun != null) {
            btnProductoComun.setOnAction(e -> mostrarAlerta("Funcionalidad no implementada", "El botón 'Producto Común' aún no está implementado."));
        }
        if (btnBuscarVenta != null) {
            btnBuscarVenta.setOnAction(e -> mostrarAlerta("Funcionalidad no implementada", "El botón 'Buscar Venta' aún no está implementado."));
        }
        if (btnNuevaVenta != null) {
            btnNuevaVenta.setOnAction(e -> limpiarVenta());
        }
    }
    
    /**
     * Configura las columnas de la tabla de productos
     */
    private void configurarTabla() {
        // Configurar columnas de la tabla
        colCodigo.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getCodigo()));
        colProducto.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNombre()));
        colPrecio.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(formatoMoneda.format(cellData.getValue().getPrecioVenta())));
        
        // Configurar columna de cantidad con Spinner
        colCantidad.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(1));
        colCantidad.setCellFactory(col -> new TableCell<ProductoFX, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ProductoFX producto = getTableView().getItems().get(getIndex());
                    Spinner<Integer> spinner = new Spinner<>(1, producto.getStock(), 1);
                    spinner.setPrefWidth(80);
                    spinner.valueProperty().addListener((obs, oldVal, newVal) -> {
                        actualizarTotalCarrito();
                    });
                    setGraphic(spinner);
                }
            }
        });
        
        // Configurar columna de subtotal
        colSubtotal.setCellValueFactory(cellData -> {
            // Esto es simplificado, en realidad deberías obtener la cantidad del spinner
            BigDecimal precio = cellData.getValue().getPrecioVenta();
            return new javafx.beans.property.SimpleStringProperty(formatoMoneda.format(precio));
        });
        
        // Configurar columna de acciones
        colAcciones.setCellFactory(col -> new TableCell<ProductoFX, Void>() {
            private final Button btnEliminar = new Button("X");
            {
                btnEliminar.getStyleClass().add("button-eliminar-item");
                btnEliminar.setOnAction(event -> {
                    ProductoFX producto = getTableView().getItems().get(getIndex());
                    tablaProductos.getItems().remove(producto);
                    actualizarTotalCarrito();
                });
            }
            
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnEliminar);
                }
            }
        });
    }

    private void cargarProductos() throws Exception {
        todosLosProductos = productoService.obtenerTodos().stream()
                .filter(ProductoFX::getEstado)
                .collect(Collectors.toList());
    }

    @FXML
    void buscarProductos(KeyEvent event) {
        String textoBusqueda = "";
        if (txtBusqueda != null && txtBusqueda.getText() != null) {
            textoBusqueda = txtBusqueda.getText().toLowerCase().trim();
        }

        // Limpiar el contenedor de resultados
        contenedorResultados.getChildren().clear();

        if (todosLosProductos == null) {
            mostrarError("Error", "La lista de productos no ha sido cargada.");
            return;
        }

        // Si no hay texto de búsqueda, ocultar el panel de resultados
        if (textoBusqueda.isEmpty()) {
            panelResultadosBusqueda.setVisible(false);
            panelResultadosBusqueda.setManaged(false);
            return;
        }

        // Filtrar productos según el texto de búsqueda
        String finalTextoBusqueda = textoBusqueda;
        List<ProductoFX> resultados = todosLosProductos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(finalTextoBusqueda) ||
                             String.valueOf(p.getCodigo()).toLowerCase().contains(finalTextoBusqueda) ||
                             String.valueOf(p.getIdProducto()).toLowerCase().contains(finalTextoBusqueda))
                .limit(10) // Limitar a 10 resultados para no sobrecargar la UI
                .collect(Collectors.toList());

        // Si hay resultados, mostrar el panel y agregar los productos
        if (!resultados.isEmpty()) {
            resultados.forEach(this::agregarProductoAResultadosBusqueda);
            panelResultadosBusqueda.setVisible(true);
            panelResultadosBusqueda.setManaged(true);
        } else {
            // Si no hay resultados, mostrar un mensaje
            Label noResultados = new Label("No se encontraron productos");
            noResultados.getStyleClass().add("search-no-results");
            contenedorResultados.getChildren().add(noResultados);
            panelResultadosBusqueda.setVisible(true);
            panelResultadosBusqueda.setManaged(true);
        }
    }

    /**
     * Agrega un producto al panel de resultados de búsqueda con los nuevos estilos
     */
    private void agregarProductoAResultadosBusqueda(ProductoFX producto) {
        HBox itemResultado = new HBox();
        itemResultado.setSpacing(10);
        itemResultado.setPadding(new Insets(5));
        itemResultado.setAlignment(Pos.CENTER_LEFT);
        itemResultado.getStyleClass().add("resultado-item");
        
        // Contenedor para la información del producto
        VBox infoProducto = new VBox();
        infoProducto.setSpacing(2);
        HBox.setHgrow(infoProducto, Priority.ALWAYS);
        
        // Nombre del producto
        Label lblNombre = new Label(producto.getNombre());
        lblNombre.getStyleClass().add("resultado-nombre");
        
        // Código del producto
        Label lblCodigo = new Label("Código: " + producto.getCodigo());
        lblCodigo.getStyleClass().add("resultado-codigo");
        
        infoProducto.getChildren().addAll(lblNombre, lblCodigo);
        
        // Precio y existencias
        VBox precioStock = new VBox();
        precioStock.setAlignment(Pos.CENTER_RIGHT);
        precioStock.setSpacing(2);
        
        Label lblPrecio = new Label(formatoMoneda.format(producto.getPrecioVenta()));
        lblPrecio.getStyleClass().add("resultado-precio");
        
        Label lblStock = new Label("Existencias: " + producto.getStock());
        lblStock.getStyleClass().add("resultado-stock");
        
        precioStock.getChildren().addAll(lblPrecio, lblStock);
        
        // Botón para agregar el producto
        Button btnAgregar = new Button("Agregar");
        btnAgregar.getStyleClass().add("button-agregar-resultado");
        btnAgregar.setOnAction(e -> {
            agregarAlCarrito(producto);
            panelResultadosBusqueda.setVisible(false);
            panelResultadosBusqueda.setManaged(false);
            txtBusqueda.clear();
        });
        
        // Agregar todos los elementos al contenedor principal
        itemResultado.getChildren().addAll(infoProducto, precioStock, btnAgregar);
        
        // Agregar el item al contenedor de resultados
        contenedorResultados.getChildren().add(itemResultado);
    }

    private void agregarAlCarrito(ProductoFX producto) {
        if (producto.getStock() <= 0) {
            mostrarError("Stock Agotado", "No hay stock disponible para " + producto.getNombre());
            return;
        }

        // Agregar el producto a la tabla
        tablaProductos.getItems().add(producto);
        
        // Actualizar el total y el contador de items
        actualizarTotalCarrito();
        
        // Mostrar mensaje de confirmación
        mostrarAlerta("Producto agregado", producto.getNombre() + " ha sido agregado a la venta.");
    }

    private void actualizarTotalCarrito() {
        BigDecimal subtotal = BigDecimal.ZERO;
        int cantidadItems = 0;

        // Calcular totales basados en los productos de la tabla
        for (ProductoFX producto : tablaProductos.getItems()) {
            // Por ahora, asumimos cantidad 1 para cada producto
            // En una implementación completa, obtendrías la cantidad del spinner
            int cantidad = 1;
            
            // Calcular subtotal del item
            BigDecimal precioUnitario = producto.getPrecioVenta();
            BigDecimal subtotalItem = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            subtotal = subtotal.add(subtotalItem);
            cantidadItems += cantidad;
        }

        // Calcular IVA (12%)
        BigDecimal iva = subtotal.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(iva);

        // Actualizar labels
        lblSubtotal.setText(formatoMoneda.format(subtotal));
        lblIVA.setText(formatoMoneda.format(iva));
        lblTotalGeneral.setText(formatoMoneda.format(total));
        lblItemsCarrito.setText(cantidadItems + " productos en la venta");
    }
    
    private void mostrarError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void procesarVenta() {
        if (carritoBox.getChildren().isEmpty()) {
            mostrarAlerta("Carrito Vacío", "Agrega productos al carrito antes de procesar la venta.");
            return;
        }
        mostrarAlerta("Procesar Venta", "Funcionalidad de procesar venta aún no implementada completamente.");
    }
    
    private void limpiarVenta() {
        carritoBox.getChildren().clear();
        actualizarTotalCarrito();
        if (txtBusqueda != null) {
            txtBusqueda.clear();
        }
        buscarProductos(null);
        mostrarAlerta("Nueva Venta", "Se ha iniciado una nueva venta. El carrito está vacío.");
    }
}