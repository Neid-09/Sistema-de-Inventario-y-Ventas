package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaCaja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.AuditoriaCajaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.AuditoriaCajaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.MovimientoCajaService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AuditoriaCajaServiceImpl implements AuditoriaCajaService {

    private final AuditoriaCajaRepository auditoriaCajaRepository;
    private final MovimientoCajaService movimientoCajaService;

    public AuditoriaCajaServiceImpl(AuditoriaCajaRepository auditoriaCajaRepository, MovimientoCajaService movimientoCajaService) {
        this.auditoriaCajaRepository = auditoriaCajaRepository;
        this.movimientoCajaService = movimientoCajaService;
    }

    @Override
    @Transactional
    public AuditoriaCaja registrarAuditoria(Caja caja, BigDecimal dineroEsperado, BigDecimal dineroReal, String motivo, Usuario usuario) {
        AuditoriaCaja auditoria = new AuditoriaCaja();
        auditoria.setCaja(caja);
        auditoria.setDineroEsperado(dineroEsperado);
        auditoria.setDineroReal(dineroReal);
        auditoria.setFecha(Timestamp.valueOf(LocalDateTime.now()));
        auditoria.setMotivo(motivo);
        auditoria.setUsuario(usuario);

        AuditoriaCaja auditoriaGuardada = auditoriaCajaRepository.save(auditoria);

        BigDecimal diferencia = dineroReal.subtract(dineroEsperado);

        if (diferencia.compareTo(BigDecimal.ZERO) != 0) {
            String tipoMovimiento = diferencia.compareTo(BigDecimal.ZERO) > 0 ? "AJUSTE_INGRESO_AUDITORIA" : "AJUSTE_RETIRO_AUDITORIA";
            String descripcionMovimiento = "Ajuste por auditoría (ID: " + auditoriaGuardada.getIdAuditoria() + "): " + motivo;
            BigDecimal montoMovimiento = diferencia.abs();

            movimientoCajaService.registrarMovimiento(caja, tipoMovimiento, descripcionMovimiento, montoMovimiento, usuario);
        }

        return auditoriaGuardada;
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaCaja> getAuditoriasByCaja(Caja caja) {
        return auditoriaCajaRepository.findByCaja(caja);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AuditoriaCaja> getAllAuditorias() {
        return auditoriaCajaRepository.findAll();
    }
} 