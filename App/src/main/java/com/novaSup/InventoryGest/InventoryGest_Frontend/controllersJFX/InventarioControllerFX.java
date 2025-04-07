package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

@Component
public class InventarioControllerFX {

    @FXML
    private Button btnCategorias;

    @FXML
    private Button btnLotes;

    @FXML
    private Button btnMovimientos;

    @FXML
    private Button btnProductos;

    @FXML
    private Button btnPromociones;

    @FXML
    private Button btnProveedores;

    @FXML
    private Label lblStatus;

    @FXML
    void irGestionCategorias(ActionEvent event) {
        try {
            // Obtener el Stage actual
            Stage stage = (Stage) btnCategorias.getScene().getWindow();

            // Obtener el controlador del MenuPrincipal desde userData
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            // Cargar el módulo de gestión de categorías
            // Como no existe una constante predefinida, usamos la ruta directamente
            menuController.cargarModuloEnPanel("/views/ModuloInventario/GestCategorias.fxml");

            // Actualizar el estado
            lblStatus.setText("Módulo de gestión de categorías cargado");
        } catch (Exception e) {
            System.err.println("Error al cargar el módulo de categorías: " + e.getMessage());
            e.printStackTrace();

            // Actualizar mensaje de estado en caso de error
            lblStatus.setText("Error al cargar módulo de categorías");
        }
    }

    @FXML
    void irGestionLotes(ActionEvent event) {

    }

    @FXML
    void irGestionMovimientos(ActionEvent event) {

    }

    @FXML
    void irGestionProductos(ActionEvent event) {
        try {
            // Obtener el Stage actual
            Stage stage = (Stage) btnProductos.getScene().getWindow();

            // Obtener el controlador del MenuPrincipal desde userData
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            // Cargar el módulo de CRUD de productos
            menuController.cargarModuloEnPanel(PathsFXML.CRUD_PRDUCTOS);

            // Actualizar el estado
            lblStatus.setText("Módulo de gestión de productos cargado");
        } catch (Exception e) {
            System.err.println("Error al cargar el módulo de productos: " + e.getMessage());
            e.printStackTrace();

            // Actualizar mensaje de estado en caso de error
            lblStatus.setText("Error al cargar módulo de productos");
        }
    }

    @FXML
    void irGestionPromociones(ActionEvent event) {

    }

    @FXML
    void irGestionProveedores(ActionEvent event) {

    }

}
