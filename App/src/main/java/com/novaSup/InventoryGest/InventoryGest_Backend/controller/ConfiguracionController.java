package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Configuracion;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.ConfiguracionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/configuraciones")
public class ConfiguracionController {

    @Autowired
    private ConfiguracionService configuracionService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_configuracion')")
    public ResponseEntity<?> listarConfiguraciones() {
        List<Configuracion> configuraciones = configuracionService.listarConfiguraciones();
        return ResponseEntity.ok(configuraciones);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_configuracion')")
    public ResponseEntity<?> obtenerConfiguracion(@PathVariable Integer id) {
        Optional<Configuracion> configuracion = configuracionService.obtenerConfiguracionPorId(id);
        return configuracion
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/clave/{clave}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_configuracion')")
    public ResponseEntity<?> obtenerConfiguracionPorClave(@PathVariable String clave) {
        Optional<Configuracion> configuracion = configuracionService.obtenerConfiguracionPorClave(clave);
        return configuracion
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_configuracion')")
    public ResponseEntity<?> crearConfiguracion(@RequestBody Configuracion configuracion) {
        try {
            Configuracion nuevaConfiguracion = configuracionService.guardarConfiguracion(configuracion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaConfiguracion);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al crear la configuración: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_configuracion')")
    public ResponseEntity<?> actualizarConfiguracion(
            @PathVariable Integer id,
            @RequestBody Configuracion configuracion) {
        if (!configuracionService.obtenerConfiguracionPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        configuracion.setIdConfig(id);
        Configuracion configuracionActualizada = configuracionService.guardarConfiguracion(configuracion);
        return ResponseEntity.ok(configuracionActualizada);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_configuracion')")
    public ResponseEntity<?> eliminarConfiguracion(@PathVariable Integer id) {
        if (!configuracionService.obtenerConfiguracionPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }

        configuracionService.eliminarConfiguracion(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/buscar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('gestionar_configuracion')")
    public ResponseEntity<?> buscarConfiguraciones(@RequestParam String clave) {
        List<Configuracion> configuraciones = configuracionService.buscarConfiguracionesPorClave(clave);
        return ResponseEntity.ok(configuraciones);
    }
}