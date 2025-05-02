package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ConfiguracionFX;

import java.util.List;

/**
 * Interfaz para el servicio de gestión de configuraciones en el frontend.
 */
public interface IConfiguracionServiceFX {

    /**
     * Obtiene todas las configuraciones del backend.
     *
     * @return Lista de ConfiguracionFX.
     * @throws Exception Si ocurre un error durante la comunicación o procesamiento.
     */
    List<ConfiguracionFX> listarConfiguraciones() throws Exception;

    /**
     * Busca configuraciones por su clave.
     *
     * @param clave La clave (o parte de ella) a buscar.
     * @return Lista de ConfiguracionFX que coinciden con la clave.
     * @throws Exception Si ocurre un error durante la comunicación o procesamiento.
     */
    List<ConfiguracionFX> buscarConfiguracionesPorClave(String clave) throws Exception;

    /**
     * Obtiene una configuración específica por su ID.
     *
     * @param id El ID de la configuración a obtener.
     * @return La ConfiguracionFX encontrada.
     * @throws Exception Si no se encuentra la configuración o hay un error.
     */
    ConfiguracionFX obtenerConfiguracionPorId(Integer id) throws Exception;

    /**
     * Guarda (crea o actualiza) una configuración en el backend.
     *
     * @param configuracion La ConfiguracionFX a guardar.
     * @return La ConfiguracionFX guardada (puede tener el ID actualizado si era nueva).
     * @throws Exception Si ocurre un error durante la comunicación o guardado.
     */
    ConfiguracionFX guardarConfiguracion(ConfiguracionFX configuracion) throws Exception;

    /**
     * Elimina una configuración por su ID.
     *
     * @param id El ID de la configuración a eliminar.
     * @throws Exception Si ocurre un error durante la eliminación.
     */
    void eliminarConfiguracion(Integer id) throws Exception;
}
