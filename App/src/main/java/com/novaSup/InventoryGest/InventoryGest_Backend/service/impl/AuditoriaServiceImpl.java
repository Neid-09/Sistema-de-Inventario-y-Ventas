package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Auditoria;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.AuditoriaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.AuditoriaService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    private final HttpServletRequest request;

    private final ObjectMapper objectMapper;

    public AuditoriaServiceImpl(AuditoriaRepository auditoriaRepository, HttpServletRequest request, ObjectMapper objectMapper) {
        this.auditoriaRepository = auditoriaRepository;
        this.request = request;
        this.objectMapper = objectMapper;
    }

    @Override
    public Auditoria registrarAccion(String accion, String tablaAfectada, Integer idRegistro,
                                     Object datosAnteriores, Object datosNuevos, Integer idUsuario) {
        try {
            Auditoria auditoria = new Auditoria();
            auditoria.setAccion(accion);
            auditoria.setTablaAfectada(tablaAfectada);
            auditoria.setIdRegistroAfectado(idRegistro);
            auditoria.setFecha(LocalDateTime.now());

            // Si no se proporciona el ID de usuario, intentar obtenerlo del contexto de seguridad
            if (idUsuario == null) {
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                if (auth != null && auth.isAuthenticated()) {
                    // Aquí deberías extraer el ID del usuario según tu implementación
                    // Este es un ejemplo genérico
                    Object principal = auth.getPrincipal();
                    // Lógica para extraer el ID del usuario desde el principal
                }
            } else {
                auditoria.setIdUsuario(idUsuario);
            }

            // Convertir objetos a JSON
            if (datosAnteriores != null) {
                auditoria.setDatosAnteriores(objectMapper.writeValueAsString(datosAnteriores));
            }

            if (datosNuevos != null) {
                auditoria.setDatosNuevos(objectMapper.writeValueAsString(datosNuevos));
            }

            return auditoriaRepository.save(auditoria);
        } catch (Exception e) {
            // Log the error
            e.printStackTrace();
            return null;
        }
    }
}