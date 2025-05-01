package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util;

public class ApiConfig {
    // URL base para las APIs (se puede cambiar según el entorno)
    private static final String BASE_URL = "http://localhost:8080";

    public static String getBaseUrl() {
        return BASE_URL;
    }
}