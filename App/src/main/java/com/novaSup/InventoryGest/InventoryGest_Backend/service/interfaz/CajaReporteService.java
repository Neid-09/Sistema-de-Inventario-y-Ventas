package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.CajaReporteConsolidadoDTO;

public interface CajaReporteService {
    CajaReporteConsolidadoDTO generarReporteConsolidado(Integer idCaja);
} 