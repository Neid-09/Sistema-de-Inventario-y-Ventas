package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.PermisoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.PermisoServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.RolServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IPermisoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IRolService;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PPermisosRolesCtrlFX implements Initializable {

    @FXML
    private ComboBox<RolFX> cbRoles;

    @FXML
    private TableView<PermisoConEstado> tblPermisos;

    @FXML
    private TableColumn<PermisoConEstado, String> colPermiso;

    @FXML
    private TableColumn<PermisoConEstado, String> colDescripcion;

    @FXML
    private TableColumn<PermisoConEstado, Boolean> colAsignado;

    @FXML
    private Button btnGuardar;

    private final IPermisoService permisoService;
    private final IRolService rolService;

    private ObservableList<RolFX> listaRoles;
    private ObservableList<PermisoConEstado> listaPermisos;

    // Variable estática para comunicación entre controladores
    private static Integer rolSeleccionadoId;

    public PPermisosRolesCtrlFX() {
        this.permisoService = new PermisoServiceImplFX();
        this.rolService = new RolServiceImplFX();
        this.listaRoles = FXCollections.observableArrayList();
        this.listaPermisos = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar tabla de permisos
        colPermiso.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPermiso().getNombre()));
        colDescripcion.setCellValueFactory(cellData ->
                new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPermiso().getDescripcion()));

        // Hacer la tabla editable
        tblPermisos.setEditable(true);

        // Configurar la columna de checkbox como editable
        colAsignado.setCellValueFactory(cellData -> cellData.getValue().asignadoProperty());
        colAsignado.setCellFactory(CheckBoxTableCell.forTableColumn(colAsignado));
        colAsignado.setEditable(true);

        // Cargar roles
        cargarRoles();

        // Configurar listener para cambio de rol
        cbRoles.valueProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                cargarPermisosParaRol(newValue.getIdRol());
            }
        });

        // Configurar botón guardar
        btnGuardar.setOnAction(event -> guardarAsignacionPermisos());

        // Si hay un rol preseleccionado, cargarlo
        if (rolSeleccionadoId != null) {
            cargarRolPreseleccionado();
        }
    }

    private void cargarRoles() {
        try {
            listaRoles.clear();
            listaRoles.addAll(rolService.obtenerRoles());
            cbRoles.setItems(listaRoles);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los roles: " + e.getMessage());
        }
    }

    private void cargarRolPreseleccionado() {
        if (rolSeleccionadoId == null) return;

        for (RolFX rol : listaRoles) {
            if (rol.getIdRol().equals(rolSeleccionadoId)) {
                cbRoles.getSelectionModel().select(rol);
                break;
            }
        }

        // Limpiar el ID del rol seleccionado para evitar problemas en futuras cargas
        rolSeleccionadoId = null;
    }

    private void cargarPermisosParaRol(Integer idRol) {
        try {
            // Obtener todos los permisos
            List<PermisoFX> todosPermisos = permisoService.obtenerPermisos();

            // Obtener permisos asignados al rol
            Set<PermisoFX> permisosAsignados = rolService.obtenerPermisosRol(idRol);

            // Crear lista de permisos con estado de asignación
            listaPermisos.clear();
            for (PermisoFX permiso : todosPermisos) {
                boolean asignado = permisosAsignados.stream()
                        .anyMatch(p -> p.getIdPermiso().equals(permiso.getIdPermiso()));
                listaPermisos.add(new PermisoConEstado(permiso, asignado));
            }

            tblPermisos.setItems(listaPermisos);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los permisos: " + e.getMessage());
        }
    }

    private void guardarAsignacionPermisos() {
        RolFX rolSeleccionado = cbRoles.getValue();
        if (rolSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación",
                    "Debe seleccionar un rol");
            return;
        }

        try {
            // Obtener IDs de permisos seleccionados
            List<Integer> permisosSeleccionados = listaPermisos.stream()
                    .filter(PermisoConEstado::isAsignado)
                    .map(p -> p.getPermiso().getIdPermiso())
                    .collect(Collectors.toList());

            // Guardar asignación
            rolService.asignarPermisosRol(rolSeleccionado.getIdRol(), permisosSeleccionados);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Se han guardado los permisos para el rol " + rolSeleccionado.getNombre());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron guardar los permisos: " + e.getMessage());
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    // Clase interna para manejar permisos con estado de asignación
    public static class PermisoConEstado {
        private final PermisoFX permiso;
        private final SimpleBooleanProperty asignado;

        public PermisoConEstado(PermisoFX permiso, boolean asignado) {
            this.permiso = permiso;
            this.asignado = new SimpleBooleanProperty(asignado);
        }

        public PermisoFX getPermiso() {
            return permiso;
        }

        public boolean isAsignado() {
            return asignado.get();
        }

        public SimpleBooleanProperty asignadoProperty() {
            return asignado;
        }

        public void setAsignado(boolean asignado) {
            this.asignado.set(asignado);
        }
    }

    // Método para comunicación entre controladores
    public static void setRolSeleccionadoId(Integer id) {
        rolSeleccionadoId = id;
    }
}