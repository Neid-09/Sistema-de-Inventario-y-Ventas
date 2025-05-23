package com.novaSup.InventoryGest.InventoryGest_Backend.service.util;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DatosFiscalesEmisorPdfDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DatosFiscalesReceptorPdfDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleImpuestoPdfDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleProductoPdfDTO;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FacturaPdfData {

    private String numeroFactura; // O "PREVISUALIZACIÓN"
    private LocalDateTime fechaEmision;

    private DatosFiscalesEmisorPdfDTO emisor;
    private DatosFiscalesReceptorPdfDTO receptor;

    private List<DetalleProductoPdfDTO> detallesProducto;

    private BigDecimal subtotal;
    private List<DetalleImpuestoPdfDTO> detallesImpuesto; // Desglose de impuestos
    private BigDecimal totalImpuestos;
    private BigDecimal totalConImpuestos;

    // Opcional: Campos para información adicional si se requiere en el PDF
    // private String tipoPago;
    // private String nombreVendedor;
    // private String mensajePiePagina; // Por si es dinámico
}