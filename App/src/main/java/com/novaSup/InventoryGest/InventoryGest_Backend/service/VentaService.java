package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;

import java.util.List;

public interface VentaService {
    Venta registrarVenta(Venta venta);
    Venta obtenerVentaPorId(Long id);
    List<Venta> listarVentas();
    void eliminarVenta(Long id);
}