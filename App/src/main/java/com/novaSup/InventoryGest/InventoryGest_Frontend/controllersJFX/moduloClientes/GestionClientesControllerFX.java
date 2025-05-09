package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloClientes;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ClienteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IClienteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML; // Importar PathsFXML
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class GestionClientesControllerFX implements Initializable {

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
    // Los Labels individuales para detalles (lblDetailNombre, etc.) se manejan dinámicamente.

    private final IClienteService clienteService;
    private ObservableList<ClienteFX> masterData = FXCollections.observableArrayList();
    private FilteredList<ClienteFX> filteredData;
    private SortedList<ClienteFX> sortedData;

    public GestionClientesControllerFX(IClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configurarTabla();
        configurarFiltros();
        cargarClientes();
        configurarBotones();
        // Inicialmente ocultar el panel de detalles del lado derecho
        detailsPanelContainer.setVisible(false);
        lblDetailsTitle.setVisible(false);
        // Listener para limpiar/ocultar panel de detalles si no hay selección
        clientesTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection == null) {
                detailsPanelContainer.setVisible(false);
                lblDetailsTitle.setVisible(false);
                detailsPanelContainer.getChildren().clear(); // Limpiar contenido
            } else {
                // Si se desea que el panel derecho se actualice con la selección (además del botón "Ver Detalles")
                // se puede llamar a un método similar a handleViewDetails aquí, pero solo para el panel.
                 // por ahora, el panel derecho solo se llena explícitamente con el botón.
            }
        });
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colDocumentoIdentidad.setCellValueFactory(new PropertyValueFactory<>("documentoIdentidad"));
        colIdentificacionFiscal.setCellValueFactory(new PropertyValueFactory<>("identificacionFiscal"));
        colCelular.setCellValueFactory(new PropertyValueFactory<>("celular"));
        colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        colLimiteCredito.setCellValueFactory(new PropertyValueFactory<>("limiteCredito"));
        colActivo.setCellValueFactory(new PropertyValueFactory<>("activo"));

        colActivo.setCellFactory(column -> new TableCell<ClienteFX, Boolean>() {
            private final CheckBox checkBox = new CheckBox();
            {
                checkBox.setDisable(true);
                checkBox.setStyle("-fx-opacity: 1;");
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
                    setAlignment(javafx.geometry.Pos.CENTER);
                }
            }
        });
        colActivo.setStyle("-fx-alignment: CENTER;");

        filteredData = new FilteredList<>(masterData, p -> true);
        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(clientesTable.comparatorProperty());
        clientesTable.setItems(sortedData);
    }

    private void configurarFiltros() {
        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> actualizarFiltro());
        cmbStatusFilter.setItems(FXCollections.observableArrayList("Todos", "Activos", "Inactivos"));
        cmbStatusFilter.setValue("Todos");
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

            boolean matchEstado = true; // Default to true
            if (filtroEstado != null && !filtroEstado.equals("Todos")) {
                if (filtroEstado.equals("Activos")) {
                    matchEstado = cliente.isActivo();
                } else if (filtroEstado.equals("Inactivos")) {
                    matchEstado = !cliente.isActivo();
                }
            }
            return matchTexto && matchEstado;
        });
    }

    private void cargarClientes() {
        try {
            masterData.setAll(clienteService.obtenerTodosLosClientes());
            clientesTable.refresh(); 
        } catch (Exception e) {
            mostrarAlertaError("Error de Carga", "No se pudieron cargar los clientes", "Detalles: " + e.getMessage());
            logger.error("Error al cargar clientes: {}", e.getMessage(), e);
            masterData.clear();
        }
    }

    private void configurarBotones() {
        btnAddCliente.setOnAction(event -> handleAddEditCliente(null)); // null para nuevo cliente
        btnEditCliente.setOnAction(event -> {
            ClienteFX selectedCliente = clientesTable.getSelectionModel().getSelectedItem();
            if (selectedCliente != null) {
                handleAddEditCliente(selectedCliente); // cliente seleccionado para editar
            }
        });
        btnDeleteCliente.setOnAction(event -> handleDeleteCliente());
        btnViewDetails.setOnAction(event -> handleViewDetailsInNewWindow()); // Cambiado para nueva ventana

        btnEditCliente.disableProperty().bind(clientesTable.getSelectionModel().selectedItemProperty().isNull());
        btnDeleteCliente.disableProperty().bind(clientesTable.getSelectionModel().selectedItemProperty().isNull());
        btnViewDetails.disableProperty().bind(clientesTable.getSelectionModel().selectedItemProperty().isNull());
    }

    private void handleAddEditCliente(ClienteFX cliente) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.DIALOG_ADD_EDIT_CLIENTE));
            Parent root = loader.load();

            AddEditClienteDialogController controller = loader.getController();
            controller.setClienteParaOperacion(cliente, clienteService); // Pasa el cliente y el servicio

            Stage dialogStage = new Stage();
            dialogStage.setTitle(cliente == null ? "Agregar Nuevo Cliente" : "Editar Cliente");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            Window owner = btnAddCliente.getScene().getWindow(); // o cualquier nodo de la escena actual
            if (owner != null) { 
                dialogStage.initOwner(owner);
            }
            dialogStage.setScene(new Scene(root));
            
            controller.setDialogStage(dialogStage); // Para que el diálogo pueda cerrarse a sí mismo
            controller.setClienteParaOperacion(cliente, clienteService); // Método unificado para pasar datos

            dialogStage.showAndWait(); // Espera a que el diálogo se cierre

            if (controller.isGuardadoExitosamente()) {
                cargarClientes(); // Recargar datos si se guardó
                // Opcionalmente, seleccionar el cliente recién añadido/editado en la tabla
            }

        } catch (IOException e) {
            logger.error("Error al abrir diálogo de agregar/editar cliente: {}", e.getMessage(), e);
            mostrarAlertaError("Error de UI", "No se pudo abrir la ventana de edición.", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCliente() {
        ClienteFX selectedCliente = clientesTable.getSelectionModel().getSelectedItem();
        if (selectedCliente != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Confirmar Eliminación");
            confirmacion.setHeaderText("Eliminar Cliente: " + selectedCliente.getNombre());
            confirmacion.setContentText("¿Está seguro de que desea eliminar a este cliente?\nEsta acción no se puede deshacer.");
            confirmacion.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    try {
                        boolean eliminado = clienteService.eliminarCliente(selectedCliente.getIdCliente());
                        if (eliminado) {
                            cargarClientes();
                            detailsPanelContainer.setVisible(false); // Ocultar panel si el cliente eliminado estaba mostrándose
                            lblDetailsTitle.setVisible(false);
                        } else {
                            mostrarAlertaError("Eliminación Fallida", "No se pudo eliminar el cliente.", "El cliente no fue encontrado o ya había sido eliminado.");
                        }
                    } catch (Exception e) {
                        mostrarAlertaError("Error al Eliminar", "Ocurrió un error.", e.getMessage());
                        logger.error("Error al eliminar cliente: {}", e.getMessage(), e);
                    }
                }
            });
        }
    }
    
    @FXML
    private void handleViewDetailsInPanel() { // Muestra detalles en el panel derecho
        ClienteFX selectedCliente = clientesTable.getSelectionModel().getSelectedItem();
        if (selectedCliente != null) {
            detailsPanelContainer.getChildren().clear(); 
            detailsPanelContainer.setVisible(true);
            lblDetailsTitle.setVisible(true);

            Label title = new Label("Detalles del Cliente (Panel):");
            title.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding-bottom: 8px;");
            detailsPanelContainer.getChildren().add(title);

            populateDetailsInPanel(selectedCliente);

        } else {
            detailsPanelContainer.setVisible(false);
            lblDetailsTitle.setVisible(false);
        }
    }

    private void populateDetailsInPanel(ClienteFX cliente) {
        addDetailRow("ID Cliente:", String.valueOf(cliente.getIdCliente()));
        addDetailRow("Nombre:", cliente.getNombre());
        addDetailRow("Doc. Identidad:", cliente.getDocumentoIdentidad());
        addDetailRow("ID Fiscal:", cliente.getIdentificacionFiscal());
        addDetailRow("Correo:", cliente.getCorreo());
        addDetailRow("Celular:", cliente.getCelular());
        addDetailRow("Dirección:", cliente.getDireccion());
        addDetailRow("Activo:", cliente.isActivo() ? "Sí" : "No");
        addDetailRow("Límite Crédito:", cliente.getLimiteCredito() != null ? cliente.getLimiteCredito().toString() + " $" : "N/A");
        addDetailRow("Total Comprado:", cliente.getTotalComprado() != null ? cliente.getTotalComprado().toString() + " $" : "N/A");
        addDetailRow("Puntos Fidelidad:", cliente.getPuntosFidelidad() != null ? String.valueOf(cliente.getPuntosFidelidad()) : "N/A");
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        if (cliente.getUltimaCompra() != null) {
            addDetailRow("Última Compra:", cliente.getUltimaCompra().format(formatter));
        }
        if (cliente.getFechaRegistro() != null) {
            addDetailRow("Fecha Registro:", cliente.getFechaRegistro().format(formatter));
        }
        if (cliente.getFechaActualizacion() != null) {
            addDetailRow("Fecha Actualización:", cliente.getFechaActualizacion().format(formatter));
        }
        addDetailRow("Requiere Factura:", cliente.isRequiereFacturaDefault() ? "Sí" : "No");
        addDetailRow("Razón Social:", cliente.getRazonSocial());
        addDetailRow("Dir. Facturación:", cliente.getDireccionFacturacion());
        addDetailRow("Correo Facturación:", cliente.getCorreoFacturacion());
        addDetailRow("Tipo Factura:", cliente.getTipoFacturaDefault());
    }

    @FXML
    private void handleViewDetailsInNewWindow() {
        ClienteFX selectedCliente = clientesTable.getSelectionModel().getSelectedItem();
        if (selectedCliente == null) {
            mostrarAlertaInformacion("Sin Selección", "No hay cliente seleccionado", "Por favor, seleccione un cliente de la tabla para ver sus detalles.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.VIEW_CLIENTE_DETALLES));
            Parent root = loader.load();

            ClienteDetallesViewController controller = loader.getController();
            // Es importante pasar el Stage al controlador de detalles si este tiene un botón "Cerrar" propio que dependa del Stage.
            // Y también para que el propio controlador de detalles pueda gestionar su cierre si es necesario.
            
            Stage detailsStage = new Stage();
            controller.setStage(detailsStage); // Pasar el Stage al controlador de detalles
            controller.setCliente(selectedCliente);

            detailsStage.setTitle("Detalles del Cliente: " + selectedCliente.getNombre());
            detailsStage.initModality(Modality.WINDOW_MODAL);
            Window owner = btnViewDetails.getScene().getWindow();
            if (owner != null) { 
                detailsStage.initOwner(owner);
            }
            detailsStage.setScene(new Scene(root));
            
            // Configurar para que la ventana se abra maximizada
            detailsStage.setMaximized(true);

            detailsStage.showAndWait();

        } catch (IOException e) {
            logger.error("Error al abrir la vista de detalles del cliente: {}", e.getMessage(), e);
            mostrarAlertaError("Error de UI", "No se pudo abrir la ventana de detalles.", e.getMessage());
        } catch (Exception e) { // Captura genérica por si el controlador no es el esperado
            logger.error("Error al configurar el controlador de detalles: {}", e.getMessage(), e);
            mostrarAlertaError("Error Interno", "Ocurrió un error al preparar la vista de detalles.", e.getMessage());
        }
    }

    private void addDetailRow(String labelText, String valueText) {
        HBox row = new HBox(5);
        Label label = new Label(labelText);
        label.setStyle("-fx-font-weight: bold; -fx-text-fill: #37474F; -fx-pref-width: 150px; -fx-min-width: 150px;");
        
        Label value = new Label(valueText != null && !valueText.trim().isEmpty() ? valueText : "N/A");
        value.setStyle("-fx-text-fill: #455A64;");
        value.setWrapText(true);
        HBox.setHgrow(value, javafx.scene.layout.Priority.ALWAYS);

        row.getChildren().addAll(label, value);
        detailsPanelContainer.getChildren().add(row);
    }

    private void mostrarAlertaError(String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private void mostrarAlertaInformacion(String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        alert.showAndWait();
    }

    private static final org.slf4j.Logger logger = org.slf4j.LoggerFactory.getLogger(GestionClientesControllerFX.class);
}
