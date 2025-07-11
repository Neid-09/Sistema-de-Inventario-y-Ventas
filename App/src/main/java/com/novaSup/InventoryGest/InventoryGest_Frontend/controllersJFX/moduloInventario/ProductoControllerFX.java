package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloInventario;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProveedorFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IInventarioService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ProductoControllerFX implements Initializable {

    // Componentes FXML - campos de formulario
    @FXML
    private TextField txtId;
    @FXML
    private TextField txtCodigo;
    @FXML
    private TextField txtNombre;
    @FXML
    private TextArea txtDescripcion;
    @FXML
    private TextField txtPrecioCosto;
    @FXML
    private TextField txtPrecioVenta;
    // @FXML private TextField txtStock;
    @FXML
    private TextField txtStockMinimo;
    @FXML
    private TextField txtStockMaximo;
    @FXML
    private ComboBox<CategoriaFX> cmbCategoria;
    @FXML
    private ComboBox<ProveedorFX> cmbProveedor;
    @FXML
    private CheckBox chkEstado;

    // Componentes FXNL - Botones
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnAumentarStock;
    @FXML
    private Button btnAjusteStock;
    @FXML
    private Button btnEliminar;
    @FXML
    private Button btnFiltrar;
    @FXML
    private Button btnGuardar;
    @FXML
    private Button btnLimpiar;
    @FXML
    private Button btnRefrescar;
    @FXML
    private Button btnVerDetalles;

    // Componentes FXML - filtros
    @FXML
    private TextField txtFiltroNombre;
    @FXML
    private TextField txtFiltroCodigo;
    @FXML
    private ComboBox<CategoriaFX> cmbFiltroCategoria;
    @FXML
    private ComboBox<String> cmbFiltroEstado;

    // Componentes FXML - tabla
    @FXML
    private TableView<ProductoFX> tablaProductos;
    @FXML
    private TableColumn<ProductoFX, String> colCodigo;
    @FXML
    private TableColumn<ProductoFX, String> colNombre;
    @FXML
    private TableColumn<ProductoFX, String> colCategoria;
    @FXML
    private TableColumn<ProductoFX, BigDecimal> colPrecioVenta;
    @FXML
    private TableColumn<ProductoFX, Integer> colStock;
    @FXML
    private TableColumn<ProductoFX, String> colEstado; // Componentes FXML - otros
    @FXML
    private Label lblMensaje;

    // Servicios - inyectados por constructor
    private final IProductoService productoService;
    private final ILoteService loteService;
    private final IInventarioService inventarioService;

    // Colecciones de datos
    private ObservableList<ProductoFX> listaProductos = FXCollections.observableArrayList();
    private ObservableList<CategoriaFX> listaCategorias = FXCollections.observableArrayList();
    private ObservableList<ProveedorFX> listaProveedores = FXCollections.observableArrayList();

    public ProductoControllerFX(IProductoService productoService, ILoteService loteService,
            IInventarioService inventarioService) {
        this.productoService = productoService;
        this.loteService = loteService;
        this.inventarioService = inventarioService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarPermisos();
        configurarTabla();
        configurarCombos();
        configurarEventos();

        // Inicializar estado de botones
        btnActualizar.setDisable(true);
        btnGuardar.setDisable(false);

        cargarDatos();
    }

    /**
     * Método para configurar la visibilidad de elementos según los permisos del
     * usuario
     * Se debe llamar en el método initialize()
     */
    private void configurarPermisos() {
        // Configurar permisos para operaciones CRUD básicas
        PermisosUIUtil.configurarBoton(btnGuardar, "crear_producto");
        PermisosUIUtil.configurarBoton(btnActualizar, "editar_producto");
        PermisosUIUtil.configurarBoton(btnEliminar, "eliminar_producto"); // Configurar permisos para operaciones de
                                                                          // stock
        PermisosUIUtil.configurarBoton(btnAumentarStock, "ajustar_stock");

        // Configurar permisos para otras acciones
        PermisosUIUtil.configurarBoton(btnVerDetalles, "ver_productos");
    }

    private void configurarTabla() {
        colCodigo.setCellValueFactory(new PropertyValueFactory<>("codigo"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCategoria.setCellValueFactory(cellData -> {
            String categoria = cellData.getValue().getCategoria();
            return new SimpleStringProperty(categoria != null ? categoria : "Sin categoría");
        });
        colPrecioVenta.setCellValueFactory(new PropertyValueFactory<>("precioVenta"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
        colEstado.setCellValueFactory(cellData -> {
            Boolean estado = cellData.getValue().getEstado();
            return new SimpleStringProperty(estado != null && estado ? "Activo" : "Inactivo");
        });

        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                cargarProductoEnFormulario(newSelection);
            }
        });
    }

    private void configurarCombos() {
        // Configurar filtro de estado
        cmbFiltroEstado.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        cmbFiltroEstado.getSelectionModel().selectFirst();

        // Configurar combo de categoría en formulario y en filtro
        cmbCategoria.setConverter(new StringConverter<CategoriaFX>() {
            @Override
            public String toString(CategoriaFX categoria) {
                return categoria != null ? categoria.getNombre() : "Sin categoría";
            }

            @Override
            public CategoriaFX fromString(String string) {
                return null; // No necesario para este caso
            }
        });

        cmbFiltroCategoria.setConverter(new StringConverter<CategoriaFX>() {
            @Override
            public String toString(CategoriaFX categoria) {
                return categoria != null ? categoria.getNombre() : "Todas las categorías";
            }

            @Override
            public CategoriaFX fromString(String string) {
                return null; // No necesario para este caso
            }
        });

        // Configurar combo de proveedor
        cmbProveedor.setConverter(new StringConverter<ProveedorFX>() {
            @Override
            public String toString(ProveedorFX proveedor) {
                return proveedor != null ? proveedor.getNombre() : "Sin proveedor";
            }

            @Override
            public ProveedorFX fromString(String string) {
                return null; // No necesario para este caso
            }
        });
    }

    @FXML
    private void configurarEventos() {
        // Validadores numéricos para campos de precio y stock
        txtPrecioCosto.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*(\\.\\d*)?")) {
                txtPrecioCosto.setText(oldText);
            }
        });

        txtPrecioVenta.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*(\\.\\d*)?")) {
                txtPrecioVenta.setText(oldText);
            }
        });

        txtStockMinimo.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                txtStockMinimo.setText(oldText);
            }
        });
        txtStockMaximo.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                txtStockMaximo.setText(oldText);
            }
        });
    }

    @FXML
    public void cargarDatos() {

        lblMensaje.setText("Cargando datos...");

        // Ejecutar en segundo plano para no bloquear la UI
        new Thread(() -> {
            try {
                // Cargar productos
                List<ProductoFX> productos = productoService.obtenerTodos();
                listaProductos.clear();
                listaProductos.addAll(productos);

                // Cargar categorías
                List<CategoriaFX> categorias = productoService.obtenerCategorias();

                // Añadir opción "Sin categoría"
                CategoriaFX sinCategoria = new CategoriaFX(null, "Sin categoría", "Productos sin categoría asignada",
                        true, 0);

                // Cargar proveedores
                List<ProveedorFX> proveedores = productoService.obtenerProveedores();

                // Añadir opción "Sin proveedor"
                ProveedorFX sinProveedor = new ProveedorFX(null, "Sin proveedor", null, null, null, null);

                // Actualizar UI en el hilo de JavaFX
                Platform.runLater(() -> {
                    tablaProductos.setItems(listaProductos);

                    // Actualizar combos de categoría
                    listaCategorias.clear();
                    listaCategorias.add(sinCategoria);
                    listaCategorias.addAll(categorias);

                    cmbCategoria.setItems(listaCategorias);
                    cmbCategoria.getSelectionModel().selectFirst();

                    // También para el filtro pero con una opción para "Todas"
                    ObservableList<CategoriaFX> categoriasConTodas = FXCollections.observableArrayList();
                    CategoriaFX todasCategorias = new CategoriaFX(null, "Todas las categorías", "", true, null);
                    categoriasConTodas.add(todasCategorias);
                    categoriasConTodas.addAll(categorias);

                    cmbFiltroCategoria.setItems(categoriasConTodas);
                    cmbFiltroCategoria.getSelectionModel().selectFirst();

                    // Actualizar combo de proveedor
                    listaProveedores.clear();
                    listaProveedores.add(sinProveedor);
                    listaProveedores.addAll(proveedores);

                    cmbProveedor.setItems(listaProveedores);
                    cmbProveedor.getSelectionModel().selectFirst();

                    lblMensaje.setText("");
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblMensaje.setText("Error al cargar datos: " + e.getMessage());
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudieron cargar los datos1", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void buscarProductos() {
        lblMensaje.setText("Buscando productos...");

        // Obtener valores de los filtros
        String nombre = txtFiltroNombre.getText() != null ? txtFiltroNombre.getText().trim() : "";
        String codigo = txtFiltroCodigo.getText() != null ? txtFiltroCodigo.getText().trim() : "";

        // Obtener categoría seleccionada - considerar nulo si es la opción "Todas las
        // categorías"
        CategoriaFX categoriaSeleccionada = cmbFiltroCategoria.getSelectionModel().getSelectedItem();
        Integer idCategoria = null;
        if (categoriaSeleccionada != null && categoriaSeleccionada.getIdCategoria() != null
                && categoriaSeleccionada.getIdCategoria() > 0) {
            idCategoria = categoriaSeleccionada.getIdCategoria();
        }

        // Obtener estado seleccionado
        String estadoSeleccionado = cmbFiltroEstado.getSelectionModel().getSelectedItem();
        Boolean estado = null;
        if ("Activos".equals(estadoSeleccionado)) {
            estado = true;
        } else if ("Inactivos".equals(estadoSeleccionado)) {
            estado = false;
        }

        // Crear copias finales para usar en el lambda
        final String nombreFinal = nombre;
        final String codigoFinal = codigo;
        final Integer idCategoriaFinal = idCategoria;
        final Boolean estadoFinal = estado;

        // Logear los valores para depuración
        System.out.println("Filtros: nombre=[" + nombreFinal + "], codigo=[" + codigoFinal +
                "], idCategoria=[" + idCategoriaFinal + "], estado=[" + estadoFinal + "]");

        // Deshabilitar botón durante búsqueda
        btnFiltrar.setDisable(true);

        // Ejecutar en segundo plano
        new Thread(() -> {
            try {
                List<ProductoFX> productos = productoService.filtrarProductos(
                        nombreFinal, codigoFinal, idCategoriaFinal, estadoFinal);

                Platform.runLater(() -> {
                    listaProductos.clear();
                    if (productos != null && !productos.isEmpty()) {
                        listaProductos.addAll(productos);
                        lblMensaje.setText("Se encontraron " + productos.size() + " productos");
                    } else {
                        lblMensaje.setText("No se encontraron productos");
                    }
                    btnFiltrar.setDisable(false);
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblMensaje.setText("Error al buscar: " + e.getMessage());
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo realizar la búsqueda", e.getMessage());
                    btnFiltrar.setDisable(false);
                });
            }
        }).start();
    }

    @FXML
    public void limpiarCampos() {
        txtId.clear();
        txtCodigo.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecioCosto.clear();
        txtPrecioVenta.clear();
        txtStockMinimo.clear();
        txtStockMaximo.clear();
        chkEstado.setSelected(true);
        cmbCategoria.getSelectionModel().selectFirst(); // "Sin categoría"
        cmbProveedor.getSelectionModel().selectFirst(); // "Sin proveedor"
        tablaProductos.getSelectionModel().clearSelection();
        lblMensaje.setText("");

        // Configurar botones para modo "nuevo producto"
        btnGuardar.setDisable(false);
        btnActualizar.setDisable(true);
    }

    private void cargarProductoEnFormulario(ProductoFX producto) {
        txtId.setText(producto.getIdProducto() != null ? producto.getIdProducto().toString() : "");
        txtCodigo.setText(producto.getCodigo());
        txtNombre.setText(producto.getNombre());
        txtDescripcion.setText(producto.getDescripcion());
        txtPrecioCosto.setText(producto.getPrecioCosto() != null ? producto.getPrecioCosto().toString() : "");
        txtPrecioVenta.setText(producto.getPrecioVenta() != null ? producto.getPrecioVenta().toString() : "");
        txtStockMinimo.setText(producto.getStockMinimo() != null ? producto.getStockMinimo().toString() : "");
        txtStockMaximo.setText(producto.getStockMaximo() != null ? producto.getStockMaximo().toString() : "");
        chkEstado.setSelected(producto.getEstado() != null && producto.getEstado());

        // Configurar botones para modo "edición de producto"
        btnGuardar.setDisable(true);
        btnActualizar.setDisable(false);

        // Seleccionar categoría correcta
        Integer idCategoria = producto.getIdCategoria();
        if (idCategoria != null) {
            for (CategoriaFX categoria : cmbCategoria.getItems()) {
                if (categoria.getIdCategoria() != null && categoria.getIdCategoria().equals(idCategoria)) {
                    cmbCategoria.getSelectionModel().select(categoria);
                    break;
                }
            }
        } else {
            cmbCategoria.getSelectionModel().selectFirst(); // Sin categoría
        }

        // Seleccionar proveedor correcto
        Integer idProveedor = producto.getIdProveedor();
        if (idProveedor != null) {
            for (ProveedorFX proveedor : cmbProveedor.getItems()) {
                if (proveedor.getIdProveedor() != null && proveedor.getIdProveedor().equals(idProveedor)) {
                    cmbProveedor.getSelectionModel().select(proveedor);
                    break;
                }
            }
        } else {
            cmbProveedor.getSelectionModel().selectFirst(); // Sin proveedor
        }
    }

    @FXML
    private void guardarProducto() {
        try {
            // Validar campos obligatorios
            if (!validarCampos()) {
                return;
            }

            // Crear un nuevo objeto ProductoFX con los datos del formulario
            ProductoFX producto = new ProductoFX();
            producto.setCodigo(txtCodigo.getText());
            producto.setNombre(txtNombre.getText());
            producto.setDescripcion(txtDescripcion.getText());

            // Establecer precios
            producto.setPrecioCosto(new BigDecimal(txtPrecioCosto.getText()));
            producto.setPrecioVenta(new BigDecimal(txtPrecioVenta.getText()));

            // Establecer stock explícitamente como 0 para productos nuevos
            producto.setStock(0);

            // Otros campos
            producto.setStockMinimo(
                    txtStockMinimo.getText().isEmpty() ? 0 : Integer.parseInt(txtStockMinimo.getText()));
            producto.setStockMaximo(
                    txtStockMaximo.getText().isEmpty() ? 0 : Integer.parseInt(txtStockMaximo.getText()));

            // Categoría y proveedor
            if (cmbCategoria.getValue() != null) {
                producto.setIdCategoria(cmbCategoria.getValue().getIdCategoria());
            }

            if (cmbProveedor.getValue() != null) {
                producto.setIdProveedor(cmbProveedor.getValue().getIdProveedor());
            }

            // Estado
            producto.setEstado(chkEstado.isSelected());

            // Guardar
            ProductoFX productoGuardado = productoService.guardar(producto);

            // Mostrar mensaje de éxito
            lblMensaje.setText("Producto guardado correctamente");

            // Verificar si el producto tiene un proveedor asignado
            if (productoGuardado.getIdProveedor() == null || producto.getIdProveedor() == 0) {
                // Mostrar alerta informando que no se puede crear un lote sin proveedor
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "No se puede crear lote",
                        "No se puede crear un lote para este producto porque no tiene un proveedor asignado. " +
                                "Por favor, asigne un proveedor al producto antes de crear un lote.");

                // Limpiar campos y actualizar tabla
                limpiarCampos();
                cargarDatos();
            } else {
                // Si hay proveedor, preguntar si desea crear un lote
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                alert.setTitle("Crear lote");
                alert.setHeaderText("¿Deseas crear un lote para este producto?");
                alert.setContentText("Esto te permitirá agregar stock inicial.");

                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.OK) {
                    abrirVentanaCrearLote(productoGuardado);
                } else {
                    // Si no desea crear lote, solo limpiar campos y actualizar tabla
                    limpiarCampos();
                    cargarDatos();
                }
            }

        } catch (Exception e) {
            lblMensaje.setText("Error al guardar el producto: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ha ocurrido un error",
                    "Error al guardar el producto: " + e.getMessage());
        }
    }

    private void abrirVentanaCrearLote(ProductoFX producto) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.VENTANA_CREATELOTE));

            // Crear el controlador manualmente e inyectar dependencias
            DialogAddLoteCtrlFX controller = new DialogAddLoteCtrlFX(this.loteService, this.productoService);

            // Establecer el controlador en el FXMLLoader ANTES de cargar
            loader.setController(controller);

            // Cargar el FXML. Ahora FXMLLoader usará la instancia del controlador
            // proporcionada.
            Parent root = loader.load();

            // Inicializar el controlador con el producto DESPUÉS de cargar el FXML
            // (para que los componentes @FXML ya estén inyectados en el controlador)
            controller.inicializar(producto);

            // Crear y configurar la escena
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Crear lote - " + producto.getNombre());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            // Manejar el cierre de la ventana
            stage.setOnHidden(event -> {
                // Refrescar datos después de cerrar la ventana
                limpiarCampos();
                cargarDatos();
            });

            // Mostrar la ventana
            stage.showAndWait();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir ventana",
                    "No se pudo abrir la ventana para crear lote: " + e.getMessage());
            // Imprimir stack trace para más detalles en la consola
            e.printStackTrace();
        } catch (Exception e) { // Capturar otras posibles excepciones
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error inesperado",
                    "Ocurrió un error inesperado al abrir la ventana: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void actualizarProducto() {
        try {
            // Validar que haya un producto seleccionado
            if (txtId.getText().isEmpty()) {
                lblMensaje.setText("Debe seleccionar un producto para actualizar");
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Selección requerida",
                        "Debe seleccionar un producto para actualizar");
                return;
            }

            // Validar campos obligatorios
            if (!validarCampos()) {
                return;
            }

            // Crear objeto con los datos actualizados
            ProductoFX producto = new ProductoFX();
            producto.setIdProducto(Integer.parseInt(txtId.getText()));
            producto.setCodigo(txtCodigo.getText());
            producto.setNombre(txtNombre.getText());
            producto.setDescripcion(txtDescripcion.getText());

            // Establecer precios
            producto.setPrecioCosto(new BigDecimal(txtPrecioCosto.getText()));
            producto.setPrecioVenta(new BigDecimal(txtPrecioVenta.getText()));

            // Al actualizar, respetamos el stock actual del producto
            ProductoFX productoActual = productoService.obtenerPorId(Integer.parseInt(txtId.getText()));
            producto.setStock(productoActual.getStock());

            // Otros campos
            producto.setStockMinimo(
                    txtStockMinimo.getText().isEmpty() ? 0 : Integer.parseInt(txtStockMinimo.getText()));
            producto.setStockMaximo(
                    txtStockMaximo.getText().isEmpty() ? 0 : Integer.parseInt(txtStockMaximo.getText()));

            // Categoría y proveedor
            if (cmbCategoria.getValue() != null) {
                producto.setIdCategoria(cmbCategoria.getValue().getIdCategoria());
            }

            if (cmbProveedor.getValue() != null) {
                producto.setIdProveedor(cmbProveedor.getValue().getIdProveedor());
            }

            // Estado
            producto.setEstado(chkEstado.isSelected());

            // Actualizar
            productoService.actualizar(producto.getIdProducto(), producto);

            // Mostrar mensaje de éxito y actualizar la vista
            lblMensaje.setText("Producto actualizado correctamente");
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Operación completada",
                    "Producto actualizado correctamente");
            cargarDatos();

        } catch (Exception e) {
            lblMensaje.setText("Error al actualizar el producto: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ha ocurrido un error",
                    "Error al actualizar el producto: " + e.getMessage());
        }
    }

    @FXML
    public void eliminarProducto() {

        // Verificar permisos antes de proceder
        if (!PermisosUIUtil.verificarPermisoConAlerta("eliminar_producto")) {
            return;
        }

        if (txtId.getText().isEmpty()) {
            lblMensaje.setText("Debe seleccionar un producto para eliminar.");
            return;
        }

        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION);
        alerta.setTitle("Confirmar eliminación");
        alerta.setHeaderText("¿Está seguro de eliminar este producto?");
        alerta.setContentText("Esta acción no se puede deshacer.");

        Optional<ButtonType> resultado = alerta.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            Integer id = Integer.parseInt(txtId.getText());

            // Ejecutar en segundo plano
            new Thread(() -> {
                try {
                    productoService.eliminar(id);

                    Platform.runLater(() -> {
                        lblMensaje.setText("Producto eliminado correctamente.");
                        cargarDatos();
                        limpiarCampos();
                    });
                } catch (Exception e) {
                    Platform.runLater(() -> {
                        lblMensaje.setText("Error al eliminar: " + e.getMessage());
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el producto",
                                e.getMessage());
                    });
                }
            }).start();
        }
    }

    @FXML
    void abrirVentanaAumentarStock() {
        // Verificar permisos antes de proceder
        if (!PermisosUIUtil.verificarPermisoConAlerta("ajustar_stock")) {
            return;
        }

        // Verificar que haya un producto seleccionado
        if (txtId.getText().isEmpty()) {
            lblMensaje.setText("Debe seleccionar un producto para aumentar stock");
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selección requerida",
                    "Debe seleccionar un producto para aumentar el stock");
            return;
        }

        // Obtener el producto seleccionado
        Integer idProducto = Integer.parseInt(txtId.getText());
        ProductoFX productoSeleccionado;

        try {
            productoSeleccionado = productoService.obtenerPorId(idProducto);

            // Verificar si el producto tiene un proveedor asignado
            if (productoSeleccionado.getIdProveedor() == null) {
                // Mostrar alerta informando que no se puede crear un lote sin proveedor
                mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "No se puede crear lote",
                        "No se puede aumentar el stock porque este producto no tiene un proveedor asignado. " +
                                "Por favor, asigne un proveedor al producto antes de aumentar el stock.");
                return;
            }

            // Abrir la ventana para crear un nuevo lote
            abrirVentanaCrearLote(productoSeleccionado);

        } catch (Exception e) {
            lblMensaje.setText("Error al cargar el producto: " + e.getMessage());
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ha ocurrido un error",
                    "No se pudo cargar el producto para aumentar el stock: " + e.getMessage());
        }
    }

    @FXML
    public void ajustarStock() {
        // Verificar permisos
        if (!PermisosUIUtil.verificarPermisoConAlerta("ajustar_stock")) {
            return;
        }

        // Verificar que haya un producto seleccionado
        if (txtId.getText().isEmpty()) {
            lblMensaje.setText("Debe seleccionar un producto para ajustar stock");
            mostrarAlerta(Alert.AlertType.WARNING, "Aviso", "Selección requerida",
                    "Debe seleccionar un producto para ajustar el stock");
            return;
        }

        // Mostrar confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar operación");
        confirmacion.setHeaderText("¿Está seguro de ajustar el stock?");
        confirmacion.setContentText("Esta acción modificará el stock del producto seleccionado.");

        Optional<ButtonType> resultado = confirmacion.showAndWait();
        if (resultado.isPresent() && resultado.get() == ButtonType.OK) {
            try {
                // Obtener el producto seleccionado
                Integer idProducto = Integer.parseInt(txtId.getText());
                ProductoFX productoSeleccionado = productoService.obtenerPorId(idProducto);

                // Abrir la ventana de ajuste de stock
                abrirVentanaAjustarStock(productoSeleccionado);
            } catch (Exception e) {
                lblMensaje.setText("Error al cargar el producto: " + e.getMessage());
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Ha ocurrido un error",
                        "No se pudo cargar el producto para ajustar el stock: " + e.getMessage());
            }
        }
    }

    private void abrirVentanaAjustarStock(ProductoFX producto) {
        try {
            // Cargar el archivo FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.VENTANA_AJUSTE_STOCK));
            Parent root = loader.load();

            // Obtener el controlador y configurarlo
            VAjusteStockCtrlFX controller = loader.getController();

            // Inicializar el controlador con el producto
            controller.inicializar(producto);

            controller.setServices(inventarioService);

            // Crear y configurar la escena
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Ajuste de Stock - " + producto.getNombre());
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);

            // Manejar el cierre de la ventana
            stage.setOnHidden(event -> {
                // Refrescar datos después de cerrar la ventana
                cargarDatos();
            });

            // Mostrar la ventana
            stage.showAndWait();

        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al abrir ventana",
                    "No se pudo abrir la ventana para ajustar stock: " + e.getMessage());
        }
    }

    @FXML
    public void mostrarDetalles() {
        ProductoFX seleccionado = tablaProductos.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            lblMensaje.setText("Debe seleccionar un producto para ver detalles.");
            return;
        }

        StringBuilder detalles = new StringBuilder();
        detalles.append("ID: ").append(seleccionado.getIdProducto()).append("\n");
        detalles.append("Código: ").append(seleccionado.getCodigo()).append("\n");
        detalles.append("Nombre: ").append(seleccionado.getNombre()).append("\n");
        detalles.append("Descripción: ").append(seleccionado.getDescripcion()).append("\n");
        detalles.append("Precio Costo: ").append(seleccionado.getPrecioCosto()).append("\n");
        detalles.append("Precio Venta: ").append(seleccionado.getPrecioVenta()).append("\n");
        detalles.append("Stock: ").append(seleccionado.getStock()).append("\n");
        detalles.append("Stock Mínimo: ").append(seleccionado.getStockMinimo()).append("\n");
        detalles.append("Stock Máximo: ").append(seleccionado.getStockMaximo()).append("\n");
        detalles.append("Categoría: ")
                .append(seleccionado.getCategoria() != null ? seleccionado.getCategoria() : "Sin categoría")
                .append("\n");
        detalles.append("Proveedor: ")
                .append(seleccionado.getProveedor() != null ? seleccionado.getProveedor() : "Sin proveedor")
                .append("\n");
        detalles.append("Estado: ")
                .append(seleccionado.getEstado() != null && seleccionado.getEstado() ? "Activo" : "Inactivo");

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles del Producto");
        alert.setHeaderText("Información completa del producto");
        alert.setContentText(detalles.toString());
        alert.showAndWait();
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtCodigo.getText().isEmpty()) {
            errores.append("El código es obligatorio.\n");
        }

        if (txtNombre.getText().isEmpty()) {
            errores.append("El nombre es obligatorio.\n");
        }

        if (txtPrecioCosto.getText().isEmpty()) {
            errores.append("El precio de costo es obligatorio.\n");
        }

        if (txtPrecioVenta.getText().isEmpty()) {
            errores.append("El precio de venta es obligatorio.\n");
        }

        if (errores.length() > 0) {
            lblMensaje.setText(errores.toString());
            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(header);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}