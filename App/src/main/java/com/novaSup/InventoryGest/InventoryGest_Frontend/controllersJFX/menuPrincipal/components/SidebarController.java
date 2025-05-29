package com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.components;

import com.novaSup.InventoryGest.InventoryGest_Frontend.controllersJFX.menuPrincipal.interfaces.ISidebarController;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Controlador para la gestión de la barra lateral del menú principal.
 * Maneja el estado, animaciones y eventos de la sidebar.
 */
public class SidebarController implements ISidebarController {
      // Referencias a componentes FXML (inyectadas desde MenuPrincipalController)
    private VBox sidePanelVBox;
    private ImageView toggleIcon;
    private Button btnInicio;
    
    // Labels de los botones del sidebar
    private Label lblInicio;
    private Label lblVender;
    private Label lblReporteVentas;
    private Label lblInventario;
    private Label lblConsulta;
    private Label lblCreditoClientes;
    private Label lblMasVendido;
    private Label lblEntradasSalidas;
    private Label lblGarantiasServicios;
    private Label lblConfigurar;
    
    // Estado y configuración
    private boolean isSidebarExpanded = false;
    private Button currentActiveButton = null;
    
    // Configuración de dimensiones
    private final double collapsedWidth = 60.0;
    private final double expandedWidth = 200.0;
    
    // Animaciones
    private Timeline expandTimeline;
    private Timeline collapseTimeline;
    
    // Iconos para el botón toggle
    private final Image iconCollapse = new Image(getClass().getResourceAsStream("/img/menuPrincipal/flecha-izquierda.png"));
    private final Image iconExpand = new Image(getClass().getResourceAsStream("/img/menuPrincipal/flecha-derecha.png"));
    
    // Clases de estilo para botones activos
    private final String styleClassModuleActive = "module-button-active";
    private final String styleClassInicioActive = "inicio-button-active";
      /**
     * Constructor que recibe las referencias a los componentes FXML.
     */
    public SidebarController(VBox sidePanelVBox, ImageView toggleIcon, 
                           Button btnInicio, Label lblInicio, Label lblVender, Label lblReporteVentas,
                           Label lblInventario, Label lblConsulta, Label lblCreditoClientes, 
                           Label lblMasVendido, Label lblEntradasSalidas, Label lblGarantiasServicios, 
                           Label lblConfigurar) {
        this.sidePanelVBox = sidePanelVBox;
        this.toggleIcon = toggleIcon;
        this.btnInicio = btnInicio;
        this.lblInicio = lblInicio;
        this.lblVender = lblVender;
        this.lblReporteVentas = lblReporteVentas;
        this.lblInventario = lblInventario;
        this.lblConsulta = lblConsulta;
        this.lblCreditoClientes = lblCreditoClientes;
        this.lblMasVendido = lblMasVendido;
        this.lblEntradasSalidas = lblEntradasSalidas;
        this.lblGarantiasServicios = lblGarantiasServicios;
        this.lblConfigurar = lblConfigurar;
    }
    
    @Override
    public void initialize() {
        setupAnimations();
        setSidebarState(false, false);
        setActiveButton(btnInicio); // Marcar el botón de inicio como activo por defecto
    }
    
