package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar;

import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.MenuPrincipalControllerFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class PermisosRolesCtrlFX implements Initializable {

    @FXML
    private Button btnGestionPermisos;

    @FXML
    private Button btnGestionRoles;

    @FXML
    private Button btnPlantillasRoles;

    @FXML
    private Button btnPermisosUsuarios;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar los eventos de botones
        btnGestionPermisos.setOnAction(this::navegarGestionPermisos);
        btnGestionRoles.setOnAction(this::navegarGestionRoles);
        btnPlantillasRoles.setOnAction(this::navegarPlantillasRoles);
        btnPermisosUsuarios.setOnAction(this::navegarPermisosUsuarios);

        // Configurar visibilidad según permisos
        configurarPermisos();
    }

    private void configurarPermisos() {
        // Botón de Gestión de Permisos solo visible si tiene permiso "gestionar_permisos"
        PermisosUIUtil.configurarBoton(btnGestionPermisos, "gestionar_permisos");

        // Botón de Gestión de Roles solo visible si tiene permiso "gestionar_roles"
        PermisosUIUtil.configurarBoton(btnGestionRoles, "gestionar_roles");

        // Botón de Plantillas de Roles visible si gestiona roles y permisos
        boolean puedeGestionarPlantillas = LoginServiceImplFX.tienePermiso("gestionar_roles") &&
                LoginServiceImplFX.tienePermiso("gestionar_permisos");
        btnPlantillasRoles.setVisible(puedeGestionarPlantillas);
        btnPlantillasRoles.setManaged(puedeGestionarPlantillas);
        btnPlantillasRoles.setDisable(!puedeGestionarPlantillas);

        // Botón de Permisos Usuarios visible si gestiona usuarios
        PermisosUIUtil.configurarBoton(btnPermisosUsuarios, "gestionar_usuarios");
    }

    private void navegarAModulo(ActionEvent event, String permiso, String path, String errorMsg) {
        if (!PermisosUIUtil.tienePermiso(permiso)) {
            mostrarAlerta(Alert.AlertType.WARNING, "Acceso denegado",
                    "No tienes permisos para acceder a esta funcionalidad.");
            return;
        }

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            if (menuController != null) {
                menuController.cargarModuloEnPanel(path);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "No se pudo encontrar el controlador del menú principal");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", errorMsg + ": " + e.getMessage());
        }
    }

    @FXML
    private void navegarGestionPermisos(ActionEvent event) {
        navegarAModulo(event, "gestionar_permisos", PathsFXML.GESTION_PERMISOS,
                "No se pudo cargar el módulo de gestión de permisos");
    }

    @FXML
    private void navegarGestionRoles(ActionEvent event) {
        navegarAModulo(event, "gestionar_roles", PathsFXML.GESTION_ROLES,
                "No se pudo cargar el módulo de gestión de roles");
    }

    @FXML
    private void navegarPlantillasRoles(ActionEvent event) {
        navegarAModulo(event, "gestionar_roles", PathsFXML.PLANTILLAS_ROLES,
                "No se pudo cargar el módulo de plantillas de roles");
    }

    @FXML
    private void navegarPermisosUsuarios(ActionEvent event) {
        navegarAModulo(event, "gestionar_usuarios", PathsFXML.PERMISOS_USUARIOS,
                "No se pudo cargar el módulo de permisos de usuarios");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}