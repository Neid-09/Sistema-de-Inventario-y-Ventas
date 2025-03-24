package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpClient {

    public static String get(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/json");

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new RuntimeException("Error HTTP: " + responseCode);
        }

        return leerRespuesta(connection);
    }

    public static String post(String urlStr, String jsonBody) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        connection.getOutputStream().write(jsonBody.getBytes());

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new RuntimeException("Error HTTP: " + responseCode);
        }

        return leerRespuesta(connection);
    }

    public static String put(String urlStr, String jsonBody) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PUT");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        connection.getOutputStream().write(jsonBody.getBytes());

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new RuntimeException("Error HTTP: " + responseCode);
        }

        return leerRespuesta(connection);
    }

    public static void delete(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("DELETE");

        if (connection.getResponseCode() != 204) {
            throw new RuntimeException("Error HTTP: " + connection.getResponseCode());
        }
    }

    public static String patch(String urlStr, String jsonBody) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("PATCH");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setDoOutput(true);

        if (jsonBody != null) {
            connection.getOutputStream().write(jsonBody.getBytes());
        }

        int responseCode = connection.getResponseCode();
        if (responseCode < 200 || responseCode >= 300) {
            throw new RuntimeException("Error HTTP: " + responseCode);
        }

        return leerRespuesta(connection);
    }

    public static String patch(String urlStr) throws Exception {
        return patch(urlStr, null);
    }


    private static String leerRespuesta(HttpURLConnection connection) throws Exception {
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        StringBuilder response = new StringBuilder();
        String line;

        while ((line = reader.readLine()) != null) {
            response.append(line);
        }
        reader.close();

        return response.toString();
    }
}