package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.TasaImpuesto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.TipoImpuesto;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.AuditoriaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.TasaImpuestoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.TipoImpuestoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/tasas-impuestos")
public class TasaImpuestoController {

    private final TasaImpuestoService tasaImpuestoService;
    private final TipoImpuestoService tipoImpuestoService;
    private final AuditoriaService auditoriaService;

    public TasaImpuestoController(
            TasaImpuestoService tasaImpuestoService,
            TipoImpuestoService tipoImpuestoService,
            AuditoriaService auditoriaService) {
        this.tasaImpuestoService = tasaImpuestoService;
        this.tipoImpuestoService = tipoImpuestoService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Obtiene todas las tasas de impuestos.
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_impuestos')")
    public ResponseEntity<List<TasaImpuesto>> listarTasasImpuestos() {
        List<TasaImpuesto> tasasImpuestos = tasaImpuestoService.findAll();
        return ResponseEntity.ok(tasasImpuestos);
    }

    /**
     * Obtiene una tasa de impuesto por su ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_impuestos')")
    public ResponseEntity<?> obtenerTasaImpuesto(@PathVariable Integer id) {
        Optional<TasaImpuesto> tasaImpuesto = tasaImpuestoService.findById(id);
        if (tasaImpuesto.isPresent()) {
            return ResponseEntity.ok(tasaImpuesto.get());
        } else {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Tasa de impuesto no encontrada con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    /**
     * Crea una nueva tasa de impuesto.
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> crearTasaImpuesto(@RequestBody TasaImpuesto tasaImpuesto) {
        try {
            // Verificar que el tipo de impuesto referenciado existe
            if (tasaImpuesto.getTipoImpuesto() == null || tasaImpuesto.getTipoImpuesto().getIdTipoImpuesto() == null) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Se requiere un tipo de impuesto válido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            Optional<TipoImpuesto> tipoImpuestoOpt = tipoImpuestoService.findById(tasaImpuesto.getTipoImpuesto().getIdTipoImpuesto());
            if (!tipoImpuestoOpt.isPresent()) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Tipo de impuesto no encontrado con ID: " + tasaImpuesto.getTipoImpuesto().getIdTipoImpuesto());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            // Asignar el tipo de impuesto completo
            tasaImpuesto.setTipoImpuesto(tipoImpuestoOpt.get());
            
            TasaImpuesto nuevaTasaImpuesto = tasaImpuestoService.save(tasaImpuesto);
            
            // Registrar auditoría
            auditoriaService.registrarAccion(
                "CREAR",
                "tasas_impuestos",
                nuevaTasaImpuesto.getIdTasa(),
                null,
                nuevaTasaImpuesto,
                null
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaTasaImpuesto);
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Error al crear la tasa de impuesto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    /**
     * Actualiza una tasa de impuesto existente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> actualizarTasaImpuesto(@PathVariable Integer id, @RequestBody TasaImpuesto tasaImpuesto) {
        try {
            Optional<TasaImpuesto> tasaImpuestoExistente = tasaImpuestoService.findById(id);
            
            if (!tasaImpuestoExistente.isPresent()) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Tasa de impuesto no encontrada con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
            }
            
            // Verificar que el tipo de impuesto referenciado existe
            if (tasaImpuesto.getTipoImpuesto() == null || tasaImpuesto.getTipoImpuesto().getIdTipoImpuesto() == null) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Se requiere un tipo de impuesto válido");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            Optional<TipoImpuesto> tipoImpuestoOpt = tipoImpuestoService.findById(tasaImpuesto.getTipoImpuesto().getIdTipoImpuesto());
            if (!tipoImpuestoOpt.isPresent()) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Tipo de impuesto no encontrado con ID: " + tasaImpuesto.getTipoImpuesto().getIdTipoImpuesto());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }
            
            // Guardar estado anterior para auditoría
            TasaImpuesto tasaImpuestoAnterior = tasaImpuestoExistente.get();
            
            // Actualizar el objeto
            tasaImpuesto.setIdTasa(id);
            tasaImpuesto.setTipoImpuesto(tipoImpuestoOpt.get());
            TasaImpuesto tasaImpuestoActualizada = tasaImpuestoService.save(tasaImpuesto);
            
            // Registrar auditoría
            auditoriaService.registrarAccion(
                "ACTUALIZAR",
                "tasas_impuestos",
                id,
                tasaImpuestoAnterior,
                tasaImpuestoActualizada,
                null
            );
            
            return ResponseEntity.ok(tasaImpuestoActualizada);
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Error al actualizar la tasa de impuesto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    /**
     * Obtiene las tasas de impuesto por tipo de impuesto.
     */
    @GetMapping("/por-tipo/{idTipoImpuesto}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_impuestos')")
    public ResponseEntity<?> obtenerTasasPorTipoImpuesto(@PathVariable Integer idTipoImpuesto) {
        // Verificar que el tipo de impuesto existe
        Optional<TipoImpuesto> tipoImpuestoOpt = tipoImpuestoService.findById(idTipoImpuesto);
        if (!tipoImpuestoOpt.isPresent()) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Tipo de impuesto no encontrado con ID: " + idTipoImpuesto);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        
        // Aquí debería haber un método en el servicio para filtrar tasas por tipo de impuesto
        // Por ahora, obtener todas y filtrar manualmente
        List<TasaImpuesto> todasLasTasas = tasaImpuestoService.findAll();
        List<TasaImpuesto> tasasFiltradas = todasLasTasas.stream()
            .filter(tasa -> tasa.getTipoImpuesto() != null && 
                   idTipoImpuesto.equals(tasa.getTipoImpuesto().getIdTipoImpuesto()))
            .toList();
        
        return ResponseEntity.ok(tasasFiltradas);
    }
} 