package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Factura;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleImpuestoFacturaTemporalDTO;

import java.util.List;

public interface FacturaService {
    /**
     * Genera una factura para una venta dada y su desglose de impuestos.
     * @param ventaGuardada La entidad Venta ya persistida.
     * @param desgloseImpuestos Lista de DTOs con el detalle de impuestos temporales.
     * @return La entidad Factura generada y persistida.
     */
    Factura generarFactura(Venta ventaGuardada, List<DetalleImpuestoFacturaTemporalDTO> desgloseImpuestos);
} 