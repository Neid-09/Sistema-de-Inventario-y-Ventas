package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Permiso;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.PermisoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/permisos")
@CrossOrigin("*")
public class PermisoController {

    @Autowired
    private PermisoRepository permisoRepository;

    @Autowired
    private UsuarioServiceImpl usuarioService;

    @GetMapping
    public ResponseEntity<?> listarPermisos(@RequestHeader("usuario-id") Integer idUsuario) {
        try {
            Usuario admin = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (!usuarioService.tienePermiso(admin, "gestionar_permisos")) {
                return ResponseEntity.status(403).body("No tienes permisos para gestionar permisos");
            }

            return ResponseEntity.ok(permisoRepository.findAll());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping
    public ResponseEntity<?> crearPermiso(@RequestBody Permiso permiso, @RequestHeader("usuario-id") Integer idUsuario) {
        try {
            Usuario admin = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (!usuarioService.tienePermiso(admin, "gestionar_permisos")) {
                return ResponseEntity.status(403).body("No tienes permisos para gestionar permisos");
            }

            return ResponseEntity.ok(permisoRepository.save(permiso));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> eliminarPermiso(@PathVariable Integer id, @RequestHeader("usuario-id") Integer idUsuario) {
        try {
            Usuario admin = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (!usuarioService.tienePermiso(admin, "gestionar_permisos")) {
                return ResponseEntity.status(403).body("No tienes permisos para gestionar permisos");
            }

            permisoRepository.deleteById(id);
            return ResponseEntity.ok("Permiso eliminado correctamente");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}