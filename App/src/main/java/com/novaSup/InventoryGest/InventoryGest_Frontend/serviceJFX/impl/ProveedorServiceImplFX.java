package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Proveedor;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProveedorService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ProveedorServiceImplFX implements IProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorServiceImplFX.class);
    private static final String API_URL = ApiConfig.getBaseUrl() + "/api/proveedores";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<Proveedor> obtenerTodos() throws Exception {
        try {
            String response = HttpClient.get(API_URL);
            return mapper.readValue(response, new TypeReference<List<Proveedor>>() {});
        } catch (Exception e) {
            logger.error("Error al obtener todos los proveedores", e);
            throw new Exception("Error al obtener proveedores: " + e.getMessage());
        }
    }

    @Override
    public List<Proveedor> buscarPorNombreOCorreo(String termino) throws Exception {
        try {
            String url = API_URL + "/buscar?termino=" + termino;
            String response = HttpClient.get(url);
            return mapper.readValue(response, new TypeReference<List<Proveedor>>() {});
        } catch (Exception e) {
            logger.error("Error al buscar proveedores con término: {}", termino, e);
            throw new Exception("Error al buscar proveedores: " + e.getMessage());
        }
    }

    @Override
    public Optional<Proveedor> obtenerPorId(Integer id) throws Exception {
        try {
            String url = API_URL + "/" + id;
            String response = HttpClient.get(url);
            JsonNode root = mapper.readTree(response);
            JsonNode proveedorNode = root.get("proveedor");

            if (proveedorNode != null) {
                Proveedor proveedor = mapper.treeToValue(proveedorNode, Proveedor.class);
                return Optional.of(proveedor);
            }
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error al obtener proveedor con ID: {}", id, e);
            if (e.getMessage().contains("404")) {
                return Optional.empty();
            }
            throw new Exception("Error al obtener proveedor: " + e.getMessage());
        }
    }

    @Override
    public Proveedor guardar(Proveedor proveedor) throws Exception {
        try {
            String jsonProveedor = mapper.writeValueAsString(proveedor);
            String response;

            if (proveedor.getIdProveedor() == null) {
                // Es un nuevo proveedor
                response = HttpClient.post(API_URL, jsonProveedor);
            } else {
                // Es una actualización
                response = HttpClient.put(API_URL + "/" + proveedor.getIdProveedor(), jsonProveedor);
            }

            return mapper.readValue(response, Proveedor.class);
        } catch (Exception e) {
            logger.error("Error al guardar proveedor: {}", proveedor, e);
            throw new Exception("Error al guardar proveedor: " + e.getMessage());
        }
    }

    @Override
    public Proveedor actualizar(Integer id, Proveedor proveedor) throws Exception {
        try {
            proveedor.setIdProveedor(id);
            String jsonProveedor = mapper.writeValueAsString(proveedor);
            String response = HttpClient.put(API_URL + "/" + id, jsonProveedor);
            return mapper.readValue(response, Proveedor.class);
        } catch (Exception e) {
            logger.error("Error al actualizar proveedor con ID: {}", id, e);
            throw new Exception("Error al actualizar proveedor: " + e.getMessage());
        }
    }

    @Override
    public boolean eliminar(Integer id) throws Exception {
        try {
            HttpClient.delete(API_URL + "/" + id);
            return true;
        } catch (Exception e) {
            logger.error("Error al eliminar proveedor con ID: {}", id, e);

            // Si contiene mensaje de productos asociados, lanzar excepción específica
            if (e.getMessage().contains("productos asociados")) {
                throw new Exception("No se puede eliminar el proveedor porque tiene productos asociados");
            }

            throw new Exception("Error al eliminar proveedor: " + e.getMessage());
        }
    }

    @Override
    public int obtenerCantidadProductosAsociados(Integer id) throws Exception {
        try {
            String url = API_URL + "/" + id;
            String response = HttpClient.get(url);
            JsonNode root = mapper.readTree(response);
            JsonNode cantidadNode = root.get("cantidadProductos");

            if (cantidadNode != null) {
                return cantidadNode.asInt();
            }
            return 0;
        } catch (Exception e) {
            logger.error("Error al obtener cantidad de productos para proveedor ID: {}", id, e);
            return 0; // Devolver 0 en caso de error
        }
    }
}