    /**
     * Configura las animaciones para expandir y colapsar la barra lateral.
     */
    private void setupAnimations() {
        // Animación para expandir la barra lateral
        expandTimeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(sidePanelVBox.prefWidthProperty(), expandedWidth, Interpolator.EASE_BOTH)
                )
        );
        expandTimeline.setOnFinished(event -> {
            setLabelVisibility(true);
            isSidebarExpanded = true;
            updateToggleIcon();
        });

        // Animación para colapsar la barra lateral
        collapseTimeline = new Timeline(
                new KeyFrame(Duration.millis(200),
                        new KeyValue(sidePanelVBox.prefWidthProperty(), collapsedWidth, Interpolator.EASE_BOTH)
                )
        );
        collapseTimeline.getKeyFrames().add(0, new KeyFrame(Duration.ZERO, event -> {
            if (!isSidebarExpanded) { // Solo ocultar labels si se está colapsando
                setLabelVisibility(false);
            }
        }));
        collapseTimeline.setOnFinished(event -> {
            if (!isSidebarExpanded) { // Asegurar que los labels estén ocultos si el estado es colapsado
                setLabelVisibility(false);
            }
            updateToggleIcon();
        });
    }
    
    @Override
    public void setSidebarState(boolean expand, boolean animate) {
        isSidebarExpanded = expand; // Actualizar estado primero
        updateToggleIcon(); // Actualizar icono basado en el nuevo estado

        if (animate) {
            if (expand) {
                collapseTimeline.stop();
                expandTimeline.playFromStart();
            } else {
                expandTimeline.stop();
                collapseTimeline.playFromStart();
            }
        } else {
            // Establecer inmediatamente sin animación
            sidePanelVBox.setPrefWidth(expand ? expandedWidth : collapsedWidth);
            setLabelVisibility(expand);
        }
    }
    
    /**
     * Establece la visibilidad de los labels de los botones.
     * @param visible true para mostrar, false para ocultar
     */
    private void setLabelVisibility(boolean visible) {
        // Solo cambiar visibilidad si el estado lo requiere
        if (visible == isSidebarExpanded) {
            // Mostrar/ocultar todos los labels de módulos basado en el parámetro 'visible'
            if (lblInicio != null) { lblInicio.setVisible(visible); lblInicio.setManaged(visible); }
            if (lblVender != null) { lblVender.setVisible(visible); lblVender.setManaged(visible); }
            if (lblReporteVentas != null) { lblReporteVentas.setVisible(visible); lblReporteVentas.setManaged(visible); }
            if (lblInventario != null) { lblInventario.setVisible(visible); lblInventario.setManaged(visible); }
            if (lblConsulta != null) { lblConsulta.setVisible(visible); lblConsulta.setManaged(visible); }
            if (lblCreditoClientes != null) { lblCreditoClientes.setVisible(visible); lblCreditoClientes.setManaged(visible); }
            if (lblMasVendido != null) { lblMasVendido.setVisible(visible); lblMasVendido.setManaged(visible); }
            if (lblEntradasSalidas != null) { lblEntradasSalidas.setVisible(visible); lblEntradasSalidas.setManaged(visible); }
            if (lblGarantiasServicios != null) { lblGarantiasServicios.setVisible(visible); lblGarantiasServicios.setManaged(visible); }
            if (lblConfigurar != null) { lblConfigurar.setVisible(visible); lblConfigurar.setManaged(visible); }
        }
    }
    
    /**
     * Actualiza el icono del botón toggle según el estado actual.
     */
    private void updateToggleIcon() {
        if (toggleIcon != null) {
            toggleIcon.setImage(isSidebarExpanded ? iconCollapse : iconExpand);
        }
    }
    
    @Override
    public void setActiveButton(Button button) {
        if (currentActiveButton != null) {
            // Remover clase activa del botón anterior
            currentActiveButton.getStyleClass().remove(styleClassModuleActive);
            currentActiveButton.getStyleClass().remove(styleClassInicioActive);
        }

        currentActiveButton = button;

        if (currentActiveButton != null) {
            // Agregar la clase activa apropiada al nuevo botón
            if (currentActiveButton == btnInicio) {
                currentActiveButton.getStyleClass().add(styleClassInicioActive);
            } else {
                currentActiveButton.getStyleClass().add(styleClassModuleActive);
            }
        }
    }
    
    @Override
    public boolean isSidebarExpanded() {
        return isSidebarExpanded;
    }
    
    @Override
    public Button getCurrentActiveButton() {
        return currentActiveButton;
    }
    
    @Override
    public void handleToggleSidebar(ActionEvent event) {
        setSidebarState(!isSidebarExpanded, true); // Alternar estado con animación
    }
    
    @Override
    public void handleSidePanelMouseEnter(MouseEvent event) {
        // Expandir solo si no está ya expandido por el botón toggle
        if (!isSidebarExpanded) {
            setSidebarState(true, true); // Expandir con animación
        }
    }
    
    @Override
    public void handleSidePanelMouseExit(MouseEvent event) {
        // Colapsar solo si fue expandido por hover (no manualmente por toggle)
        // Esta lógica podría necesitar refinamiento dependiendo de la interacción deseada.
        // Por ahora, mantengamos simple: si el mouse sale, colapsar.
        // Un enfoque más robusto podría involucrar verificar si el botón toggle forzó la expansión.
        setSidebarState(false, true); // Colapsar con animación
    }
    
    @Override
    public void cleanup() {
        if (expandTimeline != null) {
            expandTimeline.stop();
        }
        if (collapseTimeline != null) {
            collapseTimeline.stop();
        }
    }
}
