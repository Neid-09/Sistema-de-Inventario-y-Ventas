package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Recompensa;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.RecompensaService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recompensas") // Ruta base para recompensas
public class RecompensaController {

    @Autowired
    private RecompensaService recompensaService;

    // Obtener todas las recompensas de un cliente específico
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<?> getRecompensasByClienteId(@PathVariable Integer idCliente) {
        try {
             // El servicio ya valida si el cliente existe
            List<Recompensa> recompensas = recompensaService.obtenerRecompensasPorCliente(idCliente);
            return ResponseEntity.ok(recompensas);
        } catch (EntityNotFoundException e) {
             // Si el servicio lanza EntityNotFoundException porque el cliente no existe
             return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al obtener las recompensas: " + e.getMessage());
        }
    }

    // Obtener una recompensa específica por su ID
    @GetMapping("/{idRecompensa}")
    public ResponseEntity<Recompensa> getRecompensaById(@PathVariable Integer idRecompensa) {
        return recompensaService.obtenerRecompensaPorId(idRecompensa)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // Canjear una nueva recompensa para un cliente
    // El ID del cliente viene en la URL, los detalles de la recompensa en el cuerpo
    @PostMapping("/cliente/{idCliente}/canjear")
    public ResponseEntity<?> canjearRecompensa(@PathVariable Integer idCliente, @RequestBody Recompensa recompensa) {
        try {
            Recompensa nuevaRecompensa = recompensaService.canjearRecompensa(idCliente, recompensa);
            return new ResponseEntity<>(nuevaRecompensa, HttpStatus.CREATED);
        } catch (EntityNotFoundException e) {
            // Cliente no encontrado
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalArgumentException e) {
            // Puntos insuficientes, datos inválidos, etc.
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al canjear la recompensa: " + e.getMessage());
        }
    }
}
