package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties; // Necesario para ignorar propiedades desconocidas
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
// Eliminar la importación del modelo backend: import com.novaSup.InventoryGest.InventoryGest_Backend.model.Categoria;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICategoriaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class CategoriaServiceImplFX implements ICategoriaService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = ApiConfig.getBaseUrl() + "/api/categorias";

    @Override
    public List<CategoriaFX> obtenerTodas() throws Exception {
        String response = HttpClient.get(baseUrl);
        // Deserializar en la lista de DTOs
        List<CategoriaDTO> categoriasDTO = objectMapper.readValue(response, new TypeReference<List<CategoriaDTO>>() {});
        return categoriasDTO.stream()
                .map(this::convertirAModeloFX)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaFX> buscarPorNombre(String nombre) throws Exception {
        String url = baseUrl + "/buscar?nombre=" + nombre;
        String response = HttpClient.get(url);
        // Deserializar en la lista de DTOs
        List<CategoriaDTO> categoriasDTO = objectMapper.readValue(response, new TypeReference<List<CategoriaDTO>>() {});
        return categoriasDTO.stream()
                .map(this::convertirAModeloFX)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoriaFX> obtenerPorId(Integer id) throws Exception {
        try {
            String url = baseUrl + "/" + id;
            String response = HttpClient.get(url);

            JsonNode jsonNode = objectMapper.readTree(response);
            // Deserializar la parte 'categoria' del JSON en el DTO
            CategoriaDTO categoriaDTO = objectMapper.treeToValue(jsonNode.get("categoria"), CategoriaDTO.class);
            CategoriaFX categoriaFX = convertirAModeloFX(categoriaDTO);

            return Optional.of(categoriaFX);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public CategoriaFX guardar(CategoriaFX categoriaFX) throws Exception {
        // Convertir el modelo FX a DTO para enviar al backend
        CategoriaDTO categoriaDTO = convertirADTO(categoriaFX);
        String json = objectMapper.writeValueAsString(categoriaDTO);

        String response;
        if (categoriaFX.getIdCategoria() == 0) {
            response = HttpClient.post(baseUrl, json);
        } else {
            String url = baseUrl + "/" + categoriaFX.getIdCategoria();
            response = HttpClient.put(url, json);
        }

        // Deserializar la respuesta del backend (que es un DTO)
        CategoriaDTO categoriaGuardadaDTO = objectMapper.readValue(response, CategoriaDTO.class);
        // Convertir el DTO de respuesta al modelo FX
        return convertirAModeloFX(categoriaGuardadaDTO);
    }

    @Override
    public boolean eliminar(Integer id) throws Exception {
        try {
            String url = baseUrl + "/" + id;
            HttpClient.delete(url);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public int obtenerCantidadProductos(Integer idCategoria) throws Exception {
        try {
            String url = baseUrl + "/" + idCategoria;
            String response = HttpClient.get(url);

            JsonNode jsonNode = objectMapper.readTree(response);
            return jsonNode.get("cantidadProductos").asInt();
        } catch (Exception e) {
            return 0;
        }
    }

    // Método auxiliar para convertir de DTO a modelo FX
    private CategoriaFX convertirAModeloFX(CategoriaDTO categoriaDTO) {
        return new CategoriaFX(
                categoriaDTO.idCategoria,
                categoriaDTO.nombre,
                categoriaDTO.descripcion,
                categoriaDTO.estado,
                categoriaDTO.duracionGarantia
        );
    }

    // Método auxiliar para convertir de modelo FX a DTO
    private CategoriaDTO convertirADTO(CategoriaFX categoriaFX) {
        CategoriaDTO dto = new CategoriaDTO();
        // Si es una nueva categoría, el ID debe ser null para el backend
        dto.idCategoria = (categoriaFX.getIdCategoria() == 0) ? null : categoriaFX.getIdCategoria();
        dto.nombre = categoriaFX.getNombre();
        dto.descripcion = categoriaFX.getDescripcion();
        dto.estado = categoriaFX.getEstado();
        dto.duracionGarantia = categoriaFX.getDuracionGarantia();
        return dto;
    }

    // Clase DTO interna para representar la estructura JSON de la API
    @JsonIgnoreProperties(ignoreUnknown = true) // Ignora campos extra en el JSON
    private static class CategoriaDTO {
        public Integer idCategoria;
        public String nombre;
        public String descripcion;
        public Boolean estado;
        public Integer duracionGarantia;
        // No se necesitan constructores, getters o setters si los campos son públicos
        // y solo se usa para la deserialización/serialización con Jackson.
    }
}