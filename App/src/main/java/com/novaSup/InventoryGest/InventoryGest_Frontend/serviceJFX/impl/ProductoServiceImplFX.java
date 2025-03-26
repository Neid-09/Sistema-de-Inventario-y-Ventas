package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductoServiceImplFX implements IProductoService {

    private final String API_URL = "http://localhost:8080/productos";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<ProductoFX> obtenerTodos() throws Exception {
        String respuesta = HttpClient.get(API_URL);
        List<ProductoDTO> productos = objectMapper.readValue(respuesta,
                new TypeReference<List<ProductoDTO>>() {});

        return productos.stream()
                .map(this::convertirAProductoFX)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoFX obtenerPorId(Integer id) throws Exception {
        String respuesta = HttpClient.get(API_URL + "/" + id);
        ProductoDTO producto = objectMapper.readValue(respuesta, ProductoDTO.class);
        return convertirAProductoFX(producto);
    }

    @Override
    public ProductoFX guardar(String nombre, String descripcion, BigDecimal precio, Integer stock) throws Exception {
        ProductoDTO productoDTO = new ProductoDTO(null, nombre, descripcion, precio, stock, true);
        String json = objectMapper.writeValueAsString(productoDTO);
        String respuesta = HttpClient.post(API_URL, json);
        ProductoDTO productoGuardado = objectMapper.readValue(respuesta, ProductoDTO.class);
        return convertirAProductoFX(productoGuardado);
    }

    @Override
    public ProductoFX actualizar(Integer id, String nombre, String descripcion, BigDecimal precio, Integer stock) throws Exception {
        ProductoDTO productoDTO = new ProductoDTO(id, nombre, descripcion, precio, stock, true);
        String json = objectMapper.writeValueAsString(productoDTO);
        String respuesta = HttpClient.put(API_URL + "/" + id, json);
        ProductoDTO productoActualizado = objectMapper.readValue(respuesta, ProductoDTO.class);
        return convertirAProductoFX(productoActualizado);
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        HttpClient.delete(API_URL + "/" + id);
    }

    @Override
    public void desactivarProducto(Integer id) throws Exception {
        // Llamar al endpoint para desactivar el producto
        HttpClient.patch(API_URL + "/" + id + "/desactivar");
    }

    @Override
    public ProductoFX actualizarStock(Integer id, Integer cantidad) throws Exception {
        String respuesta = HttpClient.patch(API_URL + "/" + id + "/stock?cantidad=" + cantidad);
        ProductoDTO productoActualizado = objectMapper.readValue(respuesta, ProductoDTO.class);
        return convertirAProductoFX(productoActualizado);
    }

    private ProductoFX convertirAProductoFX(ProductoDTO dto) {
        return new ProductoFX(
                dto.idProducto,
                dto.nombre,
                dto.descripcion,
                dto.precio,
                dto.stock,
                dto.estado
        );
    }

    // Clase interna para el mapeo de JSON
    private static class ProductoDTO {
        public Integer idProducto;
        public String nombre;
        public String descripcion;
        public BigDecimal precio;
        public Integer stock;
        public Boolean estado;

        public ProductoDTO() {}

        public ProductoDTO(Integer idProducto, String nombre, String descripcion, BigDecimal precio, Integer stock, Boolean estado) {
            this.idProducto = idProducto;
            this.nombre = nombre;
            this.descripcion = descripcion;
            this.precio = precio;
            this.stock = stock;
            this.estado = estado;
        }
    }
}