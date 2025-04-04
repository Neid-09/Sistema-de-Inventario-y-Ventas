package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.PermisoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.RolFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IRolService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RolServiceImplFX implements IRolService {

    private static final Logger logger = LoggerFactory.getLogger(RolServiceImplFX.class);
    private static final String BASE_URL = "http://localhost:8080/roles";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<RolFX> obtenerRoles() throws Exception {
        try {
            String response = HttpClient.get(BASE_URL);
            JsonNode rolesNode = mapper.readTree(response);
            List<RolFX> roles = new ArrayList<>();

            for (JsonNode rolNode : rolesNode) {
                try {
                    RolFX rol = convertirDeJSON(rolNode);
                    roles.add(rol);
                } catch (Exception e) {
                    logger.error("Error procesando rol: {}", e.getMessage());
                }
            }
            return roles;
        } catch (Exception e) {
            logger.error("Error al obtener roles: {}", e.getMessage());
            throw new Exception("Error al obtener roles: " + e.getMessage());
        }
    }

    @Override
    public RolFX obtenerRol(Integer id) throws Exception {
        try {
            String response = HttpClient.get(BASE_URL + "/" + id);
            JsonNode rolNode = mapper.readTree(response);
            return convertirDeJSON(rolNode);
        } catch (Exception e) {
            logger.error("Error al obtener rol: {}", e.getMessage());
            throw new Exception("Error al obtener rol: " + e.getMessage());
        }
    }

    @Override
    public RolFX crearRol(RolFX rol) throws Exception {
        try {
            ObjectNode rolDTO = mapper.createObjectNode();
            rolDTO.put("nombre", rol.getNombre());

            String response = HttpClient.post(
                    BASE_URL,
                    mapper.writeValueAsString(rolDTO)
            );

            JsonNode rolNode = mapper.readTree(response);
            return convertirDeJSON(rolNode);
        } catch (Exception e) {
            logger.error("Error al crear rol: {}", e.getMessage());
            throw new Exception("Error al crear rol: " + e.getMessage());
        }
    }

    @Override
    public RolFX actualizarRol(Integer id, RolFX rol) throws Exception {
        try {
            ObjectNode rolDTO = mapper.createObjectNode();
            rolDTO.put("nombre", rol.getNombre());

            String response = HttpClient.put(
                    BASE_URL + "/" + id,
                    mapper.writeValueAsString(rolDTO)
            );

            JsonNode rolNode = mapper.readTree(response);
            return convertirDeJSON(rolNode);
        } catch (Exception e) {
            logger.error("Error al actualizar rol: {}", e.getMessage());
            throw new Exception("Error al actualizar rol: " + e.getMessage());
        }
    }

    @Override
    public void eliminarRol(Integer id) throws Exception {
        try {
            HttpClient.delete(BASE_URL + "/" + id);
        } catch (Exception e) {
            logger.error("Error al eliminar rol: {}", e.getMessage());
            throw new Exception("Error al eliminar rol: " + e.getMessage());
        }
    }

    @Override
    public Set<PermisoFX> obtenerPermisosRol(Integer idRol) throws Exception {
        try {
            String response = HttpClient.get(BASE_URL + "/" + idRol + "/permisos");
            JsonNode permisosNode = mapper.readTree(response);
            Set<PermisoFX> permisos = new HashSet<>();

            for (JsonNode permisoNode : permisosNode) {
                Integer idPermiso = permisoNode.has("idPermiso") ? permisoNode.get("idPermiso").asInt() : null;
                String nombre = permisoNode.has("nombre") ? permisoNode.get("nombre").asText() : "";
                String descripcion = permisoNode.has("descripcion") ? permisoNode.get("descripcion").asText() : "";

                permisos.add(new PermisoFX(idPermiso, nombre, descripcion));
            }

            return permisos;
        } catch (Exception e) {
            logger.error("Error al obtener permisos del rol: {}", e.getMessage());
            throw new Exception("Error al obtener permisos del rol: " + e.getMessage());
        }
    }

    @Override
    public RolFX asignarPermisosRol(Integer idRol, List<Integer> permisosIds) throws Exception {
        try {
            ArrayNode permisosArray = mapper.createArrayNode();
            for (Integer id : permisosIds) {
                permisosArray.add(id);
            }

            String response = HttpClient.put(
                    BASE_URL + "/" + idRol + "/permisos",
                    mapper.writeValueAsString(permisosArray)
            );

            JsonNode rolNode = mapper.readTree(response);
            return convertirDeJSON(rolNode);
        } catch (Exception e) {
            logger.error("Error al asignar permisos al rol: {}", e.getMessage());
            throw new Exception("Error al asignar permisos al rol: " + e.getMessage());
        }
    }

    private RolFX convertirDeJSON(JsonNode rolNode) {
        Integer idRol = rolNode.has("idRol") ? rolNode.get("idRol").asInt() : null;
        String nombre = rolNode.has("nombre") ? rolNode.get("nombre").asText() : "";

        RolFX rol = new RolFX(idRol, nombre);

        // Procesar permisos si están incluidos
        if (rolNode.has("permisos") && !rolNode.get("permisos").isNull()) {
            Set<PermisoFX> permisos = new HashSet<>();
            for (JsonNode permisoNode : rolNode.get("permisos")) {
                Integer idPermiso = permisoNode.has("idPermiso") ? permisoNode.get("idPermiso").asInt() : null;
                String nombrePermiso = permisoNode.has("nombre") ? permisoNode.get("nombre").asText() : "";
                String descripcion = permisoNode.has("descripcion") ? permisoNode.get("descripcion").asText() : "";

                permisos.add(new PermisoFX(idPermiso, nombrePermiso, descripcion));
            }
            rol.setPermisos(permisos);
        }

        return rol;
    }
}