package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoVentaInfo; // Nueva importación
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Window;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode; // Para el cálculo del IVA
import java.net.URL;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class VenderControllerFX implements Initializable {

    private final IProductoService productoService;

    @FXML
    private Button btnProcesarVenta;
    @FXML
    private Button btnProductoComun; // No se implementará ahora
    @FXML
    private Button btnBuscarVenta; // No se implementará ahora
    @FXML
    private Button btnNuevaVenta; // No se implementará ahora


    @FXML
    private TextField txtBusqueda;

    @FXML
    private VBox productosBox;

    @FXML
    private VBox carritoBox; // Contiene los items del carrito

    // Labels para el resumen del carrito del FXML
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
    // totalVenta se calculará dinámicamente, no es necesario un campo global si se recalcula siempre.

    public VenderControllerFX(IProductoService productoService) {
        this.productoService = productoService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        formatoMoneda.setMaximumFractionDigits(2); // Asegurar 2 decimales para COP
        try {
            cargarProductos();
            buscarProductos(null); // Mostrar todos los productos al inicio
            actualizarTotalCarrito(); // Inicializar etiquetas de totales y contador
        } catch (Exception e) {
            mostrarError("Error al inicializar", "No se pudieron cargar los productos: " + e.getMessage());
            e.printStackTrace();
        }
        // Configurar acciones para botones no implementados (opcional)
        if (btnProductoComun != null) {
            btnProductoComun.setOnAction(event -> mostrarAlerta("Función no implementada", "La opción 'Producto Común' aún no está disponible."));
        }
        if (btnBuscarVenta != null) {
            btnBuscarVenta.setOnAction(event -> mostrarAlerta("Función no implementada", "La opción 'Buscar Venta' aún no está disponible."));
        }
        if (btnNuevaVenta != null) {
            btnNuevaVenta.setOnAction(event -> {
                limpiarVenta();
                mostrarAlerta("Nueva Venta", "Se ha iniciado una nueva venta. El carrito está vacío.");
            });
        }
    }

    // crearHeaderCarrito ya no es necesario si el FXML lo define o si los items son auto-descriptivos.
    // Si el FXML no tiene un header para el carrito, y se desea uno dinámico, se podría mantener.
    // Por ahora, asumimos que el FXML maneja la estructura visual del carrito y sus items.

    private void cargarProductos() throws Exception {
        todosLosProductos = productoService.obtenerTodos().stream()
                .filter(ProductoFX::getEstado) // Solo productos activos
                .collect(Collectors.toList());
    }

    @FXML
    void buscarProductos(KeyEvent event) { // Puede ser llamado sin evento al inicio
        String textoBusqueda = "";
        if (txtBusqueda != null && txtBusqueda.getText() != null) {
            textoBusqueda = txtBusqueda.getText().trim().toLowerCase();
        }

        productosBox.getChildren().clear(); // Limpiar resultados anteriores

        if (todosLosProductos == null) {
            mostrarError("Error", "La lista de productos no ha sido cargada.");
            return;
        }

        String finalTextoBusqueda = textoBusqueda; // Necesario para lambda
        todosLosProductos.stream()
                .filter(p -> p.getNombre().toLowerCase().contains(finalTextoBusqueda) ||
                             String.valueOf(p.getIdProducto()).toLowerCase().contains(finalTextoBusqueda))
                .forEach(this::agregarProductoAResultados);
    }

    private void agregarProductoAResultados(ProductoFX producto) {
        // Contenedor principal para la tarjeta del producto
        HBox productoCard = new HBox(15); // Espaciado entre imagen (futura) y detalles
        productoCard.setPadding(new Insets(10));
        productoCard.getStyleClass().add("producto-item-card"); // Usar clase CSS del FXML
        productoCard.setAlignment(Pos.CENTER_LEFT);

        // Espacio para la imagen (Placeholder)
        StackPane imagenPlaceholder = new StackPane();
        imagenPlaceholder.setPrefSize(80, 80); // Tamaño deseado para la imagen
        imagenPlaceholder.setMinSize(80, 80);
        imagenPlaceholder.setStyle("-fx-background-color: #e0e0e0; -fx-background-radius: 5;");
        Label lblPlaceholder = new Label("Img");
        imagenPlaceholder.getChildren().add(lblPlaceholder);
        // Cuando tengas la imagen:
        // ImageView imageView = new ImageView(new Image(producto.getRutaImagen()));
        // imageView.setFitHeight(80);
        // imageView.setFitWidth(80);
        // imageView.setPreserveRatio(true);
        // imagenPlaceholder.getChildren().setAll(imageView);

        // VBox para detalles del producto (Nombre, Stock, Precio)
        VBox detallesBox = new VBox(5);
        detallesBox.setAlignment(Pos.CENTER_LEFT);

        Label lblNombre = new Label(producto.getNombre());
        lblNombre.getStyleClass().add("label-nombre"); // Clase CSS del FXML

        Label lblStock = new Label("Stock: " + producto.getStock());
        // lblStock.getStyleClass().add("label-stock"); // Añadir si se define en CSS

        Label lblPrecio = new Label(formatoMoneda.format(producto.getPrecioVenta()));
        lblPrecio.getStyleClass().add("label-precio"); // Clase CSS del FXML

        detallesBox.getChildren().addAll(lblNombre, lblStock, lblPrecio);

        // Botón para agregar al carrito
        Button btnAgregar = new Button("Agregar");
        btnAgregar.getStyleClass().add("button-agregar"); // Clase CSS del FXML
        btnAgregar.setOnAction(e -> agregarAlCarrito(producto));

        // HBox para alinear el botón a la derecha si es necesario o simplemente añadirlo
        // Si se quiere el botón debajo de los detalles, añadirlo al VBox detallesBox
        // Si se quiere al lado, añadirlo al HBox productoCard directamente o en un contenedor.
        // Por simplicidad, lo añadimos al lado de los detalles, pero podría necesitar un Pane para empujar.
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        productoCard.getChildren().addAll(imagenPlaceholder, detallesBox, spacer, btnAgregar);
        productosBox.getChildren().add(productoCard);
    }

    private void agregarAlCarrito(ProductoFX producto) {
        // Verificar si el producto ya está en el carrito
        for (Node node : carritoBox.getChildren()) {
            if (node.getUserData() instanceof ProductoFX) {
                ProductoFX productoEnCarrito = (ProductoFX) node.getUserData();
                if (productoEnCarrito.getIdProducto() == producto.getIdProducto()) {
                    // Producto ya existe, podríamos incrementar cantidad o mostrar mensaje
                    HBox itemExistente = (HBox) node;
                    Spinner<Integer> spinnerCantidad = encontrarSpinnerEnItem(itemExistente);
                    if (spinnerCantidad != null) {
                        int nuevaCantidad = spinnerCantidad.getValue() + 1;
                        if (nuevaCantidad <= producto.getStock()) {
                            spinnerCantidad.getValueFactory().setValue(nuevaCantidad);
                        } else {
                            mostrarAlerta("Stock insuficiente", "No hay suficiente stock para agregar más unidades de " + producto.getNombre());
                        }
                    }
                    actualizarTotalCarrito();
                    return;
                }
            }
        }

        if (producto.getStock() <= 0) {
            mostrarAlerta("Sin Stock", "El producto " + producto.getNombre() + " no tiene stock disponible.");
            return;
        }

        HBox itemCarrito = new HBox(10); // Espaciado entre elementos
        itemCarrito.setPadding(new Insets(8));
        itemCarrito.setAlignment(Pos.CENTER_LEFT);
        itemCarrito.getStyleClass().add("carrito-item"); // Para CSS si se define
        itemCarrito.setUserData(producto); // Guardar el objeto ProductoFX

        Label lblNombre = new Label(producto.getNombre());
        lblNombre.setPrefWidth(150); // Ajustar según necesidad
        lblNombre.setWrapText(true);
        HBox.setHgrow(lblNombre, Priority.ALWAYS);

        Spinner<Integer> spinnerCantidad = new Spinner<>(1, producto.getStock(), 1);
        spinnerCantidad.setPrefWidth(70);
        spinnerCantidad.setEditable(true); // Permitir edición manual
        spinnerCantidad.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue > producto.getStock()) {
                spinnerCantidad.getValueFactory().setValue(oldValue); // Revertir si excede stock
                mostrarAlerta("Stock limitado", "La cantidad no puede exceder el stock disponible (" + producto.getStock() + ").");
            } else if (newValue <= 0) {
                 spinnerCantidad.getValueFactory().setValue(1); // No permitir cero o negativo directamente aquí, el botón eliminar es para eso
            }
            actualizarTotalCarrito();
        });

        Label lblPrecioUnitario = new Label(formatoMoneda.format(producto.getPrecioVenta()));
        lblPrecioUnitario.setPrefWidth(80);
        lblPrecioUnitario.setAlignment(Pos.CENTER_RIGHT);

        Label lblSubtotalItem = new Label(formatoMoneda.format(producto.getPrecioVenta().multiply(BigDecimal.valueOf(spinnerCantidad.getValue()))));
        lblSubtotalItem.setPrefWidth(90);
        lblSubtotalItem.setAlignment(Pos.CENTER_RIGHT);
        // Actualizar subtotal del item cuando cambia la cantidad
        spinnerCantidad.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue > 0) {
                lblSubtotalItem.setText(formatoMoneda.format(producto.getPrecioVenta().multiply(BigDecimal.valueOf(newValue))));
            }
        });


        Button btnEliminar = new Button("X"); // O un icono
        btnEliminar.getStyleClass().add("button-eliminar-item"); // Estilo para el botón
        btnEliminar.setOnAction(e -> eliminarDelCarrito(itemCarrito));

        itemCarrito.getChildren().addAll(lblNombre, spinnerCantidad, lblPrecioUnitario, lblSubtotalItem, btnEliminar);
        carritoBox.getChildren().add(itemCarrito);
        actualizarTotalCarrito();
    }

    private Spinner<Integer> encontrarSpinnerEnItem(HBox itemCarrito) {
        for (Node child : itemCarrito.getChildren()) {
            if (child instanceof Spinner) {
                @SuppressWarnings("unchecked")
                Spinner<Integer> spinner = (Spinner<Integer>) child;
                return spinner;
            }
        }
        return null;
    }

    private void eliminarDelCarrito(Node itemNode) {
        carritoBox.getChildren().remove(itemNode);
        actualizarTotalCarrito();
    }


    private void actualizarTotalCarrito() {
        BigDecimal subtotalCalculado = BigDecimal.ZERO;
        int cantidadItems = 0;

        for (Node node : carritoBox.getChildren()) {
            if (node.getUserData() instanceof ProductoFX) { // Asegurarse que es un item de producto
                HBox itemCarrito = (HBox) node;
                ProductoFX producto = (ProductoFX) itemCarrito.getUserData();
                Spinner<Integer> spinnerCantidad = encontrarSpinnerEnItem(itemCarrito);

                if (spinnerCantidad != null) {
                    int cantidad = spinnerCantidad.getValue();
                    subtotalCalculado = subtotalCalculado.add(producto.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)));
                    cantidadItems++;
                }
            }
        }

        BigDecimal ivaCalculado = subtotalCalculado.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalGeneralCalculado = subtotalCalculado.add(ivaCalculado);

        lblSubtotal.setText(formatoMoneda.format(subtotalCalculado));
        lblIVA.setText(formatoMoneda.format(ivaCalculado));
        lblTotalGeneral.setText(formatoMoneda.format(totalGeneralCalculado));
        lblItemsCarrito.setText(cantidadItems + (cantidadItems == 1 ? " Item" : " Items"));
    }


    private void mostrarError(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.ERROR);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void procesarVenta() {
        if (carritoBox.getChildren().isEmpty() || carritoBox.getChildren().stream().noneMatch(node -> node.getUserData() instanceof ProductoFX)) {
            mostrarError("Carrito vacío", "No hay productos en el carrito para procesar la venta.");
            return;
        }

        List<ProductoVentaInfo> productosParaVenta = new ArrayList<>();
        BigDecimal totalVentaCalculado = BigDecimal.ZERO;

        for (Node itemNode : carritoBox.getChildren()) {
            if (itemNode instanceof HBox && itemNode.getUserData() instanceof ProductoFX) {
                HBox itemCarrito = (HBox) itemNode;
                ProductoFX productoEnCarrito = (ProductoFX) itemCarrito.getUserData();
                Spinner<Integer> spCantidad = encontrarSpinnerEnItem(itemCarrito);

                if (productoEnCarrito != null && spCantidad != null) {
                    int cantidad = spCantidad.getValue();
                    if (cantidad <= 0) {
                        mostrarError("Error en Carrito", "El producto '" + productoEnCarrito.getNombre() + "' tiene cantidad cero o negativa.");
                        return;
                    }
                    if (cantidad > productoEnCarrito.getStock()) {
                         mostrarError("Stock Insuficiente", "No hay suficiente stock para '" + productoEnCarrito.getNombre() + "'. Disponible: " + productoEnCarrito.getStock() + ", Solicitado: " + cantidad);
                        return;
                    }
                    productosParaVenta.add(new ProductoVentaInfo(
                            productoEnCarrito.getIdProducto(),
                            productoEnCarrito.getNombre(),
                            cantidad,
                            productoEnCarrito.getPrecioVenta()
                    ));
                    totalVentaCalculado = totalVentaCalculado.add(productoEnCarrito.getPrecioVenta().multiply(BigDecimal.valueOf(cantidad)));
                } else {
                    mostrarError("Error Interno", "No se pudo obtener información completa de un producto en el carrito.");
                    return;
                }
            }
        }

        if (productosParaVenta.isEmpty()) {
            mostrarError("Error en Carrito", "No se pudieron procesar los productos del carrito.");
            return;
        }
        
        // El IVA y el total final para el diálogo se calculan a partir del totalVentaCalculado (que es el subtotal)
        BigDecimal ivaDialogo = totalVentaCalculado.multiply(new BigDecimal("0.12")).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalConIvaDialogo = totalVentaCalculado.add(ivaDialogo);


        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.DIALOG_PROCESAR_VENTA));
            DialogPane dialogContentPane = loader.load();
            ProcesarVentaDialogController dialogController = loader.getController();

            // Pasar el total SIN IVA (subtotal) al diálogo, el diálogo podría calcular su propio IVA y total si es necesario
            // o pasar el total CON IVA. Aquí pasamos el total CON IVA.
            dialogController.setDatosVenta(productosParaVenta, totalConIvaDialogo);


            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setDialogPane(dialogContentPane);
            dialog.setTitle("Confirmar y Procesar Venta");
            
            // Configurar el propietario del diálogo si es posible (para la modalidad)
            Window ownerWindow = null;
            if (btnProcesarVenta != null && btnProcesarVenta.getScene() != null) {
                 ownerWindow = btnProcesarVenta.getScene().getWindow();
            }
            if (ownerWindow != null) {
                dialog.initOwner(ownerWindow);
                dialog.initModality(Modality.WINDOW_MODAL); // Asegurar modalidad correcta
            }

            dialogController.configurarValidacionBotonConfirmar(); // Se necesitará este método en ProcesarVentaDialogController

            // Mostrar el diálogo y esperar el resultado
            Optional<ButtonType> result = dialog.showAndWait();

            // Si el diálogo se cierra presionando el botón de confirmación del ProcesarVentaDialogController
            if (result.isPresent() && result.get() == dialogController.getBtnConfirmarType()) {
                // Se asume que ProcesarVentaDialogController manejó la venta.
                // VenderControllerFX solo necesita limpiar su estado para una nueva venta.
                limpiarVenta();
                // Opcionalmente, un mensaje indicando que el proceso continúa/finaliza en el diálogo
                // mostrarAlerta("Información", "El proceso de venta ha sido gestionado. Puede iniciar una nueva venta.");
            } else {
                // El usuario cerró el diálogo de otra manera (ej. Cancelar o botón de cierre de ventana)
                // No se realiza ninguna acción de limpieza de venta aquí, el carrito permanece como estaba.
            }

        } catch (IOException ioEx) {
            mostrarError("Error al cargar diálogo", "No se pudo cargar la ventana de procesamiento: " + ioEx.getMessage());
            ioEx.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error Inesperado", "Ocurrió un error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void limpiarVenta() {
        if (carritoBox != null) {
            // Dejar solo el header si existe, o limpiar todo si no hay header persistente.
            // Si el FXML define el header, no necesitamos recrearlo.
            // Si el header es dinámico (como en el código original), se debe manejar aquí.
            // Por ahora, limpiamos todos los items de producto.
            carritoBox.getChildren().removeIf(node -> node.getUserData() instanceof ProductoFX);
            if (carritoBox.getChildren().isEmpty() && lblItemsCarrito != null) { // Si no hay header y el carrito está realmente vacío
                 // No hacer nada si el header es parte del FXML y debe permanecer.
            }
        }
        if (productosBox != null) {
            productosBox.getChildren().clear(); // Limpiar los productos mostrados
        }
        if (txtBusqueda != null) {
            txtBusqueda.clear();
        }
        
        try {
            cargarProductos(); // Recargar la lista original de todos los productos
            buscarProductos(null); // Mostrar todos los productos de nuevo
        } catch (Exception e) {
            mostrarError("Error", "No se pudieron recargar los productos: " + e.getMessage());
        }
        actualizarTotalCarrito(); // Reiniciar los totales a cero y contador de items
    }
}