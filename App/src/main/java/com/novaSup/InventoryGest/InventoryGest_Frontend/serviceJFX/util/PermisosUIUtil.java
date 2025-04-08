package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Clase de utilidad para manejar los permisos en la interfaz de usuario.
 * Proporciona métodos para verificar permisos y configurar la visibilidad de controles.
 */
public class PermisosUIUtil {
    private static final Logger logger = LoggerFactory.getLogger(PermisosUIUtil.class);

    /**
     * Verifica si el usuario actual tiene un permiso específico.
     *
     * @param permiso Nombre del permiso a verificar
     * @return true si el usuario tiene el permiso, false en caso contrario
     */
    public static boolean tienePermiso(String permiso) {
        // Verificar si el usuario es administrador o tiene el permiso específico
        boolean esAdmin = LoginServiceImplFX.tienePermiso("ROLE_ADMINISTRADOR");
        boolean tienePermisoEspecifico = LoginServiceImplFX.tienePermiso(permiso);

        logger.debug("Verificando permiso: {}, Es admin: {}, Tiene permiso específico: {}",
                permiso, esAdmin, tienePermisoEspecifico);

        return esAdmin || tienePermisoEspecifico;
    }

    /**
     * Configura la visibilidad de un control de JavaFX según un permiso.
     * Si el usuario no tiene el permiso, el control se oculta y no ocupa espacio.
     *
     * @param control Control a configurar (Button, MenuItem, etc.)
     * @param nombrePermiso Nombre del permiso requerido
     */
    public static void configurarVisibilidad(Control control, String nombrePermiso) {
        boolean tienePermiso = tienePermiso(nombrePermiso);
        control.setVisible(tienePermiso);
        control.setManaged(tienePermiso);
    }

    /**
     * Muestra una alerta de acceso denegado.
     * Se usa cuando un usuario intenta acceder a una funcionalidad sin tener permisos.
     */
    public static void mostrarAlertaAccesoDenegado() {
        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.setTitle("Acceso Denegado");
        alerta.setHeaderText(null);
        alerta.setContentText("No tienes permisos para acceder a esta funcionalidad.");
        alerta.showAndWait();
    }

    /**
     * Verifica si el usuario tiene permiso para una acción.
     * Si no tiene el permiso, muestra una alerta y devuelve false.
     *
     * @param nombrePermiso Nombre del permiso a verificar
     * @return true si tiene permiso, false en caso contrario
     */
    public static boolean verificarPermisoConAlerta(String nombrePermiso) {
        if (!tienePermiso(nombrePermiso)) {
            mostrarAlertaAccesoDenegado();
            return false;
        }
        return true;
    }

    /**
     * Configura un botón según los permisos del usuario.
     *
     * @param boton Botón a configurar
     * @param permiso Permiso requerido para el botón
     */
    public static void configurarBoton(Button boton, String permiso) {
        boolean tieneAcceso = tienePermiso(permiso);
        boton.setVisible(tieneAcceso);
        boton.setManaged(tieneAcceso); // Para que no ocupe espacio si no es visible
        boton.setDisable(!tieneAcceso);
    }

    /**
     * Imprime todos los permisos del usuario actual para diagnóstico
     */
    public static void imprimirTodosLosPermisos() {
        System.out.println("==== DIAGNÓSTICO DE PERMISOS ====");
        System.out.println("Es ROLE_ADMIN: " + tienePermiso("ROLE_ADMIN"));
        System.out.println("Todos los permisos: " + LoginServiceImplFX.getPermisos());
        System.out.println("================================");
    }
}