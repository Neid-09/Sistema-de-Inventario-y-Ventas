package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloInventario;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProveedorFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.ProductoServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

@Component
public class ProductoControllerFX implements Initializable {

    // Componentes FXML - campos de formulario
    @FXML private TextField txtId;
    @FXML private TextField txtCodigo;
    @FXML private TextField txtNombre;
    @FXML private TextArea txtDescripcion;
    @FXML private TextField txtPrecioCosto;
    @FXML private TextField txtPrecioVenta;
    @FXML private TextField txtStock;
    @FXML private TextField txtStockMinimo;
    @FXML private TextField txtStockMaximo;
    @FXML private ComboBox<CategoriaFX> cmbCategoria;
    @FXML private ComboBox<ProveedorFX> cmbProveedor;
    @FXML private CheckBox chkEstado;

    //Componentes FXNL - Botones
    @FXML
    private Button btnActualizar;
    @FXML
    private Button btnAumentarStock;
    @FXML
    private Button btnDisminuirStock;
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
    @FXML private TextField txtFiltroNombre;
    @FXML private TextField txtFiltroCodigo;
    @FXML private ComboBox<CategoriaFX> cmbFiltroCategoria;
    @FXML private ComboBox<String> cmbFiltroEstado;

    // Componentes FXML - tabla
    @FXML private TableView<ProductoFX> tablaProductos;
    @FXML private TableColumn<ProductoFX, String> colCodigo;
    @FXML private TableColumn<ProductoFX, String> colNombre;
    @FXML private TableColumn<ProductoFX, String> colCategoria;
    @FXML private TableColumn<ProductoFX, BigDecimal> colPrecioVenta;
    @FXML private TableColumn<ProductoFX, Integer> colStock;
    @FXML private TableColumn<ProductoFX, String> colEstado;

    // Componentes FXML - otros
    @FXML private Label lblMensaje;
    @FXML private TextField txtCantidadMovimiento;

    // Servicio
    private final IProductoService productoService = new ProductoServiceImplFX();
    @Autowired
    private ILoteService loteService;

