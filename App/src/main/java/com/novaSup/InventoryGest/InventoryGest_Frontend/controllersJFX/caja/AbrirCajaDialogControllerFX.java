package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.caja;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICajaService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.AbrirCajaRequestDTO;

public class AbrirCajaDialogControllerFX {

    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField dineroInicialField;

    @FXML
    private CheckBox heredarSaldoCheckbox;

    @FXML
    private TextArea justificacionField;

    @FXML
    private Button abrirCajaButton;

    @FXML
    private Button cancelarButton;

    private ICajaService cajaService;
    private Integer idUsuario;
    private Runnable onSuccessCallback;

    @FXML
    public void initialize() {
        // Listener para validar entrada de dinero inicial como número
        dineroInicialField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([.]\\d{0,2})?")) {
                dineroInicialField.setText(oldValue);
            }
        });
    }

    public void initData(ICajaService cajaService, Integer idUsuario, Runnable onSuccessCallback) {
        this.cajaService = cajaService;
        this.idUsuario = idUsuario;
        this.onSuccessCallback = onSuccessCallback;

        // Lógica para heredar saldo
        heredarSaldoCheckbox.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                dineroInicialField.setDisable(true);
                dineroInicialField.setText(""); // Limpiar por si acaso
            } else {
                dineroInicialField.setDisable(false);
            }
        });
    }

    @FXML
    private void handleAbrirCaja() {
        if (attemptAbrirCaja()) {
            cerrarVentana();
        }
    }

    @FXML
    private void handleCancelar() {
        cerrarVentana();
    }

    // Renombrado desde handleAbrirCaja, ya no es FXML, devuelve booleano para indicar éxito
    private boolean attemptAbrirCaja() {
        String dineroInicialStr = dineroInicialField.getText();
        String justificacion = justificacionField.getText();
        boolean heredarSaldo = heredarSaldoCheckbox.isSelected();

        AbrirCajaRequestDTO requestDTO = new AbrirCajaRequestDTO();
        requestDTO.setIdUsuario(this.idUsuario);
        requestDTO.setHeredarSaldoAnterior(heredarSaldo);

        if (heredarSaldo) {
            requestDTO.setDineroInicial(null); 
            requestDTO.setJustificacionManual(null);
        } else {
            // No se hereda saldo, se usa el dinero inicial ingresado
            if (dineroInicialStr.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El campo 'Dinero Inicial' es obligatorio a menos que se herede el saldo.");
                return false;
            }

            BigDecimal dineroInicialNum;
            try {
                dineroInicialNum = new BigDecimal(dineroInicialStr);
                if (dineroInicialNum.compareTo(BigDecimal.ZERO) < 0) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El dinero inicial no puede ser negativo.");
                    return false;
                }
            } catch (NumberFormatException e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "Por favor, ingrese un monto válido para 'Dinero Inicial'.");
                return false;
            }
            requestDTO.setDineroInicial(dineroInicialNum);

            // Validar justificación si el monto es cero (y no se hereda)
            if (dineroInicialNum.compareTo(BigDecimal.ZERO) == 0 && (justificacion == null || justificacion.trim().isEmpty())) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "Se requiere una justificación si el monto inicial es cero.");
                return false;
            }
            requestDTO.setJustificacionManual(justificacion.isEmpty() ? null : justificacion.trim());
        }

        try {
            cajaService.abrirCaja(requestDTO); 
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Caja abierta correctamente.");
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            return true; // Indica éxito
        } catch (Exception e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Abrir Caja", "No se pudo abrir la caja: " + e.getMessage());
            return false; // Indica fallo
        }
    }

    private void cerrarVentana() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.initOwner(rootPane.getScene().getWindow());
        alerta.showAndWait();
    }
}
