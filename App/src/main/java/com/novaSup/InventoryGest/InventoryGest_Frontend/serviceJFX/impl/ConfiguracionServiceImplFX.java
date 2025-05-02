package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ConfiguracionFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IConfiguracionServiceFX;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionServiceImplFX implements IConfiguracionServiceFX {
    private static final String BASE_URL = "http://localhost:8080/api/configuraciones";
    private static final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<ConfiguracionFX> listarConfiguraciones() throws Exception {
        String response = HttpClient.get(BASE_URL);
        List<ConfiguracionFX> configuraciones = new ArrayList<>();

        JsonNode jsonArray = mapper.readTree(response);
        for (JsonNode node : jsonArray) {
            Integer id = node.has("idConfig") ? node.get("idConfig").asInt() : null;
            String clave = node.has("clave") ? node.get("clave").asText() : "";
            String valor = node.has("valor") ? node.get("valor").asText() : "";
            String descripcion = node.has("descripcion") ? node.get("descripcion").asText() : "";

            configuraciones.add(new ConfiguracionFX(id, clave, valor, descripcion));
        }

        return configuraciones;
    }

    @Override
    public List<ConfiguracionFX> buscarConfiguracionesPorClave(String clave) throws Exception {
        String response = HttpClient.get(BASE_URL + "/buscar?clave=" + clave);
        List<ConfiguracionFX> configuraciones = new ArrayList<>();

        JsonNode jsonArray = mapper.readTree(response);
        for (JsonNode node : jsonArray) {
            Integer id = node.has("idConfig") ? node.get("idConfig").asInt() : null;
            String claveConfig = node.has("clave") ? node.get("clave").asText() : "";
            String valor = node.has("valor") ? node.get("valor").asText() : "";
            String descripcion = node.has("descripcion") ? node.get("descripcion").asText() : "";

            configuraciones.add(new ConfiguracionFX(id, claveConfig, valor, descripcion));
        }

        return configuraciones;
    }

    @Override
    public ConfiguracionFX obtenerConfiguracionPorId(Integer id) throws Exception {
        String response = HttpClient.get(BASE_URL + "/" + id);
        JsonNode node = mapper.readTree(response);

        Integer idConfig = node.has("idConfig") ? node.get("idConfig").asInt() : null;
        String clave = node.has("clave") ? node.get("clave").asText() : "";
        String valor = node.has("valor") ? node.get("valor").asText() : "";
        String descripcion = node.has("descripcion") ? node.get("descripcion").asText() : "";

        return new ConfiguracionFX(idConfig, clave, valor, descripcion);
    }

    @Override
    public ConfiguracionFX guardarConfiguracion(ConfiguracionFX configuracion) throws Exception {
        String json = mapper.writeValueAsString(configuracionToMap(configuracion));
        String url;
        String response;

        try {
            // Verificar si es null O si es menor o igual a 0 para considerarlo nuevo
            if (configuracion.getIdConfig() == null || configuracion.getIdConfig() <= 0) {
                // Si es una nueva configuración (POST)
                url = BASE_URL;
                System.out.println("Realizando POST a: " + url);
                System.out.println("Datos: " + json);
                response = HttpClient.post(url, json);
            } else {
                // Si es una actualización (PUT)
                url = BASE_URL + "/" + configuracion.getIdConfig();
                System.out.println("Realizando PUT a: " + url);
                System.out.println("Datos: " + json);
                response = HttpClient.put(url, json);
            }

            System.out.println("Respuesta: " + response);
            JsonNode node = mapper.readTree(response);

            Integer idConfig = node.has("idConfig") ? node.get("idConfig").asInt() : null;
            String clave = node.has("clave") ? node.get("clave").asText() : "";
            String valor = node.has("valor") ? node.get("valor").asText() : "";
            String descripcion = node.has("descripcion") ? node.get("descripcion").asText() : "";

            return new ConfiguracionFX(idConfig, clave, valor, descripcion);
        } catch (Exception e) {
            System.err.println("Error en la comunicación: " + e.getMessage());
            throw new Exception("Error al guardar la configuración: " + e.getMessage());
        }
    }

    @Override
    public void eliminarConfiguracion(Integer id) throws Exception {
        HttpClient.delete(BASE_URL + "/" + id);
    }

    private java.util.Map<String, Object> configuracionToMap(ConfiguracionFX configuracion) {
        java.util.Map<String, Object> map = new java.util.HashMap<>();
        // Solo incluir ID si es mayor que 0
        if (configuracion.getIdConfig() != null && configuracion.getIdConfig() > 0) {
            map.put("idConfig", configuracion.getIdConfig());
        }
        map.put("clave", configuracion.getClave());
        map.put("valor", configuracion.getValor());
        map.put("descripcion", configuracion.getDescripcion() != null ?
                configuracion.getDescripcion() : "");
        return map;
    }
}