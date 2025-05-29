package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.TasaImpuestoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ITasaImpuestoServiceFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;

public class TasaImpuestoServiceImplFX implements ITasaImpuestoServiceFX {

    private final String tasasImpuestosUrl;
    private final ObjectMapper objectMapper;

    public TasaImpuestoServiceImplFX() {
        this.tasasImpuestosUrl = ApiConfig.getBaseUrl() + "/api/tasas-impuestos";
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public ObservableList<TasaImpuestoFX> getAllTasasImpuestos() throws Exception {
        String jsonResponse = HttpClient.get(tasasImpuestosUrl);
        List<TasaImpuestoFX> lista = objectMapper.readValue(jsonResponse, new TypeReference<List<TasaImpuestoFX>>() {});
        return FXCollections.observableArrayList(lista);
    }

    @Override
    public TasaImpuestoFX getTasaImpuestoById(int id) throws Exception {
        String url = tasasImpuestosUrl + "/" + id;
        String jsonResponse = HttpClient.get(url);
        if (jsonResponse == null || jsonResponse.isEmpty()) {
            // Podría lanzar una excepción específica o devolver null si se espera
            // Por ahora, si HttpClient no lanzó excepción pero la respuesta es vacía,
            // asumimos que Jackson podría manejarlo o fallar si no es JSON válido.
        }
        return objectMapper.readValue(jsonResponse, TasaImpuestoFX.class);
    }

    @Override
    public TasaImpuestoFX createTasaImpuesto(TasaImpuestoFX tasaImpuesto) throws Exception {
        String jsonRequest = objectMapper.writeValueAsString(tasaImpuesto);
        String jsonResponse = HttpClient.post(tasasImpuestosUrl, jsonRequest);
        return objectMapper.readValue(jsonResponse, TasaImpuestoFX.class);
    }

    @Override
    public TasaImpuestoFX updateTasaImpuesto(int id, TasaImpuestoFX tasaImpuesto) throws Exception {
        String url = tasasImpuestosUrl + "/" + id;
        String jsonRequest = objectMapper.writeValueAsString(tasaImpuesto);
        String jsonResponse = HttpClient.put(url, jsonRequest);
        return objectMapper.readValue(jsonResponse, TasaImpuestoFX.class);
    }

    @Override
    public ObservableList<TasaImpuestoFX> getTasasImpuestosByTipo(int idTipoImpuesto) throws Exception {
        String url = tasasImpuestosUrl + "/por-tipo/" + idTipoImpuesto;
        String jsonResponse = HttpClient.get(url);
        List<TasaImpuestoFX> lista = objectMapper.readValue(jsonResponse, new TypeReference<List<TasaImpuestoFX>>() {});
        return FXCollections.observableArrayList(lista);
    }
} 