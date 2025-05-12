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

public class ConfigurarControllerFX {
    @FXML
    private Button btnGestionarUsuarios;

    @FXML
    private Button btnConfiguracionesGlobales;

    @FXML
    private Button btnGestionarPermisosRoles;

    @FXML
    private Button btnConfigurarImpuestos;

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

    @FXML
    public void irConfigurarImpuestos(ActionEvent event) {
        // Verificar permiso (ejemplo: "configurar_impuestos")
        if (!PermisosUIUtil.verificarPermisoConAlerta("configurar_impuestos")) {
            // Si no tiene el permiso general, verificar permisos más específicos si aplica
            // por ejemplo, si se permite ver pero no editar ciertos aspectos.
            // Esto dependerá de tu lógica de permisos detallada.
            // if (!PermisosUIUtil.verificarPermisoConAlerta("ver_tipos_impuesto") && 
            //     !PermisosUIUtil.verificarPermisoConAlerta("ver_tasas_impuesto")) {
            //     return;
            // }
            return; // Por ahora, si no tiene el permiso general, no accede.
        }

        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            if (menuController != null) {
                // Asegúrate de que PathsFXML.CONFIGURACION_IMPUESTOS esté definido
                // y apunte a "/views/configuracion/ConfiguracionImpuestosView.fxml" o la ruta correcta.
                menuController.cargarModuloEnPanel(PathsFXML.CONFIGURACION_IMPUESTOS); 
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "No se pudo encontrar el controlador del menú principal");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la configuración de impuestos: " + e.getMessage());
            e.printStackTrace(); // Es útil para ver el error detallado en la consola durante el desarrollo
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