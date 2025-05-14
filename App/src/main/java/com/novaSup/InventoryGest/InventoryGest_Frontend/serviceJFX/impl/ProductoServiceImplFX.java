package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.CategoriaFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProveedorFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ProductoDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.CategoriaDTO;
import com.novaSup.InventoryGest.InventoryGest_Frontend.dto.ProveedorDTO;

import java.util.List;
import java.util.stream.Collectors;

public class ProductoServiceImplFX implements IProductoService {

    private final String API_URL;
    private final String API_CATEGORIAS;
    private final String API_PROVEEDORES;
    private final ObjectMapper objectMapper;

    public ProductoServiceImplFX() {
        API_URL = ApiConfig.getBaseUrl() + "/productos";
        API_CATEGORIAS = ApiConfig.getBaseUrl() + "/api/categorias";
        API_PROVEEDORES = ApiConfig.getBaseUrl() + "/api/proveedores";

        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

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
    public List<ProductoFX> obtenerActivos() throws Exception {
        String respuesta = HttpClient.get(API_URL + "/activos");
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
    public List<ProductoFX> filtrarProductos(String nombre, String codigo, Integer idCategoria, Boolean estado) throws Exception {
        StringBuilder url = new StringBuilder(API_URL + "/filtrar?");

        // Preparar los parámetros, sustituyendo null por cadenas vacías
        nombre = (nombre != null) ? nombre : "";
        codigo = (codigo != null) ? codigo : "";

        // Añadir los parámetros de texto
        url.append("nombre=").append(java.net.URLEncoder.encode(nombre, "UTF-8"));
        url.append("&codigo=").append(java.net.URLEncoder.encode(codigo, "UTF-8"));

        // Para idCategoria, enviar cadena vacía si es null o cero
        String idCategoriaStr = "";
        if (idCategoria != null && idCategoria > 0) {
            idCategoriaStr = idCategoria.toString();
        }
        url.append("&idCategoria=").append(idCategoriaStr);

        // Para estado, siempre incluirlo pero como cadena vacía si es null
        String estadoStr = "";
        if (estado != null) {
            estadoStr = estado.toString();
        }
        url.append("&estado=").append(estadoStr);
        
        String respuesta = HttpClient.get(url.toString());
        List<ProductoDTO> productos = objectMapper.readValue(respuesta,
                new TypeReference<List<ProductoDTO>>() {});
        return productos.stream()
                .map(this::convertirAProductoFX)
                .collect(Collectors.toList());
    }

    @Override
    public ProductoFX guardar(ProductoFX producto) throws Exception {
        ProductoDTO productoDTO = convertirAProductoDTO(producto);
        String json = objectMapper.writeValueAsString(productoDTO);
        String respuesta = HttpClient.post(API_URL, json);
        productoDTO = objectMapper.readValue(respuesta, ProductoDTO.class);
        return convertirAProductoFX(productoDTO);
    }

    @Override
    public ProductoFX actualizar(ProductoFX producto) throws Exception {
        ProductoDTO productoDTO = convertirAProductoDTO(producto);
        String json = objectMapper.writeValueAsString(productoDTO);
        String respuesta = HttpClient.put(API_URL + "/" + producto.getIdProducto(), json);
        productoDTO = objectMapper.readValue(respuesta, ProductoDTO.class);
        return convertirAProductoFX(productoDTO);
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        try {
            HttpClient.delete(API_URL + "/" + id);
            // Si llegamos aquí, es que el producto se eliminó con éxito
        } catch (Exception e) {
            // Extraer el mensaje de error
            String mensajeError = e.getMessage();
            if (mensajeError.contains("lotes asociados")) {
                throw new Exception("No se puede eliminar el producto porque tiene lotes asociados. Elimine primero los lotes.");
            }
            throw e; // Re-lanzar la excepción original si no es el caso específico
        }
    }

    @Override
    public ProductoFX actualizarStock(Integer id, Integer cantidad) throws Exception {
        String respuesta = HttpClient.patch(API_URL + "/" + id + "/stock/ajustar?cantidad=" + cantidad, "");
        ProductoDTO productoDTO = objectMapper.readValue(respuesta, ProductoDTO.class);
        return convertirAProductoFX(productoDTO);
    }

    @Override
    public ProductoFX cambiarEstado(Integer id, Boolean estado) throws Exception {
        String respuesta = HttpClient.put(API_URL + "/" + id + "/estado?estado=" + estado, "");
        ProductoDTO productoDTO = objectMapper.readValue(respuesta, ProductoDTO.class);
        return convertirAProductoFX(productoDTO);
    }

    @Override
    public boolean existeCodigo(String codigo, Integer idProducto) throws Exception {
        String respuesta = HttpClient.get(API_URL + "/verificar-codigo/" + codigo);
        Boolean existe = objectMapper.readValue(respuesta, Boolean.class);

        if (idProducto != null && existe) {
            // Si estamos editando un producto, verificamos si el código pertenece al mismo producto
            try {
                ProductoFX producto = obtenerPorId(idProducto);
                return !producto.getCodigo().equals(codigo) && existe;
            } catch (Exception e) {
                return existe;
            }
        }

        return existe;
    }

    @Override
    public List<CategoriaFX> obtenerCategorias() throws Exception {
        String respuesta = HttpClient.get(API_CATEGORIAS);
        List<CategoriaDTO> categorias = objectMapper.readValue(respuesta,
                new TypeReference<List<CategoriaDTO>>() {});
        return categorias.stream()
                .map(this::convertirACategoriaFX)
                .collect(Collectors.toList());
    }

    @Override
    public List<ProveedorFX> obtenerProveedores() throws Exception {
        String respuesta = HttpClient.get(API_PROVEEDORES);
        List<ProveedorDTO> proveedores = objectMapper.readValue(respuesta,
                new TypeReference<List<ProveedorDTO>>() {});
        return proveedores.stream()
                .map(this::convertirAProveedorFX)
                .collect(Collectors.toList());
    }

    // Métodos de conversión
    private ProductoFX convertirAProductoFX(ProductoDTO dto) {
        return new ProductoFX(
                dto.idProducto,
                dto.codigo,
                dto.nombre,
                dto.descripcion,
                dto.precioCosto,
                dto.precioVenta,
                dto.stock,
                dto.stockMinimo,
                dto.stockMaximo,
                dto.categoria != null ? dto.categoria.nombre : null,
                dto.idCategoria,
                dto.proveedor != null ? dto.proveedor.nombre : null,
                dto.idProveedor,
                dto.estado
        );
    }

    private ProductoDTO convertirAProductoDTO(ProductoFX fx) {
        ProductoDTO dto = new ProductoDTO();
        dto.idProducto = fx.getIdProducto();
        dto.codigo = fx.getCodigo();
        dto.nombre = fx.getNombre();
        dto.descripcion = fx.getDescripcion();
        dto.precioCosto = fx.getPrecioCosto();
        dto.precioVenta = fx.getPrecioVenta();
        dto.stock = fx.getStock();
        dto.stockMinimo = fx.getStockMinimo();
        dto.stockMaximo = fx.getStockMaximo();
        dto.idCategoria = fx.getIdCategoria();
        dto.idProveedor = fx.getIdProveedor();
        dto.estado = fx.getEstado();
        return dto;
    }

    private CategoriaFX convertirACategoriaFX(CategoriaDTO dto) {
        return new CategoriaFX(
                dto.idCategoria,
                dto.nombre,
                dto.descripcion,
                dto.estado,
                dto.duracionGarantia
        );
    }

    private ProveedorFX convertirAProveedorFX(ProveedorDTO dto) {
        return new ProveedorFX(
                dto.idProveedor,
                dto.nombre,
                dto.contacto,
                dto.telefono,
                dto.correo,
                dto.direccion
        );
    }
}