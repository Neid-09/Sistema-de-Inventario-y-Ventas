package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IEntradaProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static com.novaSup.InventoryGest.MainApp.springContext;

@Component
public class ProductoControllerFX implements Initializable {

    @Autowired
    private IProductoService productoServiceFX; // Servicio frontend en lugar del backend

    @Autowired
    private IEntradaProductoService entradaProductoService;

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnLimpiar;

    //Botones para control de stock
    @FXML
    private Button btnAumentarStock;

    @FXML
    private Button btnDisminuirStock;

    @FXML
    private TableColumn<ProductoFX, String> colDescripcion;

    @FXML
    private TableColumn<ProductoFX, Integer> colId;

    @FXML
    private TableColumn<ProductoFX, String> colNombre;

    @FXML
    private TableColumn<ProductoFX, BigDecimal> colPrecio;

    @FXML
    private TableColumn<ProductoFX, Integer> colStock;

    @FXML
    private TableView<ProductoFX> tablaProductos;

    //Para añadir un movimiento en stock que + o -
    @FXML
    private TextField txtCantidadMovimiento;

    @FXML
    private TextArea txtDescripcion;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtPrecio;

    @FXML
    private TextField txtStock;

    private ObservableList<ProductoFX> listaProductos = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {

        Platform.runLater(() -> {
            Stage stage = (Stage) tablaProductos.getScene().getWindow();
            stage.setResizable(true);
        });

        configurarColumnas();
        cargarProductos();
        configurarSeleccionTabla();

        // Hacer el campo de stock no editable
        txtStock.setEditable(true);

        // Deshabilitar botones hasta seleccionar un producto
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        btnAumentarStock.setDisable(true);
        btnDisminuirStock.setDisable(true);
    }

