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

    private static final String API_URL = "http://localhost:8080/productos";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public List<ProductoFX> obtenerTodos() throws Exception {
        try {
            String respuesta = HttpClient.get(API_URL);
            List<ProductoDTO> productos = objectMapper.readValue(respuesta,
                    new TypeReference<List<ProductoDTO>>() {});
            return productos.stream()
                    .map(this::convertirAProductoFX)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new Exception("Error al obtener todos los productos: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductoFX obtenerPorId(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del producto no es válido.");
        }
        try {
            String respuesta = HttpClient.get(API_URL + "/" + id);
            ProductoDTO producto = objectMapper.readValue(respuesta, ProductoDTO.class);
            return convertirAProductoFX(producto);
        } catch (Exception e) {
            throw new Exception("Error al obtener el producto con ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public ProductoFX guardar(String nombre, String descripcion, BigDecimal precio, Integer stock) throws Exception {
        if (nombre == null || nombre.isEmpty() || precio == null || stock == null || stock < 0) {
            throw new IllegalArgumentException("Datos del producto no válidos.");
        }
        try {
            ProductoDTO productoDTO = new ProductoDTO(null, nombre, descripcion, precio, stock, true);
            String json = objectMapper.writeValueAsString(productoDTO);
            String respuesta = HttpClient.post(API_URL, json);
            ProductoDTO productoGuardado = objectMapper.readValue(respuesta, ProductoDTO.class);
            return convertirAProductoFX(productoGuardado);
        } catch (Exception e) {
            throw new Exception("Error al guardar el producto: " + e.getMessage(), e);
        }
    }

    @Override
    public ProductoFX actualizar(Integer id, String nombre, String descripcion, BigDecimal precio, Integer stock) throws Exception {
        if (id == null || id <= 0 || nombre == null || nombre.isEmpty() || precio == null || stock == null || stock < 0) {
            throw new IllegalArgumentException("Datos del producto no válidos.");
        }
        try {
            ProductoDTO productoDTO = new ProductoDTO(id, nombre, descripcion, precio, stock, true);
            String json = objectMapper.writeValueAsString(productoDTO);
            String respuesta = HttpClient.put(API_URL + "/" + id, json);
            ProductoDTO productoActualizado = objectMapper.readValue(respuesta, ProductoDTO.class);
            return convertirAProductoFX(productoActualizado);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el producto con ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del producto no es válido.");
        }
        try {
            HttpClient.delete(API_URL + "/" + id);
        } catch (Exception e) {
            throw new Exception("Error al eliminar el producto con ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void desactivarProducto(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del producto no es válido.");
        }
        try {
            HttpClient.patch(API_URL + "/" + id + "/desactivar");
        } catch (Exception e) {
            throw new Exception("Error al desactivar el producto con ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public ProductoFX actualizarStock(Integer id, Integer cantidad) throws Exception {
        if (id == null || id <= 0 || cantidad == null) {
            throw new IllegalArgumentException("Datos para actualizar el stock no válidos.");
        }
        try {
            String respuesta = HttpClient.patch(API_URL + "/" + id + "/stock?cantidad=" + cantidad);
            ProductoDTO productoActualizado = objectMapper.readValue(respuesta, ProductoDTO.class);
            return convertirAProductoFX(productoActualizado);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el stock del producto con ID " + id + ": " + e.getMessage(), e);
        }
    }

    @Override
    public void actualizarProducto(ProductoFX producto) throws Exception {
        if (producto == null || producto.getIdProducto() == null || producto.getIdProducto() <= 0) {
            throw new IllegalArgumentException("El producto no es válido.");
        }
        try {
            ProductoDTO productoDTO = new ProductoDTO(
                    producto.getIdProducto(),
                    producto.getNombre(),
                    producto.getDescripcion(),
                    producto.getPrecio(),
                    producto.getStock(),
                    producto.getEstado()
            );
            String json = objectMapper.writeValueAsString(productoDTO);
            HttpClient.put(API_URL + "/" + producto.getIdProducto(), json);
        } catch (Exception e) {
            throw new Exception("Error al actualizar el producto: " + e.getMessage(), e);
        }
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