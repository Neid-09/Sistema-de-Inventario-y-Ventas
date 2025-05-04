package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVenta;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.DetalleVentaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.DetalleVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementación del servicio para la gestión de Detalles de Venta.
 */
@Service
public class DetalleVentaServiceImpl implements DetalleVentaService {

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Override
    @Transactional // Asegura que todos los detalles se guarden en una transacción
    public List<DetalleVenta> guardarDetalles(List<DetalleVenta> detalles) {
        // Aquí podrían ir validaciones adicionales si fuesen necesarias
        return detalleVentaRepository.saveAll(detalles);
    }
 
    @Override
    @Transactional(readOnly = true)
    public List<DetalleVenta> obtenerDetallesPorVentaId(Integer idVenta) {
        return detalleVentaRepository.findByVentaIdVenta(idVenta);
    }
}
