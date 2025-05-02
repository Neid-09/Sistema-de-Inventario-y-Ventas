package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloInventario;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICategoriaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.CategoriaServiceImplFX;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class CategoriaControllerFX implements Initializable {

    private final ICategoriaService categoriaService;

    private ObservableList<CategoriaFX> listaCategorias = FXCollections.observableArrayList();
    private CategoriaFX categoriaSeleccionada;
    private boolean modoEdicion = false;

    @FXML private Button btnBuscar;
    @FXML private Button btnCancelar;
    @FXML private Button btnGuardar;
    @FXML private Button btnMostrarTodos;
    @FXML private Button btnNuevo;
    @FXML private CheckBox chkEstado;
    @FXML private TableColumn<CategoriaFX, String> colAcciones;
    @FXML private TableColumn<CategoriaFX, String> colDescripcion;
    @FXML private TableColumn<CategoriaFX, String> colDuracionGarantia;
    @FXML private TableColumn<CategoriaFX, String> colEstado;
    @FXML private TextField txtDuracionGarantia;
    @FXML private TableColumn<CategoriaFX, Integer> colId;
    @FXML private TableColumn<CategoriaFX, String> colNombre;
    @FXML private TableColumn<CategoriaFX, Integer> colProductos;
    @FXML private Label lblCantidadProductos;
    @FXML private Label lblEstado;
    @FXML private Label lblTotalCategorias;
    @FXML private VBox panelFormulario;
    @FXML private VBox panelProductosAsociados;
    @FXML private TableView<CategoriaFX> tablaCategorias;
    @FXML private TextField txtBuscar;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtId;
    @FXML private TextField txtNombre;

    public CategoriaControllerFX() {
        this.categoriaService = new CategoriaServiceImplFX(); // Instanciación directa
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarTabla();
        configurarFormulario();
        cargarCategorias();

        // Deshabilitar el botón guardar inicialmente
        btnGuardar.setDisable(true);

        // Configurar eventos de selección
        tablaCategorias.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        mostrarDetalleCategoria(newSelection);
                        // Deshabilitar el botón guardar cuando solo se selecciona
                        btnGuardar.setDisable(true);
                        modoEdicion = false;
                    }
                });
    }

    private void configurarTabla() {
        // Configurar las columnas
        colId.setCellValueFactory(new PropertyValueFactory<>("idCategoria"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colDuracionGarantia.setCellValueFactory(new PropertyValueFactory<>("duracionGarantia"));
        colEstado.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEstado() ? "Activo" : "Inactivo"));

        // La columna de productos asociados necesita una consulta especial
        colProductos.setCellValueFactory(cellData -> {
            try {
                int cantidad = categoriaService.obtenerCantidadProductos(cellData.getValue().getIdCategoria());
                return new javafx.beans.property.SimpleObjectProperty<>(cantidad);
            } catch (Exception e) {
                return new javafx.beans.property.SimpleObjectProperty<>(0);
            }
        });

        // Configurar columna de acciones con botones
        colAcciones.setCellFactory(col -> new TableCell<CategoriaFX, String>() {
            private final Button btnEditar = new Button("Editar");
            private final Button btnEliminar = new Button("Eliminar");
            private final HBox box = new HBox(5, btnEditar, btnEliminar);

            {
                btnEditar.getStyleClass().add("btn-secundario");
                btnEliminar.getStyleClass().add("btn-danger");

                btnEditar.setOnAction(event -> {
                    CategoriaFX categoria = getTableView().getItems().get(getIndex());
                    cargarCategoriaParaEditar(categoria);
                });

                btnEliminar.setOnAction(event -> {
                    CategoriaFX categoria = getTableView().getItems().get(getIndex());
                    confirmarEliminarCategoria(categoria);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : box);
            }
        });

        tablaCategorias.setItems(listaCategorias);
    }

    private void configurarFormulario() {
        limpiarFormulario();
    }

    @FXML
    void buscarCategorias(ActionEvent event) {
        try {
            String termino = txtBuscar.getText().trim();
            if (termino.isEmpty()) {
                cargarCategorias();
                return;
            }

            listaCategorias.clear();
            listaCategorias.addAll(categoriaService.buscarPorNombre(termino));
            actualizarContador();
            lblEstado.setText("Búsqueda completada: " + listaCategorias.size() + " resultados");
        } catch (Exception e) {
            mostrarError("Error al buscar categorías", e);
        }
    }

    @FXML
    void cancelarOperacion(ActionEvent event) {
        limpiarFormulario();
        modoEdicion = false;
        btnGuardar.setDisable(true); // Asegurar que el botón esté deshabilitado
        lblEstado.setText("Operación cancelada");
    }

    @FXML
    void guardarCategoria(ActionEvent event) {
        try {
            // Validaciones
            if (txtNombre.getText().trim().isEmpty()) {
                mostrarAlerta("El nombre de la categoría es obligatorio", Alert.AlertType.WARNING);
                return;
            }

            // Crear o actualizar objeto
            CategoriaFX categoria;
            if (modoEdicion && categoriaSeleccionada != null) {
                // Usar la categoría seleccionada existente
                categoria = categoriaSeleccionada;
            } else {
                // Crear una nueva instancia
                categoria = new CategoriaFX();
                categoria.setIdCategoria(0); // Asegurar que sea una nueva categoría
            }

            // Actualizar los campos con los valores del formulario
            categoria.setNombre(txtNombre.getText().trim());
            categoria.setDescripcion(txtDescripcion.getText().trim());
            categoria.setEstado(chkEstado.isSelected());

            // Asegurar que no haya problemas con la duración de garantía
            try {
                int duracionGarantia = txtDuracionGarantia.getText().isEmpty() ?
                        0 : Integer.parseInt(txtDuracionGarantia.getText().trim());
                categoria.setDuracionGarantia(duracionGarantia);
            } catch (NumberFormatException e) {
                categoria.setDuracionGarantia(0);
            }

            // Guardar en la BD
            CategoriaFX guardada = categoriaService.guardar(categoria);

            // Actualizar UI
            limpiarFormulario();
            cargarCategorias();

            String mensaje = modoEdicion ?
                    "Categoría actualizada correctamente" :
                    "Categoría creada correctamente";
            lblEstado.setText(mensaje);

            // Reiniciar estado
            modoEdicion = false;
            btnGuardar.setDisable(true);
            categoriaSeleccionada = null;

        } catch (Exception e) {
            mostrarError("Error al guardar la categoría", e);
        }
    }

    @FXML
    void mostrarTodas(ActionEvent event) {
        txtBuscar.clear();
        cargarCategorias();
    }

    @FXML
    void nuevaCategoria(ActionEvent event) {
        limpiarFormulario();
        modoEdicion = true; // Activamos modo edición para nueva categoría
        btnGuardar.setDisable(false); // Habilitamos el botón
        lblEstado.setText("Creando nueva categoría");
    }

    private void cargarCategorias() {
        try {
            listaCategorias.clear();
            listaCategorias.addAll(categoriaService.obtenerTodas());
            actualizarContador();
            lblEstado.setText("Categorías cargadas correctamente");
        } catch (Exception e) {
            mostrarError("Error al cargar las categorías", e);
        }
    }

    private void mostrarDetalleCategoria(CategoriaFX categoria) {
        try {
            Optional<CategoriaFX> optCategoria = categoriaService.obtenerPorId(categoria.getIdCategoria());
            if (optCategoria.isPresent()) {
                categoriaSeleccionada = optCategoria.get();
                txtId.setText(String.valueOf(categoriaSeleccionada.getIdCategoria()));
                txtNombre.setText(categoriaSeleccionada.getNombre());
                txtDescripcion.setText(categoriaSeleccionada.getDescripcion());
                chkEstado.setSelected(categoriaSeleccionada.getEstado());
                txtDuracionGarantia.setText(
                        categoriaSeleccionada.getDuracionGarantia() != null ?
                                categoriaSeleccionada.getDuracionGarantia().toString() : "0"
                );

                // Mostrar panel de productos asociados
                int cantidadProductos = categoriaService.obtenerCantidadProductos(categoriaSeleccionada.getIdCategoria());
                lblCantidadProductos.setText("Cantidad de productos: " + cantidadProductos);
                panelProductosAsociados.setVisible(true);
                panelProductosAsociados.setManaged(true);
            }
        } catch (Exception e) {
            mostrarError("Error al cargar detalles de categoría", e);
        }
    }

    private void cargarCategoriaParaEditar(CategoriaFX categoria) {
        mostrarDetalleCategoria(categoria);
        modoEdicion = true;
        // Añadir esta línea para habilitar el botón guardar
        btnGuardar.setDisable(false);
        lblEstado.setText("Editando categoría: " + categoria.getNombre());
    }

    private void confirmarEliminarCategoria(CategoriaFX categoria) {
        try {
            // Verificar si tiene productos asociados
            int cantidadProductos = categoriaService.obtenerCantidadProductos(categoria.getIdCategoria());
            if (cantidadProductos > 0) {
                mostrarAlerta(
                        "No se puede eliminar la categoría porque tiene " +
                                cantidadProductos + " productos asociados",
                        Alert.AlertType.WARNING);
                return;
            }

            // Confirmar eliminación
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar eliminación");
            confirmacion.setHeaderText("¿Está seguro de eliminar la categoría?");
            confirmacion.setContentText("Esta acción no se puede deshacer.");

            Optional<ButtonType> result = confirmacion.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                boolean eliminado = categoriaService.eliminar(categoria.getIdCategoria());
                if (eliminado) {
                    cargarCategorias();
                    limpiarFormulario();
                    lblEstado.setText("Categoría eliminada correctamente");
                }
            }
        } catch (Exception e) {
            mostrarError("Error al eliminar la categoría", e);
        }
    }

    private void limpiarFormulario() {
        txtId.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtDuracionGarantia.clear();
        chkEstado.setSelected(true);
        categoriaSeleccionada = null;
        // Si no estamos en modo edición, deshabilitamos el botón
        if (!modoEdicion) {
            btnGuardar.setDisable(true);
        }
        panelProductosAsociados.setVisible(false);
        panelProductosAsociados.setManaged(false);
    }

    private void actualizarContador() {
        lblTotalCategorias.setText("Total: " + listaCategorias.size());
    }

    private void mostrarError(String mensaje, Exception e) {
        lblEstado.setText(mensaje + ": " + e.getMessage());
        e.printStackTrace();

        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(mensaje);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }

    private void mostrarAlerta(String mensaje, Alert.AlertType tipo) {
        Alert alert = new Alert(tipo);
        alert.setTitle(tipo == Alert.AlertType.WARNING ? "Advertencia" : "Información");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}