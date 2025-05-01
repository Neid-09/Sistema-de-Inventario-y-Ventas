package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloInventario;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProveedorFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.ProveedorServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProveedorService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class ProveedorControllerFX implements Initializable {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorControllerFX.class);
    private final IProveedorService proveedorService = new ProveedorServiceImplFX();
    private ObservableList<ProveedorFX> listaProveedores = FXCollections.observableArrayList();
    private ProveedorFX proveedorSeleccionado;
    private boolean modoEdicion = false;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnCancelar;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnNuevoProveedor;

    @FXML
    private ComboBox<String> cbCriterioBusqueda;

    @FXML
    private TableColumn<ProveedorFX, Integer> colId;

    @FXML
    private TableColumn<ProveedorFX, String> colNombre;

    @FXML
    private TableColumn<ProveedorFX, String> colContacto;

    @FXML
    private TableColumn<ProveedorFX, String> colTelefono;

    @FXML
    private TableColumn<ProveedorFX, String> colCorreo;

    @FXML
    private TableColumn<ProveedorFX, String> colDireccion;

    @FXML
    private TableColumn<ProveedorFX, Integer> colNumProductos;

    @FXML
    private TableColumn<ProveedorFX, Void> colAcciones;

    @FXML
    private ProgressIndicator indicadorProgreso;

    @FXML
    private Label lblEstadoOperacion;

    @FXML
    private Label lblTotalProveedores;

    @FXML
    private TitledPane panelFormulario;

    @FXML
    private TableView<ProveedorFX> tablaProveedores;

    @FXML
    private TextField txtBuscar;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtContacto;

    @FXML
    private TextField txtTelefono;

    @FXML
    private TextField txtCorreo;

    @FXML
    private TextField txtDireccion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarControles();
        verificarPermisos();
        cargarProveedores();
    }

    private void configurarControles() {
        // Configurar criterios de búsqueda
        cbCriterioBusqueda.setItems(FXCollections.observableArrayList("Nombre", "Correo", "Todos"));
        cbCriterioBusqueda.getSelectionModel().selectLast();

        // Configurar columnas de la tabla
        colId.setCellValueFactory(cellData -> cellData.getValue().idProveedorProperty().asObject());
        colNombre.setCellValueFactory(cellData -> cellData.getValue().nombreProperty());
        colContacto.setCellValueFactory(cellData -> cellData.getValue().contactoProperty());
        colTelefono.setCellValueFactory(cellData -> cellData.getValue().telefonoProperty());
        colCorreo.setCellValueFactory(cellData -> cellData.getValue().correoProperty());
        colDireccion.setCellValueFactory(cellData -> cellData.getValue().direccionProperty());

        // Columna para mostrar la cantidad de productos asociados
        colNumProductos.setCellValueFactory(cellData ->
                new SimpleIntegerProperty(obtenerCantidadProductos(cellData.getValue().getIdProveedor())).asObject());

        // Configurar columna de acciones (Editar/Eliminar)
        configurarColumnaBotones();

        // Vincular tabla con lista observable
        tablaProveedores.setItems(listaProveedores);

        // Configurar listener de selección
        tablaProveedores.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            proveedorSeleccionado = newSelection;
        });

        // Configurar panel de formulario
        panelFormulario.setExpanded(false);
    }

    private void verificarPermisos() {
        boolean puedeEditar = PermisosUIUtil.tienePermiso("gestionar_proveedores");
        btnNuevoProveedor.setDisable(!puedeEditar);
        btnGuardar.setDisable(!puedeEditar);
    }

    private void configurarColumnaBotones() {
        Callback<TableColumn<ProveedorFX, Void>, TableCell<ProveedorFX, Void>> cellFactory = new Callback<>() {
            @Override
            public TableCell<ProveedorFX, Void> call(final TableColumn<ProveedorFX, Void> param) {
                return new TableCell<>() {
                    private final Button btnEditar = new Button("Editar");
                    private final Button btnEliminar = new Button("Eliminar");
                    private final Button btnVer = new Button("Ver");

                    {
                        btnEditar.setStyle("-fx-background-color: #2196F3; -fx-text-fill: white;");
                        btnEliminar.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
                        btnVer.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");

                        btnEditar.setOnAction((ActionEvent event) -> {
                            if (PermisosUIUtil.tienePermiso("gestionar_proveedores")) {
                                ProveedorFX proveedor = getTableView().getItems().get(getIndex());
                                editarProveedor(proveedor);
                            } else {
                                PermisosUIUtil.mostrarAlertaAccesoDenegado();
                            }
                        });

                        btnEliminar.setOnAction((ActionEvent event) -> {
                            if (PermisosUIUtil.tienePermiso("gestionar_proveedores")) {
                                ProveedorFX proveedor = getTableView().getItems().get(getIndex());
                                eliminarProveedor(proveedor);
                            } else {
                                PermisosUIUtil.mostrarAlertaAccesoDenegado();
                            }
                        });

                        btnVer.setOnAction((ActionEvent event) -> {
                            ProveedorFX proveedor = getTableView().getItems().get(getIndex());
                            mostrarDetallesProveedor(proveedor);
                        });
                    }

                    @Override
                    public void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            // Crear contenedor para los botones
                            ButtonBar buttonBar = new ButtonBar();
                            buttonBar.getButtons().addAll(btnVer, btnEditar, btnEliminar);
                            setGraphic(buttonBar);
                        }
                    }
                };
            }
        };

        colAcciones.setCellFactory(cellFactory);
    }

    @FXML
    void buscarProveedores(ActionEvent event) {
        String termino = txtBuscar.getText().trim();
        String criterio = cbCriterioBusqueda.getValue();

        mostrarProgreso(true);
        setEstadoOperacion("Buscando proveedores...");

        CompletableFuture.runAsync(() -> {
            try {
                List<ProveedorFX> proveedores;
                if (termino.isEmpty() || "Todos".equals(criterio)) {
                    proveedores = proveedorService.obtenerTodos();
                } else {
                    proveedores = proveedorService.buscarPorNombreOCorreo(termino);
                }

                Platform.runLater(() -> {
                    listaProveedores.clear();
                    listaProveedores.addAll(proveedores);
                    actualizarTotalProveedores();
                    setEstadoOperacion("Búsqueda completada");
                    mostrarProgreso(false);
                });
            } catch (Exception e) {
                logger.error("Error al buscar proveedores", e);
                Platform.runLater(() -> {
                    logger.error("Error al buscar proveedores", e);
                    Platform.runLater(() -> {
                        setEstadoOperacion("Error: " + e.getMessage());
                        mostrarAlerta("Error", "Error al buscar proveedores", e.getMessage(), Alert.AlertType.ERROR);
                        mostrarProgreso(false);
                    });
                });
            }
        });
    }

    @FXML
    void cancelarFormulario(ActionEvent event) {
        limpiarFormulario();
        panelFormulario.setExpanded(false);
    }

    @FXML
    void guardarProveedor(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }

        ProveedorFX proveedor = obtenerProveedorDesdeFormulario();
        mostrarProgreso(true);
        setEstadoOperacion("Guardando proveedor...");

        CompletableFuture.runAsync(() -> {
            try {
                ProveedorFX proveedorGuardado = proveedorService.guardar(proveedor);

                Platform.runLater(() -> {
                    ProveedorFX proveedorFX = new ProveedorFX(
                            proveedorGuardado.getIdProveedor(),
                            proveedorGuardado.getNombre(),
                            proveedorGuardado.getContacto(),
                            proveedorGuardado.getTelefono(),
                            proveedorGuardado.getCorreo(),
                            proveedorGuardado.getDireccion()
                    );

                    if (modoEdicion) {
                        // Actualizar proveedor existente en la lista
                        for (int i = 0; i < listaProveedores.size(); i++) {
                            if (listaProveedores.get(i).getIdProveedor().equals(proveedorFX.getIdProveedor())) {
                                listaProveedores.set(i, proveedorFX);
                                break;
                            }
                        }
                        setEstadoOperacion("Proveedor actualizado correctamente");
                    } else {
                        // Añadir nuevo proveedor a la lista
                        listaProveedores.add(proveedorFX);
                        setEstadoOperacion("Proveedor guardado correctamente");
                    }

                    limpiarFormulario();
                    panelFormulario.setExpanded(false);
                    actualizarTotalProveedores();
                    mostrarProgreso(false);
                });
            } catch (Exception e) {
                logger.error("Error al guardar proveedor", e);
                Platform.runLater(() -> {
                    setEstadoOperacion("Error: " + e.getMessage());
                    mostrarAlerta("Error", "Error al guardar proveedor", e.getMessage(), Alert.AlertType.ERROR);
                    mostrarProgreso(false);
                });
            }
        });
    }

    @FXML
    void mostrarFormularioNuevo(ActionEvent event) {
        if (!PermisosUIUtil.tienePermiso("gestionar_proveedores")) {
            PermisosUIUtil.mostrarAlertaAccesoDenegado();
            return;
        }

        modoEdicion = false;
        limpiarFormulario();
        panelFormulario.setExpanded(true);
        txtNombre.requestFocus();
    }

    private void cargarProveedores() {
        mostrarProgreso(true);
        setEstadoOperacion("Cargando proveedores...");

        CompletableFuture.runAsync(() -> {
            try {
                List<ProveedorFX> proveedores = proveedorService.obtenerTodos();

                Platform.runLater(() -> {
                    listaProveedores.clear();
                    listaProveedores.addAll(proveedores);
                    actualizarTotalProveedores();
                    setEstadoOperacion("Proveedores cargados");
                    mostrarProgreso(false);
                });
            } catch (Exception e) {

                logger.error("Error al cargar proveedores", e);
                Platform.runLater(() -> {
                    setEstadoOperacion("Error: " + e.getMessage());
                    mostrarAlerta("Error", "Error al cargar proveedores", e.getMessage(), Alert.AlertType.ERROR);
                    mostrarProgreso(false);
                });
            }
        });
    }

    private void editarProveedor(ProveedorFX proveedor) {
        modoEdicion = true;

        // Cargar datos del proveedor en el formulario
        txtId.setText(proveedor.getIdProveedor().toString());
        txtNombre.setText(proveedor.getNombre());
        txtContacto.setText(proveedor.getContacto());
        txtTelefono.setText(proveedor.getTelefono());
        txtCorreo.setText(proveedor.getCorreo());
        txtDireccion.setText(proveedor.getDireccion());

        // Expandir el panel y enfocar el nombre
        panelFormulario.setExpanded(true);
        txtNombre.requestFocus();
    }

    private void eliminarProveedor(ProveedorFX proveedor) {
        // Verificar si tiene productos asociados
        int cantidadProductos = obtenerCantidadProductos(proveedor.getIdProveedor());
        if (cantidadProductos > 0) {
            mostrarAlerta("No se puede eliminar",
                    "El proveedor tiene productos asociados",
                    "No se puede eliminar el proveedor porque tiene " + cantidadProductos + " productos asociados.",
                    Alert.AlertType.WARNING);
            return;
        }

        // Confirmar eliminación
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmar eliminación");
        alert.setHeaderText("¿Está seguro de eliminar este proveedor?");
        alert.setContentText("Esta acción no se puede deshacer.");

        if (alert.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            mostrarProgreso(true);
            setEstadoOperacion("Eliminando proveedor...");

            CompletableFuture.runAsync(() -> {
                try {
                    boolean eliminado = proveedorService.eliminar(proveedor.getIdProveedor());

                    Platform.runLater(() -> {
                        if (eliminado) {
                            listaProveedores.remove(proveedor);
                            actualizarTotalProveedores();
                            setEstadoOperacion("Proveedor eliminado correctamente");
                        } else {
                            setEstadoOperacion("No se pudo eliminar el proveedor");
                        }
                        mostrarProgreso(false);
                    });
                } catch (Exception e) {
                    logger.error("Error al eliminar proveedor", e);
                    Platform.runLater(() -> {
                        setEstadoOperacion("Error: " + e.getMessage());
                        mostrarAlerta("Error", "Error al eliminar proveedor", e.getMessage(), Alert.AlertType.ERROR);
                        mostrarProgreso(false);
                    });
                }
            });
        }
    }

    private void mostrarDetallesProveedor(ProveedorFX proveedor) {
        // Cargar datos del proveedor en el formulario en modo solo lectura
        txtId.setText(proveedor.getIdProveedor().toString());
        txtNombre.setText(proveedor.getNombre());
        txtContacto.setText(proveedor.getContacto());
        txtTelefono.setText(proveedor.getTelefono());
        txtCorreo.setText(proveedor.getCorreo());
        txtDireccion.setText(proveedor.getDireccion());

        // Deshabilitar campos
        setFormularioEditable(false);

        // Expandir el panel
        panelFormulario.setExpanded(true);

        // Cambiar texto del botón guardar
        btnGuardar.setText("Editar");
        btnGuardar.setOnAction(e -> {
            setFormularioEditable(true);
            btnGuardar.setText("Guardar");
            btnGuardar.setOnAction(this::guardarProveedor);
            modoEdicion = true;
        });
    }

    private void setFormularioEditable(boolean editable) {
        txtNombre.setEditable(editable);
        txtContacto.setEditable(editable);
        txtTelefono.setEditable(editable);
        txtCorreo.setEditable(editable);
        txtDireccion.setEditable(editable);
    }

    private boolean validarFormulario() {
        StringBuilder errores = new StringBuilder();

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("- El nombre es obligatorio\n");
        }

        if (txtCorreo.getText().trim().isEmpty()) {
            errores.append("- El correo es obligatorio\n");
        } else if (!validarFormatoCorreo(txtCorreo.getText().trim())) {
            errores.append("- El formato del correo no es válido\n");
        }

        if (errores.length() > 0) {
            mostrarAlerta("Datos incompletos", "Por favor corrija los siguientes errores:",
                    errores.toString(), Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    private boolean validarFormatoCorreo(String correo) {
        String regex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return correo.matches(regex);
    }

    private ProveedorFX obtenerProveedorDesdeFormulario() {
        Integer id = null;
        if (!txtId.getText().isEmpty()) {
            id = Integer.parseInt(txtId.getText());
        }

        return new ProveedorFX(
                id,
                txtNombre.getText(),
                txtContacto.getText(),
                txtTelefono.getText(),
                txtCorreo.getText(),
                txtDireccion.getText()
        );
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtContacto.clear();
        txtTelefono.clear();
        txtCorreo.clear();
        txtDireccion.clear();

        modoEdicion = false;
        setFormularioEditable(true);

        // Restablecer acción del botón guardar
        btnGuardar.setText("Guardar");
        btnGuardar.setOnAction(this::guardarProveedor);
    }

    private int obtenerCantidadProductos(int idProveedor) {
        try {
            return proveedorService.obtenerCantidadProductosAsociados(idProveedor);
        } catch (Exception e) {
            logger.error("Error al obtener cantidad de productos", e);
            return 0;
        }
    }

    private void actualizarTotalProveedores() {
        lblTotalProveedores.setText(String.valueOf(listaProveedores.size()));
    }

    private void mostrarProgreso(boolean mostrar) {
        indicadorProgreso.setVisible(mostrar);
    }

    private void setEstadoOperacion(String mensaje) {
        lblEstadoOperacion.setText(mensaje);
    }

    private void mostrarAlerta(String titulo, String header, String contenido, Alert.AlertType tipo) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(header);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}