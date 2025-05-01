package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Categoria;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;

import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ICategoriaService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CategoriaServiceImplFX implements ICategoriaService {

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String baseUrl = ApiConfig.getBaseUrl() + "/api/categorias";

    @Override
    public List<CategoriaFX> obtenerTodas() throws Exception {
        String response = HttpClient.get(baseUrl);
        List<Categoria> categorias = objectMapper.readValue(response, new TypeReference<List<Categoria>>() {});
        return categorias.stream()
                .map(this::convertirAModeloFX)
                .collect(Collectors.toList());
    }

    @Override
    public List<CategoriaFX> buscarPorNombre(String nombre) throws Exception {
        String url = baseUrl + "/buscar?nombre=" + nombre;
        String response = HttpClient.get(url);
        List<Categoria> categorias = objectMapper.readValue(response, new TypeReference<List<Categoria>>() {});
        return categorias.stream()
                .map(this::convertirAModeloFX)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<CategoriaFX> obtenerPorId(Integer id) throws Exception {
        try {
            String url = baseUrl + "/" + id;
            String response = HttpClient.get(url);

            // El API devuelve un objeto con la categoría y cantidad de productos
            JsonNode jsonNode = objectMapper.readTree(response);
            Categoria categoria = objectMapper.treeToValue(jsonNode.get("categoria"), Categoria.class);
            CategoriaFX categoriaFX = convertirAModeloFX(categoria);

            // Añadimos la información de productos asociados que el API proporciona
            return Optional.of(categoriaFX);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public CategoriaFX guardar(CategoriaFX categoriaFX) throws Exception {
        Categoria categoria = convertirAModeloBackend(categoriaFX);
        String json = objectMapper.writeValueAsString(categoria);

        String response;
        if (categoriaFX.getIdCategoria() == 0) {
            // Crear nueva categoría
            response = HttpClient.post(baseUrl, json);
        } else {
            // Actualizar categoría existente
            String url = baseUrl + "/" + categoriaFX.getIdCategoria();
            response = HttpClient.put(url, json);
        }

        Categoria categoriaGuardada = objectMapper.readValue(response, Categoria.class);
        return convertirAModeloFX(categoriaGuardada);
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

    // Método auxiliar para convertir de modelo Backend a modelo FX
    private CategoriaFX convertirAModeloFX(Categoria categoria) {
        return new CategoriaFX(
                categoria.getIdCategoria(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.getEstado(),
                categoria.getDuracionGarantia()
        );
    }

    // Método auxiliar para convertir de modelo FX a modelo Backend
    private Categoria convertirAModeloBackend(CategoriaFX categoriaFX) {
        Categoria categoria = new Categoria();
        categoria.setIdCategoria(categoriaFX.getIdCategoria() == 0 ? null : categoriaFX.getIdCategoria());
        categoria.setNombre(categoriaFX.getNombre());
        categoria.setDescripcion(categoriaFX.getDescripcion());
        categoria.setEstado(categoriaFX.getEstado());
        categoria.setDuracionGarantia(categoriaFX.getDuracionGarantia());
        return categoria;
    }
}