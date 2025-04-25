package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

@Component
public class VenderControllerFX implements Initializable {

    @Autowired
    private IProductoService productoService;

    @FXML
    private TextField txtBusqueda;

    @FXML
    private VBox productosBox;

    @FXML
    private VBox carritoBox;

    @FXML
    private TextField txtTotal;

    private List<ProductoFX> todosLosProductos;
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
    private BigDecimal totalVenta = BigDecimal.ZERO;

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
        Label lblPrecio = new Label("Precio: " + formatoMoneda.format(producto.getPrecio()));

        Button btnAgregar = new Button("Agregar");
        btnAgregar.setOnAction(e -> agregarAlCarrito(producto));

        productoPane.getChildren().addAll(lblNombre, lblStock, lblPrecio, btnAgregar);
        productosBox.getChildren().add(productoPane);
    }

    private void agregarAlCarrito(ProductoFX producto) {
        HBox itemCarrito = new HBox(10);
        itemCarrito.setPadding(new Insets(5));
        itemCarrito.setStyle("-fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0;");

        Label lblNombre = new Label(producto.getNombre());
        HBox.setHgrow(lblNombre, Priority.ALWAYS);

        Spinner<Integer> spCantidad = new Spinner<>(1, producto.getStock(), 1);
        spCantidad.setPrefWidth(70);

        Label lblPrecio = new Label(formatoMoneda.format(producto.getPrecio()));
        lblPrecio.setMinWidth(80);

        Button btnEliminar = new Button("X");
        btnEliminar.setStyle("-fx-text-fill: red;");
        btnEliminar.setOnAction(e -> {
            carritoBox.getChildren().remove(itemCarrito);
            BigDecimal subtotal = producto.getPrecio().multiply(BigDecimal.valueOf(spCantidad.getValue()));
            totalVenta = totalVenta.subtract(subtotal);
            actualizarTotal();
        });

        spCantidad.valueProperty().addListener((obs, oldValue, newValue) -> {
            BigDecimal diferencia = producto.getPrecio().multiply(BigDecimal.valueOf(newValue - oldValue));
            totalVenta = totalVenta.add(diferencia);
            actualizarTotal();
        });

        itemCarrito.getChildren().addAll(lblNombre, spCantidad, lblPrecio, btnEliminar);
        carritoBox.getChildren().add(0, itemCarrito);

        totalVenta = totalVenta.add(producto.getPrecio());
        actualizarTotal();
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

            for (Node item : carritoBox.getChildren()) {
                if (item instanceof HBox) {
                    HBox itemCarrito = (HBox) item;
                    Label lblNombre = (Label) itemCarrito.getChildren().get(0);
                    Spinner<Integer> spCantidad = (Spinner<Integer>) itemCarrito.getChildren().get(1);

                    ProductoFX producto = todosLosProductos.stream()
                            .filter(p -> p.getNombre().equals(lblNombre.getText()))
                            .findFirst()
                            .orElse(null);

                    if (producto != null) {
                        int cantidadVendida = spCantidad.getValue();
                        producto.setStock(producto.getStock() - cantidadVendida);
                        productoService.actualizarProducto(producto);
                    }
                }
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Venta Procesada");
            alert.setHeaderText(null);
            alert.setContentText("La venta fue procesada con éxito. Total: " + txtTotal.getText());
            alert.showAndWait();

            carritoBox.getChildren().clear();
            totalVenta = BigDecimal.ZERO;
            actualizarTotal();
        } catch (Exception e) {
            mostrarError("Error al procesar la venta", e.getMessage());
        }
    }
}