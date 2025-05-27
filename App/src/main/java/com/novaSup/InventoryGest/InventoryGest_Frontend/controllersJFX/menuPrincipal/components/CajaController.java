package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.components;

import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces.ICajaController;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICajaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CajaResponseFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CajaReporteConsolidadoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.caja.AbrirCajaDialogControllerFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.caja.CerrarCajaDialogControllerFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.utils.PathsFXML;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.Optional;

/**
 * Implementación del controlador delegado para la gestión de caja.
 * Maneja toda la lógica relacionada con el sistema de caja en el menú principal.
 */
public class CajaController implements ICajaController {
    
    private ICajaService cajaService;
    private Button btnCaja;
    private Label lblDineroInicial;
    private Label lblTotalEsperadoCaja;
    private Label lblTotalVentas;
    private Label lblProductosVendidos;
    private AlertCallback alertCallback;
    
    private CajaResponseFX cajaAbiertaActual = null;
    private CajaReporteConsolidadoFX reporteCajaActual = null;
    
    // Formato para mostrar valores monetarios
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.of("es", "CO"));
    
    @Override
    public void inicializar(ICajaService cajaService, Button btnCaja, Label lblDineroInicial,
                           Label lblTotalEsperadoCaja, Label lblTotalVentas, Label lblProductosVendidos,
                           AlertCallback alertCallback) {
        this.cajaService = cajaService;
        this.btnCaja = btnCaja;
        this.lblDineroInicial = lblDineroInicial;
        this.lblTotalEsperadoCaja = lblTotalEsperadoCaja;
        this.lblTotalVentas = lblTotalVentas;
        this.lblProductosVendidos = lblProductosVendidos;
        this.alertCallback = alertCallback;
        
        // Configurar formato de moneda
        currencyFormat.setMaximumFractionDigits(2);
        currencyFormat.setMinimumFractionDigits(2);
        
        // Configurar acción del botón de caja
        if (btnCaja != null) {
            btnCaja.setOnAction(this::handleBotonCaja);
        }
        
        // Cargar información inicial de la caja
        cargarInformacionCajaAbierta();
    }
    
    @Override
    public void cargarInformacionCajaAbierta() {
        Integer idUsuario = LoginServiceImplFX.getIdUsuario();
        if (idUsuario == null) {
            System.err.println("No se pudo obtener ID de usuario para cargar información de caja");
            actualizarLabelsCaja(null, null);
            Platform.runLater(() -> btnCaja.setText("Abrir Caja (Error)"));
            return;
        }
        
        Task<Optional<CajaResponseFX>> getCajaTask = new Task<>() {
            @Override
            protected Optional<CajaResponseFX> call() throws Exception {
                if (cajaService == null) {
                    throw new IllegalStateException("Servicio de caja no disponible.");
                }
                return cajaService.getCajaAbiertaPorUsuario(idUsuario);
            }
        };

        getCajaTask.setOnSucceeded(event -> {
            Optional<CajaResponseFX> cajaOpt = getCajaTask.getValue();
            if (cajaOpt.isPresent()) {
                cajaAbiertaActual = cajaOpt.get();
                System.out.println("Caja abierta encontrada con ID: " + cajaAbiertaActual.getIdCaja());
                // Si hay caja abierta, obtener reporte consolidado
                cargarReporteConsolidado(cajaAbiertaActual.getIdCaja());
                Platform.runLater(() -> btnCaja.setText("Cerrar Caja"));
            } else {
                System.out.println("No hay caja abierta para el usuario.");
                cajaAbiertaActual = null;
                actualizarLabelsCaja(null, null);
                Platform.runLater(() -> btnCaja.setText("Abrir Caja"));
            }
        });

        getCajaTask.setOnFailed(event -> {
            Throwable ex = getCajaTask.getException();
            System.err.println("Error al obtener caja abierta: " + ex.getMessage());
            ex.printStackTrace();
            actualizarLabelsCaja(null, null);
            Platform.runLater(() -> {
                if (alertCallback != null) {
                    alertCallback.mostrarAlerta(Alert.AlertType.ERROR, "Error de Caja", 
                            "No se pudo verificar el estado de la caja: " + ex.getMessage());
                }
                btnCaja.setText("Abrir Caja (Error)");
            });
        });

        Thread thread = new Thread(getCajaTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Obtiene el reporte consolidado para una caja específica y actualiza los labels.
     * @param idCaja ID de la caja para la que obtener el reporte.
     */
    private void cargarReporteConsolidado(Integer idCaja) {
        if (idCaja == null || cajaService == null) {
            actualizarLabelsCaja(null, null);
            return;
        }
        
        Task<Optional<CajaReporteConsolidadoFX>> getReporteTask = new Task<>() {
            @Override
            protected Optional<CajaReporteConsolidadoFX> call() throws Exception {
                return cajaService.getReporteConsolidadoByCajaId(idCaja);
            }
        };

        getReporteTask.setOnSucceeded(event -> {
            Optional<CajaReporteConsolidadoFX> reporteOpt = getReporteTask.getValue();
            Platform.runLater(() -> {
                if (reporteOpt.isPresent()) {
                    CajaReporteConsolidadoFX reporte = reporteOpt.get();
                    reporteCajaActual = reporte; // Guardar referencia
                    System.out.println("Reporte consolidado obtenido.");
                    actualizarLabelsCaja(reporte, cajaAbiertaActual);
                } else {
                    System.out.println("No se encontró reporte consolidado para la caja ID: " + idCaja);
                    actualizarLabelsCaja(null, cajaAbiertaActual);
                }
            });
        });

        getReporteTask.setOnFailed(event -> {
            Throwable ex = getReporteTask.getException();
            System.err.println("Error al obtener reporte consolidado: " + ex.getMessage());
            ex.printStackTrace();
            Platform.runLater(() -> {
                actualizarLabelsCaja(null, cajaAbiertaActual);
                if (alertCallback != null) {
                    alertCallback.mostrarAlerta(Alert.AlertType.ERROR, "Error de Reporte", 
                            "No se pudo obtener el reporte consolidado: " + ex.getMessage());
                }
            });
        });

        Thread thread = new Thread(getReporteTask);
        thread.setDaemon(true);
        thread.start();
    }
    
    /**
     * Actualiza los labels en el footer con la información de la caja y su reporte.
     * @param reporte Reporte consolidado (puede ser null).
     * @param caja Caja abierta (puede ser null).
     */
    private void actualizarLabelsCaja(CajaReporteConsolidadoFX reporte, CajaResponseFX caja) {
        // Asegurarse de que los labels existen antes de actualizar
        if (lblDineroInicial == null || lblTotalEsperadoCaja == null || 
            lblTotalVentas == null || lblProductosVendidos == null) {
            System.err.println("Error: Labels del footer no inicializados en FXML.");
            return;
        }

        if (reporte != null) {
            // Usar los campos del reporte consolidado
            BigDecimal dineroInicial = (reporte.getDineroInicial() != null) ? 
                    reporte.getDineroInicial() : BigDecimal.ZERO;
            BigDecimal totalEsperadoCaja = reporte.getTotalEsperadoCaja() != null ? 
                    reporte.getTotalEsperadoCaja() : BigDecimal.ZERO;
            BigDecimal totalVentas = reporte.getTotalGeneralVentas() != null ? 
                    reporte.getTotalGeneralVentas() : BigDecimal.ZERO;
            int totalProductosVendidos = reporte.getTotalUnidadesVendidas();

            lblDineroInicial.setText(formatCurrency(dineroInicial));
            lblTotalEsperadoCaja.setText(formatCurrency(totalEsperadoCaja));
            lblTotalVentas.setText(formatCurrency(totalVentas));
            lblProductosVendidos.setText(String.valueOf(totalProductosVendidos));

        } else if (caja != null) {
            // Si no hay reporte consolidado pero sí información básica de la caja
            BigDecimal dineroInicial = (caja.getDineroInicial() != null) ? 
                    caja.getDineroInicial() : BigDecimal.ZERO;

            lblDineroInicial.setText(formatCurrency(dineroInicial));
            lblTotalEsperadoCaja.setText(formatCurrency(BigDecimal.ZERO));
            lblTotalVentas.setText(formatCurrency(BigDecimal.ZERO));
            lblProductosVendidos.setText("0");

        } else {
            // Si no hay ninguna información de caja disponible
            lblDineroInicial.setText(formatCurrency(BigDecimal.ZERO));
            lblTotalEsperadoCaja.setText(formatCurrency(BigDecimal.ZERO));
            lblTotalVentas.setText(formatCurrency(BigDecimal.ZERO));
            lblProductosVendidos.setText("0");
        }
    }
    
    /**
     * Formatea un BigDecimal como moneda.
     * @param value El valor a formatear.
     * @return String formateado.
     */
    private String formatCurrency(BigDecimal value) {
        if (value == null) {
            value = BigDecimal.ZERO;
        }
        return currencyFormat.format(value);
    }
    
    @Override
    public void handleBotonCaja(ActionEvent event) {
        if (cajaService == null) {
            if (alertCallback != null) {
                alertCallback.mostrarAlerta(Alert.AlertType.ERROR, "Error", "Servicio de caja no disponible.");
            }
            return;
        }

        if (LoginServiceImplFX.getIdUsuario() == null) {
            if (alertCallback != null) {
                alertCallback.mostrarAlerta(Alert.AlertType.ERROR, "Error de Autenticación", 
                        "No se pudo obtener el ID del usuario logueado. Por favor, inicie sesión.");
            }
            return;
        }
        int idUsuario = LoginServiceImplFX.getIdUsuario();

        if (cajaAbiertaActual == null) {
            // No hay caja abierta, proceder a abrir
            abrirCaja(event, idUsuario);
        } else {
            // Hay una caja abierta, proceder a cerrar
            cerrarCaja(event);
        }
    }
    
    /**
     * Maneja la apertura de caja.
     */
    private void abrirCaja(ActionEvent event, int idUsuario) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.ABRIR_CAJA_DIALOG_FXML));
            Parent root = loader.load();

            AbrirCajaDialogControllerFX controller = loader.getController();
            controller.initData(cajaService, idUsuario, this::cargarInformacionCajaAbierta);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Abrir Caja");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            if (event != null && event.getSource() instanceof Node) {
                dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            } else if (btnCaja != null && btnCaja.getScene() != null) { 
                dialogStage.initOwner(btnCaja.getScene().getWindow());
            }

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            if (alertCallback != null) {
                alertCallback.mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", 
                        "No se pudo cargar el diálogo para abrir caja: " + e.getMessage());
            }
        } catch (Exception e) { 
            e.printStackTrace();
            if (alertCallback != null) {
                alertCallback.mostrarAlerta(Alert.AlertType.ERROR, "Error en Diálogo", 
                        "Ocurrió un error al inicializar el diálogo de abrir caja: " + e.getMessage());
            }
        }
    }
    
    /**
     * Maneja el cierre de caja.
     */
    private void cerrarCaja(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(PathsFXML.CERRAR_CAJA_DIALOG_FXML));
            Parent root = loader.load();

            CerrarCajaDialogControllerFX controller = loader.getController();
            
            BigDecimal totalEsperadoEnCaja;
            if (reporteCajaActual != null && reporteCajaActual.getTotalEsperadoCaja() != null) {
                totalEsperadoEnCaja = reporteCajaActual.getTotalEsperadoCaja();
            } else if (cajaAbiertaActual != null && cajaAbiertaActual.getDineroInicial() != null) { 
                totalEsperadoEnCaja = cajaAbiertaActual.getDineroInicial(); 
            } else {
                totalEsperadoEnCaja = BigDecimal.ZERO;
            }

            controller.initData(cajaService, cajaAbiertaActual.getIdCaja(), totalEsperadoEnCaja, 
                    this::cargarInformacionCajaAbierta);

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Cerrar Caja");
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            
            if (event != null && event.getSource() instanceof Node) {
                dialogStage.initOwner(((Node) event.getSource()).getScene().getWindow());
            } else if (btnCaja != null && btnCaja.getScene() != null) {
                dialogStage.initOwner(btnCaja.getScene().getWindow());
            }

            Scene scene = new Scene(root);
            dialogStage.setScene(scene);
            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            if (alertCallback != null) {
                alertCallback.mostrarAlerta(Alert.AlertType.ERROR, "Error de Carga", 
                        "No se pudo cargar el diálogo para cerrar caja: " + e.getMessage());
            }
        } catch (Exception e) { 
            e.printStackTrace();
            if (alertCallback != null) {
                alertCallback.mostrarAlerta(Alert.AlertType.ERROR, "Error en Diálogo", 
                        "Ocurrió un error al inicializar el diálogo de cerrar caja: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void cleanup() {
        // Limpiar referencias y recursos
        cajaAbiertaActual = null;
        reporteCajaActual = null;
        cajaService = null;
        btnCaja = null;
        lblDineroInicial = null;
        lblTotalEsperadoCaja = null;
        lblTotalVentas = null;
        lblProductosVendidos = null;
        alertCallback = null;
    }
}
