package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Categoria;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.ImpuestoAplicable;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.TasaImpuesto;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/impuestos-aplicables")
public class ImpuestoAplicableController {

    private final ImpuestoAplicableService impuestoAplicableService;
    private final TasaImpuestoService tasaImpuestoService;
    private final ProductoService productoService;
    private final CategoriaService categoriaService;
    private final AuditoriaService auditoriaService;

    public ImpuestoAplicableController(
            ImpuestoAplicableService impuestoAplicableService,
            TasaImpuestoService tasaImpuestoService,
            ProductoService productoService,
            CategoriaService categoriaService,
            AuditoriaService auditoriaService) {
        this.impuestoAplicableService = impuestoAplicableService;
        this.tasaImpuestoService = tasaImpuestoService;
        this.productoService = productoService;
        this.categoriaService = categoriaService;
        this.auditoriaService = auditoriaService;
    }

    /**
     * Obtiene todos los impuestos aplicables
     */
    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_impuestos')")
    public ResponseEntity<List<ImpuestoAplicable>> listarImpuestosAplicables() {
        // Este método debería obtener todos desde el repositorio
        // Como no está en la interfaz actual, se puede extender
        List<ImpuestoAplicable> impuestos = impuestoAplicableService.findAll();
        return ResponseEntity.ok(impuestos);
    }

    /**
     * Obtiene un impuesto aplicable por su ID
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_impuestos')")
    public ResponseEntity<?> obtenerImpuestoAplicable(@PathVariable Integer id) {
        Optional<ImpuestoAplicable> impuesto = impuestoAplicableService.findById(id);
        if (impuesto.isPresent()) {
            return ResponseEntity.ok(impuesto.get());
        } else {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Impuesto aplicable no encontrado con ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
    }

    /**
     * Obtiene impuestos aplicables a un producto en una fecha determinada
     */
    @GetMapping("/por-producto/{idProducto}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_impuestos')")
    public ResponseEntity<?> obtenerImpuestosPorProducto(
            @PathVariable Integer idProducto,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fecha) {
        
        if (fecha == null) {
            fecha = new Date(); // Si no se proporciona fecha, usar la actual
        }
        
        Producto producto = productoService.obtenerPorId(idProducto).orElse(null);
        if (producto == null) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Producto no encontrado con ID: " + idProducto);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        
        Categoria categoria = producto.getCategoria();
        Integer idCategoria = categoria != null ? categoria.getIdCategoria() : null;
        
        List<ImpuestoAplicable> impuestos = impuestoAplicableService.obtenerImpuestosAplicables(idProducto, idCategoria, fecha);
        return ResponseEntity.ok(impuestos);
    }

