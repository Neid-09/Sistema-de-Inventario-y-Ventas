package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VentaCreateRequestFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IFacturaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class FacturaServiceImplFX implements IFacturaService {

    private static final String API_PATH = "/api/facturas";
    private final ObjectMapper objectMapper;

    public FacturaServiceImplFX() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public byte[] getFacturaPdfByIdVenta(int idVenta) throws IOException, InterruptedException {
        try {
            String url = ApiConfig.getBaseUrl() + API_PATH + "/" + idVenta + "/pdf";
            return HttpClient.getBytes(url);
        } catch (Exception e) {
            // Aquí puedes añadir logging más detallado si lo necesitas
            System.err.println("Error al obtener el PDF de la factura por ID: " + idVenta + ". Error: " + e.getMessage());
            throw new IOException("Error al obtener el PDF de la factura.", e); // Re-lanzar como IOException o una excepción personalizada
        }
    }

    @Override
    public byte[] getPreviewFacturaPdf(VentaCreateRequestFX ventaRequest) throws IOException, InterruptedException {
        try {
            String url = ApiConfig.getBaseUrl() + API_PATH + "/preview/pdf";
            String requestBody = objectMapper.writeValueAsString(ventaRequest);
            return HttpClient.postForBytes(url, requestBody);
        } catch (Exception e) {
            // Aquí puedes añadir logging más detallado si lo necesitas
             System.err.println("Error al obtener el PDF de previsualización.");
            throw new IOException("Error al obtener el PDF de previsualización.", e); // Re-lanzar como IOException o una excepción personalizada
        }
    }
} 