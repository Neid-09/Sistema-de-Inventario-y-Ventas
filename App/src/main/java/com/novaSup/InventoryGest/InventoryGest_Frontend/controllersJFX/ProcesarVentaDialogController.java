package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoVentaInfo;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class ProcesarVentaDialogController {

    @FXML
    private DialogPane dialogPane;

    @FXML
    private Label lblResumenVenta; // Nuevo Label para el resumen

    @FXML
    private ComboBox<String> cmbFormaPago;

    @FXML
    private RadioButton rbFacturaSi;

    @FXML
    private RadioButton rbFacturaNo;

    @FXML
    private ToggleGroup facturaToggleGroup; // Asegúrate que este fx:id esté en el FXML

    @FXML
    private VBox datosFacturaBox;

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
    private ButtonType btnConfirmar; // fx:id para el ButtonType de Confirmar

    private Stage dialogStage; // Este campo podría volverse obsoleto o usarse para Window
    private boolean confirmado = false;

    private List<ProductoVentaInfo> productosEnVenta;
    private BigDecimal totalVentaActual;
    private NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CO"));

    @FXML
    public void initialize() {
        // Configurar ComboBox de forma de pago
        cmbFormaPago.setItems(FXCollections.observableArrayList("Efectivo", "Tarjeta", "Transferencia", "Mixto"));
        cmbFormaPago.getSelectionModel().selectFirst(); // Seleccionar Efectivo por defecto

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
    }

    /**
     * Establece los datos de la venta actual en el diálogo.
     * @param productos Lista de productos en la venta.
     * @param totalVenta El total de la venta calculado.
     */
    public void setDatosVenta(List<ProductoVentaInfo> productos, BigDecimal totalVenta) {
        this.productosEnVenta = productos;
        this.totalVentaActual = totalVenta;
        if (lblResumenVenta != null) {
            lblResumenVenta.setText("Total Venta: " + formatoMoneda.format(totalVentaActual));
        }
        // Es un buen momento para configurar la validación del botón aquí también,
        // ya que todos los FXML fields deberían estar inyectados.
        configurarValidacionBotonConfirmar();
    }

    /**
     * Configura la lógica de habilitación/deshabilitación del botón de confirmar.
     * Este método asume que dialogPane y btnConfirmar (ButtonType) ya están disponibles.
     */
    public void configurarValidacionBotonConfirmar() {
        if (dialogPane == null) {
            System.err.println("DialogPane no inyectado al llamar a configurarValidacionBotonConfirmar.");
            return;
        }
        // El ButtonType btnConfirmar (fx:id) se usa para buscar el nodo Button real.
        Node confirmButtonNode = dialogPane.lookupButton(getBtnConfirmarType()); 

        if (confirmButtonNode instanceof Button) {
            ((Button) confirmButtonNode).disableProperty().bind(
                cmbFormaPago.valueProperty().isNull()
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

    /**
     * Este método podría ya no ser necesario si el Stage no se usa directamente,
     * o podría adaptarse para recibir un Window si se necesita para algo específico.
     * La validación del botón ahora se maneja en configurarValidacionBotonConfirmar().
     * @param stage El Stage del diálogo (puede ser null si no se usa).
     */
    public void setDialogStage(Stage stage) {
        this.dialogStage = stage;
        // La lógica de validación del botón se ha movido a configurarValidacionBotonConfirmar()
        // para ser llamada cuando el dialogPane esté listo.
        // Si aún se necesita el Stage para algo más, se puede mantener.
        // Por ahora, la llamada principal a la validación está en setDatosVenta y VenderControllerFX.
    }

    public boolean isConfirmado() {
        return confirmado;
    }

    public String getFormaDePago() {
        return cmbFormaPago.getValue();
    }

    public boolean isRequiereFactura() {
        return rbFacturaSi.isSelected();
    }

    public String getDocumentoCliente() {
        return txtDocumento.getText();
    }

    public String getNombreCliente() {
        return txtNombre.getText();
    }

    public String getDireccionCliente() {
        return txtDireccion.getText();
    }

    public String getTelefonoCliente() {
        return txtTelefono.getText();
    }

    public String getCorreoCliente() {
        return txtCorreo.getText();
    }

    public void setConfirmado(boolean confirmado) {
        this.confirmado = confirmado;
    }

    public ButtonType getBtnConfirmarType() {
        return btnConfirmar;
    }

}
