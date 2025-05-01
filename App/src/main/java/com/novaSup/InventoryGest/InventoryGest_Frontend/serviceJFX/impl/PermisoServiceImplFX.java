package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.PermisoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IPermisoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class PermisoServiceImplFX implements IPermisoService {

    private static final Logger logger = LoggerFactory.getLogger(PermisoServiceImplFX.class);
    private static final String BASE_URL = "http://localhost:8080/permisos";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<PermisoFX> obtenerPermisos() throws Exception {
        try {
            String response = HttpClient.get(BASE_URL);
            JsonNode permisosNode = mapper.readTree(response);
            List<PermisoFX> permisos = new ArrayList<>();

            for (JsonNode permisoNode : permisosNode) {
                try {
                    PermisoFX permiso = convertirDeJSON(permisoNode);
                    permisos.add(permiso);
                } catch (Exception e) {
                    logger.error("Error procesando permiso: {}", e.getMessage());
                }
            }
            return permisos;
        } catch (Exception e) {
            String mensaje = e.getMessage() != null ? e.getMessage() : "Sin mensaje de error";
            logger.error("Error al obtener permisos: {}", mensaje);
            throw new Exception("Error al obtener permisos: " + mensaje);
        }
    }

    @Override
    public PermisoFX obtenerPermiso(Integer id) throws Exception {
        try {
            String response = HttpClient.get(BASE_URL + "/" + id);
            JsonNode permisoNode = mapper.readTree(response);
            return convertirDeJSON(permisoNode);
        } catch (Exception e) {
            String mensaje = e.getMessage() != null ? e.getMessage() : "Sin mensaje de error";
            logger.error("Error al obtener permiso: {}", mensaje);
            throw new Exception("Error al obtener permiso: " + mensaje);
        }
    }

    @Override
    public PermisoFX crearPermiso(PermisoFX permiso) throws Exception {
        try {
            ObjectNode permisoDTO = mapper.createObjectNode();
            permisoDTO.put("nombre", permiso.getNombre());
            permisoDTO.put("descripcion", permiso.getDescripcion());

            String response = HttpClient.post(
                    BASE_URL,
                    mapper.writeValueAsString(permisoDTO)
            );

            JsonNode permisoNode = mapper.readTree(response);
            return convertirDeJSON(permisoNode);
        } catch (Exception e) {
            logger.error("Error al crear permiso: {}", e.getMessage());
            throw new Exception("Error al crear permiso: " + e.getMessage());
        }
    }

    @Override
    public PermisoFX actualizarPermiso(Integer id, PermisoFX permiso) throws Exception {
        try {
            ObjectNode permisoDTO = mapper.createObjectNode();
            permisoDTO.put("nombre", permiso.getNombre());
            permisoDTO.put("descripcion", permiso.getDescripcion());

            String response = HttpClient.put(
                    BASE_URL + "/" + id,
                    mapper.writeValueAsString(permisoDTO)
            );

            JsonNode permisoNode = mapper.readTree(response);
            return convertirDeJSON(permisoNode);
        } catch (Exception e) {
            logger.error("Error al actualizar permiso: {}", e.getMessage());
            throw new Exception("Error al actualizar permiso: " + e.getMessage());
        }
    }

    @Override
    public void eliminarPermiso(Integer id) throws Exception {
        try {
            HttpClient.delete(BASE_URL + "/" + id);
        } catch (Exception e) {
            logger.error("Error al eliminar permiso: {}", e.getMessage());
            throw new Exception("Error al eliminar permiso: " + e.getMessage());
        }
    }

    private PermisoFX convertirDeJSON(JsonNode permisoNode) {
        // Cambiar de "id" a "idPermiso" para que coincida con el backend
        Integer id = permisoNode.has("idPermiso") ? permisoNode.get("idPermiso").asInt() : null;
        String nombre = permisoNode.has("nombre") ? permisoNode.get("nombre").asText() : "";
        String descripcion = permisoNode.has("descripcion") ? permisoNode.get("descripcion").asText() : "";

        return new PermisoFX(id, nombre, descripcion);
    }
}