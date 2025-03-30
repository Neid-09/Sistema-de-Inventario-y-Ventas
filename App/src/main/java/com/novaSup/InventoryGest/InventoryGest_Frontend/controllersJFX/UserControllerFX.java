package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IUsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.UsuarioServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.util.List;

import static com.novaSup.InventoryGest.MainApp.springContext;

@Controller
public class UserControllerFX {

    @FXML
    private ComboBox<RolFX> cmbRol;

    @FXML
    private TableColumn<UsuarioFX, Integer> colId;

    @FXML
    private TableColumn<UsuarioFX, String> colNombre;

    @FXML
    private TableColumn<UsuarioFX, String> colCorreo;

    @FXML
    private TableColumn<UsuarioFX, String> colTelefono;

    @FXML
    private TableColumn<UsuarioFX, String> colRol;

    @FXML
    private TableView<UsuarioFX> tablaUsuarios;

    @FXML
    private PasswordField txtContraseña;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    private Button btnRegistrar;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnEliminar;

    // Usamos la interfaz en lugar de la implementación directa
    private final IUsuarioService usuarioService = new UsuarioServiceImplFX();

    @FXML
    public void initialize() {

        Platform.runLater(() -> {
            if (tablaUsuarios.getScene() != null && tablaUsuarios.getScene().getWindow() != null) {
                Stage stage = (Stage) tablaUsuarios.getScene().getWindow();
                stage.setResizable(true);
            }
        });

        // Configurar estado inicial de botones
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);

        // Configurar selección de tabla
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        seleccionarUsuario();
                    }
                });

        cargarRoles();
        cargarUsuarios();

        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colRol.setCellValueFactory(param -> new SimpleStringProperty(param.getValue().getRol().getRol()));

        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                seleccionarUsuario();
            }
        });
    }

    @FXML
    void registrarUsuario(ActionEvent event) {
        try {
            if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()
                    || txtTelefono.getText().isEmpty() || txtContraseña.getText().isEmpty()
                    || cmbRol.getSelectionModel().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Por favor rellena todos los campos.");
                return;
            }

            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String telefono = txtTelefono.getText();
            String contraseña = txtContraseña.getText();
            RolFX rolSeleccionado = cmbRol.getSelectionModel().getSelectedItem();

            UsuarioFX nuevoUsuario = new UsuarioFX(null, nombre, correo, telefono, contraseña, rolSeleccionado);
            usuarioService.registrarUsuario(nuevoUsuario);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "¡Usuario registrado exitosamente!");
            limpiarCampos();
            cargarUsuarios();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarRoles() {
        try {
            List<RolFX> roles = usuarioService.obtenerRoles();
            cmbRol.setItems(FXCollections.observableArrayList(roles));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarUsuarios() {
        try {
            List<UsuarioFX> usuarios = usuarioService.obtenerUsuarios();
            tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    void actualizarUsuario(ActionEvent event) {
        try {
            UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (usuarioSeleccionado == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Por favor selecciona un usuario para actualizar.");
                return;
            }

            if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()
                    || txtTelefono.getText().isEmpty() || txtContraseña.getText().isEmpty()
                    || cmbRol.getSelectionModel().isEmpty()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Por favor rellena todos los campos.");
                return;
            }

            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String telefono = txtTelefono.getText();
            String contraseña = txtContraseña.getText();
            RolFX rolSeleccionado = cmbRol.getSelectionModel().getSelectedItem();

            UsuarioFX usuarioActualizado = new UsuarioFX(usuarioSeleccionado.getIdUsuario(), nombre, correo, telefono, contraseña, rolSeleccionado);
            usuarioService.actualizarUsuario(usuarioSeleccionado.getIdUsuario(), usuarioActualizado);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "¡Usuario actualizado exitosamente!");
            limpiarCampos();
            cargarUsuarios();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    @FXML
    void seleccionarUsuario() {
        UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            // Cargar datos en los campos
            txtNombre.setText(usuarioSeleccionado.getNombre());
            txtCorreo.setText(usuarioSeleccionado.getCorreo());
            txtTelefono.setText(usuarioSeleccionado.getTelefono());
            txtContraseña.setText(usuarioSeleccionado.getContraseña());
            cmbRol.setValue(usuarioSeleccionado.getRol());

            // Gestionar estado de botones
            btnRegistrar.setDisable(true);
            btnActualizar.setDisable(false);
            btnEliminar.setDisable(false);
        }
    }

    @FXML
    void eliminarUsuario(ActionEvent event) {
        try {
            UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (usuarioSeleccionado == null) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Por favor selecciona un usuario para eliminar.");
                return;
            }

            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText(null);
            confirmacion.setContentText("¿Estás seguro de que deseas eliminar este usuario?");

            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                usuarioService.eliminarUsuario(usuarioSeleccionado.getIdUsuario());
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "¡Usuario eliminado exitosamente!");
                cargarUsuarios();
                limpiarCampos();
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void limpiarCampos() {
        // Limpiar todos los campos
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtContraseña.clear();
        cmbRol.getSelectionModel().clearSelection();

        // Deseleccionar fila de la tabla
        tablaUsuarios.getSelectionModel().clearSelection();

        // Restablecer estado de botones
        btnRegistrar.setDisable(false);
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
    }

    @FXML
    void cerrarGestionUsuarios(ActionEvent event) {
        try {
            // Obtener el Stage actual
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Obtener el controlador del MenuPrincipal desde userData
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            if (menuController != null) {
                // Cargar el módulo de configuración en el panel
                menuController.cargarModuloEnPanel(PathsFXML.CONFIGURACION_FXML);
            } else {
                // Alternativa por si no encontramos el controlador
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                        "No se encontró el controlador principal. Usando método alternativo.");

                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.MENUPRINCIPAL_FXML));
                loader.setControllerFactory(springContext::getBean);
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);

                // Obtener el controlador del menú y cargar la configuración
                MenuPrincipalControllerFX controller = loader.getController();
                controller.cargarModuloEnPanel(PathsFXML.CONFIGURACION_FXML);
                stage.show();
            }
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo regresar a la configuración: " + e.getMessage());
        }
    }
}