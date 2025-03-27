package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Permiso;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Rol;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.PermisoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.RolRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.impl.UsuarioServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
    private UsuarioServiceImpl usuarioService;

    @GetMapping
    public List<Rol> getRoles() {
        return rolRepository.findAll();
    }

    @PostMapping
    public ResponseEntity<Rol> crearRol(@RequestBody Rol rol) {
        return ResponseEntity.ok(rolRepository.save(rol));
    }

    @PutMapping("/{rolId}/permisos")
    public ResponseEntity<?> asignarPermisos(
            @PathVariable Integer rolId,
            @RequestBody List<Integer> permisosIds,
            @RequestHeader("usuario-id") Integer idUsuario) {

        try {
            Usuario admin = usuarioService.obtenerUsuarioPorId(idUsuario);
            if (!usuarioService.tienePermiso(admin, "gestionar_roles")) {
                return ResponseEntity.status(403).body("No tienes permisos para gestionar roles");
            }

            Rol rol = rolRepository.findById(rolId)
                    .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

            Set<Permiso> permisos = new HashSet<>();
            for (Integer permisoId : permisosIds) {
                Permiso permiso = permisoRepository.findById(permisoId)
                        .orElseThrow(() -> new RuntimeException("Permiso no encontrado: " + permisoId));
                permisos.add(permiso);
            }

            rol.setPermisos(permisos);
            return ResponseEntity.ok(rolRepository.save(rol));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}