    private void configurarColumnas() {
        colId.setCellValueFactory(new PropertyValueFactory<>("idProducto"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDescripcion.setCellValueFactory(new PropertyValueFactory<>("descripcion"));
        colPrecio.setCellValueFactory(new PropertyValueFactory<>("precio"));
        colStock.setCellValueFactory(new PropertyValueFactory<>("stock"));
    }

    private void configurarSeleccionTabla() {
        tablaProductos.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtId.setText(String.valueOf(newSelection.getIdProducto()));
                txtNombre.setText(newSelection.getNombre());
                txtDescripcion.setText(newSelection.getDescripcion());
                txtPrecio.setText(newSelection.getPrecio().toString());
                txtStock.setText(String.valueOf(newSelection.getStock()));

                // Bloquear edición de stock para productos existentes
                txtStock.setEditable(false);

                btnActualizar.setDisable(false);
                btnEliminar.setDisable(false);
                btnAumentarStock.setDisable(false);
                btnDisminuirStock.setDisable(false);
                btnGuardar.setDisable(true);
            } else {
                btnActualizar.setDisable(true);
                btnEliminar.setDisable(true);
                btnAumentarStock.setDisable(true);
                btnDisminuirStock.setDisable(true);
                btnGuardar.setDisable(false);
            }
        });
    }

    private void cargarProductos() {
        try {
            listaProductos.clear();
            List<ProductoFX> productos = productoServiceFX.obtenerTodos();
            // Filtrar solo productos activos
            listaProductos.addAll(productos.stream()
                    .filter(ProductoFX::getEstado)
                    .collect(Collectors.toList()));
            tablaProductos.setItems(listaProductos);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar productos: " + e.getMessage());
        }
    }

    @FXML
    void guardarProducto(ActionEvent event) {
        try {
            // Obtener valores de los campos
            String nombre = txtNombre.getText().trim();
            String descripcion = txtDescripcion.getText().trim();
            String precioStr = txtPrecio.getText().trim();
            String stockStr = txtStock.getText().trim();

            // Validar campos obligatorios
            if (nombre.isEmpty() || precioStr.isEmpty() || stockStr.isEmpty()) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Todos los campos son obligatorios");
                return;
            }

            // Convertir a tipos numéricos
            BigDecimal precio = new BigDecimal(precioStr);
            Integer stock = Integer.parseInt(stockStr);

            // Validar valores
            if (precio.compareTo(BigDecimal.ZERO) <= 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "El precio debe ser mayor que cero");
                return;
            }

            if (stock < 0) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "El stock no puede ser negativo");
                return;
            }

            // Guardar el producto con stock inicial 0
            ProductoFX productoGuardado = productoServiceFX.guardar(nombre, descripcion, precio, 0);

            // Si hay stock inicial, registrar un movimiento de entrada
            if (stock > 0) {
                entradaProductoService.registrarMovimiento(
                        productoGuardado.getIdProducto(),
                        stock,
                        EntradaProductoFX.TipoMovimiento.ENTRADA,
                        precio
                );
            }

            limpiarCampos();
            cargarProductos();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto guardado correctamente");
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Formato inválido en campos numéricos");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    void actualizarProducto(ActionEvent event) {
        if (txtId.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un producto para actualizar");
            return;
        }

        if (validarCampos()) {
            try {
                Integer id = Integer.parseInt(txtId.getText());
                String nombre = txtNombre.getText();
                String descripcion = txtDescripcion.getText();
                BigDecimal precio = new BigDecimal(txtPrecio.getText());

                // Obtener el stock actual del producto en la base de datos
                ProductoFX productoActual = tablaProductos.getSelectionModel().getSelectedItem();
                Integer stock = productoActual.getStock();

                productoServiceFX.actualizar(id, nombre, descripcion, precio, stock);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto actualizado correctamente");

                limpiarCampos(null);
                cargarProductos();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al actualizar: " + e.getMessage());
            }
        }
    }

    @FXML
    void eliminarProducto(ActionEvent event) {
        if (txtId.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un producto para desactivar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar desactivación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro que desea desactivar este producto?\n\nEl producto no se eliminará permanentemente, solo quedará oculto y se preservará el historial de movimientos.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            try {
                Integer id = Integer.parseInt(txtId.getText());
                productoServiceFX.desactivarProducto(id);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto desactivado correctamente");

                limpiarCampos();
                cargarProductos();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al desactivar: " + e.getMessage());
            }
        }
    }

    @FXML
    void limpiarCampos(ActionEvent event) {
        txtId.clear();
        txtNombre.clear();
        txtDescripcion.clear();
        txtPrecio.clear();
        txtStock.clear();
        txtCantidadMovimiento.clear();

        // Habilitar edición de stock para productos nuevos
        txtStock.setEditable(true);
        btnGuardar.setDisable(false);

        tablaProductos.getSelectionModel().clearSelection();

        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
        btnAumentarStock.setDisable(true);
        btnDisminuirStock.setDisable(true);
    }

    void limpiarCampos() {
        limpiarCampos(null);
    }

    private boolean validarCampos() {
        StringBuilder errores = new StringBuilder();

        if (txtNombre.getText().trim().isEmpty()) {
            errores.append("- El nombre es requerido\n");
        }

        if (txtPrecio.getText().trim().isEmpty()) {
            errores.append("- El precio es requerido\n");
        } else {
            try {
                BigDecimal precio = new BigDecimal(txtPrecio.getText());
                if (precio.compareTo(BigDecimal.ZERO) < 0) {
                    errores.append("- El precio no puede ser negativo\n");
                }
            } catch (NumberFormatException e) {
                errores.append("- El precio debe ser un número válido\n");
            }
        }

        // Solo validar stock si es un producto nuevo (sin ID)
        if (txtId.getText().isEmpty()) {
            if (txtStock.getText().trim().isEmpty()) {
                errores.append("- El stock inicial es requerido\n");
            } else {
                try {
                    int stock = Integer.parseInt(txtStock.getText());
                    if (stock < 0) {
                        errores.append("- El stock no puede ser negativo\n");
                    }
                } catch (NumberFormatException e) {
                    errores.append("- El stock debe ser un número entero\n");
                }
            }
        }

        if (errores.length() > 0) {
            mostrarAlerta(Alert.AlertType.WARNING, "Validación", "Por favor corrija los siguientes errores:\n" + errores.toString());
            return false;
        }

        return true;
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }

    //Para control de stock
    @FXML
    void aumentarStock(ActionEvent event) {
        if (txtId.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un producto primero");
            return;
        }

        if (txtCantidadMovimiento.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Ingrese la cantidad a aumentar");
            return;
        }

        try {
            Integer idProducto = Integer.parseInt(txtId.getText());
            Integer cantidad = Integer.parseInt(txtCantidadMovimiento.getText());

            // Obtener el precio del producto seleccionado
            ProductoFX productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
            BigDecimal precioUnitario = productoSeleccionado.getPrecio();

            if (cantidad <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "La cantidad debe ser mayor a cero");
                return;
            }

            // Registrar el movimiento
            entradaProductoService.registrarMovimiento(idProducto, cantidad, EntradaProductoFX.TipoMovimiento.ENTRADA, precioUnitario);

            // Actualizar la tabla de productos
            cargarProductos();

            // Seleccionar el producto actualizado
            tablaProductos.getItems().stream()
                    .filter(p -> p.getIdProducto().equals(idProducto))
                    .findFirst()
                    .ifPresent(p -> {
                        tablaProductos.getSelectionModel().select(p);
                        tablaProductos.scrollTo(p);
                        txtStock.setText(p.getStock().toString());
                    });

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Stock aumentado correctamente");
            txtCantidadMovimiento.clear();
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "La cantidad debe ser un número entero");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al aumentar stock: " + e.getMessage());
        }
    }

    @FXML
    void disminuirStock(ActionEvent event) {
        if (txtId.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un producto primero");
            return;
        }

        if (txtCantidadMovimiento.getText().isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Ingrese la cantidad a disminuir");
            return;
        }

        try {
            Integer idProducto = Integer.parseInt(txtId.getText());
            Integer cantidad = Integer.parseInt(txtCantidadMovimiento.getText());
            Integer stockActual = Integer.parseInt(txtStock.getText());

            // Obtener el precio del producto seleccionado
            ProductoFX productoSeleccionado = tablaProductos.getSelectionModel().getSelectedItem();
            BigDecimal precioUnitario = productoSeleccionado.getPrecio();

            if (cantidad <= 0) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "La cantidad debe ser mayor a cero");
                return;
            }

            if (cantidad > stockActual) {
                mostrarAlerta(Alert.AlertType.WARNING, "Advertencia",
                        "No hay suficiente stock. Stock actual: " + stockActual);
                return;
            }

            // Registrar el movimiento
            entradaProductoService.registrarMovimiento(idProducto, cantidad, EntradaProductoFX.TipoMovimiento.SALIDA, precioUnitario);

            // Actualizar la tabla de productos
            cargarProductos();

            // Seleccionar el producto actualizado
            tablaProductos.getItems().stream()
                    .filter(p -> p.getIdProducto().equals(idProducto))
                    .findFirst()
                    .ifPresent(p -> {
                        tablaProductos.getSelectionModel().select(p);
                        tablaProductos.scrollTo(p);
                        txtStock.setText(p.getStock().toString());
                    });

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Stock disminuido correctamente");
            txtCantidadMovimiento.clear();
        } catch (NumberFormatException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "La cantidad debe ser un número entero");
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al disminuir stock: " + e.getMessage());
        }
    }

    @FXML
    void verHistorialStock(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.CONTROLSTOCK_FXML));
            loader.setControllerFactory(springContext::getBean);

            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Historial de Movimientos");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

            // Refrescar la tabla de productos
            cargarProductos();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo abrir la ventana de historial: " + e.getMessage());
        }
    }

    @FXML
    void cerrarGestionProductos(ActionEvent event) {
        try {
            // Obtener el Stage actual desde el botón
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Cargar la vista del menú principal
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.MENUPRINCIPAL_FXML));

            // Usar el contexto de Spring para la inyección de dependencias
            loader.setControllerFactory(springContext::getBean);
            Parent root = loader.load();

            // Crear nueva escena y mostrarla
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo cargar la pantalla del menú principal: " + e.getMessage());
        }
    }

}