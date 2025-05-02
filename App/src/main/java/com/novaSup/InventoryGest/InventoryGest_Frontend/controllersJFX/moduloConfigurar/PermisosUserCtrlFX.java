package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.PermisoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IPermisoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IUsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.util.*;
import java.util.stream.Collectors;

public class PermisosUserCtrlFX implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(PermisosUserCtrlFX.class);

    @FXML private ComboBox<UsuarioFX> comboUsuarios;
    @FXML private Label lblRolUsuario;
    @FXML private Label lblNombreUsuario;

    // Tabla de permisos del rol
    @FXML private TableView<PermisoFX> tablaPermisosRol;
    @FXML private TableColumn<PermisoFX, Integer> colIdPermisoRol;
    @FXML private TableColumn<PermisoFX, String> colNombrePermisoRol;
    @FXML private TableColumn<PermisoFX, String> colDescripcionPermisoRol;

    // Tabla de permisos disponibles
    @FXML private TableView<PermisoFX> tablaPermisosDisponibles;
    @FXML private TableColumn<PermisoFX, Integer> colIdPermisoDisponible;
    @FXML private TableColumn<PermisoFX, String> colNombrePermisoDisponible;
    @FXML private TableColumn<PermisoFX, String> colDescripcionPermisoDisponible;

    // Tabla de permisos asignados
    @FXML private TableView<PermisoFX> tablaPermisosAsignados;
    @FXML private TableColumn<PermisoFX, Integer> colIdPermisoAsignado;
    @FXML private TableColumn<PermisoFX, String> colNombrePermisoAsignado;
    @FXML private TableColumn<PermisoFX, String> colDescripcionPermisoAsignado;

    @FXML private Button btnAsignarPermiso;
    @FXML private Button btnQuitarPermiso;
    @FXML private Button btnGuardar;
    @FXML private Button btnCancelar;
    @FXML private Button btnCargarPermisos;

    private final IUsuarioService usuarioService;
    private final IPermisoService permisoService;

    private ObservableList<UsuarioFX> listaUsuarios;
    private ObservableList<PermisoFX> permisosRol;
    private ObservableList<PermisoFX> permisosDisponibles;
    private ObservableList<PermisoFX> permisosAsignados;

    private Set<PermisoFX> permisosOriginales;
    private UsuarioFX usuarioSeleccionado;

    // Constructor para inyección de dependencias
    public PermisosUserCtrlFX(IUsuarioService usuarioService, IPermisoService permisoService) {
        this.usuarioService = usuarioService;
        this.permisoService = permisoService;
        this.listaUsuarios = FXCollections.observableArrayList();
        this.permisosRol = FXCollections.observableArrayList();
        this.permisosDisponibles = FXCollections.observableArrayList();
        this.permisosAsignados = FXCollections.observableArrayList();
        this.permisosOriginales = new HashSet<>();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTablas();
        configurarControles();
        verificarPermisos();
        cargarUsuarios();
    }

    private void configurarTablas() {
        // Configurar tabla de permisos de rol
        colIdPermisoRol.setCellValueFactory(new PropertyValueFactory<>("idPermiso"));
        colNombrePermisoRol.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcionPermisoRol.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Configurar tabla de permisos disponibles
        colIdPermisoDisponible.setCellValueFactory(new PropertyValueFactory<>("idPermiso"));
        colNombrePermisoDisponible.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcionPermisoDisponible.setCellValueFactory(new PropertyValueFactory<>("descripcion"));

        // Configurar tabla de permisos asignados
        colIdPermisoAsignado.setCellValueFactory(new PropertyValueFactory<>("idPermiso"));
        colNombrePermisoAsignado.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcionPermisoAsignado.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
    }

    private void configurarControles() {
        // Configurar el formateo del ComboBox de usuarios
        comboUsuarios.setConverter(new StringConverter<UsuarioFX>() {
            @Override
            public String toString(UsuarioFX usuario) {
                return usuario != null ? usuario.getNombre() + " (" + usuario.getCorreo() + ")" : "";
            }

            @Override
            public UsuarioFX fromString(String string) {
                return null; // No necesitamos convertir de String a Usuario
            }
        });

        // Deshabilitar botones por defecto hasta seleccionar un usuario
        btnAsignarPermiso.setDisable(true);
        btnQuitarPermiso.setDisable(true);
        btnGuardar.setDisable(true);

        // Configurar eventos de selección en tablas
        tablaPermisosDisponibles.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> btnAsignarPermiso.setDisable(newSelection == null));

        tablaPermisosAsignados.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> btnQuitarPermiso.setDisable(newSelection == null));
    }

    private void verificarPermisos() {
        boolean tienePermisoGestionar = PermisosUIUtil.tienePermiso("gestionar_usuarios");

        comboUsuarios.setDisable(!tienePermisoGestionar);
        btnAsignarPermiso.setDisable(!tienePermisoGestionar);
        btnQuitarPermiso.setDisable(!tienePermisoGestionar);
        btnGuardar.setDisable(!tienePermisoGestionar);
        btnCargarPermisos.setDisable(!tienePermisoGestionar);
    }

    private void cargarUsuarios() {
        try {
            listaUsuarios.clear();
            listaUsuarios.addAll(usuarioService.obtenerUsuarios());
            comboUsuarios.setItems(listaUsuarios);

            if (!listaUsuarios.isEmpty()) {
                comboUsuarios.getSelectionModel().selectFirst();
            }
        } catch (Exception e) {
            logger.error("Error al cargar usuarios: {}", e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron cargar los usuarios: " + e.getMessage());
        }
    }

    @FXML
    public void cargarPermisosUsuario() {
        usuarioSeleccionado = comboUsuarios.getValue();
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección",
                    "Debe seleccionar un usuario");
            return;
        }

        try {
            // Mostrar información del usuario
            lblNombreUsuario.setText(usuarioSeleccionado.getNombre());
            lblRolUsuario.setText(usuarioSeleccionado.getRol() != null ?
                    usuarioSeleccionado.getRol().getNombre() : "Sin rol");

            // Cargar permisos del rol
            Set<PermisoFX> permisosDelRol = new HashSet<>();
            if (usuarioSeleccionado.getRol() != null) {
                permisosDelRol = usuarioService.obtenerPermisosRol(
                        usuarioSeleccionado.getRol().getIdRol());
                permisosRol.clear();
                permisosRol.addAll(permisosDelRol);
                tablaPermisosRol.setItems(permisosRol);
            } else {
                permisosRol.clear();
                tablaPermisosRol.setItems(permisosRol);
            }

            // Recopilar IDs de permisos del rol
            Set<Integer> idsPermisosRol = permisosDelRol.stream()
                    .map(PermisoFX::getIdPermiso)
                    .collect(Collectors.toSet());

            // Cargar permisos específicos del usuario
            Set<PermisoFX> permisosEspecificos = usuarioService.obtenerPermisosEspecificos(
                    usuarioSeleccionado.getIdUsuario());

            // Filtrar permisos específicos para eliminar los que ya están en el rol
            Set<PermisoFX> permisosEspecificosFiltrados = permisosEspecificos.stream()
                    .filter(p -> !idsPermisosRol.contains(p.getIdPermiso()))
                    .collect(Collectors.toSet());

            permisosAsignados.clear();
            permisosAsignados.addAll(permisosEspecificosFiltrados);
            tablaPermisosAsignados.setItems(permisosAsignados);

            // Guardar copia de los permisos originales para comparar al guardar
            permisosOriginales = new HashSet<>(permisosEspecificosFiltrados);

            // Cargar permisos disponibles (todos los permisos menos los ya asignados y los del rol)
            actualizarPermisosDisponibles();

            // Habilitar botón guardar
            btnGuardar.setDisable(false);

        } catch (Exception e) {
            logger.error("Error al cargar permisos: {}", e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Error al cargar permisos: " + e.getMessage());
        }
    }

    private void actualizarPermisosDisponibles() throws Exception {
        List<PermisoFX> todosLosPermisos = permisoService.obtenerPermisos();

        // Obtener IDs de permisos ya asignados al usuario
        Set<Integer> idsPermisosAsignados = permisosAsignados.stream()
                .map(PermisoFX::getIdPermiso)
                .collect(Collectors.toSet());

        // Obtener IDs de permisos del rol para excluirlos también
        Set<Integer> idsPermisosRol = permisosRol.stream()
                .map(PermisoFX::getIdPermiso)
                .collect(Collectors.toSet());

        // Filtrar permisos que no estén ya asignados ni formen parte del rol
        List<PermisoFX> disponibles = todosLosPermisos.stream()
                .filter(p -> !idsPermisosAsignados.contains(p.getIdPermiso())
                        && !idsPermisosRol.contains(p.getIdPermiso()))
                .collect(Collectors.toList());

        permisosDisponibles.clear();
        permisosDisponibles.addAll(disponibles);
        tablaPermisosDisponibles.setItems(permisosDisponibles);
    }

    @FXML
    public void asignarPermiso() {
        PermisoFX seleccionado = tablaPermisosDisponibles.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        permisosDisponibles.remove(seleccionado);
        permisosAsignados.add(seleccionado);
    }

    @FXML
    public void quitarPermiso() {
        PermisoFX seleccionado = tablaPermisosAsignados.getSelectionModel().getSelectedItem();
        if (seleccionado == null) return;

        permisosAsignados.remove(seleccionado);
        permisosDisponibles.add(seleccionado);
    }

    @FXML
    public void guardarPermisos() {
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección",
                    "Debe seleccionar un usuario");
            return;
        }

        try {
            // Convertir a lista de IDs para enviar al servidor
            List<Integer> idsPermisosAsignados = permisosAsignados.stream()
                    .map(PermisoFX::getIdPermiso)
                    .collect(Collectors.toList());

            // Guardar permisos
            usuarioService.asignarPermisosEspecificos(
                    usuarioSeleccionado.getIdUsuario(), idsPermisosAsignados);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Se han guardado los permisos para el usuario " + usuarioSeleccionado.getNombre());

            // Actualizar permisos originales
            permisosOriginales.clear();
            permisosOriginales.addAll(permisosAsignados);

        } catch (Exception e) {
            logger.error("Error al guardar permisos: {}", e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudieron guardar los permisos: " + e.getMessage());
        }
    }

    @FXML
    public void cancelar() {
        if (!permisosOriginales.isEmpty() && usuarioSeleccionado != null) {
            // Restaurar permisos a su estado original
            permisosAsignados.clear();
            permisosAsignados.addAll(permisosOriginales);

            try {
                actualizarPermisosDisponibles();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Información",
                        "Se han restaurado los permisos originales");
            } catch (Exception e) {
                logger.error("Error al restaurar permisos: {}", e.getMessage());
            }
        } else {
            // Limpiar pantalla
            limpiarSeleccion();
        }
    }

    private void limpiarSeleccion() {
        usuarioSeleccionado = null;
        lblNombreUsuario.setText("-");
        lblRolUsuario.setText("-");

        permisosRol.clear();
        permisosAsignados.clear();
        permisosDisponibles.clear();
        permisosOriginales.clear();

        tablaPermisosRol.setItems(permisosRol);
        tablaPermisosAsignados.setItems(permisosAsignados);
        tablaPermisosDisponibles.setItems(permisosDisponibles);

        btnGuardar.setDisable(true);
        comboUsuarios.getSelectionModel().clearSelection();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}