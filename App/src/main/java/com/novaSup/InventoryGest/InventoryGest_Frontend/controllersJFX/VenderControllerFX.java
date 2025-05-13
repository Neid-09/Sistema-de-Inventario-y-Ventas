package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
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

    private List<ProductoFX> todosLosProductos;
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    public VenderControllerFX(IProductoService productoService) {
        this.productoService = productoService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        formatoMoneda.setMaximumFractionDigits(2);
        try {
            cargarProductos();
            buscarProductos(null);
        } catch (Exception e) {
            mostrarError("Error al cargar productos", "No se pudieron cargar los productos: " + e.getMessage());
            e.printStackTrace();
        }
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

        productosBox.getChildren().clear();

        if (todosLosProductos == null) {
            mostrarError("Error", "La lista de productos no ha sido cargada.");
            return;
        }

        String finalTextoBusqueda = textoBusqueda;
        todosLosProductos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(finalTextoBusqueda) ||
                             String.valueOf(p.getCodigo()).toLowerCase().contains(finalTextoBusqueda) ||
                             String.valueOf(p.getIdProducto()).toLowerCase().contains(finalTextoBusqueda))
                .forEach(this::agregarProductoAResultados);
    }

    private void agregarProductoAResultados(ProductoFX producto) {
        VBox productoCard = new VBox();
        productoCard.getStyleClass().add("producto-item-card");
        productoCard.setPrefWidth(200);
        productoCard.setMinWidth(Region.USE_PREF_SIZE);
        productoCard.setMaxWidth(Region.USE_PREF_SIZE);

        StackPane imagePlaceholder = new StackPane();
        imagePlaceholder.setPrefHeight(120);
        imagePlaceholder.getStyleClass().add("image-placeholder");
        imagePlaceholder.setPrefWidth(Double.MAX_VALUE);

        VBox contentVBox = new VBox(5);
        contentVBox.setPadding(new Insets(8, 10, 10, 10));
        VBox.setVgrow(contentVBox, Priority.ALWAYS);

        HBox namePriceHBox = new HBox();
        Label lblNombre = new Label(producto.getNombre());
        lblNombre.getStyleClass().add("label-nombre-card");
        lblNombre.setWrapText(true);
        HBox.setHgrow(lblNombre, Priority.SOMETIMES);

        Region namePriceSpacer = new Region();
        HBox.setHgrow(namePriceSpacer, Priority.ALWAYS);

        Label lblPrecio = new Label(formatoMoneda.format(producto.getPrecioVenta()));
        lblPrecio.getStyleClass().add("label-precio-card");
        namePriceHBox.getChildren().addAll(lblNombre, namePriceSpacer, lblPrecio);

        Label lblCategoria = new Label(producto.getCategoria());
        lblCategoria.getStyleClass().add("label-categoria-tag");
        HBox categoriaContainer = new HBox(lblCategoria);
        categoriaContainer.setPadding(new Insets(4, 0, 6, 0));

        Label lblCodigo = new Label("Cód: " + producto.getCodigo());
        lblCodigo.getStyleClass().add("label-codigo-card");
        HBox codigoContainer = new HBox(lblCodigo);
        codigoContainer.setPadding(new Insets(0, 0, 4, 0));

        Region contentSpacer = new Region();
        VBox.setVgrow(contentSpacer, Priority.ALWAYS);

        Button btnAgregar = new Button("Agregar");
        btnAgregar.getStyleClass().add("button-agregar-card");
        btnAgregar.setMaxWidth(Double.MAX_VALUE);
        btnAgregar.setOnAction(e -> agregarAlCarrito(producto));

        contentVBox.getChildren().addAll(namePriceHBox, categoriaContainer, codigoContainer, contentSpacer, btnAgregar);
        productoCard.getChildren().addAll(imagePlaceholder, contentVBox);

        productosBox.getChildren().add(productoCard);
    }

    private void agregarAlCarrito(ProductoFX producto) {
        boolean productoYaEnCarrito = carritoBox.getChildren().stream()
            .anyMatch(node -> node.getUserData() instanceof ProductoFX && ((ProductoFX)node.getUserData()).getIdProducto() == producto.getIdProducto());

        if (productoYaEnCarrito) {
            mostrarAlerta("Producto ya en carrito", producto.getNombre() + " ya ha sido agregado.");
            return;
        }
        
        if (producto.getStock() <= 0) {
            mostrarError("Stock Agotado", "No hay stock disponible para " + producto.getNombre());
            return;
        }

        HBox itemCarrito = new HBox(10);
        itemCarrito.setUserData(producto);
        itemCarrito.setAlignment(Pos.CENTER_LEFT);
        itemCarrito.setPadding(new Insets(5));
        itemCarrito.getStyleClass().add("carrito-item");

        Label nombreProductoCarrito = new Label(producto.getNombre());
        nombreProductoCarrito.setMaxWidth(150);
        nombreProductoCarrito.setWrapText(true);

        Spinner<Integer> cantidadSpinner = new Spinner<>(1, producto.getStock(), 1);
        cantidadSpinner.setPrefWidth(70);
        cantidadSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
            actualizarTotalCarrito(); 
        });

        Label precioUnitarioLabel = new Label(formatoMoneda.format(producto.getPrecioVenta()));

        Button btnEliminar = new Button("X");
        btnEliminar.getStyleClass().add("button-eliminar-item");
        btnEliminar.setOnAction(e -> eliminarDelCarrito(itemCarrito));

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        itemCarrito.getChildren().addAll(nombreProductoCarrito, spacer, cantidadSpinner, precioUnitarioLabel, btnEliminar);
        carritoBox.getChildren().add(itemCarrito);

        actualizarTotalCarrito();
    }

    @SuppressWarnings("unchecked")
    private Spinner<Integer> encontrarSpinnerEnItem(HBox itemCarrito) {
        return itemCarrito.getChildren().stream()
                .filter(node -> node instanceof Spinner)
                .map(node -> (Spinner<Integer>) node)
                .findFirst().orElse(null);
    }

    private void eliminarDelCarrito(Node itemNode) {
        carritoBox.getChildren().remove(itemNode);
        actualizarTotalCarrito();
    }

    private void actualizarTotalCarrito() {
        BigDecimal subtotal = BigDecimal.ZERO;
        int totalItems = 0;

        for (Node node : carritoBox.getChildren()) {
            if (node instanceof HBox && node.getUserData() instanceof ProductoFX) {
                HBox itemCarrito = (HBox) node;
                ProductoFX producto = (ProductoFX) itemCarrito.getUserData();
                Spinner<Integer> cantidadSpinner = encontrarSpinnerEnItem(itemCarrito);
                int cantidad = (cantidadSpinner != null) ? cantidadSpinner.getValue() : 0;

                subtotal = subtotal.add(producto.getPrecioVenta().multiply(new BigDecimal(cantidad)));
                totalItems += cantidad;
            }
        }

        BigDecimal iva = subtotal.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalGeneral = subtotal.add(iva);

        lblSubtotal.setText(formatoMoneda.format(subtotal));
        lblIVA.setText(formatoMoneda.format(iva));
        lblTotalGeneral.setText(formatoMoneda.format(totalGeneral));
        lblItemsCarrito.setText(totalItems + (totalItems == 1 ? " Item" : " Items"));
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