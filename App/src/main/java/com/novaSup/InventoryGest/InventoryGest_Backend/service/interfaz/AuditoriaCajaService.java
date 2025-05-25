package com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaCaja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;

import java.math.BigDecimal;
import java.util.List;

public interface AuditoriaCajaService {
    AuditoriaCaja registrarAuditoria(Caja caja, BigDecimal dineroEsperado, BigDecimal dineroReal, String motivo, Usuario usuario);
    List<AuditoriaCaja> getAuditoriasByCaja(Caja caja);
    List<AuditoriaCaja> getAllAuditorias();
} 