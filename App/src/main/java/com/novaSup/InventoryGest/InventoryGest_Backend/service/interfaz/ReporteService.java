package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaReporteDTO;
import java.util.List;

public interface ReporteService {
    List<VentaReporteDTO> generarReporteVentas();
} 