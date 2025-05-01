package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class ConfigurarControllerFX {
    @FXML
    private Button btnGestionarUsuarios;

    @FXML
    private Button btnConfiguracionesGlobales;

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

    @FXML
    public void irConfiguracionesGlobales(ActionEvent event) {
        // El permiso correcto es 'gestionar_configuracion' según el backend
        if (!PermisosUIUtil.verificarPermisoConAlerta("gestionar_configuracion")) {
            return;
        }

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            if (menuController != null) {
                menuController.cargarModuloEnPanel(PathsFXML.CONFIGURACIONES_GLOBALES);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "No se pudo encontrar el controlador del menú principal");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar las configuraciones globales: " + e.getMessage());
        }
    }

    @FXML
    public void irGestionPermisosRoles(ActionEvent event) {
        if (!PermisosUIUtil.verificarPermisoConAlerta("gestionar_permisos") &&
                !PermisosUIUtil.verificarPermisoConAlerta("gestionar_roles") &&
                !PermisosUIUtil.verificarPermisoConAlerta("gestionar_usuarios")) {
            return;
        }

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            if (menuController != null) {
                menuController.cargarModuloEnPanel(PathsFXML.GESTION_PERMISOS_ROLES);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "No se pudo encontrar el controlador del menú principal");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la gestión de permisos y roles: " + e.getMessage());
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