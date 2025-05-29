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
public class DetalleVentaPreviewDTO {
    private Integer idDetalleVenta; // Cambiado a Integer
    private ProductoPreviewInfoDTO producto;
    private Integer cantidad; // Cambiado a Integer
    private BigDecimal precioUnitario; // Mantener como BigDecimal para el precio mapeado
    private BigDecimal subtotal;
    // Añade otros campos necesarios del detalle de venta

    // Getters y Setters generados por Lombok
} 