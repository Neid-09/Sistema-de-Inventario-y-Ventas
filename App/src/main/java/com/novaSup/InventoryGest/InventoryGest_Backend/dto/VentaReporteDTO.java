package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp; // Usar java.sql.Timestamp si Venta.fecha es de este tipo

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaReporteDTO {
    private Integer idVenta; // Para posible navegación o "Ver Detalle"
    private String numeroVenta;
    private Timestamp fecha;
    private String nombreVendedor;
    private String formaDePago; // Placeholder - necesita definición
    private String nombreCliente;
    private BigDecimal totalVenta;
} 