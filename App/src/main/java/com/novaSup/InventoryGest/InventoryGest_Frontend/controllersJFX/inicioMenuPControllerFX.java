package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

@Component
public class inicioMenuPControllerFX implements Initializable {

    @FXML
    private Label lblFecha;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Establecer la fecha actual
        LocalDate fechaActual = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        lblFecha.setText("Fecha: " + fechaActual.format(formatter));
    }
}