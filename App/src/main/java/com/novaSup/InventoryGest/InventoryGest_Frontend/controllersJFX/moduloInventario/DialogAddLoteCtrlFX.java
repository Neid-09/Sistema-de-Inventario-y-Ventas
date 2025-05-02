package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloInventario;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.LoteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;

public class DialogAddLoteCtrlFX {

    @FXML
    private Label lblNombreProducto;

    @FXML
    private TextField txtNumeroLote;

    @FXML
    private TextField txtCantidad;

    @FXML
    private DatePicker dpFechaEntrada;

    @FXML
    private DatePicker dpFechaVencimiento;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnCancelar;

    private final ILoteService loteService;
    private final IProductoService productoService;
    private ProductoFX productoSeleccionado;

    // Constructor para inyección de dependencias
    public DialogAddLoteCtrlFX(ILoteService loteService, IProductoService productoService) {
        this.loteService = loteService;
        this.productoService = productoService;
    }

    public void inicializar(ProductoFX producto) {
        this.productoSeleccionado = producto;

        // Configurar la interfaz con los datos del producto
        lblNombreProducto.setText(producto.getNombre());

        // Establecer fecha actual como fecha de entrada predeterminada
        dpFechaEntrada.setValue(LocalDate.now());

        // Configurar validadores para campos numéricos
        txtCantidad.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                txtCantidad.setText(oldText);
            }
        });

        // Generar número de lote sugerido basado en código de producto y fecha
        String numeroLoteSugerido = "LOT-" + producto.getCodigo() + "-" +
                LocalDate.now().getYear() + LocalDate.now().getMonthValue();
        txtNumeroLote.setText(numeroLoteSugerido);
    }

    @FXML
    private void guardarLote() {
        // Asegurarse que los servicios fueron inyectados antes de usarlos
        if (loteService == null || productoService == null) {
            mostrarAlerta("Error interno: Servicios no inicializados.", Alert.AlertType.ERROR);
            return;
        }
        try {
            // Validar campos obligatorios
            if (txtNumeroLote.getText().trim().isEmpty()) {
                mostrarAlerta("El número de lote es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            if (txtCantidad.getText().trim().isEmpty() ||
                    Integer.parseInt(txtCantidad.getText().trim()) <= 0) {
                mostrarAlerta("La cantidad debe ser un número positivo", Alert.AlertType.WARNING);
                return;
            }

            if (dpFechaEntrada.getValue() == null) {
                mostrarAlerta("La fecha de entrada es obligatoria", Alert.AlertType.WARNING);
                return;
            }

            // Crear objeto lote
            LoteFX nuevoLote = new LoteFX();
            nuevoLote.setNumeroLote(txtNumeroLote.getText().trim());
            nuevoLote.setIdProducto(productoSeleccionado.getIdProducto());
            nuevoLote.setProducto(productoSeleccionado);
            nuevoLote.setCantidad(Integer.parseInt(txtCantidad.getText().trim()));
            nuevoLote.setFechaEntrada(dpFechaEntrada.getValue());
            nuevoLote.setFechaVencimiento(dpFechaVencimiento.getValue());
            nuevoLote.setActivo(true);

            // Guardar en la base de datos usando el servicio inyectado
            loteService.crear(nuevoLote);

            // Opcional: Refrescar el producto si es necesario (aunque el stock se actualiza en el backend)
            // productoService.obtenerPorId(productoSeleccionado.getIdProducto());

            // Mostrar mensaje de éxito
            mostrarAlerta("Lote creado y stock actualizado correctamente.", Alert.AlertType.INFORMATION);

            // Cerrar ventana
            cerrarVentana();

        } catch (Exception e) {
            mostrarAlerta("Error al crear el lote: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.WARNING ? "Advertencia" :
                (tipo == Alert.AlertType.ERROR ? "Error" : "Información"));
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
