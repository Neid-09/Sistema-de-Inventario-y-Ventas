package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.ResultadoCalculoImpuestosDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVenta; // Asegurando el import correcto
import java.util.Date;
import java.util.List;

public interface CalculoImpuestoService {
    ResultadoCalculoImpuestosDTO calcularImpuestosParaVenta(List<DetalleVenta> detallesVenta, Date fechaVenta);
} 