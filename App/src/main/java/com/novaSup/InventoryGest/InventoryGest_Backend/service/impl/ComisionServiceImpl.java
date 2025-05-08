package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Comision;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVenta;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ComisionRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ComisionService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.VendedorService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ConfiguracionService;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(ComisionServiceImpl.class);

    private final ComisionRepository comisionRepository;
    private final VendedorService vendedorService;
    private final ConfiguracionService configuracionService;

    // Constante para el estado inicial de la comisión
    private static final String ESTADO_PENDIENTE = "PENDIENTE";
    private static final String ESTADO_PAGADO = "PAGADO";

    // Clave para obtener el porcentaje de comisión por defecto desde la configuración
    private static final String CLAVE_PORCENTAJE_COMISION_DEFAULT_CONFIG = "comision.porcentaje.default.global";

    // Valor de fallback si no se encuentra en configuración o es inválido
    private static final BigDecimal FALLBACK_PORCENTAJE_COMISION = new BigDecimal("5.00"); // Ejemplo: 5%

    public ComisionServiceImpl(ComisionRepository comisionRepository,
                               VendedorService vendedorService,
                               ConfiguracionService configuracionService) {
        this.comisionRepository = comisionRepository;
        this.vendedorService = vendedorService;
        this.configuracionService = configuracionService;
    }

    @Override
    @Transactional
    public Comision calcularYGuardarComision(Venta venta) {
        if (venta == null || venta.getVendedor() == null) {
            logger.warn("Intento de calcular comisión para una venta nula o sin vendedor.");
            return null;
        }

        // Asegurarse de que los detalles de la venta están disponibles
        if (venta.getDetallesVenta() == null || venta.getDetallesVenta().isEmpty()) {
            logger.warn("Intento de calcular comisión para una venta sin detalles. Venta ID: {}", venta.getIdVenta());
            return null;
        }

        Vendedor vendedor = vendedorService.obtenerPorId(venta.getVendedor().getIdVendedor())
                .orElseThrow(() -> new EntityNotFoundException("Vendedor no encontrado para calcular comisión: " + venta.getVendedor().getIdVendedor()));

        // --- Lógica de Porcentaje de Comisión ---
        BigDecimal porcentajeComision = FALLBACK_PORCENTAJE_COMISION; // Inicializar con el fallback

        String porcentajeConfigStr = configuracionService.obtenerValorConfiguracion(CLAVE_PORCENTAJE_COMISION_DEFAULT_CONFIG, null);

        if (porcentajeConfigStr != null && !porcentajeConfigStr.trim().isEmpty()) {
            try {
                BigDecimal porcentajeConfig = new BigDecimal(porcentajeConfigStr.trim());
                if (porcentajeConfig.compareTo(BigDecimal.ZERO) > 0) {
                    porcentajeComision = porcentajeConfig;
                    logger.debug("Usando porcentaje de comisión desde configuración ({}%): {}", CLAVE_PORCENTAJE_COMISION_DEFAULT_CONFIG, porcentajeComision);
                } else {
                    logger.warn("Porcentaje de comisión desde configuración ('{}'='{}') no es válido (debe ser > 0). Usando fallback: {}%", CLAVE_PORCENTAJE_COMISION_DEFAULT_CONFIG, porcentajeConfigStr, FALLBACK_PORCENTAJE_COMISION);
                }
            } catch (NumberFormatException e) {
                logger.warn("Error al convertir el porcentaje de comisión desde configuración ('{}'='{}') a BigDecimal. Usando fallback: {}%", CLAVE_PORCENTAJE_COMISION_DEFAULT_CONFIG, porcentajeConfigStr, FALLBACK_PORCENTAJE_COMISION, e);
            }
        } else {
            logger.info("No se encontró configuración para '{}' o está vacía. Usando porcentaje de comisión de fallback: {}%", CLAVE_PORCENTAJE_COMISION_DEFAULT_CONFIG, FALLBACK_PORCENTAJE_COMISION);
        }
        // Futuro: reemplazar esto con la lógica real para obtener el porcentaje específico del vendedor, si existe.
        // Si el vendedor tiene un porcentaje específico, debería sobreescribir este valor por defecto/global.

        if (porcentajeComision.compareTo(BigDecimal.ZERO) <= 0) { // Esta validación ahora cubre tanto el configurado como el fallback
            logger.warn("No se aplica comisión para el vendedor ID: {} (porcentaje {}% <= 0)", vendedor.getIdVendedor(), porcentajeComision);
            return null;
        }
        // --- Fin Lógica de Porcentaje ---

        // Calcular la ganancia total de la venta
        BigDecimal gananciaTotalVenta = BigDecimal.ZERO;
        for (DetalleVenta detalle : venta.getDetallesVenta()) { // Especificar tipo y usar getDetallesVenta()
            if (detalle != null && detalle.getGananciaDetalle() != null) {
                gananciaTotalVenta = gananciaTotalVenta.add(detalle.getGananciaDetalle());
            }
        }

        if (gananciaTotalVenta.compareTo(BigDecimal.ZERO) <= 0) {
            logger.info("No se calcula comisión para venta ID: {} (ganancia total <= 0)", venta.getIdVenta());
            return null; // No calcular comisión si no hay ganancia positiva
        }

        BigDecimal montoComision = gananciaTotalVenta.multiply(porcentajeComision.divide(new BigDecimal("100.00"), 4, RoundingMode.HALF_UP))
                                        .setScale(2, RoundingMode.HALF_UP); // Redondear al final a 2 decimales

        Comision comision = new Comision();
        comision.setVendedor(vendedor);
        comision.setVenta(venta);
        comision.setBaseComisionable(gananciaTotalVenta); // Usar la ganancia total como base comisionable
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
