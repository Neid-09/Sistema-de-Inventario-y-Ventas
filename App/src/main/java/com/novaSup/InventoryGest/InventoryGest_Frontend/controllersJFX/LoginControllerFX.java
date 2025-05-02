package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoginService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;

public class LoginControllerFX {

    @FXML
    private Button btnLogin;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    private final ILoginService loginService;
    private final Callback<Class<?>, Object> controllerFactory;

    public LoginControllerFX(ILoginService loginService, Callback<Class<?>, Object> controllerFactory) {
        this.loginService = loginService;
        this.controllerFactory = controllerFactory;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setResizable(false);

            stage.setOnCloseRequest(event -> {
                stage.close();
            });
        });
    }

    @FXML
    void iniciarSesion(ActionEvent event) {
        if (txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de inicio de sesión",
                    "Por favor completa todos los campos");
            return;
        }

        try {
            boolean autenticado = loginService.autenticarUsuario(
                    txtEmail.getText(), txtPassword.getText());

            if (autenticado) {
                cargarMenuPrincipal();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación",
                        "Usuario o contraseña incorrectos");
            }
        } catch (Exception e) {
            if (e.getMessage() != null &&
                    (e.getMessage().contains("Credenciales inválidas") ||
                            e.getMessage().contains("code: 400") ||
                            e.getMessage().contains("code: 401"))) {

                mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación",
                        "Usuario o contraseña incorrectos");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                        "No se pudo conectar con el servidor. Intente nuevamente más tarde.");

                System.err.println("Error de conexión: " + e.getMessage());
            }
        }
    }

    private void cargarMenuPrincipal() {
        try {
            URL fxmlUrl = getClass().getResource(PathsFXML.MENUPRINCIPAL_FXML);

            if (fxmlUrl == null) {
                throw new IOException("No se encontró el archivo FXML del menú principal");
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            loader.setControllerFactory(this.controllerFactory);
            Parent root = loader.load();

            Scene scene = new Scene(root);
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el menú principal: " + e.getMessage());
            e.printStackTrace();
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