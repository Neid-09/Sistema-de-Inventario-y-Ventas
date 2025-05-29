package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DetalleImpuestoFacturaPreviewDTO {
    private Long idDetalleImpuestoFactura; // Cambiado a Long
    private TipoImpuestoPreviewInfoDTO tipoImpuesto;
    private BigDecimal baseImponible;
    private BigDecimal tasaAplicada;
    private BigDecimal montoImpuesto;
    // Añade otros campos necesarios del detalle de impuesto

    // Getters y Setters generados por Lombok
} 