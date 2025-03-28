package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static com.novaSup.InventoryGest.MainApp.springContext;

@Component
public class MenuPrincipalControllerFX implements Initializable {

    @FXML
    private Label lblUsuario;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private StackPane modulosDinamicos;

    private String nombreUsuario;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Platform.runLater(() -> {
            try {
                // Configurar el Stage
                Stage stage = (Stage) modulosDinamicos.getScene().getWindow();
                stage.setResizable(true);
                stage.setUserData(this);

                // Cargar el módulo de inicio por defecto
                cargarModuloEnPanel(PathsFXML.INICIO_FXML);


            } catch (Exception e) {
                System.err.println("Error en inicialización: " + e.getMessage());
            }
        });
    }

    public void establecerUsuario(String usuario) {
        this.nombreUsuario = usuario;
        lblUsuario.setText(usuario);
    }

    public void cargarModuloEnPanel(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            loader.setControllerFactory(springContext::getBean);
            Parent contenido = loader.load();

            // Limpiar y agregar el nuevo contenido
            modulosDinamicos.getChildren().clear();
            modulosDinamicos.getChildren().add(contenido);
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el módulo: " + e.getMessage());
        }
    }

    @FXML
    void volverAlInicio(ActionEvent event) {
        cargarModuloEnPanel(PathsFXML.INICIO_FXML);
    }

    @FXML
    void irGestionProductos(ActionEvent event) {
        cargarModuloEnPanel(PathsFXML.CRUD_PRDUCTOS);
    }

    @FXML
    void verHistorialStock(ActionEvent event) {
        cargarModuloEnPanel(PathsFXML.CONTROLSTOCK_FXML);
    }

    @FXML
    void irConfiguracion(ActionEvent event) {
        cargarModuloEnPanel(PathsFXML.CONFIGURACION_FXML);
    }

    @FXML
    void cerrarSesion(ActionEvent event) {
        try {
            Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.LOGIN_FXML));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setResizable(false);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Error al cerrar sesión: " + e.getMessage());
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