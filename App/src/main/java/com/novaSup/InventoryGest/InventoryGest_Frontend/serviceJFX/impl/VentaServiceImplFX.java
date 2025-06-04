package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaCreateRequestFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVentaSerivice;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.util.List;

public class VentaServiceImplFX implements IVentaSerivice {

    private static final String API_BASE_URL = ApiConfig.getBaseUrl();
    private static final String VENTAS_ENDPOINT = "/api/ventas";
    private final ObjectMapper objectMapper;

    public VentaServiceImplFX() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public VentaFX registrarVenta(VentaCreateRequestFX ventaRequest) throws Exception {
        String jsonBody = objectMapper.writeValueAsString(ventaRequest);
        String response = HttpClient.post(API_BASE_URL + VENTAS_ENDPOINT, jsonBody);
        return objectMapper.readValue(response, VentaFX.class);
    }

    @Override
    public VentaFX obtenerVentaPorId(Integer id) throws Exception {
        String response = HttpClient.get(API_BASE_URL + VENTAS_ENDPOINT + "/" + id);
        return objectMapper.readValue(response, VentaFX.class);
    }

    @Override
    public List<VentaFX> listarVentas() throws Exception {
        String response = HttpClient.get(API_BASE_URL + VENTAS_ENDPOINT);
        return objectMapper.readValue(response, new TypeReference<List<VentaFX>>() {});
    }

    @Override
    public List<VentaFX> listarVentasPorCliente(Integer clienteId) throws Exception {
        String response = HttpClient.get(API_BASE_URL + VENTAS_ENDPOINT + "/cliente/" + clienteId);
        return objectMapper.readValue(response, new TypeReference<List<VentaFX>>() {});
    }
}
