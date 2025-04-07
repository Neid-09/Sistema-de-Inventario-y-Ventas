package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Notificacion;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.NotificacionService;
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
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<List<Notificacion>> obtenerTodasLasNotificaciones() {
        return ResponseEntity.ok(notificacionService.obtenerTodasLasNotificaciones());
    }

    @PatchMapping("/{id}/leer")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_notificaciones')")
    public ResponseEntity<?> marcarComoLeida(@PathVariable Integer id) {
        Integer idUsuario = obtenerIdUsuarioActual();

        return notificacionService.obtenerPorId(id)
                .map(notificacion -> {
                    // Verificar que la notificación pertenece al usuario actual o es admin
                    if (!notificacion.getIdUsuario().equals(idUsuario) &&
                            !tieneRolAdmin()) {
                        Map<String, String> error = new HashMap<>();
                        error.put("mensaje", "No tiene permisos para modificar esta notificación");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
                    }

                    Notificacion notificacionLeida = notificacionService.marcarComoLeida(id);
                    return ResponseEntity.ok(notificacionLeida);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_notificaciones')")
    public ResponseEntity<?> eliminarNotificacion(@PathVariable Integer id) {
        Integer idUsuario = obtenerIdUsuarioActual();

        return notificacionService.obtenerPorId(id)
                .map(notificacion -> {
                    // Verificar que la notificación pertenece al usuario actual o es admin
                    if (!notificacion.getIdUsuario().equals(idUsuario) &&
                            !tieneRolAdmin()) {
                        Map<String, String> error = new HashMap<>();
                        error.put("mensaje", "No tiene permisos para eliminar esta notificación");
                        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(error);
                    }

                    notificacionService.eliminar(id);
                    return ResponseEntity.ok().build();
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