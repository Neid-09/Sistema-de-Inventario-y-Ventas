package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private List<ProductoFX> todosLosProductos;
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            // Cargar todos los productos activos al iniciar
            cargarProductos();
        } catch (Exception e) {
            mostrarError("Error al cargar productos", e.getMessage());
        }
    }

    private void cargarProductos() throws Exception {
        todosLosProductos = productoService.obtenerTodos().stream()
                .filter(ProductoFX::getEstado) // Solo productos activos
                .collect(Collectors.toList());
    }

    @FXML
    void buscarProductos(KeyEvent event) {
        String textoBusqueda = txtBusqueda.getText().trim().toLowerCase();

        try {
            // Limpiar el panel de resultados
            productosBox.getChildren().clear();

            if (textoBusqueda.isEmpty()) {
                return; // No mostrar nada si no hay texto de búsqueda
            }

            // Si es posible que sea un ID
            boolean posibleId = textoBusqueda.matches("\\d+");

            List<ProductoFX> resultados = todosLosProductos.stream()
                    .filter(p -> {
                        // Búsqueda por ID
                        if (posibleId && String.valueOf(p.getIdProducto()).equals(textoBusqueda)) {
                            return true;
                        }

                        // Búsqueda por nombre
                        if (p.getNombre() != null && p.getNombre().toLowerCase().contains(textoBusqueda)) {
                            return true;
                        }

                        // Búsqueda por descripción (si existe)
                        if (p.getDescripcion() != null && p.getDescripcion().toLowerCase().contains(textoBusqueda)) {
                            return true;
                        }

                        return false;
                    })
                    .limit(10) // Limitamos a 10 resultados para no sobrecargar la interfaz
                    .collect(Collectors.toList());

            // Mostrar resultados
            for (ProductoFX producto : resultados) {
                agregarProductoAResultados(producto);
            }

        } catch (Exception e) {
            mostrarError("Error en búsqueda", e.getMessage());
        }
    }

    private void agregarProductoAResultados(ProductoFX producto) {
        // Crear un panel para el producto
        VBox productoPane = new VBox(5);
        productoPane.setPadding(new Insets(10));
        productoPane.setStyle("-fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-color: white;");

        // Nombre del producto con fuente en negrita
        Label lblNombre = new Label(producto.getNombre());
        lblNombre.setFont(Font.font("System", FontWeight.BOLD, 14));

        // Información de stock y precio
        Label lblStock = new Label("Stock: " + producto.getStock());
        Label lblPrecio = new Label("Precio: " + formatoMoneda.format(producto.getPrecio()));

        // Descripción (si existe)
        Label lblDescripcion = null;
        if (producto.getDescripcion() != null && !producto.getDescripcion().isEmpty()) {
            lblDescripcion = new Label(producto.getDescripcion());
            lblDescripcion.setWrapText(true);
        }

        // Botón para agregar al carrito
        Button btnAgregar = new Button("Agregar");
        btnAgregar.setOnAction(e -> agregarAlCarrito(producto));

        // Agregar todos los componentes al panel
        if (lblDescripcion != null) {
            productoPane.getChildren().addAll(lblNombre, lblDescripcion, lblStock, lblPrecio, btnAgregar);
        } else {
            productoPane.getChildren().addAll(lblNombre, lblStock, lblPrecio, btnAgregar);
        }

        // Cuando se hace clic en el panel, selecciona este producto
        productoPane.setOnMouseClicked(e -> seleccionarProducto(producto));

        // Agregar al panel de productos
        productosBox.getChildren().add(productoPane);
    }

    private void seleccionarProducto(ProductoFX producto) {
        // Aquí puedes mostrar detalles ampliados del producto o pre-seleccionarlo
        // Por ahora solo simulamos que lo agregamos al carrito
        agregarAlCarrito(producto);
    }

    private void agregarAlCarrito(ProductoFX producto) {
        // Esta es una implementación básica para mostrar productos en el carrito
        // En un sistema real necesitarías manejar cantidades, totales, etc.

        HBox itemCarrito = new HBox(10);
        itemCarrito.setPadding(new Insets(5));
        itemCarrito.setStyle("-fx-border-color: #eeeeee; -fx-border-width: 0 0 1 0;");

        Label lblNombre = new Label(producto.getNombre());
        HBox.setHgrow(lblNombre, Priority.ALWAYS);

        // Control numérico para la cantidad
        Spinner<Integer> spCantidad = new Spinner<>(1, producto.getStock(), 1);
        spCantidad.setPrefWidth(70);

        // Precio por unidad
        Label lblPrecio = new Label(formatoMoneda.format(producto.getPrecio()));
        lblPrecio.setMinWidth(80);

        // Botón para eliminar del carrito
        Button btnEliminar = new Button("X");
        btnEliminar.setStyle("-fx-text-fill: red;");
        btnEliminar.setOnAction(e -> carritoBox.getChildren().remove(itemCarrito));

        itemCarrito.getChildren().addAll(lblNombre, spCantidad, lblPrecio, btnEliminar);
        carritoBox.getChildren().add(0, itemCarrito);
    }

    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}