    /**
     * Obtiene impuestos aplicables a una categoría en una fecha determinada
     */
    @GetMapping("/por-categoria/{idCategoria}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_impuestos')")
    public ResponseEntity<?> obtenerImpuestosPorCategoria(
            @PathVariable Integer idCategoria,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) Date fecha) {
        
        if (fecha == null) {
            fecha = new Date(); // Si no se proporciona fecha, usar la actual
        }
        
        if (!categoriaService.obtenerPorId(idCategoria).isPresent()) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Categoría no encontrada con ID: " + idCategoria);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
        }
        
        List<ImpuestoAplicable> impuestos = impuestoAplicableService.obtenerImpuestosAplicables(null, idCategoria, fecha);
        return ResponseEntity.ok(impuestos);
    }

    /**
     * Crea un nuevo impuesto aplicable
     */
    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> crearImpuestoAplicable(@RequestBody ImpuestoAplicable impuestoAplicable) {
        try {
            // Validaciones
            if (impuestoAplicable.getTasaImpuesto() == null || impuestoAplicable.getTasaImpuesto().getIdTasa() == null) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Se requiere una tasa de impuesto válida");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            // Validar tasa de impuesto
            Optional<TasaImpuesto> tasaImpuestoOpt = tasaImpuestoService.findById(impuestoAplicable.getTasaImpuesto().getIdTasa());
            if (!tasaImpuestoOpt.isPresent()) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Tasa de impuesto no encontrada con ID: " + impuestoAplicable.getTasaImpuesto().getIdTasa());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            // Verificar que se proporciona producto o categoría, pero no ambos
            if ((impuestoAplicable.getProducto() == null || impuestoAplicable.getProducto().getIdProducto() == null) &&
                (impuestoAplicable.getCategoria() == null || impuestoAplicable.getCategoria().getIdCategoria() == null)) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Se debe proporcionar un producto o una categoría");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            if (impuestoAplicable.getProducto() != null && impuestoAplicable.getProducto().getIdProducto() != null &&
                impuestoAplicable.getCategoria() != null && impuestoAplicable.getCategoria().getIdCategoria() != null) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Un impuesto debe aplicarse a un producto O a una categoría, no a ambos");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            // Validar producto si se proporciona
            if (impuestoAplicable.getProducto() != null && impuestoAplicable.getProducto().getIdProducto() != null) {
                Optional<Producto> productoOpt = productoService.obtenerPorId(impuestoAplicable.getProducto().getIdProducto());
                if (!productoOpt.isPresent()) {
                    Map<String, String> respuesta = new HashMap<>();
                    respuesta.put("mensaje", "Producto no encontrado con ID: " + impuestoAplicable.getProducto().getIdProducto());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
                }
                impuestoAplicable.setProducto(productoOpt.get());
                impuestoAplicable.setCategoria(null);
            }

            // Validar categoría si se proporciona
            if (impuestoAplicable.getCategoria() != null && impuestoAplicable.getCategoria().getIdCategoria() != null) {
                Optional<Categoria> categoriaOpt = categoriaService.obtenerPorId(impuestoAplicable.getCategoria().getIdCategoria());
                if (!categoriaOpt.isPresent()) {
                    Map<String, String> respuesta = new HashMap<>();
                    respuesta.put("mensaje", "Categoría no encontrada con ID: " + impuestoAplicable.getCategoria().getIdCategoria());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
                }
                impuestoAplicable.setCategoria(categoriaOpt.get());
                impuestoAplicable.setProducto(null);
            }

            // Asignar la tasa de impuesto completa
            impuestoAplicable.setTasaImpuesto(tasaImpuestoOpt.get());
            
            if (impuestoAplicable.getAplica() == null) {
                impuestoAplicable.setAplica(true); // Por defecto, el impuesto aplica
            }
            
            if (impuestoAplicable.getFechaInicio() == null) {
                impuestoAplicable.setFechaInicio(new Date()); // Por defecto, inicia hoy
            }
            
            ImpuestoAplicable nuevoImpuestoAplicable = impuestoAplicableService.save(impuestoAplicable);
            
            // Registrar auditoría
            auditoriaService.registrarAccion(
                "CREAR",
                "impuestos_aplicables",
                nuevoImpuestoAplicable.getIdImpuestoAplicable(),
                null,
                nuevoImpuestoAplicable,
                null
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoImpuestoAplicable);
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Error al crear el impuesto aplicable: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    /**
     * Actualiza un impuesto aplicable existente
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> actualizarImpuestoAplicable(@PathVariable Integer id, @RequestBody ImpuestoAplicable impuestoAplicable) {
        try {
            Optional<ImpuestoAplicable> impuestoExistente = impuestoAplicableService.findById(id);
            
            if (!impuestoExistente.isPresent()) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Impuesto aplicable no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
            }
            
            // Mismas validaciones que para crear
            if (impuestoAplicable.getTasaImpuesto() == null || impuestoAplicable.getTasaImpuesto().getIdTasa() == null) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Se requiere una tasa de impuesto válida");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            Optional<TasaImpuesto> tasaImpuestoOpt = tasaImpuestoService.findById(impuestoAplicable.getTasaImpuesto().getIdTasa());
            if (!tasaImpuestoOpt.isPresent()) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Tasa de impuesto no encontrada con ID: " + impuestoAplicable.getTasaImpuesto().getIdTasa());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            if ((impuestoAplicable.getProducto() == null || impuestoAplicable.getProducto().getIdProducto() == null) &&
                (impuestoAplicable.getCategoria() == null || impuestoAplicable.getCategoria().getIdCategoria() == null)) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Se debe proporcionar un producto o una categoría");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            if (impuestoAplicable.getProducto() != null && impuestoAplicable.getProducto().getIdProducto() != null &&
                impuestoAplicable.getCategoria() != null && impuestoAplicable.getCategoria().getIdCategoria() != null) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Un impuesto debe aplicarse a un producto O a una categoría, no a ambos");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
            }

            if (impuestoAplicable.getProducto() != null && impuestoAplicable.getProducto().getIdProducto() != null) {
                Optional<Producto> productoOpt = productoService.obtenerPorId(impuestoAplicable.getProducto().getIdProducto());
                if (!productoOpt.isPresent()) {
                    Map<String, String> respuesta = new HashMap<>();
                    respuesta.put("mensaje", "Producto no encontrado con ID: " + impuestoAplicable.getProducto().getIdProducto());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
                }
                impuestoAplicable.setProducto(productoOpt.get());
                impuestoAplicable.setCategoria(null);
            }

            if (impuestoAplicable.getCategoria() != null && impuestoAplicable.getCategoria().getIdCategoria() != null) {
                Optional<Categoria> categoriaOpt = categoriaService.obtenerPorId(impuestoAplicable.getCategoria().getIdCategoria());
                if (!categoriaOpt.isPresent()) {
                    Map<String, String> respuesta = new HashMap<>();
                    respuesta.put("mensaje", "Categoría no encontrada con ID: " + impuestoAplicable.getCategoria().getIdCategoria());
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
                }
                impuestoAplicable.setCategoria(categoriaOpt.get());
                impuestoAplicable.setProducto(null);
            }
            
            // Guardar estado anterior para auditoría
            ImpuestoAplicable impuestoAnterior = impuestoExistente.get();
            
            // Actualizar el objeto
            impuestoAplicable.setIdImpuestoAplicable(id);
            impuestoAplicable.setTasaImpuesto(tasaImpuestoOpt.get());
            
            ImpuestoAplicable impuestoActualizado = impuestoAplicableService.save(impuestoAplicable);
            
            // Registrar auditoría
            auditoriaService.registrarAccion(
                "ACTUALIZAR",
                "impuestos_aplicables",
                id,
                impuestoAnterior,
                impuestoActualizado,
                null
            );
            
            return ResponseEntity.ok(impuestoActualizado);
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Error al actualizar el impuesto aplicable: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }

    /**
     * Cambia el estado de aplicabilidad de un impuesto
     */
    @PatchMapping("/{id}/cambiar-aplicabilidad")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> cambiarAplicabilidad(@PathVariable Integer id) {
        try {
            Optional<ImpuestoAplicable> impuestoOpt = impuestoAplicableService.findById(id);
            
            if (!impuestoOpt.isPresent()) {
                Map<String, String> respuesta = new HashMap<>();
                respuesta.put("mensaje", "Impuesto aplicable no encontrado con ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuesta);
            }
            
            ImpuestoAplicable impuesto = impuestoOpt.get();
            ImpuestoAplicable impuestoAnterior = new ImpuestoAplicable();
            impuestoAnterior.setIdImpuestoAplicable(impuesto.getIdImpuestoAplicable());
            impuestoAnterior.setAplica(impuesto.getAplica());
            
            // Cambiar el estado
            impuesto.setAplica(!impuesto.getAplica());
            ImpuestoAplicable impuestoActualizado = impuestoAplicableService.save(impuesto);
            
            // Registrar auditoría
            auditoriaService.registrarAccion(
                "ACTUALIZAR",
                "impuestos_aplicables",
                id,
                impuestoAnterior,
                impuestoActualizado,
                null
            );
            
            return ResponseEntity.ok(impuestoActualizado);
        } catch (Exception e) {
            Map<String, String> respuesta = new HashMap<>();
            respuesta.put("mensaje", "Error al cambiar la aplicabilidad del impuesto: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuesta);
        }
    }
} 