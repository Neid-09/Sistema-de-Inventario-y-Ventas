package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.TipoImpuesto;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.AuditoriaService;
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
@RequestMapping("/api/tipos-impuestos")
public class TipoImpuestoController {

    private final TipoImpuestoService tipoImpuestoService;
    private final AuditoriaService auditoriaService;

    public TipoImpuestoController(TipoImpuestoService tipoImpuestoService, AuditoriaService auditoriaService) {
        this.tipoImpuestoService = tipoImpuestoService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Obtiene todos los tipos de impuestos.
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_impuestos')")
    public ResponseEntity<List<TipoImpuesto>> listarTiposImpuestos() {
        List<TipoImpuesto> tiposImpuestos = tipoImpuestoService.findAll();
        return ResponseEntity.ok(tiposImpuestos);
    }

    /**
     * Obtiene un tipo de impuesto por su ID.
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_impuestos')")
    public ResponseEntity<?> obtenerTipoImpuesto(@PathVariable Integer id) {
        Optional<TipoImpuesto> tipoImpuesto = tipoImpuestoService.findById(id);
        if (tipoImpuesto.isPresent()) {
            return ResponseEntity.ok(tipoImpuesto.get());
        } else {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Tipo de impuesto no encontrado con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    /**
     * Crea un nuevo tipo de impuesto.
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> crearTipoImpuesto(@RequestBody TipoImpuesto tipoImpuesto) {
        try {
            TipoImpuesto nuevoTipoImpuesto = tipoImpuestoService.save(tipoImpuesto);
            
            // Registrar auditoría
            auditoriaService.registrarAccion(
                "CREAR",
                "tipos_impuestos",
                nuevoTipoImpuesto.getIdTipoImpuesto(),
                null,
                nuevoTipoImpuesto,
                null
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoTipoImpuesto);
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Error al crear el tipo de impuesto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    /**
     * Actualiza un tipo de impuesto existente.
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> actualizarTipoImpuesto(@PathVariable Integer id, @RequestBody TipoImpuesto tipoImpuesto) {
        try {
            Optional<TipoImpuesto> tipoImpuestoExistente = tipoImpuestoService.findById(id);
            
            if (!tipoImpuestoExistente.isPresent()) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Tipo de impuesto no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
            }
            
            // Guardar estado anterior para auditoría
            TipoImpuesto tipoImpuestoAnterior = tipoImpuestoExistente.get();
            
            // Actualizar el objeto
            tipoImpuesto.setIdTipoImpuesto(id);
            TipoImpuesto tipoImpuestoActualizado = tipoImpuestoService.save(tipoImpuesto);
            
            // Registrar auditoría
            auditoriaService.registrarAccion(
                "ACTUALIZAR",
                "tipos_impuestos",
                id,
                tipoImpuestoAnterior,
                tipoImpuestoActualizado,
                null
            );
            
            return ResponseEntity.ok(tipoImpuestoActualizado);
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Error al actualizar el tipo de impuesto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    /**
     * Cambia el estado activo/inactivo de un tipo de impuesto.
     */
    @PatchMapping("/{id}/cambiar-estado")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> cambiarEstado(@PathVariable Integer id) {
        try {
            Optional<TipoImpuesto> tipoImpuestoOpt = tipoImpuestoService.findById(id);
            
            if (!tipoImpuestoOpt.isPresent()) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Tipo de impuesto no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
            }
            
            TipoImpuesto tipoImpuesto = tipoImpuestoOpt.get();
            TipoImpuesto tipoImpuestoAnterior = new TipoImpuesto();
            tipoImpuestoAnterior.setIdTipoImpuesto(tipoImpuesto.getIdTipoImpuesto());
            tipoImpuestoAnterior.setActivo(tipoImpuesto.getActivo());
            
            // Cambiar el estado
            tipoImpuesto.setActivo(!tipoImpuesto.getActivo());
            TipoImpuesto tipoImpuestoActualizado = tipoImpuestoService.save(tipoImpuesto);
            
            // Registrar auditoría
            auditoriaService.registrarAccion(
                "ACTUALIZAR",
                "tipos_impuestos",
                id,
                tipoImpuestoAnterior,
                tipoImpuestoActualizado,
                null
            );
            
            return ResponseEntity.ok(tipoImpuestoActualizado);
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Error al cambiar el estado del tipo de impuesto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }
} 