package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Comision;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ComisionRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ComisionService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.VendedorService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Implementación del servicio para la gestión de Comisiones.
 */
@Service
public class ComisionServiceImpl implements ComisionService {

    private final ComisionRepository comisionRepository;

    private final VendedorService vendedorService; // Para obtener datos del vendedor

    // Constante para el estado inicial de la comisión
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_PAGADO = "PAGADO";

    // Definir cómo se obtiene el porcentaje de comisión.
    // Opciones:
    // 1. Añadir un campo 'porcentajeComision' a la entidad Vendedor (requiere cambio en DB y modelo).
    // 2. Obtenerlo de una configuración global (application.properties o tabla de configuración).
    // 3. Obtenerlo basado en el Rol del Usuario asociado al Vendedor.
    // Por ahora, usaremos un valor fijo como placeholder.
    private static final BigDecimal DEFAULT_PORCENTAJE_COMISION = new BigDecimal("5.00"); // Ejemplo: 5%

    public ComisionServiceImpl(ComisionRepository comisionRepository, VendedorService vendedorService) {
        this.comisionRepository = comisionRepository;
        this.vendedorService = vendedorService;
    }

    @Override
    @Transactional
    public Comision calcularYGuardarComision(Venta venta) {
        if (venta == null || venta.getVendedor() == null) {
            // Loggear advertencia o lanzar excepción si es un error crítico
            System.err.println("Intento de calcular comisión para una venta nula o sin vendedor.");
            return null;
        }

        Vendedor vendedor = vendedorService.obtenerPorId(venta.getVendedor().getIdVendedor())
                .orElseThrow(() -> new EntityNotFoundException("Vendedor no encontrado para calcular comisión: " + venta.getVendedor().getIdVendedor()));

        // --- Lógica de Porcentaje de Comisión ---
        // Reemplazar esto con la lógica real cuando se defina cómo obtener el porcentaje.
        BigDecimal porcentajeComision = DEFAULT_PORCENTAJE_COMISION;
        // Ejemplo si viniera del vendedor (requiere añadir campo a Vendedor.java y DB):
        // BigDecimal porcentajeComision = vendedor.getPorcentajeComision();

        if (porcentajeComision == null || porcentajeComision.compareTo(BigDecimal.ZERO) <= 0) {
            // No hay comisión aplicable (porcentaje es 0 o no definido)
             System.out.println("No se aplica comisión para el vendedor ID: " + vendedor.getIdVendedor() + " (porcentaje <= 0 o nulo)");
            return null;
        }
        // --- Fin Lógica de Porcentaje ---


        BigDecimal montoVenta = venta.getTotalConImpuestos();
        if (montoVenta == null || montoVenta.compareTo(BigDecimal.ZERO) <= 0) {
             System.out.println("No se calcula comisión para venta ID: " + venta.getIdVenta() + " (monto <= 0 o nulo)");
             return null; // No calcular comisión para ventas sin monto positivo
        }

        BigDecimal montoComision = montoVenta.multiply(porcentajeComision.divide(new BigDecimal("100.00"), 4, RoundingMode.HALF_UP)) // Usar 4 decimales para precisión intermedia
                                        .setScale(2, RoundingMode.HALF_UP); // Redondear al final a 2 decimales

        Comision comision = new Comision();
        comision.setVendedor(vendedor); // Asignar la entidad Vendedor completa
        comision.setVenta(venta);       // Asignar la entidad Venta completa
        comision.setMontoVenta(montoVenta);
        comision.setPorcentajeComision(porcentajeComision); // Guardar el porcentaje usado
        comision.setMontoComision(montoComision);
        comision.setFechaCalculo(Timestamp.from(Instant.now()));
        comision.setEstado(ESTADO_PENDIENTE); // Estado inicial

        return comisionRepository.save(comision);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comision> obtenerTodas() {
        return comisionRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comision> obtenerComisionesPorVendedor(Integer idVendedor) {
        return comisionRepository.findByVendedorIdVendedor(idVendedor);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Comision> obtenerComisionesPorEstado(String estado) {
        return comisionRepository.findByEstado(estado);
    }

    @Override
    @Transactional
    public Comision marcarComoPagada(Integer idComision) throws Exception {
        Comision comision = comisionRepository.findById(idComision)
                .orElseThrow(() -> new EntityNotFoundException("Comisión no encontrada con id: " + idComision));

        if (ESTADO_PAGADO.equals(comision.getEstado())) {
            throw new IllegalStateException("La comisión ya está marcada como pagada.");
        }

        comision.setEstado(ESTADO_PAGADO);
        // Opcional: Se podría añadir una fecha de pago
        // comision.setFechaPago(Timestamp.from(Instant.now()));

        return comisionRepository.save(comision);
    }
}
