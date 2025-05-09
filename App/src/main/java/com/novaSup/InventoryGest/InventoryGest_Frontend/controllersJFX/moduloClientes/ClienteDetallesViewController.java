package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloClientes;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ClienteFX;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.format.DateTimeFormatter;

public class ClienteDetallesViewController {

    private static final Logger logger = LoggerFactory.getLogger(ClienteDetallesViewController.class);
    private Stage stage;

    // --- CAMPOS FXML --- 
    // Debes declarar aquí los @FXML correspondientes a los Labels y otros controles 
    // que tengas en tu ClienteDetallesView.fxml para mostrar la información.
    // Ejemplo:
    @FXML private Label idClienteLabel;
    @FXML private Label nombreLabel;
    @FXML private Label documentoIdentidadLabel;
    @FXML private Label correoLabel;
    @FXML private Label celularLabel;
    @FXML private Label direccionLabel;
    @FXML private Label puntosFidelidadLabel;
    @FXML private Label limiteCreditoLabel;
    @FXML private Label totalCompradoLabel;
    @FXML private Label ultimaCompraLabel;
    @FXML private Label fechaRegistroLabel;
    @FXML private Label fechaActualizacionLabel;
    @FXML private CheckBox activoCheckBox; // Usar CheckBox no editable para booleanos

    // Datos de Facturación
    @FXML private CheckBox requiereFacturaDefaultCheckBox;
    @FXML private Label razonSocialLabel;
    @FXML private Label identificacionFiscalLabel;
    @FXML private Label direccionFacturacionLabel;
    @FXML private Label correoFacturacionLabel;
    @FXML private Label tipoFacturaDefaultLabel;

    // Contenedor si los detalles se añaden dinámicamente (alternativa a labels individuales)
    // @FXML private VBox detailsContainer; 

    private ClienteFX clienteMostrado;

    // Método opcional si necesitas acceso al Stage (ej. para cerrarlo desde el controlador)
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setCliente(ClienteFX cliente) {
        this.clienteMostrado = cliente;
        if (clienteMostrado != null) {
            poblarDetalles();
        }
    }

    private void poblarDetalles() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

        try {
            idClienteLabel.setText(clienteMostrado.getIdCliente() != null ? clienteMostrado.getIdCliente().toString() : "N/A");
            nombreLabel.setText(formatValue(clienteMostrado.getNombre()));
            documentoIdentidadLabel.setText(formatValue(clienteMostrado.getDocumentoIdentidad()));
            correoLabel.setText(formatValue(clienteMostrado.getCorreo()));
            celularLabel.setText(formatValue(clienteMostrado.getCelular()));
            direccionLabel.setText(formatValue(clienteMostrado.getDireccion()));
            puntosFidelidadLabel.setText(clienteMostrado.getPuntosFidelidad() != null ? clienteMostrado.getPuntosFidelidad().toString() : "N/A");
            limiteCreditoLabel.setText(clienteMostrado.getLimiteCredito() != null ? clienteMostrado.getLimiteCredito().toPlainString() + " $" : "N/A");
            totalCompradoLabel.setText(clienteMostrado.getTotalComprado() != null ? clienteMostrado.getTotalComprado().toPlainString() + " $" : "N/A");
            
            ultimaCompraLabel.setText(clienteMostrado.getUltimaCompra() != null ? clienteMostrado.getUltimaCompra().format(formatter) : "N/A");
            fechaRegistroLabel.setText(clienteMostrado.getFechaRegistro() != null ? clienteMostrado.getFechaRegistro().format(formatter) : "N/A");
            fechaActualizacionLabel.setText(clienteMostrado.getFechaActualizacion() != null ? clienteMostrado.getFechaActualizacion().format(formatter) : "N/A");
            
            activoCheckBox.setSelected(clienteMostrado.isActivo());
            activoCheckBox.setDisable(true); // Hacerlo no editable
            activoCheckBox.setStyle("-fx-opacity: 1;");

            // Datos de Facturación
            requiereFacturaDefaultCheckBox.setSelected(clienteMostrado.isRequiereFacturaDefault());
            requiereFacturaDefaultCheckBox.setDisable(true);
            requiereFacturaDefaultCheckBox.setStyle("-fx-opacity: 1;");
            razonSocialLabel.setText(formatValue(clienteMostrado.getRazonSocial()));
            identificacionFiscalLabel.setText(formatValue(clienteMostrado.getIdentificacionFiscal()));
            direccionFacturacionLabel.setText(formatValue(clienteMostrado.getDireccionFacturacion()));
            correoFacturacionLabel.setText(formatValue(clienteMostrado.getCorreoFacturacion()));
            tipoFacturaDefaultLabel.setText(formatValue(clienteMostrado.getTipoFacturaDefault()));

        } catch (NullPointerException npe) {
            logger.error("Error al poblar detalles: uno o más controles FXML no están inyectados. Verifica los fx:id en ClienteDetallesView.fxml", npe);
            // Podrías mostrar una alerta al usuario aquí si es crítico
        } catch (Exception e) {
            logger.error("Error inesperado al poblar detalles del cliente: ", e);
        }
    }

    private String formatValue(String value) {
        return (value != null && !value.trim().isEmpty()) ? value : "N/A";
    }

    @FXML
    private void handleCerrar() {
        if (stage != null) {
            stage.close();
        } else {
            // Si el stage no fue inyectado, intenta obtenerlo del botón si existe un botón "cerrar"
            // Por ejemplo, si tienes un @FXML Button btnCerrar:
            // Stage currentStage = (Stage) btnCerrar.getScene().getWindow();
            // currentStage.close();
            logger.warn("Stage no fue inyectado en ClienteDetallesViewController, no se puede cerrar mediante handleCerrar sin un control de referencia.");
        }
    }
} 