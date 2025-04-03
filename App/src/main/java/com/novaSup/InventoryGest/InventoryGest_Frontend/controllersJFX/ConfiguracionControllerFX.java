package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.novaSup.InventoryGest.MainApp.springContext;

@Component
public class ConfiguracionControllerFX {

    @FXML
    private Button btnGestionarUsuarios;

    @FXML
    public void irGestionUsuarios(ActionEvent event) {
        // Verificación más robusta de permisos
        if (!LoginServiceImplFX.tienePermiso("gestionar_usuarios")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acceso denegado",
                    "No tienes permisos para acceder a la gestión de usuarios.");
            return;
        }

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            if (menuController != null) {
                menuController.cargarModuloEnPanel(PathsFXML.CRUD_USERS);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "No se pudo encontrar el controlador del menú principal");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la gestión de usuarios: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}