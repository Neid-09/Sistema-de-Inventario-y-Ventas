package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.PermisoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IPermisoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class PermisosControllerFX implements Initializable {

    @FXML private TableView<PermisoFX> tablaPermisos;
    @FXML private TableColumn<PermisoFX, Integer> colId;
    @FXML private TableColumn<PermisoFX, String> colNombre;
    @FXML private TableColumn<PermisoFX, String> colDescripcion;

    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private Button btnGuardar;
    @FXML private Button btnNuevo;
    @FXML private Button btnEliminar;

    private final IPermisoService permisoService;
    private ObservableList<PermisoFX> listaPermisos;
    private PermisoFX permisoSeleccionado;
    private boolean modoEdicion = false;

    // Constructor para inyección de dependencias
    public PermisosControllerFX(IPermisoService permisoService) {
        this.permisoService = permisoService;
        listaPermisos = FXCollections.observableArrayList();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Configurar tabla
        colId.setCellValueFactory(new PropertyValueFactory<>("idPermiso"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Configurar listener para selección en tabla
        tablaPermisos.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> mostrarDetallesPermiso(newSelection));

        // Configurar botones
        btnGuardar.setOnAction(event -> guardarPermiso());
        btnNuevo.setOnAction(event -> nuevoPermiso());
        btnEliminar.setOnAction(event -> confirmarEliminarPermiso());

        // Configurar permisos
        verificarPermisos();

        // Cargar datos
        cargarPermisos();
    }

    private void verificarPermisos() {
        boolean tienePermisoGestionar = PermisosUIUtil.tienePermiso("gestionar_permisos");

        btnGuardar.setDisable(!tienePermisoGestionar);
        btnNuevo.setDisable(!tienePermisoGestionar);
        btnEliminar.setDisable(!tienePermisoGestionar);
        txtNombre.setEditable(tienePermisoGestionar);
        txtDescripcion.setEditable(tienePermisoGestionar);
    }

    private void cargarPermisos() {
        try {
            listaPermisos.clear();
            listaPermisos.addAll(permisoService.obtenerPermisos());
            tablaPermisos.setItems(listaPermisos);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los permisos: " + e.getMessage());
        }
    }

    private void mostrarDetallesPermiso(PermisoFX permiso) {
        permisoSeleccionado = permiso;

        if (permiso != null) {
            txtNombre.setText(permiso.getNombre());
            txtDescripcion.setText(permiso.getDescripcion());
            modoEdicion = true;
        } else {
            limpiarFormulario();
        }
    }

    private void limpiarFormulario() {
        txtNombre.clear();
        txtDescripcion.clear();
        permisoSeleccionado = null;
        modoEdicion = false;
    }

    @FXML
    private void nuevoPermiso() {
        limpiarFormulario();
        txtNombre.requestFocus();
    }

    @FXML
    private void guardarPermiso() {
        if (!validarFormulario()) {
            return;
        }

        try {
            if (modoEdicion && permisoSeleccionado != null) {
                permisoSeleccionado.setNombre(txtNombre.getText());
                permisoSeleccionado.setDescripcion(txtDescripcion.getText());

                permisoService.actualizarPermiso(permisoSeleccionado.getIdPermiso(), permisoSeleccionado);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Permiso actualizado correctamente");
            } else {
                PermisoFX nuevoPermiso = new PermisoFX();
                nuevoPermiso.setNombre(txtNombre.getText());
                nuevoPermiso.setDescripcion(txtDescripcion.getText());

                permisoService.crearPermiso(nuevoPermiso);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                        "Permiso creado correctamente");
            }

            cargarPermisos();
            limpiarFormulario();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo guardar el permiso: " + e.getMessage());
        }
    }

    @FXML
    private void confirmarEliminarPermiso() {
        if (permisoSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección",
                    "Debe seleccionar un permiso para eliminar");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este permiso?");
        alert.setContentText("Esta acción no se puede deshacer y podría afectar a roles y usuarios");

        Optional<ButtonType> resultado = alert.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            eliminarPermiso();
        }
    }

    private void eliminarPermiso() {
        try {
            permisoService.eliminarPermiso(permisoSeleccionado.getIdPermiso());
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Permiso eliminado correctamente");
            cargarPermisos();
            limpiarFormulario();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo eliminar el permiso: " + e.getMessage());
        }
    }

    private boolean validarFormulario() {
        if (txtNombre.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación",
                    "El nombre del permiso es obligatorio");
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