package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaRequest;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaResponse;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig; // Import ApiConfig
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.util.List;

public class VentaServiceImplFX implements IVentaSerivice {

    private static final String API_BASE_URL = ApiConfig.getBaseUrl(); // Use ApiConfig
    private static final String VENTAS_ENDPOINT = "/api/ventas";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public VentaResponse registrarVenta(VentaRequest ventaRequest) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(ventaRequest);
        String response = HttpClient.post(API_BASE_URL + VENTAS_ENDPOINT, jsonBody);
        return objectMapper.readValue(response, VentaResponse.class);
    }

    @Override
    public VentaResponse obtenerVentaPorId(Integer id) throws Exception {
        String response = HttpClient.get(API_BASE_URL + VENTAS_ENDPOINT + "/" + id);
        return objectMapper.readValue(response, VentaResponse.class);
    }

    @Override
    public List<VentaResponse> listarVentas() throws Exception {
        String response = HttpClient.get(API_BASE_URL + VENTAS_ENDPOINT);
        return objectMapper.readValue(response, new TypeReference<List<VentaResponse>>() {});
    }
}
