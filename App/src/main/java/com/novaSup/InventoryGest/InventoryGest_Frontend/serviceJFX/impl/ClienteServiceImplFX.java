package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule; // Para manejar LocalDate
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.ClienteFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IClienteService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para interactuar con la API de Clientes.
 */
public class ClienteServiceImplFX implements IClienteService {

    private static final Logger logger = LoggerFactory.getLogger(ClienteServiceImplFX.class);
    private static final String API_URL = ApiConfig.getBaseUrl() + "/api/clientes"; // Ruta base de la API de clientes
    private final ObjectMapper objectMapper;

    public ClienteServiceImplFX() {
        this.objectMapper = new ObjectMapper();
        // Registrar el módulo para manejar tipos de fecha/hora de Java 8 (LocalDate, etc.)
        this.objectMapper.registerModule(new JavaTimeModule());
        // Configurar para no fallar en propiedades desconocidas y formatear fechas como ISO
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        // Considera añadir: objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false); si es necesario
    }

    @Override
    public List<ClienteFX> obtenerTodosLosClientes() throws Exception {
        try {
            String response = HttpClient.get(API_URL);
            List<ClienteDTO> clientesDTO = objectMapper.readValue(response, new TypeReference<List<ClienteDTO>>() {});
            return clientesDTO.stream()
                    .map(this::convertirAClienteFX)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error al obtener todos los clientes: {}", e.getMessage(), e);
            throw new Exception("No se pudo obtener la lista de clientes: " + e.getMessage());
        }
    }

    @Override
    public Optional<ClienteFX> obtenerClientePorId(Integer id) throws Exception {
        if (id == null || id <= 0) {
            return Optional.empty();
        }
        try {
            String url = API_URL + "/" + id;
            String response = HttpClient.get(url);
            ClienteDTO clienteDTO = objectMapper.readValue(response, ClienteDTO.class);
            return Optional.of(convertirAClienteFX(clienteDTO));
        } catch (Exception e) {
            // Si la API devuelve 404, HttpClient podría lanzar una excepción.
            // O podríamos verificar el código de estado si HttpClient lo permitiera fácilmente.
            logger.warn("Cliente con ID {} no encontrado o error al obtener: {}", id, e.getMessage());
            return Optional.empty(); // Devolver vacío si no se encuentra o hay error
        }
    }

    @Override
    public Optional<ClienteFX> obtenerClientePorCedula(String cedula) throws Exception {
         if (cedula == null || cedula.trim().isEmpty()) {
            return Optional.empty();
        }
        try {
            // Asegúrate de que la cédula esté codificada correctamente para la URL si contiene caracteres especiales
            String url = API_URL + "/cedula/" + java.net.URLEncoder.encode(cedula, "UTF-8");
            String response = HttpClient.get(url);
            ClienteDTO clienteDTO = objectMapper.readValue(response, ClienteDTO.class);
            return Optional.of(convertirAClienteFX(clienteDTO));
        } catch (Exception e) {
            logger.warn("Cliente con cédula {} no encontrado o error al obtener: {}", cedula, e.getMessage());
            return Optional.empty();
        }
    }

     @Override
    public ClienteFX guardarOActualizarCliente(ClienteFX cliente) throws Exception {
        try {
            ClienteDTO dto = convertirAClienteDTO(cliente);
            String jsonCliente = objectMapper.writeValueAsString(dto);
            String response;

            // Determinar si es creación (POST) o actualización (PUT)
            if (cliente.getIdCliente() == null || cliente.getIdCliente() <= 0) {
                // Crear nuevo cliente (POST)
                 logger.debug("Enviando POST a {} con datos: {}", API_URL, jsonCliente);
                response = HttpClient.post(API_URL, jsonCliente);
            } else {
                // Actualizar cliente existente (PUT)
                String url = API_URL + "/" + cliente.getIdCliente();
                 logger.debug("Enviando PUT a {} con datos: {}", url, jsonCliente);
                response = HttpClient.put(url, jsonCliente);
            }

            ClienteDTO clienteGuardadoDTO = objectMapper.readValue(response, ClienteDTO.class);
            return convertirAClienteFX(clienteGuardadoDTO);
        } catch (Exception e) {
            logger.error("Error al guardar o actualizar cliente: {}", e.getMessage(), e);
            // Podríamos intentar extraer un mensaje más específico si la API devuelve errores de validación en el cuerpo
             String mensajeError = "Error al procesar la solicitud del cliente.";
             if (e.getMessage().contains("400")) { // Ejemplo básico de manejo de Bad Request
                 mensajeError = "Datos inválidos. Verifique la cédula o correo.";
             } else if (e.getMessage().contains("404")) {
                 mensajeError = "El cliente a actualizar no fue encontrado.";
             }
            throw new Exception(mensajeError + " Detalles: " + e.getMessage());
        }
    }


