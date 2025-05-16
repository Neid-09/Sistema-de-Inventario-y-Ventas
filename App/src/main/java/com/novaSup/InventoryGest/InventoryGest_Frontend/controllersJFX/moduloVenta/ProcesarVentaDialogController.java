package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloVenta;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoVentaInfo;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProcesarVentaDialogController {

    @FXML
    private DialogPane dialogPane;

    @FXML
    private Label lblMontoTotal; // Reemplaza a lblResumenVenta

    @FXML
    private ToggleGroup metodoPagoToggleGroup;
    @FXML
    private RadioButton rbEfectivo;
    @FXML
    private RadioButton rbTarjeta;
    @FXML
    private RadioButton rbTransferencia;

    @FXML
    private VBox vueltoBox;
    @FXML
    private TextField txtTotalRecibido;
    @FXML
    private TextField txtCambioEntregar;

    @FXML
    private RadioButton rbFacturaSi;
    @FXML
    private RadioButton rbFacturaNo;
    @FXML
    private ToggleGroup facturaToggleGroup;

    @FXML
    private VBox datosFacturaBox;
    @FXML
    private TextField txtBuscarCliente;
    @FXML
    private Button btnBuscarCliente;
    @FXML
    private Button btnRegistrarNuevoCliente;

    @FXML
    private TextField txtDocumento;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextField txtDireccion;
    @FXML
    private TextField txtTelefono;
    @FXML
    private TextField txtCorreo;
    
    @FXML
    private ButtonType btnConfirmar;

    private boolean confirmado = false;
    private BigDecimal totalVentaActual;
    private final NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    @FXML
    public void initialize() {
        // Configurar selección de método de pago por defecto
        rbEfectivo.setSelected(true); // Efectivo por defecto

        // Listener para los RadioButton de método de pago
        metodoPagoToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            actualizarVisibilidadVueltoBox();
            if (newValue != rbEfectivo) {
                txtTotalRecibido.clear();
                txtCambioEntregar.clear();
            }
            calcularCambio(); // Recalcular cambio si cambia el método de pago
        });
        actualizarVisibilidadVueltoBox(); // Estado inicial

        // Listener para calcular cambio automáticamente
        txtTotalRecibido.textProperty().addListener((obs, oldVal, newVal) -> calcularCambio());

        // Listener para los RadioButton de factura
        facturaToggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue == rbFacturaSi) {
                datosFacturaBox.setVisible(true);
                datosFacturaBox.setManaged(true);
            } else if (newValue == rbFacturaNo) {
                datosFacturaBox.setVisible(false);
                datosFacturaBox.setManaged(false);
            }
        });
        // Estado inicial del VBox de datos de factura
        if (rbFacturaNo.isSelected()) {
            datosFacturaBox.setVisible(false);
            datosFacturaBox.setManaged(false);
        } else {
            datosFacturaBox.setVisible(true);
            datosFacturaBox.setManaged(true);
        }
        
        // Placeholder para acciones de botones de cliente
        btnBuscarCliente.setOnAction(event -> handleBuscarCliente());
        btnRegistrarNuevoCliente.setOnAction(event -> handleRegistrarNuevoCliente());

        // Inicializar el campo de cambio como no editable y con estilo
        txtCambioEntregar.setEditable(false);
        txtCambioEntregar.setStyle("-fx-background-color: #e0e0e0;");
    }
    
    private void actualizarVisibilidadVueltoBox() {
        boolean esEfectivo = rbEfectivo.isSelected();
        vueltoBox.setVisible(esEfectivo);
        vueltoBox.setManaged(esEfectivo);
    }

    private void calcularCambio() {
        if (totalVentaActual == null || !rbEfectivo.isSelected()) {
            txtCambioEntregar.clear();
            return;
        }
        try {
            // Reemplazar comas por puntos para el parseo y quitar símbolos de moneda si los hubiera
            String recibidoStr = txtTotalRecibido.getText()
                                       .replace(formatoMoneda.getCurrency().getSymbol(), "")
                                       .replace(".", "") // Eliminar separadores de miles (si el formato los usa como punto)
                                       .replace(",", ".") // Convertir coma decimal a punto
                                       .trim();
            
            if (recibidoStr.isEmpty()) {
                txtCambioEntregar.clear();
                return;
            }
            BigDecimal recibido = new BigDecimal(recibidoStr);
            if (recibido.compareTo(totalVentaActual) >= 0) {
                BigDecimal cambio = recibido.subtract(totalVentaActual);
                txtCambioEntregar.setText(formatoMoneda.format(cambio));
            } else {
                txtCambioEntregar.setText("Monto insuficiente");
            }
        } catch (NumberFormatException e) {
            txtCambioEntregar.setText("Valor inválido");
        }
    }

    public void setDatosVenta(List<ProductoVentaInfo> productos, BigDecimal totalVenta) {
        this.totalVentaActual = totalVenta;
        if (lblMontoTotal != null) {
            // Escapar el $ si es necesario para el Label, aunque set_text no lo interpreta como expresión.
            lblMontoTotal.setText("Monto Total: " + formatoMoneda.format(totalVentaActual));
        }
        calcularCambio(); // Para actualizar el cambio si ya hay un total recibido
        configurarValidacionBotonConfirmar();
    }

    public void configurarValidacionBotonConfirmar() {
        if (dialogPane == null) {
            System.err.println("DialogPane no inyectado al llamar a configurarValidacionBotonConfirmar.");
            return;
        }
        Node confirmButtonNode = dialogPane.lookupButton(getBtnConfirmarType()); 

        if (confirmButtonNode instanceof Button) {
            ((Button) confirmButtonNode).disableProperty().bind(
                metodoPagoToggleGroup.selectedToggleProperty().isNull()
                .or(rbFacturaSi.selectedProperty().and(
                    txtDocumento.textProperty().isEmpty()
                    .or(txtNombre.textProperty().isEmpty())
                    .or(txtDireccion.textProperty().isEmpty())
                    .or(txtTelefono.textProperty().isEmpty())
                ))
            );
        } else {
            System.err.println("Error: No se pudo encontrar el botón de confirmación (o no es un Button) en el diálogo para la validación.");
        }
    }
    
    @FXML
    private void handleBuscarCliente() {
        String criterio = txtBuscarCliente.getText();
        System.out.println("Intentando buscar cliente con criterio: " + criterio);
        // Aquí iría la lógica para buscar un cliente existente.
        // Por ejemplo, mostrar un diálogo de búsqueda y, si se selecciona un cliente,
        // rellenar los campos txtDocumento, txtNombre, etc.
        // showAlert("Información", "Funcionalidad no implementada", "La búsqueda de clientes aún no está implementada.");
    }

    @FXML
    private void handleRegistrarNuevoCliente() {
        System.out.println("Intentando registrar nuevo cliente.");
        // Aquí iría la lógica para abrir un formulario o diálogo de registro de nuevo cliente.
        // Después del registro, se podrían rellenar automáticamente los campos.
        // showAlert("Información", "Funcionalidad no implementada", "El registro de nuevos clientes aún no está implementado.");
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getFormaDePago() {
        if (rbEfectivo.isSelected()) return "Efectivo";
        if (rbTarjeta.isSelected()) return "Tarjeta";
        if (rbTransferencia.isSelected()) return "Transferencia";
        return null;
    }
    
    public String getTotalRecibido() {
        return txtTotalRecibido.getText();
    }

    // Getters para datos de factura (ya existentes y siguen siendo válidos)
    public boolean isRequiereFactura() { return rbFacturaSi.isSelected(); }
    public String getDocumentoCliente() { return txtDocumento.getText(); }
    public String getNombreCliente() { return txtNombre.getText(); }
    public String getDireccionCliente() { return txtDireccion.getText(); }
    public String getTelefonoCliente() { return txtTelefono.getText(); }
    public String getCorreoCliente() { return txtCorreo.getText(); }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public ButtonType getBtnConfirmarType() {
        return btnConfirmar;
    }

    // El método setDialogStage puede eliminarse o adaptarse si ya no se usa Stage directamente.
    // public void setDialogStage(Stage stage) { ... }
}
