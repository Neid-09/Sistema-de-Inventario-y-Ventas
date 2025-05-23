package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleImpuestoPdfDTO {
    private String nombreTipoImpuesto;
    private BigDecimal baseImponible;
    private BigDecimal tasaAplicada;
    private BigDecimal montoImpuesto;
} 