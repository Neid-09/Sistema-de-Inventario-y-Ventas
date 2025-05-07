package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaRequest;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaResponse;

import java.util.List;

public interface IVentaSerivice {

    VentaResponse registrarVenta(VentaRequest ventaRequest) throws Exception;

    VentaResponse obtenerVentaPorId(Integer id) throws Exception;

    List<VentaResponse> listarVentas() throws Exception;
}
