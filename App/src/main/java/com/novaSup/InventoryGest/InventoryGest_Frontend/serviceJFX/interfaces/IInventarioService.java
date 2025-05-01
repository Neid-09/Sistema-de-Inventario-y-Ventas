package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.LoteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface IInventarioService {
    /**
     * Registra una compra de producto (entrada) y crea un lote automáticamente
     * @param idProducto ID del producto
     * @param cantidad Cantidad a comprar
     * @param precioUnitario Precio unitario de compra
     * @param numeroLote Número de lote
     * @param fechaVencimiento Fecha de vencimiento del lote
     * @param idProveedor ID del proveedor
     * @param motivo Motivo de la compra
     * @return El movimiento registrado
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    EntradaProductoFX registrarCompra(Integer idProducto, Integer cantidad, 
                                     BigDecimal precioUnitario, String numeroLote,
                                     LocalDate fechaVencimiento, Integer idProveedor, 
                                     String motivo) throws Exception;
    
    /**
     * Registra una venta de producto (salida)
     * @param idProducto ID del producto
     * @param cantidad Cantidad a vender
     * @param precioUnitario Precio unitario de venta
     * @param motivo Motivo de la venta
     * @return El movimiento registrado
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    EntradaProductoFX registrarVenta(Integer idProducto, Integer cantidad, 
                                    BigDecimal precioUnitario, String motivo) throws Exception;
    
    /**
     * Registra un ajuste de inventario
     * @param idProducto ID del producto
     * @param cantidad Cantidad a ajustar (positiva para incremento, negativa para decremento)
     * @param motivo Motivo del ajuste
     * @return El movimiento registrado
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    EntradaProductoFX registrarAjuste(Integer idProducto, Integer cantidad, 
                                     String motivo) throws Exception;
    
    /**
     * Registra una devolución a un lote
     * @param idLote ID del lote
     * @param cantidad Cantidad a devolver
     * @param motivo Motivo de la devolución
     * @return El lote actualizado
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    LoteFX registrarDevolucion(Integer idLote, Integer cantidad, String motivo) throws Exception;
    
    /**
     * Obtiene los lotes por producto
     * @param idProducto ID del producto
     * @return Lista de lotes del producto
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    List<LoteFX> obtenerLotesPorProducto(Integer idProducto) throws Exception;
    
    /**
     * Obtiene los movimientos por producto
     * @param idProducto ID del producto
     * @return Lista de movimientos del producto
     * @throws Exception Si ocurre un error en la comunicación con la API
     */
    List<EntradaProductoFX> obtenerMovimientosPorProducto(Integer idProducto) throws Exception;
}
