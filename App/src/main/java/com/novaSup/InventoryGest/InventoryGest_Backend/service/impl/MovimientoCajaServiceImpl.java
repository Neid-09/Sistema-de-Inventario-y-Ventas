package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.MovimientoCaja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.MovimientoCajaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.MovimientoCajaService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MovimientoCajaServiceImpl implements MovimientoCajaService {

    private final MovimientoCajaRepository movimientoCajaRepository;

    public MovimientoCajaServiceImpl(MovimientoCajaRepository movimientoCajaRepository) {
        this.movimientoCajaRepository = movimientoCajaRepository;
    }

    @Override
    @Transactional
    public MovimientoCaja registrarMovimiento(Caja caja, String tipoMovimiento, String descripcion, BigDecimal monto, Usuario usuario, Venta venta) {
        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(caja);
        movimiento.setTipoMovimiento(tipoMovimiento);
        movimiento.setDescripcion(descripcion);
        movimiento.setMonto(monto);
        movimiento.setFecha(Timestamp.valueOf(LocalDateTime.now()));
        movimiento.setUsuario(usuario);
        movimiento.setVenta(venta);
        // referencia_externa will be handled by the Venta relationship implicitly or set explicitly if needed

        return movimientoCajaRepository.save(movimiento);
    }

    @Override
    @Transactional
    public MovimientoCaja registrarMovimiento(Caja caja, String tipoMovimiento, String descripcion, BigDecimal monto, Usuario usuario) {
        return registrarMovimiento(caja, tipoMovimiento, descripcion, monto, usuario, null);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoCaja> getMovimientosByCaja(Caja caja) {
        return movimientoCajaRepository.findByCaja(caja);
    }

    @Override
    @Transactional
    public MovimientoCaja registrarAperturaCaja(Caja caja, BigDecimal dineroInicial, Usuario usuario, boolean heredado, String justificacionManual) {
        String descripcion;
        if (heredado) {
            descripcion = "Apertura de caja con saldo heredado";
        } else {
            // Usar la justificación manual si se proporciona, de lo contrario usar descripción por defecto
            descripcion = "Apertura de caja manual";
            if (justificacionManual != null && !justificacionManual.trim().isEmpty()) {
                descripcion += ": " + justificacionManual.trim();
            }
        }

        MovimientoCaja movimiento = new MovimientoCaja();
        movimiento.setCaja(caja);
        movimiento.setTipoMovimiento("APERTURA");
        movimiento.setDescripcion(descripcion);
        movimiento.setMonto(dineroInicial);
        movimiento.setFecha(Timestamp.valueOf(LocalDateTime.now()));
        movimiento.setUsuario(usuario);

        return movimientoCajaRepository.save(movimiento);
    }
} 