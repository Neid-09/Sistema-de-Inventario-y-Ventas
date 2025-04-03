package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Permiso;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.PermisoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.security.util.SecurityUtil;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.UsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.impl.UsuarioServiceImpl;
import com.novaSup.InventoryGest.InventoryGest_Backend.util.PermisoVerificacionResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_usuarios')")
    public ResponseEntity<?> listarUsuarios() {
        try {
            return ResponseEntity.ok(usuarioService.listarUsuarios());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_usuarios')")
    public ResponseEntity<?> obtenerUsuario(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(usuarioService.obtenerUsuarioPorId(id));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('crear_usuario')")
    public ResponseEntity<?> crearUsuario(@RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(usuarioService.guardarUsuario(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('editar_usuario')")
    public ResponseEntity<?> actualizarUsuario(@PathVariable Integer id, @RequestBody Usuario usuario) {
        try {
            return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('eliminar_usuario')")
    public ResponseEntity<?> eliminarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.eliminarUsuario(id);
            return ResponseEntity.ok("Usuario eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{usuarioId}/permisos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_usuarios')")
    public ResponseEntity<?> asignarPermisosUsuario(
            @PathVariable Integer usuarioId,
            @RequestBody List<Integer> permisosIds) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
            Set<Permiso> permisos = new HashSet<>();

            for (Integer permisoId : permisosIds) {
                Permiso permiso = permisoRepository.findById(permisoId)
                        .orElseThrow(() -> new RuntimeException("Permiso no encontrado: " + permisoId));
                permisos.add(permiso);
            }

            usuario.setPermisosPersonalizados(permisos);
            return ResponseEntity.ok(usuarioService.guardarUsuario(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{usuarioId}/permisos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_usuarios')")
    public ResponseEntity<?> obtenerPermisosUsuario(@PathVariable Integer usuarioId) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
            return ResponseEntity.ok(((UsuarioServiceImpl) usuarioService).obtenerTodosLosPermisos(usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/mis-permisos")
    public ResponseEntity<?> obtenerMisPermisos() {
        try {
            Usuario usuario = securityUtil.getUsuarioActual();
            if (usuario == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Usuario no autenticado");
            }

            List<Permiso> permisos = usuarioService.obtenerTodosLosPermisos(usuario);
            if (permisos == null) {
                permisos = new ArrayList<>();
            }

            return ResponseEntity.ok(permisos);
        } catch (Exception e) {
            String mensaje = e.getMessage() != null ? e.getMessage() : "Error interno del servidor";
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al cargar permisos: " + mensaje);
        }
    }

    @GetMapping("/perfil")
    public ResponseEntity<?> obtenerPerfil() {
        try {
            Usuario usuario = securityUtil.getUsuarioActual();
            return ResponseEntity.ok(usuario);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}