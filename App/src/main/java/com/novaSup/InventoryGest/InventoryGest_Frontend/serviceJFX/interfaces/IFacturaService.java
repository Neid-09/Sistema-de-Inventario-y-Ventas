package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaCreateRequestFX;

import java.io.IOException;

public interface IFacturaService {

    /**
     * Obtiene el PDF de una factura existente por su ID.
     * @param idVenta El ID de la venta.
     * @return Los bytes del PDF.
     * @throws IOException Si ocurre un error de E/S.
     * @throws InterruptedException Si la operación es interrumpida.
     */
    byte[] getFacturaPdfByIdVenta(int idVenta) throws IOException, InterruptedException;

    /**
     * Obtiene el PDF de previsualización de una factura.
     * @param ventaRequest Los datos de la solicitud de venta (incluyendo detalles).
     * @return Los bytes del PDF de previsualización.
     * @throws IOException Si ocurre un error de E/S.
     * @throws InterruptedException Si la operación es interrumpida.
     */
    byte[] getPreviewFacturaPdf(VentaCreateRequestFX ventaRequest) throws IOException, InterruptedException;
} 