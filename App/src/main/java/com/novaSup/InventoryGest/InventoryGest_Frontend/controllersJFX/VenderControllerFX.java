package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX; // Añadido
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ClienteFX; // Añadido para usar ClienteFX
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService; // Añadido
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IClienteService; // Añadido
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
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.Pane;

import java.util.List; // Añadido
import java.util.Optional;
import java.util.stream.Collectors; // Añadido para filtrar

// Clases de ejemplo (deberás reemplazarlas o definirlas según tu modelo de datos)
// Se elimina la clase Cliente interna

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

    @FXML private VBox vboxCliente; // Añadido para controlar la visibilidad de la sección cliente

    // --- Campos FXML ---
    @FXML private TextField txtVendedor;
    @FXML private TextField txtBuscarCliente;
    @FXML private CheckBox chkRequiereFactura;
    @FXML private TextField txtBuscarProducto;

    @FXML private TableView<ProductoVentaAdapter> tablaProductos;
    @FXML private TableColumn<ProductoVentaAdapter, String> colCodigo;
    @FXML private TableColumn<ProductoVentaAdapter, String> colProducto;
    @FXML private TableColumn<ProductoVentaAdapter, Double> colPrecio;
    @FXML private TableColumn<ProductoVentaAdapter, Integer> colCantidad;
    @FXML private TableColumn<ProductoVentaAdapter, Double> colSubtotal;
    @FXML private TableColumn<ProductoVentaAdapter, Void> colAcciones;

    @FXML private Label lblSubtotal; // Descomentado
    @FXML private Label lblIVA;      // Descomentado
    @FXML private Label lblTotal;    // Descomentado

    @FXML private Button btnCancelar;
    @FXML private Button btnProcesarVenta;
    @FXML private Button btnImprimirComprobante;
    
    private Button btnVerDetalleCliente; // NUEVO: Botón para ver detalles del cliente

    @FXML private ComboBox<String> cmbTipoPago;

    // --- Servicios ---
    private IProductoService productoService; // Añadido
    private IClienteService clienteService; // Añadido

    // --- Listas y Modelos ---
    private ObservableList<ProductoVentaAdapter> productosEnVenta = FXCollections.observableArrayList();
    private Vendedor vendedorActual;
    private ClienteFX clienteActual; // Actualizado a ClienteFX

    private static final double TASA_IVA = 0.16; // 16%

    // --- Componentes para Sugerencias con Popup ---
    private Popup sugerenciasPopup;
    private ListView<ProductoFX> sugerenciasListViewPopup;
    private Popup sugerenciasClientePopup; // Añadido para clientes
    private ListView<ClienteFX> sugerenciasClienteListViewPopup; // Añadido para clientes


    // --- Constructor para Inyección de Dependencias ---
    public VenderControllerFX(IProductoService productoService, IClienteService clienteService) { // Añadido IClienteService
        this.productoService = productoService;
        this.clienteService = clienteService; // Inyección de IClienteService
    }

    @FXML
    public void initialize() {
        // 1. Asignar Vendedor (manual/automático)
        // Configurar la política de redimensionamiento de las columnas de la tabla
        if (tablaProductos != null) {
            tablaProductos.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);

            // Opcionalmente, puedes definir anchos porcentuales para un control más fino.
            // Por ejemplo, si quieres que ciertas columnas sean más anchas que otras:
            // colCodigo.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.15));
            // colProducto.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.30));
            // colPrecio.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.15));
            // colCantidad.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.15));
            // colSubtotal.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.15));
            // colAcciones.prefWidthProperty().bind(tablaProductos.widthProperty().multiply(0.10));
        }

        // Aquí puedes añadir el resto de tu lógica de inicialización si es necesario,
        // como configurar las factorías de celdas (aunque parece que ya lo haces con @FXML y PropertyValueFactory)
        // y cualquier otra configuración inicial.

        // 1. Asignar Vendedor (manual/automático)
        vendedorActual = new Vendedor("V001", "Vendedor Principal"); // Ejemplo
        txtVendedor.setText(vendedorActual.getNombre());
        // txtVendedor.getStyleClass().add("readonly-textfield"); // Estilo aplicado vía FXML o CSS global

        // 2. Configurar CheckBox de Factura
        chkRequiereFactura.selectedProperty().addListener((obs, oldVal, newVal) -> {
            actualizarEstadoUIFactura(newVal);
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
        // tablaProductos.setPlaceholder(new Label("No hay productos agregados a la venta")); // Estilo aplicado vía FXML o CSS

        // 4. Configurar Búsqueda de Productos con Popup de Sugerencias
        inicializarPopupSugerencias();
        configurarBusquedaProductosConPopup();

        // Listener para búsqueda de clientes al presionar Enter en txtBuscarCliente
        // txtBuscarCliente.setOnAction(event -> handleBuscarCliente()); // Se manejará con el popup
        inicializarPopupSugerenciasCliente(); // NUEVA LLAMADA
        configurarBusquedaClientesConPopup(); // NUEVA LLAMADA
        
        // Configurar ComboBox de tipo de pago (ejemplo de ítems)
        ObservableList<String> tiposPago = FXCollections.observableArrayList("Efectivo", "Tarjeta de Crédito", "Transferencia");
        cmbTipoPago.setItems(tiposPago);
        if (!tiposPago.isEmpty()) {
            cmbTipoPago.getSelectionModel().selectFirst();
        }
        
        // Estado inicial de los totales
        actualizarTotales(); // Asegúrate que esto se llama después de inicializar los labels

        // NUEVO: Crear y configurar el botón de ver detalle de cliente
        btnVerDetalleCliente = new Button("Ver Detalle");
        btnVerDetalleCliente.setOnAction(e -> handleVerDetalleCliente());
        btnVerDetalleCliente.setVisible(false);
        btnVerDetalleCliente.setManaged(false); // No ocupa espacio si está invisible
        HBox.setMargin(btnVerDetalleCliente, new Insets(0, 0, 0, 5)); // Margen a la izquierda del botón

        // Intentar insertar el botón al lado de txtBuscarCliente
        // Esto asume que txtBuscarCliente tiene un padre que es un Pane (lo cual es típico en FXML)
        Node parentOfTxtBuscarCliente = txtBuscarCliente.getParent();
        if (parentOfTxtBuscarCliente instanceof Pane) {
            Pane parentPane = (Pane) parentOfTxtBuscarCliente;
            int indexOfTxtBuscar = parentPane.getChildren().indexOf(txtBuscarCliente);
            if (indexOfTxtBuscar != -1) {
                // Crear un HBox para agrupar txtBuscarCliente y btnVerDetalleCliente
                HBox clienteSearchBox = new HBox(5); // 5 es el espaciado entre elementos
                clienteSearchBox.setAlignment(Pos.CENTER_LEFT); // Alinear contenido

                parentPane.getChildren().remove(indexOfTxtBuscar); // Quitar txtBuscarCliente del padre original
                clienteSearchBox.getChildren().addAll(txtBuscarCliente, btnVerDetalleCliente); // Añadir ambos al HBox
                parentPane.getChildren().add(indexOfTxtBuscar, clienteSearchBox); // Añadir el HBox al padre original en la misma posición
            } else {
                // Fallback: si txtBuscarCliente no se encuentra en su padre (raro),
                // añadir el botón directamente a vboxCliente si existe.
                if (vboxCliente != null) {
                    vboxCliente.getChildren().add(btnVerDetalleCliente);
                }
            }
        } else {
            // Fallback: si el padre no es un Pane o es null (muy improbable para un control FXML),
            // añadir el botón directamente a vboxCliente si existe.
            if (vboxCliente != null) {
                vboxCliente.getChildren().add(btnVerDetalleCliente);
            }
        }
    }

    private void inicializarPopupSugerencias() {
        sugerenciasPopup = new Popup();
        sugerenciasListViewPopup = new ListView<>();
        sugerenciasListViewPopup.setPrefHeight(150); // Altura deseada para la lista de sugerencias
        sugerenciasListViewPopup.setCellFactory(listView -> new ProductoSuggestionCellPopup());
        sugerenciasPopup.getContent().add(sugerenciasListViewPopup);
        sugerenciasPopup.setAutoHide(true);
    }

    private void inicializarPopupSugerenciasCliente() {
        sugerenciasClientePopup = new Popup();
        sugerenciasClienteListViewPopup = new ListView<>();
        sugerenciasClienteListViewPopup.setPrefHeight(150);
        // Configurar celda personalizada si es necesario, por ahora toString de ClienteFX
        sugerenciasClienteListViewPopup.setCellFactory(listView -> new ListCell<ClienteFX>() {
            @Override
            protected void updateItem(ClienteFX cliente, boolean empty) {
                super.updateItem(cliente, empty);
                if (empty || cliente == null) {
                    setText(null);
                } else {
                    setText(cliente.getNombre() + " " + " (ID: " + cliente.getIdCliente() + ")");
                }
            }
        });
        sugerenciasClientePopup.getContent().add(sugerenciasClienteListViewPopup);
        sugerenciasClientePopup.setAutoHide(true);
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

        // Aplicar estilo al ListView del Popup si es necesario (o hacerlo en CSS)
        // sugerenciasListViewPopup.getStyleClass().add("list-view-popup-sugerencias");
    }

    private void configurarBusquedaClientesConPopup() {
        txtBuscarCliente.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == null || newValue.trim().isEmpty() || newValue.trim().length() < 2) {
                sugerenciasClientePopup.hide();
                sugerenciasClienteListViewPopup.getItems().clear();
            } else {
                String terminoBusqueda = newValue.trim().toLowerCase();
                try {
                    // Lógica de búsqueda de clientes usando clienteService
                    List<ClienteFX> todosLosClientes = clienteService.obtenerTodosLosClientes(); // O un método más específico si existe
                    List<ClienteFX> clientesFiltrados = todosLosClientes.stream()
                        .filter(c -> (c.getNombre() != null && c.getNombre().toLowerCase().contains(terminoBusqueda)) ||
                                     // (c.getApellido() != null && c.getApellido().toLowerCase().contains(terminoBusqueda)) || // getApellido() is undefined
                                     (String.valueOf(c.getIdCliente()).contains(terminoBusqueda)))
                        .collect(Collectors.toList());

                    if (!clientesFiltrados.isEmpty()) {
                        sugerenciasClienteListViewPopup.setItems(FXCollections.observableArrayList(clientesFiltrados));
                        Window owner = txtBuscarCliente.getScene().getWindow();
                        double x = txtBuscarCliente.localToScreen(0, 0).getX();
                        double y = txtBuscarCliente.localToScreen(0, 0).getY() + txtBuscarCliente.getHeight() + 5;
                        sugerenciasClientePopup.show(owner, x, y);
                        sugerenciasClienteListViewPopup.getSelectionModel().clearSelection();
                    } else {
                        sugerenciasClientePopup.hide();
                    }
                } catch (Exception e) {
                    e.printStackTrace(); // Manejar la excepción adecuadamente
                    sugerenciasClientePopup.hide();
                }
            }
        });

        sugerenciasClienteListViewPopup.setOnMouseClicked(event -> {
            ClienteFX selectedCliente = sugerenciasClienteListViewPopup.getSelectionModel().getSelectedItem();
            if (selectedCliente != null) {
                seleccionarCliente(selectedCliente);
                sugerenciasClientePopup.hide();
            }
        });

        sugerenciasClienteListViewPopup.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ENTER) {
                ClienteFX selectedCliente = sugerenciasClienteListViewPopup.getSelectionModel().getSelectedItem();
                if (selectedCliente != null) {
                    seleccionarCliente(selectedCliente);
                    sugerenciasClientePopup.hide();
                }
            } else if (event.getCode() == KeyCode.ESCAPE) {
                sugerenciasClientePopup.hide();
            }
        });

        txtBuscarCliente.setOnAction(event -> { // Al presionar Enter en el TextField
            if (!sugerenciasClienteListViewPopup.getItems().isEmpty() && !sugerenciasClientePopup.isShowing()) {
                // Si hay sugerencias pero el popup está oculto (ej. por perder foco y volver), mostrarlo
                 Window owner = txtBuscarCliente.getScene().getWindow();
                 double x = txtBuscarCliente.localToScreen(0, 0).getX();
                 double y = txtBuscarCliente.localToScreen(0, 0).getY() + txtBuscarCliente.getHeight() + 5;
                 sugerenciasClientePopup.show(owner, x, y);
                 sugerenciasClienteListViewPopup.getSelectionModel().selectFirst(); // Opcional: seleccionar el primero
            } else if (sugerenciasClientePopup.isShowing() && sugerenciasClienteListViewPopup.getSelectionModel().getSelectedItem() != null) {
                 // Si el popup está visible y hay un item seleccionado, procesarlo
                ClienteFX selectedCliente = sugerenciasClienteListViewPopup.getSelectionModel().getSelectedItem();
                seleccionarCliente(selectedCliente);
                sugerenciasClientePopup.hide();
            } else if (!sugerenciasClienteListViewPopup.getItems().isEmpty() && sugerenciasClienteListViewPopup.getSelectionModel().getSelectedItem() == null && sugerenciasClientePopup.isShowing()){
                // Si el popup está visible, hay items, pero ninguno seleccionado, selecciona el primero y procesa
                sugerenciasClienteListViewPopup.getSelectionModel().selectFirst();
                ClienteFX selectedCliente = sugerenciasClienteListViewPopup.getSelectionModel().getSelectedItem();
                 if (selectedCliente != null) {
                    seleccionarCliente(selectedCliente);
                    sugerenciasClientePopup.hide();
                }
            }
            else {
                // Si no hay sugerencias o el popup no está visible, intentar búsqueda exacta o crear
                handleBuscarCliente();
            }
        });

        txtBuscarCliente.focusedProperty().addListener((obs, oldVal, fieldHasFocus) -> {
            if (!fieldHasFocus) {
                // Considerar si ocultar el popup cuando el campo pierde el foco,
                // a menos que el foco se mueva al propio popup.
                // Popup.setAutoHide(true) ya maneja la mayoría de estos casos.
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
        // Controla la visibilidad de la sección de cliente
        if (vboxCliente != null) {
            vboxCliente.setVisible(requiereFactura);
            vboxCliente.setManaged(requiereFactura);
        }

        // Actualizar visibilidad del botón "Ver Detalle"
        if (btnVerDetalleCliente != null) {
            if (requiereFactura && clienteActual != null) {
                btnVerDetalleCliente.setVisible(true);
                btnVerDetalleCliente.setManaged(true);
            } else {
                btnVerDetalleCliente.setVisible(false);
                btnVerDetalleCliente.setManaged(false);
            }
        }

        // Aquí puedes añadir lógica adicional si es necesario, como cambiar estilos,
        // habilitar/deshabilitar campos específicos de la factura, etc.
        if (requiereFactura) {
            // Lógica cuando se requiere factura
            // Por ejemplo, hacer visible o editable algún campo específico para la factura
            System.out.println("Se requiere factura. Mostrando campos de cliente.");
        } else {
            // Lógica cuando NO se requiere factura
            // Por ejemplo, ocultar o deshabilitar campos específicos para la factura
            System.out.println("No se requiere factura. Ocultando campos de cliente.");
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

    private void buscarYAgregarProductoPorCodigoExacto(String codigo) {
        if (codigo == null || codigo.trim().isEmpty()) {
            // Opcional: mostrar alerta si se desea notificar sobre código vacío.
            // mostrarAlerta("Búsqueda por Código", "Por favor, ingrese un código de producto.");
            return;
        }
        try {
            if (productoService == null) {
                System.err.println("ERROR CRÍTICO: productoService es null en buscarYAgregarProductoPorCodigoExacto.");
                mostrarAlerta("Error Interno", "No se pudo realizar la búsqueda por un error de configuración. Contacte a soporte.");
                return;
            }

            ProductoFX productoEncontrado = productoService.obtenerPorCodigo(codigo.trim()); // Corregido: Usar obtenerPorCodigo

            if (productoEncontrado != null) {
                agregarProductoALaVenta(productoEncontrado, 1);
                txtBuscarProducto.clear();
                // Opcional: txtBuscarProducto.requestFocus(); // Para facilitar búsquedas consecutivas
            } else {
                mostrarAlerta("Producto no Encontrado", "No se encontró ningún producto con el código: " + codigo);
            }
        } catch (Exception e) {
            System.err.println("Error al buscar producto por código exacto '" + codigo + "': " + e.getMessage());
            e.printStackTrace();
            mostrarAlerta("Error en Búsqueda", "Ocurrió un error al intentar buscar el producto por código.");
        }
    }

    private void agregarProductoALaVenta(ProductoFX productoArgumento, int cantidad) {
        if (productoArgumento == null) {
            mostrarAlerta("Error", "No se ha seleccionado ningún producto.");
            return;
        }

        Optional<ProductoVentaAdapter> existente = productosEnVenta.stream()
                .filter(pva -> pva.getCodigo().equals(productoArgumento.getCodigo()))
                .findFirst();

        if (existente.isPresent()) {
            ProductoVentaAdapter pva = existente.get();
            // Usar el producto original que ya está en la tabla para la validación de stock
            ProductoFX productoEnTabla = pva.getProductoOriginal(); 
            int cantidadActualEnVenta = pva.getCantidad();
            int nuevaCantidadTotal = cantidadActualEnVenta + cantidad;

            // VALIDACIÓN CRÍTICA: Usar el stock del producto que ya está en la tabla
            if (nuevaCantidadTotal > productoEnTabla.getStock()) {
                mostrarAlerta("Stock Insuficiente",
                              "No se puede agregar más unidades de '" + productoEnTabla.getNombre() +
                              "'. Cantidad actual en venta: " + cantidadActualEnVenta +
                              ", Stock disponible: " + productoEnTabla.getStock() +
                              ". Solo puede agregar " + (productoEnTabla.getStock() - cantidadActualEnVenta) + " más.");
                return; // No modificar si excede
            }
            pva.setCantidad(nuevaCantidadTotal);
        } else {
            // Producto nuevo en la venta. Usar el producto del argumento (del popup) para la validación inicial.
            if (cantidad > productoArgumento.getStock()) {
                mostrarAlerta("Stock Insuficiente", "La cantidad solicitada (" + cantidad +
                                                   ") para '" + productoArgumento.getNombre() +
                                                   "' excede el stock disponible (" + productoArgumento.getStock() + ").");
                return;
            }
            // Asegurarse de pasar el productoArgumento (que es el ProductoFX completo) al adaptador
            productosEnVenta.add(new ProductoVentaAdapter(productoArgumento, cantidad)); 
        }
        tablaProductos.refresh();
        actualizarTotales();
        // lblNotaProductos.setVisible(productosEnVenta.isEmpty()); // Controlado por CSS :empty pseudo-class si es posible o FXML placeholder
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
            mostrarAlerta("Búsqueda de Cliente", "Ingrese un término de búsqueda para el cliente.");
            return;
        }

        try {
            Optional<ClienteFX> clienteEncontrado = buscarClienteEnSistema(criterio);

            if (clienteEncontrado.isPresent()) {
                seleccionarCliente(clienteEncontrado.get());
            } else {
                // Preguntar si desea crear un nuevo cliente
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Cliente no encontrado");
                alert.setHeaderText("El cliente con criterio '" + criterio + "' no fue encontrado.");
                alert.setContentText("¿Desea registrar un nuevo cliente con esta información?");

                ButtonType btnSi = new ButtonType("Sí, registrar");
                ButtonType btnNo = new ButtonType("No, intentar de nuevo", ButtonBar.ButtonData.CANCEL_CLOSE);
                alert.getButtonTypes().setAll(btnSi, btnNo);

                Optional<ButtonType> resultado = alert.showAndWait();
                if (resultado.isPresent() && resultado.get() == btnSi) {
                    handleCrearCliente(criterio); // Pasar el criterio para pre-llenar
                } else {
                    txtBuscarCliente.selectAll();
                    txtBuscarCliente.requestFocus();
                }
            }
        } catch (Exception e) {
            mostrarAlerta("Error de Búsqueda", "Ocurrió un error al buscar el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void seleccionarCliente(ClienteFX cliente) {
        this.clienteActual = cliente;
        txtBuscarCliente.setText(cliente.getNombre() + " " + " (ID: " + cliente.getIdCliente() + ")");
        // Aquí podrías actualizar otros campos de la UI relacionados con el cliente si los tuvieras
        // Por ejemplo, si mostraras la dirección o el teléfono del cliente en algún Label.

        // Hacer visible el botón de "Ver Detalle" si la factura está requerida
        if (btnVerDetalleCliente != null && chkRequiereFactura.isSelected()) {
            btnVerDetalleCliente.setVisible(true);
            btnVerDetalleCliente.setManaged(true);
        }

        chkRequiereFactura.requestFocus(); // Mover el foco al siguiente campo relevante
    }


    private void handleCrearCliente(String infoInicial) {
        // Aquí iría la lógica para abrir un diálogo/formulario de creación de cliente.
        // Por ahora, simulamos la creación y asignación.
        // Deberías usar clienteService.guardarOActualizarCliente(nuevoCliente);
        System.out.println("Lógica para crear nuevo cliente con info: " + infoInicial);
        mostrarAlerta("Crear Cliente", "Funcionalidad de crear cliente aún no implementada completamente.\nSe usaría: " + infoInicial);
        // Ejemplo:
        // ClienteFX nuevoCliente = new ClienteFX();
        // nuevoCliente.setNombre(infoInicial); // O parsear infoInicial si es más complejo
        // try {
        // clienteActual = clienteService.guardarOActualizarCliente(nuevoCliente);
        // seleccionarCliente(clienteActual);
        // } catch (Exception e) {
        // mostrarAlerta("Error", "No se pudo crear el cliente: " + e.getMessage());
        // }
    }


    private Optional<ClienteFX> buscarClienteEnSistema(String criterio) throws Exception {
        // Intenta buscar por cédula primero (si el criterio parece una cédula)
        if (criterio.matches("\\d+")) { // Asume que cédula son solo números
            Optional<ClienteFX> clientePorCedula = clienteService.obtenerClientePorCedula(criterio);
            if (clientePorCedula.isPresent()) {
                return clientePorCedula;
            }
        }
        // Luego intenta por ID (si el criterio es numérico y podría ser un ID)
        if (criterio.matches("\\d+")) {
            try {
                Integer idCliente = Integer.parseInt(criterio);
                Optional<ClienteFX> clientePorId = clienteService.obtenerClientePorId(idCliente);
                if (clientePorId.isPresent()) {
                    return clientePorId;
                }
            } catch (NumberFormatException e) {
                // No es un ID numérico válido, continuar.
            }
        }
        // Finalmente, busca por nombre (o parte del nombre/apellido)
        // Esta búsqueda puede ser más amplia y devolver múltiples resultados,
        // pero para una búsqueda "exacta" en este punto, podríamos tomar el primero si coincide.
        // O, mejor aún, si el servicio tuviera un "buscarPorTerminoGeneral" sería ideal.
        // Por ahora, si no es cédula o ID, asumimos que es un nombre/apellido.
        List<ClienteFX> clientes = clienteService.obtenerTodosLosClientes(); // Podría ser costoso
        for (ClienteFX cliente : clientes) {
            if ((cliente.getNombre() != null && cliente.getNombre().equalsIgnoreCase(criterio))) {
                return Optional.of(cliente);
            }
        }
        return Optional.empty(); // No encontrado
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

        // Considerar aplicar styleClass a los botones dentro de la celda para consistencia
        // deleteButton.getStyleClass().add("table-action-button");
        // editButton.getStyleClass().add("table-action-button");
    }


    private void actualizarTotales() {
        double subtotalCalculado = 0;
        for (ProductoVentaAdapter pva : productosEnVenta) {
            subtotalCalculado += pva.getSubtotal();
        }
        double ivaCalculado = subtotalCalculado * TASA_IVA;
        double totalCalculado = subtotalCalculado + ivaCalculado;

        if (lblSubtotal != null) {
            lblSubtotal.setText(String.format("$%.2f", subtotalCalculado));
        }
        if (lblIVA != null) {
            lblIVA.setText(String.format("$%.2f", ivaCalculado));
        }
        if (lblTotal != null) {
            lblTotal.setText(String.format("$%.2f", totalCalculado));
        }
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

    @FXML
    private void limpiarParaNuevaVenta() {
        txtBuscarCliente.clear();
        txtBuscarProducto.clear();
        productosEnVenta.clear();
        chkRequiereFactura.setSelected(false);
        if (cmbTipoPago.getItems() != null && !cmbTipoPago.getItems().isEmpty()) {
            cmbTipoPago.getSelectionModel().selectFirst();
        }
        actualizarTotales();
        // lblNotaProductos.setVisible(true); // O manejado por placeholder/CSS
        clienteActual = null;
        txtBuscarCliente.requestFocus();

        // Ocultar el botón de "Ver Detalle"
        if (btnVerDetalleCliente != null) {
            btnVerDetalleCliente.setVisible(false);
            btnVerDetalleCliente.setManaged(false);
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
        // Considerar usar un DialogPane estilizado para las alertas para mantener la consistencia visual
    }

    // NUEVO: Método para manejar el clic en el botón "Ver Detalle Cliente"
    private void handleVerDetalleCliente() {
        if (clienteActual == null) {
            mostrarAlerta("Error", "No hay un cliente seleccionado.");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalle del Cliente");
        alert.setHeaderText("Información de: " + clienteActual.getNombre());

        // Construir el contenido del diálogo.
        StringBuilder sb = new StringBuilder();
        sb.append("ID Cliente: ").append(clienteActual.getIdCliente()).append("\n");
        sb.append("Documento Identidad: ").append(clienteActual.getDocumentoIdentidad() != null ? clienteActual.getDocumentoIdentidad() : "N/A").append("\n");
        sb.append("Nombre: ").append(clienteActual.getNombre() != null ? clienteActual.getNombre() : "N/A").append("\n");
        sb.append("Correo: ").append(clienteActual.getCorreo() != null && !clienteActual.getCorreo().isEmpty() ? clienteActual.getCorreo() : "N/A").append("\n");
        sb.append("Celular: ").append(clienteActual.getCelular() != null && !clienteActual.getCelular().isEmpty() ? clienteActual.getCelular() : "N/A").append("\n");
        sb.append("Dirección: ").append(clienteActual.getDireccion() != null && !clienteActual.getDireccion().isEmpty() ? clienteActual.getDireccion() : "N/A").append("\n");
        sb.append("Puntos Fidelidad: ").append(clienteActual.getPuntosFidelidad() != null ? clienteActual.getPuntosFidelidad() : "N/A").append("\n");
        sb.append("Límite Crédito: ").append(clienteActual.getLimiteCredito() != null ? clienteActual.getLimiteCredito().toString() : "N/A").append("\n");
        sb.append("Total Comprado: ").append(clienteActual.getTotalComprado() != null ? clienteActual.getTotalComprado().toString() : "N/A").append("\n");
        sb.append("Última Compra: ").append(clienteActual.getUltimaCompra() != null ? clienteActual.getUltimaCompra().toString() : "N/A").append("\n");
        sb.append("Tiene Créditos: ").append(clienteActual.isTieneCreditos() ? "Sí" : "No").append("\n");
        sb.append("Requiere Factura por Defecto: ").append(clienteActual.isRequiereFacturaDefault() ? "Sí" : "No").append("\n");
        sb.append("Razón Social: ").append(clienteActual.getRazonSocial() != null && !clienteActual.getRazonSocial().isEmpty() ? clienteActual.getRazonSocial() : "N/A").append("\n");
        sb.append("Identificación Fiscal: ").append(clienteActual.getIdentificacionFiscal() != null && !clienteActual.getIdentificacionFiscal().isEmpty() ? clienteActual.getIdentificacionFiscal() : "N/A").append("\n");
        sb.append("Dirección Facturación: ").append(clienteActual.getDireccionFacturacion() != null && !clienteActual.getDireccionFacturacion().isEmpty() ? clienteActual.getDireccionFacturacion() : "N/A").append("\n");
        sb.append("Correo Facturación: ").append(clienteActual.getCorreoFacturacion() != null && !clienteActual.getCorreoFacturacion().isEmpty() ? clienteActual.getCorreoFacturacion() : "N/A").append("\n");
        sb.append("Tipo Factura por Defecto: ").append(clienteActual.getTipoFacturaDefault() != null && !clienteActual.getTipoFacturaDefault().isEmpty() ? clienteActual.getTipoFacturaDefault() : "N/A").append("\n");
        sb.append("Fecha Registro: ").append(clienteActual.getFechaRegistro() != null ? clienteActual.getFechaRegistro().toString() : "N/A").append("\n");
        sb.append("Fecha Actualización: ").append(clienteActual.getFechaActualizacion() != null ? clienteActual.getFechaActualizacion().toString() : "N/A").append("\n");
        sb.append("Activo: ").append(clienteActual.isActivo() ? "Sí" : "No").append("\n");

        alert.setContentText(sb.toString());

        // Añadir botones personalizados
        ButtonType botonCerrar = new ButtonType("Cerrar", ButtonBar.ButtonData.CANCEL_CLOSE);
        ButtonType botonEditar = new ButtonType("Editar Información", ButtonBar.ButtonData.OK_DONE);
        alert.getButtonTypes().setAll(botonEditar, botonCerrar); // Botones en el orden: Editar, Cerrar

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == botonEditar) {
            // Lógica para editar información del cliente (actualmente no implementada)
            Alert notImplementedAlert = new Alert(Alert.AlertType.INFORMATION);
            notImplementedAlert.setTitle("Función no implementada");
            notImplementedAlert.setHeaderText(null);
            notImplementedAlert.setContentText("La funcionalidad de editar la información del cliente aún no está implementada.");
            notImplementedAlert.showAndWait();
        }
        // Si se presiona "Cerrar" o se cierra el diálogo (X), no se hace nada más.
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
