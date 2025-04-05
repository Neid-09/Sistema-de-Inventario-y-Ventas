package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProveedorFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.util.ArrayList;
import java.util.List;

public class ProductoServiceImplFX implements IProductoService {

    private final String API_URL = "http://localhost:8080/productos";
    private final ObjectMapper objectMapper;

    public ProductoServiceImplFX() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public List<ProductoFX> obtenerTodos() throws Exception {
        String response = HttpClient.get(API_URL);
        return objectMapper.readValue(response, new TypeReference<List<ProductoFX>>() {});
    }

    @Override
    public List<ProductoFX> obtenerActivos() throws Exception {
        String response = HttpClient.get(API_URL + "/activos");
        return objectMapper.readValue(response, new TypeReference<List<ProductoFX>>() {});
    }

    @Override
    public ProductoFX obtenerPorId(Integer id) throws Exception {
        String response = HttpClient.get(API_URL + "/" + id);
        return objectMapper.readValue(response, ProductoFX.class);
    }

    @Override
    public List<ProductoFX> filtrarProductos(String nombre, String codigo, Integer idCategoria, Boolean estado) throws Exception {
        StringBuilder urlBuilder = new StringBuilder(API_URL + "/filtrar?");

        if (nombre != null && !nombre.isEmpty()) {
            urlBuilder.append("nombre=").append(nombre).append("&");
        }

        if (codigo != null && !codigo.isEmpty()) {
            urlBuilder.append("codigo=").append(codigo).append("&");
        }

        if (idCategoria != null && idCategoria > 0) {
            urlBuilder.append("idCategoria=").append(idCategoria).append("&");
        }

        if (estado != null) {
            urlBuilder.append("estado=").append(estado);
        }

        String response = HttpClient.get(urlBuilder.toString());
        return objectMapper.readValue(response, new TypeReference<List<ProductoFX>>() {});
    }

    @Override
    public ProductoFX guardar(ProductoFX producto) throws Exception {
        StringBuilder json = new StringBuilder("{");
        json.append("\"codigo\":\"").append(producto.getCodigo()).append("\",");
        json.append("\"nombre\":\"").append(producto.getNombre()).append("\",");
        json.append("\"descripcion\":\"").append(producto.getDescripcion() != null ? producto.getDescripcion() : "").append("\",");
        json.append("\"precioCosto\":").append(producto.getPrecioCosto()).append(",");
        json.append("\"precioVenta\":").append(producto.getPrecioVenta()).append(",");
        json.append("\"stock\":").append(producto.getStock()).append(",");

        if (producto.getStockMinimo() != null) {
            json.append("\"stockMinimo\":").append(producto.getStockMinimo()).append(",");
        }

        if (producto.getStockMaximo() != null) {
            json.append("\"stockMaximo\":").append(producto.getStockMaximo()).append(",");
        }

        json.append("\"estado\":").append(producto.getEstado());

        // Añadir idCategoria solo si es mayor que 0
        if (producto.getIdCategoria() != null && producto.getIdCategoria() > 0) {
            json.append(",\"idCategoria\":").append(producto.getIdCategoria());
        }

        // Añadir idProveedor solo si es mayor que 0
        if (producto.getIdProveedor() != null && producto.getIdProveedor() > 0) {
            json.append(",\"idProveedor\":").append(producto.getIdProveedor());
        }

        json.append("}");
        String jsonBody = json.toString();

        String response;
        if (producto.getIdProducto() == null) {
            response = HttpClient.post(API_URL, jsonBody);
        } else {
            json = new StringBuilder("{");
            json.append("\"idProducto\":").append(producto.getIdProducto()).append(",");
            json.append("\"codigo\":\"").append(producto.getCodigo()).append("\",");
            json.append("\"nombre\":\"").append(producto.getNombre()).append("\",");
            json.append("\"descripcion\":\"").append(producto.getDescripcion() != null ? producto.getDescripcion() : "").append("\",");
            json.append("\"precioCosto\":").append(producto.getPrecioCosto()).append(",");
            json.append("\"precioVenta\":").append(producto.getPrecioVenta()).append(",");
            json.append("\"stock\":").append(producto.getStock()).append(",");

            if (producto.getStockMinimo() != null) {
                json.append("\"stockMinimo\":").append(producto.getStockMinimo()).append(",");
            }

            if (producto.getStockMaximo() != null) {
                json.append("\"stockMaximo\":").append(producto.getStockMaximo()).append(",");
            }

            json.append("\"estado\":").append(producto.getEstado());

            // Añadir idCategoria solo si es mayor que 0
            if (producto.getIdCategoria() != null && producto.getIdCategoria() > 0) {
                json.append(",\"idCategoria\":").append(producto.getIdCategoria());
            }

            // Añadir idProveedor solo si es mayor que 0
            if (producto.getIdProveedor() != null && producto.getIdProveedor() > 0) {
                json.append(",\"idProveedor\":").append(producto.getIdProveedor());
            }

            json.append("}");
            jsonBody = json.toString();

            response = HttpClient.put(API_URL + "/" + producto.getIdProducto(), jsonBody);
        }

        return objectMapper.readValue(response, ProductoFX.class);
    }

