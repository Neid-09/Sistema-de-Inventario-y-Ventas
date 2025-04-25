package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.VentaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VentaServiceImpl implements VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Override
    public Venta registrarVenta(Venta venta) {
        return ventaRepository.save(venta);
    }

    @Override
    public Venta obtenerVentaPorId(Long id) {
        return ventaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    }

    @Override
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    @Override
    public void eliminarVenta(Long id) {
        if (!ventaRepository.existsById(id)) {
            throw new RuntimeException("Venta no encontrada");
        }
        ventaRepository.deleteById(id);
    }
}