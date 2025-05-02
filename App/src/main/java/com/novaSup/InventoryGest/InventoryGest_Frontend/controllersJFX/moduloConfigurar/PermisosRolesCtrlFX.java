package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar;

import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.MenuPrincipalControllerFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.RolServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IPermisoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IRolService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.PermisoServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class PermisosRolesCtrlFX implements Initializable {

    private final IRolService rolService;
    private final IPermisoService permisoService;

    @FXML
    private Button btnGestionPermisos;

    @FXML
    private Button btnGestionRoles;

    @FXML
    private Button btnPlantillasRoles;

    @FXML
    private Button btnPermisosUsuarios;

    public PermisosRolesCtrlFX() {
        this.rolService = new RolServiceImplFX();
        this.permisoService = new PermisoServiceImplFX();
    }

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
        PermisosUIUtil.configurarBoton(btnGestionPermisos, "gestionar_roles_permisos");

        // Botón de Gestión de Roles solo visible si tiene permiso "gestionar_roles"
        PermisosUIUtil.configurarBoton(btnGestionRoles, "gestionar_roles_permisos");

        // Botón de Plantillas de Roles visible si gestiona roles y permisos
        PermisosUIUtil.configurarBoton(btnPlantillasRoles, "gestionar_roles_permisos");

        // Botón de Permisos Usuarios visible si gestiona usuarios
        PermisosUIUtil.configurarBoton(btnPermisosUsuarios, "gestionar_roles_permisos");
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
        navegarAModulo(event, "gestionar_roles_permisos", PathsFXML.GESTION_PERMISOS,
                "No se pudo cargar el módulo de gestión de permisos");
    }

    @FXML
    private void navegarGestionRoles(ActionEvent event) {
        navegarAModulo(event, "gestionar_roles_permisos", PathsFXML.GESTION_ROLES,
                "No se pudo cargar el módulo de gestión de roles");
    }

    @FXML
    private void navegarPlantillasRoles(ActionEvent event) {
        navegarAModulo(event, "gestionar_roles_permisos", PathsFXML.PLANTILLAS_ROLES,
                "No se pudo cargar el módulo de plantillas de roles");
    }

    @FXML
    private void navegarPermisosUsuarios(ActionEvent event) {
        navegarAModulo(event, "gestionar_roles_permisos", PathsFXML.PERMISOS_USUARIOS,
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