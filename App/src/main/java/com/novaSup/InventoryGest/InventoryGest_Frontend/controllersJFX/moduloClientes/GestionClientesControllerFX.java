package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloClientes;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ClienteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IClienteService; // Importar la interfaz
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.math.BigDecimal;
import java.net.URL;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class GestionClientesControllerFX implements Initializable {

    // ... (FXML fields - txtSearch, cmbStatusFilter, clientesTable, columns, buttons) ...
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbStatusFilter;
    @FXML private TableView<ClienteFX> clientesTable;
    @FXML private TableColumn<ClienteFX, String> colNombre;
    @FXML private TableColumn<ClienteFX, String> colCorreo;
    @FXML private TableColumn<ClienteFX, String> colCedula;
    @FXML private TableColumn<ClienteFX, String> colCelular;
    @FXML private TableColumn<ClienteFX, String> colDireccion;
    @FXML private TableColumn<ClienteFX, BigDecimal> colTotalCompras; // Cambiado a BigDecimal
    @FXML private TableColumn<ClienteFX, OffsetDateTime> colUltimaCompra;
    @FXML private TableColumn<ClienteFX, BigDecimal> colLimiteCredito; // Cambiado a BigDecimal
    @FXML private TableColumn<ClienteFX, Integer> colPuntosFidelidad;
    @FXML private TableColumn<ClienteFX, Boolean> colTieneCredito;

    @FXML private Button btnAddCliente;
    @FXML private Button btnEditCliente;
    @FXML private Button btnDeleteCliente;
    @FXML private Button btnViewDetails;

    private final IClienteService clienteService; // Campo para el servicio inyectado
    private ObservableList<ClienteFX> masterData = FXCollections.observableArrayList();
    private FilteredList<ClienteFX> filteredData;
    private SortedList<ClienteFX> sortedData;

    // Constructor para inyección de dependencias
    public GestionClientesControllerFX(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        configurarFiltros();
        cargarClientes(); // Usará el servicio inyectado
        configurarBotones();
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colCedula.setCellValueFactory(new PropertyValueFactory<>("cedula"));
        colCelular.setCellValueFactory(new PropertyValueFactory<>("celular"));
        colDireccion.setCellValueFactory(new PropertyValueFactory<>("direccion"));
        colTotalCompras.setCellValueFactory(new PropertyValueFactory<>("totalComprado"));
        colUltimaCompra.setCellValueFactory(new PropertyValueFactory<>("ultimaCompra"));
        colLimiteCredito.setCellValueFactory(new PropertyValueFactory<>("limiteCredito"));
        colPuntosFidelidad.setCellValueFactory(new PropertyValueFactory<>("puntosFidelidad"));
        colTieneCredito.setCellValueFactory(new PropertyValueFactory<>("tieneCreditos"));

        // Formateador para fecha/hora
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        colUltimaCompra.setCellFactory(column -> new TableCell<ClienteFX, OffsetDateTime>() {
            @Override
            protected void updateItem(OffsetDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });

        // Celda para el booleano de crédito
        colTieneCredito.setCellFactory(CheckBoxTableCell.forTableColumn(colTieneCredito));
        colTieneCredito.setStyle("-fx-alignment: CENTER;");

        // Configuración de listas filtrada y ordenada
        filteredData = new FilteredList<>(masterData, p -> true);
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(clientesTable.comparatorProperty());
        clientesTable.setItems(sortedData);
    }

    private void configurarFiltros() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        // Configuración de cmbStatusFilter si se usa
    }

     private void actualizarFiltro() {
        String filtroTexto = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();
        // String filtroEstado = cmbStatusFilter.getValue();

        filteredData.setPredicate(cliente -> {
            boolean matchTexto = filtroTexto.isEmpty() ||
                                 (cliente.getNombre() != null && cliente.getNombre().toLowerCase().contains(filtroTexto)) ||
                                 (cliente.getCorreo() != null && cliente.getCorreo().toLowerCase().contains(filtroTexto)) ||
                                 (cliente.getCedula() != null && cliente.getCedula().toLowerCase().contains(filtroTexto));

            // boolean matchEstado = ...;
            return matchTexto; // && matchEstado;
        });
    }

    private void cargarClientes() {
        try {
            // Usar la instancia del servicio inyectado
            masterData.setAll(clienteService.obtenerTodosLosClientes());
            clientesTable.refresh();
        } catch (Exception e) {
            // Manejo de errores mejorado (mostrar alerta)
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error de Carga");
            alert.setHeaderText("No se pudieron cargar los clientes");
            alert.setContentText("Detalles: " + e.getMessage());
            alert.showAndWait();
            logger.error("Error al cargar clientes: {}", e.getMessage(), e); // Usar logger si está configurado
            masterData.clear();
        }
    }

    private void configurarBotones() {
        btnAddCliente.setOnAction(event -> handleAddCliente());
        btnEditCliente.setOnAction(event -> handleEditCliente());
        btnDeleteCliente.setOnAction(event -> handleDeleteCliente()); // Usará el servicio
        btnViewDetails.setOnAction(event -> handleViewDetails());

        btnEditCliente.disableProperty().bind(clientesTable.getSelectionModel().selectedItemProperty().isNull());
        btnDeleteCliente.disableProperty().bind(clientesTable.getSelectionModel().selectedItemProperty().isNull());
        btnViewDetails.disableProperty().bind(clientesTable.getSelectionModel().selectedItemProperty().isNull());
    }

    @FXML
    private void handleAddCliente() {
        System.out.println("Abrir diálogo/vista para agregar cliente...");
        // Lógica para mostrar diálogo de agregar
        // Si se guarda exitosamente: cargarClientes();
    }

    @FXML
    private void handleEditCliente() {
        ClienteFX selectedCliente = clientesTable.getSelectionModel().getSelectedItem();
        if (selectedCliente != null) {
            System.out.println("Abrir diálogo/vista para editar cliente: " + selectedCliente.getNombre());
            // Lógica para mostrar diálogo de editar con selectedCliente
            // Si se guarda exitosamente: cargarClientes();
        }
    }

    @FXML
    private void handleDeleteCliente() {
        ClienteFX selectedCliente = clientesTable.getSelectionModel().getSelectedItem();
        if (selectedCliente != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("Eliminar Cliente: " + selectedCliente.getNombre());
            confirmacion.setContentText("¿Está seguro?");

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.OK) {
                    try {
                        // Usar la instancia del servicio inyectado
                        boolean eliminado = clienteService.eliminarCliente(selectedCliente.getIdCliente());
                        if (eliminado) {
                            cargarClientes(); // Recargar la lista
                        } else {
                            // Mostrar error si la API devuelve false (ej. no encontrado)
                            Alert errorAlert = new Alert(Alert.AlertType.WARNING);
                            errorAlert.setTitle("Eliminación Fallida");
                            errorAlert.setHeaderText("No se pudo eliminar el cliente.");
                            errorAlert.setContentText("El cliente no fue encontrado o no se pudo eliminar.");
                            errorAlert.showAndWait();
                        }
                    } catch (Exception e) {
                        // Mostrar error si ocurre una excepción
                        Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                        errorAlert.setTitle("Error al Eliminar");
                        errorAlert.setHeaderText("Ocurrió un error al intentar eliminar el cliente.");
                        errorAlert.setContentText("Detalles: " + e.getMessage());
                        errorAlert.showAndWait();
                        logger.error("Error al eliminar cliente: {}", e.getMessage(), e);
                    }
                }
            });
        }
    }

    @FXML
    private void handleViewDetails() {
        ClienteFX selectedCliente = clientesTable.getSelectionModel().getSelectedItem();
        if (selectedCliente != null) {
            System.out.println("Mostrar detalles del cliente: " + selectedCliente.getNombre());
            // Lógica para mostrar vista de detalles
        }
    }

    // Logger (opcional pero recomendado)
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GestionClientesControllerFX.class);

}
