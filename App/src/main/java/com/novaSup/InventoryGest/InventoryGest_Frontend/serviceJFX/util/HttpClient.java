package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class HttpClient {

    private static final int CONNECT_TIMEOUT = 5000; // 5 segundos
    private static final int READ_TIMEOUT = 5000; // 5 segundos

    // Método GET sin encabezados
    public static String get(String urlStr) throws Exception {
        return get(urlStr, null, null);
    }

    // Método GET con encabezados
    public static String get(String urlStr, String headerName, String headerValue) throws Exception {
        HttpURLConnection connection = null;
        try {
            // Configurar conexión
            URL apiUrl = new URL(urlStr);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Añadir encabezado adicional si se proporciona
            if (headerName != null && headerValue != null) {
                connection.setRequestProperty(headerName, headerValue);
            }
            // Establecer timeouts
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            // Añadir encabezado si se proporciona
            if (headerName != null && headerValue != null) {
                connection.setRequestProperty(headerName, headerValue);
            }

            // Verificar código de respuesta
            int responseCode = connection.getResponseCode();

            if (responseCode == HttpURLConnection.HTTP_OK) {
                // Leer respuesta
                BufferedReader reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return response.toString();
            } else {
                // Leer mensaje de error si está disponible
                String errorMessage;
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    errorMessage = errorResponse.toString();
                } catch (Exception e) {
                    errorMessage = "Sin detalles de error";
                }

                throw new Exception("Error HTTP " + responseCode + ": " + errorMessage);
            }
        } catch (SocketTimeoutException e) {
            throw new Exception("Tiempo de espera agotado conectando al servidor");
        } catch (ConnectException e) {
            throw new Exception("No se pudo conectar al servidor: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage() != null ? e.getMessage() : "Error de conexión");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Método POST con encabezados
    public static String post(String urlStr, String jsonBody, String headerName, String headerValue) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL apiUrl = new URL(urlStr);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Establecer timeouts
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            // Añadir encabezado adicional si se proporciona
            if (headerName != null && headerValue != null) {
                connection.setRequestProperty(headerName, headerValue);
            }

            connection.setDoOutput(true);

            // Escribir el cuerpo JSON
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Verificar código de respuesta
            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine);
                    }
                    return response.toString();
                }
            } else {
                // Leer mensaje de error
                String errorMessage;
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    errorMessage = errorResponse.toString();
                } catch (Exception e) {
                    errorMessage = "Sin detalles de error";
                }

                throw new Exception("Error HTTP " + responseCode + ": " + errorMessage);
            }
        } catch (SocketTimeoutException e) {
            throw new Exception("Tiempo de espera agotado conectando al servidor");
        } catch (ConnectException e) {
            throw new Exception("No se pudo conectar al servidor: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage() != null ? e.getMessage() : "Error de conexión");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Método POST sin encabezados
    public static String post(String urlStr, String jsonBody) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL apiUrl = new URL(urlStr);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Establecer timeouts
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            connection.setDoOutput(true);

            // Escribir el cuerpo JSON
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Verificar código de respuesta
            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine);
                    }
                    return response.toString();
                }
            } else {
                // Leer mensaje de error
                String errorMessage;
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorMessage = errorResponse.toString();
                } catch (Exception e) {
                    errorMessage = "Sin detalles de error";
                }

                throw new Exception("Error HTTP " + responseCode + ": " + errorMessage);
            }
        } catch (SocketTimeoutException e) {
            throw new Exception("Tiempo de espera agotado conectando al servidor");
        } catch (ConnectException e) {
            throw new Exception("No se pudo conectar al servidor: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage() != null ? e.getMessage() : "Error de conexión");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Método PUT sin encabezados
    public static String put(String urlStr, String jsonBody) throws Exception {
        return put(urlStr, jsonBody, null, null);
    }

    // Método PUT con encabezados
    public static String put(String urlStr, String jsonBody, String headerName, String headerValue) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL apiUrl = new URL(urlStr);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("PUT");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Establecer timeouts
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            // Añadir encabezado adicional si se proporciona
            if (headerName != null && headerValue != null) {
                connection.setRequestProperty(headerName, headerValue);
            }

            connection.setDoOutput(true);

            // Escribir el cuerpo JSON
            try (OutputStream os = connection.getOutputStream()) {
                byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Verificar código de respuesta
            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine);
                    }
                    return response.toString();
                }
            } else {
                // Leer mensaje de error
                String errorMessage;
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorMessage = errorResponse.toString();
                } catch (Exception e) {
                    errorMessage = "Sin detalles de error";
                }

                throw new Exception("Error HTTP " + responseCode + ": " + errorMessage);
            }
        } catch (SocketTimeoutException e) {
            throw new Exception("Tiempo de espera agotado conectando al servidor");
        } catch (ConnectException e) {
            throw new Exception("No se pudo conectar al servidor: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage() != null ? e.getMessage() : "Error de conexión");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Método DELETE con encabezados
    public static String delete(String urlStr, String headerName, String headerValue) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL apiUrl = new URL(urlStr);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("DELETE");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Establecer timeouts
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            // Añadir encabezado adicional si se proporciona
            if (headerName != null && headerValue != null) {
                connection.setRequestProperty(headerName, headerValue);
            }

            // Verificar código de respuesta
            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine);
                    }
                    return response.toString();
                }
            } else {
                // Leer mensaje de error
                String errorMessage;
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String line;
                    while ((line = br.readLine()) != null) {
                        errorResponse.append(line);
                    }
                    errorMessage = errorResponse.toString();
                } catch (Exception e) {
                    errorMessage = "Sin detalles de error";
                }

                throw new Exception("Error HTTP " + responseCode + ": " + errorMessage);
            }
        } catch (SocketTimeoutException e) {
            throw new Exception("Tiempo de espera agotado conectando al servidor");
        } catch (ConnectException e) {
            throw new Exception("No se pudo conectar al servidor: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage() != null ? e.getMessage() : "Error de conexión");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    // Método DELETE sin encabezados
    public static String delete(String urlStr) throws Exception {
        return delete(urlStr, null, null);
    }

    // Método PATCH para añadir al HttpClient
    public static String patch(String urlStr, String jsonBody) throws Exception {
        HttpURLConnection connection = null;
        try {
            URL apiUrl = new URL(urlStr);
            connection = (HttpURLConnection) apiUrl.openConnection();
            connection.setRequestMethod("PATCH");
            connection.setRequestProperty("X-HTTP-Method-Override", "PATCH");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                connection.setRequestProperty("Authorization", "Bearer " + token);
            }

            // Establecer timeouts
            connection.setConnectTimeout(CONNECT_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            connection.setDoOutput(true);

            // Escribir el cuerpo JSON si no está vacío
            if (jsonBody != null && !jsonBody.isEmpty()) {
                try (OutputStream os = connection.getOutputStream()) {
                    byte[] input = jsonBody.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }
            }

            // Verificar código de respuesta
            int responseCode = connection.getResponseCode();

            if (responseCode >= 200 && responseCode < 300) {
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8))) {
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine);
                    }
                    return response.toString();
                }
            } else {
                // Leer mensaje de error
                String errorMessage;
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(connection.getErrorStream(), StandardCharsets.UTF_8))) {
                    StringBuilder errorResponse = new StringBuilder();
                    String errorLine;
                    while ((errorLine = br.readLine()) != null) {
                        errorResponse.append(errorLine);
                    }
                    errorMessage = errorResponse.toString();
                } catch (Exception e) {
                    errorMessage = "Sin detalles de error";
                }

                throw new Exception("Error HTTP " + responseCode + ": " + errorMessage);
            }
        } catch (SocketTimeoutException e) {
            throw new Exception("Tiempo de espera agotado conectando al servidor");
        } catch (ConnectException e) {
            throw new Exception("No se pudo conectar al servidor: " + e.getMessage());
        } catch (Exception e) {
            throw new Exception(e.getMessage() != null ? e.getMessage() : "Error de conexión");
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}