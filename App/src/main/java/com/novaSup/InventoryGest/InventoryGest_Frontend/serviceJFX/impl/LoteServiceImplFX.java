package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.LoteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ProductoFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.ILoteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class LoteServiceImplFX implements ILoteService {

    private static final Logger logger = LoggerFactory.getLogger(LoteServiceImplFX.class);
    private static final String BASE_URL = ApiConfig.getBaseUrl() + "/api/lotes";
    private final ObjectMapper objectMapper;
    private final SimpleDateFormat dateFormat;

    public LoteServiceImplFX() {
        this.objectMapper = new ObjectMapper();
        this.dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        this.objectMapper.setDateFormat(dateFormat);
    }

    @Override
    public List<LoteFX> obtenerTodos() throws Exception {
        try {
            String response = HttpClient.get(BASE_URL);
            List<JsonNode> lotesJson = objectMapper.readValue(response, new TypeReference<List<JsonNode>>() {});
            return convertirJsonALotesFX(lotesJson);
        } catch (Exception e) {
            logger.error("Error al obtener todos los lotes: {}", e.getMessage());
            throw new Exception("Error al obtener los lotes: " + e.getMessage());
        }
    }

    @Override
    public LoteFX obtenerPorId(Integer id) throws Exception {
        try {
            String response = HttpClient.get(BASE_URL + "/" + id);
            JsonNode loteJson = objectMapper.readTree(response);
            return convertirJsonALoteFX(loteJson);
        } catch (Exception e) {
            logger.error("Error al obtener lote por ID {}: {}", id, e.getMessage());
            throw new Exception("Error al obtener el lote: " + e.getMessage());
        }
    }

    @Override
    public List<LoteFX> obtenerPorProducto(Integer idProducto) throws Exception {
        try {
            String response = HttpClient.get(BASE_URL + "/producto/" + idProducto);
            List<JsonNode> lotesJson = objectMapper.readValue(response, new TypeReference<List<JsonNode>>() {});
            return convertirJsonALotesFX(lotesJson);
        } catch (Exception e) {
            logger.error("Error al obtener lotes por producto ID {}: {}", idProducto, e.getMessage());
            throw new Exception("Error al obtener los lotes del producto: " + e.getMessage());
        }
    }

    @Override
    public List<LoteFX> obtenerProximosAVencer(Integer diasAlerta) throws Exception {
        try {
            String response = HttpClient.get(BASE_URL + "/proximos-vencer?diasAlerta=" + diasAlerta);
            List<JsonNode> lotesJson = objectMapper.readValue(response, new TypeReference<List<JsonNode>>() {});
            return convertirJsonALotesFX(lotesJson);
        } catch (Exception e) {
            logger.error("Error al obtener lotes próximos a vencer: {}", e.getMessage());
            throw new Exception("Error al obtener los lotes próximos a vencer: " + e.getMessage());
        }
    }

    @Override
    public List<LoteFX> obtenerPorRangoFechaEntrada(Date fechaInicio, Date fechaFin) throws Exception {
        try {
            String fechaInicioStr = dateFormat.format(fechaInicio);
            String fechaFinStr = dateFormat.format(fechaFin);
            String url = String.format("%s/por-fecha-entrada?fechaInicio=%s&fechaFin=%s",
                    BASE_URL, fechaInicioStr, fechaFinStr);

            String response = HttpClient.get(url);
            List<JsonNode> lotesJson = objectMapper.readValue(response, new TypeReference<List<JsonNode>>() {});
            return convertirJsonALotesFX(lotesJson);
        } catch (Exception e) {
            logger.error("Error al obtener lotes por rango de fechas: {}", e.getMessage());
            throw new Exception("Error al obtener los lotes por rango de fecha: " + e.getMessage());
        }
    }

    @Override
    public LoteFX crear(LoteFX lote) throws Exception {
        try {
            ObjectNode loteJson = crearLoteJson(lote);
            String jsonBody = objectMapper.writeValueAsString(loteJson);
            String response = HttpClient.post(BASE_URL, jsonBody);
            JsonNode loteCreado = objectMapper.readTree(response);
            return convertirJsonALoteFX(loteCreado);
        } catch (Exception e) {
            logger.error("Error al crear lote: {}", e.getMessage());
            throw new Exception("Error al crear el lote: " + e.getMessage());
        }
    }

    @Override
    public LoteFX actualizar(LoteFX lote) throws Exception {
        if (lote.getIdLote() <= 0) {
            throw new IllegalArgumentException("El ID del lote debe ser válido para actualizarlo");
        }

        try {
            ObjectNode loteJson = crearLoteJson(lote);
            loteJson.put("idLote", lote.getIdLote());

            String jsonBody = objectMapper.writeValueAsString(loteJson);
            String response = HttpClient.put(BASE_URL + "/" + lote.getIdLote(), jsonBody);
            JsonNode loteActualizado = objectMapper.readTree(response);
            return convertirJsonALoteFX(loteActualizado);
        } catch (Exception e) {
            logger.error("Error al actualizar lote ID {}: {}", lote.getIdLote(), e.getMessage());
            throw new Exception("Error al actualizar el lote: " + e.getMessage());
        }
    }

    @Override
    public void eliminar(Integer id) throws Exception {
        try {
            HttpClient.delete(BASE_URL + "/" + id);
        } catch (Exception e) {
            logger.error("Error al eliminar lote ID {}: {}", id, e.getMessage());
            throw new Exception("Error al eliminar el lote: " + e.getMessage());
        }
    }

    @Override
    public LoteFX activar(Integer id) throws Exception {
        try {
            String url = ApiConfig.getBaseUrl() + "/api/lotes/" + id + "/activar";
            String respuesta = HttpClient.put(url, "");

            // Convertir respuesta JSON a objeto LoteFX
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new JavaTimeModule());
            objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            return objectMapper.readValue(respuesta, LoteFX.class);
        } catch (Exception e) {
            // Capturar mensajes específicos del backend (como lote vencido)
            String mensajeError = e.getMessage();

            // Si es un error de lote vencido, propagar el mensaje específico
            if (mensajeError.contains("vencido")) {
                throw new Exception("No se puede activar un lote vencido: " + mensajeError);
            }

            logger.error("Error al activar lote ID {}: {}", id, mensajeError);
            throw new Exception("Error al activar lote: " + mensajeError);
        }
    }

    @Override
    public LoteFX desactivar(Integer id) throws Exception {
        try {
            String response = HttpClient.patch(BASE_URL + "/" + id + "/desactivar", "");
            JsonNode jsonResponse = objectMapper.readTree(response);

            if (jsonResponse.has("lote")) {
                LoteFX lote = convertirJsonALoteFX(jsonResponse.get("lote"));
                if (lote.getProducto() == null) {
                    throw new Exception("El lote no tiene un producto asociado.");
                }
                return lote;
            } else if (jsonResponse.has("error")) {
                throw new Exception(jsonResponse.get("error").asText());
            } else {
                return convertirJsonALoteFX(jsonResponse);
            }
        } catch (Exception e) {
            logger.error("Error al desactivar lote ID {}: {}", id, e.getMessage());
            throw new Exception("Error al desactivar el lote: " + e.getMessage());
        }
    }

    @Override
    public List<LoteFX> obtenerActivos() throws Exception {
        try {
            String response = HttpClient.get(BASE_URL + "/activos");
            List<JsonNode> lotesJson = objectMapper.readValue(response, new TypeReference<List<JsonNode>>() {});
            return convertirJsonALotesFX(lotesJson);
        } catch (Exception e) {
            logger.error("Error al obtener lotes activos: {}", e.getMessage());
            throw new Exception("Error al obtener los lotes activos: " + e.getMessage());
        }
    }

    @Override
    public List<LoteFX> obtenerInactivos() throws Exception {
        try {
            String response = HttpClient.get(BASE_URL + "/inactivos");
            List<JsonNode> lotesJson = objectMapper.readValue(response, new TypeReference<List<JsonNode>>() {});
            return convertirJsonALotesFX(lotesJson);
        } catch (Exception e) {
            logger.error("Error al obtener lotes inactivos: {}", e.getMessage());
            throw new Exception("Error al obtener los lotes inactivos: " + e.getMessage());
        }
    }

    @Override
    public LoteFX reducirCantidadLote(Integer idLote, Integer cantidad) throws Exception {
        try {
            String url = String.format("%s/%d/reducir?cantidad=%d", BASE_URL, idLote, cantidad);
            String response = HttpClient.patch(url, "");
            JsonNode loteJson = objectMapper.readTree(response);
            return convertirJsonALoteFX(loteJson);
        } catch (Exception e) {
            logger.error("Error al reducir cantidad del lote ID {}: {}", idLote, e.getMessage());
            throw new Exception("Error al reducir cantidad del lote: " + e.getMessage());
        }
    }

    @Override
    public void reducirCantidadDeLotes(Integer idProducto, Integer cantidadTotal) throws Exception {
        try {
            String url = String.format("%s/producto/%d/reducir?cantidad=%d", BASE_URL, idProducto, cantidadTotal);
            HttpClient.patch(url, "");
        } catch (Exception e) {
            logger.error("Error al reducir cantidad de lotes para producto ID {}: {}", idProducto, e.getMessage());
            throw new Exception("Error al reducir cantidad de lotes: " + e.getMessage());
        }
    }

    @Override
    public LoteFX procesarDevolucion(Integer idLote, Integer cantidad) throws Exception {
        try {
            if (cantidad <= 0) {
                throw new IllegalArgumentException("La cantidad a devolver debe ser mayor que cero");
            }

            String url = String.format("%s/%d/devolucion?cantidad=%d", BASE_URL, idLote, cantidad);
            String response = HttpClient.patch(url, "");
            JsonNode jsonResponse = objectMapper.readTree(response);

            if (jsonResponse.has("error")) {
                throw new Exception(jsonResponse.get("error").asText());
            }

            if (jsonResponse.has("lote")) {
                return convertirJsonALoteFX(jsonResponse.get("lote"));
            } else {
                throw new Exception("Respuesta del servidor no contiene información del lote");
            }
        } catch (Exception e) {
            logger.error("Error al procesar devolución para lote ID {}: {}", idLote, e.getMessage());
            throw new Exception("Error al procesar la devolución: " + e.getMessage());
        }
    }

    // Métodos auxiliares
    private ObjectNode crearLoteJson(LoteFX lote) {
        ObjectNode loteJson = objectMapper.createObjectNode();

        if (lote.getIdEntrada() > 0) {
            loteJson.put("idEntrada", lote.getIdEntrada());
        }

        loteJson.put("idProducto", lote.getIdProducto());
        loteJson.put("numeroLote", lote.getNumeroLote());
        loteJson.put("cantidad", lote.getCantidad());
        loteJson.put("activo", lote.getActivo());

        // Fechas
        if (lote.getFechaEntrada() != null) {
            loteJson.put("fechaEntrada", dateFormat.format(lote.getFechaEntradaAsDate()));
        }

        if (lote.getFechaVencimiento() != null) {
            loteJson.put("fechaVencimiento", dateFormat.format(lote.getFechaVencimientoAsDate()));
        }

        return loteJson;
    }

    private LoteFX convertirJsonALoteFX(JsonNode json) {
        if (json == null) return null;

        Integer idLote = json.has("idLote") && !json.get("idLote").isNull() ?
                json.get("idLote").asInt() : null;
        Integer idEntrada = json.has("idEntrada") && !json.get("idEntrada").isNull() ?
                json.get("idEntrada").asInt() : null;

        // Procesar el producto
        ProductoFX producto = null;
        Integer idProducto = null;

        if (json.has("producto") && !json.get("producto").isNull()) {
            JsonNode productoJson = json.get("producto");
            producto = extraerProductoDeJson(productoJson);
            idProducto = producto != null ? producto.getIdProducto() : null;
        } else if (json.has("idProducto") && !json.get("idProducto").isNull()) {
            idProducto = json.get("idProducto").asInt();
        }

        String numeroLote = json.has("numeroLote") && !json.get("numeroLote").isNull() ?
                json.get("numeroLote").asText() : null;

        // Convertir fechas
        Date fechaEntrada = extraerFecha(json, "fechaEntrada");
        Date fechaVencimiento = extraerFecha(json, "fechaVencimiento");

        Integer cantidad = json.has("cantidad") && !json.get("cantidad").isNull() ?
                json.get("cantidad").asInt() : null;
        Boolean activo = json.has("activo") && !json.get("activo").isNull() ?
                json.get("activo").asBoolean() : true;

        return new LoteFX(idLote, idEntrada, producto, idProducto, numeroLote,
                fechaEntrada, fechaVencimiento, cantidad, activo);
    }

    private Date extraerFecha(JsonNode json, String nombreCampo) {
        if (json.has(nombreCampo) && !json.get(nombreCampo).isNull()) {
            try {
                return dateFormat.parse(json.get(nombreCampo).asText());
            } catch (Exception e) {
                logger.warn("Error al parsear fecha {}: {}", nombreCampo, e.getMessage());
            }
        }
        return null;
    }

    private ProductoFX extraerProductoDeJson(JsonNode productoJson) {
        if (productoJson == null) return null;

        try {
            Integer idProducto = productoJson.has("idProducto") ? productoJson.get("idProducto").asInt() : null;
            String codigo = productoJson.has("codigo") ? productoJson.get("codigo").asText() : null;
            String nombre = productoJson.has("nombre") ? productoJson.get("nombre").asText() : null;
            String descripcion = productoJson.has("descripcion") ? productoJson.get("descripcion").asText() : null;

            BigDecimal precioCosto = null;
            if (productoJson.has("precioCosto") && !productoJson.get("precioCosto").isNull()) {
                precioCosto = new BigDecimal(productoJson.get("precioCosto").asText());
            }

            BigDecimal precioVenta = null;
            if (productoJson.has("precioVenta") && !productoJson.get("precioVenta").isNull()) {
                precioVenta = new BigDecimal(productoJson.get("precioVenta").asText());
            }

            Integer stock = productoJson.has("stock") ? productoJson.get("stock").asInt() : 0;
            Integer stockMinimo = productoJson.has("stockMinimo") ? productoJson.get("stockMinimo").asInt() : 0;
            Integer stockMaximo = productoJson.has("stockMaximo") ? productoJson.get("stockMaximo").asInt() : 0;

            String categoria = productoJson.has("categoria") ?
                    (productoJson.get("categoria").isObject() ?
                            productoJson.get("categoria").get("nombre").asText() : null) : null;

            Integer idCategoria = productoJson.has("idCategoria") ? productoJson.get("idCategoria").asInt() :
                    (productoJson.has("categoria") && productoJson.get("categoria").has("idCategoria") ?
                            productoJson.get("categoria").get("idCategoria").asInt() : null);

            String proveedor = productoJson.has("proveedor") ?
                    (productoJson.get("proveedor").isObject() ?
                            productoJson.get("proveedor").get("nombre").asText() : null) : null;

            Integer idProveedor = productoJson.has("idProveedor") ? productoJson.get("idProveedor").asInt() :
                    (productoJson.has("proveedor") && productoJson.get("proveedor").has("idProveedor") ?
                            productoJson.get("proveedor").get("idProveedor").asInt() : null);

            Boolean estado = productoJson.has("estado") ? productoJson.get("estado").asBoolean() : true;

            return new ProductoFX(idProducto, codigo, nombre, descripcion, precioCosto, precioVenta,
                    stock, stockMinimo, stockMaximo, categoria, idCategoria, proveedor, idProveedor, estado);
        } catch (Exception e) {
            logger.warn("Error al construir objeto ProductoFX: {}", e.getMessage());
            return null;
        }
    }

    private List<LoteFX> convertirJsonALotesFX(List<JsonNode> jsonList) {
        List<LoteFX> lotes = new ArrayList<>();
        if (jsonList != null) {
            for (JsonNode json : jsonList) {
                LoteFX lote = convertirJsonALoteFX(json);
                if (lote != null) {
                    lotes.add(lote);
                }
            }
        }
        return lotes;
    }
}