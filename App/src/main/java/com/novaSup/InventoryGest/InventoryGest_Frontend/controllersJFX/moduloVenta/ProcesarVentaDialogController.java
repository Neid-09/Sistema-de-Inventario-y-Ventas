package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoVentaInfo;
// Importaciones de servicios (descomentar cuando estén disponibles)
// import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IClienteService;
// import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaService;
// import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IFacturaService;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
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
    // private IClienteService clienteService;
    // private IVentaService ventaService;
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

        rbEfectivo.setSelected(true);
        rbFacturaSi.setSelected(true);
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
    // public void setServices(IClienteService clienteService, IVentaService ventaService, IFacturaService facturaService) {
    //     this.clienteService = clienteService;
    //     this.ventaService = ventaService;
    //     this.facturaService = facturaService;
    //     // Aquí podrías inicializar listeners o datos que dependan de los servicios
    // }

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
        // Lógica de validación y procesamiento de la venta
        if (rbFacturaSi.isSelected()) {
            // Validar campos del cliente
            if (txtDocumento.getText().isEmpty() || txtNombre.getText().isEmpty() || txtDireccion.getText().isEmpty() || txtTelefono.getText().isEmpty()) {
                mostrarAlerta("Datos Incompletos", "Para generar factura, todos los datos del cliente son obligatorios (excepto correo).");
                return;
            }
        }

        // Aquí iría la lógica para:
        // 1. Obtener el cliente (buscar o registrar nuevo)
        // 2. Crear el objeto Venta
        // 3. Registrar la venta usando ventaService
        // 4. Generar la factura usando facturaService si es necesario
        // 5. Actualizar el stock de los productosEnVenta

        System.out.println("Venta confirmada (simulación):");
        System.out.println("Monto Total: " + formatoMoneda.format(montoTotalVenta));
        System.out.println("Método de pago: " + ((RadioButton)metodoPagoToggleGroup.getSelectedToggle()).getText());
        if(rbEfectivo.isSelected()){
            System.out.println("Total recibido: " + txtTotalRecibido.getText());
            System.out.println("Cambio: " + txtCambioEntregar.getText());
        }
        System.out.println("Factura: " + ((RadioButton)facturaToggleGroup.getSelectedToggle()).getText());
        if(rbFacturaSi.isSelected()){
            System.out.println("Cliente: " + txtNombre.getText() + " (ID: "+txtDocumento.getText()+")");
        }
        System.out.println("Productos:");
        if(productosEnVenta != null){
            productosEnVenta.forEach(p -> System.out.println("- " + p.getNombre() + " x" + p.getCantidad() + " @ " + formatoMoneda.format(p.getPrecioVentaUnitario()))); // Corregido aquí
        }


        cerrarDialogo(true);
    }

    private void cerrarDialogo(boolean ventaConfirmada) {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        // El Dialog en VenderControllerFX espera un ButtonType.OK_DONE para confirmar.
        // Si los botones tienen el ButtonData correcto, el Dialog lo manejará.
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