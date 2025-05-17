package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoVentaInfo;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IClienteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;

import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.stage.Popup;
import javafx.stage.Window;

import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VenderControllerFX implements Initializable {

    private final IProductoService productoService;
    private final IVentaSerivice ventaService;
    private final IClienteService clienteService;

    @FXML
    private Button btnProcesarVenta;
    @FXML
    private Button btnProductoComun;
    @FXML
    private Button btnBuscarVenta;
    @FXML
    private Button btnNuevaVenta;
    @FXML
    private Button btnCancelarVenta;

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

    private Popup popupSugerencias;
    private VBox contenedorSugerencias;

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

    public VenderControllerFX(IProductoService productoService, IVentaSerivice ventaService, IClienteService clienteService) {
        this.clienteService = clienteService;
        this.productoService = productoService;
        this.ventaService = ventaService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Cargar productos al inicio
            cargarProductos();
            // Aplicar estilos explícitamente a elementos principales para asegurar que se carguen
            // correctamente incluso si no se están aplicando desde el FXML
            if (tablaProductos != null) {
                tablaProductos.getStyleClass().add("productos-table");
            }
            
            // Configurar acción para el botón Cancelar Venta
            if (btnCancelarVenta != null) {
                btnCancelarVenta.setOnAction(event -> cancelarVenta());
            }
            
            // Configurar acción para el botón Procesar Venta
            if (btnProcesarVenta != null) {
                btnProcesarVenta.setOnAction(event -> procesarVenta());
            }
            
            // La escena puede no estar disponible en initialize, así que usamos un Platform.runLater
            // para asegurarnos de que esté inicializada completamente
            Platform.runLater(() -> {
                if (txtBusqueda.getScene() != null) {
                    txtBusqueda.getScene().getWindow().setOnCloseRequest(event -> {
                        if (!tablaProductos.getItems().isEmpty()) {
                            event.consume(); // Prevenir que la ventana se cierre automáticamente
                            confirmarSalida();
                        }
                    });
                }
            });
            
            // Inicializar el popup de sugerencias
            popupSugerencias = new Popup();
            popupSugerencias.setAutoHide(true);
            popupSugerencias.setHideOnEscape(true);
            
            // Crear un panel con fondo para el popup
            StackPane popupBackground = new StackPane();
            popupBackground.getStyleClass().add("popup-background");
            popupBackground.setStyle("-fx-background-color: white; -fx-padding: 10px; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 10, 0, 0, 3);");
            
            // Contenedor para las sugerencias
            contenedorSugerencias = new VBox(5);
            contenedorSugerencias.getStyleClass().add("sugerencias-popup");
            contenedorSugerencias.setStyle("-fx-background-color: white; -fx-padding: 8px; -fx-spacing: 5px;");
            contenedorSugerencias.setPadding(new Insets(5));
            contenedorSugerencias.setMaxWidth(600);
            contenedorSugerencias.setMinWidth(500);
            
            // Agregar un efecto de sombra al popup
            DropShadow shadow = new DropShadow();
            shadow.setRadius(5.0);
            shadow.setOffsetX(0.0);
            shadow.setOffsetY(2.0);
            shadow.setColor(javafx.scene.paint.Color.rgb(0, 0, 0, 0.2));
            popupBackground.setEffect(shadow);
            
            // Agregar el contenedor de sugerencias al panel con fondo
            popupBackground.getChildren().add(contenedorSugerencias);
            
            // Agregar el panel con fondo al popup
            popupSugerencias.getContent().add(popupBackground);
            
            // Configurar la tabla de productos
            configurarTabla();
            
            // Cargar productos
            cargarProductos();
            
            // Configurar eventos
            txtBusqueda.setOnKeyReleased(this::buscarProductos);
            
            btnProcesarVenta.setOnAction(e -> procesarVenta());
            btnNuevaVenta.setOnAction(e -> limpiarVenta());
            
            // Asegurar que los botones tengan estilos aplicados correctamente
            if (btnProcesarVenta != null) {
                btnProcesarVenta.setStyle("-fx-background-color: #28A745; -fx-text-fill: white;");
            }
            if (btnNuevaVenta != null) {
                btnNuevaVenta.setStyle("-fx-background-color: #F8F9FA; -fx-text-fill: #6C757D; -fx-border-color: #DEE2E6;");
            }
            if (btnProductoComun != null) {
                btnProductoComun.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
            }
            if (btnBuscarVenta != null) {
                btnBuscarVenta.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
            }
            if (btnNuevaVenta != null) {
                btnNuevaVenta.setStyle("-fx-background-color: #3498DB; -fx-text-fill: white;");
            }
        } catch (Exception e) {
            mostrarError("Error de inicialización", "No se pudo inicializar el módulo de ventas: " + e.getMessage());
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
        
        // Hacer que las columnas sean responsivas
        tablaProductos.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
        
        // Configurar el ancho de las columnas para que se ajusten al ancho de la tabla
        colCodigo.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.15));
        colProducto.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.30));
        colPrecio.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.15));
        colCantidad.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.15));
        colSubtotal.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.15));
        colAcciones.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.10));
        
        // Configurar columna de cantidad con control personalizado
        colCantidad.setCellValueFactory(cellData -> new javafx.beans.property.SimpleObjectProperty<>(cellData.getValue().getStock()));
        colCantidad.setCellFactory(col -> new TableCell<ProductoFX, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    ProductoFX producto = getTableView().getItems().get(getIndex());
                    
                    // Verificar el stock máximo disponible
                    int stockMaximo = obtenerStockMaximoDisponible(producto);
                    
                    // Crear control de cantidad personalizado
                    HBox cantidadControl = new HBox();
                    cantidadControl.getStyleClass().add("cantidad-control");
                    cantidadControl.setAlignment(Pos.CENTER);
                    cantidadControl.setMinHeight(30);
                    cantidadControl.setMaxHeight(30);
                    
                    Button btnMenos = new Button("-");
                    TextField txtCantidad = new TextField(String.valueOf(producto.getStock()));
                    Button btnMas = new Button("+");
                    
                    txtCantidad.setPrefWidth(40);
                    txtCantidad.setEditable(false);
                    
                    // Configurar eventos
                    btnMenos.setOnAction(e -> {
                        if (producto.getStock() > 1) {
                            producto.setStock(producto.getStock() - 1);
                            txtCantidad.setText(String.valueOf(producto.getStock()));
                            actualizarTotalCarrito();
                        }
                    });
                    
                    btnMas.setOnAction(e -> {
                        // Verificar si hay stock disponible para aumentar
                        if (producto.getStock() < stockMaximo) {
                            producto.setStock(producto.getStock() + 1);
                            txtCantidad.setText(String.valueOf(producto.getStock()));
                            actualizarTotalCarrito();
                        } else {
                            mostrarAlerta("Stock Limitado", "No hay más stock disponible para este producto. Stock máximo: " + stockMaximo);
                        }
                    });
                    
                    cantidadControl.getChildren().addAll(btnMenos, txtCantidad, btnMas);
                    setGraphic(cantidadControl);
                }
            }
        });
        
        // Configurar columna de subtotal
        colSubtotal.setCellValueFactory(cellData -> {
            BigDecimal precio = cellData.getValue().getPrecioVenta();
            int cantidad = cellData.getValue().getStock();
            BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
            return new javafx.beans.property.SimpleStringProperty(formatoMoneda.format(subtotal));
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
        final String textoBusqueda;
        if (txtBusqueda != null && txtBusqueda.getText() != null) {
            textoBusqueda = txtBusqueda.getText().toLowerCase().trim();
        } else {
            textoBusqueda = "";
        }

        // Limpiar el contenedor de sugerencias
        contenedorSugerencias.getChildren().clear();
        popupSugerencias.hide();

        if (todosLosProductos == null) {
            mostrarError("Error", "La lista de productos no ha sido cargada.");
            return;
        }

        // Si no hay texto de búsqueda, ocultar las sugerencias
        if (textoBusqueda.isEmpty()) {
            return;
        }

        // Filtrar productos que coincidan con la búsqueda
        List<ProductoFX> productosFiltrados = todosLosProductos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(textoBusqueda) || 
                        p.getCodigo().toLowerCase().contains(textoBusqueda))
                .limit(5) // Limitar a 5 resultados
                .collect(Collectors.toList());

        // Si no hay resultados, mostrar mensaje
        if (productosFiltrados.isEmpty()) {
            Label noResultados = new Label("No se encontraron productos");
            noResultados.getStyleClass().add("no-resultados-label");
            contenedorSugerencias.getChildren().add(noResultados);
        } else {
            // Crear tarjetas para cada producto filtrado
            for (ProductoFX producto : productosFiltrados) {
                HBox tarjeta = crearTarjetaProducto(producto);
                contenedorSugerencias.getChildren().add(tarjeta);
            }
        }

        // Si hay resultados, asegurarnos de que el popup tenga un fondo blanco sólido
        if (!productosFiltrados.isEmpty()) {
            // Asegurarnos de que cada tarjeta tenga la clase de estilo correcta
            for (Node node : contenedorSugerencias.getChildren()) {
                if (node instanceof HBox) {
                    HBox tarjeta = (HBox) node;
                    if (!tarjeta.getStyleClass().contains("producto-sugerido")) {
                        tarjeta.getStyleClass().add("producto-sugerido");
                    }
                }
            }
            
            // Mostrar el popup de sugerencias debajo del campo de búsqueda
            // Calcular la posición exacta para que aparezca debajo del campo de búsqueda
            double x = txtBusqueda.localToScreen(0, 0).getX();
            double y = txtBusqueda.localToScreen(0, 0).getY() + txtBusqueda.getHeight() + 2; // Pequeño espacio
            
            // Asegurarse de que el popup no se salga de la pantalla
            if (x + contenedorSugerencias.getMaxWidth() > javafx.stage.Screen.getPrimary().getVisualBounds().getMaxX()) {
                x = javafx.stage.Screen.getPrimary().getVisualBounds().getMaxX() - contenedorSugerencias.getMaxWidth();
            }
            
            popupSugerencias.show(txtBusqueda.getScene().getWindow(), x, y);
        }
    }

    /**
     * Crea una tarjeta para el producto en las sugerencias
     */
    private HBox crearTarjetaProducto(ProductoFX producto) {
        // Crear la tarjeta principal como HBox para layout horizontal
        HBox tarjeta = new HBox();
        tarjeta.getStyleClass().add("producto-sugerido");
        // Aplicar estilos directamente además de las clases CSS
        tarjeta.setStyle("-fx-background-color: white; -fx-border-color: #DEE2E6; -fx-border-radius: 4px; -fx-background-radius: 4px; -fx-padding: 10px 15px; -fx-spacing: 10px; -fx-border-width: 1px;");
        
        // Información del producto (lado izquierdo)
        VBox infoProducto = new VBox(2);
        infoProducto.getStyleClass().addAll("producto-info", "producto-info-container");
        infoProducto.setStyle("-fx-spacing: 2px; -fx-alignment: center-left; -fx-max-width: 400px; -fx-min-width: 300px; -fx-pref-width: 350px;");
        HBox.setHgrow(infoProducto, Priority.ALWAYS);
        
        // Nombre del producto
        Label lblNombre = new Label(producto.getNombre());
        lblNombre.getStyleClass().add("producto-nombre");
        lblNombre.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #212529;");
        
        // Código y precio
        Label lblCodigo = new Label("Código: " + producto.getCodigo() + " | Precio: " + formatoMoneda.format(producto.getPrecioVenta()) + " | Existencias: " + producto.getStock());
        lblCodigo.getStyleClass().add("producto-codigo");
        lblCodigo.setStyle("-fx-font-size: 11px; -fx-text-fill: #6C757D; -fx-padding: 1px 0;");
        
        infoProducto.getChildren().addAll(lblNombre, lblCodigo);
        
        // Contenedor para controles de cantidad y botón agregar (lado derecho)
        HBox accionesContainer = new HBox();
        accionesContainer.getStyleClass().add("producto-actions-container");
        accionesContainer.setStyle("-fx-spacing: 10px; -fx-alignment: center-right; -fx-min-width: 150px;");
        accionesContainer.setAlignment(Pos.CENTER);
        
        // Control de cantidad
        HBox cantidadControl = new HBox(2);
        cantidadControl.getStyleClass().add("cantidad-control-sugerencia");
        cantidadControl.setStyle("-fx-spacing: 2px; -fx-alignment: center; -fx-padding: 0;");
        cantidadControl.setAlignment(Pos.CENTER);
        cantidadControl.setMinHeight(30);
        cantidadControl.setMaxHeight(30);
        
        // Botón de disminuir
        Button btnMenos = new Button("-");
        btnMenos.setStyle("-fx-min-width: 28px; -fx-min-height: 28px; -fx-background-color: #F8F9FA; -fx-text-fill: #212529; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-border-color: #DEE2E6; -fx-border-radius: 4px; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 0;");
        
        // Campo de texto para la cantidad
        TextField txtCantidad = new TextField("1");
        txtCantidad.setPrefWidth(40);
        txtCantidad.setStyle("-fx-alignment: center; -fx-max-width: 40px; -fx-min-width: 40px; -fx-pref-width: 40px; -fx-background-color: #FFFFFF; -fx-border-color: #DEE2E6; -fx-border-radius: 4px; -fx-font-size: 12px; -fx-padding: 5px 2px;");
        txtCantidad.setEditable(false);
        
        // Botón de aumentar
        Button btnMas = new Button("+");
        btnMas.setStyle("-fx-min-width: 28px; -fx-min-height: 28px; -fx-background-color: #F8F9FA; -fx-text-fill: #212529; -fx-font-weight: bold; -fx-background-radius: 4px; -fx-border-color: #DEE2E6; -fx-border-radius: 4px; -fx-cursor: hand; -fx-font-size: 12px; -fx-padding: 0;");
        
        // Contador para la cantidad seleccionada
        final int[] cantidad = {1};
        
        // Configurar eventos de los botones
        btnMenos.setOnAction(e -> {
            e.consume(); // Evitar que el evento se propague a la tarjeta
            if (cantidad[0] > 1) {
                cantidad[0]--;
                txtCantidad.setText(String.valueOf(cantidad[0]));
            }
        });
        
        btnMas.setOnAction(e -> {
            e.consume(); // Evitar que el evento se propague a la tarjeta
            if (cantidad[0] < producto.getStock()) {
                cantidad[0]++;
                txtCantidad.setText(String.valueOf(cantidad[0]));
            } else {
                mostrarAlerta("Stock Limitado", "No hay más stock disponible para este producto.");
            }
        });
        
        cantidadControl.getChildren().addAll(btnMenos, txtCantidad, btnMas);
        
        // Botón de agregar
        Button btnAgregar = new Button("Agregar");
        btnAgregar.getStyleClass().add("btn-agregar-sugerencia");
        btnAgregar.setStyle("-fx-background-color: #0275d8; -fx-text-fill: white; -fx-font-weight: bold; -fx-min-width: 80px; -fx-min-height: 28px; -fx-background-radius: 4px; -fx-cursor: hand; -fx-font-size: 12px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);");
        btnAgregar.setMinHeight(30);
        btnAgregar.setMaxHeight(30);
        
        // Configurar acción del botón agregar
        btnAgregar.setOnAction(e -> {
            e.consume(); // Evitar que el evento se propague a la tarjeta
            agregarAlCarritoConCantidad(producto, cantidad[0]);
            popupSugerencias.hide();
            txtBusqueda.clear();
        });
        
        // Agregar controles al contenedor de acciones
        accionesContainer.getChildren().addAll(cantidadControl, btnAgregar);
        
        tarjeta.getChildren().addAll(infoProducto, accionesContainer);
        
        return tarjeta;
    }

    /**
     * Obtiene el stock máximo disponible para un producto
     * @param producto Producto para verificar stock
     * @return Stock máximo disponible
     */
    private int obtenerStockMaximoDisponible(ProductoFX producto) {
        // Buscar el producto en la lista original para obtener el stock real
        if (todosLosProductos != null) {
            for (ProductoFX p : todosLosProductos) {
                if (p.getIdProducto().equals(producto.getIdProducto())) {
                    return p.getStock();
                }
            }
        }
        // Si no se encuentra, devolver el stock actual del producto
        return producto.getStock();
    }
    
    /**
     * Agrega un producto al carrito con cantidad 1
     * @param producto Producto a agregar
     */
    private void agregarAlCarrito(ProductoFX producto) {
        agregarAlCarritoConCantidad(producto, 1);
    }
    
    /**
     * Agrega un producto al carrito con una cantidad específica
     * @param producto Producto a agregar
     * @param cantidadAgregar Cantidad a agregar
     */
    private void agregarAlCarritoConCantidad(ProductoFX producto, int cantidadAgregar) {
        // Obtener el stock máximo disponible del producto original
        int stockMaximoDisponible = obtenerStockMaximoDisponible(producto);
        
        // Validar que haya stock disponible
        if (stockMaximoDisponible <= 0) {
            mostrarError("Stock Agotado", "No hay stock disponible para " + producto.getNombre());
            return;
        }
        
        // Validar que la cantidad a agregar sea positiva
        if (cantidadAgregar <= 0) {
            mostrarError("Cantidad Inválida", "La cantidad debe ser mayor a cero");
            return;
        }
        
        // Validar que la cantidad a agregar no supere el stock disponible
        if (cantidadAgregar > stockMaximoDisponible) {
            mostrarError("Stock Insuficiente", "Solo hay " + stockMaximoDisponible + " unidades disponibles de " + producto.getNombre());
            cantidadAgregar = stockMaximoDisponible; // Limitar a stock disponible
        }
        
        // Verificar si el producto ya está en el carrito
        boolean productoExistente = false;
        for (ProductoFX p : tablaProductos.getItems()) {
            if (p.getIdProducto().equals(producto.getIdProducto())) {
                // El producto ya está en el carrito, calcular la nueva cantidad
                int cantidadActual = p.getStock();
                int nuevaCantidad = cantidadActual + cantidadAgregar;
                
                // Validar que la nueva cantidad no supere el stock disponible
                if (nuevaCantidad > stockMaximoDisponible) {
                    mostrarError("Stock Insuficiente", "No se puede agregar más unidades. Stock máximo disponible: " + stockMaximoDisponible);
                    nuevaCantidad = stockMaximoDisponible; // Limitar a stock disponible
                }
                
                p.setStock(nuevaCantidad);
                productoExistente = true;
                
                // Mostrar mensaje de confirmación
                mostrarAlerta("Cantidad actualizada", "Se ha actualizado la cantidad de " + p.getNombre() + " a " + nuevaCantidad + " unidades.");
                break;
            }
        }
        
        if (!productoExistente) {
            // Crear una copia del producto para agregar al carrito
            ProductoFX nuevoProducto = new ProductoFX();
            nuevoProducto.setIdProducto(producto.getIdProducto());
            nuevoProducto.setCodigo(producto.getCodigo());
            nuevoProducto.setNombre(producto.getNombre());
            nuevoProducto.setDescripcion(producto.getDescripcion());
            nuevoProducto.setPrecioVenta(producto.getPrecioVenta());
            nuevoProducto.setStock(cantidadAgregar); // Establecer la cantidad seleccionada
            nuevoProducto.setEstado(producto.getEstado());
            nuevoProducto.setIdCategoria(producto.getIdCategoria());
            
            // Agregar el producto a la tabla
            tablaProductos.getItems().add(nuevoProducto);
            
            // Mostrar mensaje de confirmación
            mostrarAlerta("Producto agregado", cantidadAgregar + " unidad(es) de " + nuevoProducto.getNombre() + " ha(n) sido agregada(s) a la venta.");
        }
        
        // Actualizar el total y el contador de items
        actualizarTotalCarrito();
    }

    private void actualizarTotalCarrito() {
        BigDecimal subtotal = BigDecimal.ZERO;
        int cantidadItems = 0;

        // Calcular totales basados en los productos de la tabla
        for (ProductoFX producto : tablaProductos.getItems()) {
            BigDecimal precioUnitario = producto.getPrecioVenta();
            int cantidad = producto.getStock();
            BigDecimal subtotalProducto = precioUnitario.multiply(BigDecimal.valueOf(cantidad));
            subtotal = subtotal.add(subtotalProducto);
            cantidadItems += cantidad;
        }

        // Calcular IVA (12%)
        BigDecimal iva = subtotal.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal total = subtotal.add(iva);

        // Actualizar etiquetas
        lblSubtotal.setText(formatoMoneda.format(subtotal));
        lblIVA.setText(formatoMoneda.format(iva));
        lblTotalGeneral.setText(formatoMoneda.format(total));
        lblItemsCarrito.setText(cantidadItems + " productos en la venta");

        // Refrescar la tabla
        tablaProductos.refresh();
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

    private void procesarVenta() {
        // Verificar si hay productos en el carrito
        if (tablaProductos.getItems().isEmpty()) {
            mostrarError("Carrito Vacío", "No hay productos en la venta para procesar.");
            return;
        }
        
        // Abrir la ventana para procesar la venta
        boolean ventaRealizada = abrirVentanaProcesarVenta();
        
        // Si la venta se confirmó correctamente, limpiar el carrito y actualizar productos
        if (ventaRealizada) {
            try {
                // Recargar los productos para actualizar el stock
                cargarProductos();
                
                // Limpiar la venta actual
                limpiarVenta();
                
                // Mostrar mensaje de éxito
                mostrarAlerta("Venta Procesada", "La venta ha sido procesada correctamente.");
            } catch (Exception e) {
                mostrarError("Error al Actualizar", "La venta se procesó correctamente, pero hubo un error al actualizar los productos: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Abre la ventana de diálogo para procesar la venta
     * @return true si la venta fue confirmada, false si fue cancelada
     */
    private boolean abrirVentanaProcesarVenta() {
        try {
            // Obtener la ventana actual
            Window ownerWindow = txtBusqueda.getScene().getWindow();
            
            // Cargar el FXML del diálogo
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.DIALOG_PROCESAR_VENTA));
            DialogPane dialogPane = loader.load();
            
            // Obtener el controlador
            ProcesarVentaDialogController controller = loader.getController();
            
            // Configurar el diálogo
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogPane);
            dialog.setTitle("Procesar Venta");
            dialog.initOwner(ownerWindow);
            
            // Almacenar la referencia del diálogo en el userData de la ventana del diálogo
            // para que ProcesarVentaDialogController pueda acceder a él
            dialogPane.getScene().getWindow().setUserData(dialog);
            
            // Calcular el total de la venta para mostrar en el diálogo
            BigDecimal total = BigDecimal.ZERO;
            for (ProductoFX producto : tablaProductos.getItems()) {
                BigDecimal subtotal = producto.getPrecioVenta().multiply(new BigDecimal(producto.getStock()));
                total = total.add(subtotal);
            }
            
            // Aquí se inyectan los servicios necesarios para el controlador del diálogo
            /* 
             * AQUÍ SE INYECTAN LOS SERVICIOS NECESARIOS:
             * Por ejemplo:
             * - IClienteService clienteService para buscar clientes
             * - IVentaService ventaService para registrar la venta
             * - IFacturaService facturaService para generar facturas
             * controller.setServices(clienteService, ventaService, facturaService);
             */
            controller.setServices(ventaService, clienteService);
            
            // Convertir los productos de ProductoFX a ProductoVentaInfo
            List<ProductoVentaInfo> productosInfo = convertirAProductoVentaInfo(tablaProductos.getItems());
            
            // Pasar los datos de la venta al controlador
            controller.setDatosVenta(productosInfo, total);
            
            // Mostrar el diálogo y esperar la respuesta
            return dialog.showAndWait()
                    .filter(buttonType -> buttonType.getButtonData() == ButtonBar.ButtonData.OK_DONE)
                    .isPresent();
            
        } catch (Exception e) {
            mostrarError("Error", "Ha ocurrido un error al abrir la ventana de procesamiento: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Convierte una lista de ProductoFX a una lista de ProductoVentaInfo para ser utilizada en el diálogo de procesar venta
     * @param productos Lista de productos del carrito
     * @return Lista de ProductoVentaInfo
     */
    private List<ProductoVentaInfo> convertirAProductoVentaInfo(ObservableList<ProductoFX> productos) {
        List<ProductoVentaInfo> resultado = new ArrayList<>();
        
        for (ProductoFX producto : productos) {
            // Crear una instancia de ProductoVentaInfo usando el constructor
            ProductoVentaInfo info = new ProductoVentaInfo(
                producto.getIdProducto(), 
                producto.getNombre(), 
                producto.getStock(), // La cantidad en el carrito
                producto.getPrecioVenta()
            );
            
            resultado.add(info);
        }
        
        return resultado;
    }

    private void limpiarVenta() {
        // Limpiar la tabla de productos
        tablaProductos.getItems().clear();
        
        // Actualizar totales
        actualizarTotalCarrito();
        
        // Limpiar campo de búsqueda
        txtBusqueda.clear();
    }
    
    /**
     * Cancela la venta actual después de confirmar con el usuario
     */
    private void cancelarVenta() {
        // Verificar si hay productos en el carrito
        if (tablaProductos.getItems().isEmpty()) {
            mostrarAlerta("Carrito Vacío", "No hay productos en la venta para cancelar.");
            return;
        }
        
        // Mostrar diálogo de confirmación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancelar Venta");
        alert.setHeaderText("¿Está seguro que desea cancelar la venta?");
        alert.setContentText("Se eliminarán todos los productos del carrito.");
        
        alert.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                limpiarVenta();
                mostrarAlerta("Venta Cancelada", "La venta ha sido cancelada correctamente.");
            }
        });
    }
    
    /**
     * Muestra un diálogo para confirmar la salida cuando hay productos en el carrito
     */
    private void confirmarSalida() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Confirmación");
        alert.setHeaderText("Hay productos en el carrito");
        alert.setContentText("¿Está seguro que desea salir? Se perderán los productos agregados.");
        
        // Agregar botones personalizados
        ButtonType btnSalir = new ButtonType("Salir sin guardar");
        ButtonType btnCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        
        alert.getButtonTypes().setAll(btnSalir, btnCancelar);
        
        alert.showAndWait().ifPresent(response -> {
            if (response == btnSalir) {
                // Cerrar la ventana
                txtBusqueda.getScene().getWindow().hide();
            }
        });
    }
}
