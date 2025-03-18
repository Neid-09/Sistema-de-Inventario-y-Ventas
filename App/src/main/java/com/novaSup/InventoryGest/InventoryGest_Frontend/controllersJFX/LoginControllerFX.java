package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.novaSup.InventoryGest.MainApp.springContext;

@Component
public class LoginControllerFX {

    @FXML
    private Button btnLogin;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    private final String API_URL = "http://localhost:8080/usuarios/login";

    @FXML
    void iniciarSesion(ActionEvent event) {
        // Validar campos
        if (txtEmail.getText().isEmpty() || txtPassword.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de inicio de sesión",
                    "Por favor completa todos los campos");
            return;
        }

        try {
            // Preparar datos para la petición
            Map<String, String> credentials = new HashMap<>();
            credentials.put("correo", txtEmail.getText());
            credentials.put("contraseña", txtPassword.getText());

            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> request = new HttpEntity<>(credentials, headers);

            // Enviar petición al backend
            ResponseEntity<?> response = restTemplate.postForEntity(API_URL, request, Object.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                // Autenticación exitosa - navegar a la pantalla de registro
                cargarPantallaRegistro();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación",
                        "No se pudo iniciar sesión. Verifica tus credenciales.");
            }
        } catch (HttpClientErrorException e) {
            if (e.getRawStatusCode() == 401) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de autenticación",
                        "Usuario o contraseña incorrectos");
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Error de conexión con el servidor: " + e.getMessage());
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Ocurrió un error: " + e.getMessage());
        }
    }

    private void cargarPantallaRegistro() {
        try {
            // Obtener el stage actual
            Stage stage = (Stage) btnLogin.getScene().getWindow();

            // Cargar la vista de registro de usuarios
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.REGISTER));
            loader.setControllerFactory(springContext::getBean); // Para que Spring inyecte los controladores
            Parent root = loader.load();

            // Cambiar a la nueva escena
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la pantalla de registro: " + e.getMessage());
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