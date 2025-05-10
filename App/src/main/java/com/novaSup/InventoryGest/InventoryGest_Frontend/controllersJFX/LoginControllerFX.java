package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoginService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
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
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import javafx.util.Callback;
import java.io.IOException;
import java.net.URL;
import javafx.concurrent.Task;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.geometry.Pos;
import javafx.scene.text.Font;

public class LoginControllerFX {

    @FXML
    private Button btnLogin;

    @FXML
    private TextField txtEmail;

    @FXML
    private PasswordField txtPassword;

    private final ILoginService loginService;
    private final Callback<Class<?>, Object> controllerFactory;

    // Clase interna estática para el resultado de la carga del FXML del menú
    private static class LoadResult {
        final Parent root;
        final MenuPrincipalControllerFX controller;

        LoadResult(Parent root, MenuPrincipalControllerFX controller) {
            this.root = root;
            this.controller = controller;
        }
    }

    public LoginControllerFX(ILoginService loginService, Callback<Class<?>, Object> controllerFactory) {
        this.loginService = loginService;
        this.controllerFactory = controllerFactory;
    }

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            Stage stage = (Stage) txtEmail.getScene().getWindow();
            if (stage != null) {
                stage.setResizable(false);
                stage.setOnCloseRequest(event -> {
                    stage.close();
                });
            }
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
        Stage stage = (Stage) btnLogin.getScene().getWindow();
        StackPane rootPaneCarga = new StackPane();
        ProgressIndicator progressIndicator = new ProgressIndicator();
        rootPaneCarga.getChildren().add(progressIndicator);
        Scene loadingScene = new Scene(rootPaneCarga, stage.getScene().getWidth(), stage.getScene().getHeight());
        stage.setScene(loadingScene);
        stage.show();

        Task<LoadResult> loadMenuTask = new Task<>() {
            @Override
            protected LoadResult call() throws Exception {
                URL fxmlUrl = getClass().getResource(PathsFXML.MENUPRINCIPAL_FXML);
                if (fxmlUrl == null) {
                    throw new IOException("No se encontró el archivo FXML del menú principal: " + PathsFXML.MENUPRINCIPAL_FXML);
                }
                FXMLLoader loader = new FXMLLoader(fxmlUrl);
                loader.setControllerFactory(controllerFactory);
                Parent loadedRoot = loader.load();
                MenuPrincipalControllerFX menuController = loader.getController();
                if (menuController == null) {
                    throw new IllegalStateException("El controlador MenuPrincipalControllerFX no fue cargado.");
                }
                return new LoadResult(loadedRoot, menuController);
            }
        };

        loadMenuTask.setOnSucceeded(event -> {
            LoadResult result = loadMenuTask.getValue();
            final String nombreUsuario;
            String tempNombreUsuario = LoginServiceImplFX.getNombreUsuario();
            if (tempNombreUsuario == null || tempNombreUsuario.trim().isEmpty()) {
                nombreUsuario = "Usuario";
            } else {
                nombreUsuario = tempNombreUsuario;
            }

            FadeTransition fadeOutCarga = new FadeTransition(Duration.millis(400), rootPaneCarga);
            fadeOutCarga.setFromValue(1.0);
            fadeOutCarga.setToValue(0.0);
            fadeOutCarga.setOnFinished(fadeOutEvent -> {
                StackPane panelBienvenida = new StackPane();
                panelBienvenida.setStyle("-fx-background-color: #083671;");
                Label lblBienvenida = new Label("Bienvenido, " + nombreUsuario + "!");
                lblBienvenida.setFont(Font.font("System", 30));
                lblBienvenida.setStyle("-fx-text-fill: white;");
                panelBienvenida.getChildren().add(lblBienvenida);
                StackPane.setAlignment(lblBienvenida, Pos.CENTER);
                panelBienvenida.setOpacity(0.0);

                Scene escenaBienvenida = new Scene(panelBienvenida, stage.getWidth(), stage.getHeight());
                stage.setScene(escenaBienvenida);

                FadeTransition fadeInBienvenida = new FadeTransition(Duration.millis(600), panelBienvenida);
                fadeInBienvenida.setFromValue(0.0);
                fadeInBienvenida.setToValue(1.0);
                fadeInBienvenida.setOnFinished(eventBienvenidaIn -> {
                    PauseTransition pausa = new PauseTransition(Duration.millis(200));
                    pausa.setOnFinished(eventPausa -> {
                        FadeTransition fadeOutBienvenida = new FadeTransition(Duration.millis(400), panelBienvenida);
                        fadeOutBienvenida.setFromValue(1.0);
                        fadeOutBienvenida.setToValue(0.0);
                        fadeOutBienvenida.setOnFinished(eventBienvenidaOut -> {
                            Scene menuScene = new Scene(result.root);
                            stage.setScene(menuScene);
                            result.root.setOpacity(0.0);
                            stage.show();

                            if (result.controller != null) {
                                result.controller.postDisplaySetup(stage);
                            } else {
                                mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico", "No se pudo obtener el controlador del menú principal.");
                                return;
                            }

                            FadeTransition fadeInMenu = new FadeTransition(Duration.millis(600), result.root);
                            fadeInMenu.setFromValue(0.0);
                            fadeInMenu.setToValue(1.0);
                            fadeInMenu.play();
                        });
                        fadeOutBienvenida.play();
                    });
                    pausa.play();
                });
                fadeInBienvenida.play();
            });
            fadeOutCarga.play();
        });

        loadMenuTask.setOnFailed(event -> {
            Throwable e = loadMenuTask.getException();
            e.printStackTrace();
            String errorMessage = (e.getMessage() != null && !e.getMessage().isEmpty()) ? e.getMessage() : "Causa desconocida.";
            mostrarAlerta(Alert.AlertType.ERROR, "Error Crítico",
                    "No se pudo cargar la aplicación principal: " + errorMessage);
            try {
                FXMLLoader loginLoader = new FXMLLoader(getClass().getResource(PathsFXML.LOGIN_FXML));
                loginLoader.setControllerFactory(controllerFactory);
                Parent loginRoot = loginLoader.load();
                Scene loginScene = new Scene(loginRoot, stage.getScene().getWidth(), stage.getScene().getHeight());
                stage.setScene(loginScene);
                stage.setTitle("Login");
            } catch (IOException ioEx) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error Fatal",
                    "No se pudo recargar la pantalla de login: " + ioEx.getMessage());
                ioEx.printStackTrace();
                Platform.exit();
            }
        });

        Thread thread = new Thread(loadMenuTask);
        thread.setDaemon(true);
        thread.start();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}