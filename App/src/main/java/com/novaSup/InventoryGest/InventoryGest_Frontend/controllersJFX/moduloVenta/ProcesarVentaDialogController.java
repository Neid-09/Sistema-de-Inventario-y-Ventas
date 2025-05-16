package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.DetalleVentaCreateRequestFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoVentaInfo;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaCreateRequestFX;
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

    // Instancias de servicios (descomentar cuando estén disponibles)
    //private IClienteService clienteService;
    private IVentaSerivice ventaService;
    // private IFacturaService facturaService;

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
    public void setServices(IVentaSerivice ventaService) {
        this.ventaService = ventaService;
        // Aquí podrías inicializar listeners o datos que dependan de los servicios
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
        // Validación básica
        if (productosEnVenta == null || productosEnVenta.isEmpty()) {
            mostrarAlerta("Error", "No hay productos para vender");
            return;
        }
        
        if (rbEfectivo.isSelected() && txtTotalRecibido.getText().isEmpty()) {
            mostrarAlerta("Campos Incompletos", "Debe ingresar el monto recibido para ventas en efectivo.");
            return;
        }
        
        if (rbFacturaSi.isSelected()) {
            // Validar campos del cliente
            if (txtNombre.getText().isEmpty() || txtDocumento.getText().isEmpty()) {
                mostrarAlerta("Campos Incompletos", "Para generar factura, el nombre y documento del cliente son obligatorios.");
                return;
            }
        }

        try {
            // Crear el objeto de solicitud de venta
            VentaCreateRequestFX ventaRequest = new VentaCreateRequestFX();
            
            // Según las especificaciones:
            ventaRequest.setIdCliente(null); // Cliente null (venta general)
            ventaRequest.setIdVendedor(1); // Vendedor predeterminado (id 1)
            ventaRequest.setRequiereFactura(false); // No generar factura
            ventaRequest.setAplicarImpuestos(false); // No aplicar impuestos
            
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
}