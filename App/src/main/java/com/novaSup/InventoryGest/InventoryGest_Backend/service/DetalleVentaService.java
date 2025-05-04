package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVenta;
import java.util.List;

/**
 * Interfaz para el servicio de gestión de Detalles de Venta.
 */
public interface DetalleVentaService {

    /**
     * Guarda una lista de detalles asociados a una venta.
     * @param detalles Lista de DetalleVenta a guardar.
     * @return La lista de DetalleVenta guardados.
     */
    List<DetalleVenta> guardarDetalles(List<DetalleVenta> detalles);

    /**
     * Obtiene todos los detalles asociados a una venta específica.
     * @param idVenta ID de la venta.
     * @return Lista de detalles de la venta.
     */
    List<DetalleVenta> obtenerDetallesPorVentaId(Integer idVenta);
}
