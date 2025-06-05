package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ResumenInventarioDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ProductoStockCriticoDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ProductoSobrestockDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IReporteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Implementación del servicio de reportes para JavaFX.
 * Maneja la comunicación con la API de reportes del backend de forma asíncrona.
 */
public class ReporteServiceImplFX implements IReporteService {

    private final String API_URL;
    private final ObjectMapper objectMapper;

    public ReporteServiceImplFX() {
        API_URL = ApiConfig.getBaseUrl() + "/api/reportes";
        
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public CompletableFuture<ResumenInventarioDTO> obtenerResumenInventario() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String respuesta = HttpClient.get(API_URL + "/inventario/resumen");
                return objectMapper.readValue(respuesta, ResumenInventarioDTO.class);
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener el resumen del inventario: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<ProductoStockCriticoDTO>> obtenerProductosStockCritico() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String respuesta = HttpClient.get(API_URL + "/productos/stock-critico");
                return objectMapper.readValue(respuesta, 
                        new TypeReference<List<ProductoStockCriticoDTO>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener productos con stock crítico: " + e.getMessage(), e);
            }
        });
    }

    @Override
    public CompletableFuture<List<ProductoSobrestockDTO>> obtenerProductosSobrestock() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String respuesta = HttpClient.get(API_URL + "/productos/sobrestock");
                return objectMapper.readValue(respuesta, 
                        new TypeReference<List<ProductoSobrestockDTO>>() {});
            } catch (Exception e) {
                throw new RuntimeException("Error al obtener productos con sobrestock: " + e.getMessage(), e);
            }
        });
    }
}
