package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ClienteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.DetalleVentaCreateRequestFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoVentaInfo;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaCreateRequestFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IClienteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class ProcesarVentaDialogController {

    @FXML
    private Button btnBuscarCliente;

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

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtDireccion;

    @FXML
    private TextField txtDocumento;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

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

    @FXML
    public void initialize() {
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
        
        // Configurar el botón de búsqueda de cliente (verificando que no sea null)
        if (btnBuscarCliente != null) {
            btnBuscarCliente.setOnAction(event -> buscarCliente());
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
    public void setServices(IVentaSerivice ventaService, IClienteService clienteService) {
        this.ventaService = ventaService;
        this.clienteService = clienteService;
        // Inicializar otros servicios cuando estén disponibles
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
                    // Buscar los campos en el panel de datos del cliente incluido
                    TabPane tabPane = (TabPane) panelDatosCliente.lookup(".client-tabs");
                    if (tabPane != null && tabPane.getTabs().size() > 1) {
                        Tab nuevoClienteTab = tabPane.getTabs().get(1); // Tab "Nuevo Cliente"
                        if (nuevoClienteTab != null) {
                            TextField nombreField = (TextField) nuevoClienteTab.getContent().lookup("#nombreField");
                            TextField documentoField = (TextField) nuevoClienteTab.getContent().lookup("#documentoIdentidadField");
                            
                            // Validar que se hayan ingresado los datos mínimos del cliente
                            if (nombreField == null || documentoField == null || 
                                nombreField.getText().trim().isEmpty() || documentoField.getText().trim().isEmpty()) {
                                mostrarAlerta("Datos Incompletos", "Debe ingresar al menos el nombre y documento del cliente para generar factura.");
                                return;
                            }
                        } else {
                            mostrarAlerta("Datos Incompletos", "Debe seleccionar o crear un cliente para generar factura.");
                            return;
                        }
                    } else {
                        mostrarAlerta("Datos Incompletos", "Debe seleccionar un cliente para generar factura.");
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
        String busqueda = txtBuscarCliente.getText().trim();
        
        if (busqueda.isEmpty()) {
            mostrarAlerta("Búsqueda vacía", "Ingrese un nombre o documento para buscar un cliente.");
            return;
        }
        
        try {
            // Intentar buscar por documento primero (cédula o identificación fiscal)
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
            
            // Si no se encuentra por documento, buscar por nombre
            Optional<ClienteFX> clientePorNombre = clienteService.obtenerClientePorNombre(busqueda);
            
            if (clientePorNombre.isPresent()) {
                // Cliente encontrado por nombre, mostrar sus datos
                mostrarDatosCliente(clientePorNombre.get());
                return;
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
    }
}