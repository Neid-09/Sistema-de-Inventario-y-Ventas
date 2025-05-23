package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

// Reutilizar DatosFiscalesFacturaDTO ya existente
// import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DatosFiscalesFacturaDTO;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FacturaPreviewDTO {
    private int idFactura;
    private String numeroFactura;
    private LocalDateTime fechaEmision;
    private BigDecimal subtotal;
    private BigDecimal totalImpuestos;
    private BigDecimal totalConImpuestos;
    private DatosFiscalesFacturaDTO datosFiscales; // Reutilizar DTO existente
    private String estado;
    private VentaPreviewInfoDTO ventaInfo;
    private List<DetalleImpuestoFacturaPreviewDTO> detallesImpuesto;

    // Getters y Setters generados por Lombok
} 