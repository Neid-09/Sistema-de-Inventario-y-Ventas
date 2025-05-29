package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.caja;

import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.CerrarCajaRequestDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICajaService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;

public class CerrarCajaDialogControllerFX {

    @FXML
    private BorderPane rootPane;

    @FXML
    private TextField dineroRealField;

    @FXML
    private Label valorEsperadoLabel;

    @FXML
    private Button cerrarCajaButton;

    @FXML
    private Button cancelarButton;

    private ICajaService cajaService;
    private Integer idCaja;
    private BigDecimal valorEsperado;
    private Runnable onSuccessCallback;

    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));

    @FXML
    public void initialize() {
        // Validar entrada numérica en el campo de dinero real
        dineroRealField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*([.]\\d{0,2})?")) {
                dineroRealField.setText(oldValue);
            }
        });
    }

    public void initData(ICajaService cajaService, Integer idCaja, BigDecimal valorEsperado, Runnable onSuccessCallback) {
        this.cajaService = cajaService;
        this.idCaja = idCaja;
        this.valorEsperado = valorEsperado;
        this.onSuccessCallback = onSuccessCallback;

        this.valorEsperadoLabel.setText(currencyFormat.format(this.valorEsperado != null ? this.valorEsperado : BigDecimal.ZERO));
    }

    @FXML
    private void handleAceptar() {
        System.out.println("handleAceptar llamado desde el filtro de evento.");
        String dineroRealStr = dineroRealField.getText();

        if (dineroRealStr.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El campo 'Dinero Real en Caja' no puede estar vacío.");
            return;
        }

        BigDecimal dineroReal;
        try {
            dineroReal = new BigDecimal(dineroRealStr.replace(",", "."));
            if (dineroReal.compareTo(BigDecimal.ZERO) < 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "El dinero real no puede ser negativo.");
                return;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Validación", "Formato de 'Dinero Real en Caja' inválido.");
            return;
        }

        CerrarCajaRequestDTO requestDTO = new CerrarCajaRequestDTO();
        requestDTO.setDineroReal(dineroReal);

        try {
            cajaService.cerrarCaja(idCaja, requestDTO); 
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Caja cerrada correctamente.");
            if (onSuccessCallback != null) {
                onSuccessCallback.run();
            }
            cerrarVentana(); 
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Cerrar Caja", "No se pudo cerrar la caja: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancelar() {
        System.out.println("handleCancelar llamado desde el filtro de evento.");
        cerrarVentana();
    }

    private void mostrarAlerta(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.initOwner(rootPane.getScene().getWindow());
        alert.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        stage.close();
    }
}
