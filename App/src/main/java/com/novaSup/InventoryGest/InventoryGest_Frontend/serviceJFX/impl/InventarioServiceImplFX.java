package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.EntradaProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.LoteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IInventarioService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class InventarioServiceImplFX implements IInventarioService {
    
    private static final Logger logger = LoggerFactory.getLogger(InventarioServiceImplFX.class);
    private static final String BASE_URL = ApiConfig.getBaseUrl() + "/api/inventario";
    private final ObjectMapper objectMapper;
    private final DateTimeFormatter dateFormatter;
    
    public InventarioServiceImplFX() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    }
    
    @Override
    public EntradaProductoFX registrarCompra(Integer idProducto, Integer cantidad, 
                                           BigDecimal precioUnitario, String numeroLote,
                                           LocalDate fechaVencimiento, Integer idProveedor, 
                                           String motivo) throws Exception {
        try {
            ObjectNode compraDTO = objectMapper.createObjectNode();
            compraDTO.put("idProducto", idProducto);
            compraDTO.put("cantidad", cantidad);
            
            if (precioUnitario != null) {
                compraDTO.put("precioUnitario", precioUnitario.toString());
            }
            
            compraDTO.put("numeroLote", numeroLote);
            
            if (fechaVencimiento != null) {
                compraDTO.put("fechaVencimiento", fechaVencimiento.format(dateFormatter));
            }
            
            if (idProveedor != null) {
                compraDTO.put("idProveedor", idProveedor);
            }
            
            if (motivo != null && !motivo.isEmpty()) {
                compraDTO.put("motivo", motivo);
            }
            
            String jsonBody = objectMapper.writeValueAsString(compraDTO);
            String response = HttpClient.post(BASE_URL + "/compra", jsonBody);
            
            JsonNode movimientoJson = objectMapper.readTree(response);
            return convertirJsonAEntradaFX(movimientoJson);
        } catch (Exception e) {
            logger.error("Error al registrar compra: {}", e.getMessage());
            throw new Exception("Error al registrar la compra: " + e.getMessage());
        }
    }
    
    @Override
    public EntradaProductoFX registrarVenta(Integer idProducto, Integer cantidad, 
                                          BigDecimal precioUnitario, String motivo) throws Exception {
        try {
            ObjectNode ventaDTO = objectMapper.createObjectNode();
            ventaDTO.put("idProducto", idProducto);
            ventaDTO.put("cantidad", cantidad);
            
            if (precioUnitario != null) {
                ventaDTO.put("precioUnitario", precioUnitario.toString());
            }
            
            if (motivo != null && !motivo.isEmpty()) {
                ventaDTO.put("motivo", motivo);
            }
            
            String jsonBody = objectMapper.writeValueAsString(ventaDTO);
            String response = HttpClient.post(BASE_URL + "/venta", jsonBody);
            
            JsonNode movimientoJson = objectMapper.readTree(response);
            return convertirJsonAEntradaFX(movimientoJson);
        } catch (Exception e) {
            logger.error("Error al registrar venta: {}", e.getMessage());
            throw new Exception("Error al registrar la venta: " + e.getMessage());
        }
    }
    
    @Override
    public EntradaProductoFX registrarAjuste(Integer idProducto, Integer cantidad, 
                                           String motivo) throws Exception {
        try {
            ObjectNode ajusteDTO = objectMapper.createObjectNode();
            ajusteDTO.put("idProducto", idProducto);
            ajusteDTO.put("cantidad", cantidad);
            
            if (motivo != null && !motivo.isEmpty()) {
                ajusteDTO.put("motivo", motivo);
            }
            
            String jsonBody = objectMapper.writeValueAsString(ajusteDTO);
            String response = HttpClient.post(BASE_URL + "/ajuste", jsonBody);
            
            JsonNode movimientoJson = objectMapper.readTree(response);
            return convertirJsonAEntradaFX(movimientoJson);
        } catch (Exception e) {
            logger.error("Error al registrar ajuste: {}", e.getMessage());
            throw new Exception("Error al registrar el ajuste: " + e.getMessage());
        }
    }
    
    @Override
    public LoteFX registrarDevolucion(Integer idLote, Integer cantidad, String motivo) throws Exception {
        try {
            ObjectNode devolucionDTO = objectMapper.createObjectNode();
            devolucionDTO.put("idLote", idLote);
            devolucionDTO.put("cantidad", cantidad);
            
            if (motivo != null && !motivo.isEmpty()) {
                devolucionDTO.put("motivo", motivo);
            }
            
            String jsonBody = objectMapper.writeValueAsString(devolucionDTO);
            String response = HttpClient.post(BASE_URL + "/devolucion", jsonBody);
            
            JsonNode responseJson = objectMapper.readTree(response);
            if (responseJson.has("lote")) {
                return convertirJsonALoteFX(responseJson.get("lote"));
            } else {
                throw new Exception("No se pudo procesar la devolución");
            }
        } catch (Exception e) {
            logger.error("Error al registrar devolución: {}", e.getMessage());
            throw new Exception("Error al registrar la devolución: " + e.getMessage());
        }
    }
    
    private EntradaProductoFX convertirJsonAEntradaFX(JsonNode json) {
        try {
            Integer idEntrada = json.has("idMovimiento") ? json.get("idMovimiento").asInt() : null;
            
            // Extraer datos del producto
            Integer idProducto = null;
            String nombreProducto = null;
            if (json.has("producto")) {
                JsonNode productoJson = json.get("producto");
                idProducto = productoJson.has("idProducto") ? productoJson.get("idProducto").asInt() : null;
                nombreProducto = productoJson.has("nombre") ? productoJson.get("nombre").asText() : null;
            }
            
            // Extraer datos del proveedor
            Integer idProveedor = null;
            String nombreProveedor = null;
            if (json.has("proveedor")) {
                JsonNode proveedorJson = json.get("proveedor");
                idProveedor = proveedorJson.has("idProveedor") ? proveedorJson.get("idProveedor").asInt() : null;
                nombreProveedor = proveedorJson.has("nombre") ? proveedorJson.get("nombre").asText() : null;
            }
            
            Integer cantidad = json.has("cantidad") ? json.get("cantidad").asInt() : null;
            
            // Extraer fecha
            LocalDateTime fecha = null;
            if (json.has("fecha") && !json.get("fecha").isNull()) {
                String fechaStr = json.get("fecha").asText();
                fecha = LocalDateTime.parse(fechaStr, DateTimeFormatter.ISO_DATE_TIME);
            }
            
            String tipoMovimiento = json.has("tipoMovimiento") ? json.get("tipoMovimiento").asText() : null;
            
            BigDecimal precioUnitario = null;
            if (json.has("precioUnitario") && !json.get("precioUnitario").isNull()) {
                precioUnitario = new BigDecimal(json.get("precioUnitario").asText());
            }
            
            String motivo = json.has("motivo") ? json.get("motivo").asText() : null;
            
            return new EntradaProductoFX(
                    idEntrada,
                    idProducto,
                    nombreProducto,
                    idProveedor,
                    nombreProveedor,
                    cantidad,
                    fecha,
                    tipoMovimiento,
                    precioUnitario,
                    motivo
            );
        } catch (Exception e) {
            logger.error("Error al convertir JSON a EntradaProductoFX: {}", e.getMessage());
            return null;
        }
    }
    
    private LoteFX convertirJsonALoteFX(JsonNode json) {
        try {
            Integer idLote = json.has("idLote") && !json.get("idLote").isNull() ?
                    json.get("idLote").asInt() : null;
            Integer idEntrada = json.has("idEntrada") && !json.get("idEntrada").isNull() ?
                    json.get("idEntrada").asInt() : null;
            Integer idProducto = json.has("idProducto") && !json.get("idProducto").isNull() ?
                    json.get("idProducto").asInt() : null;
            String numeroLote = json.has("numeroLote") && !json.get("numeroLote").isNull() ?
                    json.get("numeroLote").asText() : "";
            LocalDate fechaEntrada = json.has("fechaEntrada") && !json.get("fechaEntrada").isNull() ?
                    LocalDate.parse(json.get("fechaEntrada").asText(), dateFormatter) : null;
            LocalDate fechaVencimiento = json.has("fechaVencimiento") && !json.get("fechaVencimiento").isNull() ?
                    LocalDate.parse(json.get("fechaVencimiento").asText(), dateFormatter) : null;
            Integer cantidad = json.has("cantidad") && !json.get("cantidad").isNull() ?
                    json.get("cantidad").asInt() : null;
            Boolean activo = json.has("activo") && !json.get("activo").isNull() ?
                    json.get("activo").asBoolean() : true;

            // Convert LocalDate to Date for the constructor
            java.util.Date fechaEntradaDate = null;
            if (fechaEntrada != null) {
                fechaEntradaDate = java.util.Date.from(fechaEntrada.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            }
            
            java.util.Date fechaVencimientoDate = null;
            if (fechaVencimiento != null) {
                fechaVencimientoDate = java.util.Date.from(fechaVencimiento.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
            }
                    
            return new LoteFX(idLote, idEntrada, null, idProducto, numeroLote,
                    fechaEntradaDate, fechaVencimientoDate, cantidad, activo);
        } catch (Exception e) {
            logger.error("Error al convertir JSON a LoteFX: {}", e.getMessage());
            return null;
        }
    }
    
    @Override
    public List<LoteFX> obtenerLotesPorProducto(Integer idProducto) throws Exception {
        try {
            if (idProducto == null) {
                throw new IllegalArgumentException("El ID del producto es obligatorio");
            }
            
            String response = HttpClient.get(BASE_URL + "/lotes/producto/" + idProducto);
            JsonNode lotesJson = objectMapper.readTree(response);
            
            List<LoteFX> lotes = new ArrayList<>();
            if (lotesJson.isArray()) {
                for (JsonNode loteJson : lotesJson) {
                    LoteFX lote = convertirJsonALoteFX(loteJson);
                    if (lote != null) {
                        lotes.add(lote);
                    }
                }
            }
            
            return lotes;
        } catch (Exception e) {
            logger.error("Error al obtener lotes por producto: {}", e.getMessage());
            throw new Exception("Error al obtener los lotes del producto: " + e.getMessage());
        }
    }
    
    @Override
    public List<EntradaProductoFX> obtenerMovimientosPorProducto(Integer idProducto) throws Exception {
        try {
            if (idProducto == null) {
                throw new IllegalArgumentException("El ID del producto es obligatorio");
            }
            
            String response = HttpClient.get(BASE_URL + "/movimientos/producto/" + idProducto);
            JsonNode movimientosJson = objectMapper.readTree(response);
            
            List<EntradaProductoFX> movimientos = new ArrayList<>();
            if (movimientosJson.isArray()) {
                for (JsonNode movimientoJson : movimientosJson) {
                    EntradaProductoFX movimiento = convertirJsonAEntradaFX(movimientoJson);
                    if (movimiento != null) {
                        movimientos.add(movimiento);
                    }
                }
            }
            
            return movimientos;
        } catch (Exception e) {
            logger.error("Error al obtener movimientos por producto: {}", e.getMessage());
            throw new Exception("Error al obtener los movimientos del producto: " + e.getMessage());
        }
    }
}
