package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaRequestDTO; // Asumiendo que crearás este DTO

import java.util.List;
import java.util.Optional;

/**
 * Interfaz para el servicio de gestión de Ventas.
 * Define las operaciones de negocio relacionadas con las ventas, detalles y comisiones.
 */
public interface VentaService {

    /**
     * Registra una nueva venta completa, incluyendo detalles y cálculo de comisiones.
     * @param ventaRequest DTO que contiene la información de la venta y sus detalles.
     * @return La entidad Venta creada.
     * @throws Exception Si ocurre un error durante el proceso (ej. stock insuficiente).
     */
    Venta registrarVentaCompleta(VentaRequestDTO ventaRequest) throws Exception;

    /**
     * Obtiene una venta por su ID.
     * @param id ID de la venta.
     * @return Optional con la venta si se encuentra.
     */
    Optional<Venta> obtenerVentaPorId(Integer id);

    /**
     * Obtiene todas las ventas registradas.
     * @return Lista de todas las ventas.
     */
    List<Venta> listarVentas();

    // --- Métodos específicos (pueden moverse a servicios dedicados si crecen) ---

    /**
     * Obtiene las ventas realizadas por un cliente específico.
     * @param idCliente ID del cliente.
     * @return Lista de ventas del cliente.
     */
    List<Venta> obtenerVentasPorCliente(Integer idCliente);

    /**
     * Obtiene las ventas realizadas por un vendedor específico.
     * @param idVendedor ID del vendedor.
     * @return Lista de ventas del vendedor.
     */
    List<Venta> obtenerVentasPorVendedor(Integer idVendedor);

    /**
      * Cancela una venta (lógica de negocio puede implicar revertir stock, etc.).
      * @param idVenta ID de la venta a cancelar.
      * @throws Exception Si la venta no se puede cancelar.
      */
    // void cancelarVenta(Integer idVenta) throws Exception; // Considerar implementación futura

}