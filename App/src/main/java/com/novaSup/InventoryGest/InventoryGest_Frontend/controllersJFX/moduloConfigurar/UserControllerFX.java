package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar;

import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.MenuPrincipalControllerFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
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
import javafx.stage.Stage;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import java.util.List;

import static com.novaSup.InventoryGest.MainApp.springContext;

@Component
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

    private UsuarioFX usuarioSeleccionado;

    @FXML
    public void initialize() {
        Platform.runLater(() -> {
            try {
                // Verificar información del usuario para depuración
                if (LoginServiceImplFX.getUsuarioActual() != null) {
                    String nombreRol = LoginServiceImplFX.getUsuarioActual().getRol() != null ?
                            LoginServiceImplFX.getUsuarioActual().getRol().getNombre() : "sin rol";
                    System.out.println("Usuario actual: " + LoginServiceImplFX.getUsuarioActual().getNombre() +
                            " - Rol: " + nombreRol);
                }

                // Verificar explícitamente el permiso
                boolean tienePermiso = LoginServiceImplFX.tienePermiso("gestionar_usuarios");
                System.out.println("¿Tiene permiso para gestionar_usuarios? " + tienePermiso);

                if (!tienePermiso) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Acceso Denegado",
                            "No tienes permisos para acceder a la gestión de usuarios.");
                    volverAConfiguracion();
                    return;
                }

                // Si tiene permiso, continuar con la inicialización normal
                configurarColumnas();
                configurarSeleccion();
                configurarBotonesPorPermisos();
                cargarRoles();
                cargarUsuarios();
            } catch (Exception e) {
                // Manejo de errores
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Error al inicializar la gestión de usuarios: " + e.getMessage());
                volverAConfiguracion();
            }
        });
    }

    // Método para configurar los botones según permisos
    private void configurarBotonesPorPermisos() {
        btnRegistrar.setVisible(LoginServiceImplFX.tienePermiso("crear_usuario"));
        btnRegistrar.setManaged(btnRegistrar.isVisible());

        btnActualizar.setVisible(LoginServiceImplFX.tienePermiso("editar_usuario"));
        btnActualizar.setManaged(btnActualizar.isVisible());

        btnEliminar.setVisible(LoginServiceImplFX.tienePermiso("eliminar_usuario"));
        btnEliminar.setManaged(btnEliminar.isVisible());
    }

    /**
     * Verifica si el usuario tiene alguno de los permisos necesarios para gestionar usuarios
     */
    /**
     * Verifica si el usuario tiene alguno de los permisos necesarios para gestionar usuarios
     */
    private boolean tienePermisosGestionUsuarios() {
        boolean tienePermiso = LoginServiceImplFX.tienePermiso("gestionar_usuarios");

        // Verificar permiso explícito para ver usuarios si el permiso general no está presente
        if (!tienePermiso) {
            tienePermiso = LoginServiceImplFX.tienePermiso("ver_usuarios");
        }

        return tienePermiso;
    }

    /**
     * Configura las columnas de la tabla de usuarios
     */
    private void configurarColumnas() {
        // Configuración para cada columna
        colId.setCellValueFactory(cellData -> cellData.getValue().idUsuarioProperty().asObject());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colCorreo.setCellValueFactory(cellData -> cellData.getValue().correoProperty());
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colRol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getRol() != null ? cellData.getValue().getRol().getNombre() : ""));
    }

    /**
     * Configura el manejo de la selección de filas en la tabla
     */
    private void configurarSeleccion() {
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            // Actualizar la UI según la selección
            usuarioSeleccionado = newSelection;
            boolean haySeleccion = (newSelection != null);

            // Activar/desactivar botones según permisos y selección
            btnActualizar.setDisable(!haySeleccion || !LoginServiceImplFX.tienePermiso("editar_usuario"));
            btnEliminar.setDisable(!haySeleccion || !LoginServiceImplFX.tienePermiso("eliminar_usuario"));

            // Cargar los datos del usuario seleccionado en el formulario
            if (haySeleccion) {
                seleccionarUsuario();
            }
        });
    }

    /**
     * Muestra mensaje de acceso denegado y redirige a configuración
     */
    private void mostrarMensajeAccesoDenegado() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Acceso Denegado");
        alert.setHeaderText(null);
        alert.setContentText("No tienes los permisos necesarios para acceder a la gestión de usuarios.");
        alert.showAndWait();

        volverAConfiguracion();
    }

    /**
     * Maneja errores mostrando un mensaje apropiado
     */
    private void manejarError(String mensaje, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje + ": " + e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
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
            String mensajeError = e.getMessage().toLowerCase();

            // Manejar específicamente error de permisos
            if (mensajeError.contains("403") || mensajeError.contains("permisos")) {
                Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.ERROR, "Acceso denegado",
                            "No tienes permisos para gestionar usuarios");
                    volverAConfiguracion();
                });
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "Error al cargar usuarios: " + e.getMessage());
            }
        }
    }

    // Método auxiliar para regresar a la pantalla de configuración
    private void regresarAConfiguracion() {
        try {
            // Ahora ejecutamos esto cuando la interfaz ya está completamente inicializada
            if (tablaUsuarios.getScene() != null && tablaUsuarios.getScene().getWindow() != null) {
                Stage stage = (Stage) tablaUsuarios.getScene().getWindow();
                MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();
                if (menuController != null) {
                    menuController.cargarModuloEnPanel(PathsFXML.CONFIGURACION_FXML);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
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


    /**
     * Vuelve a la pantalla de configuración de forma segura
     * @param event Evento que disparó la acción (opcional)
     */
    private void volverAConfiguracion(ActionEvent event) {
        try {
            Stage stage;

            // Obtener el Stage según el contexto disponible
            if (event != null) {
                stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            } else if (tablaUsuarios.getScene() != null) {
                stage = (Stage) tablaUsuarios.getScene().getWindow();
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error",
                        "No se pudo determinar la ventana actual.");
                return;
            }

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
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo regresar a la configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método público que recibe el evento
    @FXML
    void cerrarGestionUsuarios(ActionEvent event) {
        volverAConfiguracion(event);
    }

    // Sobrecarga para llamadas internas sin evento
    private void volverAConfiguracion() {
        volverAConfiguracion(null);
    }
}