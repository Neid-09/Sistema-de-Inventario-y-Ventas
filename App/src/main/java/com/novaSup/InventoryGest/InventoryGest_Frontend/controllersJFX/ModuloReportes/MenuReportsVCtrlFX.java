package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.ModuloReportes;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.layout.AnchorPane; // Añadido para concordancia con ConfigImptsCtrlFX
//TODO: Optimizar la carga en las pestañas, ya que al iniciar la aplicación se cargan todas las pestañas y no solo la activa.
public class MenuReportsVCtrlFX {

    @FXML
    private Button btnFiltrar;

    @FXML
    private ComboBox<?> comboCategoria;

    @FXML
    private ComboBox<?> comboVendedor;

    @FXML
    private DatePicker fechaFin;

    @FXML
    private DatePicker fechaInicio;

    // Controladores de pestañas inyectados
    @FXML private AnchorPane reportesVentasTabContent;
    @FXML private ReportVentasCtrlFX reportesVentasTabContentController; // Asumiendo que existe ReportesVentasCtrlFX

    @FXML private AnchorPane graficosTabContent;
    @FXML private GraficosCtrlFX graficosTabContentController; // Asumiendo que existe GraficosCtrlFX

    @FXML private AnchorPane resumenTabContent;
    @FXML private ResumenCtrlFX resumenTabContentController; // Asumiendo que existe ResumenCtrlFX

    private final IVentaSerivice ventaService; 
    
    public MenuReportsVCtrlFX(IVentaSerivice ventaService) {
        this.ventaService = ventaService;
    }

    @FXML
    public void initialize() {
        System.out.println("MenuReportsVCtrlFX (Coordinador Principal) inicializado.");

        // Verificar inyección de controladores de pestañas
        if (reportesVentasTabContentController == null) {
            System.err.println("Error Crítico: reportesVentasTabContentController no inyectado. Verifica fx:id='reportesVentasTabContent' en FXML.");
            // Podrías retornar o manejar el error como consideres
        }
/*         if (graficosTabContentController == null) {
            System.err.println("Error Crítico: graficosTabContentController no inyectado. Verifica fx:id='graficosTabContent' en FXML.");
            // Podrías retornar o manejar el error como consideres
        } */
/*         if (resumenTabContentController == null) {
            System.err.println("Error Crítico: resumenTabContentController no inyectado. Verifica fx:id='resumenTabContent' en FXML.");
            // Podrías retornar o manejar el error como consideres
        } */

        // Aquí puedes inyectar servicios a los sub-controladores si es necesario
        // Ejemplo: (Asegúrate de que los métodos setServicio existan en los respectivos controladores)
        if (reportesVentasTabContentController != null) {
             reportesVentasTabContentController.setService(ventaService); 
        }
        if (graficosTabContentController != null) {
            graficosTabContentController.setService(ventaService);
        }
        if (resumenTabContentController != null) {
            resumenTabContentController.setService(ventaService);
        }

        // Aquí puedes configurar la comunicación entre pestañas si es necesario
        // setupTabCommunication();

        System.out.println("Sub-controladores verificados (y servicios inyectados si aplica).");
    }

    // Ejemplo de método para configurar comunicación (si es necesario)
    /*
    private void setupTabCommunication() {
        // Lógica similar a ConfigImptsCtrlFX si las pestañas necesitan compartir datos o reaccionar a cambios en otras
        System.out.println("Configurando comunicación entre pestañas de reportes...");
    }
    */

}
