package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleImpuestoFacturaTemporalDTO {
    private Integer idTipoImpuesto;
    private String nombreTipoImpuesto;
    private BigDecimal baseImponible;
    private BigDecimal tasaAplicada; // Ej: 16.00 para 16% o el valor directo si no es porcentual
    private BigDecimal montoImpuesto;
} 