package com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.novaSup.InventoryGest.InventoryGest_Frontend.modelJFX.NotificacionFX;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.interfaces.INotificacionService;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.ApiConfig;
import com.novaSup.InventoryGest.InventoryGest_Frontend.serviceJFX.util.HttpClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class NotificacionServiceImplFX implements INotificacionService {

    private static final String API_URL = ApiConfig.getBaseUrl() + "/api/notificaciones";
    private final ObjectMapper objectMapper;

    public NotificacionServiceImplFX() {
        this.objectMapper = new ObjectMapper();
        // Configurar el mapper para manejar fechas con LocalDateTime
        objectMapper.registerModule(new JavaTimeModule());
    }

    @Override
    public List<NotificacionFX> obtenerNotificaciones() throws Exception {
        String respuesta = HttpClient.get(API_URL);
        List<NotificacionDTO> notificaciones = objectMapper.readValue(respuesta,
                new TypeReference<List<NotificacionDTO>>() {});
        return notificaciones.stream()
                .map(this::convertirANotificacionFX)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificacionFX> obtenerNotificacionesNoLeidas() throws Exception {
        String respuesta = HttpClient.get(API_URL + "/no-leidas");
        List<NotificacionDTO> notificaciones = objectMapper.readValue(respuesta,
                new TypeReference<List<NotificacionDTO>>() {});
        return notificaciones.stream()
                .map(this::convertirANotificacionFX)
                .collect(Collectors.toList());
    }

    @Override
    public int contarNotificacionesNoLeidas() throws Exception {
        List<NotificacionFX> noLeidas = obtenerNotificacionesNoLeidas();
        return noLeidas.size();
    }

    @Override
    public NotificacionFX marcarComoLeida(Integer idNotificacion) throws Exception {
        String respuesta = HttpClient.patch(API_URL + "/" + idNotificacion + "/leer", "");
        NotificacionDTO notificacion = objectMapper.readValue(respuesta, NotificacionDTO.class);
        return convertirANotificacionFX(notificacion);
    }

    @Override
    public void marcarComoNoLeida(Integer idNotificacion) throws Exception {
        String url = ApiConfig.getBaseUrl() + "/api/notificaciones/" + idNotificacion + "/marcar-no-leida";
        HttpClient.patch(url, "");
    }

    @Override
    public void eliminarNotificacion(Integer idNotificacion) throws Exception {
        HttpClient.delete(API_URL + "/" + idNotificacion);
    }

    // Método de conversión de DTO a FX
    private NotificacionFX convertirANotificacionFX(NotificacionDTO dto) {
        return new NotificacionFX(
                dto.idNotificacion,
                dto.idUsuario,
                dto.titulo,
                dto.mensaje,
                dto.fecha,
                dto.leida,
                dto.tipo,
                dto.idReferencia
        );
    }

    // Clase DTO interna para deserialización
    private static class NotificacionDTO {
        public Integer idNotificacion;
        public Integer idUsuario;
        public String titulo;
        public String mensaje;
        public LocalDateTime fecha;
        public Boolean leida;
        public String tipo;
        public Integer idReferencia;
    }
}