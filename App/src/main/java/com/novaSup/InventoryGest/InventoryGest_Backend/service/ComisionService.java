package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Comision;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;

import java.util.List;

/**
 * Interfaz para el servicio de gestión de Comisiones.
 */
public interface ComisionService {

    /**
     * Calcula y guarda la comisión para una venta específica.
     * @param venta La venta sobre la cual calcular la comisión.
     * @return La entidad Comision creada.
     */
    Comision calcularYGuardarComision(Venta venta);

    /**
     * Obtiene todas las comisiones generadas.
     * @return Lista de todas las comisiones.
     */
    List<Comision> obtenerTodas();

    /**
     * Obtiene las comisiones de un vendedor específico.
     * @param idVendedor ID del vendedor.
     * @return Lista de comisiones del vendedor.
     */
    List<Comision> obtenerComisionesPorVendedor(Integer idVendedor);

    /**
     * Obtiene las comisiones por estado (ej. PENDIENTE, PAGADO).
     * @param estado Estado de la comisión.
     * @return Lista de comisiones con ese estado.
     */
    List<Comision> obtenerComisionesPorEstado(String estado);

    /**
     * Marca una comisión como pagada.
     * @param idComision ID de la comisión a marcar.
     * @return La comisión actualizada.
     * @throws Exception Si la comisión no se encuentra o ya está pagada.
     */
    Comision marcarComoPagada(Integer idComision) throws Exception;
}
