package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IUsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.UsuarioServiceImplFX;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Controller;

import java.util.List;

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

    // Usamos la interfaz en lugar de la implementación directa
    private final IUsuarioService usuarioService = new UsuarioServiceImplFX();

    @FXML
    public void initialize() {
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
                System.err.println("Por favor rellena todos los campos.");
                return;
            }

            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String telefono = txtTelefono.getText();
            String contraseña = txtContraseña.getText();
            RolFX rolSeleccionado = cmbRol.getSelectionModel().getSelectedItem();

            UsuarioFX nuevoUsuario = new UsuarioFX(null, nombre, correo, telefono, contraseña, rolSeleccionado);
            usuarioService.registrarUsuario(nuevoUsuario);

            System.out.println("¡Usuario registrado exitosamente!");
            limpiarCampos();
            cargarUsuarios();
        } catch (Exception e) {
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

    private void limpiarCampos() {
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtContraseña.clear();
        cmbRol.getSelectionModel().clearSelection();
    }

    @FXML
    void actualizarUsuario(ActionEvent event) {
        try {
            UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (usuarioSeleccionado == null) {
                System.err.println("Por favor selecciona un usuario para actualizar.");
                return;
            }

            if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()
                    || txtTelefono.getText().isEmpty() || txtContraseña.getText().isEmpty()
                    || cmbRol.getSelectionModel().isEmpty()) {
                System.err.println("Por favor rellena todos los campos.");
                return;
            }

            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String telefono = txtTelefono.getText();
            String contraseña = txtContraseña.getText();
            RolFX rolSeleccionado = cmbRol.getSelectionModel().getSelectedItem();

            UsuarioFX usuarioActualizado = new UsuarioFX(usuarioSeleccionado.getIdUsuario(), nombre, correo, telefono, contraseña, rolSeleccionado);
            usuarioService.actualizarUsuario(usuarioSeleccionado.getIdUsuario(), usuarioActualizado);

            System.out.println("¡Usuario actualizado exitosamente!");
            limpiarCampos();
            cargarUsuarios();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void seleccionarUsuario() {
        UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            txtNombre.setText(usuarioSeleccionado.getNombre());
            txtCorreo.setText(usuarioSeleccionado.getCorreo());
            txtTelefono.setText(usuarioSeleccionado.getTelefono());
            txtContraseña.setText(usuarioSeleccionado.getContraseña());

            RolFX rolUsuario = usuarioSeleccionado.getRol();
            if (rolUsuario != null) {
                for (RolFX rol : cmbRol.getItems()) {
                    if (rol.getIdRol().equals(rolUsuario.getIdRol())) {
                        cmbRol.getSelectionModel().select(rol);
                        break;
                    }
                }
            }
        }
    }

    @FXML
    void eliminarUsuario(ActionEvent event) {
        try {
            UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (usuarioSeleccionado == null) {
                System.err.println("Por favor selecciona un usuario para eliminar.");
                return;
            }

            usuarioService.eliminarUsuario(usuarioSeleccionado.getIdUsuario());
            System.out.println("¡Usuario eliminado exitosamente!");
            cargarUsuarios();
            limpiarCampos();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}