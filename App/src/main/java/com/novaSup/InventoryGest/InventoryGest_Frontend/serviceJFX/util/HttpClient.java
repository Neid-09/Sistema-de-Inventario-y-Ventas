package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    // Método GET sin encabezados
    public static String get(String urlStr) throws IOException {
        return get(urlStr, null, null);
    }

    // Método GET con encabezados
    public static String get(String urlStr, String headerName, String headerValue) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Content-Type", "application/json");

        // Añadir encabezado si se proporciona
        if (headerName != null && headerValue != null) {
            connection.setRequestProperty(headerName, headerValue);
        }

        return leerRespuesta(connection);
    }

    // Método POST sin encabezados
    public static String post(String urlStr, String jsonBody) throws IOException {
        return post(urlStr, jsonBody, null, null);
    }

    // Método POST con encabezados
    public static String post(String urlStr, String jsonBody, String headerName, String headerValue) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        // Añadir encabezado si se proporciona
        if (headerName != null && headerValue != null) {
            connection.setRequestProperty(headerName, headerValue);
        }

        connection.setDoOutput(true);

        // Escribir el cuerpo JSON
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return leerRespuesta(connection);
    }

    // Método PUT sin encabezados
    public static String put(String urlStr, String jsonBody) throws IOException {
        return put(urlStr, jsonBody, null, null);
    }

    // Método PUT con encabezados
    public static String put(String urlStr, String jsonBody, String headerName, String headerValue) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");

        // Añadir encabezado si se proporciona
        if (headerName != null && headerValue != null) {
            connection.setRequestProperty(headerName, headerValue);
        }

        connection.setDoOutput(true);

        // Escribir el cuerpo JSON
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return leerRespuesta(connection);
    }

    // Método DELETE sin encabezados
    public static String delete(String urlStr) throws IOException {
        return delete(urlStr, null, null);
    }

    // Método DELETE con encabezados
    public static String delete(String urlStr, String headerName, String headerValue) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();
        connection.setRequestMethod("DELETE");
        connection.setRequestProperty("Content-Type", "application/json");

        // Añadir encabezado si se proporciona
        if (headerName != null && headerValue != null) {
            connection.setRequestProperty(headerName, headerValue);
        }

        return leerRespuesta(connection);
    }

    // Método PATCH sin cuerpo (para operaciones que no requieren datos)
    public static String patch(String urlStr) throws IOException {
        return patch(urlStr, "{}", null, null);
    }

    // Método PATCH sin encabezados
    public static String patch(String urlStr, String jsonBody) throws IOException {
        return patch(urlStr, jsonBody, null, null);
    }

    // Método PATCH con encabezados
    public static String patch(String urlStr, String jsonBody, String headerName, String headerValue) throws IOException {
        HttpURLConnection connection = (HttpURLConnection) new URL(urlStr).openConnection();

        // Configurar para usar PATCH - Requiere esta configuración especial
        connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");

        // Añadir encabezado si se proporciona
        if (headerName != null && headerValue != null) {
            connection.setRequestProperty(headerName, headerValue);
        }

        connection.setDoOutput(true);

        // Escribir el cuerpo JSON
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }

        return leerRespuesta(connection);
    }

    // Método auxiliar para leer la respuesta
    private static String leerRespuesta(HttpURLConnection connection) throws IOException {
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            return response.toString();
        }
    }
}