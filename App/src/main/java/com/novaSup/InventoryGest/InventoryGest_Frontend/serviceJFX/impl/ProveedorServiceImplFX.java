package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProveedorFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProveedorService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service  // Asegúrate de que tenga esta anotación
public class ProveedorServiceImplFX implements IProveedorService {

    private static final Logger logger = LoggerFactory.getLogger(ProveedorServiceImplFX.class);
    private static final String API_URL = ApiConfig.getBaseUrl() + "/api/proveedores";
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public List<ProveedorFX> obtenerTodos() throws Exception {
        try {
            String response = HttpClient.get(API_URL);
            List<ProveedorDTO> proveedores = mapper.readValue(response, new TypeReference<List<ProveedorDTO>>() {});
            return proveedores.stream()
                    .map(this::convertirAProveedorFX)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener todos los proveedores", e);
            throw new Exception("Error al obtener proveedores: " + e.getMessage());
        }
    }

    @Override
    public List<ProveedorFX> buscarPorNombreOCorreo(String termino) throws Exception {
        try {
            String url = API_URL + "/buscar?termino=" + termino;
            String response = HttpClient.get(url);
            List<ProveedorDTO> proveedores = mapper.readValue(response, new TypeReference<List<ProveedorDTO>>() {});
            return proveedores.stream()
                    .map(this::convertirAProveedorFX)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al buscar proveedores con término: {}", termino, e);
            throw new Exception("Error al buscar proveedores: " + e.getMessage());
        }
    }

    @Override
    public Optional<ProveedorFX> obtenerPorId(Integer id) throws Exception {
        try {
            String url = API_URL + "/" + id;
            String response = HttpClient.get(url);
            JsonNode root = mapper.readTree(response);
            JsonNode proveedorNode = root.get("proveedor");

            if (proveedorNode != null) {
                ProveedorDTO proveedor = mapper.treeToValue(proveedorNode, ProveedorDTO.class);
                return Optional.of(convertirAProveedorFX(proveedor));
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
    public ProveedorFX guardar(ProveedorFX proveedor) throws Exception {
        try {
            ProveedorDTO dto = convertirAProveedorDTO(proveedor);
            String jsonProveedor = mapper.writeValueAsString(dto);
            String response;

            if (proveedor.getIdProveedor() == null || proveedor.getIdProveedor() == 0) {
                // Es un nuevo proveedor
                response = HttpClient.post(API_URL, jsonProveedor);
            } else {
                // Es una actualización
                response = HttpClient.put(API_URL + "/" + proveedor.getIdProveedor(), jsonProveedor);
            }

            ProveedorDTO proveedorGuardado = mapper.readValue(response, ProveedorDTO.class);
            return convertirAProveedorFX(proveedorGuardado);
        } catch (Exception e) {
            logger.error("Error al guardar proveedor", e);
            throw new Exception("Error al guardar proveedor: " + e.getMessage());
        }
    }

    @Override
    public ProveedorFX actualizar(Integer id, ProveedorFX proveedor) throws Exception {
        try {
            // Crear un nuevo objeto ProveedorFX con el ID actualizado
            ProveedorFX proveedorConId = new ProveedorFX(
                    id,
                    proveedor.getNombre(),
                    proveedor.getContacto(),
                    proveedor.getTelefono(),
                    proveedor.getCorreo(),
                    proveedor.getDireccion()
            );

            ProveedorDTO dto = convertirAProveedorDTO(proveedorConId);
            String jsonProveedor = mapper.writeValueAsString(dto);
            String response = HttpClient.put(API_URL + "/" + id, jsonProveedor);
            ProveedorDTO proveedorActualizado = mapper.readValue(response, ProveedorDTO.class);
            return convertirAProveedorFX(proveedorActualizado);
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

    private ProveedorFX convertirAProveedorFX(ProveedorDTO dto) {
        return new ProveedorFX(
                dto.idProveedor,
                dto.nombre,
                dto.contacto,   // Ahora incluimos el campo contacto
                dto.telefono,
                dto.correo,
                dto.direccion
        );
    }

    private ProveedorDTO convertirAProveedorDTO(ProveedorFX fx) {
        ProveedorDTO dto = new ProveedorDTO();
        dto.idProveedor = fx.getIdProveedor();
        dto.nombre = fx.getNombre();
        dto.contacto = fx.getContacto(); // Añadimos el campo contacto
        dto.correo = fx.getCorreo();
        dto.telefono = fx.getTelefono();
        dto.direccion = fx.getDireccion();
        return dto;
    }

    // Clase interna para mapeo a/desde API
    private static class ProveedorDTO {
        public Integer idProveedor;
        public String nombre;
        public String contacto; // Añadimos el campo contacto
        public String correo;
        public String telefono;
        public String direccion;
    }
}