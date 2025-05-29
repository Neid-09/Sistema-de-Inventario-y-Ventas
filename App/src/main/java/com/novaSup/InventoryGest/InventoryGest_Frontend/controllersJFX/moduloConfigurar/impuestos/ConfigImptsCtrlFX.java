package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.moduloConfigurar.impuestos;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TipoImpuestoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICategoriaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IImpuestoAplicableServiceFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ITasaImpuestoServiceFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ITipoImpuestoService;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TabPane;
import javafx.scene.layout.AnchorPane;

public class ConfigImptsCtrlFX {

    @FXML private TabPane tabPaneImpuestos;

    @FXML private AnchorPane tipoImpuestoTabContent;
    @FXML private TipoImptsCtrlFX tipoImpuestoTabContentController;

    @FXML private AnchorPane tasaImpuestoTabContent;
    @FXML private TasaImptsCtrlFX tasaImpuestoTabContentController;

    @FXML private AnchorPane impuestoAplicableTabContent;
    @FXML private ImptsAplicCtrlFX impuestoAplicableTabContentController;

    private final ITipoImpuestoService tipoImpuestoService;
    private final ITasaImpuestoServiceFX tasaImpuestoService;
    private final IImpuestoAplicableServiceFX impuestoAplicableService;
    private final IProductoService productoService;
    private final ICategoriaService categoriaService;


    public ConfigImptsCtrlFX(ITipoImpuestoService tipoImpuestoService, ITasaImpuestoServiceFX tasaImpuestoService, IImpuestoAplicableServiceFX impuestoAplicableService, IProductoService productoService, ICategoriaService categoriaService) {
        this.tipoImpuestoService = tipoImpuestoService;
        this.tasaImpuestoService = tasaImpuestoService;
        this.impuestoAplicableService = impuestoAplicableService;
        this.productoService = productoService;
        this.categoriaService = categoriaService;
    }

    @FXML
    public void initialize() {
        System.out.println("ConfigImptsCtrlFX (Coordinador Principal) inicializado.");

        if (tipoImpuestoTabContentController == null) {
            System.err.println("Error Crítico: tipoImpuestoTabContentController no inyectado. Verifica fx:id='tipoImpuestoTabContent' en FXML.");
            return;
        }
        if (tasaImpuestoTabContentController == null) {
            System.err.println("Error Crítico: tasaImpuestoTabContentController no inyectado. Verifica fx:id='tasaImpuestoTabContent' en FXML.");
            return;
        }

        tipoImpuestoTabContentController.setServicio(tipoImpuestoService);
        tasaImpuestoTabContentController.setServicio(tasaImpuestoService);
        impuestoAplicableTabContentController.setServicios(impuestoAplicableService, tasaImpuestoService, productoService, categoriaService);

        setupTipoTasaCommunication();

        System.out.println("Servicios inyectados y comunicación configurada.");
    }

    private void setupTipoTasaCommunication() {
        ObservableList<TipoImpuestoFX> tiposActivos = tipoImpuestoTabContentController.getTiposImpuestoActivos();

        tasaImpuestoTabContentController.setTiposImpuestoActivos(tiposActivos);

        tiposActivos.addListener((ListChangeListener<TipoImpuestoFX>) change -> {
            System.out.println("Detectado cambio en tiposActivos, actualizando TasaImpuestoTabController...");
            tasaImpuestoTabContentController.setTiposImpuestoActivos(tiposActivos);
        });
    }
} 