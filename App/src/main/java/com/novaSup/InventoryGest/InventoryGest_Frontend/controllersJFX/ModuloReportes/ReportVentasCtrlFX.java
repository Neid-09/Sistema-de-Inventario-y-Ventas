package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.ModuloReportes;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ReportVentasCtrlFX {

    @FXML
    private TableColumn<VentaFX, Integer> colCantidad; // Asumiendo que VentaFX tendrá una propiedad para cantidad total o se adaptará

    @FXML
    private TableColumn<VentaFX, String> colCliente;

    @FXML
    private TableColumn<VentaFX, LocalDateTime> colFecha;

    @FXML
    private TableColumn<VentaFX, BigDecimal> colImpuestos; // Se adaptará para mostrar un valor representativo

    @FXML
    private TableColumn<VentaFX, String> colNumFactura;

    @FXML
    private TableColumn<VentaFX, BigDecimal> colSubtotal; // Asumiendo que VentaFX tendrá esta propiedad o se usará 'total'

    @FXML
    private TableColumn<VentaFX, String> colTipoPago;

    @FXML
    private TableColumn<VentaFX, BigDecimal> colTotal;

    @FXML
    private TableColumn<VentaFX, String> colVendedor;

    @FXML
    private TableColumn<VentaFX, Void> colAcciones;

    @FXML
    private TableView<VentaFX> tablaVentas;

    @FXML
    private TextField txtGanancia;

    @FXML
    private TextField txtImpuestos;

    @FXML
    private TextField txtSubtotal;

    @FXML
    private TextField txtTotal;

    private IVentaSerivice ventaService;
    private ObservableList<VentaFX> ventasData = FXCollections.observableArrayList();

    public void setService(IVentaSerivice ventaService) {
        this.ventaService = ventaService;
        cargarVentas(); // Cargar ventas cuando el servicio esté disponible
    }

    @FXML
    private void initialize() {
        configurarColumnas();
        // No llamamos a cargarVentas() aquí directamente si depende de que setService sea llamado primero.
        // Si el servicio puede ser nulo inicialmente y luego establecido, cargarVentas() en setService es mejor.
    }

    private void configurarColumnas() {
        colNumFactura.setCellValueFactory(new PropertyValueFactory<>("numeroVenta"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("nombreCliente"));
        colVendedor.setCellValueFactory(new PropertyValueFactory<>("nombreVendedor"));
        colTipoPago.setCellValueFactory(new PropertyValueFactory<>("tipoPago"));
        // Para colSubtotal, colImpuestos y colCantidad, se necesitaría que VentaFX tenga estas propiedades.
        // Provisionalmente, usaré 'total' para colTotal. Las otras requerirán ajustes en VentaFX o lógica adicional.
        colTotal.setCellValueFactory(new PropertyValueFactory<>("total"));
        colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadTotalProductos")); // Usar la nueva propiedad

        colAcciones.setCellFactory(param -> new TableCell<>() {
            private final Button btnDetalles = new Button("Detalles");

            {
                btnDetalles.setOnAction(event -> {
                    VentaFX venta = getTableView().getItems().get(getIndex());
                    mostrarDetallesVenta(venta);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(btnDetalles);
                }
            }
        });

        // Ejemplo para colImpuestos si VentaFX tuviera una propiedad booleana 'aplicarImpuestos'
        // TableColumn<VentaFX, String> colImpuestosText = new TableColumn<>("Impuestos Aplicados");
        // colImpuestosText.setCellValueFactory(cellData -> {
        //     boolean aplica = cellData.getValue().isAplicarImpuestos();
        //     return new SimpleStringProperty(aplica ? "Sí" : "No");
        // });
        // Adaptar colImpuestos, colSubtotal, colCantidad según las propiedades reales de VentaFX
        // Por ahora, las dejaré vinculadas a propiedades que podrían no existir o necesitar formato.
        colSubtotal.setCellValueFactory(new PropertyValueFactory<>("subtotal")); 
        colImpuestos.setCellValueFactory(new PropertyValueFactory<>("totalImpuestos")); 
        // colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidadItems")); // Placeholder

        tablaVentas.setItems(ventasData);
    }

    private void cargarVentas() {
        if (ventaService != null) {
            try {
                List<VentaFX> ventas = ventaService.listarVentas();
                ventasData.setAll(ventas);
                // Aquí podrías calcular y mostrar los totales en los TextField si es necesario
                // calcularTotalesGenerales(ventas);
            } catch (Exception e) {
                // Manejar la excepción, por ejemplo, mostrar un diálogo de error
                e.printStackTrace(); // Reemplazar con un manejo de errores adecuado para el usuario
                ventasData.clear();
            }
        }
    }
    
    private void mostrarDetallesVenta(VentaFX venta) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/views/ModuloReportes/DetalleVentas.fxml"));
            Parent root = loader.load();

            // Obtener el controlador de la vista de detalles
            DetalleVentasCtrlFX controller = loader.getController();
            // Pasar el objeto VentaFX al controlador de detalles
            controller.initData(venta); 
            
            // Si el controlador de DetalleVentas.fxml necesita el servicio, también se lo puedes pasar:
            // controller.setService(this.ventaService);

            Stage stage = new Stage();
            stage.setTitle("Detalles de Venta: " + venta.getNumeroVenta());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea otras ventanas hasta que esta se cierre
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            // Manejar la excepción, por ejemplo, mostrar un diálogo de error al usuario
            System.err.println("Error al cargar la vista de detalles de venta: " + e.getMessage());
        }
        // Lógica para mostrar los detalles de la venta
        // Esto podría implicar abrir una nueva ventana o un diálogo
        System.out.println("Mostrando detalles para la venta: " + venta.getNumeroVenta());
        // Aquí puedes implementar la navegación a una vista de detalles de venta
        // o mostrar un diálogo con más información.
    }

    // Método de ejemplo para calcular totales, necesitaría implementarse según la lógica de negocio
    /*
    private void calcularTotalesGenerales(List<VentaFX> ventas) {
        BigDecimal subtotalGeneral = BigDecimal.ZERO;
        BigDecimal impuestosGeneral = BigDecimal.ZERO;
        BigDecimal totalGeneral = BigDecimal.ZERO;
        // BigDecimal gananciaGeneral = BigDecimal.ZERO; // Si se calcula

        for (VentaFX venta : ventas) {
            // Asumiendo que VentaFX tiene estos campos o se pueden derivar
            // subtotalGeneral = subtotalGeneral.add(venta.getSubtotal()); // Necesita VentaFX.getSubtotal()
            // impuestosGeneral = impuestosGeneral.add(venta.getImpuestos()); // Necesita VentaFX.getImpuestos()
            totalGeneral = totalGeneral.add(venta.getTotal());
        }

        txtSubtotal.setText(subtotalGeneral.toString());
        txtImpuestos.setText(impuestosGeneral.toString());
        txtTotal.setText(totalGeneral.toString());
        // txtGanancia.setText(gananciaGeneral.toString());
    }
    */

}
