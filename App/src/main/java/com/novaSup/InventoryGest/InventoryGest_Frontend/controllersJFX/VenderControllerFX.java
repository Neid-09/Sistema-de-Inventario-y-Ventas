package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoVentaInfo; // Nueva importación
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaCreateRequestFX; // Cambio aquí
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.DetalleVentaCreateRequestFX; // Nueva importación para detalles
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaFX; // Cambio aquí
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;
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
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Modality;
import javafx.stage.Window;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.math.BigDecimal;
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
    private final IVentaSerivice ventaService;

    @FXML
    private Button btnProcesarVenta;

    @FXML
    private TextField txtBusqueda;

    @FXML
    private VBox productosBox;

    @FXML
    private VBox carritoBox; // Este VBox contendrá el header y luego los items

    @FXML
    private TextField txtTotal;

    private List<ProductoFX> todosLosProductos;
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));
    private BigDecimal totalVenta = BigDecimal.ZERO;

    public VenderControllerFX(IProductoService productoService, IVentaSerivice ventaService) {
        this.productoService = productoService;
        this.ventaService = ventaService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            cargarProductos();
            actualizarTotal(); // Usar el método para formatear
            crearHeaderCarrito(); // Añadir el header al carrito
            // Aplicar estilos al TextField de total (opcional, si no se hace en FXML/CSS)
            txtTotal.getStyleClass().add("text-field-total");
            txtBusqueda.getStyleClass().add("text-field-busqueda");

            // Considerar la reasignación o eliminación del botón btnImprimir aquí
            // Por ahora, lo dejaremos como está, pero su funcionalidad original de imprimir
            // la venta actual se moverá al flujo de "Procesar Venta".
            // btnImprimir.setOnAction(e -> System.out.println("Funcionalidad de Imprimir Venta Anterior pendiente"));

        } catch (Exception e) {
            mostrarError("Error al inicializar", "Error al cargar productos o configurar la vista: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void crearHeaderCarrito() {
        HBox header = new HBox(10); // Espaciado entre elementos del header
        header.setPadding(new Insets(8, 12, 8, 12)); // Padding interior del header
        header.getStyleClass().add("carrito-header");
        header.setAlignment(Pos.CENTER_LEFT); // Alineación general de los elementos del header

        Label lblCodigoH = new Label("CÓDIGO");
        lblCodigoH.setPrefWidth(80); // Ancho ajustado
        lblCodigoH.getStyleClass().add("carrito-header-label");

        Label lblNombreH = new Label("NOMBRE");
        // lblNombreH.setPrefWidth(250); // Se gestionará con HGrow
        HBox.setHgrow(lblNombreH, Priority.ALWAYS); // Permitir que la columna nombre crezca
        lblNombreH.setMaxWidth(Double.MAX_VALUE); // Permitir que la columna nombre crezca
        lblNombreH.getStyleClass().add("carrito-header-label");

        Label lblCantidadH = new Label("CANTIDAD");
        lblCantidadH.setPrefWidth(100); // Ancho ajustado
        lblCantidadH.setAlignment(Pos.CENTER); // Centrar texto
        lblCantidadH.getStyleClass().add("carrito-header-label");

        Label lblPrecioH = new Label("PRECIO");
        lblPrecioH.setPrefWidth(120); // Ancho ajustado
        lblPrecioH.setAlignment(Pos.CENTER_RIGHT); // Alinear a la derecha
        lblPrecioH.getStyleClass().add("carrito-header-label");

        Label lblSubtotalH = new Label("SUBTOTAL");
        lblSubtotalH.setPrefWidth(120); // Ancho ajustado
        lblSubtotalH.setAlignment(Pos.CENTER_RIGHT); // Alinear a la derecha
        lblSubtotalH.getStyleClass().add("carrito-header-label");

        Label lblAccionesH = new Label("ACCIÓN");
        lblAccionesH.setPrefWidth(80); // Ancho ajustado
        lblAccionesH.setAlignment(Pos.CENTER); // Centrar texto
        lblAccionesH.getStyleClass().add("carrito-header-label");

        header.getChildren().addAll(lblCodigoH, lblNombreH, lblCantidadH, lblPrecioH, lblSubtotalH, lblAccionesH);
        carritoBox.getChildren().add(header);
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
        // Verificar si el producto ya está en el carrito
        for (Node node : carritoBox.getChildren()) {
            if (node instanceof HBox && node.getUserData() instanceof ProductoFX) {
                ProductoFX productoEnCarrito = (ProductoFX) node.getUserData();
                if (productoEnCarrito.getIdProducto() == producto.getIdProducto()) {
                    // Producto ya existe, podríamos incrementar cantidad o notificar
                    mostrarAlerta("Producto ya agregado", "El producto '" + producto.getNombre() + "' ya está en el carrito.");
                    return;
                }
            }
        }

        HBox itemCarrito = new HBox(10);
        itemCarrito.setPadding(new Insets(8));
        itemCarrito.getStyleClass().add("carrito-item"); // Para CSS
        itemCarrito.setUserData(producto);

        Label lblCodigo = new Label(String.valueOf(producto.getIdProducto()));
        lblCodigo.setPrefWidth(80); // Ancho ajustado
        lblCodigo.getStyleClass().add("carrito-item-label");

        Label lblNombre = new Label(producto.getNombre());
        // lblNombre.setPrefWidth(250); // Se gestionará con HGrow
        HBox.setHgrow(lblNombre, Priority.ALWAYS); // Permitir que la columna nombre crezca
        lblNombre.setMaxWidth(Double.MAX_VALUE); // Permitir que la columna nombre crezca
        lblNombre.getStyleClass().add("carrito-item-label");
        lblNombre.setWrapText(true); // Permitir que el texto del nombre se ajuste si es muy largo

        Spinner<Integer> spCantidad = new Spinner<>(1, producto.getStock(), 1);
        spCantidad.setPrefWidth(100); // Ancho ajustado
        spCantidad.setEditable(true); // Permitir edición manual
        // Aplicar un estilo para posible padding/margin desde CSS si es necesario
        spCantidad.getStyleClass().add("cantidad-spinner");
        // Centrar el spinner visualmente (el contenido ya se centra con CSS)
        VBox cantidadContainer = new VBox(spCantidad);
        cantidadContainer.setAlignment(Pos.CENTER);
        cantidadContainer.setPrefWidth(100); // Mantener el ancho del spinner

        Label lblPrecio = new Label(formatoMoneda.format(producto.getPrecioVenta()));
        lblPrecio.setPrefWidth(120); // Ancho ajustado
        lblPrecio.setAlignment(Pos.CENTER_RIGHT);
        lblPrecio.getStyleClass().add("carrito-item-precio");

        Label lblSubtotal = new Label(formatoMoneda.format(producto.getPrecioVenta())); // Subtotal inicial para cantidad 1
        lblSubtotal.setPrefWidth(120); // Ancho ajustado
        lblSubtotal.setAlignment(Pos.CENTER_RIGHT);
        lblSubtotal.getStyleClass().add("carrito-item-subtotal");

        Button btnEliminar = new Button(); // Se quita el texto o emoji
        btnEliminar.getStyleClass().add("boton-eliminar-carrito"); // Aplicar clase CSS para transparencia y estilo
        btnEliminar.setPrefWidth(80); // Ancho ajustado
        btnEliminar.setAlignment(Pos.CENTER); // Centrar el icono en el botón
        // El tamaño se controlará mejor desde CSS o con el fit del ImageView
        try {
            Image imgEliminar = new Image(getClass().getResourceAsStream("/img/moduloVentas/borrar.png"));
            ImageView ivEliminar = new ImageView(imgEliminar);
            ivEliminar.setFitHeight(20); // Ajusta el tamaño del icono
            ivEliminar.setFitWidth(20);  // Ajusta el tamaño del icono
            btnEliminar.setGraphic(ivEliminar);
        } catch (Exception e) {
            btnEliminar.setText("X"); // Fallback si no se carga el icono
            System.err.println("No se pudo cargar el icono de eliminar: " + e.getMessage());
        }

        btnEliminar.setOnAction(e -> {
            carritoBox.getChildren().remove(itemCarrito);
            BigDecimal subtotalItem = producto.getPrecioVenta().multiply(BigDecimal.valueOf(spCantidad.getValue()));
            totalVenta = totalVenta.subtract(subtotalItem);
            actualizarTotal();
        });

        spCantidad.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue == null || newValue < 1) {
                spCantidad.getValueFactory().setValue(oldValue != null && oldValue >=1 ? oldValue : 1); // Evitar valores nulos o menores a 1
                return;
            }
            if (newValue > producto.getStock()) {
                mostrarAlerta("Stock insuficiente", "Solo hay " + producto.getStock() + " unidades de " + producto.getNombre());
                spCantidad.getValueFactory().setValue(oldValue); // Revertir al valor anterior o al stock máximo
                return;
            }

            BigDecimal subtotalAnterior = producto.getPrecioVenta().multiply(BigDecimal.valueOf(oldValue != null ? oldValue : 0));
            BigDecimal subtotalNuevo = producto.getPrecioVenta().multiply(BigDecimal.valueOf(newValue));
            lblSubtotal.setText(formatoMoneda.format(subtotalNuevo));

            totalVenta = totalVenta.subtract(subtotalAnterior).add(subtotalNuevo);
            actualizarTotal();
        });
        // Forzar la actualización del listener para el valor inicial si es necesario, aunque el cálculo inicial ya lo hace.
        // totalVenta = totalVenta.add(producto.getPrecioVenta().multiply(BigDecimal.valueOf(spCantidad.getValue())));
        // actualizarTotal(); // Se llama después de añadir al carrito

        itemCarrito.getChildren().addAll(lblCodigo, lblNombre, cantidadContainer, lblPrecio, lblSubtotal, btnEliminar);

        // Insertar después del header (índice 1)
        if (carritoBox.getChildren().size() > 0) { // Asegurarse que el header existe
            carritoBox.getChildren().add(1, itemCarrito);
        } else {
            carritoBox.getChildren().add(itemCarrito); // Fallback si no hay header (no debería pasar)
        }

        // Actualizar total general
        BigDecimal subtotalInicialItem = producto.getPrecioVenta().multiply(BigDecimal.valueOf(spCantidad.getValue()));
        totalVenta = totalVenta.add(subtotalInicialItem);
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
    
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    private void procesarVenta() {
        if (carritoBox.getChildren().size() <= 1) { // Solo el header o vacío
            mostrarError("Carrito vacío", "No hay productos en el carrito para procesar la venta.");
            return;
        }

        List<ProductoVentaInfo> productosParaVenta = new ArrayList<>();
        for (Node itemNode : carritoBox.getChildren()) {
            if (itemNode instanceof HBox && itemNode.getUserData() instanceof ProductoFX) {
                HBox itemCarrito = (HBox) itemNode;
                ProductoFX productoEnCarrito = (ProductoFX) itemCarrito.getUserData();
                Spinner<Integer> spCantidad = null;

                // Buscar el VBox que contiene el Spinner
                VBox cantidadContainerNode = null;
                for (Node child : itemCarrito.getChildren()) {
                    if (child instanceof VBox) { // Asumimos que el VBox es el contenedor del spinner
                        VBox tempVBox = (VBox) child;
                        if (!tempVBox.getChildren().isEmpty() && tempVBox.getChildren().get(0) instanceof Spinner) {
                            cantidadContainerNode = tempVBox;
                            break;
                        }
                    }
                }

                if (cantidadContainerNode != null) {
                    try {
                        @SuppressWarnings("unchecked")
                        Spinner<Integer> tempSpinner = (Spinner<Integer>) cantidadContainerNode.getChildren().get(0);
                        spCantidad = tempSpinner;
                    } catch (ClassCastException ex) {
                        mostrarError("Error Interno", "El componente de cantidad no es del tipo esperado dentro de su contenedor.");
                        return;
                    }
                } else {
                    // Fallback por si el Spinner estuviera directamente (aunque no debería ser el caso ahora)
                    for (Node child : itemCarrito.getChildren()) {
                        if (child instanceof Spinner) {
                            try {
                                @SuppressWarnings("unchecked")
                                Spinner<Integer> tempSpinner = (Spinner<Integer>) child;
                                spCantidad = tempSpinner;
                                break;
                            } catch (ClassCastException ex) {
                                mostrarError("Error Interno", "El componente de cantidad no es del tipo esperado.");
                                return;
                            }
                        }
                    }
                }

                if (productoEnCarrito != null && spCantidad != null) {
                    productosParaVenta.add(new ProductoVentaInfo(
                            productoEnCarrito.getIdProducto(),
                            productoEnCarrito.getNombre(),
                            spCantidad.getValue(),
                            productoEnCarrito.getPrecioVenta()
                    ));
                } else {
                    mostrarError("Error Interno", "No se pudo obtener información completa del producto en el carrito.");
                    return;
                }
            }
        }

        if (productosParaVenta.isEmpty()) {
            mostrarError("Error en Carrito", "No se pudieron procesar los productos del carrito.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.DIALOG_PROCESAR_VENTA));
            DialogPane dialogContentPane = loader.load(); // Carga el DialogPane
            ProcesarVentaDialogController dialogController = loader.getController();

            dialogController.setDatosVenta(productosParaVenta, totalVenta);

            // Crear un Dialog<ButtonType> y establecer su DialogPane
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

            if (result.isPresent() && result.get() == dialogController.getBtnConfirmarType()) {
                dialogController.setConfirmado(true);
            } else {
                dialogController.setConfirmado(false);
            }

            if (dialogController.isConfirmado()) {
                VentaCreateRequestFX ventaRequest = new VentaCreateRequestFX(); // Cambio aquí
                // Asumiendo que tienes una forma de obtener el ID del vendedor actual.
                // Por ahora, usaremos un valor placeholder o uno predeterminado si es aplicable.
                // Si tienes un sistema de login, deberías obtener el ID del vendedor logueado.
                ventaRequest.setIdVendedor(1); // TODO: Reemplazar con el ID del vendedor real
                
                // El número de venta podría ser generado por el backend, pero si se requiere desde el frontend:
                ventaRequest.setNumeroVenta("VTA-" + System.currentTimeMillis());

                String formaPago = dialogController.getFormaDePago();
                ventaRequest.setTipoPago(formaPago); // Añadir forma de pago

                boolean requiereFactura = dialogController.isRequiereFactura();
                ventaRequest.setRequiereFactura(requiereFactura);

                if (requiereFactura) {
                    System.out.println("Factura solicitada para:");
                    System.out.println("  Documento: " + dialogController.getDocumentoCliente());
                    System.out.println("  Nombre: " + dialogController.getNombreCliente());
                    System.out.println("  Dirección: " + dialogController.getDireccionCliente());
                    System.out.println("  Teléfono: " + dialogController.getTelefonoCliente());
                    System.out.println("  Correo: " + dialogController.getCorreoCliente());
                    // Aquí deberías tener lógica para obtener/crear un ID de cliente si es necesario,
                    // o enviar los datos del cliente para que el backend los procese.
                    // Por ahora, se envía null como en el código original si no hay un cliente específico.
                    // int idClienteFactura = obtenerOcrearCliente(dialogController.getDocumentoCliente(), ...);
                    // ventaRequest.setIdCliente(idClienteFactura); 
                    ventaRequest.setIdCliente(null); // Placeholder o lógica para cliente de factura
                } else {
                    // Para ventas generales sin factura específica, se podría usar un ID de cliente genérico o null
                    // si el backend lo maneja como "Público General".
                    ventaRequest.setIdCliente(null); // Cliente general o nulo
                }

                // Según el markdown, aplicarImpuestos es un campo de la solicitud.
                // Asumimos que siempre se aplican impuestos a menos que haya una lógica específica para no hacerlo.
                ventaRequest.setAplicarImpuestos(true); 

                List<DetalleVentaCreateRequestFX> detalles = new ArrayList<>(); // Cambio aquí
                for (ProductoVentaInfo pInfo : productosParaVenta) {
                    DetalleVentaCreateRequestFX detalle = new DetalleVentaCreateRequestFX(); // Cambio aquí
                    detalle.setIdProducto(pInfo.getIdProducto());
                    detalle.setCantidad(pInfo.getCantidad());
                    // El precioUnitario no está en DetalleVentaCreateRequestFX porque el backend lo tomará del producto.
                    // Si se necesitara enviar explícitamente (por ejemplo, por promociones ya calculadas en frontend),
                    // se debería añadir el campo a DetalleVentaCreateRequestFX y establecerlo aquí.
                    detalles.add(detalle);
                }
                ventaRequest.setDetalles(detalles);

                VentaFX ventaResponse = ventaService.registrarVenta(ventaRequest); // Cambio aquí

                System.out.println("-----------------------------------------------------");
                System.out.println("VENTA REGISTRADA CON ÉXITO - SIMULACIÓN DE IMPRESIÓN");
                System.out.println("ID Venta: " + ventaResponse.getIdVenta());
                System.out.println("Fecha: " + ventaResponse.getFecha());
                System.out.println("Forma de Pago: " + formaPago);
                if (requiereFactura) {
                    System.out.println("Cliente: " + dialogController.getNombreCliente() + " (Doc: " + dialogController.getDocumentoCliente() + ")");
                    System.out.println("Dirección: " + dialogController.getDireccionCliente());
                }
                System.out.println("--- Detalles ---");
                for (ProductoVentaInfo pInfo : productosParaVenta) {
                    System.out.println(String.format("%s (x%d) - %s c/u - Subtotal: %s",
                            pInfo.getNombre(), pInfo.getCantidad(),
                            formatoMoneda.format(pInfo.getPrecioVentaUnitario()),
                            formatoMoneda.format(pInfo.getSubtotal())));
                }
                System.out.println("TOTAL VENTA: " + formatoMoneda.format(ventaResponse.getTotal()));
                System.out.println("-----------------------------------------------------");

                mostrarAlerta("Venta Procesada",
                        "Venta registrada con ID: " + ventaResponse.getIdVenta() +
                        "\nTotal: " + formatoMoneda.format(ventaResponse.getTotal()) +
                        "\nForma de pago: " + formaPago + "\n\nSimulando impresión en consola...");

                limpiarVenta();
                try {
                    cargarProductos();
                } catch (Exception loadEx) {
                    mostrarError("Error post-venta", "No se pudo recargar la lista de productos: " + loadEx.getMessage());
                }
            }
        } catch (IOException ioEx) {
            mostrarError("Error al abrir diálogo", "No se pudo cargar la ventana de procesamiento de venta: " + ioEx.getMessage());
            ioEx.printStackTrace();
        } catch (Exception e) {
            mostrarError("Error al procesar la venta", "Ocurrió un error general: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void limpiarVenta() {
        // Limpiar carrito excepto el header
        if (carritoBox.getChildren().size() > 1) {
            carritoBox.getChildren().remove(1, carritoBox.getChildren().size());
        }
        totalVenta = BigDecimal.ZERO;
        actualizarTotal();
        productosBox.getChildren().clear(); // Limpiar resultados de búsqueda
        txtBusqueda.clear();
    }
}