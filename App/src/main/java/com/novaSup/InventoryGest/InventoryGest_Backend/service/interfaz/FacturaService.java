package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Factura;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleImpuestoFacturaTemporalDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.FacturaPreviewDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaRequestDTO;

import java.util.List;

public interface FacturaService {
    /**
     * Genera una factura para una venta dada y su desglose de impuestos.
     * @param ventaGuardada La entidad Venta ya persistida.
     * @param desgloseImpuestos Lista de DTOs con el detalle de impuestos temporales.
     * @return La entidad Factura generada y persistida.
     */
    Factura generarFactura(Venta ventaGuardada, List<DetalleImpuestoFacturaTemporalDTO> desgloseImpuestos);

    /**
     * Obtiene una factura asociada a una venta por el identificador de la venta y la mapea a un DTO para previsualización.
     * @param idVenta El ID de la venta asociada a la factura a obtener.
     * @return La FacturaPreviewDTO encontrada, o null si no existe.
     */
    FacturaPreviewDTO obtenerFacturaPorIdVenta(int idVenta);

    /**
     * Genera una previsualización de una factura basada en los datos de venta propuestos.
     * No persiste la factura ni afecta el inventario.
     * @param ventaRequest DTO con la información de la venta propuesta.
     * @return La entidad FacturaPreviewDTO generada.
     */
    FacturaPreviewDTO previewFactura(VentaRequestDTO ventaRequest) throws Exception;

    /**
     * Genera el contenido PDF para una factura existente.
     * Genera el contenido PDF para una factura asociada a una venta.
     * @param idVenta El ID de la venta asociada a la factura a generar el PDF.
     * @return Un array de bytes representando el contenido del archivo PDF.
     * @throws Exception Si ocurre un error durante la generación del PDF.
     */
    byte[] generarPdfFacturaPorIdVenta(int idVenta) throws Exception;

    /**
     * Genera el contenido PDF para una previsualización de factura.
     * @param ventaRequest DTO con la información de la venta propuesta.
     * @return Un array de bytes representando el contenido del archivo PDF de previsualización.
     * @throws Exception Si ocurre un error durante la generación del PDF.
     */
    byte[] generarPdfPreview(VentaRequestDTO ventaRequest) throws Exception;
} 