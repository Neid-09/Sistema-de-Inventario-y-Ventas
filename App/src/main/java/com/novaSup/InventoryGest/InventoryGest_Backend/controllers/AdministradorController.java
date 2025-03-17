package com.novaSup.InventoryGest.InventoryGest_Backend.controllers;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Administrador;
import com.novaSup.InventoryGest.InventoryGest_Backend.services.AdministradorService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/administradores")
public class AdministradorController {
    private final AdministradorService administradorService;

    public AdministradorController(AdministradorService administradorService) {
        this.administradorService = administradorService;
    }

    @GetMapping
    public List<Administrador> obtenerTodos() {
        return administradorService.obtenerTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Administrador> obtenerPorId(@PathVariable int id) {
        Optional<Administrador> administrador = administradorService.obtenerPorId(id);
        return administrador.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    public Administrador guardar(@RequestBody Administrador administrador) {
        return administradorService.guardar(administrador);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable int id) {
        if (administradorService.obtenerPorId(id).isPresent()) {
            administradorService.eliminar(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}
