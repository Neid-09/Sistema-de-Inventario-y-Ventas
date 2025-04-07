package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaStock;
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
}