package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ConfiguracionFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.ConfiguracionServiceFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class ConfigsControllerFX implements Initializable {

    @FXML
    private TextField buscarConfiguracion;

    @FXML
    private TableColumn<ConfiguracionFX, String> colAcciones;

    @FXML
    private TableColumn<ConfiguracionFX, String> colClave;

    @FXML
    private TableColumn<ConfiguracionFX, String> colDescripcion;

    @FXML
    private TableColumn<ConfiguracionFX, String> colValor;

    @FXML
    private VBox formulario;

    @FXML
    private TableView<ConfiguracionFX> tablaConfiguraciones;

    @FXML
    private TextField txtClave;

    @FXML
    private TextField txtDescripcion;

    @FXML
    private TextField txtValor;

    private final ConfiguracionServiceFX configuracionService;
    private ObservableList<ConfiguracionFX> listaConfiguraciones;
    private ConfiguracionFX configuracionActual;

    public ConfigsControllerFX() {
        this.configuracionService = new ConfiguracionServiceFX(); // Instanciación directa
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaConfiguraciones = FXCollections.observableArrayList();

        // Configurar columnas de la tabla
        colClave.setCellValueFactory(cellData -> cellData.getValue().claveProperty());
        colValor.setCellValueFactory(cellData -> cellData.getValue().valorProperty());
        colDescripcion.setCellValueFactory(cellData -> cellData.getValue().descripcionProperty());

        // Configurar columna de acciones
        colAcciones.setCellValueFactory(param -> new SimpleObjectProperty<>(""));
        colAcciones.setCellFactory(param -> new TableCell<ConfiguracionFX, String>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox hbox = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.setOnAction(event -> {
                    ConfiguracionFX config = getTableView().getItems().get(getIndex());
                    mostrarFormularioEditar(config);
                });

                btnEliminar.setOnAction(event -> {
                    ConfiguracionFX config = getTableView().getItems().get(getIndex());
                    eliminarConfiguracion(config);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(hbox);
                }
            }
        });

        // Establecer la lista de configuraciones en la tabla
        tablaConfiguraciones.setItems(listaConfiguraciones);

        // Verificar permisos
        verificarPermisos();

        // Cargar datos iniciales
        cargarConfiguraciones();
    }

    private void verificarPermisos() {
        boolean tienePermiso = PermisosUIUtil.tienePermiso("gestionar_configuracion");

        if (!tienePermiso) {
            // Mostrar mensaje y deshabilitar controles si no tiene permiso
            mostrarAlerta(Alert.AlertType.WARNING, "Acceso restringido",
                    "No tienes permisos para gestionar configuraciones.");
        }
    }

    private void cargarConfiguraciones() {
        try {
            listaConfiguraciones.clear();
            listaConfiguraciones.addAll(configuracionService.listarConfiguraciones());
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Error al cargar configuraciones: " + e.getMessage());
        }
    }

    @FXML
    void buscarConfiguracion(ActionEvent event) {
        String textoBusqueda = buscarConfiguracion.getText().trim();

        try {
            if (textoBusqueda.isEmpty()) {
                cargarConfiguraciones();
            } else {
                listaConfiguraciones.clear();
                listaConfiguraciones.addAll(configuracionService.buscarConfiguracionesPorClave(textoBusqueda));
            }
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Error al buscar configuraciones: " + e.getMessage());
        }
    }

    @FXML
    void guardarConfiguracion(ActionEvent event) {
        if (!validarFormulario()) {
            return;
        }

        try {
            // Si es una nueva configuración, crear un nuevo objeto con ID null
            if (configuracionActual == null) {
                configuracionActual = new ConfiguracionFX(null, txtClave.getText().trim(),
                        txtValor.getText().trim(), txtDescripcion.getText().trim());
            } else {
                // Solo actualizar los campos, manteniendo el ID original
                configuracionActual.setClave(txtClave.getText().trim());
                configuracionActual.setValor(txtValor.getText().trim());
                configuracionActual.setDescripcion(txtDescripcion.getText().trim());
            }

            // Imprimir información para depuración
            System.out.println("Guardando configuración con ID: " +
                    (configuracionActual.getIdConfig() == null ? "null (nueva)" : configuracionActual.getIdConfig()));

            ConfiguracionFX respuesta = configuracionService.guardarConfiguracion(configuracionActual);
            System.out.println("Configuración guardada con ID: " + respuesta.getIdConfig());

            ocultarFormulario(event);
            cargarConfiguraciones();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                    "Configuración guardada correctamente.");
        } catch (Exception e) {
            System.err.println("Error detallado: " + e);
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "Error al guardar configuración: " + e.getMessage());
        }
    }

    @FXML
    void mostrarFormulario(ActionEvent event) {
        limpiarFormulario();
        configuracionActual = null;
        formulario.setVisible(true);
    }

    private void mostrarFormularioEditar(ConfiguracionFX configuracion) {
        this.configuracionActual = configuracion;
        txtClave.setText(configuracion.getClave());
        txtValor.setText(configuracion.getValor());
        txtDescripcion.setText(configuracion.getDescripcion());
        formulario.setVisible(true);
    }

    @FXML
    void ocultarFormulario(ActionEvent event) {
        limpiarFormulario();
        formulario.setVisible(false);
    }

    private void limpiarFormulario() {
        txtClave.clear();
        txtValor.clear();
        txtDescripcion.clear();
    }

    private boolean validarFormulario() {
        String clave = txtClave.getText().trim();
        String valor = txtValor.getText().trim();

        if (clave.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "La clave es obligatoria.");
            return false;
        }

        if (valor.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "El valor es obligatorio.");
            return false;
        }

        return true;
    }

    private void eliminarConfiguracion(ConfiguracionFX configuracion) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de eliminar la configuración '" + configuracion.getClave() + "'?");

        confirmacion.showAndWait().ifPresent(respuesta -> {
            if (respuesta == ButtonType.OK) {
                try {
                    configuracionService.eliminarConfiguracion(configuracion.getIdConfig());
                    cargarConfiguraciones();
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Configuración eliminada correctamente.");
                } catch (Exception e) {
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "Error al eliminar configuración: " + e.getMessage());
                }
            }
        });
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}