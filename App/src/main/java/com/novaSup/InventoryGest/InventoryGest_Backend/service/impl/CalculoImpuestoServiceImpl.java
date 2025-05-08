package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleImpuestoFacturaTemporalDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.ResultadoCalculoImpuestosDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVenta;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.ImpuestoAplicable;
// Asumiendo que DetalleVenta tiene getProducto() y Producto tiene getIdProducto()
// Asumiendo que Producto tiene getCategoria() y Categoria tiene getIdCategoria()
import com.novaSup.InventoryGest.InventoryGest_Backend.service.CalculoImpuestoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ImpuestoAplicableService;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
// Considerar agregar un logger, por ejemplo:
// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;

@Service
public class CalculoImpuestoServiceImpl implements CalculoImpuestoService {

    // private static final Logger log = LoggerFactory.getLogger(CalculoImpuestoServiceImpl.class);

    private final ImpuestoAplicableService impuestoAplicableService;

    public CalculoImpuestoServiceImpl(ImpuestoAplicableService impuestoAplicableService) {
        this.impuestoAplicableService = impuestoAplicableService;
    }

    @Override
    public ResultadoCalculoImpuestosDTO calcularImpuestosParaVenta(List<DetalleVenta> detallesVenta, Date fechaVenta) {
        BigDecimal totalImpuestosVenta = BigDecimal.ZERO;
        List<DetalleImpuestoFacturaTemporalDTO> listaDesglose = new ArrayList<>();

        if (detallesVenta == null || detallesVenta.isEmpty()) {
            // log.info("La lista de detallesVenta está vacía o es nula. No se calculan impuestos.");
            return new ResultadoCalculoImpuestosDTO(totalImpuestosVenta, listaDesglose);
        }
        if (fechaVenta == null) {
            // log.error("fechaVenta es nula. No se pueden calcular impuestos.");
            throw new IllegalArgumentException("La fechaVenta no puede ser nula para el cálculo de impuestos.");
        }

        for (DetalleVenta detalle : detallesVenta) {
            if (detalle.getProducto() == null || detalle.getProducto().getIdProducto() == null || detalle.getSubtotal() == null) {
                // log.warn("DetalleVenta con ID {} omitido del cálculo de impuestos por falta de producto, ID de producto o subtotal.", detalle.getIdDetalleVenta());
                continue;
            }

            Integer idProducto = detalle.getProducto().getIdProducto();
            Integer idCategoria = null;
            if (detalle.getProducto().getCategoria() != null && detalle.getProducto().getCategoria().getIdCategoria() != null) {
                 idCategoria = detalle.getProducto().getCategoria().getIdCategoria();
            }

            // log.debug("Calculando impuestos para DetalleVenta ID: {}, Producto ID: {}, Categoria ID: {}, Fecha: {}", 
            //    detalle.getIdDetalleVenta(), idProducto, idCategoria, fechaVenta);

            List<ImpuestoAplicable> impuestosParaDetalle = new ArrayList<>();
            try {
                impuestosParaDetalle = impuestoAplicableService.obtenerImpuestosAplicables(idProducto, idCategoria, fechaVenta);
            } catch (IllegalArgumentException e) {
                // log.error("Error al obtener impuestos aplicables para Producto ID: {} y Categoria ID: {}: {}", idProducto, idCategoria, e.getMessage());
                // Decidir si continuar con el siguiente detalle o relanzar/manejar de otra forma
                continue; 
            }


            for (ImpuestoAplicable impuestoAplicable : impuestosParaDetalle) {
                BigDecimal montoImpuestoDetalle = BigDecimal.ZERO;
                BigDecimal baseImponible = detalle.getSubtotal();

                if (impuestoAplicable.getTasaImpuesto() == null || 
                    impuestoAplicable.getTasaImpuesto().getTipoImpuesto() == null ||
                    impuestoAplicable.getTasaImpuesto().getTasa() == null ||
                    impuestoAplicable.getTasaImpuesto().getTipoImpuesto().getEsPorcentual() == null) {
                    // log.error("Datos incompletos en ImpuestoAplicable ID: {} (TasaImpuesto, TipoImpuesto, Tasa o EsPorcentual es null). Se omite este impuesto.", impuestoAplicable.getIdImpuestoAplicable());
                    continue; 
                }
                
                BigDecimal tasa = impuestoAplicable.getTasaImpuesto().getTasa();

                if (Boolean.TRUE.equals(impuestoAplicable.getTasaImpuesto().getTipoImpuesto().getEsPorcentual())) {
                    if (tasa.compareTo(BigDecimal.ZERO) < 0) {
                        // log.warn("Tasa porcentual negativa encontrada ({}) para TipoImpuesto ID: {}. Se tratará como cero.", tasa, impuestoAplicable.getTasaImpuesto().getTipoImpuesto().getIdTipoImpuesto());
                        tasa = BigDecimal.ZERO;
                    }
                    montoImpuestoDetalle = baseImponible.multiply(tasa.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP));
                } else {
                    // Impuesto de monto fijo
                    montoImpuestoDetalle = tasa;
                }
                
                montoImpuestoDetalle = montoImpuestoDetalle.setScale(2, RoundingMode.HALF_UP);

                totalImpuestosVenta = totalImpuestosVenta.add(montoImpuestoDetalle);

                DetalleImpuestoFacturaTemporalDTO detalleDto = new DetalleImpuestoFacturaTemporalDTO();
                detalleDto.setIdTipoImpuesto(impuestoAplicable.getTasaImpuesto().getTipoImpuesto().getIdTipoImpuesto());
                detalleDto.setNombreTipoImpuesto(impuestoAplicable.getTasaImpuesto().getTipoImpuesto().getNombre());
                detalleDto.setBaseImponible(baseImponible.setScale(2, RoundingMode.HALF_UP));
                detalleDto.setTasaAplicada(tasa); 
                detalleDto.setMontoImpuesto(montoImpuestoDetalle);
                
                listaDesglose.add(detalleDto);
                // log.debug("Impuesto calculado para DetalleVenta ID: {}: Tipo: {}, Base: {}, Tasa: {}, Monto: {}", 
                //    detalle.getIdDetalleVenta(), detalleDto.getNombreTipoImpuesto(), detalleDto.getBaseImponible(), detalleDto.getTasaAplicada(), detalleDto.getMontoImpuesto());
            }
        }
        // log.info("Cálculo de impuestos finalizado. Total Impuestos: {}, Desglose Items: {}", 
        //    totalImpuestosVenta.setScale(2, RoundingMode.HALF_UP), listaDesglose.size());
        return new ResultadoCalculoImpuestosDTO(totalImpuestosVenta.setScale(2, RoundingMode.HALF_UP), listaDesglose);
    }
} 