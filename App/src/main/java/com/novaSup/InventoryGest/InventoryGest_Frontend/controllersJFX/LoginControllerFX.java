package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoginService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
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
import org.springframework.stereotype.Component;
import java.io.IOException;

import static com.novaSup.InventoryGest.MainApp.springContext;

@Component
public class LoginControllerFX {

    @FXML
    private Button btnLogin;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    // Usamos la interfaz en lugar de la implementación directa
    private final ILoginService loginService = new LoginServiceImplFX();

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            stage.setResizable(false);
        });
    }

    @FXML
    void iniciarSesion(ActionEvent event) {
        // Validar campos
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
            // Mostrar mensaje amigable para credenciales inválidas
            if (e.getMessage() != null &&
                    (e.getMessage().contains("Credenciales inválidas") ||
                            e.getMessage().contains("code: 400") ||
                            e.getMessage().contains("code: 401"))) {

                mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación",
                        "Usuario o contraseña incorrectos");
            } else {
                // Para otros errores de conexión
                mostrarAlerta(Alert.AlertType.ERROR, "Error de conexión",
                        "No se pudo conectar con el servidor. Intente nuevamente más tarde.");

                // Log del error para depuración
                System.err.println("Error de conexión: " + e.getMessage());
            }
        }
    }

    private void cargarMenuPrincipal() {
        try {
            Stage stage = (Stage) btnLogin.getScene().getWindow();
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.MENUPRINCIPAL_FXML));
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // Obtén el controlador y establece el nombre de usuario
            MenuPrincipalControllerFX controlador = loader.getController();
            controlador.establecerUsuario(txtEmail.getText());

            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar el menú principal: " + e.getMessage());
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