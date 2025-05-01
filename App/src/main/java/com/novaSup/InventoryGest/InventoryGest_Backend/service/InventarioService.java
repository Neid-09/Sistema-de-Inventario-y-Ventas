package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaStock;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.RegistMovimient;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Servicio de coordinación para operaciones de inventario.
 * Este servicio se encarga de coordinar las operaciones entre el servicio de lotes y
 * el servicio de registro de movimientos, garantizando la coherencia en las operaciones
 * que afectan al stock y los registros de movimientos.
 */
public interface InventarioService {

    /**
     * Registra la compra de un producto, creando el registro de movimiento y el lote correspondiente.
     * 
     * @param producto Producto que se compra
     * @param cantidad Cantidad comprada
     * @param precioUnitario Precio unitario de compra
     * @param numeroLote Número de lote
     * @param fechaVencimiento Fecha de vencimiento del lote
     * @param motivo Motivo de la entrada (opcional)
     * @return El registro de movimiento creado
     */
    RegistMovimient registrarCompraProducto(
        Producto producto,
        Integer cantidad,
        BigDecimal precioUnitario,
        String numeroLote,
        Date fechaVencimiento,
        String motivo
    );

    /**
     * Registra la venta de un producto, creando el registro de movimiento y reduciendo los lotes correspondientes.
     * 
     * @param producto Producto que se vende
     * @param cantidad Cantidad vendida
     * @param precioUnitario Precio unitario de venta
     * @param motivo Motivo de la salida (opcional)
     * @return El registro de movimiento creado
     * @throws Exception Si no hay suficiente stock disponible
     */
    RegistMovimient registrarVentaProducto(
        Producto producto,
        Integer cantidad,
        BigDecimal precioUnitario,
        String motivo
    ) throws Exception;

    /**
     * Registra un ajuste de inventario, creando el registro de movimiento y ajustando los lotes correspondientes.
     * 
     * @param producto Producto que se ajusta
     * @param cantidad Cantidad a ajustar (positiva para aumentar, negativa para disminuir)
     * @param motivo Motivo del ajuste (obligatorio)
     * @return El registro de movimiento creado
     * @throws Exception Si no hay suficiente stock para un ajuste negativo
     */
    RegistMovimient registrarAjusteInventario(
        Producto producto,
        Integer cantidad,
        String motivo
    ) throws Exception;

    /**
     * Registra una devolución de producto a un lote específico.
     * 
     * @param idLote ID del lote al que se va a devolver producto
     * @param cantidad Cantidad a devolver
     * @param motivo Motivo de la devolución
     * @return El lote actualizado
     * @throws Exception Si ocurre un error en la devolución
     */
    Lote registrarDevolucion(
        Integer idLote,
        Integer cantidad,
        String motivo
    ) throws Exception;
    
    /**
     * Registra una auditoría de stock cuando se encuentra una diferencia entre el inventario físico y el sistema.
     * Ajusta automáticamente el stock para que coincida con el conteo físico.
     * 
     * @param producto Producto auditado
     * @param stockReal Cantidad real contada en el inventario físico
     * @param motivo Motivo de la diferencia
     * @param idUsuario ID del usuario que realiza la auditoría
     * @return El registro de auditoría creado
     * @throws Exception Si ocurre un error al ajustar el inventario
     */
    AuditoriaStock registrarAuditoriaStock(
        Producto producto,
        Integer stockReal,
        String motivo,
        Integer idUsuario
    ) throws Exception;

    /**
     * Obtiene todos los lotes por producto con su información de movimientos.
     * 
     * @param idProducto ID del producto
     * @return Lista de lotes del producto
     */
    List<Lote> obtenerLotesPorProducto(Integer idProducto);

    /**
     * Obtiene todos los movimientos por producto.
     * 
     * @param idProducto ID del producto
     * @return Lista de movimientos del producto
     */
    List<RegistMovimient> obtenerMovimientosPorProducto(Integer idProducto);
} 