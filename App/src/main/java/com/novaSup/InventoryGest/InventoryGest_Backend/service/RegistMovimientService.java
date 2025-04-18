package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.RegistMovimient;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface RegistMovimientService {
    List<RegistMovimient> obtenerTodas();
    List<RegistMovimient> obtenerPorProducto(Integer idProducto);
    List<RegistMovimient> obtenerPorTipoMovimiento(String tipoMovimiento);
    List<RegistMovimient> obtenerPorProveedor(Integer idProveedor);
    List<RegistMovimient> obtenerPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    Optional<RegistMovimient> obtenerPorId(Integer id);
    RegistMovimient guardar(RegistMovimient registMovimient);
    boolean existsEntradaByProductoId(Integer idProducto);

    /**
     * CASO 1: Registra la compra de un producto (entrada)
     * - Crea un nuevo lote con la cantidad inicial
     * - Crea un registro de movimiento de tipo ENTRADA
     *
     * @param producto Producto que se compra
     * @param cantidad Cantidad comprada
     * @param precioUnitario Precio unitario de compra
     * @param numeroLote Número de lote
     * @param fechaVencimiento Fecha de vencimiento del lote
     * @param idProveedor ID del proveedor
     * @param motivo Motivo de la entrada (opcional)
     * @return El registro de movimiento creado
     */
    RegistMovimient registrarCompraProducto(
        Producto producto,
        Integer cantidad,
        BigDecimal precioUnitario,
        String numeroLote,
        java.util.Date fechaVencimiento,
        Integer idProveedor,
        String motivo
    );

    /**
     * CASO 2: Registra la venta de un producto (salida)
     * - Busca lotes disponibles con stock (por orden FIFO)
     * - Crea un registro de movimiento de tipo SALIDA
     * - Descuenta la cantidad en los lotes correspondientes
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
     * CASO 3: Registra un ajuste de inventario (puede ser positivo o negativo)
     * - Crea un lote de ajuste con la cantidad especificada
     * - Crea un registro de movimiento según el tipo de ajuste (ENTRADA o SALIDA)
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
}
