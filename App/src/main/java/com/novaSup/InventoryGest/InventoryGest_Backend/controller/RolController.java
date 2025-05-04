package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Permiso;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Rol;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.PermisoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.RolRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/roles")
@CrossOrigin("*")
public class RolController {

    @Autowired
    private RolRepository rolRepository;

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_roles')")
    public ResponseEntity<?> listarRoles() {
        try {
            return ResponseEntity.ok(rolRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_roles')")
    public ResponseEntity<?> obtenerRol(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(rolRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_roles')")
    public ResponseEntity<?> crearRol(@RequestBody Rol rol) {
        try {
            return ResponseEntity.ok(rolRepository.save(rol));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_roles')")
    public ResponseEntity<?> actualizarRol(@PathVariable Integer id, @RequestBody Rol rol) {
        try {
            Rol rolExistente = rolRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            rolExistente.setNombre(rol.getNombre());

            return ResponseEntity.ok(rolRepository.save(rolExistente));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_roles')")
    public ResponseEntity<?> eliminarRol(@PathVariable Integer id) {
        try {
            // Verificar que no haya usuarios con este rol
            if (usuarioService.existenUsuariosConRol(id)) {
                return ResponseEntity.badRequest().body("No se puede eliminar un rol asignado a usuarios");
            }

            rolRepository.deleteById(id);
            return ResponseEntity.ok("Rol eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}/permisos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_roles')")
    public ResponseEntity<?> obtenerPermisosRol(@PathVariable Integer id) {
        try {
            Rol rol = rolRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            return ResponseEntity.ok(rol.getPermisos());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}/permisos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_roles')")
    public ResponseEntity<?> asignarPermisos(@PathVariable Integer id, @RequestBody List<Integer> permisosIds) {
        try {
            Rol rol = rolRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            Set<Permiso> permisos = new HashSet<>();
            for (Integer permisoId : permisosIds) {
                Permiso permiso = permisoRepository.findById(permisoId)
                        .orElseThrow(() -> new RuntimeException("Permiso no encontrado: " + permisoId));
                permisos.add(permiso);
            }

            rol.setPermisos(permisos);
            return ResponseEntity.ok(rolRepository.save(rol));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}