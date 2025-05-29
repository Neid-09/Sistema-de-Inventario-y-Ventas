package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleImpuestoFacturaTemporalDTO {
    private Integer idTipoImpuesto; // ID del TipoImpuesto ya existente (Cambiado a Integer)
    private String nombreTipoImpuesto; // Nombre del tipo de impuesto
    private BigDecimal baseImponible;
    private BigDecimal tasaAplicada;
    private BigDecimal montoImpuesto;
} 