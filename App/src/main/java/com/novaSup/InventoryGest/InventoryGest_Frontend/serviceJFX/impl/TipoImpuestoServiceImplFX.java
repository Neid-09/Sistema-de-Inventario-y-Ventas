package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TipoImpuestoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ITipoImpuestoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class TipoImpuestoServiceImplFX implements ITipoImpuestoService {

    private final ObjectMapper objectMapper;
    private final String baseUrl;

    public TipoImpuestoServiceImplFX() {
        this.objectMapper = new ObjectMapper();
        this.baseUrl = ApiConfig.getBaseUrl() + "/api/tipos-impuestos";
    }

    @Override
    public List<TipoImpuestoFX> getAllTiposImpuestos() throws Exception {
        String response = HttpClient.get(baseUrl);
        return objectMapper.readValue(response, new TypeReference<List<TipoImpuestoFX>>() {});
    }

    @Override
    public Optional<TipoImpuestoFX> getTipoImpuestoById(int id) throws Exception {
        try {
            String response = HttpClient.get(baseUrl + "/" + id);
            TipoImpuestoFX tipoImpuesto = objectMapper.readValue(response, TipoImpuestoFX.class);
            return Optional.of(tipoImpuesto);
        } catch (IOException e) {
            System.err.println("Error al obtener tipo de impuesto por ID " + id + ": " + e.getMessage());
            return Optional.empty(); 
        }
    }

    @Override
    public TipoImpuestoFX createTipoImpuesto(TipoImpuestoFX tipoImpuesto) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(tipoImpuesto);
        String response = HttpClient.post(baseUrl, jsonBody);
        return objectMapper.readValue(response, TipoImpuestoFX.class);
    }

    @Override
    public TipoImpuestoFX updateTipoImpuesto(int id, TipoImpuestoFX tipoImpuesto) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(tipoImpuesto);
        String response = HttpClient.put(baseUrl + "/" + id, jsonBody);
        return objectMapper.readValue(response, TipoImpuestoFX.class);
    }

    @Override
    public TipoImpuestoFX changeTipoImpuestoEstado(int id) throws Exception {
        String response = HttpClient.patch(baseUrl + "/" + id + "/cambiar-estado", null);
        return objectMapper.readValue(response, TipoImpuestoFX.class);
    }
} 