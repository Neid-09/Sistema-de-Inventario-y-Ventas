package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.novaSup.InventoryGest.MainApp.springContext;

@Component
public class MenuPrincipalControllerFX {

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnProductos;

    @FXML
    private Button btnUsuarios;

    @FXML
    private Label lblTitle;

    @FXML
    private Label lblUsuario;

    @FXML
    void cerrarSesion(ActionEvent event) {
        try {
            // Obtener el Stage actual desde el botón
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();

            // Cargar la vista de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.LOGIN_FXML));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // Crear nueva escena y mostrarla
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la pantalla de inicio de sesión: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    void irGestionProductos(ActionEvent event) {
        try {
            // Obtener el Stage actual desde el botón
            Stage stage = (Stage) btnProductos.getScene().getWindow();

            // Cargar la vista de CRUD de productos
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.CRUD_PRDUCTOS));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // Crear nueva escena y mostrarla
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la pantalla de gestión de productos: " + e.getMessage());
        }
    }

    @FXML
    void irGestionUsuarios(ActionEvent event) {
        try {
            // Obtener el Stage actual desde el botón
            Stage stage = (Stage) btnUsuarios.getScene().getWindow();

            // Cargar la vista de CRUD de usuarios
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.CRUD_USERS));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // Crear nueva escena y mostrarla
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la pantalla de gestión de usuarios: " + e.getMessage());
        }
    }

    public void establecerUsuario(String correoUsuario) {
        lblUsuario.setText("Bienvenido, " + correoUsuario);
    }

}