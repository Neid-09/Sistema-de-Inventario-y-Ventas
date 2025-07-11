package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Permiso;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.PermisoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.security.util.SecurityUtil;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.UsuarioService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/usuarios")
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
    public ResponseEntity<?> listarUsuarios(@RequestParam(required = false) Boolean estado) {
        try {
            List<Usuario> usuarios;
            if (estado == null) {
                usuarios = usuarioService.listarUsuarios();
            } else if (estado) {
                usuarios = usuarioService.listarUsuariosActivos();
            } else {
                usuarios = usuarioService.listarUsuariosInactivos();
            }
            return ResponseEntity.ok(usuarios);
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
            if (usuario.getIdUsuario() != null && !usuario.getIdUsuario().equals(id)) {
                return ResponseEntity.badRequest().body("El ID del path no coincide con el ID del cuerpo de la petición.");
            }
            usuario.setIdUsuario(id);
            return ResponseEntity.ok(usuarioService.actualizarUsuario(id, usuario));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('activar_usuario')")
    public ResponseEntity<?> activarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.activarUsuario(id);
            return ResponseEntity.ok("Usuario activado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('eliminar_usuario')")
    public ResponseEntity<?> desactivarUsuario(@PathVariable Integer id) {
        try {
            usuarioService.desactivarUsuario(id);
            return ResponseEntity.ok("Usuario desactivado correctamente");
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

    @GetMapping("/{usuarioId}/permisos-disponibles")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_usuarios')")
    public ResponseEntity<?> obtenerPermisosDisponibles(@PathVariable Integer usuarioId) {
        try {
            Usuario usuario = usuarioService.obtenerUsuarioPorId(usuarioId);
            Map<String, Object> resultado = new HashMap<>();

            Set<Permiso> permisosRol = usuario.getRol() != null ?
                    usuario.getRol().getPermisos() : new HashSet<>();

            Set<Permiso> permisosPersonalizados = usuario.getPermisosPersonalizados();

            List<Permiso> todosLosPermisos = permisoRepository.findAll();

            resultado.put("permisosRol", permisosRol);
            resultado.put("permisosPersonalizados", permisosPersonalizados);
            resultado.put("todosLosPermisos", todosLosPermisos);

            return ResponseEntity.ok(resultado);
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