package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl.LoginServiceImplFX;
import okhttp3.*;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class HttpClient {

    private static final int CONNECT_TIMEOUT = 5000; // 5 segundos
    private static final int READ_TIMEOUT = 5000; // 5 segundos
    private static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    // Cliente OkHttp como variable estática
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
            .readTimeout(READ_TIMEOUT, TimeUnit.MILLISECONDS)
            .build();

    // Método GET sin encabezados
    public static String get(String urlStr) throws Exception {
        return get(urlStr, null, null);
    }

    // Método GET con encabezados
    public static String get(String urlStr, String headerName, String headerValue) throws Exception {
        try {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(urlStr)
                    .get()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            // Añadir encabezado adicional si se proporciona
            if (headerName != null && headerValue != null) {
                requestBuilder.header(headerName, headerValue);
            }

            Request request = requestBuilder.build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body() != null ? response.body().string() : "";
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Sin detalles de error";
                    throw new Exception("Error HTTP " + response.code() + ": " + errorBody);
                }
            }
        } catch (IOException e) {
            throw new Exception("Error de conexión: " + e.getMessage());
        }
    }

    // Método POST sin encabezados
    public static String post(String urlStr, String jsonBody) throws Exception {
        return post(urlStr, jsonBody, null, null);
    }

    // Método POST con encabezados
    public static String post(String urlStr, String jsonBody, String headerName, String headerValue) throws Exception {
        try {
            RequestBody requestBody = jsonBody != null ?
                    RequestBody.create(jsonBody, JSON) :
                    RequestBody.create("", JSON);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(urlStr)
                    .post(requestBody)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            // Añadir encabezado adicional si se proporciona
            if (headerName != null && headerValue != null) {
                requestBuilder.header(headerName, headerValue);
            }

            Request request = requestBuilder.build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body() != null ? response.body().string() : "";
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Sin detalles de error";
                    throw new Exception("Error HTTP " + response.code() + ": " + errorBody);
                }
            }
        } catch (IOException e) {
            throw new Exception("Error de conexión: " + e.getMessage());
        }
    }

    // Método PUT sin encabezados
    public static String put(String urlStr, String jsonBody) throws Exception {
        return put(urlStr, jsonBody, null, null);
    }

    // Método PUT con encabezados
    public static String put(String urlStr, String jsonBody, String headerName, String headerValue) throws Exception {
        try {
            RequestBody requestBody = jsonBody != null ?
                    RequestBody.create(jsonBody, JSON) :
                    RequestBody.create("", JSON);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(urlStr)
                    .put(requestBody)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            // Añadir encabezado adicional si se proporciona
            if (headerName != null && headerValue != null) {
                requestBuilder.header(headerName, headerValue);
            }

            Request request = requestBuilder.build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body() != null ? response.body().string() : "";
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Sin detalles de error";
                    throw new Exception("Error HTTP " + response.code() + ": " + errorBody);
                }
            }
        } catch (IOException e) {
            throw new Exception("Error de conexión: " + e.getMessage());
        }
    }

    // Método DELETE sin encabezados
    public static String delete(String urlStr) throws Exception {
        return delete(urlStr, null, null);
    }

    // Método DELETE con encabezados
    public static String delete(String urlStr, String headerName, String headerValue) throws Exception {
        try {
            Request.Builder requestBuilder = new Request.Builder()
                    .url(urlStr)
                    .delete()
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            // Añadir encabezado adicional si se proporciona
            if (headerName != null && headerValue != null) {
                requestBuilder.header(headerName, headerValue);
            }

            Request request = requestBuilder.build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body() != null ? response.body().string() : "";
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Sin detalles de error";
                    throw new Exception("Error HTTP " + response.code() + ": " + errorBody);
                }
            }
        } catch (IOException e) {
            throw new Exception("Error de conexión: " + e.getMessage());
        }
    }

    // Método PATCH
    public static String patch(String urlStr, String jsonBody) throws Exception {
        try {
            RequestBody requestBody = jsonBody != null && !jsonBody.isEmpty() ?
                    RequestBody.create(jsonBody, JSON) :
                    RequestBody.create("", JSON);

            Request.Builder requestBuilder = new Request.Builder()
                    .url(urlStr)
                    .patch(requestBody)
                    .header("Content-Type", "application/json")
                    .header("Accept", "application/json");

            // Añadir token JWT si está disponible
            String token = LoginServiceImplFX.getToken();
            if (token != null && !token.isEmpty()) {
                requestBuilder.header("Authorization", "Bearer " + token);
            }

            Request request = requestBuilder.build();
            try (Response response = client.newCall(request).execute()) {
                if (response.isSuccessful()) {
                    return response.body() != null ? response.body().string() : "";
                } else {
                    String errorBody = response.body() != null ? response.body().string() : "Sin detalles de error";
                    throw new Exception("Error HTTP " + response.code() + ": " + errorBody);
                }
            }
        } catch (IOException e) {
            throw new Exception("Error de conexión: " + e.getMessage());
        }
    }
}