package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.PermisosUIUtil;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;

@Component
public class InventarioControllerFX implements Initializable {

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


    @Override
    public void initialize(URL url, ResourceBundle rb) {
        configurarPermisos();
        // Resto del código de inicialización...
    }

    @FXML
    void irGestionCategorias(ActionEvent event) {
        try {
            // Obtener el Stage actual
            Stage stage = (Stage) btnCategorias.getScene().getWindow();

            // Obtener el controlador del MenuPrincipal desde userData
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            // Cargar el módulo de gestión de categorías
            // Como no existe una constante predefinida, usamos la ruta directamente
            menuController.cargarModuloEnPanel(PathsFXML.GEST_CATEGORIAS);

            // Actualizar el estado
            lblStatus.setText("Módulo de gestión de categorías cargado");
        } catch (Exception e) {
            System.err.println("Error al cargar el módulo de categorías: " + e.getMessage());
            e.printStackTrace();

            // Actualizar mensaje de estado en caso de error
            lblStatus.setText("Error al cargar módulo de categorías");
        }
    }

    /**
     * Configura la visibilidad de los elementos según los permisos del usuario
     */
    private void configurarPermisos() {
        // Configurar permisos para acceder a las diferentes secciones
        PermisosUIUtil.configurarBoton(btnCategorias, "gestionar_categorias");
        PermisosUIUtil.configurarBoton(btnProductos, "gestionar_productos");
        PermisosUIUtil.configurarBoton(btnProveedores, "gestionar_proveedores");
        PermisosUIUtil.configurarBoton(btnMovimientos, "gestionar_movimientos");
       // PermisosUIUtil.configurarBoton(btnReportes, "ver_reportes_inventario");

        // Si hay más botones o controles que requieran permisos específicos
        // PermisosUIUtil.configurarBoton(btnOtroControl, "permiso_requerido");
    }

    @FXML
    void irGestionLotes(ActionEvent event) {

        try {
            // Obtener el Stage actual
            Stage stage = (Stage) btnLotes.getScene().getWindow();

            // Obtener el controlador del MenuPrincipal desde userData
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            // Cargar el módulo de gestión de proveedores
            menuController.cargarModuloEnPanel(PathsFXML.GEST_LOTES);

            // Actualizar el estado
            lblStatus.setText("Módulo de gestión de lotes cargado");
        } catch (Exception e) {
            System.err.println("Error al cargar el módulo de lotes: " + e.getMessage());
            e.printStackTrace();

            // Actualizar mensaje de estado en caso de error
            lblStatus.setText("Error al cargar módulo de lotes");
        }

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
        try {
            // Obtener el Stage actual
            Stage stage = (Stage) btnProveedores.getScene().getWindow();

            // Obtener el controlador del MenuPrincipal desde userData
            MenuPrincipalControllerFX menuController = (MenuPrincipalControllerFX) stage.getUserData();

            // Cargar el módulo de gestión de proveedores
            menuController.cargarModuloEnPanel(PathsFXML.GEST_PROVEEDORES);

            // Actualizar el estado
            lblStatus.setText("Módulo de gestión de proveedores cargado");
        } catch (Exception e) {
            System.err.println("Error al cargar el módulo de proveedores: " + e.getMessage());
            e.printStackTrace();

            // Actualizar mensaje de estado en caso de error
            lblStatus.setText("Error al cargar módulo de proveedores");
        }
    }

}
