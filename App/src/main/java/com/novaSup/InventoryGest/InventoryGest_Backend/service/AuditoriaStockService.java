package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaStock;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AuditoriaStockService {
    List<AuditoriaStock> obtenerTodas();
    Optional<AuditoriaStock> obtenerPorId(Integer id);
    List<AuditoriaStock> obtenerPorProducto(Integer idProducto);
    List<AuditoriaStock> obtenerPorRangoFecha(LocalDateTime fechaInicio, LocalDateTime fechaFin);
    AuditoriaStock registrarAuditoria(Integer idProducto, Integer stockEsperado,
                                      Integer stockReal, String motivo, Integer idUsuario);
    AuditoriaStock guardar(AuditoriaStock auditoriaStock);

    /**
     * CASO 3: Registra una diferencia en el inventario físico
     * - Compara las cantidades reales con las del sistema
     * - Crea un registro de auditoría con la diferencia
     * - Actualiza el stock afectando los lotes o creando uno temporal de ajuste
     * - No crea registro de movimiento porque no fue una venta ni compra real
     * 
     * @param producto Producto auditado
     * @param stockReal Cantidad real contada en el inventario físico
     * @param motivo Motivo de la diferencia
     * @param idUsuario ID del usuario que realiza la auditoría
     * @return El registro de auditoría creado
     * @throws Exception Si ocurre un error al ajustar el inventario
     */
    AuditoriaStock registrarDiferenciaInventario(
        Producto producto,
        Integer stockReal,
        String motivo,
        Integer idUsuario
    ) throws Exception;
}
