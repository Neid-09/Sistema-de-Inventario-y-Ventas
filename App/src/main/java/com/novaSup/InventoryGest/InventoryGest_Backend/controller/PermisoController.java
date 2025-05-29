package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Permiso;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.PermisoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/permisos")
@CrossOrigin("*")
public class PermisoController {

    @Autowired
    private PermisoRepository permisoRepository;


    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_permisos')")
    public ResponseEntity<?> listarPermisos() {
        try {
            return ResponseEntity.ok(permisoRepository.findAll());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_permisos')")
    public ResponseEntity<?> obtenerPermiso(@PathVariable Integer id) {
        try {
            return ResponseEntity.ok(permisoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado")));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_permisos')")
    public ResponseEntity<?> crearPermiso(@RequestBody Permiso permiso) {
        try {
            return ResponseEntity.ok(permisoRepository.save(permiso));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_permisos')")
    public ResponseEntity<?> actualizarPermiso(@PathVariable Integer id, @RequestBody Permiso permiso) {
        try {
            Permiso permisoExistente = permisoRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Permiso no encontrado"));

            permisoExistente.setNombre(permiso.getNombre());
            permisoExistente.setDescripcion(permiso.getDescripcion());

            return ResponseEntity.ok(permisoRepository.save(permisoExistente));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_permisos')")
    public ResponseEntity<?> eliminarPermiso(@PathVariable Integer id) {
        try {
            permisoRepository.deleteById(id);
            return ResponseEntity.ok("Permiso eliminado correctamente");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}