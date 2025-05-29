package com.novaSup.InventoryGest.InventoryGest_Backend.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO (Data Transfer Object) para recibir la información de una nueva venta.
 * Contiene los datos de la cabecera de la venta y la lista de detalles.
 */
@Data
public class VentaRequestDTO {

    private Integer idCliente; // Opcional, puede ser venta a consumidor final
    private Integer idVendedor;
    private Boolean requiereFactura;
    private Boolean aplicarImpuestos;
    private String numeroVenta; // Podría generarse automáticamente
    private String tipoPago; // Añadir este campo para el tipo de pago

    private List<DetalleVentaDTO> detalles;

    // DTO anidado para los detalles de la venta
    @Data
    public static class DetalleVentaDTO {
        private Integer idProducto;
        private Integer cantidad;
        private BigDecimal precioUnitario; // Precio al que se vende, puede incluir descuentos/promociones
        // No se incluye idLote aquí, el servicio debe determinarlo (FIFO/FEFO)
    }
}
