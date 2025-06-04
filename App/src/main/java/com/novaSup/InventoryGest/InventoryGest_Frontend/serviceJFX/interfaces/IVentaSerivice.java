package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaCreateRequestFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaFX;

import java.util.List;

public interface IVentaSerivice {

    VentaFX registrarVenta(VentaCreateRequestFX ventaRequest) throws Exception;

    VentaFX obtenerVentaPorId(Integer id) throws Exception;

    List<VentaFX> listarVentas() throws Exception;

    List<VentaFX> listarVentasPorCliente(Integer clienteId) throws Exception;
}
