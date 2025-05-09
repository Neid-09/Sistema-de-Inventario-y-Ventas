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
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class GestionClientesControllerFX implements Initializable {

    // ... (FXML fields - txtSearch, cmbStatusFilter, clientesTable, columns, buttons) ...
    @FXML private TextField txtSearch;
    @FXML private ComboBox<String> cmbStatusFilter;
    @FXML private TableView<ClienteFX> clientesTable;
    @FXML private TableColumn<ClienteFX, String> colNombre;
    @FXML private TableColumn<ClienteFX, String> colDocumentoIdentidad;
    @FXML private TableColumn<ClienteFX, String> colIdentificacionFiscal;
    @FXML private TableColumn<ClienteFX, String> colCelular;
    @FXML private TableColumn<ClienteFX, String> colCorreo;
    @FXML private TableColumn<ClienteFX, Boolean> colActivo;
    @FXML private TableColumn<ClienteFX, BigDecimal> colLimiteCredito;

    @FXML private Button btnAddCliente;
    @FXML private Button btnEditCliente;
    @FXML private Button btnDeleteCliente;
    @FXML private Button btnViewDetails;

    @FXML private VBox detailsPanelContainer;
    @FXML private Label lblDetailsTitle;
    @FXML private Label lblDetailNombre;
    @FXML private Label lblDetailDocumento;
    @FXML private Label lblDetailCorreo;
    @FXML private Label lblDetailTotalCompras;

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
        colDocumentoIdentidad.setCellValueFactory(new PropertyValueFactory<>("documentoIdentidad"));
        colIdentificacionFiscal.setCellValueFactory(new PropertyValueFactory<>("identificacionFiscal"));
        colCelular.setCellValueFactory(new PropertyValueFactory<>("celular"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colLimiteCredito.setCellValueFactory(new PropertyValueFactory<>("limiteCredito"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        // Celda para el booleano de activo (usando CheckBox)
        colActivo.setCellFactory(column -> new TableCell<ClienteFX, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            {
                // Deshabilitar el checkbox para que sea solo visual
                checkBox.setDisable(true);
                checkBox.setStyle("-fx-opacity: 1;"); // Para que no se vea grisáceo por estar deshabilitado
                setContentDisplay(ContentDisplay.GRAPHIC_ONLY);
            }
            @Override
            protected void updateItem(Boolean item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    checkBox.setSelected(item);
                    setGraphic(checkBox);
                    setAlignment(javafx.geometry.Pos.CENTER); // Centrar el CheckBox
                }
            }
        });
        colActivo.setStyle("-fx-alignment: CENTER;");

        // Configuración de listas filtrada y ordenada
        filteredData = new FilteredList<>(masterData, p -> true);
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(clientesTable.comparatorProperty());
        clientesTable.setItems(sortedData);
    }

    private void configurarFiltros() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        
        // Configuración de cmbStatusFilter
        cmbStatusFilter.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        cmbStatusFilter.setValue("Todos"); // Valor por defecto
        cmbStatusFilter.setOnAction(event -> actualizarFiltro());
    }

     private void actualizarFiltro() {
        String filtroTexto = txtSearch.getText() == null ? "" : txtSearch.getText().toLowerCase().trim();
        String filtroEstado = cmbStatusFilter.getValue();

        filteredData.setPredicate(cliente -> {
            boolean matchTexto = filtroTexto.isEmpty() ||
                                 (cliente.getNombre() != null && cliente.getNombre().toLowerCase().contains(filtroTexto)) ||
                                 (cliente.getCorreo() != null && cliente.getCorreo().toLowerCase().contains(filtroTexto)) ||
                                 (cliente.getDocumentoIdentidad() != null && cliente.getDocumentoIdentidad().toLowerCase().contains(filtroTexto)) ||
                                 (cliente.getIdentificacionFiscal() != null && cliente.getIdentificacionFiscal().toLowerCase().contains(filtroTexto)) ||
                                 (cliente.getCelular() != null && cliente.getCelular().toLowerCase().contains(filtroTexto));

            boolean matchEstado = false;
            if (filtroEstado == null || filtroEstado.equals("Todos")) {
                matchEstado = true;
            } else if (filtroEstado.equals("Activos")) {
                matchEstado = cliente.isActivo();
            } else if (filtroEstado.equals("Inactivos")) {
                matchEstado = !cliente.isActivo();
            }
            return matchTexto && matchEstado;
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
            detailsPanelContainer.getChildren().clear(); // Limpiar detalles anteriores
            detailsPanelContainer.setVisible(true);
            lblDetailsTitle.setVisible(true); // El título ya está en FXML, solo hacerlo visible.

            // Añadir título al VBox si no está ya como un Label separado.
            Label title = new Label("Detalles del Cliente:");
            title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding-bottom: 8px;"); // Coincide con .details-title-label
            detailsPanelContainer.getChildren().add(title);

            // Añadir detalles dinámicamente
            addDetailRow("ID Cliente:", String.valueOf(selectedCliente.getIdCliente()));
            addDetailRow("Nombre:", selectedCliente.getNombre());
            addDetailRow("Doc. Identidad:", selectedCliente.getDocumentoIdentidad());
            addDetailRow("ID Fiscal:", selectedCliente.getIdentificacionFiscal());
            addDetailRow("Correo:", selectedCliente.getCorreo());
            addDetailRow("Celular:", selectedCliente.getCelular());
            addDetailRow("Dirección:", selectedCliente.getDireccion());
            addDetailRow("Activo:", selectedCliente.isActivo() ? "Sí" : "No");
            addDetailRow("Límite Crédito:", selectedCliente.getLimiteCredito() != null ? selectedCliente.getLimiteCredito().toString() + " €" : "N/A");
            addDetailRow("Total Comprado:", selectedCliente.getTotalComprado() != null ? selectedCliente.getTotalComprado().toString() + " €" : "N/A");
            addDetailRow("Puntos Fidelidad:", selectedCliente.getPuntosFidelidad() != null ? String.valueOf(selectedCliente.getPuntosFidelidad()) : "N/A");
            
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            if (selectedCliente.getUltimaCompra() != null) {
                addDetailRow("Última Compra:", selectedCliente.getUltimaCompra().format(formatter));
            }
             if (selectedCliente.getFechaRegistro() != null) {
                addDetailRow("Fecha Registro:", selectedCliente.getFechaRegistro().format(formatter));
            }
            if (selectedCliente.getFechaActualizacion() != null) {
                addDetailRow("Fecha Actualización:", selectedCliente.getFechaActualizacion().format(formatter));
            }

            addDetailRow("Requiere Factura Default:", selectedCliente.isRequiereFacturaDefault() ? "Sí" : "No");
            addDetailRow("Razón Social:", selectedCliente.getRazonSocial());
            addDetailRow("Dirección Facturación:", selectedCliente.getDireccionFacturacion());
            addDetailRow("Correo Facturación:", selectedCliente.getCorreoFacturacion());
            addDetailRow("Tipo Factura Default:", selectedCliente.getTipoFacturaDefault());
            // Ocultar el panel si no hay cliente seleccionado (o al deseleccionar)
             clientesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
                if (newSelection == null) {
                    detailsPanelContainer.setVisible(false);
                    lblDetailsTitle.setVisible(false);
                }
            });

        } else {
            detailsPanelContainer.setVisible(false);
            lblDetailsTitle.setVisible(false);
        }
    }

    // Método helper para añadir filas al panel de detalles
    private void addDetailRow(String labelText, String valueText) {
        HBox row = new HBox(5); // Espaciado entre etiqueta y valor
        Label label = new Label(labelText);
        // Aplicar estilo directamente o usar clases de estilo si están definidas en el CSS para estos elementos
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #37474F; -fx-pref-width: 150px;"); 
        
        Label value = new Label(valueText != null && !valueText.trim().isEmpty() ? valueText : "N/A");
        value.setStyle("-fx-text-fill: #455A64;");
        value.setWrapText(true); // Para que el texto se ajuste si es largo
        HBox.setHgrow(value, javafx.scene.layout.Priority.ALWAYS); // Para que el valor ocupe el espacio restante

        row.getChildren().addAll(label, value);
        detailsPanelContainer.getChildren().add(row);
    }

    // Logger (opcional pero recomendado)
    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GestionClientesControllerFX.class);

}
