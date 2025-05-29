package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar;

import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.MenuPrincipalControllerFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VendedorFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IRolService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IUsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVendedorService;
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
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class UserControllerFX {    // --- Componentes FXML ---
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
    // Eliminar la antigua columna de estado booleana
    // @FXML
    // private TableColumn<UsuarioFX, Boolean> colEstado;
    // Añadir la nueva columna de estado de texto
    @FXML
    private TableColumn<UsuarioFX, String> colEstadoTexto;
    @FXML
    private TableColumn<UsuarioFX, String> colVendedor;
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
    private Button btnCambiarEstado; // Botón unificado    // --- Componentes de la Sección Vendedor ---
    @FXML
    private StackPane seccionVendedor;
    @FXML
    private TextField txtObjetivoVentas;
    @FXML
    private DatePicker dpFechaContratacion;
    @FXML
    private Label lblEstadoVendedor;
    @FXML
    private Label lblUsuarioAsociado;
    @FXML
    private Label lblIdVendedor;
    @FXML
    private Button btnAsignarVendedor;
    @FXML
    private Button btnActualizarVendedor;
    @FXML
    private Button btnCambiarEstadoVendedor;    // --- Servicios ---
    private final IUsuarioService servicioUsuario;
    private final IRolService servicioRol;
    private final IVendedorService servicioVendedor;

    // --- Variables para gestión de vendedor ---
    private VendedorFX vendedorActual;

    // --- Constructor ---
    public UserControllerFX(IUsuarioService servicioUsuario, IRolService servicioRol, IVendedorService servicioVendedor) {
        this.servicioUsuario = servicioUsuario;
        this.servicioRol = servicioRol;
        this.servicioVendedor = servicioVendedor;
    }

    // --- Inicialización ---
    @FXML
    public void initialize() {
        // Ejecutar después de que la plataforma JavaFX esté lista
        Platform.runLater(() -> {
            try {
                // ... verificaciones de permisos existentes ...
                if (!LoginServiceImplFX.tienePermiso("gestionar_usuarios")) {
                    mostrarAlerta(Alert.AlertType.WARNING, "Acceso Denegado",
                            "No tienes permisos para acceder a la gestión de usuarios.");
                    volverAConfiguracion(null); // Intentar volver
                    return; // Detener inicialización si no hay permiso
                }                configurarColumnas();
                configurarBotonesPorPermisos(); // Configura visibilidad/gestión inicial
                cargarRoles();
                cargarUsuarios();
                configurarListenerSeleccion(); // Configurar listener después de cargar datos iniciales
                configurarListenerRol(); // Configurar listener para el ComboBox de rol
                limpiarCampos(); // Asegura estado inicial correcto de botones y campos

            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error de Inicialización",
                        "Error al inicializar la gestión de usuarios: " + e.getMessage());
                e.printStackTrace(); // Imprimir traza para depuración
                volverAConfiguracion(null); // Intentar volver en caso de error
            }
        });
    }

    // --- Configuración UI ---

    /**
     * Configura la visibilidad y gestión inicial de los botones según los permisos del usuario.
     */
    private void configurarBotonesPorPermisos() {
        btnRegistrar.setVisible(LoginServiceImplFX.tienePermiso("crear_usuario"));
        btnRegistrar.setManaged(btnRegistrar.isVisible()); // Ocultar espacio si no es visible

        btnActualizar.setVisible(LoginServiceImplFX.tienePermiso("editar_usuario"));
        btnActualizar.setManaged(btnActualizar.isVisible());

        // Configurar el nuevo botón unificado para cambiar estado
        // Visible si el usuario puede activar O desactivar
        boolean puedeGestionarEstado = LoginServiceImplFX.tienePermiso("activar_usuario") || LoginServiceImplFX.tienePermiso("eliminar_usuario");
        btnCambiarEstado.setVisible(puedeGestionarEstado);
        btnCambiarEstado.setManaged(puedeGestionarEstado);
        btnCambiarEstado.setDisable(true); // Inicialmente deshabilitado hasta seleccionar usuario
    }

    /**
     * Configura las columnas de la tabla de usuarios.
     */
    private void configurarColumnas() {
        colId.setCellValueFactory(cellData -> cellData.getValue().idUsuarioProperty().asObject());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colCorreo.setCellValueFactory(cellData -> cellData.getValue().correoProperty());
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colRol.setCellValueFactory(cellData -> new SimpleStringProperty(
                cellData.getValue().getRol() != null ? cellData.getValue().getRol().getNombre() : "N/A"));

        // Configurar la nueva columna Estado como Texto con Estilo
        colEstadoTexto.setCellValueFactory(cellData -> {
            boolean estado = cellData.getValue().isEstado();
            // Devuelve "Activo" o "Inactivo" como StringProperty
            return new SimpleStringProperty(estado ? "Activo" : "Inactivo");
        });        // Aplicar estilo a la celda basado en el texto ("Activo" o "Inactivo")
        colEstadoTexto.setCellFactory(column -> new TableCell<UsuarioFX, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item); // Establecer el texto ("Activo" o "Inactivo")

                // Limpiar estilos anteriores para evitar artefactos al reutilizar celdas
                getStyleClass().removeAll("estado-activo", "estado-inactivo");
                setTextFill(javafx.scene.paint.Color.BLACK); // Color por defecto

                if (item != null && !empty) {
                    if ("Activo".equals(item)) {
                        getStyleClass().add("estado-activo");
                        setTextFill(javafx.scene.paint.Color.GREEN);
                    } else { // "Inactivo"
                        getStyleClass().add("estado-inactivo");
                        setTextFill(javafx.scene.paint.Color.RED);
                    }
                } else {
                    setText(null); // Limpiar texto si la celda está vacía
                }
            }
        });

        // Configurar la columna de Vendedor
        colVendedor.setCellValueFactory(cellData -> {
            UsuarioFX usuario = cellData.getValue();
            // Verificar si el usuario es vendedor basándose en su rol
            if (usuario.getRol() != null && "Vendedor".equals(usuario.getRol().getNombre())) {
                return new SimpleStringProperty("Sí");
            } else {
                return new SimpleStringProperty("-");
            }
        });

        // Aplicar estilo a la columna de vendedor
        colVendedor.setCellFactory(column -> new TableCell<UsuarioFX, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setText(item);

                getStyleClass().removeAll("vendedor-si", "vendedor-no");
                setTextFill(javafx.scene.paint.Color.BLACK);

                if (item != null && !empty) {
                    if ("Sí".equals(item)) {
                        getStyleClass().add("vendedor-si");
                        setTextFill(javafx.scene.paint.Color.BLUE);
                        setStyle("-fx-font-weight: bold;");
                    } else {
                        getStyleClass().add("vendedor-no");
                        setTextFill(javafx.scene.paint.Color.GRAY);
                        setStyle("-fx-font-style: italic;");
                    }
                } else {
                    setText(null);
                    setStyle("");
                }
            }
        });
    }

    /**
     * Configura el listener para detectar cambios en la selección de la tabla.
     */
    private void configurarListenerSeleccion() {
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            actualizarEstadoInterfaz(newSelection); // Llamar al método centralizado
        });
    }

    /**
     * Actualiza el estado de los botones y campos del formulario basado en la selección actual.
     * Llamado por el listener de selección y por limpiarCampos.
     * @param usuarioSeleccionado El usuario actualmente seleccionado, o null si no hay selección.
     */
    private void actualizarEstadoInterfaz(UsuarioFX usuarioSeleccionado) {
        boolean haySeleccion = (usuarioSeleccionado != null);

        // --- Habilitar/Deshabilitar Botones ---
        // Registrar: Habilitado solo si tiene permiso Y no hay selección
        btnRegistrar.setDisable(haySeleccion || !LoginServiceImplFX.tienePermiso("crear_usuario"));

        // Actualizar: Habilitado solo si hay selección Y tiene permiso
        btnActualizar.setDisable(!haySeleccion || !LoginServiceImplFX.tienePermiso("editar_usuario"));

        // Cambiar Estado: Lógica más compleja
        boolean puedeActivar = false;
        boolean puedeDesactivar = false;

        if (haySeleccion && usuarioSeleccionado != null) {
            // Calcular si se puede activar o desactivar SOLO si hay selección (evita NullPointerException)
            puedeActivar = !usuarioSeleccionado.isEstado() && LoginServiceImplFX.tienePermiso("activar_usuario");
            puedeDesactivar = usuarioSeleccionado.isEstado() && LoginServiceImplFX.tienePermiso("eliminar_usuario"); // Usar permiso "eliminar" o "desactivar"
        }

        // Habilitar/Deshabilitar botón Cambiar Estado
        btnCambiarEstado.setDisable(!(puedeActivar || puedeDesactivar)); // Deshabilitado si no puede hacer ninguna acción

        // --- Actualizar Texto y Estilo del Botón Cambiar Estado ---
        if (puedeActivar) {
            btnCambiarEstado.setText("Activar");
            btnCambiarEstado.getStyleClass().removeAll("button-warning"); // Quitar estilo de desactivar
            btnCambiarEstado.getStyleClass().add("button-success"); // Añadir estilo de activar
        } else if (puedeDesactivar) {
            btnCambiarEstado.setText("Desactivar");
            btnCambiarEstado.getStyleClass().removeAll("button-success"); // Quitar estilo de activar
            btnCambiarEstado.getStyleClass().add("button-warning"); // Añadir estilo de desactivar
        } else {
            btnCambiarEstado.setText("Activar/Desactivar"); // Texto genérico
            btnCambiarEstado.getStyleClass().removeAll("button-success", "button-warning"); // Quitar estilos específicos
        }        // --- Actualizar Campos del Formulario ---
        if (haySeleccion && usuarioSeleccionado != null) {
            txtNombre.setText(usuarioSeleccionado.getNombre());
            txtCorreo.setText(usuarioSeleccionado.getCorreo());
            txtTelefono.setText(usuarioSeleccionado.getTelefono());
            txtContraseña.clear(); // Nunca mostrar contraseña existente
            txtContraseña.setPromptText("Dejar vacío para no cambiar"); // Indicar cómo mantener contraseña
            cmbRol.setValue(usuarioSeleccionado.getRol()); // Seleccionar rol actual
            
            // Cargar información de vendedor si corresponde
            cargarInformacionVendedor(usuarioSeleccionado);
        } else {
            // Si no hay selección, los campos se limpian en limpiarCampos()
            txtContraseña.setPromptText(""); // Restablecer texto de ayuda si se deselecciona
            limpiarSeccionVendedor();
        }

        // Actualizar visibilidad de la sección vendedor basada en el rol seleccionado
        actualizarVisibilidadSeccionVendedor();
    }

    // --- Carga de Datos ---

    /**
     * Carga los roles disponibles en el ComboBox.
     */
    private void cargarRoles() {
        try {
            List<RolFX> roles = servicioRol.obtenerRoles();
            cmbRol.setItems(FXCollections.observableArrayList(roles));
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error al Cargar Roles", "No se pudieron cargar los roles: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga o recarga la lista de usuarios en la tabla.
     */
    private void cargarUsuarios() {
        try {
            List<UsuarioFX> usuarios = servicioUsuario.obtenerUsuarios();
            tablaUsuarios.setItems(FXCollections.observableArrayList(usuarios));
            tablaUsuarios.refresh(); // Asegurar que la tabla se redibuje
        } catch (Exception e) {
            String mensajeError = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
            // Manejar específicamente error de permisos si se detecta
            if (mensajeError.contains("403") || mensajeError.contains("permisos") || mensajeError.contains("forbidden")) {
                Platform.runLater(() -> { // Asegurar ejecución en hilo de UI
                    mostrarAlerta(Alert.AlertType.ERROR, "Acceso Denegado",
                            "No tienes permisos suficientes para ver la lista de usuarios.");
                    // Opcional: Deshabilitar controles o cerrar vista si es crítico
                    // volverAConfiguracion(null);
                });
            } else {
                mostrarAlerta(Alert.AlertType.ERROR, "Error al Cargar Usuarios",
                        "No se pudieron cargar los usuarios: " + e.getMessage());
            }
            e.printStackTrace();
        }
    }

    // --- Acciones de Botones ---

    @FXML
    void registrarUsuario(ActionEvent evento) {
        // Validación de campos obligatorios (incluyendo contraseña para registro)
        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()
                || txtTelefono.getText().isEmpty() || txtContraseña.getText().isEmpty() // Contraseña requerida al registrar
                || cmbRol.getSelectionModel().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor rellena todos los campos para registrar un nuevo usuario.");
            return;
        }

        try {
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String telefono = txtTelefono.getText();
            String contraseña = txtContraseña.getText(); // Tomar contraseña del campo
            RolFX rolSeleccionado = cmbRol.getSelectionModel().getSelectedItem();

            // Crear nuevo usuario (activo por defecto)
            UsuarioFX nuevoUsuario = new UsuarioFX(null, nombre, correo, telefono, contraseña, rolSeleccionado, true);
            servicioUsuario.registrarUsuario(nuevoUsuario);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Registro Exitoso", "¡Usuario registrado correctamente!");
            limpiarCampos(); // Limpiar formulario
            cargarUsuarios(); // Recargar tabla
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Registro", "Error al registrar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void actualizarUsuario(ActionEvent evento) {
        UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Por favor selecciona un usuario de la tabla para actualizar.");
            return;
        }

        // Validación de campos obligatorios (contraseña NO es obligatoria para actualizar)
        if (txtNombre.getText().isEmpty() || txtCorreo.getText().isEmpty()
                || txtTelefono.getText().isEmpty() || cmbRol.getSelectionModel().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos Incompletos", "Por favor rellena los campos obligatorios (Nombre, Correo, Teléfono, Rol).");
            return;
        }

        try {
            String nombre = txtNombre.getText();
            String correo = txtCorreo.getText();
            String telefono = txtTelefono.getText();
            // Obtener contraseña solo si el campo no está vacío, sino enviar null
            String contraseñaNueva = txtContraseña.getText().isEmpty() ? null : txtContraseña.getText();
            RolFX rolSeleccionado = cmbRol.getSelectionModel().getSelectedItem();

            // Crear objeto para la actualización (manteniendo estado actual)
            UsuarioFX usuarioActualizado = new UsuarioFX(
                usuarioSeleccionado.getIdUsuario(),
                nombre, correo, telefono,
                contraseñaNueva, // Enviar null si no se quiere cambiar
                rolSeleccionado,
                usuarioSeleccionado.isEstado() // Mantener el estado (activo/inactivo)
            );

            servicioUsuario.actualizarUsuario(usuarioSeleccionado.getIdUsuario(), usuarioActualizado);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Actualización Exitosa", "¡Usuario actualizado correctamente!");
            limpiarCampos();
            cargarUsuarios();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Actualización", "Error al actualizar usuario: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método unificado para activar o desactivar el usuario seleccionado.
     * Se llama desde el botón btnCambiarEstado.
     */
    @FXML
    void cambiarEstadoUsuario(ActionEvent evento) {
        UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", "Por favor selecciona un usuario de la tabla.");
            return;
        }

        boolean deseaActivar = !usuarioSeleccionado.isEstado(); // Determinar la acción opuesta al estado actual
        String textoAccion = deseaActivar ? "activar" : "desactivar";
        String permisoRequerido = deseaActivar ? "activar_usuario" : "eliminar_usuario"; // Permiso necesario

        // Verificar permiso específico para la acción
        if (!LoginServiceImplFX.tienePermiso(permisoRequerido)) {
             mostrarAlerta(Alert.AlertType.WARNING, "Acceso Denegado", "No tienes permisos para " + textoAccion + " usuarios.");
             return;
        }

        // Confirmación del usuario
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar " + textoAccion);
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText("¿Deseas " + textoAccion + " al usuario '" + usuarioSeleccionado.getNombre() + "'?");

        // Procesar solo si el usuario confirma
        if (confirmacion.showAndWait().filter(respuesta -> respuesta == ButtonType.OK).isPresent()) {
            try {
                if (deseaActivar) {
                    servicioUsuario.activarUsuario(usuarioSeleccionado.getIdUsuario());
                } else {
                    servicioUsuario.desactivarUsuario(usuarioSeleccionado.getIdUsuario());
                }
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "¡Usuario " + (deseaActivar ? "activado" : "desactivado") + " correctamente!");
                cargarUsuarios(); // Recargar la tabla para reflejar el cambio de estado
                limpiarCampos(); // Limpiar formulario y restablecer botones
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al " + textoAccion + " usuario: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    @FXML
    void limpiarCampos() {
        // Limpiar todos los campos del formulario
        txtNombre.clear();
        txtCorreo.clear();
        txtTelefono.clear();
        txtContraseña.clear();
        txtContraseña.setPromptText(""); // Limpiar texto de ayuda de contraseña

        // --- Cambios para el ComboBox ---
        cmbRol.getSelectionModel().clearSelection(); // Deseleccionar ítem
        cmbRol.setValue(null); // Establecer valor a null para mostrar prompt text
        cmbRol.setPromptText("Seleccionar Rol"); // Opcional: Asegurar que el prompt text esté definido (aunque debería estarlo desde FXML)

        // Deseleccionar fila de la tabla
        tablaUsuarios.getSelectionModel().clearSelection();

        // Restablecer estado de botones llamando a la lógica centralizada sin selección
        actualizarEstadoInterfaz(null);
    }

    // --- Navegación ---

    /**
     * Intenta regresar a la vista principal de configuración.
     * @param evento El evento que disparó la acción (puede ser null si se llama internamente).
     */
    private void volverAConfiguracion(ActionEvent evento) {
        try {
            Stage stage = null;
            // Intentar obtener el Stage desde el evento si existe
            if (evento != null && evento.getSource() instanceof Node) {
                stage = (Stage) ((Node) evento.getSource()).getScene().getWindow();
            }
            // Si no, intentar desde la tabla (si está inicializada)
            else if (tablaUsuarios != null && tablaUsuarios.getScene() != null) {
                stage = (Stage) tablaUsuarios.getScene().getWindow();
            }
            // Como último recurso, intentar desde otro nodo FXML
            else {
                Node nodoReferencia = btnRegistrar; // Usar un botón como referencia
                if (nodoReferencia != null && nodoReferencia.getScene() != null) {
                    stage = (Stage) nodoReferencia.getScene().getWindow();
                }
            }

            if (stage == null) {
                 mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo determinar la ventana actual para regresar.");
                 return;
            }

            // Intentar obtener el controlador del menú principal desde userData
            Object userData = stage.getUserData();
            if (userData instanceof MenuPrincipalControllerFX) {
                MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) userData;
                menuController.cargarModuloEnPanel(PathsFXML.CONFIGURACION_FXML); // Cargar vista de configuración
            } else {
                // Si no se encuentra el controlador, mostrar advertencia y recargar menú (como fallback)
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "No se encontró el controlador principal. Recargando menú principal.");
                FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.MENUPRINCIPAL_FXML));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                stage.setScene(scene);
                // Opcional: Restaurar userData si es necesario
                // stage.setUserData(loader.getController());
                stage.show();
                // Podrías intentar navegar a configuración desde el nuevo menú si es necesario
                // MenuPrincipalControllerFX nuevoControlador = loader.getController();
                // nuevoControlador.cargarModuloEnPanel(PathsFXML.CONFIGURACION_FXML);
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo regresar a la configuración: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Manejador del evento del botón para cerrar la gestión de usuarios y volver.
     * @param evento El evento del botón.
     */
    @FXML
    void cerrarGestionUsuarios(ActionEvent evento) {
        volverAConfiguracion(evento);
    }

    // --- Utilidades ---

    /**
     * Muestra una alerta simple al usuario.
     * @param tipo El tipo de alerta (INFORMATION, WARNING, ERROR, etc.).
     * @param titulo El título de la ventana de alerta.
     * @param mensaje El mensaje a mostrar.
     */
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        // Asegurar que se ejecute en el hilo de la UI
        Platform.runLater(() -> {
            Alert alerta = new Alert(tipo);
            alerta.setTitle(titulo);
            alerta.setHeaderText(null); // No usar cabecera
            alerta.setContentText(mensaje);
            alerta.showAndWait();
        });
    }

    // --- Métodos de Gestión de Vendedor ---

    /**
     * Configura el listener para el ComboBox de rol para mostrar/ocultar la sección de vendedor
     */
    private void configurarListenerRol() {
        cmbRol.valueProperty().addListener((obs, oldValue, newValue) -> {
            actualizarVisibilidadSeccionVendedor();
        });
    }

    /**
     * Actualiza la visibilidad de la sección de vendedor basada en el rol seleccionado
     */
    private void actualizarVisibilidadSeccionVendedor() {
        RolFX rolSeleccionado = cmbRol.getValue();
        boolean mostrarSeccion = false;
        
        if (rolSeleccionado != null) {
            String nombreRol = rolSeleccionado.getNombre();
            mostrarSeccion = "Vendedor".equals(nombreRol) || "Administrador".equals(nombreRol);
        }
        
        seccionVendedor.setVisible(mostrarSeccion);
        seccionVendedor.setManaged(mostrarSeccion);
        
        // Configurar botones de vendedor
        configurarBotonesVendedor(mostrarSeccion);
        
        if (!mostrarSeccion) {
            limpiarSeccionVendedor();
        }
    }

    /**
     * Configura la visibilidad y estado de los botones específicos de vendedor
     */
    private void configurarBotonesVendedor(boolean mostrarSeccion) {
        if (!mostrarSeccion) {
            // Ocultar todos los botones si no se muestra la sección
            btnAsignarVendedor.setVisible(false);
            btnActualizarVendedor.setVisible(false);
            btnCambiarEstadoVendedor.setVisible(false);
            return;
        }

        UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        boolean haySeleccion = usuarioSeleccionado != null;
        boolean esNuevoUsuario = !haySeleccion;
        
        // Verificar si el usuario ya tiene información de vendedor
        boolean tieneVendedor = vendedorActual != null;
        
        // Configurar botón Asignar Vendedor
        btnAsignarVendedor.setVisible(esNuevoUsuario || (!tieneVendedor && haySeleccion));
        btnAsignarVendedor.setDisable(esNuevoUsuario); // Solo habilitado si hay usuario seleccionado sin vendedor
        
        // Configurar botón Actualizar Vendedor
        btnActualizarVendedor.setVisible(tieneVendedor && haySeleccion);
        btnActualizarVendedor.setDisable(!tieneVendedor);
        
        // Configurar botón Cambiar Estado Vendedor
        btnCambiarEstadoVendedor.setVisible(tieneVendedor && haySeleccion);
        btnCambiarEstadoVendedor.setDisable(!tieneVendedor);        if (tieneVendedor && vendedorActual != null) {
            // Usar el estado del usuario seleccionado en la tabla en lugar del UsuarioSimplifiedFX
            UsuarioFX usuarioTabla = tablaUsuarios.getSelectionModel().getSelectedItem();
            boolean vendedorActivo = usuarioTabla != null ? usuarioTabla.isEstado() : true;
            btnCambiarEstadoVendedor.setText(vendedorActivo ? "Desactivar Vendedor" : "Activar Vendedor");
        }
    }

    /**
     * Carga la información de vendedor para el usuario seleccionado
     */
    private void cargarInformacionVendedor(UsuarioFX usuario) {
        try {
            // Intentar obtener vendedor por ID de usuario
            VendedorFX vendedor = servicioVendedor.obtenerVendedorConUsuario(usuario.getIdUsuario());
            
            if (vendedor != null) {
                vendedorActual = vendedor;
                
                // Llenar campos de vendedor
                if (vendedor.getObjetivoVentas() != null) {
                    txtObjetivoVentas.setText(vendedor.getObjetivoVentas().toString());
                }
                
                if (vendedor.getFechaContratacion() != null) {
                    dpFechaContratacion.setValue(vendedor.getFechaContratacion());
                }
                
                // Actualizar labels informativos
                lblIdVendedor.setText("ID: " + vendedor.getIdVendedor());
                lblUsuarioAsociado.setText(usuario.getNombre() + " (" + usuario.getCorreo() + ")");
                  // Actualizar estado del vendedor (usar estado del usuario seleccionado en la tabla)
                boolean activo = usuario.isEstado(); // Usar el usuario completo de la tabla
                lblEstadoVendedor.setText(activo ? "Activo" : "Inactivo");
                lblEstadoVendedor.setStyle(activo ? 
                    "-fx-background-color: #28a745; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15;" :
                    "-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15;");
            } else {
                vendedorActual = null;
                limpiarCamposVendedor();
                lblIdVendedor.setText("Se generará automáticamente");
                lblUsuarioAsociado.setText(usuario.getNombre() + " (Sin información de vendedor)");
                lblEstadoVendedor.setText("Nuevo");
                lblEstadoVendedor.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15;");
            }
            
        } catch (Exception e) {
            vendedorActual = null;
            mostrarAlerta(Alert.AlertType.WARNING, "Información de Vendedor", 
                "No se pudo cargar la información de vendedor: " + e.getMessage());
            limpiarCamposVendedor();
        }
    }

    /**
     * Limpia todos los campos de la sección de vendedor
     */
    private void limpiarSeccionVendedor() {
        vendedorActual = null;
        limpiarCamposVendedor();
        lblIdVendedor.setText("Se generará automáticamente");
        lblUsuarioAsociado.setText("Se asignará automáticamente");
        lblEstadoVendedor.setText("Nuevo");
        lblEstadoVendedor.setStyle("-fx-background-color: #6c757d; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15;");
    }

    /**
     * Limpia solo los campos editables de vendedor
     */
    private void limpiarCamposVendedor() {
        txtObjetivoVentas.clear();
        dpFechaContratacion.setValue(null);
    }

    // --- Eventos de Botones de Vendedor ---

    @FXML
    void asignarVendedor(ActionEvent evento) {
        UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (usuarioSeleccionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Selección Requerida", 
                "Por favor selecciona un usuario para asignar como vendedor.");
            return;
        }

        // Validar campos de vendedor
        if (!validarCamposVendedor()) {
            return;
        }

        try {
            BigDecimal objetivoVentas = new BigDecimal(txtObjetivoVentas.getText());
            LocalDate fechaContratacion = dpFechaContratacion.getValue();

            // Crear nuevo vendedor
            VendedorFX nuevoVendedor = new VendedorFX();
            nuevoVendedor.setIdUsuario(usuarioSeleccionado.getIdUsuario());
            nuevoVendedor.setObjetivoVentas(objetivoVentas);
            nuevoVendedor.setFechaContratacion(fechaContratacion);

            VendedorFX vendedorCreado = servicioVendedor.crearVendedor(nuevoVendedor);
            vendedorActual = vendedorCreado;

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", 
                "¡Vendedor asignado correctamente!");
            
            // Recargar información
            cargarInformacionVendedor(usuarioSeleccionado);
            configurarBotonesVendedor(true);
            cargarUsuarios(); // Refrescar tabla para mostrar cambios

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", 
                "El objetivo de ventas debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                "Error al asignar vendedor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void actualizarVendedor(ActionEvent evento) {
        if (vendedorActual == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", 
                "No hay información de vendedor para actualizar.");
            return;
        }

        // Validar campos de vendedor
        if (!validarCamposVendedor()) {
            return;
        }

        try {
            BigDecimal objetivoVentas = new BigDecimal(txtObjetivoVentas.getText());
            LocalDate fechaContratacion = dpFechaContratacion.getValue();

            // Actualizar vendedor existente
            vendedorActual.setObjetivoVentas(objetivoVentas);
            vendedorActual.setFechaContratacion(fechaContratacion);

            servicioVendedor.actualizarVendedor(vendedorActual.getIdVendedor(), vendedorActual);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", 
                "¡Información de vendedor actualizada correctamente!");

        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Formato", 
                "El objetivo de ventas debe ser un número válido.");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                "Error al actualizar vendedor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    void cambiarEstadoVendedor(ActionEvent evento) {
        if (vendedorActual == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Error", 
                "No hay información de vendedor para cambiar estado.");
            return;
        }        try {
            // Usar el estado del usuario seleccionado en la tabla
            UsuarioFX usuarioTabla = tablaUsuarios.getSelectionModel().getSelectedItem();
            boolean estadoActual = usuarioTabla != null ? usuarioTabla.isEstado() : true;
            String accion = estadoActual ? "desactivar" : "activar";

            // Confirmar acción
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar " + accion);
            confirmacion.setHeaderText("¿Estás seguro?");
            confirmacion.setContentText("¿Deseas " + accion + " este vendedor?");

            if (confirmacion.showAndWait().filter(respuesta -> respuesta == ButtonType.OK).isPresent()) {
                if (estadoActual) {
                    servicioVendedor.desactivarVendedor(vendedorActual.getIdVendedor());
                } else {
                    servicioVendedor.activarVendedor(vendedorActual.getIdVendedor());
                }

                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", 
                    "¡Vendedor " + (estadoActual ? "desactivado" : "activado") + " correctamente!");

                // Recargar información
                UsuarioFX usuarioSeleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
                if (usuarioSeleccionado != null) {
                    cargarInformacionVendedor(usuarioSeleccionado);
                }
                cargarUsuarios(); // Refrescar tabla
            }

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", 
                "Error al cambiar estado del vendedor: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Valida los campos obligatorios de la sección vendedor
     */
    private boolean validarCamposVendedor() {
        if (txtObjetivoVentas.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", 
                "Por favor ingresa el objetivo de ventas.");
            return false;
        }

        if (dpFechaContratacion.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campo Requerido", 
                "Por favor selecciona la fecha de contratación.");
            return false;
        }

        try {
            BigDecimal objetivo = new BigDecimal(txtObjetivoVentas.getText());
            if (objetivo.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Valor Inválido", 
                    "El objetivo de ventas debe ser mayor a cero.");
                return false;
            }
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.WARNING, "Formato Inválido", 
                "El objetivo de ventas debe ser un número válido.");
            return false;
        }

        return true;
    }
}