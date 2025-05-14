package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX; // Añadido
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService; // Añadido
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Popup; // Añadido para Popup
import javafx.scene.input.KeyCode; // Añadido para manejo de teclas
import javafx.stage.Window; // Añadido para el propietario del Popup
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.geometry.Pos;

import java.util.List; // Añadido
import java.util.Optional;

// Clases de ejemplo (deberás reemplazarlas o definirlas según tu modelo de datos)
class Cliente {
    String id;
    String nombre;
    // Otros campos relevantes

    public Cliente(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre + " (ID: " + id + ")";
    }
}

class Vendedor {
    String id;
    String nombre;

    public Vendedor(String id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    public String getId() { return id; }
    public String getNombre() { return nombre; }

    @Override
    public String toString() {
        return nombre;
    }
}

public class VenderControllerFX {

    // --- Campos FXML ---
    @FXML private TextField txtVendedor;
    @FXML private TextField txtBuscarCliente;
    @FXML private CheckBox chkRequiereFactura;
    @FXML private TextField txtBuscarProducto;
    // @FXML private ListView<ProductoFX> sugerenciasProductoListView; // Eliminado en FXML y aquí

    @FXML private TableView<ProductoVentaAdapter> tablaProductos;
    @FXML private TableColumn<ProductoVentaAdapter, String> colCodigo;
    @FXML private TableColumn<ProductoVentaAdapter, String> colProducto;
    @FXML private TableColumn<ProductoVentaAdapter, Double> colPrecio;
    @FXML private TableColumn<ProductoVentaAdapter, Integer> colCantidad;
    @FXML private TableColumn<ProductoVentaAdapter, Double> colSubtotal;
    @FXML private TableColumn<ProductoVentaAdapter, Void> colAcciones;

    @FXML private Label lblSubtotal;
    @FXML private Label lblIVA;
    @FXML private Label lblTotal;

    @FXML private Button btnCancelar;
    @FXML private Button btnProcesarVenta;
    @FXML private Button btnImprimirComprobante; // El texto de este botón cambiará

    // --- Servicios ---
    private IProductoService productoService; // Añadido

    // --- Listas y Modelos ---
    private ObservableList<ProductoVentaAdapter> productosEnVenta = FXCollections.observableArrayList();
    private Vendedor vendedorActual;
    private Cliente clienteActual;

    private static final double TASA_IVA = 0.16; // 16%

    // --- Componentes para Sugerencias con Popup ---
    private Popup sugerenciasPopup;
    private ListView<ProductoFX> sugerenciasListViewPopup;

    // --- Constructor para Inyección de Dependencias ---
    public VenderControllerFX(IProductoService productoService) { // Añadido
        this.productoService = productoService;
    }

    @FXML
    public void initialize() {
        // 1. Asignar Vendedor (manual/automático)
        vendedorActual = new Vendedor("V001", "Vendedor Principal"); // Ejemplo
        txtVendedor.setText(vendedorActual.getNombre());

        // 2. Configurar CheckBox de Factura
        chkRequiereFactura.selectedProperty().addListener((obs, oldVal, newVal) -> {
            actualizarEstadoUIFactura(newVal); 
            if (newVal) {
                btnImprimirComprobante.setText("Finalizar e Imprimir Factura");
            } else {
                btnImprimirComprobante.setText("Finalizar e Imprimir Comprobante");
            }
        });
        actualizarEstadoUIFactura(chkRequiereFactura.isSelected()); // Estado inicial basado en el checkbox

        // 3. Configurar Tabla de Productos
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colProducto.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal"));
        configurarColumnaAcciones();
        configurarColumnaCantidad(); // <--- NUEVA LLAMADA

        tablaProductos.setItems(productosEnVenta);
        tablaProductos.setPlaceholder(new Label("No hay productos agregados a la venta"));

        // 4. Configurar Búsqueda de Productos con Popup de Sugerencias
        inicializarPopupSugerencias();
        configurarBusquedaProductosConPopup();

        // Listener para búsqueda de clientes al presionar Enter en txtBuscarCliente
        txtBuscarCliente.setOnAction(event -> handleBuscarCliente());

        // Estado inicial de los totales
        actualizarTotales();
    }

