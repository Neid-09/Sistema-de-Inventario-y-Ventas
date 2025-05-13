package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DetalleVentaResponseDTO {
    private Integer idDetalleVenta;
    private Integer idProducto;
    private String nombreProducto;
    private Integer cantidad;
    private BigDecimal precioUnitarioOriginal;
    private Integer idPromocionAplicada;
    private BigDecimal precioUnitarioFinal;
    private BigDecimal subtotal;
    private List<DetalleVentaLoteUsoResponseDTO> lotesUsados;
    // Considerar si se quiere exponer costoUnitarioProducto y gananciaDetalle
    // private BigDecimal costoUnitarioProducto;
    // private BigDecimal gananciaDetalle;
} 