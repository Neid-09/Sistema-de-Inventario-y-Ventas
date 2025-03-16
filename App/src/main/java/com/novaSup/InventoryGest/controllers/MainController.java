package com.novaSup.InventoryGest.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

@Component
@Controller
public class MainController {

    @FXML
    private void handleButtonClick() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Mensaje");
        alert.setHeaderText(null);
        alert.setContentText("¡Hola desde JavaFX con Spring Boot!");
        alert.showAndWait();
    }
}
