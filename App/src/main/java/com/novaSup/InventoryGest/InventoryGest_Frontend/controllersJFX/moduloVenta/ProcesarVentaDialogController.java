package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ClienteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.DetalleVentaCreateRequestFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoVentaInfo;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaCreateRequestFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IClienteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IFacturaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVendedorService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VendedorFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloClientes.AddEditClienteDialogController;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.concurrent.Task;
import javafx.scene.Cursor;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;
import java.awt.Desktop;

public class ProcesarVentaDialogController {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnConfirmar;
    
    @FXML
    private Button btnPrevisualizarFactura;

    @FXML
    private ToggleGroup facturaToggleGroup;

    @FXML
    private ToggleGroup metodoPagoToggleGroup;

    @FXML
    private VBox panelDatosCliente;
    
    @FXML
    private VBox clienteSeleccionadoInfo;

    @FXML
    private RadioButton rbEfectivo;

    @FXML
    private RadioButton rbFacturaNo;

    @FXML
    private RadioButton rbFacturaSi;

    @FXML
    private RadioButton rbTarjeta;

    @FXML
    private RadioButton rbTransferencia;

    @FXML
    private TextField txtBuscarCliente;

    @FXML
    private TextField txtCambioEntregar;

    // Estos campos están en el formulario incluido mediante fx:include
    // Se accederán mediante lookup en tiempo de ejecución

    @FXML
    private TextField txtTotalRecibido;

    @FXML
    private VBox vueltoBox;

    @FXML
    private Label lblMontoTotalDialog;

    private List<ProductoVentaInfo> productosEnVenta;
    private BigDecimal montoTotalVenta;
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    // Instancias de servicios
    private IClienteService clienteService;
    private IVentaSerivice ventaService;
    private IFacturaService facturaService;
    private IVendedorService vendedorService;
    // private IFacturaService facturaService;
    
    // Cliente seleccionado para la venta
    private ClienteFX clienteSeleccionado;

    // Popup para mostrar sugerencias de clientes
    private Popup clienteSugerenciaPopup;
    private ListView<ClienteFX> listViewClientes;
    private ObservableList<ClienteFX> clientesSugeridos = FXCollections.observableArrayList();
    
    // Controlador para el diálogo de añadir/editar cliente
    private AddEditClienteDialogController clienteDialogController;
    