    @Override
    public ProductoFX actualizar(ProductoFX producto) throws Exception {
        if (producto.getIdProducto() == null) {
            throw new Exception("No se puede actualizar un producto sin ID");
        }

        StringBuilder json = new StringBuilder("{");
        json.append("\"idProducto\":").append(producto.getIdProducto()).append(",");
        json.append("\"codigo\":\"").append(producto.getCodigo()).append("\",");
        json.append("\"nombre\":\"").append(producto.getNombre()).append("\",");
        json.append("\"descripcion\":\"").append(producto.getDescripcion() != null ? producto.getDescripcion() : "").append("\",");
        json.append("\"precioCosto\":").append(producto.getPrecioCosto()).append(",");
        json.append("\"precioVenta\":").append(producto.getPrecioVenta()).append(",");
        json.append("\"stock\":").append(producto.getStock()).append(",");

        if (producto.getStockMinimo() != null) {
            json.append("\"stockMinimo\":").append(producto.getStockMinimo()).append(",");
        }

        if (producto.getStockMaximo() != null) {
            json.append("\"stockMaximo\":").append(producto.getStockMaximo()).append(",");
        }

        json.append("\"estado\":").append(producto.getEstado());

        // Añadir idCategoria solo si es mayor que 0
        if (producto.getIdCategoria() != null && producto.getIdCategoria() > 0) {
            json.append(",\"idCategoria\":").append(producto.getIdCategoria());
        }

        // Añadir idProveedor solo si es mayor que 0
        if (producto.getIdProveedor() != null && producto.getIdProveedor() > 0) {
            json.append(",\"idProveedor\":").append(producto.getIdProveedor());
        }

        json.append("}");
        String jsonBody = json.toString();

        String response = HttpClient.put(API_URL + "/" + producto.getIdProducto(), jsonBody);
        return objectMapper.readValue(response, ProductoFX.class);
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        HttpClient.delete(API_URL + "/" + id);
    }

    @Override
    public ProductoFX actualizarStock(Integer id, Integer cantidad) throws Exception {
        String response = HttpClient.patch(API_URL + "/" + id + "/stock?cantidad=" + cantidad, "");
        return objectMapper.readValue(response, ProductoFX.class);
    }

    @Override
    public ProductoFX cambiarEstado(Integer id, Boolean estado) throws Exception {
        if (estado) {
            String response = HttpClient.patch(API_URL + "/" + id + "/activar", "");
            return objectMapper.readValue(response, ProductoFX.class);
        } else {
            String response = HttpClient.patch(API_URL + "/" + id + "/desactivar", "");
            return objectMapper.readValue(response, ProductoFX.class);
        }
    }

    @Override
    public boolean existeCodigo(String codigo, Integer idProducto) throws Exception {
        try {
            String url = API_URL + "/verificar-codigo/" + codigo;
            if (idProducto != null) {
                url += "?idProducto=" + idProducto;
            }
            String response = HttpClient.get(url);
            return Boolean.parseBoolean(response);
        } catch (Exception e) {
            throw new Exception("Error al verificar el código: " + e.getMessage());
        }
    }

    @Override
    public List<CategoriaFX> obtenerCategorias() throws Exception {
        try {
            String response = HttpClient.get("http://localhost:8080/categorias");
            List<CategoriaFX> categorias = objectMapper.readValue(response, new TypeReference<List<CategoriaFX>>() {});
            categorias.add(0, new CategoriaFX(null, "Sin categoría", "", true));
            return categorias;
        } catch (Exception e) {
            // Si hay error, al menos devolver la opción por defecto
            List<CategoriaFX> categorias = new ArrayList<>();
            categorias.add(new CategoriaFX(null, "Sin categoría", "", true));
            return categorias;
        }
    }

    @Override
    public List<ProveedorFX> obtenerProveedores() throws Exception {
        try {
            String response = HttpClient.get("http://localhost:8080/proveedores");
            List<ProveedorFX> proveedores = objectMapper.readValue(response, new TypeReference<List<ProveedorFX>>() {});
            proveedores.add(0, new ProveedorFX(null, "Sin proveedor", "", "", "", ""));
            return proveedores;
        } catch (Exception e) {
            // Si hay error, al menos devolver la opción por defecto
            List<ProveedorFX> proveedores = new ArrayList<>();
            proveedores.add(new ProveedorFX(null, "Sin proveedor", "", "", "", ""));
            return proveedores;
        }
    }
}