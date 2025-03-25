package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX.TipoMovimiento;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IEntradaProductoService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EntradaProductoServiceImplFX implements IEntradaProductoService {

    private final String API_URL = "http://localhost:8080/entradas";
    private final ObjectMapper objectMapper;

    public EntradaProductoServiceImplFX() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule()); // Para manejo de fechas
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); // Ignorar propiedades no mapeadas
    }

    @Override
    public List<EntradaProductoFX> obtenerTodos() throws Exception {
        String respuesta = HttpClient.get(API_URL);
        List<EntradaDTO> entradas = objectMapper.readValue(respuesta,
                new TypeReference<List<EntradaDTO>>() {});

        return entradas.stream()
                .map(this::convertirAEntradaFX)
                .collect(Collectors.toList());
    }

    @Override
    public List<EntradaProductoFX> obtenerPorIdProducto(Integer idProducto) throws Exception {
        String respuesta = HttpClient.get(API_URL + "/producto/" + idProducto);
        List<EntradaDTO> entradas = objectMapper.readValue(respuesta,
                new TypeReference<List<EntradaDTO>>() {});

        return entradas.stream()
                .map(this::convertirAEntradaFX)
                .collect(Collectors.toList());
    }

    @Override
    public List<EntradaProductoFX> obtenerPorFecha(LocalDate desde, LocalDate hasta) throws Exception {
        StringBuilder url = new StringBuilder(API_URL + "/fecha?");
        if (desde != null) {
            url.append("desde=").append(desde.toString());
        }
        if (hasta != null) {
            if (desde != null) url.append("&");
            url.append("hasta=").append(hasta.toString());
        }

        String respuesta = HttpClient.get(url.toString());
        List<EntradaDTO> entradas = objectMapper.readValue(respuesta,
                new TypeReference<List<EntradaDTO>>() {});

        return entradas.stream()
                .map(this::convertirAEntradaFX)
                .collect(Collectors.toList());
    }

    @Override
    public List<EntradaProductoFX> obtenerPorTipoMovimiento(TipoMovimiento tipo) throws Exception {
        String respuesta = HttpClient.get(API_URL + "/tipo/" + tipo.name());
        List<EntradaDTO> entradas = objectMapper.readValue(respuesta,
                new TypeReference<List<EntradaDTO>>() {});

        return entradas.stream()
                .map(this::convertirAEntradaFX)
                .collect(Collectors.toList());
    }

    @Override
    public List<EntradaProductoFX> obtenerFiltrados(Integer idProducto, LocalDate desde,
                                                    LocalDate hasta, TipoMovimiento tipo) throws Exception {
        StringBuilder url = new StringBuilder(API_URL + "/filtro?");
        boolean hayParametro = false;

        if (idProducto != null) {
            url.append("idProducto=").append(idProducto);
            hayParametro = true;
        }

        if (desde != null) {
            if (hayParametro) url.append("&");
            url.append("desde=").append(desde.toString());
            hayParametro = true;
        }

        if (hasta != null) {
            if (hayParametro) url.append("&");
            url.append("hasta=").append(hasta.toString());
            hayParametro = true;
        }

        if (tipo != null) {
            if (hayParametro) url.append("&");
            url.append("tipo=").append(tipo.name());
        }

        String respuesta = HttpClient.get(url.toString());
        List<EntradaDTO> entradas = objectMapper.readValue(respuesta,
                new TypeReference<List<EntradaDTO>>() {});

        return entradas.stream()
                .map(this::convertirAEntradaFX)
                .collect(Collectors.toList());
    }

    @Override
    public EntradaProductoFX registrarMovimiento(Integer idProducto, Integer cantidad, TipoMovimiento tipo, BigDecimal precioUnitario) throws Exception {
        MovimientoDTO movimientoDTO = new MovimientoDTO(idProducto, cantidad, tipo.name(), precioUnitario);
        String json = objectMapper.writeValueAsString(movimientoDTO);
        String respuesta = HttpClient.post(API_URL, json);
        EntradaDTO entradaDTO = objectMapper.readValue(respuesta, EntradaDTO.class);
        return convertirAEntradaFX(entradaDTO);
    }

    private EntradaProductoFX convertirAEntradaFX(EntradaDTO dto) {
        return new EntradaProductoFX(
                dto.idEntrada,
                dto.producto.idProducto,
                dto.producto.nombre,
                dto.cantidad,
                dto.fecha,
                TipoMovimiento.valueOf(dto.tipoMovimiento),
                dto.precioUnitario
        );
    }

    // Clases internas para el mapeo de JSON
    private static class EntradaDTO {
        public Integer idEntrada;
        public ProductoDTO producto;  // En lugar de idProducto y nombreProducto directos
        public Integer cantidad;
        public LocalDateTime fecha;
        public String tipoMovimiento;
        public BigDecimal precioUnitario;

        // Clase interna para el objeto producto
// Clase interna para el objeto producto
        public static class ProductoDTO {
            public Integer idProducto;
            public String nombre;
            public String descripcion;    // Añadir este campo
            public BigDecimal precio;     // Añadir este campo
            public Integer stock;         // Añadir este campo
        }

        public EntradaDTO() {}
    }

    private static class MovimientoDTO {
        public Integer idProducto;
        public Integer cantidad;
        public String tipoMovimiento;
        public BigDecimal precioUnitario;

        // Constructor actualizado
        public MovimientoDTO(Integer idProducto, Integer cantidad, String tipoMovimiento, BigDecimal precioUnitario) {
            this.idProducto = idProducto;
            this.cantidad = cantidad;
            this.tipoMovimiento = tipoMovimiento;
            this.precioUnitario = precioUnitario;
        }
    }
}