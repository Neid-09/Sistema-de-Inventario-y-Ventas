package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloClientes;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ClienteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IClienteService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;


public class AddEditClienteDialogController {

    private static final Logger logger = LoggerFactory.getLogger(AddEditClienteDialogController.class);

    @FXML private TextField nombreField;
    @FXML private TextField documentoIdentidadField;
    @FXML private TextField correoField;
    @FXML private TextField telefonoField;
    @FXML private TextField direccionField;
    @FXML private TextField limiteCreditoField;
    
    @FXML private TextField identificacionFiscalField;
    @FXML private TextField razonSocialField;
    @FXML private TextField direccionFacturacionField;
    @FXML private TextField correoFacturacionField;
    @FXML private TextField tipoFacturaDefaultField;
    @FXML private CheckBox requiereFacturaDefaultCheckBox;
    @FXML private CheckBox activoCheckBox;

    @FXML private javafx.scene.control.Label dialogTitleLabel;

    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private Stage dialogStage;
    private ClienteFX clienteOperado;
    private IClienteService clienteService;
    private boolean guardadoExitosamente = false;
    private boolean isEditMode = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        if (dialogTitleLabel != null) {
            dialogTitleLabel.setText(isEditMode ? "Editar Cliente" : "Agregar Nuevo Cliente");
        }
    }

    /**
     * Prepara el diálogo para una operación de añadir o editar.
     * @param cliente El cliente a editar, o null si es un nuevo cliente.
     * @param clienteService El servicio para realizar la operación de guardado.
     */
    public void setClienteParaOperacion(ClienteFX cliente, IClienteService clienteService) {
        this.clienteService = clienteService;
        if (cliente == null) { // Modo Añadir
            this.clienteOperado = new ClienteFX();
            this.isEditMode = false;
            if (activoCheckBox != null) activoCheckBox.setSelected(true);
            if (requiereFacturaDefaultCheckBox != null) requiereFacturaDefaultCheckBox.setSelected(false);
            this.clienteOperado.setActivo(true);
            this.clienteOperado.setRequiereFacturaDefault(false);
            if (limiteCreditoField != null) limiteCreditoField.setText("0.00");
            this.clienteOperado.setLimiteCredito(BigDecimal.ZERO);
        } else { // Modo Editar
            this.clienteOperado = cliente;
            this.isEditMode = true;
            poblarCampos();
        }
    }

    private void poblarCampos() {
        if (clienteOperado == null) return;

        nombreField.setText(clienteOperado.getNombre());
        documentoIdentidadField.setText(clienteOperado.getDocumentoIdentidad());
        correoField.setText(clienteOperado.getCorreo());
        telefonoField.setText(clienteOperado.getCelular());
        direccionField.setText(clienteOperado.getDireccion());
        limiteCreditoField.setText(clienteOperado.getLimiteCredito() != null ? clienteOperado.getLimiteCredito().toPlainString() : "0.00");
        
        if (identificacionFiscalField != null) identificacionFiscalField.setText(clienteOperado.getIdentificacionFiscal());
        if (razonSocialField != null) razonSocialField.setText(clienteOperado.getRazonSocial());
        if (direccionFacturacionField != null) direccionFacturacionField.setText(clienteOperado.getDireccionFacturacion());
        if (correoFacturacionField != null) correoFacturacionField.setText(clienteOperado.getCorreoFacturacion());
        if (tipoFacturaDefaultField != null) tipoFacturaDefaultField.setText(clienteOperado.getTipoFacturaDefault());
        
        if (requiereFacturaDefaultCheckBox != null) requiereFacturaDefaultCheckBox.setSelected(clienteOperado.isRequiereFacturaDefault());
        if (activoCheckBox != null) activoCheckBox.setSelected(clienteOperado.isActivo());
    }

    public boolean isGuardadoExitosamente() {
        return guardadoExitosamente;
    }

    public ClienteFX getClienteOperado() {
        return clienteOperado;
    }

    /**
     * Guarda el cliente actual. Este método puede ser llamado desde otros controladores.
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    public boolean guardarCliente() {
        return handleGuardar();
    }
    
    /**
     * Maneja el evento de guardar cliente desde la interfaz de usuario.
     * @return true si el guardado fue exitoso, false en caso contrario
     */
    @FXML
    public boolean handleGuardar() {
        if (!validarCampos()) {
            return false;
        }

        try {
            clienteOperado.setNombre(nombreField.getText());
            clienteOperado.setDocumentoIdentidad(documentoIdentidadField.getText());
            clienteOperado.setCorreo(correoField.getText());
            clienteOperado.setCelular(telefonoField.getText());
            clienteOperado.setDireccion(direccionField.getText());
            
            try {
                String limiteCreditoStr = limiteCreditoField.getText().replace(",", ".");
                clienteOperado.setLimiteCredito(new BigDecimal(limiteCreditoStr.isEmpty() ? "0" : limiteCreditoStr));
            } catch (NumberFormatException e) {
                mostrarAlertaError("Entrada Inválida", "Límite de Crédito Inválido", "Por favor, ingrese un número válido para el límite de crédito (ej: 1500.50).");
                return false;
            }

            if (identificacionFiscalField != null) clienteOperado.setIdentificacionFiscal(identificacionFiscalField.getText());
            if (razonSocialField != null) clienteOperado.setRazonSocial(razonSocialField.getText());
            if (direccionFacturacionField != null) clienteOperado.setDireccionFacturacion(direccionFacturacionField.getText());
            if (correoFacturacionField != null) clienteOperado.setCorreoFacturacion(correoFacturacionField.getText());
            if (tipoFacturaDefaultField != null) clienteOperado.setTipoFacturaDefault(tipoFacturaDefaultField.getText());
            
            if (requiereFacturaDefaultCheckBox != null) clienteOperado.setRequiereFacturaDefault(requiereFacturaDefaultCheckBox.isSelected());
            if (activoCheckBox != null) clienteOperado.setActivo(activoCheckBox.isSelected());

            ClienteFX clienteGuardado = clienteService.guardarOActualizarCliente(this.clienteOperado);
            this.clienteOperado = clienteGuardado;
            guardadoExitosamente = true;
            if (dialogStage != null) dialogStage.close();
            return true;

        } catch (Exception e) {
            logger.error("Error al guardar cliente: {}", e.getMessage(), e);
            mostrarAlertaError("Error al Guardar", "No se pudo guardar el cliente.", "Detalles: " + e.getMessage());
            guardadoExitosamente = false;
            return false;
        }
    }

    private boolean validarCampos() {
        StringBuilder errorMessages = new StringBuilder();
        
        if (nombreField.getText() == null || nombreField.getText().trim().isEmpty()) {
            errorMessages.append("El nombre completo es obligatorio.\n");
        }
        if (documentoIdentidadField.getText() == null || documentoIdentidadField.getText().trim().isEmpty()) {
            errorMessages.append("El documento de identidad es obligatorio.\n");
        }
        String correo = correoField.getText();
        if (correo != null && !correo.trim().isEmpty() && !correo.matches("[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}")) {
            errorMessages.append("El formato del correo electrónico no es válido.\n");
        }

        String limiteCreditoStr = limiteCreditoField.getText().replace(",", ".");
        if (limiteCreditoStr != null && !limiteCreditoStr.trim().isEmpty()) {
            try {
                new BigDecimal(limiteCreditoStr);
            } catch (NumberFormatException e) {
                errorMessages.append("El límite de crédito debe ser un número válido (ej: 1500.50).\n");
            }
        } else {
            // Si se permite vacío, se podría interpretar como 0 o dejarlo null según la lógica de negocio.
            // Aquí se podría setear a "0.00" si se desea obligar a tener un valor.
            // limiteCreditoField.setText("0.00"); 
        }

        // Puedes añadir más validaciones para los otros campos (longitud, formato específico, etc.)
        // Ejemplo:
        // if (identificacionFiscalField.getText() != null && identificacionFiscalField.getText().trim().length() > 20) {
        //     errorMessages.append("La identificación fiscal no debe exceder los 20 caracteres.\n");
        // }

        if (errorMessages.length() == 0) {
            return true;
        } else {
            mostrarAlertaError("Campos Inválidos", "Por favor, corrija los siguientes errores:", errorMessages.toString());
            return false;
        }
    }

    @FXML
    private void handleCancelar() {
        if (dialogStage != null) dialogStage.close();
    }

    private void mostrarAlertaError(String titulo, String cabecera, String contenido) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(cabecera);
        alert.setContentText(contenido);
        if (dialogStage != null) {
            alert.initOwner(dialogStage);
        }
        alert.showAndWait();
    }
} 