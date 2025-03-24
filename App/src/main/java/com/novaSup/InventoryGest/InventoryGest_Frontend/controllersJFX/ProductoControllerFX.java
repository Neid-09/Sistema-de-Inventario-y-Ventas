package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ResourceBundle;

@Component
public class ProductoControllerFX implements Initializable {

    @Autowired
    private IProductoService productoServiceFX; // Servicio frontend en lugar del backend

    @FXML
    private Button btnActualizar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnGuardar;

    @FXML
    private Button btnLimpiar;

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
        configurarColumnas();
        cargarProductos();
        configurarSeleccionTabla();
        btnActualizar.setDisable(true);
        btnEliminar.setDisable(true);
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

                btnActualizar.setDisable(false);
                btnEliminar.setDisable(false);
            }
        });
    }

    private void cargarProductos() {
        try {
            listaProductos.clear();
            listaProductos.addAll(productoServiceFX.obtenerTodos());
            tablaProductos.setItems(listaProductos);
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al cargar productos: " + e.getMessage());
        }
    }

    @FXML
    void guardarProducto(ActionEvent event) {
        if (validarCampos()) {
            try {
                String nombre = txtNombre.getText();
                String descripcion = txtDescripcion.getText();
                BigDecimal precio = new BigDecimal(txtPrecio.getText());
                Integer stock = Integer.parseInt(txtStock.getText());

                productoServiceFX.guardar(nombre, descripcion, precio, stock);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto guardado correctamente");

                limpiarCampos(null);
                cargarProductos();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al guardar: " + e.getMessage());
            }
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
                Integer stock = Integer.parseInt(txtStock.getText());

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
            mostrarAlerta(Alert.AlertType.WARNING, "Advertencia", "Seleccione un producto para eliminar");
            return;
        }

        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Confirmar eliminación");
        confirmacion.setHeaderText(null);
        confirmacion.setContentText("¿Está seguro de eliminar este producto?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            try {
                Integer id = Integer.parseInt(txtId.getText());
                productoServiceFX.eliminar(id);
                mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Producto eliminado correctamente");

                limpiarCampos(null);
                cargarProductos();
            } catch (Exception e) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Error al eliminar: " + e.getMessage());
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

        tablaProductos.getSelectionModel().clearSelection();

        btnActualizar.setDisable(true);
        btnEliminar.setDisable(false);
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

        if (txtStock.getText().trim().isEmpty()) {
            errores.append("- El stock es requerido\n");
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
}