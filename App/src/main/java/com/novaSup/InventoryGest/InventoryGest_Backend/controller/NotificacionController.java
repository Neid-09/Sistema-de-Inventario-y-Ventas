package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Notificacion;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.NotificacionService;
import com.novaSup.InventoryGest.InventoryGest_Backend.security.service.CustomUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notificaciones")
@CrossOrigin("*") // Agregar para consistencia con otros controladores
public class NotificacionController {

    @Autowired
    private NotificacionService notificacionService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_notificaciones')")
    public ResponseEntity<List<Notificacion>> obtenerNotificacionesDelUsuario() {
        Integer idUsuario = obtenerIdUsuarioActual();
        if (idUsuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(notificacionService.obtenerNotificacionesPorUsuario(idUsuario));
    }

    @GetMapping("/no-leidas")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_notificaciones')")
    public ResponseEntity<List<Notificacion>> obtenerNotificacionesNoLeidas() {
        Integer idUsuario = obtenerIdUsuarioActual();
        if (idUsuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.ok(notificacionService.obtenerNotificacionesNoLeidasPorUsuario(idUsuario));
    }

    @GetMapping("/todas")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_notificaciones')")
    public ResponseEntity<List<Notificacion>> obtenerTodasLasNotificaciones() {
        return ResponseEntity.ok(notificacionService.obtenerTodasLasNotificaciones());
    }


    @PatchMapping("/{id}/leer")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_notificaciones')")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Integer id) {
        Integer idUsuario = obtenerIdUsuarioActual();
        if (idUsuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return notificacionService.obtenerPorId(id)
                .map(notificacion -> {
                    // Verificar que la notificación pertenece al usuario actual o es admin
                    if (!notificacion.getIdUsuario().equals(idUsuario) && !tieneRolAdmin()) {
                        Map<String, String> error = new HashMap<>();
                        error.put("mensaje", "No tiene permisos para modificar esta notificación");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
                    }

                    try {
                        Notificacion notificacionLeida = notificacionService.marcarComoLeida(id);
                        return ResponseEntity.ok(notificacionLeida);
                    } catch (Exception e) {
                        Map<String, String> error = new HashMap<>();
                        error.put("mensaje", "Error al marcar notificación como leída: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @PatchMapping("/{id}/marcar-no-leida")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_notificaciones')")
    public ResponseEntity<?> marcarComoNoLeida(@PathVariable Integer id) {
        try {
            Notificacion notificacion = notificacionService.marcarComoNoLeida(id);
            return ResponseEntity.ok(notificacion);
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Error al marcar notificación como no leída: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuesta);
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_notificaciones')")
    public ResponseEntity<?> eliminarNotificacion(@PathVariable Integer id) {
        Integer idUsuario = obtenerIdUsuarioActual();
        if (idUsuario == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return notificacionService.obtenerPorId(id)
                .map(notificacion -> {
                    // Verificar que la notificación pertenece al usuario actual o es admin
                    if (!notificacion.getIdUsuario().equals(idUsuario) && !tieneRolAdmin()) {
                        Map<String, String> error = new HashMap<>();
                        error.put("mensaje", "No tiene permisos para eliminar esta notificación");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
                    }

                    try {
                        notificacionService.eliminar(id);
                        Map<String, String> respuesta = new HashMap<>();
                        respuesta.put("mensaje", "Notificación eliminada correctamente");
                        return ResponseEntity.ok(respuesta);
                    } catch (Exception e) {
                        Map<String, String> error = new HashMap<>();
                        error.put("mensaje", "Error al eliminar la notificación: " + e.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
                    }
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // Obtener el ID del usuario autenticado
    private Integer obtenerIdUsuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && auth.getPrincipal() instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) auth.getPrincipal();
            if (userDetails instanceof CustomUserDetails) {
                return ((CustomUserDetails) userDetails).getIdUsuario();
            }
        }
        return null;
    }

    private boolean tieneRolAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth != null && auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMINISTRADOR"));
    }
}