    // Colecciones de datos
    private ObservableList<ProductoFX> listaProductos = FXCollections.observableArrayList();
    private ObservableList<CategoriaFX> listaCategorias = FXCollections.observableArrayList();
    private ObservableList<ProveedorFX> listaProveedores = FXCollections.observableArrayList();

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
     * Método para configurar la visibilidad de elementos según los permisos del usuario
     * Se debe llamar en el método initialize()
     */
    private void configurarPermisos() {
        // Configurar permisos para operaciones CRUD básicas
        PermisosUIUtil.configurarBoton(btnGuardar, "crear_producto");
        PermisosUIUtil.configurarBoton(btnActualizar, "editar_producto");
        PermisosUIUtil.configurarBoton(btnEliminar, "eliminar_producto");

        // Configurar permisos para operaciones de stock
        PermisosUIUtil.configurarBoton(btnAumentarStock, "ajustar_stock");
        PermisosUIUtil.configurarBoton(btnDisminuirStock, "ajustar_stock");

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

        txtStock.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                txtStock.setText(oldText);
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

        txtCantidadMovimiento.textProperty().addListener((obs, oldText, newText) -> {
            if (!newText.matches("\\d*")) {
                txtCantidadMovimiento.setText(oldText);
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
                CategoriaFX sinCategoria = new CategoriaFX(null, "Sin categoría", "Productos sin categoría asignada", true, 0);

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

        // Obtener categoría seleccionada - considerar nulo si es la opción "Todas las categorías"
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
        txtStock.clear();
        txtStockMinimo.clear();
        txtStockMaximo.clear();
        chkEstado.setSelected(true);
        cmbCategoria.getSelectionModel().selectFirst(); // "Sin categoría"
        cmbProveedor.getSelectionModel().selectFirst(); // "Sin proveedor"
        txtCantidadMovimiento.clear();
        tablaProductos.getSelectionModel().clearSelection();
        lblMensaje.setText("");

        // Habilitar stock para nuevo producto
        txtStock.setEditable(true);

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
        txtStock.setText(producto.getStock() != null ? producto.getStock().toString() : "");
        txtStockMinimo.setText(producto.getStockMinimo() != null ? producto.getStockMinimo().toString() : "");
        txtStockMaximo.setText(producto.getStockMaximo() != null ? producto.getStockMaximo().toString() : "");
        chkEstado.setSelected(producto.getEstado() != null && producto.getEstado());

        // Hacer que el stock no sea editable para productos existentes
        txtStock.setEditable(false);

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
    public void guardarProducto() {

        // Verificar permisos antes de proceder
        if (!PermisosUIUtil.verificarPermisoConAlerta("crear_producto")) {
            return;
        }

        if (!validarCampos()) {
            return;
        }

        // Crear objeto de producto
        ProductoFX producto = obtenerProductoDesdeFormulario();

        // Ejecutar en segundo plano
        new Thread(() -> {
            try {
                // Verificar si el código ya existe
                if (producto.getIdProducto() == null &&
                        productoService.existeCodigo(producto.getCodigo(), null)) {
                    Platform.runLater(() -> {
                        lblMensaje.setText("El código ya existe. Por favor, use otro.");
                    });
                    return;
                }

                // Guardar producto
                ProductoFX productoGuardado = productoService.guardar(producto);

                Platform.runLater(() -> {
                    lblMensaje.setText("Producto guardado correctamente.");
                    cargarDatos();
                    limpiarCampos();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblMensaje.setText("Error al guardar: " + e.getMessage());
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo guardar el producto", e.getMessage());
                });
            }
        }).start();
    }

    @FXML
    public void actualizarProducto() {

        // Verificar permisos antes de proceder
        if (!PermisosUIUtil.verificarPermisoConAlerta("editar_producto")) {
            return;
        }

        if (txtId.getText().isEmpty()) {
            lblMensaje.setText("Debe seleccionar un producto para actualizar.");
            return;
        }

        if (!validarCampos()) {
            return;
        }

        ProductoFX producto = obtenerProductoDesdeFormulario();

        // Obtener el producto original de la tabla para comparar
        ProductoFX productoOriginal = null;
        for (ProductoFX p : listaProductos) {
            if (p.getIdProducto().equals(producto.getIdProducto())) {
                productoOriginal = p;
                break;
            }
        }

        // Guardar referencia al producto para usar en el hilo
        final ProductoFX productoFinal = producto;
        final ProductoFX productoOriginalFinal = productoOriginal;

        // Ejecutar en segundo plano
        new Thread(() -> {
            try {
                // Solo verificar el código si ha cambiado
                boolean codigoModificado = productoOriginalFinal != null &&
                        !productoOriginalFinal.getCodigo().equals(productoFinal.getCodigo());

                if (codigoModificado && productoService.existeCodigo(productoFinal.getCodigo(), productoFinal.getIdProducto())) {
                    Platform.runLater(() -> {
                        lblMensaje.setText("El código ya está en uso por otro producto.");
                    });
                    return;
                }

                // Actualizar producto
                ProductoFX productoActualizado = productoService.actualizar(productoFinal);

                Platform.runLater(() -> {
                    lblMensaje.setText("Producto actualizado correctamente.");
                    cargarDatos();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblMensaje.setText("Error al actualizar: " + e.getMessage());
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo actualizar el producto", e.getMessage());
                });
            }
        }).start();
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
                        mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo eliminar el producto", e.getMessage());
                    });
                }
            }).start();
        }
    }

    @FXML
    void abrirVentanaAumentarStock() {

    }

    @FXML
    void verLotesProducto() {

    }
