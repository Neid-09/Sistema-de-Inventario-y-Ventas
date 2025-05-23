package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Promocion;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.PromocionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/promociones")
@CrossOrigin(origins = "*")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @GetMapping
    public List<Promocion> obtenerTodas() {
        return promocionService.obtenerTodas();
    }

    @GetMapping("/activas")
    public List<Promocion> obtenerPromocionesActivas() {
        return promocionService.obtenerPromocionesActivas();
    }

    @GetMapping("/producto/{idProducto}")
    public List<Promocion> obtenerPorProducto(@PathVariable Integer idProducto) {
        return promocionService.obtenerPorProducto(idProducto);
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> crear(@RequestBody Promocion promocion) {
        try {
            Promocion nuevaPromocion = promocionService.guardar(promocion);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPromocion);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("mensaje", "Error al crear la promoción: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (!promocionService.obtenerPorId(id).isPresent()) {
            return ResponseEntity.notFound().build();
        }
        promocionService.eliminar(id);
        return ResponseEntity.ok().build();
    }
}