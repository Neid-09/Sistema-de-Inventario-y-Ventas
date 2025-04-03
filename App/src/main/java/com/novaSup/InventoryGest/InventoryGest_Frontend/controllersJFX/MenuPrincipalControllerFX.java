package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
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
    private Label lblRol;

    @FXML
    private Button btnCerrarSesion;

    @FXML
    private Button btnVender;

    @FXML
    private Button btnProductos;

    @FXML
    private Button btnVerHistorial;

    @FXML
    private Button btnConfiguracion;

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

                // Configurar visibilidad de botones según permisos
                configurarPermisosMenu();

                // IMPORTANTE: Verificar que lblRol no sea nulo antes de usarlo
                if (lblRol != null) {
                    if (LoginServiceImplFX.getUsuarioActual() != null &&
                            LoginServiceImplFX.getUsuarioActual().getRol() != null) {
                        lblRol.setText("ROL: " + LoginServiceImplFX.getUsuarioActual().getRol().getNombre());
                    }
                } else {
                    System.err.println("Error: lblRol es nulo en initialize");
                }
            } catch (Exception e) {
                System.err.println("Error en inicialización: " + e.getMessage());
                e.printStackTrace();
            }
        });
    }



    private void configurarPermisosMenu() {
        // Configurar botones según permisos
        btnVender.setVisible(LoginServiceImplFX.tienePermiso("crear_venta"));
        btnVender.setManaged(LoginServiceImplFX.tienePermiso("crear_venta"));

        btnProductos.setVisible(LoginServiceImplFX.tienePermiso("gestionar_productos") ||
                LoginServiceImplFX.tienePermiso("ver_productos"));
        btnProductos.setManaged(LoginServiceImplFX.tienePermiso("gestionar_productos") ||
                LoginServiceImplFX.tienePermiso("ver_productos"));

        btnVerHistorial.setVisible(LoginServiceImplFX.tienePermiso("ver_stock") ||
                LoginServiceImplFX.tienePermiso("gestionar_stock"));
        btnVerHistorial.setManaged(LoginServiceImplFX.tienePermiso("ver_stock") ||
                LoginServiceImplFX.tienePermiso("gestionar_stock"));

        btnConfiguracion.setVisible(LoginServiceImplFX.tienePermiso("gestionar_configuracion"));
        btnConfiguracion.setManaged(LoginServiceImplFX.tienePermiso("gestionar_configuracion"));
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
    void irVender(ActionEvent event) {
        if (!LoginServiceImplFX.tienePermiso("crear_venta")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acceso denegado",
                    "No tienes permisos para acceder al módulo de ventas.");
            return;
        }
        cargarModuloEnPanel(PathsFXML.VENDER_FXML);
    }

    @FXML
    void irGestionProductos(ActionEvent event) {
        if (!LoginServiceImplFX.tienePermiso("gestionar_productos") &&
                !LoginServiceImplFX.tienePermiso("ver_productos")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acceso denegado",
                    "No tienes permisos para acceder al módulo de productos.");
            return;
        }
        cargarModuloEnPanel(PathsFXML.CRUD_PRDUCTOS);
    }

    @FXML
    void verHistorialStock(ActionEvent event) {
        if (!LoginServiceImplFX.tienePermiso("ver_stock") &&
                !LoginServiceImplFX.tienePermiso("gestionar_stock")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acceso denegado",
                    "No tienes permisos para acceder al historial de stock.");
            return;
        }
        cargarModuloEnPanel(PathsFXML.CONTROLSTOCK_FXML);
    }

    @FXML
    void irConfiguracion(ActionEvent event) {
        if (!LoginServiceImplFX.tienePermiso("gestionar_configuracion")) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acceso denegado",
                    "No tienes permisos para acceder a la configuración.");
            return;
        }
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