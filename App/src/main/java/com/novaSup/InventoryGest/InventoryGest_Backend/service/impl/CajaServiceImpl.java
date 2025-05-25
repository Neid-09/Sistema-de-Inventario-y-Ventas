package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.MovimientoCaja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaCaja;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.CajaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.MovimientoCajaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.AuditoriaCajaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.CajaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.MovimientoCajaService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CajaServiceImpl implements CajaService {

    private final CajaRepository cajaRepository;
    private final MovimientoCajaRepository movimientoCajaRepository;
    private final AuditoriaCajaRepository auditoriaCajaRepository;
    private final MovimientoCajaService movimientoCajaService;

    public CajaServiceImpl(CajaRepository cajaRepository, MovimientoCajaRepository movimientoCajaRepository, AuditoriaCajaRepository auditoriaCajaRepository, MovimientoCajaService movimientoCajaService) {
        this.cajaRepository = cajaRepository;
        this.movimientoCajaRepository = movimientoCajaRepository;
        this.auditoriaCajaRepository = auditoriaCajaRepository;
        this.movimientoCajaService = movimientoCajaService;
    }

    @Override
    @Transactional
    public Caja abrirCaja(Usuario usuario, BigDecimal dineroInicial, boolean heredarSaldoAnterior, String justificacionManual) {
        // Verificar si el usuario ya tiene una caja abierta
        List<Caja> cajasAbiertas = cajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA");
        if (!cajasAbiertas.isEmpty()) {
            throw new RuntimeException("El usuario ya tiene una caja abierta.");
        }

        BigDecimal montoInicialReal = dineroInicial; // Default to provided dineroInicial
        boolean heredado = false;

        if (heredarSaldoAnterior) {
            Optional<BigDecimal> ultimoSaldoOpt = getLastClosedBalance(usuario);
            if (ultimoSaldoOpt.isPresent()) {
                montoInicialReal = ultimoSaldoOpt.get();
                heredado = true;
            } else {
                throw new RuntimeException("No hay un cierre de caja anterior para heredar saldo.");
            }
        } else {
            if (dineroInicial == null) {
                throw new RuntimeException("Debe heredar saldo o proporcionar un dinero inicial.");
            }
        }

        Caja caja = new Caja();
        caja.setUsuario(usuario);
        caja.setDineroInicial(montoInicialReal);
        caja.setFechaApertura(Timestamp.valueOf(LocalDateTime.now()));
        caja.setEstado("ABIERTA");

        Caja cajaGuardada = cajaRepository.save(caja);

        // Registrar el movimiento de apertura de caja
        movimientoCajaService.registrarAperturaCaja(cajaGuardada, montoInicialReal, usuario, heredado, justificacionManual);

        return cajaGuardada;
    }

    @Override
    @Transactional
    public Caja cerrarCaja(Integer idCaja, BigDecimal dineroReal) {
        Optional<Caja> cajaOpt = cajaRepository.findById(idCaja);

        if (!cajaOpt.isPresent()) {
            throw new RuntimeException("Caja no encontrada.");
        }

        Caja caja = cajaOpt.get();

        if (!caja.getEstado().equals("ABIERTA")) {
            throw new RuntimeException("La caja ya está cerrada.");
        }

        // Calcular dinero esperado: dinero inicial + suma de movimientos (tipo VENTA, INGRESO) - (tipo RETIRO, AJUSTE negativo)
        List<MovimientoCaja> movimientos = movimientoCajaRepository.findByCaja(caja);
        BigDecimal dineroMovimientos = movimientos.stream()
                                       .map(MovimientoCaja::getMonto)
                                       .reduce(BigDecimal.ZERO, BigDecimal::add);


        BigDecimal dineroEsperado = caja.getDineroInicial().add(dineroMovimientos);

        caja.setFechaCierre(Timestamp.valueOf(LocalDateTime.now()));
        caja.setEstado("CERRADA");
        caja.setDineroTotal(dineroReal);
        cajaRepository.save(caja);

        // Registrar auditoría
        AuditoriaCaja auditoria = new AuditoriaCaja();
        auditoria.setCaja(caja);
        auditoria.setDineroEsperado(dineroEsperado);
        auditoria.setDineroReal(dineroReal);
        auditoria.setFecha(Timestamp.valueOf(LocalDateTime.now()));
        auditoria.setMotivo(dineroReal.compareTo(dineroEsperado) != 0 ? "Discrepancia al cierre" : "Cierre normal");
        auditoria.setUsuario(caja.getUsuario());

        auditoriaCajaRepository.save(auditoria);

        return caja;
    }

    @Override
    @Transactional(readOnly = true)
    public Caja getCajaAbiertaByUsuario(Usuario usuario) {
        List<Caja> cajasAbiertas = cajaRepository.findByUsuarioAndEstado(usuario, "ABIERTA");
        if (cajasAbiertas.isEmpty()) {
            return null;
        } else if (cajasAbiertas.size() > 1) {
            throw new RuntimeException("Múltiples cajas abiertas para el mismo usuario.");
        }
        return cajasAbiertas.get(0);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Caja> getAllCajas() {
        return cajaRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Caja getCajaById(Integer idCaja) {
        return cajaRepository.findById(idCaja)
                             .orElseThrow(() -> new RuntimeException("Caja no encontrada con id: " + idCaja));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<BigDecimal> getLastClosedBalance(Usuario usuario) {
        Optional<AuditoriaCaja> lastAudit = auditoriaCajaRepository.findFirstByUsuarioOrderByFechaDesc(usuario);
        return lastAudit.map(AuditoriaCaja::getDineroReal);
    }
} 