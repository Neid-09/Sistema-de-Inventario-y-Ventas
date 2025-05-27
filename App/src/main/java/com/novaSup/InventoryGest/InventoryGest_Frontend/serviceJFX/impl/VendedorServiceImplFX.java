package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.VendedorFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.UsuarioSimplifiedFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.IVendedorService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementación del servicio para interactuar con la API de Vendedores.
 */
public class VendedorServiceImplFX implements IVendedorService {

    private static final Logger logger = LoggerFactory.getLogger(VendedorServiceImplFX.class);
    private static final String API_URL = ApiConfig.getBaseUrl() + "/api/vendedores";
    private final ObjectMapper objectMapper;

    public VendedorServiceImplFX() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @Override
    public ObservableList<VendedorFX> obtenerTodosLosVendedores() {
        try {
            String response = HttpClient.get(API_URL);
            List<VendedorDTO> vendedoresDTO = objectMapper.readValue(response, new TypeReference<List<VendedorDTO>>() {});
            List<VendedorFX> vendedores = vendedoresDTO.stream()
                    .map(this::convertirAVendedorFX)
                    .collect(Collectors.toList());
            return FXCollections.observableArrayList(vendedores);
        } catch (Exception e) {
            logger.error("Error al obtener todos los vendedores: {}", e.getMessage(), e);
            return FXCollections.observableArrayList();
        }
    }

    @Override
    public ObservableList<VendedorFX> obtenerVendedoresActivos() {
        try {
            String url = API_URL + "/activos";
            String response = HttpClient.get(url);
            List<VendedorDTO> vendedoresDTO = objectMapper.readValue(response, new TypeReference<List<VendedorDTO>>() {});
            List<VendedorFX> vendedores = vendedoresDTO.stream()
                    .map(this::convertirAVendedorFX)
                    .collect(Collectors.toList());
            return FXCollections.observableArrayList(vendedores);
        } catch (Exception e) {
            logger.error("Error al obtener vendedores activos: {}", e.getMessage(), e);
            return FXCollections.observableArrayList();
        }
    }

