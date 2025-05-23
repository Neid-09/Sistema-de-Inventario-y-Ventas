package com.novaSup.InventoryGest.InventoryGest_Backend.service;

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
     * Obtiene una factura por su identificador único y la mapea a un DTO para previsualización.
     * @param idFactura El ID de la factura a obtener.
     * @return La FacturaPreviewDTO encontrada, o null si no existe.
     */
    FacturaPreviewDTO obtenerFacturaPorId(int idFactura);

    /**
     * Genera una previsualización de una factura basada en los datos de venta propuestos.
     * No persiste la factura ni afecta el inventario.
     * @param ventaRequest DTO con la información de la venta propuesta.
     * @return La entidad FacturaPreviewDTO generada.
     */
    FacturaPreviewDTO previewFactura(VentaRequestDTO ventaRequest) throws Exception;
} 