package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.MovimientoCaja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;

import java.math.BigDecimal;
import java.util.List;

public interface MovimientoCajaService {
    MovimientoCaja registrarMovimiento(Caja caja, String tipoMovimiento, String descripcion, BigDecimal monto, Usuario usuario, Venta venta);
    MovimientoCaja registrarMovimiento(Caja caja, String tipoMovimiento, String descripcion, BigDecimal monto, Usuario usuario);
    List<MovimientoCaja> getMovimientosByCaja(Caja caja);
    MovimientoCaja registrarAperturaCaja(Caja caja, BigDecimal dineroInicial, Usuario usuario, boolean heredado, String justificacionManual);
} 