    private void inicializarPopupSugerencias() {
        sugerenciasPopup = new Popup();
        sugerenciasListViewPopup = new ListView<>();
        sugerenciasListViewPopup.setPrefHeight(150); // Altura deseada para la lista de sugerencias
        sugerenciasListViewPopup.setCellFactory(listView -> new ProductoSuggestionCellPopup());
        sugerenciasPopup.getContent().add(sugerenciasListViewPopup);
        sugerenciasPopup.setAutoHide(true);
    }

    private void configurarBusquedaProductosConPopup() {
        txtBuscarProducto.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty() || newValue.trim().length() < 2) {
                sugerenciasPopup.hide();
                sugerenciasListViewPopup.getItems().clear();
            } else {
                String terminoBusqueda = newValue.trim();
                try {
                    if (productoService == null) {
                        System.err.println("ERROR CRÍTICO: productoService es null.");
                        sugerenciasPopup.hide();
                        return;
                    }
                    List<ProductoFX> productosEncontrados = productoService.filtrarProductos(terminoBusqueda, null, null, true);
                    if (productosEncontrados != null && !productosEncontrados.isEmpty()) {
                        sugerenciasListViewPopup.setItems(FXCollections.observableArrayList(productosEncontrados));
                        if (!sugerenciasPopup.isShowing()) {
                            Window ownerWindow = txtBuscarProducto.getScene().getWindow();
                            double x = txtBuscarProducto.localToScreen(0, 0).getX();
                            double y = txtBuscarProducto.localToScreen(0, 0).getY() + txtBuscarProducto.getHeight();
                            sugerenciasPopup.show(ownerWindow, x, y);
                        }
                        sugerenciasListViewPopup.setPrefWidth(txtBuscarProducto.getWidth()); // Ajustar ancho
                    } else {
                        sugerenciasPopup.hide();
                    }
                } catch (Exception e) {
                    System.err.println("Error al buscar productos: " + e.getMessage());
                    e.printStackTrace();
                    sugerenciasPopup.hide();
                }
            }
        });

        sugerenciasListViewPopup.setOnMouseClicked(event -> {
            ProductoFX selectedProducto = sugerenciasListViewPopup.getSelectionModel().getSelectedItem();
            if (selectedProducto != null) {
                VenderControllerFX.this.agregarProductoALaVenta(selectedProducto, 1); // Corregido
                txtBuscarProducto.clear();
                sugerenciasPopup.hide();
                txtBuscarProducto.requestFocus();
            }
        });

        sugerenciasListViewPopup.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ProductoFX selectedProducto = sugerenciasListViewPopup.getSelectionModel().getSelectedItem();
                if (selectedProducto != null) {
                    VenderControllerFX.this.agregarProductoALaVenta(selectedProducto, 1); // Corregido
                    txtBuscarProducto.clear();
                    sugerenciasPopup.hide();
                    txtBuscarProducto.requestFocus();
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                sugerenciasPopup.hide();
                txtBuscarProducto.requestFocus();
            }
        });

        txtBuscarProducto.setOnAction(event -> {
            if (sugerenciasPopup.isShowing() && !sugerenciasListViewPopup.getItems().isEmpty()) {
                if (sugerenciasListViewPopup.getSelectionModel().isEmpty()) {
                    sugerenciasListViewPopup.getSelectionModel().selectFirst();
                }
                ProductoFX selectedProducto = sugerenciasListViewPopup.getSelectionModel().getSelectedItem();
                if (selectedProducto != null) {
                    VenderControllerFX.this.agregarProductoALaVenta(selectedProducto, 1); // Corregido
                    txtBuscarProducto.clear();
                    sugerenciasPopup.hide();
                }
            } else {
                buscarYAgregarProductoPorCodigoExacto(txtBuscarProducto.getText());
            }
        });

        txtBuscarProducto.focusedProperty().addListener((obs, oldVal, fieldHasFocus) -> {
            if (!fieldHasFocus) {
                // Pequeño retraso para permitir que el clic en el popup se procese antes de ocultarlo
                javafx.application.Platform.runLater(() -> {
                    if (sugerenciasPopup.isShowing()) {
                        Node focusOwner = null;
                        if (sugerenciasPopup.getScene() != null) {
                            focusOwner = sugerenciasPopup.getScene().getFocusOwner();
                        }

                        // Hide if focus is not the ListView itself or one of its descendant nodes.
                        // The isDescendant method handles the case where focusOwner might be null.
                        // This fulfills the intent of not hiding if focus is on an interactive element within the popup.
                        if (!isDescendant(sugerenciasListViewPopup, focusOwner)) { // Original comment intent: Añadida condición para no ocultar si el botón de agregar de la tarjeta tiene foco
                            sugerenciasPopup.hide();
                        }
                    }
                });
            }
        });
    }

    // Clase interna para la celda personalizada de sugerencias de producto en el Popup
    private class ProductoSuggestionCellPopup extends ListCell<ProductoFX> {
        private BorderPane layoutPane;
        private Label lblNombreProducto;
        private Label lblDetallesProducto; // Para Código, Precio, Existencias
        private Spinner<Integer> cantidadSpinner;
        private Button btnAgregarProducto;
        private HBox accionesBox; // Para spinner y botón agregar

        public ProductoSuggestionCellPopup() {
            super();
            layoutPane = new BorderPane();
            layoutPane.setPadding(new Insets(8)); // Espaciado general de la tarjeta
            // layoutPane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;"); // Borde para la tarjeta

            // Sección de Información del Producto (Izquierda o Arriba)
            lblNombreProducto = new Label();
            lblNombreProducto.setStyle("-fx-font-weight: bold; -fx-font-size: 1.1em;");

            lblDetallesProducto = new Label();
            lblDetallesProducto.setStyle("-fx-font-size: 0.9em; -fx-text-fill: dimgray;");
            lblDetallesProducto.setWrapText(true);

            VBox infoVBox = new VBox(5, lblNombreProducto, lblDetallesProducto);
            infoVBox.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            layoutPane.setLeft(infoVBox);
            BorderPane.setMargin(infoVBox, new Insets(0, 10, 0, 0)); // Margen a la derecha de la info

            // Sección de Acciones (Derecha)
            cantidadSpinner = new Spinner<>(1, 999, 1); // Min, Max, Initial
            cantidadSpinner.setPrefWidth(70);

            btnAgregarProducto = new Button("Agregar");
            btnAgregarProducto.setOnAction(event -> {
                ProductoFX producto = getItem();
                if (producto != null) {
                    int cantidad = cantidadSpinner.getValue();
                    VenderControllerFX.this.agregarProductoALaVenta(producto, cantidad); // Corregido
                    txtBuscarProducto.clear();
                    sugerenciasPopup.hide();
                    txtBuscarProducto.requestFocus();
                }
            });

            accionesBox = new HBox(8, cantidadSpinner, btnAgregarProducto);
            accionesBox.setAlignment(javafx.geometry.Pos.CENTER_RIGHT);
            layoutPane.setRight(accionesBox);

            // Tooltip para el spinner para informar sobre existencias
            cantidadSpinner.valueProperty().addListener((obs, oldValue, newValue) -> {
                ProductoFX producto = getItem();
                if (producto != null && newValue > producto.getStock()) { // Corregido getExistencias a getStock
                    // Opcional: revertir al valor máximo de existencias o mostrar advertencia
                    // Por ahora, solo limitamos en el spinner visualmente, la lógica de agregarProductoALaVenta debería validar
                }
            });
        }

        @Override
        protected void updateItem(ProductoFX producto, boolean empty) {
            super.updateItem(producto, empty);
            if (empty || producto == null) {
                setText(null);
                setGraphic(null);
                // Opcional: limpiar estilos si se aplican dinámicamente
                // layoutPane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
            } else {
                lblNombreProducto.setText(producto.getNombre());
                lblDetallesProducto.setText(String.format("Código: %s | Precio: $%.2f | Existencias: %d",
                        producto.getCodigo(), producto.getPrecioVenta(), producto.getStock())); // Corregido getExistencias a getStock

                // Configurar el spinner basado en las existencias del producto
                SpinnerValueFactory<Integer> valueFactory = new SpinnerValueFactory.IntegerSpinnerValueFactory(
                        1, // min
                        Math.max(1, producto.getStock()), // max, asegurar que max sea al menos 1 // Corregido getExistencias a getStock
                        1  // initial
                );
                cantidadSpinner.setValueFactory(valueFactory);
                cantidadSpinner.setDisable(producto.getStock() <= 0); // Corregido getExistencias a getStock
                btnAgregarProducto.setDisable(producto.getStock() <= 0); // Corregido getExistencias a getStock

                setGraphic(layoutPane);
                // Ejemplo de cómo cambiar el estilo si no hay stock
                // if (producto.getExistencias() <= 0) {
                //     layoutPane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1; -fx-background-color: #f0f0f0;"); // Grisáceo si no hay stock
                // } else {
                //     layoutPane.setStyle("-fx-border-color: lightgray; -fx-border-width: 1;");
                // }
            }
        }
    }

    private void actualizarEstadoUIFactura(boolean requiereFactura) {
        txtBuscarCliente.setDisable(!requiereFactura);
        if (!requiereFactura) {
            txtBuscarCliente.clear();
            clienteActual = null;
            txtBuscarCliente.setPromptText("Búsqueda de cliente deshabilitada");
        } else {
            txtBuscarCliente.setPromptText("Buscar cliente por ID, cédula o nombre...");
        }
    }

    // Método auxiliar para verificar si un nodo es descendiente de otro (si aún es necesario)
    private boolean isDescendant(Node parent, Node potentialDescendant) {
        if (potentialDescendant == null) {
            return false;
        }
        while (potentialDescendant != null) {
            if (potentialDescendant == parent) {
                return true;
            }
            potentialDescendant = potentialDescendant.getParent();
        }
        return false;
    }
    
    private void configurarColumnaCantidad() {
        colCantidad.setCellFactory(param -> new TableCell<ProductoVentaAdapter, Integer>() {
            private final Button btnMenos = new Button("-");
            private final TextField txtCantidad = new TextField();
            private final Button btnMas = new Button("+");
            private final HBox hbox = new HBox(5); // Espaciado de 5px

            {
                // Estilo y tamaño de los componentes
                btnMenos.setPrefWidth(30);
                btnMas.setPrefWidth(30);
                txtCantidad.setPrefWidth(40); // Ancho para mostrar unos 3 dígitos
                txtCantidad.setAlignment(Pos.CENTER);

                hbox.getChildren().addAll(btnMenos, txtCantidad, btnMas);
                hbox.setAlignment(Pos.CENTER); // Centrar el HBox en la celda

                btnMenos.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        ProductoVentaAdapter productoAdapter = getTableRow().getItem();
                        int cantidadActual = productoAdapter.getCantidad();
                        if (cantidadActual > 1) {
                            productoAdapter.setCantidad(cantidadActual - 1);
                            actualizarTablaYTotales();
                        }
                    }
                });

                btnMas.setOnAction(event -> {
                    if (getTableRow() != null && getTableRow().getItem() != null) {
                        ProductoVentaAdapter productoAdapter = getTableRow().getItem();
                        int cantidadActual = productoAdapter.getCantidad();
                        // Validar contra el stock del producto original
                        if (cantidadActual < productoAdapter.getProductoOriginal().getStock()) {
                            productoAdapter.setCantidad(cantidadActual + 1);
                            actualizarTablaYTotales();
                        } else {
                            // Opcional: Mostrar una alerta o feedback al usuario
                            // mostrarAlerta("Stock Máximo", "Ya se ha alcanzado el stock máximo para este producto.");
                        }
                    }
                });

                txtCantidad.setOnAction(event -> { // Al presionar Enter en el TextField
                    intentarActualizarCantidadDesdeTextField();
                });

                txtCantidad.focusedProperty().addListener((obs, oldVal, newVal) -> {
                    if (!newVal) { // Perdió el foco
                        intentarActualizarCantidadDesdeTextField();
                    }
                });
            }

            private void intentarActualizarCantidadDesdeTextField() {
                if (getTableRow() != null && getTableRow().getItem() != null) {
                    ProductoVentaAdapter productoAdapter = getTableRow().getItem();
                    try {
                        int nuevaCantidad = Integer.parseInt(txtCantidad.getText());
                        int stockDisponible = productoAdapter.getProductoOriginal().getStock();

                        if (nuevaCantidad > stockDisponible) {
                            nuevaCantidad = stockDisponible; // Ajustar al stock máximo
                            // Opcional: mostrarAlerta("Stock Superado", "La cantidad se ha ajustado al stock disponible: " + nuevaCantidad);
                        }
                        
                        if (nuevaCantidad >= 1) {
                            productoAdapter.setCantidad(nuevaCantidad);
                            actualizarTablaYTotales();
                        } else {
                            // Si la cantidad es menor a 1 (ej. 0 o negativa), revertir o ajustar a 1.
                            // Por ahora, se revierte al valor anterior si es inválido.
                            txtCantidad.setText(String.valueOf(productoAdapter.getCantidad()));
                        }
                    } catch (NumberFormatException e) {
                        txtCantidad.setText(String.valueOf(productoAdapter.getCantidad())); // Revertir en caso de error de formato
                    }
                    // Asegurarse de que el TextField refleje la cantidad potencialmente ajustada
                    txtCantidad.setText(String.valueOf(productoAdapter.getCantidad()));
                }
            }

            private void actualizarTablaYTotales() {
                // Asegurarse de que la tabla se refresque en el hilo de la UI si es necesario
                // Platform.runLater(() -> {
                    tablaProductos.refresh();
                    actualizarTotales();
                // });
            }

            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    txtCantidad.setText(item.toString());
                    setGraphic(hbox);
                }
            }
        });
        colCantidad.setStyle("-fx-alignment: CENTER;"); // Centrar el contenido de la celda de la columna Cantidad
        // colCantidad.setPrefWidth(120); // Ajustar el ancho si es necesario
    }

    //TODO: Implementar la lógica de búsqueda de productos por código exacto
    private void buscarYAgregarProductoPorCodigoExacto(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            return; // Añadido return para evitar procesamiento innecesario
        }
        try {
            // Asumimos que el servicio tiene un método para buscar por código exacto.
            // Si no, necesitarías adaptar esto o usar filtrarProductos y luego verificar el código.
            // ProductoFX producto = productoService.obtenerCategorias(codigo.trim()); // Original erroneous line
            
            String codigoBuscado = codigo.trim(); // Trim once for efficiency and clarity
            List<ProductoFX> productosEncontrados = productoService.filtrarProductos(codigoBuscado, null, null, true);
            ProductoFX producto = null;

            if (productosEncontrados != null && !productosEncontrados.isEmpty()) {
                // Find the product with the exact code match, as filtrarProductos might be broad
                // Using .equalsIgnoreCase for case-insensitive comparison; adjust if exact case is required.
                Optional<ProductoFX> match = productosEncontrados.stream()
                    .filter(p -> p.getCodigo().equalsIgnoreCase(codigoBuscado))
                    .findFirst();
                if (match.isPresent()) {
                    producto = match.get();
                }
            }
            
            if (producto != null) {
                agregarProductoALaVenta(producto, 1);
                txtBuscarProducto.clear();
                sugerenciasPopup.hide(); // Ocultar popup si estaba visible por alguna razón
            } else {
                mostrarAlerta("Producto no encontrado", "No se encontró ningún producto con el código: " + codigoBuscado);
            }
        } catch (Exception e) {
            mostrarAlerta("Error", "Ocurrió un error al buscar el producto por código.");
            e.printStackTrace();
        }
    }

    private void agregarProductoALaVenta(ProductoFX producto, int cantidad) {
        if (producto == null) {
            return; // Añadido return
        }

        // Validar si la cantidad solicitada excede el stock
        if (cantidad > producto.getStock()) {
            mostrarAlerta("Stock Insuficiente", "No hay suficiente stock para '" + producto.getNombre() + "'. Disponible: " + producto.getStock());
            return;
        }

        Optional<ProductoVentaAdapter> existente = productosEnVenta.stream()
                .filter(pva -> pva.getCodigo().equals(producto.getCodigo()))
                .findFirst();

        if (existente.isPresent()) {
            ProductoVentaAdapter pva = existente.get();
            // Aquí podrías preguntar si se desea aumentar la cantidad o mostrar un mensaje.
            // Por ahora, simplemente aumentamos la cantidad.
            pva.setCantidad(pva.getCantidad() + cantidad);
        } else {
            productosEnVenta.add(new ProductoVentaAdapter(producto, cantidad));
        }
        tablaProductos.refresh();
        actualizarTotales();
    }

    @FXML
    private void handleAgregarProducto() {
        // Esta acción ahora se maneja principalmente a través de la interacción con el popup
        // o presionando Enter en el campo de búsqueda.
        // La lógica principal está en txtBuscarProducto.setOnAction y en los listeners del popup.
        // Si txtBuscarProducto tiene texto y el popup no está visible, intentar búsqueda exacta.
        if (!txtBuscarProducto.getText().trim().isEmpty() && !sugerenciasPopup.isShowing()) {
            buscarYAgregarProductoPorCodigoExacto(txtBuscarProducto.getText());
        }
    }

    @FXML
    private void handleBuscarCliente() {
        String criterio = txtBuscarCliente.getText().trim();
        if (criterio.isEmpty()) {
            mostrarAlerta("Información", "Ingrese un criterio de búsqueda para el cliente.");
            return;
        }
        Cliente clienteEncontrado = buscarClienteEnSistema(criterio);
        if (clienteEncontrado != null) {
            clienteActual = clienteEncontrado;
            txtBuscarCliente.setText(clienteEncontrado.getNombre()); 
        } else {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Cliente no encontrado");
            alert.setHeaderText("El cliente no se encuentra en el sistema.");
            alert.setContentText("¿Desea registrar un nuevo cliente con la información proporcionada?");
            Optional<ButtonType> result = alert.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                handleCrearCliente(criterio); 
            }
        }
    }

    private void handleCrearCliente(String infoInicial) {
        mostrarAlerta("Función no implementada", "La creación de clientes desde aquí aún no está implementada. Info: " + infoInicial);
    }


    private Cliente buscarClienteEnSistema(String criterio) {
        // Simulación: Reemplazar con lógica real de búsqueda
        if ("123".equals(criterio) || "Cliente Ejemplo".equalsIgnoreCase(criterio)) {
            return new Cliente("C001", "Cliente Ejemplo (123)");
        }
        return null;
    }

    private void configurarColumnaAcciones() {
        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnEliminar = new Button("Eliminar");
            {
                btnEliminar.setOnAction(event -> {
                    ProductoVentaAdapter producto = getTableView().getItems().get(getIndex());
                    productosEnVenta.remove(producto);
                    actualizarTotales();
                });
                // Aplicar un estilo si tienes uno definido, ej. "btn-danger"
                // btnEliminar.getStyleClass().add("btn-danger-tabla"); 
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


    private void actualizarTotales() {
        double subtotalCalc = productosEnVenta.stream().mapToDouble(ProductoVentaAdapter::getSubtotal).sum();
        double ivaCalc = subtotalCalc * TASA_IVA;
        double totalCalc = subtotalCalc + ivaCalc;

        lblSubtotal.setText(String.format("%.2f", subtotalCalc));
        lblIVA.setText(String.format("%.2f", ivaCalc));
        lblTotal.setText(String.format("%.2f", totalCalc));
    }

    @FXML
    private void procesarVenta(ActionEvent event) {
        if (!validarVentaAntesDeProcesar()) {
            return;
        }
        mostrarAlerta("Venta Procesada", "La venta ha sido procesada exitosamente.");
        limpiarParaNuevaVenta();
    }

    @FXML
    private void finalizarEImprimir(ActionEvent event) {
        if (!validarVentaAntesDeProcesar()) {
            return;
        }
        String tipoDocumento = chkRequiereFactura.isSelected() ? "Factura" : "Comprobante";
        mostrarAlerta("Venta Finalizada", "La venta ha sido procesada. Imprimiendo " + tipoDocumento + "...");
        limpiarParaNuevaVenta();
    }


    private boolean validarVentaAntesDeProcesar() {
        if (productosEnVenta.isEmpty()) {
            mostrarAlerta("Error de Validación", "No hay productos en la venta.");
            return false;
        }
        if (chkRequiereFactura.isSelected() && clienteActual == null) {
            mostrarAlerta("Error de Validación", "Se requiere factura pero no se ha seleccionado un cliente.");
            txtBuscarCliente.requestFocus();
            return false;
        }
        return true;
    }


    @FXML
    private void cancelarVenta(ActionEvent event) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Cancelar Venta");
        alert.setHeaderText("¿Está seguro de que desea cancelar la venta actual?");
        alert.setContentText("Todos los productos agregados se perderán.");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            limpiarParaNuevaVenta();
            mostrarAlerta("Venta Cancelada", "La venta ha sido cancelada.");
        }
    }

    private void limpiarParaNuevaVenta() {
        productosEnVenta.clear();
        txtBuscarProducto.clear();
        if (sugerenciasPopup.isShowing()) {
            sugerenciasPopup.hide();
        }
        chkRequiereFactura.setSelected(false);
        actualizarEstadoUIFactura(false);
        actualizarTotales();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    // --- Clase Adaptadora para TableView ---
    public static class ProductoVentaAdapter {
        private final ProductoFX productoOriginal;
        private final SimpleStringProperty codigo;
        private final SimpleStringProperty nombre;
        private final SimpleDoubleProperty precio; 
        private final SimpleIntegerProperty cantidad;
        private final SimpleDoubleProperty subtotal;

        public ProductoVentaAdapter(ProductoFX producto, int cantidadInicial) {
            this.productoOriginal = producto;
            this.codigo = new SimpleStringProperty(producto.getCodigo());
            this.nombre = new SimpleStringProperty(producto.getNombre());
            this.precio = new SimpleDoubleProperty(producto.getPrecioVenta().doubleValue()); 
            this.cantidad = new SimpleIntegerProperty(cantidadInicial);
            this.subtotal = new SimpleDoubleProperty(producto.getPrecioVenta().doubleValue() * cantidadInicial);

            this.cantidad.addListener((obs, oldVal, newVal) -> 
                this.subtotal.set(this.precio.get() * newVal.intValue()));
        }

        public String getCodigo() { return codigo.get(); }
        public String getNombre() { return nombre.get(); }
        public double getPrecio() { return precio.get(); }
        public int getCantidad() { return cantidad.get(); }
        public void setCantidad(int cantidad) {
            // Asegurar que la cantidad no sea <= 0
            if (cantidad > 0) { 
                this.cantidad.set(cantidad);
            } else {
                this.cantidad.set(1); // Opcional: Forzar a 1 si el valor es inválido
            }
        }
        public double getSubtotal() { return subtotal.get(); }
        public ProductoFX getProductoOriginal() { return productoOriginal; }
    }
}
