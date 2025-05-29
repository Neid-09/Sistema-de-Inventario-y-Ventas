package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.CajaReporteConsolidadoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.MovimientoCaja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVenta;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.CajaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.CajaReporteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.MovimientoCajaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.VentaService;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.sql.Timestamp;
import java.util.List;

@Service
public class CajaReporteServiceImpl implements CajaReporteService {

    private final CajaService cajaService;
    private final MovimientoCajaService movimientoCajaService;
    private final VentaService ventaService;

    public CajaReporteServiceImpl(CajaService cajaService, MovimientoCajaService movimientoCajaService, VentaService ventaService) {
        this.cajaService = cajaService;
        this.movimientoCajaService = movimientoCajaService;
        this.ventaService = ventaService;
    }

    @Override
    public CajaReporteConsolidadoDTO generarReporteConsolidado(Integer idCaja) {
        Caja caja = cajaService.getCajaById(idCaja);
        if (caja == null) {
            throw new RuntimeException("Caja no encontrada con id: " + idCaja);
        }

        CajaReporteConsolidadoDTO reporteDTO = new CajaReporteConsolidadoDTO();
        reporteDTO.setIdCaja(caja.getIdCaja());
        
        // Obtener fechas de apertura y cierre de la caja
        LocalDateTime fechaApertura = (caja.getFechaApertura() instanceof Timestamp) ? ((Timestamp) caja.getFechaApertura()).toLocalDateTime() : null;
        LocalDateTime fechaCierre = (caja.getFechaCierre() instanceof Timestamp) ? ((Timestamp) caja.getFechaCierre()).toLocalDateTime() : null;

        reporteDTO.setFechaApertura(fechaApertura);
        reporteDTO.setFechaCierre(fechaCierre);
        reporteDTO.setDineroInicial(caja.getDineroInicial() != null ? caja.getDineroInicial() : BigDecimal.ZERO);

        // 1. Calcular movimientos manuales en efectivo usando MovimientoCaja
        List<MovimientoCaja> movimientosCaja = movimientoCajaService.getMovimientosByCaja(caja);
        BigDecimal totalMovimientosManualesEfectivo = BigDecimal.ZERO;

        for (MovimientoCaja movimiento : movimientosCaja) {
            // Solo consideramos movimientos manuales (no de venta) para este total
            if (movimiento.getVenta() == null) {
                 if ("ENTRADA".equals(movimiento.getTipoMovimiento())) {
                     totalMovimientosManualesEfectivo = totalMovimientosManualesEfectivo.add(movimiento.getMonto());
                 } else if ("SALIDA".equals(movimiento.getTipoMovimiento())) {
                     totalMovimientosManualesEfectivo = totalMovimientosManualesEfectivo.subtract(movimiento.getMonto());
                 }
            }
        }
        reporteDTO.setTotalMovimientosEfectivoManuales(totalMovimientosManualesEfectivo);

        // 2. Obtener todas las ventas realizadas durante el período en que la caja estuvo abierta
        // Si la caja aún no ha sido cerrada, usamos la fecha actual como fin del rango.
        LocalDateTime rangoFinVentas = (fechaCierre != null) ? fechaCierre : LocalDateTime.now();
        List<Venta> ventasDelPeriodo = ventaService.obtenerVentasPorRangoFechas(fechaApertura, rangoFinVentas);

        // 3. Calcular totales de venta por tipo de pago y total de unidades vendidas a partir de las Ventas obtenidas
        BigDecimal totalVentasEfectivo = BigDecimal.ZERO;
        BigDecimal totalVentasTarjeta = BigDecimal.ZERO;
        BigDecimal totalVentasOtros = BigDecimal.ZERO;
        BigDecimal totalGeneralVentas = BigDecimal.ZERO;
        Integer totalUnidadesVendidas = 0;

        for (Venta venta : ventasDelPeriodo) {
            if (venta.getTipoPago() != null && venta.getTotalConImpuestos() != null) {
                BigDecimal montoVenta = venta.getTotalConImpuestos(); // Usar el total con impuestos
                String tipoPago = venta.getTipoPago();

                if ("Efectivo".equalsIgnoreCase(tipoPago)) {
                    totalVentasEfectivo = totalVentasEfectivo.add(montoVenta);
                } else if ("Tarjeta".equalsIgnoreCase(tipoPago)) {
                    totalVentasTarjeta = totalVentasTarjeta.add(montoVenta);
                } else { // Otros métodos de pago
                    totalVentasOtros = totalVentasOtros.add(montoVenta);
                }
                totalGeneralVentas = totalGeneralVentas.add(montoVenta);
            }

            // Sumar unidades de los detalles de la venta
            if (venta.getDetallesVenta() != null) {
                for (DetalleVenta detalle : venta.getDetallesVenta()) {
                    if (detalle.getCantidad() != null) {
                        totalUnidadesVendidas += detalle.getCantidad();
                    }
                }
            }
        }

        reporteDTO.setTotalVentasEfectivo(totalVentasEfectivo);
        reporteDTO.setTotalVentasTarjeta(totalVentasTarjeta);
        reporteDTO.setTotalVentasOtros(totalVentasOtros);
        reporteDTO.setTotalGeneralVentas(totalGeneralVentas);
        reporteDTO.setTotalUnidadesVendidas(totalUnidadesVendidas);

        // 4. Calcular el total esperado en caja (solo efectivo)
        reporteDTO.setTotalEsperadoCaja(reporteDTO.getDineroInicial().add(totalMovimientosManualesEfectivo).add(totalVentasEfectivo));

        return reporteDTO;
    }
} 