    @Override
    public boolean eliminarCliente(Integer id) throws Exception {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El ID del cliente para eliminar no puede ser nulo o inválido.");
        }
        try {
            String url = API_URL + "/" + id;
            HttpClient.delete(url); // HttpClient.delete no devuelve cuerpo en éxito (204 No Content)
            return true; // Asumir éxito si no hay excepción
        } catch (Exception e) {
             logger.error("Error al eliminar cliente con ID {}: {}", id, e.getMessage(), e);
             if (e.getMessage().contains("404")) { // Cliente no encontrado
                 return false;
             } else if (e.getMessage().contains("409") || e.getMessage().contains("ConstraintViolationException")) { // Conflicto (ej. dependencias)
                 throw new Exception("No se puede eliminar el cliente porque tiene registros asociados.");
             }
            // Otro tipo de error
            throw new Exception("No se pudo eliminar el cliente: " + e.getMessage());
        }
    }

    @Override
    public ClienteFX anadirPuntosFidelidad(Integer idCliente, int puntos) throws Exception {
        if (idCliente == null || idCliente <= 0) {
            throw new IllegalArgumentException("ID de cliente inválido.");
        }
        if (puntos <= 0) {
            throw new IllegalArgumentException("La cantidad de puntos a añadir debe ser positiva.");
        }
        try {
            // El endpoint espera los puntos como RequestParam, no en el body.
            // HttpClient necesita ser adaptado o usar una librería que soporte Query Params fácilmente.
            // Solución temporal: construir la URL con el parámetro.
            String url = API_URL + "/" + idCliente + "/puntos?puntos=" + puntos;
             logger.debug("Enviando POST a {}", url);
            // Usamos POST aunque no enviemos body, como define el controller
            String response = HttpClient.post(url, ""); // Enviar body vacío

            ClienteDTO clienteActualizadoDTO = objectMapper.readValue(response, ClienteDTO.class);
            return convertirAClienteFX(clienteActualizadoDTO);
        } catch (Exception e) {
            logger.error("Error al añadir puntos al cliente {}: {}", idCliente, e.getMessage(), e);
             String mensajeError = "Error al añadir puntos.";
             if (e.getMessage().contains("400")) {
                 mensajeError = "Datos inválidos para añadir puntos.";
             } else if (e.getMessage().contains("404")) {
                 mensajeError = "Cliente no encontrado para añadir puntos.";
             }
            throw new Exception(mensajeError + " Detalles: " + e.getMessage());
        }
    }

    // --- Métodos de Conversión ---

    /**
     * Convierte un objeto ClienteDTO (recibido de la API) a ClienteFX (modelo UI).
     */
    private ClienteFX convertirAClienteFX(ClienteDTO dto) {
        if (dto == null) return null;
        return new ClienteFX(
                dto.idCliente,
                dto.cedula,
                dto.nombre,
                dto.apellido,
                dto.correo,
                dto.telefono,
                dto.direccion,
                dto.puntosFidelidad,
                dto.limiteCredito,
                dto.totalComprado,
                dto.ultimaCompra
        );
    }

    /**
     * Convierte un objeto ClienteFX (modelo UI) a ClienteDTO (para enviar a la API).
     */
    private ClienteDTO convertirAClienteDTO(ClienteFX fx) {
        if (fx == null) return null;
        ClienteDTO dto = new ClienteDTO();
        // Si el ID es 0 o null, no lo enviamos o lo enviamos como null para creación
        dto.idCliente = (fx.getIdCliente() != null && fx.getIdCliente() > 0) ? fx.getIdCliente() : null;
        dto.cedula = fx.getCedula();
        dto.nombre = fx.getNombre();
        dto.apellido = fx.getApellido();
        dto.correo = fx.getCorreo();
        dto.telefono = fx.getTelefono();
        dto.direccion = fx.getDireccion();
        dto.puntosFidelidad = fx.getPuntosFidelidad();
        dto.limiteCredito = fx.getLimiteCredito();
        // Estos campos usualmente no se envían desde el frontend para crear/actualizar,
        // son calculados por el backend, pero los incluimos por completitud del DTO.
        dto.totalComprado = fx.getTotalComprado();
        dto.ultimaCompra = fx.getUltimaCompra();
        return dto;
    }

    // --- DTO Interno ---

    /**
     * Data Transfer Object (DTO) para representar la estructura JSON
     * esperada/devuelta por la API de Clientes.
     * Mantiene el frontend desacoplado del modelo exacto del backend.
     */
    private static class ClienteDTO {
        public Integer idCliente;
        public String cedula;
        public String nombre;
        public String apellido;
        public String correo;
        public String telefono;
        public String direccion;
        public Integer puntosFidelidad;
        public BigDecimal limiteCredito;
        public BigDecimal totalComprado;
        public LocalDate ultimaCompra; // Asegúrate que Jackson maneje bien LocalDate

        // Constructor, getters/setters opcionales si Jackson los necesita,
        // pero con campos públicos suele ser suficiente.
    }
}