    @FXML
    public void initialize() {
        // Inicializar el popup para sugerencias de clientes
        clienteSugerenciaPopup = new Popup();
        listViewClientes = new ListView<>(clientesSugeridos);
        
        // Configurar las pestañas de cliente
        try {
            // Obtener la referencia a las pestañas
            TabPane tabPane = (TabPane) panelDatosCliente.lookup(".client-tabs");
            if (tabPane != null && tabPane.getTabs().size() > 1) {
                // Configurar la pestaña de nuevo cliente para guardar el cliente cuando se cambia de pestaña
                tabPane.getSelectionModel().selectedItemProperty().addListener((obs, oldTab, newTab) -> {
                    // Si estamos cambiando desde la pestaña de nuevo cliente a la de buscar cliente
                    if (oldTab != null && oldTab.getText().equals("Nuevo Cliente") && 
                        newTab != null && newTab.getText().equals("Buscar Cliente")) {
                        // Verificar si se ha creado un cliente nuevo
                        if (clienteDialogController != null && clienteDialogController.isGuardadoExitosamente()) {
                            // Obtener el cliente recién creado
                            ClienteFX nuevoCliente = clienteDialogController.getClienteOperado();
                            if (nuevoCliente != null) {
                                // Establecer el cliente recién creado como el seleccionado
                                clienteSeleccionado = nuevoCliente;
                                // Mostrar los datos del cliente
                                mostrarDatosCliente(nuevoCliente);
                                // Limpiar el campo de búsqueda
                                txtBuscarCliente.clear();
                            }
                        }
                    }
                });
            }
        } catch (Exception e) {
            System.err.println("Error al configurar las pestañas de cliente: " + e.getMessage());
            e.printStackTrace();
        }
        metodoPagoToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            boolean esEfectivo = newValue == rbEfectivo;
            vueltoBox.setVisible(esEfectivo);
            vueltoBox.setManaged(esEfectivo);
            if (!esEfectivo) {
                txtTotalRecibido.clear();
                txtCambioEntregar.setText(formatoMoneda.format(BigDecimal.ZERO));
            } else {
                 // Si cambia a efectivo, recalcular el cambio
                 calcularCambio();
            }
        });

        facturaToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            boolean requiereFactura = newValue == rbFacturaSi;
            panelDatosCliente.setVisible(requiereFactura);
            panelDatosCliente.setManaged(requiereFactura);
            
            // Ajustar la altura del diálogo según si se muestra o no el panel de datos del cliente
            Stage stage = (Stage) panelDatosCliente.getScene().getWindow();
            if (stage != null) {
                if (requiereFactura) {
                    // Si se requiere factura, asegurarse de que el diálogo tenga suficiente altura
                    if (stage.getHeight() < 650) {
                        stage.setHeight(650);
                    }
                } else {
                    // Si no se requiere factura, restablecer la altura original (o una altura adecuada)
                     if (stage.getHeight() > 400) { // Altura aproximada sin el panel de cliente
                         stage.setHeight(400); // Ajusta este valor según el diseño de tu diálogo
                     }
                }
            }
            // Deshabilitar el botón de previsualizar si no se requiere factura
            if (btnPrevisualizarFactura != null) {
                 btnPrevisualizarFactura.setDisable(!requiereFactura);
            }
        });

        txtTotalRecibido.textProperty().addListener((observable, oldValue, newValue) -> {
            calcularCambio();
        });
        
        // Inicializar el popup (pero la configuración completa se hará en setServices)
        clienteSugerenciaPopup = new Popup();
        listViewClientes = new ListView<>(clientesSugeridos);

        // Establecer estado inicial
        rbEfectivo.setSelected(true); // Por defecto efectivo
        rbFacturaNo.setSelected(true); // Por defecto no factura

        // Forzar la actualización inicial de la visibilidad de los paneles
        boolean esEfectivoInicial = rbEfectivo.isSelected();
        vueltoBox.setVisible(esEfectivoInicial);
        vueltoBox.setManaged(esEfectivoInicial);

        boolean requiereFacturaInicial = rbFacturaSi.isSelected();
        panelDatosCliente.setVisible(requiereFacturaInicial);
        panelDatosCliente.setManaged(requiereFacturaInicial);
        
        // Inicializar el contenedor de información del cliente seleccionado
        if (clienteSeleccionadoInfo != null) {
            // Limpiar el contenedor
            clienteSeleccionadoInfo.getChildren().clear();
            
            // Agregar solo la etiqueta de título
            Label titleLabel = new Label("Cliente seleccionado:");
            titleLabel.setStyle("-fx-font-weight: bold;");
            
            Label infoLabel = new Label("No hay cliente seleccionado");
            infoLabel.setStyle("-fx-font-style: italic; -fx-text-fill: #888888;");
            
            clienteSeleccionadoInfo.getChildren().addAll(titleLabel, infoLabel);
        }

        txtCambioEntregar.setText(formatoMoneda.format(BigDecimal.ZERO));

        btnCancelar.setOnAction(event -> cerrarDialogo(false));
        btnConfirmar.setOnAction(event -> confirmarVenta());
        
        // Configurar la acción del botón Previsualizar Factura
        if (btnPrevisualizarFactura != null) {
            btnPrevisualizarFactura.setOnAction(event -> handlePrevisualizarFactura());
             // Deshabilitar inicialmente si no se requiere factura
            btnPrevisualizarFactura.setDisable(!requiereFacturaInicial);
        }
        
        // Configurar la búsqueda de cliente al presionar Enter en el campo de texto
        if (txtBuscarCliente != null) {
            txtBuscarCliente.setOnAction(event -> buscarCliente());
        }
    }

    public void setDatosVenta(List<ProductoVentaInfo> productos, BigDecimal total) {
        this.productosEnVenta = productos;
        this.montoTotalVenta = total;
        if (lblMontoTotalDialog != null) {
            lblMontoTotalDialog.setText(formatoMoneda.format(this.montoTotalVenta));
        }
        calcularCambio();
    }

    /**
     * Método para la inyección de dependencias (servicios).
     * Este método se llamará desde VenderControllerFX después de crear una instancia de este controlador.
     */
    public void setServices(IVentaSerivice ventaService, IClienteService clienteService, IFacturaService facturaService, 
                           IVendedorService vendedorService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        this.facturaService = facturaService;
        this.vendedorService = vendedorService;
        // Inicializar otros servicios cuando estén disponibles
        
        // Inicializar el controlador de cliente si está disponible
        inicializarControladorCliente();
        
        // Ahora que tenemos los servicios inyectados, configuramos la búsqueda de cliente
        configurarBusquedaCliente();
    }
    
    /**
     * Inicializa el controlador de cliente si está disponible en el FXML
     */
    private void inicializarControladorCliente() {
        try {
            // Buscar el contenedor del formulario de cliente
            TabPane tabPane = (TabPane) panelDatosCliente.lookup(".client-tabs");
            if (tabPane != null && tabPane.getTabs().size() > 1) {
                Tab nuevoClienteTab = tabPane.getTabs().get(1); // Tab "Nuevo Cliente"
                if (nuevoClienteTab != null) {
                    // Obtener el ScrollPane que contiene el StackPane
                    ScrollPane scrollPane = (ScrollPane) nuevoClienteTab.getContent();
                    if (scrollPane != null && scrollPane.getContent() instanceof StackPane) {
                        StackPane clienteDialogContainer = (StackPane) scrollPane.getContent();
                        
                        // Obtener el controlador a través del FXML Loader
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ModuloClientes/AddEditClienteDialog.fxml"));
                        try {
                            // Cargar el FXML
                            Parent root = loader.load();
                            
                            // Obtener el controlador
                            clienteDialogController = loader.getController();
                            
                            // Reemplazar el contenido existente con el nuevo
                            clienteDialogContainer.getChildren().clear();
                            clienteDialogContainer.getChildren().add(root);
                            
                            // Configurar el controlador con los servicios necesarios
                            if (clienteDialogController != null && clienteService != null) {
                                // Inicializar el controlador con un nuevo cliente
                                clienteDialogController.setClienteParaOperacion(null, clienteService);
                                
                                // Configurar el stage del diálogo si es necesario
                                if (btnConfirmar.getScene() != null && btnConfirmar.getScene().getWindow() != null) {
                                    clienteDialogController.setDialogStage((Stage) btnConfirmar.getScene().getWindow());
                                }
                                
                                // Configurar botones del formulario de cliente
                                Button saveButton = (Button) root.lookup("#saveButton");
                                if (saveButton != null) {
                                    // Reemplazar la acción original del botón guardar
                                    saveButton.setOnAction(event -> {
                                        boolean guardadoExitoso = clienteDialogController.handleGuardar();
                                        if (guardadoExitoso) {
                                            // Obtener el cliente recién guardado
                                            ClienteFX clienteGuardado = clienteDialogController.getClienteOperado();
                                            if (clienteGuardado != null) {
                                                // Seleccionar automáticamente el cliente para la venta
                                                clienteSeleccionado = clienteGuardado;
                                                
                                                // Cambiar a la pestaña de búsqueda (índice 0)
                                                tabPane.getSelectionModel().select(0);
                                                
                                                // Actualizar el campo de búsqueda y mostrar los datos del cliente
                                                txtBuscarCliente.setText(clienteGuardado.getNombre());
                                                mostrarDatosCliente(clienteGuardado);
                                                
                                                // Notificar al usuario que el cliente ha sido creado y seleccionado
                                                mostrarAlerta("Cliente Guardado", "El cliente ha sido creado exitosamente y seleccionado para esta venta.");
                                            }
                                        }
                                    });
                                }
                            } else {
                                System.err.println("Error: clienteDialogController o clienteService es null");
                                if (clienteDialogController == null) System.err.println("clienteDialogController es null");
                                if (clienteService == null) System.err.println("clienteService es null");
                            }
                        } catch (Exception e) {
                            System.err.println("Error al cargar el FXML del cliente: " + e.getMessage());
                            e.printStackTrace();
                        }
                    } else {
                        System.err.println("Error: No se encontró el StackPane dentro del ScrollPane");
                    }
                } else {
                    System.err.println("Error: No se encontró la pestaña 'Nuevo Cliente'");
                }
            }
        } catch (Exception e) {
            System.err.println("Error general al inicializar el controlador de cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Obtiene el ID del vendedor asociado al usuario que está logueado actualmente.
     * @return ID del vendedor o -1 si no se encuentra vendedor asociado
     */
    private int obtenerIdVendedorLogueado() {
        try {
            // Obtener el ID del usuario logueado
            int idUsuario = LoginServiceImplFX.getIdUsuario();
            
            if (idUsuario <= 0) {
                System.err.println("Error: No hay usuario logueado o ID de usuario inválido");
                return -1;
            }
            
            // Obtener el vendedor asociado al usuario
            VendedorFX vendedor = vendedorService.obtenerVendedorConUsuario(idUsuario);
            
            if (vendedor != null && vendedor.getIdVendedor() > 0) {
                return vendedor.getIdVendedor();
            } else {
                System.err.println("Error: No se encontró vendedor asociado al usuario ID: " + idUsuario);
                return -1;
            }
            
        } catch (Exception e) {
            System.err.println("Error al obtener vendedor del usuario logueado: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }
    
    private void calcularCambio() {
        if (montoTotalVenta == null || !rbEfectivo.isSelected()) {
            txtCambioEntregar.setText(formatoMoneda.format(BigDecimal.ZERO));
            return;
        }

        String totalRecibidoStr = txtTotalRecibido.getText().replace(".", "").replace(",", "."); // Adaptar a formato numérico estándar
        if (totalRecibidoStr.isEmpty()) {
            txtCambioEntregar.setText(formatoMoneda.format(BigDecimal.ZERO));
            return;
        }

        try {
            BigDecimal totalRecibido = new BigDecimal(totalRecibidoStr);
            BigDecimal cambio = totalRecibido.subtract(montoTotalVenta);
            txtCambioEntregar.setText(formatoMoneda.format(cambio.compareTo(BigDecimal.ZERO) < 0 ? BigDecimal.ZERO : cambio));
        } catch (NumberFormatException e) {
            txtCambioEntregar.setText(formatoMoneda.format(BigDecimal.ZERO));
        }
    }
    
    /**
     * Maneja la acción de previsualizar la factura.
     */
    private void handlePrevisualizarFactura() {
        // Validar si se requiere factura y si hay un cliente seleccionado
        if (!rbFacturaSi.isSelected()) {
            mostrarAlerta("Previsualización no disponible", "Debe seleccionar 'Sí' en 'Factura Legal' para previsualizar la factura.");
            return;
        }
        
        if (clienteSeleccionado == null) {
             mostrarAlerta("Cliente no seleccionado", "Debe seleccionar un cliente para previsualizar la factura.");
             return;
        }
        
        if (productosEnVenta == null || productosEnVenta.isEmpty()) {
            mostrarAlerta("Carrito Vacío", "No hay productos en la venta para previsualizar la factura.");
            return;
        }

        // Deshabilitar botones y mostrar indicador de carga (opcional)
        setControlesDeshabilitados(true);

        // Crear el VentaCreateRequestFX
        VentaCreateRequestFX ventaRequest = new VentaCreateRequestFX();
        ventaRequest.setIdCliente(clienteSeleccionado.getIdCliente());
        // Asumiendo un ID de vendedor por defecto (puedes ajustar esto según tu lógica real)
        ventaRequest.setIdVendedor(obtenerIdVendedorLogueado()); // ID del vendedor, por ejemplo, 1 para el vendedor por defecto
        ventaRequest.setRequiereFactura(true);
        ventaRequest.setAplicarImpuestos(true);
        // El número de venta y tipo de pago no son estrictamente necesarios para la previsualización según tu JSON de ejemplo, los omitimos.
        // ventaRequest.setNumeroVenta("PREVIEW-" + System.currentTimeMillis()); // Opcional, para referencia
        // ventaRequest.setTipoPago(obtenerTipoPagoSeleccionado()); // Opcional

        
        List<DetalleVentaCreateRequestFX> detalles = new ArrayList<>();
        for (ProductoVentaInfo producto : productosEnVenta) {
            DetalleVentaCreateRequestFX detalle = new DetalleVentaCreateRequestFX();
            detalle.setIdProducto(producto.getIdProducto());
            detalle.setCantidad(producto.getCantidad());
            detalles.add(detalle);
        }
        ventaRequest.setDetalles(detalles);

        // Crear una tarea en segundo plano para llamar al servicio y manejar la respuesta
        Task<byte[]> previsualizarTask = new Task<>() {
            @Override
            protected byte[] call() throws Exception {
                // Llamar al servicio para obtener el PDF de previsualización
                return facturaService.getPreviewFacturaPdf(ventaRequest);
            }

            @Override
            protected void succeeded() {
                super.succeeded();
                // Re-habilitar controles y ocultar indicador de carga
                setControlesDeshabilitados(false);

                byte[] pdfBytes = getValue();
                if (pdfBytes != null && pdfBytes.length > 0) {
                    // Guardar y abrir el PDF
                    try {
                        File tempFile = File.createTempFile("preview_factura_", ".pdf");
                        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                            fos.write(pdfBytes);
                        }
                        
                        // Abrir el archivo con la aplicación predeterminada del sistema
                        if (Desktop.isDesktopSupported()) {
                            Desktop.getDesktop().open(tempFile);
                        } else {
                            mostrarAlerta("Error", "La apertura automática de archivos no es compatible con su sistema.");
                        }
                        
                        // Opcional: eliminar el archivo temporal al cerrar la aplicación
                        tempFile.deleteOnExit();

                    } catch (IOException e) {
                        mostrarAlerta("Error al abrir PDF", "No se pudo guardar o abrir el archivo PDF de previsualización: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    mostrarAlerta("Error al obtener PDF", "No se recibieron datos de PDF válidos.");
                }
            }

            @Override
            protected void failed() {
                super.failed();
                // Re-habilitar controles y ocultar indicador de carga
                setControlesDeshabilitados(false);
                Throwable cause = getException();
                 mostrarAlerta("Error en el servicio", "Ocurrió un error al previsualizar la factura: " + cause.getMessage());
                 cause.printStackTrace();
            }
        };

        // Ejecutar la tarea en un hilo separado
        new Thread(previsualizarTask).start();
    }
    
    /**
     * Habilita o deshabilita los controles principales del diálogo mientras se procesa una operación.
     * @param disabled true para deshabilitar, false para habilitar.
     */
    private void setControlesDeshabilitados(boolean disabled) {
        btnConfirmar.setDisable(disabled);
        btnCancelar.setDisable(disabled);
        btnPrevisualizarFactura.setDisable(disabled || !rbFacturaSi.isSelected()); // Mantener deshabilitado si no se requiere factura
        txtTotalRecibido.setDisable(disabled);
        txtBuscarCliente.setDisable(disabled);
        rbEfectivo.setDisable(disabled);
        rbTarjeta.setDisable(disabled);
        rbTransferencia.setDisable(disabled);
        rbFacturaSi.setDisable(disabled);
        rbFacturaNo.setDisable(disabled);
        // También puedes deshabilitar las pestañas si lo consideras necesario
         TabPane tabPane = (TabPane) panelDatosCliente.lookup(".client-tabs");
         if (tabPane != null) {
             tabPane.setDisable(disabled);
         }
        // Cambiar el cursor para indicar que algo está sucediendo
        if (btnConfirmar.getScene() != null) {
            btnConfirmar.getScene().setCursor(disabled ? Cursor.WAIT : Cursor.DEFAULT);
        }
    }

    private void confirmarVenta() {
        try {
            // Validar que se haya ingresado un monto si el pago es en efectivo
            if (rbEfectivo.isSelected() && (txtTotalRecibido.getText() == null || txtTotalRecibido.getText().trim().isEmpty())) {
                mostrarAlerta("Datos Incompletos", "Debe ingresar el monto recibido para pagos en efectivo.");
                return;
            }

            // Crear el objeto de solicitud de venta
            VentaCreateRequestFX ventaRequest = new VentaCreateRequestFX();

            // Si se requiere factura, validar y agregar datos del cliente
            if (rbFacturaSi.isSelected()) {
                // Verificar si hay un cliente seleccionado
                if (clienteSeleccionado == null) {
                    // Verificar si estamos en la pestaña de nuevo cliente
                    TabPane tabPane = (TabPane) panelDatosCliente.lookup(".client-tabs");
                    if (tabPane != null && tabPane.getSelectionModel().getSelectedIndex() == 1) {
                        // Estamos en la pestaña de nuevo cliente, intentar guardar el cliente
                        if (clienteDialogController != null) {
                            // Llamar al método de guardar del controlador de cliente
                            boolean guardadoExitoso = clienteDialogController.guardarCliente();

                            // Verificar si se guardó correctamente
                            if (guardadoExitoso && clienteDialogController.isGuardadoExitosamente()) {
                                // Obtener el cliente recién creado
                                clienteSeleccionado = clienteDialogController.getClienteOperado();
                            } else {
                                // No se pudo guardar el cliente
                                mostrarAlerta("Error al Guardar Cliente", "No se pudo guardar el cliente. Por favor, verifique los datos ingresados.");
                                return;
                            }
                        }
                    } else {
                        // Estamos en la pestaña de buscar cliente pero no hay cliente seleccionado
                        mostrarAlerta("Datos Incompletos", "Debe seleccionar un cliente para generar factura o crear uno nuevo.");
                        return;
                    }
                }

                // Solo establecer que requiere factura y el ID del cliente si está disponible
                ventaRequest.setRequiereFactura(true);

                // Si hay un cliente seleccionado, agregar su ID
                if (clienteSeleccionado != null && clienteSeleccionado.getIdCliente() > 0) {
                    ventaRequest.setIdCliente(clienteSeleccionado.getIdCliente());
                }

                // Obtener el ID del vendedor actual del usuario logueado
                int idVendedor = obtenerIdVendedorLogueado();
                if (idVendedor <= 0) {
                    mostrarAlerta("Error de Vendedor", "No se pudo obtener el vendedor asociado al usuario logueado. Por favor, verifique que su usuario tenga un vendedor asignado.");
                    return;
                }
                ventaRequest.setIdVendedor(idVendedor);

                // Establecer que se apliquen impuestos para facturas
                ventaRequest.setAplicarImpuestos(true);
            } else {
                // Si no requiere factura, establecer como venta sin factura
                ventaRequest.setRequiereFactura(false);
                
                // Obtener el ID del vendedor actual del usuario logueado
                int idVendedor = obtenerIdVendedorLogueado();
                if (idVendedor <= 0) {
                    mostrarAlerta("Error de Vendedor", "No se pudo obtener el vendedor asociado al usuario logueado. Por favor, verifique que su usuario tenga un vendedor asignado.");
                    return;
                }
                ventaRequest.setIdVendedor(idVendedor);
                
                ventaRequest.setAplicarImpuestos(false); // Sin impuestos para ventas sin factura
            }

            // Obtener el tipo de pago seleccionado
            String tipoPago = "EFECTIVO"; // Valor predeterminado
            if (rbTarjeta.isSelected()) {
                tipoPago = "TARJETA";
            } else if (rbTransferencia.isSelected()) {
                tipoPago = "TRANSFERENCIA";
            }
            ventaRequest.setTipoPago(tipoPago);

            // Crear los detalles de venta a partir de los productos seleccionados
            List<DetalleVentaCreateRequestFX> detalles = new ArrayList<>();
            for (ProductoVentaInfo producto : productosEnVenta) {
                DetalleVentaCreateRequestFX detalle = new DetalleVentaCreateRequestFX();
                detalle.setIdProducto(producto.getIdProducto());
                detalle.setCantidad(producto.getCantidad());
                detalles.add(detalle);
            }
            ventaRequest.setDetalles(detalles);

            // Registrar la venta usando el servicio y obtener la VentaFX registrada (que contiene el ID)
            try {
                VentaFX ventaRegistrada = ventaService.registrarVenta(ventaRequest);
                int idVentaRegistrada = ventaRegistrada.getIdVenta(); // Obtener el ID de la venta registrada

                // Mostrar mensaje de éxito general de la venta
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Venta Exitosa");
                alert.setHeaderText(null);
                alert.setContentText("Venta registrada correctamente");
                alert.show(); // Mostrar la alerta no modal, para que no bloquee la apertura del PDF

                // Si la venta se registró correctamente (llegamos aquí sin lanzar excepción),
                // procedemos a generar y mostrar la factura si se requiere
                // La variable 'idVentaRegistrada' se obtiene directamente del VentaFX retornado.
                if (rbFacturaSi.isSelected() && idVentaRegistrada > 0) {
                    try {
                        // Obtener el PDF oficial de la factura usando el ID de la venta
                        byte[] pdfBytes = facturaService.getFacturaPdfByIdVenta(idVentaRegistrada);

                        if (pdfBytes != null && pdfBytes.length > 0) {
                            // Guardar y abrir el PDF
                            File tempFile = File.createTempFile("factura_", ".pdf");
                            try (FileOutputStream fos = new FileOutputStream(tempFile)) {
                                fos.write(pdfBytes);
                            }

                            if (Desktop.isDesktopSupported() && tempFile.exists()) {
                                Desktop.getDesktop().open(tempFile);
                                // Opcional: eliminar el archivo temporal al cerrar la aplicación
                                tempFile.deleteOnExit();
                            } else {
                                mostrarAlerta("Éxito - PDF Generado", "La factura ha sido generada, pero la apertura automática de archivos no es compatible con su sistema. Busque el archivo temporal.");
                            }
                        } else {
                            mostrarAlerta("Éxito - Sin PDF", "Venta registrada, pero no se recibieron datos de PDF válidos para la factura.");
                        }
                    } catch (Exception e) {
                        mostrarAlerta("Éxito - Error PDF", "Venta registrada, pero ocurrió un error al obtener o mostrar la factura en PDF: " + e.getMessage());
                        e.printStackTrace();
                    }
                }

                // Cerrar el diálogo solo si la venta se registró correctamente y se intentó mostrar la factura (o no se requirió)
                cerrarDialogo(true);

            } catch (Exception ex) {
                // Si hay un error de deserialización pero la venta se realizó correctamente
                if (ex.getMessage() != null && ex.getMessage().contains("Conflicting setter definitions for property \"fecha\"")) {
                    // Mostrar mensaje de éxito a pesar del error de deserialización
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Venta Exitosa");
                    alert.setHeaderText(null);
                    alert.setContentText("Venta registrada correctamente. Advertencia: Error al procesar respuesta del servidor (el PDF podría no abrirse automáticamente).");
                    alert.showAndWait();

                    // Cerrar el diálogo
                    cerrarDialogo(true);
                } else {
                    // Si no fue un error de deserialización conocido, mostrar alerta de error y no cerrar el diálogo (para que el usuario pueda reintentar o revisar)
                    mostrarAlerta("Error al Registrar Venta", "Ocurrió un error al intentar registrar la venta: " + ex.getMessage());
                    ex.printStackTrace(); // Imprimir stack trace para depuración
                }
            }

        } catch (Exception e) {
            // Mostrar mensaje de error general si ocurre algo antes de intentar registrar la venta
            mostrarAlerta("Error al Procesar Venta", "Ocurrió un error inesperado antes de registrar la venta: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cerrarDialogo(boolean ventaConfirmada) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        // El Dialog en VenderControllerFX espera un ButtonType.OK_DONE para confirmar.
        Object userData = stage.getUserData();
        
        if (userData instanceof Dialog<?>) {
            try {
                @SuppressWarnings("unchecked")
                Dialog<ButtonType> dialog = (Dialog<ButtonType>) userData;
                
                if (ventaConfirmada) {
                    // Establecer el resultado del diálogo como OK_DONE para que VenderControllerFX sepa que la venta fue exitosa
                    dialog.setResult(ButtonType.OK);
                } else {
                    // Establecer el resultado del diálogo como CANCEL_CLOSE para que VenderControllerFX sepa que la venta fue cancelada
                    dialog.setResult(ButtonType.CANCEL);
                }
            } catch (ClassCastException e) {
                System.err.println("Error al convertir el userData a Dialog<ButtonType>: " + e.getMessage());
            } catch (Exception e) { // Captura cualquier otra excepción aquí
                 System.err.println("Error inesperado al cerrar diálogo: " + e.getMessage());
            }
        }
        
        stage.close();
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
    
    /**
     * Método para buscar un cliente por nombre o documento de identidad
     */
    private void buscarCliente() {
        // Cerrar el popup de sugerencias si está abierto
        if (clienteSugerenciaPopup != null && clienteSugerenciaPopup.isShowing()) {
            clienteSugerenciaPopup.hide();
        }
        
        String busqueda = txtBuscarCliente.getText().trim();
        
        if (busqueda.isEmpty()) {
            mostrarAlerta("Búsqueda vacía", "Ingrese un nombre o documento para buscar un cliente.");
            return;
        }
        
        try {
            // Si parece ser un documento de identidad (solo números) y tiene longitud suficiente
            // Buscar por documento exacto (cédula o identificación fiscal)
            if (busqueda.matches("\\d+")) {
                // Buscar por documento solo si parece ser un documento completo
                // (asumiendo que un documento de identidad tiene al menos 6 dígitos)
                if (busqueda.length() >= 6) {
                    Optional<ClienteFX> clientePorDocumento = clienteService.obtenerClientePorCedula(busqueda);
                    
                    if (!clientePorDocumento.isPresent()) {
                        // Si no se encuentra por cédula, intentar por identificación fiscal
                        clientePorDocumento = clienteService.obtenerClientePorIdentificacionFiscal(busqueda);
                    }
                    
                    if (clientePorDocumento.isPresent()) {
                        // Cliente encontrado por documento, mostrar sus datos
                        mostrarDatosCliente(clientePorDocumento.get());
                        return;
                    }
                } else {
                    // Si el documento no está completo, mostrar mensaje
                    mostrarAlerta("Documento incompleto", "Para buscar por documento de identidad, ingrese el documento completo.");
                    return;
                }
            } else {
                // Si no es un documento numérico, buscar por nombre exacto
                Optional<ClienteFX> clientePorNombre = clienteService.obtenerClientePorNombre(busqueda);
                
                if (clientePorNombre.isPresent()) {
                    // Cliente encontrado por nombre exacto, mostrar sus datos
                    mostrarDatosCliente(clientePorNombre.get());
                    return;
                }
            }
            
            // Si no se encuentra el cliente, preguntar si desea crearlo
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Cliente no encontrado");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("No se encontró ningún cliente con ese nombre o documento. ¿Desea crear un nuevo cliente?");
            
            Optional<ButtonType> resultado = confirmacion.showAndWait();
            if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
                // El usuario quiere crear un nuevo cliente, cambiar a la pestaña de nuevo cliente
                TabPane tabPane = (TabPane) panelDatosCliente.lookup(".client-tabs");
                if (tabPane != null && tabPane.getTabs().size() > 1) {
                    tabPane.getSelectionModel().select(1); // Seleccionar la pestaña "Nuevo Cliente"
                    
                    // Prellenar el campo de documento si parece ser un número de documento
                    if (busqueda.matches("\\d+")) {
                        TextField txtDocumentoNuevo = (TextField) tabPane.lookup("#txtDocumento");
                        if (txtDocumentoNuevo != null) {
                            txtDocumentoNuevo.setText(busqueda);
                        }
                    } else {
                        // Si parece ser un nombre, prellenar el campo de nombre
                        TextField txtNombreNuevo = (TextField) tabPane.lookup("#txtNombre");
                        if (txtNombreNuevo != null) {
                            txtNombreNuevo.setText(busqueda);
                        }
                    }
                }
            }
            
        } catch (Exception e) {
            mostrarAlerta("Error al buscar cliente", "Ocurrió un error al buscar el cliente: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Método para mostrar los datos de un cliente en el formulario
     * @param cliente El cliente cuyos datos se mostrarán
     */
    private void mostrarDatosCliente(ClienteFX cliente) {
        // Guardar el cliente seleccionado
        this.clienteSeleccionado = cliente;
        
        // Activar la pestaña "Requiere Factura" para mostrar los datos del cliente
        rbFacturaSi.setSelected(true);
        
        // Actualizar la información en el contenedor de cliente seleccionado
        if (clienteSeleccionadoInfo != null) {
            // Limpiar el contenedor antes de agregar nueva información
            clienteSeleccionadoInfo.getChildren().clear();
            
            // Agregar los datos del cliente en formato legible
            Label nombreLabel = new Label("Nombre: " + cliente.getNombre());
            nombreLabel.setStyle("-fx-font-weight: normal;");
            
            Label documentoLabel = new Label("Documento: " + cliente.getDocumentoIdentidad());
            documentoLabel.setStyle("-fx-font-weight: normal;");
            
            Label correoLabel = new Label("Correo: " + (cliente.getCorreo() != null ? cliente.getCorreo() : "No disponible"));
            correoLabel.setStyle("-fx-font-weight: normal;");
            
            Label telefonoLabel = new Label("Teléfono: " + (cliente.getCelular() != null ? cliente.getCelular() : "No disponible"));
            telefonoLabel.setStyle("-fx-font-weight: normal;");
            
            Label direccionLabel = new Label("Dirección: " + (cliente.getDireccion() != null ? cliente.getDireccion() : "No disponible"));
            direccionLabel.setStyle("-fx-font-weight: normal;");
            
            // Agregar etiqueta de cliente seleccionado
            Label titleLabel = new Label("Cliente seleccionado:");
            titleLabel.setStyle("-fx-font-weight: bold;");
            
            // Agregar todos los elementos al contenedor
            clienteSeleccionadoInfo.getChildren().addAll(
                titleLabel, nombreLabel, documentoLabel, correoLabel, telefonoLabel, direccionLabel
            );
            
            // Establecer estilo para que se vea como una tarjeta
            clienteSeleccionadoInfo.setStyle("-fx-padding: 10; -fx-border-color: #e0e0e0; -fx-border-width: 1; -fx-border-radius: 3; -fx-background-color: #f9f9f9;");
        }
        
        // Buscar los campos en el panel de datos del cliente (para compatibilidad con código existente)
        try {
            // Buscar los campos de texto dentro del panel de datos del cliente
            TextField nombreField = (TextField) panelDatosCliente.lookup("#txtNombre");
            TextField documentoField = (TextField) panelDatosCliente.lookup("#txtDocumento");
            TextField correoField = (TextField) panelDatosCliente.lookup("#txtCorreo");
            TextField telefonoField = (TextField) panelDatosCliente.lookup("#txtTelefono");
            TextField direccionField = (TextField) panelDatosCliente.lookup("#txtDireccion");
            
            // Establecer los valores si los campos existen
            if (nombreField != null) nombreField.setText(cliente.getNombre());
            if (documentoField != null) documentoField.setText(cliente.getDocumentoIdentidad());
            if (correoField != null) correoField.setText(cliente.getCorreo());
            if (telefonoField != null) telefonoField.setText(cliente.getCelular());
            if (direccionField != null) direccionField.setText(cliente.getDireccion());
        } catch (Exception e) {
            System.err.println("Error al intentar mostrar datos del cliente en campos antiguos: " + e.getMessage());
            // No es crítico si falla, ya que tenemos la nueva visualización
        }
        
        // Mostrar un mensaje de éxito más discreto (toast o notificación)
        javafx.application.Platform.runLater(() -> {
            // Crear un popup tipo toast para mostrar confirmación
            Label toastLabel = new Label("Cliente seleccionado: " + cliente.getNombre());
            toastLabel.setStyle("-fx-background-color: #333333; -fx-text-fill: white; -fx-padding: 10 15; -fx-background-radius: 5;");
            
            Popup toast = new Popup();
            toast.getContent().add(toastLabel);
            toast.setAutoHide(true);
            toast.setHideOnEscape(true);
            
            // Mostrar el toast en la parte inferior de la ventana
            Window window = panelDatosCliente.getScene().getWindow();
            toast.show(window, 
                    window.getX() + (window.getWidth() - toastLabel.getWidth()) / 2, 
                    window.getY() + window.getHeight() - 100);
            
            // Ocultar después de 2 segundos
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    javafx.application.Platform.runLater(() -> toast.hide());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        });
        
        // Cerrar el popup de sugerencias si está abierto
        if (clienteSugerenciaPopup != null && clienteSugerenciaPopup.isShowing()) {
            clienteSugerenciaPopup.hide();
        }
        
        // Actualizar el campo de búsqueda con el nombre del cliente seleccionado
        txtBuscarCliente.setText(cliente.getNombre());
    }
    
    /**
     * Configura el campo de búsqueda de cliente para mostrar sugerencias
     */
    private void configurarBusquedaCliente() {
        // Los componentes ya se inicializaron en initialize(), aquí solo configuramos
        if (clienteService == null) {
            System.err.println("Error: configurarBusquedaCliente llamado antes de inicializar servicios");
            return; // Salir si el servicio no está disponible aún
        }
        
        // Limpiar contenido previo para prevenir duplicaciones
        if (!clienteSugerenciaPopup.getContent().isEmpty()) {
            clienteSugerenciaPopup.getContent().clear();
        }
        
        // Configurar el estilo de la lista
        listViewClientes.setPrefWidth(300);
        listViewClientes.setPrefHeight(200);
        // Aplicamos estilos inline en lugar de confiar solo en la clase CSS
        listViewClientes.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1px;");
        listViewClientes.getStyleClass().add("cliente-sugerencia-lista");
        
        // Configurar cómo se muestran los clientes en la lista
        listViewClientes.setCellFactory(lv -> new ListCell<ClienteFX>() {
            @Override
            protected void updateItem(ClienteFX cliente, boolean empty) {
                super.updateItem(cliente, empty);
                if (empty || cliente == null) {
                    setText(null);
                } else {
                    setText(cliente.getNombre() + " - " + cliente.getDocumentoIdentidad());
                    setStyle("-fx-padding: 5px;");
                }
            }
        });
        
        // Manejar la selección de un cliente de la lista
        listViewClientes.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY) {
                ClienteFX clienteSeleccionado = listViewClientes.getSelectionModel().getSelectedItem();
                if (clienteSeleccionado != null) {
                    // Asignar cliente seleccionado a la variable de clase
                    this.clienteSeleccionado = clienteSeleccionado;
                    // Actualizar campo de texto con el nombre del cliente
                    txtBuscarCliente.setText(clienteSeleccionado.getNombre());
                    // Mostrar datos del cliente
                    mostrarDatosCliente(clienteSeleccionado);
                    // Ocultar popup
                    clienteSugerenciaPopup.hide();
                }
            }
        });
        
        // Agregar la lista al popup
        clienteSugerenciaPopup.getContent().add(listViewClientes);
        
        // Configurar el campo de texto para mostrar sugerencias mientras se escribe
        txtBuscarCliente.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && !newValue.trim().isEmpty() && newValue.length() >= 2) {
                buscarClientesSugeridos(newValue);
            } else {
                if (clienteSugerenciaPopup.isShowing()) {
                    clienteSugerenciaPopup.hide();
                }
            }
        });
        
        // Configurar teclas para navegar por la lista de sugerencias
        txtBuscarCliente.setOnKeyPressed(event -> {
            if (clienteSugerenciaPopup.isShowing()) {
                if (event.getCode() == KeyCode.DOWN) {
                    listViewClientes.requestFocus();
                    listViewClientes.getSelectionModel().selectFirst();
                } else if (event.getCode() == KeyCode.ESCAPE) {
                    clienteSugerenciaPopup.hide();
                } else if (event.getCode() == KeyCode.ENTER) {
                    // Si hay un elemento seleccionado en la lista, seleccionarlo
                    ClienteFX selectedClient = listViewClientes.getSelectionModel().getSelectedItem();
                    if (selectedClient != null) {
                        this.clienteSeleccionado = selectedClient;
                        txtBuscarCliente.setText(selectedClient.getNombre());
                        mostrarDatosCliente(selectedClient);
                        clienteSugerenciaPopup.hide();
                        event.consume(); // Prevenir que se propague el evento
                    } else {
                        // Si no hay selección, realizar búsqueda normal
                        buscarCliente();
                        clienteSugerenciaPopup.hide();
                    }
                }
            }
        });
        
        // Configurar pérdida de foco para cerrar popup
        txtBuscarCliente.focusedProperty().addListener((ObservableValue<? extends Boolean> obs, Boolean wasFocused, Boolean isNowFocused) -> {
            if (!isNowFocused && clienteSugerenciaPopup.isShowing()) {
                // Usar Platform.runLater para evitar conflictos con otros eventos de UI
                javafx.application.Platform.runLater(() -> {
                    if (!listViewClientes.isFocused()) {
                        clienteSugerenciaPopup.hide();
                    }
                });
            }
        });
    }


    
    /**
     * Busca clientes que coincidan con el texto ingresado y muestra sugerencias
     * @param texto El texto de búsqueda
     */
    private void buscarClientesSugeridos(String texto) {
        if (clienteService == null) {
            System.err.println("Error: clienteService no está inicializado");
            return;
        }
        
        try {
            // Obtener todos los clientes y filtrar por el texto de búsqueda
            List<ClienteFX> todosLosClientes = clienteService.obtenerTodosLosClientes();
            if (todosLosClientes == null) {
                System.err.println("Error: No se pudo obtener la lista de clientes");
                return;
            }
            
            List<ClienteFX> clientesFiltrados = todosLosClientes.stream()
                .filter(cliente -> 
                    (cliente.getNombre() != null && cliente.getNombre().toLowerCase().contains(texto.toLowerCase())) ||
                    (cliente.getDocumentoIdentidad() != null && 
                     cliente.getDocumentoIdentidad().contains(texto)))
                .limit(10) // Limitar a 10 resultados para no sobrecargar la UI
                .collect(Collectors.toList());
            
            // Actualizar la lista de sugerencias en el hilo de la UI
            javafx.application.Platform.runLater(() -> {
                clientesSugeridos.clear();
                clientesSugeridos.addAll(clientesFiltrados);
                
                // Mostrar u ocultar el popup según los resultados
                if (!clientesFiltrados.isEmpty()) {
                    if (!clienteSugerenciaPopup.isShowing()) {
                        try {
                            Bounds bounds = txtBuscarCliente.localToScreen(txtBuscarCliente.getBoundsInLocal());
                            // Asegurar que el popup se muestra en la posición correcta
                            double offsetX = 0; // Ajustar si es necesario
                            double offsetY = 2; // Pequeño offset para que no se solape exactamente
                            clienteSugerenciaPopup.show(txtBuscarCliente, 
                                                      bounds.getMinX() + offsetX, 
                                                      bounds.getMaxY() + offsetY);
                        } catch (Exception e) {
                            System.err.println("Error al mostrar popup: " + e.getMessage());
                        }
                    }
                } else {
                    if (clienteSugerenciaPopup.isShowing()) {
                        clienteSugerenciaPopup.hide();
                    }
                }
            });
        } catch (Exception e) {
            System.err.println("Error al buscar sugerencias de clientes: " + e.getMessage());
            e.printStackTrace();
        }
    }
}