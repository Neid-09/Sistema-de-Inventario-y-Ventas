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
public class ProductoPreviewInfoDTO {
    private int idProducto; // Cambiado a int
    private String nombre;
    private String codigo;
    private BigDecimal precioUnitario;
    // Añade otros campos necesarios del producto para la factura

    // Getters y Setters generados por Lombok
} 