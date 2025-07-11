package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VentaResponseDTO {
    private Integer idVenta;
    private Timestamp fecha;
    private String numeroVenta;
    private Integer idCliente; // Puedes añadir más detalles del cliente si es necesario
    private String nombreCliente; // Nuevo campo para el nombre del cliente
    private Integer idVendedor;
    private String nombreVendedor; // Campo adicional para el nombre
    private String tipoPago; // Nuevo campo para el tipo de pago
    private BigDecimal subtotal; // Nuevo campo para el subtotal
    private Boolean aplicarImpuestos;
    private BigDecimal totalImpuestos; // Nuevo campo para el total de impuestos
    private BigDecimal total;
    private Boolean requiereFactura;
    
    // Nuevo campo para los detalles de la venta
    private List<DetalleVentaResponseDTO> detalles;
    // No incluir la lista de detalles aquí a menos que sea necesario y se maneje la carga
}
