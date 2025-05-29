package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces;

import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.*;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.*;

import java.util.List;
import java.util.Optional;

public interface ICajaService {

    // CajaController Endpoints
    CajaResponseFX abrirCaja(AbrirCajaRequestDTO request) throws Exception;

    CajaResponseFX cerrarCaja(Integer idCaja, CerrarCajaRequestDTO request) throws Exception;

    Optional<CajaResponseFX> getCajaAbiertaPorUsuario(Integer idUsuario) throws Exception;

    List<CajaResponseFX> getAllCajas() throws Exception;

    Optional<CajaResponseFX> getCajaById(Integer idCaja) throws Exception;

    // MovimientoCajaController Endpoints
    List<MovimientoCajaResponseFX> getMovimientosByCajaId(Integer idCaja) throws Exception;

    MovimientoCajaResponseFX registrarMovimientoManual(RegistrarMovimientoManualRequestDTO request) throws Exception;

    Optional<CajaReporteConsolidadoFX> getReporteConsolidadoByCajaId(Integer idCaja) throws Exception;

    // AuditoriaCajaController Endpoints
    List<AuditoriaCajaResponseFX> getAuditoriasByCajaId(Integer idCaja) throws Exception;

    List<AuditoriaCajaResponseFX> getAllAuditorias() throws Exception;

    AuditoriaCajaResponseFX registrarAuditoriaManual(RegistrarAuditoriaRequestDTO request) throws Exception;
} 