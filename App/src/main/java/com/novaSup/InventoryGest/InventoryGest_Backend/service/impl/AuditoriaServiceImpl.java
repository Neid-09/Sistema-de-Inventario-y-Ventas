package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Auditoria;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.AuditoriaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.AuditoriaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.security.service.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class AuditoriaServiceImpl implements AuditoriaService {

    private final AuditoriaRepository auditoriaRepository;

    private final ObjectMapper objectMapper;

    public AuditoriaServiceImpl(AuditoriaRepository auditoriaRepository, ObjectMapper objectMapper) {
        this.auditoriaRepository = auditoriaRepository;
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
                    Object principal = auth.getPrincipal();
                    if (principal instanceof CustomUserDetails) {
                        CustomUserDetails userDetails = (CustomUserDetails) principal;
                        auditoria.setIdUsuario(userDetails.getIdUsuario());
                    } else {
                        // Opcional: Loggear una advertencia si el principal no es del tipo esperado
                        // logger.warn("El principal autenticado no es una instancia de CustomUserDetails: {}", principal.getClass().getName());
                        // Podrías decidir no setear el idUsuario o setearlo a un valor por defecto si esto ocurre.
                    }
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