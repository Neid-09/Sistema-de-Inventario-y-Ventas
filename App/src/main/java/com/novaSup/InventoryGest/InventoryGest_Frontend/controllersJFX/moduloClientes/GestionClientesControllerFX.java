package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloClientes;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class GestionClientesControllerFX {

    @FXML
    private Button btnAddCliente;

    @FXML
    private Button btnDeleteCliente;

    @FXML
    private Button btnEditCliente;

    @FXML
    private Button btnViewDetails;

    @FXML
    private TableView<?> clientesTable;

    @FXML
    private ComboBox<?> cmbStatusFilter;

    @FXML
    private TableColumn<?, ?> colCedula;

    @FXML
    private TableColumn<?, ?> colCorreo;

    @FXML
    private TableColumn<?, ?> colDireccion;

    @FXML
    private TableColumn<?, ?> colEstado;

    @FXML
    private TableColumn<?, ?> colNombre;

    @FXML
    private TableColumn<?, ?> colPuntosFidelidad;

    @FXML
    private TableColumn<?, ?> colSaldoPendiente;

    @FXML
    private TableColumn<?, ?> colTelefono;

    @FXML
    private TableColumn<?, ?> colTotalCompras;

    @FXML
    private TableColumn<?, ?> colUltimaCompra;

    @FXML
    private TextField txtSearch;

}
