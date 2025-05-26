package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface VentaRepository extends JpaRepository<Venta, Integer> {

    // --- Consultas Principales para Venta (Cargan hasta DetalleVenta y Producto) ---

    /**
     * Busca una Venta por su ID.
     * Carga EAGER: Vendedor (con Usuario), Cliente, DetallesVenta (con Producto).
     * NO carga DetalleVentaLoteUso para evitar MultipleBagFetchException aquí.
     */
    @Query("SELECT DISTINCT v FROM Venta v " +
           "LEFT JOIN FETCH v.vendedor ven LEFT JOIN FETCH ven.usuario " +
           "LEFT JOIN FETCH v.cliente c " +
           "LEFT JOIN FETCH v.detallesVenta dv " +
           "LEFT JOIN FETCH dv.producto p " +
           "WHERE v.idVenta = :id")
    Optional<Venta> findVentaWithPrimaryDetailsById(@Param("id") Integer id);

    /**
     * Busca todas las Ventas.
     * Carga EAGER: Vendedor (con Usuario), Cliente, DetallesVenta (con Producto).
     * NO carga DetalleVentaLoteUso para evitar MultipleBagFetchException aquí.
     */
    @Query("SELECT DISTINCT v FROM Venta v " +
           "LEFT JOIN FETCH v.vendedor ven LEFT JOIN FETCH ven.usuario " +
           "LEFT JOIN FETCH v.cliente c " +
           "LEFT JOIN FETCH v.detallesVenta dv " +
           "LEFT JOIN FETCH dv.producto p")
    List<Venta> findAllVentasWithPrimaryDetails();

    /**
     * Busca Ventas por ID de Cliente.
     * Carga EAGER: Vendedor (con Usuario), Cliente, DetallesVenta (con Producto).
     */
    @Query("SELECT DISTINCT v FROM Venta v " +
           "LEFT JOIN FETCH v.vendedor ven LEFT JOIN FETCH ven.usuario " +
           "LEFT JOIN FETCH v.cliente c " +
           "LEFT JOIN FETCH v.detallesVenta dv " +
           "LEFT JOIN FETCH dv.producto p " +
           "WHERE c.idCliente = :idCliente")
    List<Venta> findVentasByClienteWithPrimaryDetails(@Param("idCliente") Integer idCliente);

    /**
     * Busca Ventas por ID de Vendedor.
     * Carga EAGER: Vendedor (con Usuario), Cliente, DetallesVenta (con Producto).
     */
    @Query("SELECT DISTINCT v FROM Venta v " +
           "LEFT JOIN FETCH v.vendedor ven LEFT JOIN FETCH ven.usuario " +
           "LEFT JOIN FETCH v.cliente c " +
           "LEFT JOIN FETCH v.detallesVenta dv " +
           "LEFT JOIN FETCH dv.producto p " +
           "WHERE ven.idVendedor = :idVendedor")
    List<Venta> findVentasByVendedorWithPrimaryDetails(@Param("idVendedor") Integer idVendedor);

    /**
     * Busca Ventas dentro de un rango de fechas.
     * Carga EAGER: Vendedor (con Usuario), Cliente, DetallesVenta (con Producto).
     * @param fechaInicio Fecha y hora de inicio del rango (inclusive).
     * @param fechaFin Fecha y hora de fin del rango (inclusive).
     * @return Lista de ventas dentro del rango de fechas.
     */
    @Query("SELECT DISTINCT v FROM Venta v " +
           "LEFT JOIN FETCH v.vendedor ven LEFT JOIN FETCH ven.usuario " +
           "LEFT JOIN FETCH v.cliente c " +
           "LEFT JOIN FETCH v.detallesVenta dv " +
           "LEFT JOIN FETCH dv.producto p " +
           "WHERE v.fecha BETWEEN :fechaInicio AND :fechaFin")
    List<Venta> findVentasByFechaBetweenWithPrimaryDetails(@Param("fechaInicio") Timestamp fechaInicio, @Param("fechaFin") Timestamp fechaFin);

    // --- Consulta Secundaria para cargar DetalleVentaLoteUso ---

    /**
     * Busca DetalleVenta por una lista de sus IDs.
     * Carga EAGER: DetalleVentaLoteUso (con su Lote).
     * Este método se usa para cargar la segunda parte de la jerarquía de objetos.
     * Se pasa un Set de IDs para eficiencia en la cláusula IN de SQL.
     */
    @Query("SELECT DISTINCT dv FROM DetalleVenta dv " +
           "LEFT JOIN FETCH dv.detalleLotesUsados dlu " +
           "LEFT JOIN FETCH dlu.lote l " +
           "WHERE dv.idDetalle IN :detalleVentaIds")
    List<DetalleVenta> findDetalleVentaWithLotesByIdIn(@Param("detalleVentaIds") Set<Integer> detalleVentaIds);

       /**
     * Busca el número de venta máximo registrado.
     * Retorna el número de venta como String.
     */
    @Query("SELECT MAX(v.numeroVenta) FROM Venta v")
    String findMaxNumeroVenta();

}