    @Override
    public VendedorFX obtenerVendedorPorId(Integer id) {
        if (id == null || id <= 0) {
            return null;
        }
        try {
            String url = API_URL + "/" + id;
            String response = HttpClient.get(url);
            VendedorDTO vendedorDTO = objectMapper.readValue(response, VendedorDTO.class);
            return convertirAVendedorFX(vendedorDTO);
        } catch (Exception e) {
            logger.warn("Vendedor con ID {} no encontrado o error al obtener: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    public VendedorFX obtenerVendedorConUsuario(Integer id) {
        if (id == null || id <= 0) {
            return null;
        }
        try {
            String url = API_URL + "/" + id + "/con-usuario";
            String response = HttpClient.get(url);
            VendedorDTO vendedorDTO = objectMapper.readValue(response, VendedorDTO.class);
            return convertirAVendedorFX(vendedorDTO);
        } catch (Exception e) {
            logger.warn("Vendedor con ID {} no encontrado o error al obtener con usuario: {}", id, e.getMessage());
            return null;
        }
    }

    @Override
    public VendedorFX crearVendedor(VendedorFX vendedor) {
        try {
            VendedorDTO dto = convertirAVendedorDTO(vendedor);
            String jsonVendedor = objectMapper.writeValueAsString(dto);
            logger.debug("Enviando POST a {} con datos: {}", API_URL, jsonVendedor);
            String response = HttpClient.post(API_URL, jsonVendedor);
            VendedorDTO vendedorGuardadoDTO = objectMapper.readValue(response, VendedorDTO.class);
            return convertirAVendedorFX(vendedorGuardadoDTO);
        } catch (Exception e) {
            logger.error("Error al crear vendedor: {}", e.getMessage(), e);
            return null;
        }
    }

    @Override
    public VendedorFX actualizarVendedor(Integer id, VendedorFX vendedor) {
        if (id == null || id <= 0) {
            return null;
        }
        try {
            VendedorDTO dto = convertirAVendedorDTO(vendedor);
            String jsonVendedor = objectMapper.writeValueAsString(dto);
            String url = API_URL + "/" + id;
            logger.debug("Enviando PUT a {} con datos: {}", url, jsonVendedor);
            String response = HttpClient.put(url, jsonVendedor);
            VendedorDTO vendedorActualizadoDTO = objectMapper.readValue(response, VendedorDTO.class);
            return convertirAVendedorFX(vendedorActualizadoDTO);
        } catch (Exception e) {
            logger.error("Error al actualizar vendedor con ID {}: {}", id, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public boolean eliminarVendedor(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        try {
            String url = API_URL + "/" + id;
            HttpClient.delete(url);
            return true;
        } catch (Exception e) {
            logger.error("Error al eliminar vendedor con ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean activarVendedor(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        try {
            String url = API_URL + "/" + id + "/activar";
            HttpClient.patch(url, "");
            return true;
        } catch (Exception e) {
            logger.error("Error al activar vendedor con ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    @Override
    public boolean desactivarVendedor(Integer id) {
        if (id == null || id <= 0) {
            return false;
        }
        try {
            String url = API_URL + "/" + id + "/desactivar";
            HttpClient.patch(url, "");
            return true;
        } catch (Exception e) {
            logger.error("Error al desactivar vendedor con ID {}: {}", id, e.getMessage(), e);
            return false;
        }
    }

    // Métodos de conversión

    /**
     * Convierte un VendedorDTO a VendedorFX.
     */
    private VendedorFX convertirAVendedorFX(VendedorDTO dto) {
        VendedorFX vendedorFX = new VendedorFX();
        vendedorFX.setIdVendedor(dto.getIdVendedor());
        vendedorFX.setIdUsuario(dto.getIdUsuario());
        vendedorFX.setObjetivoVentas(dto.getObjetivoVentas());
        
        // Convertir Date a LocalDate
        if (dto.getFechaContratacion() != null) {
            vendedorFX.setFechaContratacion(dto.getFechaContratacion().toLocalDate());
        }
        
        // Convertir usuario si existe
        if (dto.getUsuario() != null) {
            UsuarioSimplifiedFX usuarioFX = new UsuarioSimplifiedFX(
                    dto.getUsuario().getIdUsuario(),
                    dto.getUsuario().getNombre()
            );
            vendedorFX.setUsuario(usuarioFX);
        }
        
        return vendedorFX;
    }

    /**
     * Convierte un VendedorFX a VendedorDTO.
     */
    private VendedorDTO convertirAVendedorDTO(VendedorFX vendedorFX) {
        VendedorDTO dto = new VendedorDTO();
        dto.setIdVendedor(vendedorFX.getIdVendedor() > 0 ? vendedorFX.getIdVendedor() : null);
        dto.setIdUsuario(vendedorFX.getIdUsuario() > 0 ? vendedorFX.getIdUsuario() : null);
        dto.setObjetivoVentas(vendedorFX.getObjetivoVentas());
        
        // Convertir LocalDate a Date
        if (vendedorFX.getFechaContratacion() != null) {
            dto.setFechaContratacion(Date.valueOf(vendedorFX.getFechaContratacion()));
        }
        
        // Convertir usuario si existe
        if (vendedorFX.getUsuario() != null) {
            UsuarioSimplifiedDTO usuarioDTO = new UsuarioSimplifiedDTO();
            usuarioDTO.setIdUsuario(vendedorFX.getUsuario().getIdUsuario());
            usuarioDTO.setNombre(vendedorFX.getUsuario().getNombre());
            dto.setUsuario(usuarioDTO);
        }
        
        return dto;
    }

    // DTOs internos para comunicación con la API

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class VendedorDTO {
        private Integer idVendedor;
        private Integer idUsuario;
        private UsuarioSimplifiedDTO usuario;
        private BigDecimal objetivoVentas;
        @JsonFormat(pattern = "yyyy-MM-dd")
        private Date fechaContratacion;

        // Getters y Setters
        public Integer getIdVendedor() { return idVendedor; }
        public void setIdVendedor(Integer idVendedor) { this.idVendedor = idVendedor; }

        public Integer getIdUsuario() { return idUsuario; }
        public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

        public UsuarioSimplifiedDTO getUsuario() { return usuario; }
        public void setUsuario(UsuarioSimplifiedDTO usuario) { this.usuario = usuario; }

        public BigDecimal getObjetivoVentas() { return objetivoVentas; }
        public void setObjetivoVentas(BigDecimal objetivoVentas) { this.objetivoVentas = objetivoVentas; }

        public Date getFechaContratacion() { return fechaContratacion; }
        public void setFechaContratacion(Date fechaContratacion) { this.fechaContratacion = fechaContratacion; }
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class UsuarioSimplifiedDTO {
        private Integer idUsuario;
        private String nombre;

        // Getters y Setters
        public Integer getIdUsuario() { return idUsuario; }
        public void setIdUsuario(Integer idUsuario) { this.idUsuario = idUsuario; }

        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }
    }
}
