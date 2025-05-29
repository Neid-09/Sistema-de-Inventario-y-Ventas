package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VentaPreviewInfoDTO {
    private int idVenta; // Cambiado a int
    private ClientePreviewInfoDTO cliente;
    private VendedorPreviewInfoDTO vendedor;
    private List<DetalleVentaPreviewDTO> detallesVenta;
    // Añade otros campos necesarios de la venta para la factura

    // Getters y Setters generados por Lombok
} 