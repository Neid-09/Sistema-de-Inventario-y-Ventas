package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResultadoCalculoImpuestosDTO {
    private BigDecimal totalImpuestosVenta;
    private List<DetalleImpuestoFacturaTemporalDTO> desgloseImpuestos;
} 