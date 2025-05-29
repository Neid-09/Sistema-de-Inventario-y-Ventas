package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleProductoPdfDTO {
    private String codigo;
    private String nombre;
    private Integer cantidad;
    private BigDecimal precioUnitario; // Precio final con promociones
    private BigDecimal subtotal;
} 