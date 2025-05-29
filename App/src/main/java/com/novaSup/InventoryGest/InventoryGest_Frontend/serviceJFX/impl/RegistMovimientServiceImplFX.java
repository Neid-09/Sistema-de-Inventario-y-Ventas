package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IRegistMovimientService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.StringJoiner;
import java.util.stream.Collectors;

public class RegistMovimientServiceImplFX implements IRegistMovimientService {

    private static final String API_MOVIMIENTOS_URL = ApiConfig.getBaseUrl() + "/api/movimientos";
    private final ObjectMapper objectMapper;

    public RegistMovimientServiceImplFX() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Para manejo de fechas
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignorar propiedades no mapeadas
    }

    @Override
    public List<EntradaProductoFX> obtenerTodos() throws Exception {
        return fetchAndMapEntradas(API_MOVIMIENTOS_URL);
    }

    @Override
    public List<EntradaProductoFX> obtenerPorIdProducto(Integer idProducto) throws Exception {
        String url = API_MOVIMIENTOS_URL + "/producto/" + idProducto;
        return fetchAndMapEntradas(url);
    }

    @Override
    public List<EntradaProductoFX> obtenerPorProveedor(Integer idProveedor) throws Exception {
        String url = API_MOVIMIENTOS_URL + "/proveedor/" + idProveedor;
        return fetchAndMapEntradas(url);
    }

    @Override
    public List<EntradaProductoFX> obtenerPorFecha(LocalDate desde, LocalDate hasta) throws Exception {
        String url = buildFilterUrl(null, null, desde, hasta, null);
        return fetchAndMapEntradas(url);
    }

    @Override
    public List<EntradaProductoFX> obtenerPorTipoMovimiento(String tipo) throws Exception {
        String url = API_MOVIMIENTOS_URL + "/tipo/" + URLEncoder.encode(tipo, StandardCharsets.UTF_8);
        return fetchAndMapEntradas(url);
    }

    @Override
    public List<EntradaProductoFX> obtenerFiltrados(Integer idProducto, LocalDate desde,
                                                    LocalDate hasta, String tipo) throws Exception {
        return obtenerFiltradosCompleto(idProducto, null, desde, hasta, tipo);
    }

    @Override
    public List<EntradaProductoFX> obtenerFiltradosCompleto(Integer idProducto, Integer idProveedor, LocalDate desde,
                                                            LocalDate hasta, String tipo) throws Exception {
        String url = buildFilterUrl(idProducto, idProveedor, desde, hasta, tipo);
        return fetchAndMapEntradas(url);
    }

    // --- Métodos privados auxiliares ---

    private List<EntradaProductoFX> fetchAndMapEntradas(String url) throws Exception {
        String respuesta = HttpClient.get(url);
        List<EntradaDTO> entradas = objectMapper.readValue(respuesta, new TypeReference<List<EntradaDTO>>() {});
        return entradas.stream()
                       .map(this::convertirAEntradaFX)
                       .collect(Collectors.toList());
    }

    private String buildFilterUrl(Integer idProducto, Integer idProveedor, LocalDate desde,
                                  LocalDate hasta, String tipo) {
        StringJoiner params = new StringJoiner("&");
        if (idProducto != null) params.add("idProducto=" + idProducto);
        if (idProveedor != null) params.add("idProveedor=" + idProveedor);
        if (desde != null) params.add("desde=" + desde.toString());
        if (hasta != null) params.add("hasta=" + hasta.toString());
        if (tipo != null) params.add("tipo=" + URLEncoder.encode(tipo, StandardCharsets.UTF_8));

        return API_MOVIMIENTOS_URL + (params.length() > 0 ? "/filtro?" + params.toString() : "/filtro");
         // Considerar si /filtro sin parámetros es válido o debería ser API_MOVIMIENTOS_URL
    }


    private EntradaProductoFX convertirAEntradaFX(EntradaDTO dto) {
        return new EntradaProductoFX(
                dto.idEntrada,
                dto.producto.idProducto,
                dto.producto.nombre,
                dto.proveedor != null ? dto.proveedor.idProveedor : null,
                dto.proveedor != null ? dto.proveedor.nombre : null,
                dto.cantidad,
                dto.fecha,
                dto.tipoMovimiento,
                dto.precioUnitario,
                dto.motivo
        );
    }

    // Clases internas para el mapeo de JSON
    private static class EntradaDTO {
        public Integer idEntrada;
        public ProductoDTO producto;
        public ProveedorDTO proveedor;
        public Integer cantidad;
        public LocalDateTime fecha;
        public String tipoMovimiento;
        public BigDecimal precioUnitario;
        public String motivo;

        // Clase interna para el objeto producto
        public static class ProductoDTO {
            public Integer idProducto;
            public String nombre;
        }

        // Clase interna para el objeto proveedor
        public static class ProveedorDTO {
            public Integer idProveedor;
            public String nombre;
        }
    }

    // MovimientoDTO y registrarMovimiento eliminados por refactorización
}