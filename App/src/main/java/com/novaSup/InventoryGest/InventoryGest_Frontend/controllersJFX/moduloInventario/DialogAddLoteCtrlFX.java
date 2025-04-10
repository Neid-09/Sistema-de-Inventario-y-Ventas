package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloInventario;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.LoteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class DialogAddLoteCtrlFX {

    @FXML private Label lblNombreProducto;
    @FXML private TextField txtNumeroLote;
    @FXML private TextField txtCantidad;
    @FXML private DatePicker dpFechaEntrada;
    @FXML private DatePicker dpFechaVencimiento;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private ILoteService loteService;
    private IProductoService productoService;
    private ProductoFX producto;

    // Método para inyectar manualmente los servicios
    public void setServicios(ILoteService loteService, IProductoService productoService) {
        this.loteService = loteService;
        this.productoService = productoService;
    }

    public void inicializar(ProductoFX producto) {
        // Establecer el nombre del producto en la etiqueta
        lblNombreProducto.setText(producto.getNombre());

        // Guardar referencia al producto
        this.producto = producto;

        // Establecer fecha de entrada predeterminada a hoy
        dpFechaEntrada.setValue(LocalDate.now());

        // Configurar validación numérica para cantidad
        txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCantidad.setText(oldValue);
            }
        });
    }

    @FXML
    void cancelar(ActionEvent event) {
        // Cerrar la ventana
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    @FXML
    void guardarLote(ActionEvent event) {
        try {
            if (loteService == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error de configuración",
                        "No se ha configurado el servicio de lotes.");
                return;
            }

            // Validar campos
            if (!validarCampos()) {
                return;
            }

            // Crear el objeto Lote
            LoteFX lote = new LoteFX();
            lote.setIdProducto(producto.getIdProducto());
            lote.setNumeroLote(txtNumeroLote.getText());
            lote.setCantidad(Integer.parseInt(txtCantidad.getText()));

            // Usar directamente LocalDate de los DatePickers
            lote.setFechaEntrada(dpFechaEntrada.getValue());
            if (dpFechaVencimiento.getValue() != null) {
                lote.setFechaVencimiento(dpFechaVencimiento.getValue());
            }

            lote.setActivo(true);

            // Guardar el lote
            LoteFX loteCreado = loteService.crear(lote);

            // Actualizar el stock del producto
            if (productoService != null) {
                try {
                    ProductoFX productoActualizado = productoService.obtenerPorId(producto.getIdProducto());
                    if (productoActualizado != null) {
                        this.producto = productoActualizado;
                    }
                } catch (Exception ex) {
                    // Si falla la actualización, no interrumpimos el flujo
                }
            }

            // Mostrar mensaje de éxito
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Lote creado",
                    "El lote ha sido creado correctamente y se ha actualizado el stock.");

            // Cerrar la ventana
            Stage stage = (Stage) btnGuardar.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al crear el lote",
                    "Ha ocurrido un error: " + e.getMessage());
        }
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtNumeroLote.getText().isEmpty()) {
            errores.append("El número de lote es obligatorio.\n");
        }

        if (txtCantidad.getText().isEmpty()) {
            errores.append("La cantidad es obligatoria.\n");
        } else {
            try {
                int cantidad = Integer.parseInt(txtCantidad.getText());
                if (cantidad <= 0) {
                    errores.append("La cantidad debe ser mayor a cero.\n");
                }
            } catch (NumberFormatException e) {
                errores.append("La cantidad debe ser un número válido.\n");
            }
        }

        if (dpFechaEntrada.getValue() == null) {
            errores.append("La fecha de entrada es obligatoria.\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Datos incompletos", errores.toString());
            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(header);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}