package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloInventario;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IInventarioService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;


public class VAjusteStockCtrlFX {

    @FXML private Label lblProducto;
    @FXML private RadioButton rbAumentar;
    @FXML private RadioButton rbDisminuir;
    @FXML private TextField txtCantidad;
    @FXML private ComboBox<String> cmbMotivo;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;

    private ProductoFX producto;
    private IInventarioService inventarioService;

    // Listas de motivos según el tipo de ajuste
    private final ObservableList<String> motivosAumentar = FXCollections.observableArrayList(
            "Error de conteo", "Devolución cliente", "Otro"
    );

    private final ObservableList<String> motivosDisminuir = FXCollections.observableArrayList(
            "Robo", "Daño", "Producto vencido", "Otro"
    );

    // Constructor para inyección de dependencias
    public void setServices(IInventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    public void inicializar(ProductoFX producto) {
        this.producto = producto;
        lblProducto.setText("Producto: " + producto.getNombre() + " (Stock actual: " + producto.getStock() + ")");

        // Configurar validación numérica para la cantidad
        txtCantidad.textProperty().addListener((obs, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                txtCantidad.setText(oldValue);
            }
        });

        // Configurar motivos iniciales (por defecto, aumentar)
        cmbMotivo.setItems(motivosAumentar);
        if (!cmbMotivo.getItems().isEmpty()) {
            cmbMotivo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void cambiarTipoAjuste() {
        if (rbAumentar.isSelected()) {
            cmbMotivo.setItems(motivosAumentar);
        } else {
            cmbMotivo.setItems(motivosDisminuir);
        }

        if (!cmbMotivo.getItems().isEmpty()) {
            cmbMotivo.getSelectionModel().selectFirst();
        }
    }

    @FXML
    public void guardar() {
        try {
            // Validar que se haya ingresado una cantidad
            if (txtCantidad.getText().isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Cantidad requerida", "Debe ingresar una cantidad para realizar el ajuste.");
                return;
            }

            int cantidad = Integer.parseInt(txtCantidad.getText());
            if (cantidad <= 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Cantidad inválida", "La cantidad debe ser un número positivo.");
                return;
            }

            // Si es disminución, verificar que haya suficiente stock
            if (rbDisminuir.isSelected() && cantidad > producto.getStock()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Stock insuficiente",
                        "No hay suficiente stock para realizar esta disminución. Stock actual: " + producto.getStock());
                return;
            }

            // Obtener el motivo seleccionado
            String motivo = cmbMotivo.getSelectionModel().getSelectedItem();
            if (motivo == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Motivo requerido", "Debe seleccionar un motivo para el ajuste.");
                return;
            }

            // Realizar el ajuste según el tipo
            if (rbAumentar.isSelected()) {
                inventarioService.registrarAjuste(producto.getIdProducto(), cantidad, motivo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Stock ajustado",
                        "Se ha aumentado el stock en " + cantidad + " unidades.");
            } else {
                // Para disminuir, la cantidad debe ser negativa para el método registrarAjuste
                inventarioService.registrarAjuste(producto.getIdProducto(), -cantidad, motivo);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Stock ajustado",
                        "Se ha disminuido el stock en " + cantidad + " unidades.");
            }

            // Cerrar la ventana
            cerrarVentana();

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Formato inválido", "La cantidad debe ser un número entero válido.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error en la operación",
                    "No se pudo realizar el ajuste de stock: " + e.getMessage());
        }
    }

    @FXML
    public void cancelar() {
        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnCancelar.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(header);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}