/*    @FXML
    public void aumentarStock() {
        // Verificar permisos antes de proceder
        if (!PermisosUIUtil.verificarPermisoConAlerta("ajustar_stock")) {
            return;
        }
        if (txtId.getText().isEmpty()) {
            lblMensaje.setText("Debe seleccionar un producto para aumentar stock.");
            return;
        }

        if (txtCantidadMovimiento.getText().isEmpty()) {
            lblMensaje.setText("Debe ingresar una cantidad.");
            return;
        }

        Integer id = Integer.parseInt(txtId.getText());
        Integer cantidad = Integer.parseInt(txtCantidadMovimiento.getText());

        // Ejecutar en segundo plano
        new Thread(() -> {
            try {
                ProductoFX productoActualizado = productoService.actualizarStock(id, cantidad);

                Platform.runLater(() -> {
                    lblMensaje.setText("Stock aumentado correctamente.");
                    cargarProductoEnFormulario(productoActualizado);
                    txtCantidadMovimiento.clear();
                    cargarDatos();
                });
            } catch (Exception e) {
                Platform.runLater(() -> {
                    lblMensaje.setText("Error al aumentar stock: " + e.getMessage());
                    mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo aumentar el stock", e.getMessage());
                });
            }
        }).start();
    }*/

    @FXML
    public void disminuirStock() {
        // Verificar permisos antes de proceder
        if (!PermisosUIUtil.verificarPermisoConAlerta("ajustar_stock")) {
            return;
        }

        if (txtId.getText().isEmpty()) {
            lblMensaje.setText("Debe seleccionar un producto para disminuir stock.");
            return;
        }

        if (txtCantidadMovimiento.getText().isEmpty()) {
            lblMensaje.setText("Debe ingresar una cantidad.");
            return;
        }

        Integer idProducto = Integer.parseInt(txtId.getText());
        Integer cantidadAReducir = Integer.parseInt(txtCantidadMovimiento.getText());

        if (cantidadAReducir <= 0) {
            lblMensaje.setText("La cantidad debe ser un número positivo.");
            return;
        }

        // Mostrar confirmación
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar operación");
        confirmacion.setHeaderText("¿Está seguro de disminuir " + cantidadAReducir + " unidades del stock?");
        confirmacion.setContentText("Esta operación reducirá la cantidad de los lotes más próximos a vencer.");

        confirmacion.showAndWait().ifPresent(response -> {
            if (response == ButtonType.OK) {
                // Ejecutar en segundo plano
                new Thread(() -> {
                    try {
                        // Llamar al método que reduce la cantidad de lotes
                        loteService.reducirCantidadDeLotes(idProducto, cantidadAReducir);

                        // Obtener el producto actualizado para mostrar en el formulario
                        ProductoFX productoActualizado = productoService.obtenerPorId(idProducto);

                        Platform.runLater(() -> {
                            lblMensaje.setText("Stock disminuido correctamente. Se han actualizado los lotes correspondientes.");
                            cargarProductoEnFormulario(productoActualizado);
                            txtCantidadMovimiento.clear();
                            cargarDatos();
                        });
                    } catch (Exception e) {
                        Platform.runLater(() -> {
                            lblMensaje.setText("Error al disminuir stock: " + e.getMessage());
                            mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo disminuir el stock", e.getMessage());
                        });
                    }
                }).start();
            }
        });
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
        detalles.append("Categoría: ").append(seleccionado.getCategoria() != null ? seleccionado.getCategoria() : "Sin categoría").append("\n");
        detalles.append("Proveedor: ").append(seleccionado.getProveedor() != null ? seleccionado.getProveedor() : "Sin proveedor").append("\n");
        detalles.append("Estado: ").append(seleccionado.getEstado() != null && seleccionado.getEstado() ? "Activo" : "Inactivo");

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

        if (txtStock.getText().isEmpty()) {
            errores.append("El stock es obligatorio.\n");
        }

        if (errores.length() > 0) {
            lblMensaje.setText(errores.toString());
            return false;
        }

        return true;
    }

    private ProductoFX obtenerProductoDesdeFormulario() {
        ProductoFX producto = new ProductoFX();

        if (!txtId.getText().isEmpty()) {
            producto.setIdProducto(Integer.parseInt(txtId.getText()));
        }

        producto.setCodigo(txtCodigo.getText());
        producto.setNombre(txtNombre.getText());
        producto.setDescripcion(txtDescripcion.getText());

        if (!txtPrecioCosto.getText().isEmpty()) {
            producto.setPrecioCosto(new BigDecimal(txtPrecioCosto.getText()));
        }

        if (!txtPrecioVenta.getText().isEmpty()) {
            producto.setPrecioVenta(new BigDecimal(txtPrecioVenta.getText()));
        }

        if (!txtStock.getText().isEmpty()) {
            producto.setStock(Integer.parseInt(txtStock.getText()));
        }

        if (!txtStockMinimo.getText().isEmpty()) {
            producto.setStockMinimo(Integer.parseInt(txtStockMinimo.getText()));
        }

        if (!txtStockMaximo.getText().isEmpty()) {
            producto.setStockMaximo(Integer.parseInt(txtStockMaximo.getText()));
        }

        producto.setEstado(chkEstado.isSelected());

        // Configurar categoría (puede ser null)
        CategoriaFX categoriaSeleccionada = cmbCategoria.getSelectionModel().getSelectedItem();
        if (categoriaSeleccionada != null && categoriaSeleccionada.getIdCategoria() != null) {
            producto.setIdCategoria(categoriaSeleccionada.getIdCategoria());
            producto.setCategoria(categoriaSeleccionada.getNombre());
        } else {
            producto.setIdCategoria(null);
            producto.setCategoria(null);
        }

        // Configurar proveedor (puede ser null)
        ProveedorFX proveedorSeleccionado = cmbProveedor.getSelectionModel().getSelectedItem();
        if (proveedorSeleccionado != null && proveedorSeleccionado.getIdProveedor() != null) {
            producto.setIdProveedor(proveedorSeleccionado.getIdProveedor());
            producto.setProveedor(proveedorSeleccionado.getNombre());
        } else {
            producto.setIdProveedor(null);
            producto.setProveedor(null);
        }

        return producto;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String header, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(header);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}