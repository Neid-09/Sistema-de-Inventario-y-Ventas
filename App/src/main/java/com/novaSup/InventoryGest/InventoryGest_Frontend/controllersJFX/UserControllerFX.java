package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Rol;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.stereotype.Controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Controller
public class UserControllerFX {

    @FXML
    private ComboBox<Rol> cmbRol; // Cambiado a ComboBox<Rol> para almacenar objetos Rol

    @FXML
    private TableColumn<Usuario, Integer> colId;

    @FXML
    private TableColumn<Usuario, String> colNombre;

    @FXML
    private TableColumn<Usuario, String> colCorreo;

    @FXML
    private TableColumn<Usuario, String> colTelefono;

    @FXML
    private TableColumn<Usuario, String> colRol;

    @FXML
    private TableView<Usuario> tablaUsuarios;

    @FXML
    private PasswordField txtContraseña;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtTelefono;

    @FXML
    public void initialize() {
        cargarRoles();  // Cargar roles en el ComboBox
        cargarUsuarios(); // Cargar usuarios en la tabla

        colId.setCellValueFactory(new PropertyValueFactory<>("idUsuario"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colTelefono.setCellValueFactory(new PropertyValueFactory<>("telefono"));
        colRol.setCellValueFactory(new PropertyValueFactory<>("rol") {

            @Override
            public SimpleStringProperty call(TableColumn.CellDataFeatures<Usuario, String> param) {
                return new SimpleStringProperty(param.getValue().getRol().getNombre());
            }

        });

        // Vincular el método seleccionarUsuario al evento de selección de la tabla
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                seleccionarUsuario();
            }
        });

    }

    @FXML
    void registrarUsuario(ActionEvent event) {
        try {
            // Validar que se hayan llenado todos los campos
            if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()
                    || txtTelefono.getText().isEmpty() || txtContraseña.getText().isEmpty()
                    || cmbRol.getSelectionModel().isEmpty()) {
                System.err.println("Por favor rellena todos los campos.");
                return;
            }

            // Crear objeto JSON con los datos ingresados
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String telefono = txtTelefono.getText();
            String contraseña = txtContraseña.getText();

            // Obtener el objeto Rol seleccionado para usar su idRol
            Rol rolSeleccionado = cmbRol.getSelectionModel().getSelectedItem();
            int idRol = rolSeleccionado.getIdRol();

            // Crear JSON para enviar al backend
            String usuarioJson = String.format(
                    "{\"nombre\": \"%s\", \"correo\": \"%s\", \"telefono\": \"%s\", \"contraseña\": \"%s\", \"idRol\": %d}",
                    nombre, correo, telefono, contraseña, idRol
            );

            // Conexión al backend
            String url = "http://localhost:8080/usuarios/registrar";
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Enviar datos
            connection.getOutputStream().write(usuarioJson.getBytes());

            // Procesar respuesta
            if (connection.getResponseCode() == 200) {
                System.out.println("¡Usuario registrado exitosamente!");
                limpiarCampos();
            } else {
                System.err.println("Error al registrar el usuario: " + connection.getResponseCode());
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }

        cargarUsuarios();

    }

    /**
     * Método para cargar roles desde la API del backend
     */
    private void cargarRoles() {
        try {
            // URL del endpoint del backend
            String url = "http://localhost:8080/roles";

            // Crear conexión al backend
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Leer la respuesta
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Deserializar la respuesta JSON en una lista de objetos Rol
                ObjectMapper mapper = new ObjectMapper();
                List<Rol> roles = mapper.readValue(response.toString(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Rol.class)); // Lista de Roles

                // Poblar el ComboBox con los objetos Rol
                cmbRol.setItems(FXCollections.observableArrayList(roles));
            } else {
                System.err.println("Error al obtener roles: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cargarUsuarios() {
        try {
            // URL del endpoint para listar usuarios
            String url = "http://localhost:8080/usuarios/listar";

            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept", "application/json");

            // Validar respuesta HTTP
            if (connection.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // Mapear respuesta JSON a una lista de Usuarios
                ObjectMapper mapper = new ObjectMapper();
                List<Usuario> usuarios = mapper.readValue(response.toString(),
                        mapper.getTypeFactory().constructCollectionType(List.class, Usuario.class));

                // Poblar la tabla con la lista de usuarios
                tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios));

            } else {
                System.err.println("Error al obtener los usuarios: " + connection.getResponseCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * Método para limpiar los campos después de un registro exitoso
     */
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
            Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (usuarioSeleccionado == null) {
                System.err.println("Por favor selecciona un usuario para actualizar.");
                return;
            }

            // Validar que se hayan llenado todos los campos
            if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()
                    || txtTelefono.getText().isEmpty() || txtContraseña.getText().isEmpty()
                    || cmbRol.getSelectionModel().isEmpty()) {
                System.err.println("Por favor rellena todos los campos.");
                return;
            }

            // Crear objeto JSON con los datos actualizados
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String telefono = txtTelefono.getText();
            String contraseña = txtContraseña.getText();
            Rol rolSeleccionado = cmbRol.getSelectionModel().getSelectedItem();
            int idRol = rolSeleccionado.getIdRol();

            String usuarioJson = String.format(
                    "{\"nombre\": \"%s\", \"correo\": \"%s\", \"telefono\": \"%s\", \"contraseña\": \"%s\", \"idRol\": %d}",
                    nombre, correo, telefono, contraseña, idRol
            );

            // Conexión al backend
            String url = "http://localhost:8080/usuarios/actualizar/" + usuarioSeleccionado.getIdUsuario();
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Enviar datos
            connection.getOutputStream().write(usuarioJson.getBytes());

            // Procesar respuesta
            if (connection.getResponseCode() == 200) {
                System.out.println("¡Usuario actualizado exitosamente!");
                limpiarCampos();
                cargarUsuarios(); // Recargar la lista de usuarios
            } else {
                System.err.println("Error al actualizar el usuario: " + connection.getResponseCode());
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void seleccionarUsuario() {
        Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado != null) {
            txtNombre.setText(usuarioSeleccionado.getNombre());
            txtCorreo.setText(usuarioSeleccionado.getCorreo());
            txtTelefono.setText(usuarioSeleccionado.getTelefono());
            txtContraseña.setText(usuarioSeleccionado.getContraseña());
            cmbRol.getSelectionModel().select(usuarioSeleccionado.getRol());
        }
    }

    @FXML
    void eliminarUsuario(ActionEvent event) {
        try {
            // Obtener el usuario seleccionado en la tabla
            Usuario usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
            if (usuarioSeleccionado == null) {
                System.err.println("Por favor selecciona un usuario para eliminar.");
                return;
            }

            // URL del endpoint para eliminar usuario
            String url = "http://localhost:8080/usuarios/eliminar/" + usuarioSeleccionado.getIdUsuario();

            // Crear conexión al backend
            HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
            connection.setRequestMethod("DELETE");

            // Validar respuesta HTTP
            if (connection.getResponseCode() == 204) {
                System.out.println("¡Usuario eliminado exitosamente!");
                cargarUsuarios(); // Recargar la lista de usuarios
            } else {
                System.err.println("Error al eliminar el usuario: " + connection.getResponseCode());
            }

            connection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}