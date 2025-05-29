package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

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
     * Registra un movimiento de SALIDA debido a una venta.
     *
     * @param producto       Producto vendido.
     * @param cantidad       Cantidad vendida.
     * @param precioUnitario Precio de venta unitario.
     * @param motivo         Motivo del movimiento (ej. "Venta [NumeroVenta]").
     * @param idProveedor    ID del proveedor asociado al producto (opcional, para trazabilidad).
     * @return El registro de movimiento creado.
     */
    RegistMovimient registrarVentaProducto(Producto producto, Integer cantidad, BigDecimal precioUnitario, String motivo, Integer idProveedor);

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
