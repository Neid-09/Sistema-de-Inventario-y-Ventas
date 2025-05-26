package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.*;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.*;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICajaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.util.List;
import java.util.Optional;

public class CajaServiceImplFX implements ICajaService {

    private static final String BASE_URL = ApiConfig.getBaseUrl() + "/api";
    private final ObjectMapper objectMapper;

    public CajaServiceImplFX() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Para manejar LocalDateTime
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public CajaResponseFX abrirCaja(AbrirCajaRequestDTO request) throws Exception {
        String url = BASE_URL + "/cajas/abrir";
        String jsonBody = objectMapper.writeValueAsString(request);
        String responseBody = HttpClient.post(url, jsonBody);
        return objectMapper.readValue(responseBody, CajaResponseFX.class);
    }

    @Override
    public CajaResponseFX cerrarCaja(Integer idCaja, CerrarCajaRequestDTO request) throws Exception {
        String url = BASE_URL + "/cajas/cerrar/" + idCaja;
        String jsonBody = objectMapper.writeValueAsString(request);
        String responseBody = HttpClient.post(url, jsonBody);
        return objectMapper.readValue(responseBody, CajaResponseFX.class);
    }

    @Override
    public Optional<CajaResponseFX> getCajaAbiertaPorUsuario(Integer idUsuario) throws Exception {
        String url = BASE_URL + "/cajas/abierta/" + idUsuario;
        try {
            String responseBody = HttpClient.get(url);
            if (responseBody == null || responseBody.trim().isEmpty()) {
                 return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(responseBody, CajaResponseFX.class));
        } catch (Exception e) {
             return Optional.empty();
        }
    }

    @Override
    public List<CajaResponseFX> getAllCajas() throws Exception {
        String url = BASE_URL + "/cajas";
        String responseBody = HttpClient.get(url);
        return objectMapper.readValue(responseBody, new TypeReference<List<CajaResponseFX>>() {});
    }

    @Override
    public Optional<CajaResponseFX> getCajaById(Integer idCaja) throws Exception {
        String url = BASE_URL + "/cajas/" + idCaja;
         try {
            String responseBody = HttpClient.get(url);
             if (responseBody == null || responseBody.trim().isEmpty()) {
                 return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(responseBody, CajaResponseFX.class));
        } catch (Exception e) {
             return Optional.empty();
        }
    }

    @Override
    public List<MovimientoCajaResponseFX> getMovimientosByCajaId(Integer idCaja) throws Exception {
        String url = BASE_URL + "/movimientos-caja/caja/" + idCaja;
        String responseBody = HttpClient.get(url);
        return objectMapper.readValue(responseBody, new TypeReference<List<MovimientoCajaResponseFX>>() {});
    }

    @Override
    public MovimientoCajaResponseFX registrarMovimientoManual(RegistrarMovimientoManualRequestDTO request) throws Exception {
        String url = BASE_URL + "/movimientos-caja";
        String jsonBody = objectMapper.writeValueAsString(request);
        String responseBody = HttpClient.post(url, jsonBody);
        return objectMapper.readValue(responseBody, MovimientoCajaResponseFX.class);
    }

    @Override
    public Optional<CajaReporteConsolidadoFX> getReporteConsolidadoByCajaId(Integer idCaja) throws Exception {
        String url = BASE_URL + "/movimientos-caja/caja/" + idCaja + "/reporte-consolidado";
         try {
            String responseBody = HttpClient.get(url);
             if (responseBody == null || responseBody.trim().isEmpty()) {
                 return Optional.empty();
            }
            return Optional.of(objectMapper.readValue(responseBody, CajaReporteConsolidadoFX.class));
        } catch (Exception e) {
             return Optional.empty();
        }
    }

    @Override
    public List<AuditoriaCajaResponseFX> getAuditoriasByCajaId(Integer idCaja) throws Exception {
        String url = BASE_URL + "/auditorias-caja/caja/" + idCaja;
        String responseBody = HttpClient.get(url);
        return objectMapper.readValue(responseBody, new TypeReference<List<AuditoriaCajaResponseFX>>() {});
    }

    @Override
    public List<AuditoriaCajaResponseFX> getAllAuditorias() throws Exception {
        String url = BASE_URL + "/auditorias-caja";
        String responseBody = HttpClient.get(url);
        return objectMapper.readValue(responseBody, new TypeReference<List<AuditoriaCajaResponseFX>>() {});
    }

    @Override
    public AuditoriaCajaResponseFX registrarAuditoriaManual(RegistrarAuditoriaRequestDTO request) throws Exception {
        String url = BASE_URL + "/auditorias-caja";
        String jsonBody = objectMapper.writeValueAsString(request);
        String responseBody = HttpClient.post(url, jsonBody);
        return objectMapper.readValue(responseBody, AuditoriaCajaResponseFX.class);
    }
} 