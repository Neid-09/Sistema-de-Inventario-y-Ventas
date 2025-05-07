package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaRequest;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaResponse;
// Eliminada la importación de ProductoServiceImplFX ya que no se instancia aquí
// Eliminada la importación de VentaServiceImplFX ya que no se instancia aquí
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice; // Corregido el nombre de la interfaz

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.math.BigDecimal;
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

    @FXML
    private Button btnProcesarVenta;

    @FXML
    private TextField txtBusqueda;

    @FXML
    private VBox productosBox;

    @FXML
    private VBox carritoBox;

    @FXML
    private TextField txtTotal;

    private List<ProductoFX> todosLosProductos;
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
    private BigDecimal totalVenta = BigDecimal.ZERO;

    // Constructor único para inyección de dependencias
    public VenderControllerFX(IProductoService productoService, IVentaSerivice ventaService) {
        this.productoService = productoService;
        this.ventaService = ventaService;
    }

    // Eliminado el constructor por defecto que instanciaba los servicios directamente

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cargarProductos();
            txtTotal.setText(formatoMoneda.format(totalVenta));
        } catch (Exception e) {
            mostrarError("Error al cargar productos", e.getMessage());
        }
    }

    private void cargarProductos() throws Exception {
        todosLosProductos = productoService.obtenerTodos().stream()
                .filter(ProductoFX::getEstado)
                .collect(Collectors.toList());
    }

    @FXML
    void buscarProductos(KeyEvent event) {
        String textoBusqueda = txtBusqueda.getText().trim().toLowerCase();

        try {
            productosBox.getChildren().clear();

            if (textoBusqueda.isEmpty()) {
                return;
            }

            boolean posibleId = textoBusqueda.matches("\\d+");

            List<ProductoFX> resultados = todosLosProductos.stream()
                    .filter(p -> {
                        if (posibleId && String.valueOf(p.getIdProducto()).equals(textoBusqueda)) {
                            return true;
                        }
                        if (p.getNombre() != null && p.getNombre().toLowerCase().contains(textoBusqueda)) {
                            return true;
                        }
                        if (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(textoBusqueda)) {
                            return true;
                        }
                        return false;
                    })
                    .limit(10)
                    .collect(Collectors.toList());

            for (ProductoFX producto : resultados) {
                agregarProductoAResultados(producto);
            }

        } catch (Exception e) {
            mostrarError("Error en búsqueda", e.getMessage());
        }
    }

    private void agregarProductoAResultados(ProductoFX producto) {
        VBox productoPane = new VBox(5);
        productoPane.setPadding(new Insets(10));
        productoPane.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: white;");

        Label lblNombre = new Label(producto.getNombre());
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));

        Label lblStock = new Label("Stock: " + producto.getStock());
        Label lblPrecio = new Label("Precio: " + formatoMoneda.format(producto.getPrecioVenta()));

        Button btnAgregar = new Button("Agregar");
        btnAgregar.setOnAction(e -> agregarAlCarrito(producto));

        productoPane.getChildren().addAll(lblNombre, lblStock, lblPrecio, btnAgregar);
        productosBox.getChildren().add(productoPane);
    }

    private void agregarAlCarrito(ProductoFX producto) {
        // Guardar el ProductoFX en el UserData del item del carrito para fácil recuperación
        HBox itemCarrito = new HBox(10);
        itemCarrito.setPadding(new Insets(5));
        itemCarrito.setStyle("-fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0;");
        itemCarrito.setUserData(producto); // Guardar el producto aquí

        Label lblNombre = new Label(producto.getNombre());
        HBox.setHgrow(lblNombre, Priority.ALWAYS);

        Spinner<Integer> spCantidad = new Spinner<>(1, producto.getStock(), 1);
        spCantidad.setPrefWidth(70);

        Label lblPrecio = new Label(formatoMoneda.format(producto.getPrecioVenta()));
        lblPrecio.setMinWidth(80);

        Button btnEliminar = new Button("X");
        btnEliminar.setStyle("-fx-text-fill: red;");
        btnEliminar.setOnAction(e -> {
            carritoBox.getChildren().remove(itemCarrito);
            // Recuperar el producto del UserData
            ProductoFX productoEliminado = (ProductoFX) itemCarrito.getUserData();
            if (productoEliminado != null) {
                BigDecimal subtotal = productoEliminado.getPrecioVenta().multiply(BigDecimal.valueOf(spCantidad.getValue()));
                totalVenta = totalVenta.subtract(subtotal);
                actualizarTotal();
            }
        });

        spCantidad.valueProperty().addListener((obs, oldValue, newValue) -> {
            // Recuperar el producto del UserData
            ProductoFX productoActualizado = (ProductoFX) itemCarrito.getUserData();
            if (productoActualizado != null) {
                BigDecimal diferencia = productoActualizado.getPrecioVenta().multiply(BigDecimal.valueOf(newValue - oldValue));
                totalVenta = totalVenta.add(diferencia);
                actualizarTotal();
            }
        });

        itemCarrito.getChildren().addAll(lblNombre, spCantidad, lblPrecio, btnEliminar);
        carritoBox.getChildren().add(0, itemCarrito);

        // Recuperar el producto del UserData para el cálculo inicial
        ProductoFX productoAgregado = (ProductoFX) itemCarrito.getUserData();
        if (productoAgregado != null) {
            totalVenta = totalVenta.add(productoAgregado.getPrecioVenta().multiply(BigDecimal.valueOf(spCantidad.getValue())));
            actualizarTotal();
        }
    }

    private void actualizarTotal() {
        txtTotal.setText(formatoMoneda.format(totalVenta));
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void procesarVenta() {
        try {
            if (carritoBox.getChildren().isEmpty()) {
                mostrarError("Carrito vacío", "No hay productos en el carrito para procesar la venta.");
                return;
            }

            VentaRequest ventaRequest = new VentaRequest();
            // Estos IDs deberían obtenerse de la sesión del usuario o campos de la UI
            // Por ahora, se usarán valores placeholder o null.
            ventaRequest.setIdCliente(null); // Placeholder: obtener de la UI o sesión
            ventaRequest.setIdVendedor(1); // Placeholder: obtener de la UI o sesión (ej. ID del usuario logueado si es vendedor)
            ventaRequest.setRequiereFactura(false); // Placeholder: obtener de la UI
            ventaRequest.setAplicarImpuestos(true); // Placeholder: obtener de la UI
            // numeroVenta podría ser generado por el backend o ingresado manualmente
            ventaRequest.setNumeroVenta("VTA-" + System.currentTimeMillis());


            List<VentaRequest.DetalleVenta> detalles = new ArrayList<>();
            for (Node itemNode : carritoBox.getChildren()) {
                if (itemNode instanceof HBox) {
                    HBox itemCarrito = (HBox) itemNode;
                    ProductoFX productoEnCarrito = (ProductoFX) itemCarrito.getUserData(); // Recuperar producto
                    Node node = itemCarrito.getChildren().get(1); // Asumiendo que el Spinner es el segundo elemento
                    Spinner<Integer> spCantidad = null;
                    if (node instanceof Spinner<?>) {
                        try {
                            @SuppressWarnings("unchecked")
                            Spinner<Integer> tempSpinner = (Spinner<Integer>) node;
                            spCantidad = tempSpinner;
                        } catch (ClassCastException ex) {
                            mostrarError("Error Interno", "El componente de cantidad no es del tipo esperado.");
                            return;
                        }
                    }

                    if (productoEnCarrito != null && spCantidad != null) {
                        VentaRequest.DetalleVenta detalle = new VentaRequest.DetalleVenta();
                        detalle.setIdProducto(productoEnCarrito.getIdProducto());
                        detalle.setCantidad(spCantidad.getValue());
                        detalle.setPrecioUnitario(productoEnCarrito.getPrecioVenta());
                        detalles.add(detalle);
                    } else {
                        mostrarError("Error Interno", "No se pudo obtener la información del producto en el carrito.");
                        return;
                    }
                }
            }
            ventaRequest.setDetalles(detalles);

            // Llamar al servicio para registrar la venta
            VentaResponse ventaResponse = ventaService.registrarVenta(ventaRequest);

            // Si la venta fue exitosa (ventaResponse no es null y no hubo excepciones)
            // La actualización de stock ahora la maneja el backend.
            // Solo necesitamos recargar los productos para reflejar el stock actualizado.
            try {
                cargarProductos();
            } catch (Exception loadEx) {
                 mostrarError("Error post-venta", "No se pudo recargar la lista de productos: " + loadEx.getMessage());
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Venta Procesada");
            alert.setHeaderText("Venta registrada con ID: " + ventaResponse.getIdVenta());
            alert.setContentText("La venta fue procesada con éxito. Total: " + formatoMoneda.format(ventaResponse.getTotal()));
            alert.showAndWait();

            carritoBox.getChildren().clear();
            totalVenta = BigDecimal.ZERO;
            actualizarTotal();
            productosBox.getChildren().clear();
            txtBusqueda.clear();

        } catch (Exception e) {
            mostrarError("Error al procesar la venta", "Ocurrió un error: " + e.getMessage());
            e.printStackTrace(); // Mantener para depuración
        }
    }
}