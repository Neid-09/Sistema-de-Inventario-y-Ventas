package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar;

import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.MenuPrincipalControllerFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.RolServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IRolService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class RolesControllerFX implements Initializable {

    @FXML
    private TableView<RolFX> tablaRoles;

    @FXML
    private TableColumn<RolFX, Integer> colId;

    @FXML
    private TableColumn<RolFX, String> colNombre;

    @FXML
    private TextField txtNombre;

    @FXML
    private Button btnNuevo;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnAsignarPermisos;

    private IRolService rolService;
    private ObservableList<RolFX> listaRoles;
    private RolFX rolSeleccionado;
    private boolean modoEdicion = false;

    public RolesControllerFX() {
        rolService = new RolServiceImplFX();
        listaRoles = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("idRol"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));

        // Configurar listener para selección en tabla
        tablaRoles.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> mostrarDetallesRol(newSelection));

        // Verificar permisos
        verificarPermisos();

        // Cargar datos
        cargarRoles();
    }

    private void verificarPermisos() {
        boolean tienePermisoGestionar = PermisosUIUtil.tienePermiso("gestionar_roles");

        btnGuardar.setDisable(!tienePermisoGestionar);
        btnNuevo.setDisable(!tienePermisoGestionar);
        btnEliminar.setDisable(!tienePermisoGestionar);
        btnAsignarPermisos.setDisable(!tienePermisoGestionar);
        txtNombre.setEditable(tienePermisoGestionar);
    }

    private void cargarRoles() {
        try {
            listaRoles.clear();
            listaRoles.addAll(rolService.obtenerRoles());
            tablaRoles.setItems(listaRoles);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los roles: " + e.getMessage());
        }
    }

    private void mostrarDetallesRol(RolFX rol) {
        rolSeleccionado = rol;

        if (rol != null) {
            txtNombre.setText(rol.getNombre());
            modoEdicion = true;
        } else {
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        rolSeleccionado = null;
        modoEdicion = false;
    }

    @FXML
    public void nuevoRol() {
        limpiarFormulario();
        txtNombre.requestFocus();
    }

    @FXML
    public void guardarRol() {
        if (!validarFormulario()) {
            return;
        }

        try {
            if (modoEdicion && rolSeleccionado != null) {
                rolSeleccionado.setNombre(txtNombre.getText());

                rolService.actualizarRol(rolSeleccionado.getIdRol(), rolSeleccionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Rol actualizado correctamente");
            } else {
                RolFX nuevoRol = new RolFX();
                nuevoRol.setNombre(txtNombre.getText());

                rolService.crearRol(nuevoRol);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Rol creado correctamente");
            }

            cargarRoles();
            limpiarFormulario();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo guardar el rol: " + e.getMessage());
        }
    }

    @FXML
    public void confirmarEliminarRol() {
        if (rolSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección",
                    "Debe seleccionar un rol para eliminar");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este rol?");
        alert.setContentText("Esta acción no se puede deshacer y podría afectar a usuarios");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            eliminarRol();
        }
    }

    private void eliminarRol() {
        try {
            rolService.eliminarRol(rolSeleccionado.getIdRol());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Rol eliminado correctamente");
            cargarRoles();
            limpiarFormulario();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo eliminar el rol: " + e.getMessage());
        }
    }

    @FXML
    public void navegarAsignarPermisos() {
        if (rolSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección",
                    "Debe seleccionar un rol para asignar permisos");
            return;
        }

        try {
            // Obtener el stage actual
            Stage stage = (Stage) btnAsignarPermisos.getScene().getWindow();
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            if (menuController != null) {
                // Guardar el ID del rol seleccionado en algún lugar accesible
                // para el controlador de asignación de permisos
                // (Esto se puede mejorar con un patrón de comunicación entre controladores)
                PPermisosRolesCtrlFX.setRolSeleccionadoId(rolSeleccionado.getIdRol());

                // Cargar la vista de asignación de permisos
                menuController.cargarModuloEnPanel(PathsFXML.PLANTILLAS_ROLES);
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "No se pudo encontrar el controlador del menú principal");
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Error al navegar a la asignación de permisos: " + e.getMessage());
        }
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación",
                    "El nombre del rol es obligatorio");
            txtNombre.requestFocus();
            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}