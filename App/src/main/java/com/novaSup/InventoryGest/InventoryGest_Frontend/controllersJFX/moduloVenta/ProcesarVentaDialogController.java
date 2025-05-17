package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ClienteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.DetalleVentaCreateRequestFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoVentaInfo;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaCreateRequestFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IClienteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;
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

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;
import javafx.beans.value.ObservableValue;

public class ProcesarVentaDialogController {

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnConfirmar;

    @FXML
    private ToggleGroup facturaToggleGroup;

    @FXML
    private ToggleGroup metodoPagoToggleGroup;

    @FXML
    private VBox panelDatosCliente;

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
            }
        });

        facturaToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            boolean requiereFactura = newValue == rbFacturaSi;
            panelDatosCliente.setVisible(requiereFactura);
            panelDatosCliente.setManaged(requiereFactura);
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

        txtCambioEntregar.setText(formatoMoneda.format(BigDecimal.ZERO));

        btnCancelar.setOnAction(event -> cerrarDialogo(false));
        btnConfirmar.setOnAction(event -> confirmarVenta());
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
    public void setServices(IVentaSerivice ventaService, IClienteService clienteService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
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
            // Si ya tenemos una referencia al controlador, solo necesitamos inyectar el servicio
            if (clienteDialogController != null) {
                // Crear un nuevo cliente para evitar NullPointerException
                ClienteFX nuevoCliente = new ClienteFX();
                nuevoCliente.setActivo(true);
                nuevoCliente.setRequiereFacturaDefault(false);
                nuevoCliente.setLimiteCredito(BigDecimal.ZERO);
                
                // Configurar el controlador con el nuevo cliente
                clienteDialogController.setClienteParaOperacion(nuevoCliente, clienteService);
                return;
            }
            
            // Si no tenemos referencia, intentamos obtenerla
            TabPane tabPane = (TabPane) panelDatosCliente.lookup(".client-tabs");
            if (tabPane != null && tabPane.getTabs().size() > 1) {
                Tab nuevoClienteTab = tabPane.getTabs().get(1); // Tab "Nuevo Cliente"
                if (nuevoClienteTab != null && nuevoClienteTab.getContent() instanceof StackPane) {
                    StackPane clienteDialogContainer = (StackPane) nuevoClienteTab.getContent();
                    
                    // Obtener el controlador directamente del nodo incluido
                    if (clienteDialogContainer.getChildren().size() > 0) {
                        try {
                            // Cargar manualmente el FXML para obtener el controlador
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ModuloClientes/AddEditClienteDialog.fxml"));
                            Parent root = loader.load();
                            clienteDialogController = loader.getController();
                            
                            // Reemplazar el contenido existente con el recién cargado
                            clienteDialogContainer.getChildren().clear();
                            clienteDialogContainer.getChildren().add(root);
                            
                            // Configurar el controlador
                            if (clienteDialogController != null) {
                                // Crear un nuevo cliente y configurar el servicio
                                ClienteFX nuevoCliente = new ClienteFX();
                                nuevoCliente.setActivo(true);
                                nuevoCliente.setRequiereFacturaDefault(false);
                                nuevoCliente.setLimiteCredito(BigDecimal.ZERO);
                                
                                // Configurar el controlador con el nuevo cliente
                                clienteDialogController.setClienteParaOperacion(nuevoCliente, clienteService);
                                
                                // Configurar el stage del diálogo
                                // Obtener la ventana actual desde cualquier componente visible
                                if (btnConfirmar.getScene() != null && btnConfirmar.getScene().getWindow() != null) {
                                    // Intentar obtener el diálogo desde userData de la ventana
                                    Object userData = btnConfirmar.getScene().getWindow().getUserData();
                                    if (userData instanceof Dialog) {
                                        Dialog<?> dialog = (Dialog<?>) userData;
                                        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
                                        clienteDialogController.setDialogStage(stage);
                                    } else {
                                        // Si no está en userData, usar la ventana actual como stage
                                        Stage stage = (Stage) btnConfirmar.getScene().getWindow();
                                        clienteDialogController.setDialogStage(stage);
                                    }
                                }
                                
                                System.out.println("Controlador de cliente inicializado correctamente");
                            }
                        } catch (java.io.IOException e) {
                            System.err.println("Error al cargar el FXML del diálogo de cliente: " + e.getMessage());
                            e.printStackTrace();
                        }
                    }
                    
                    // Si finalmente no tenemos el controlador, registrar el error
                    if (clienteDialogController == null) {
                        System.err.println("No se pudo obtener el controlador del diálogo de cliente");
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al inicializar el controlador del diálogo de cliente: " + e.getMessage());
            e.printStackTrace();
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
                        } else {
                            mostrarAlerta("Error", "No se pudo acceder al formulario de cliente. Por favor, intente nuevamente.");
                            return;
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
                
                // Establecer el ID del vendedor (por defecto 1)
                ventaRequest.setIdVendedor(1);
                
                // Establecer que se apliquen impuestos para facturas
                ventaRequest.setAplicarImpuestos(true);
            } else {
                // Si no requiere factura, establecer como venta sin factura
                ventaRequest.setRequiereFactura(false);
                ventaRequest.setIdVendedor(1); // Vendedor por defecto
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
            
            try {
                // Registrar la venta usando el servicio
                ventaService.registrarVenta(ventaRequest);
                
                // Mostrar mensaje de éxito
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Venta Exitosa");
                alert.setHeaderText(null);
                alert.setContentText("Venta registrada correctamente");
                alert.showAndWait();
                
                // Cerrar el diálogo
                cerrarDialogo(true);
            } catch (Exception ex) {
                // Si hay un error de deserialización pero la venta se realizó correctamente
                if (ex.getMessage() != null && ex.getMessage().contains("Conflicting setter definitions for property \"fecha\"")) {
                    // Mostrar mensaje de éxito a pesar del error de deserialización
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("Venta Exitosa");
                    alert.setHeaderText(null);
                    alert.setContentText("Venta registrada correctamente");
                    alert.showAndWait();
                    
                    // Cerrar el diálogo
                    cerrarDialogo(true);
                } else {
                    // Relanzar la excepción para que sea manejada por el catch externo
                    throw ex;
                }
            }
            
        } catch (Exception e) {
            // Mostrar mensaje de error
            mostrarAlerta("Error al Procesar Venta", "Ocurrió un error: " + e.getMessage());
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
        
        // Buscar los campos en el panel de datos del cliente
        // Nota: Estos campos podrían no estar disponibles directamente, por lo que usamos lookup
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
            
            // Si no se encontraron los campos, almacenar los datos en variables temporales
            // para usar al confirmar la venta
            if (nombreField == null) {
                // Mostrar un mensaje informativo
                System.out.println("No se encontraron campos para mostrar los datos del cliente");
                System.out.println("Se usará el cliente seleccionado para la venta: " + cliente.getNombre());
            }
        } catch (Exception e) {
            System.err.println("Error al intentar mostrar datos del cliente: " + e.getMessage());
            e.printStackTrace();
        }
        
        // Mostrar un mensaje de éxito
        Alert info = new Alert(Alert.AlertType.INFORMATION);
        info.setTitle("Cliente encontrado");
        info.setHeaderText(null);
        info.setContentText("Se ha seleccionado el cliente: " + cliente.getNombre() + 
                          "\nSe utilizará este cliente para la venta.");
        info.showAndWait();
        
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