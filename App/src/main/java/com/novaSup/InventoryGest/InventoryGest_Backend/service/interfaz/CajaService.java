package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface CajaService {
    Caja abrirCaja(Usuario usuario, BigDecimal dineroInicial, boolean heredarSaldoAnterior, String justificacionManual);
    Caja cerrarCaja(Integer idCaja, BigDecimal dineroReal);
    Caja getCajaAbiertaByUsuario(Usuario usuario);
    List<Caja> getAllCajas();
    Caja getCajaById(Integer idCaja);
    Optional<BigDecimal> getLastClosedBalance(Usuario usuario);
} 