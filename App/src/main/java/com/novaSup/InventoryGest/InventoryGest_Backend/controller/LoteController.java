package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.LoteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/lotes")
public class LoteController {

    @Autowired
    private LoteService loteService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Lote>> listarLotes() {
        return ResponseEntity.ok(loteService.obtenerTodos());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<?> obtenerLote(@PathVariable Integer id) {
        return loteService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{idProducto}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Lote>> obtenerLotesPorProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(loteService.obtenerPorProducto(idProducto));
    }

    @GetMapping("/proximos-vencer")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Lote>> obtenerLotesProximosAVencer(@RequestParam(defaultValue = "30") Integer diasAlerta) {
        return ResponseEntity.ok(loteService.obtenerLotesProximosVencer(diasAlerta));
    }

    @GetMapping("/por-fecha-entrada")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Lote>> obtenerLotesPorRangoFechaEntrada(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaInicio,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") Date fechaFin) {
        return ResponseEntity.ok(loteService.obtenerLotesPorRangoFechaEntrada(fechaInicio, fechaFin));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('registrar_entrada_producto')")
    public ResponseEntity<?> crearLote(@RequestBody Lote lote) {
        try {
            // Verificar si el producto existe
            if (!productoService.obtenerPorId(lote.getIdProducto()).isPresent()) {
                Map<String, String> error = Map.of("mensaje", "El producto especificado no existe");
                return ResponseEntity.badRequest().body(error);
            }

            // El producto existe, continuar con la creación del lote
            productoService.obtenerPorId(lote.getIdProducto()).ifPresent(lote::setProducto);

            // Si no se proporciona fecha de entrada, usar la fecha actual
            if (lote.getFechaEntrada() == null) {
                lote.setFechaEntrada(new Date());
            }

            // Asegurarse que el lote esté activo por defecto
            if (lote.getActivo() == null) {
                lote.setActivo(true);
            }

            Lote nuevoLote = loteService.guardar(lote);

            // Ya no actualizamos manualmente el stock aquí, se calcula automáticamente desde los lotes

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoLote);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(
                    Map.of("mensaje", "Error al crear el lote: " + e.getMessage()));
        }
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> actualizarLote(@PathVariable Integer id, @RequestBody Lote lote) {
        return loteService.obtenerPorId(id)
                .map(loteExistente -> {
                    // Guardar el estado original para comparar después
                    boolean estadoOriginal = loteExistente.getActivo();
                    int cantidadOriginal = loteExistente.getCantidad();

                    lote.setIdLote(id);
                    // Conservar la fecha de entrada original si no se proporciona una nueva
                    if (lote.getFechaEntrada() == null) {
                        lote.setFechaEntrada(loteExistente.getFechaEntrada());
                    }

                    if (lote.getIdProducto() != null) {
                        productoService.obtenerPorId(lote.getIdProducto()).ifPresent(lote::setProducto);
                    } else {
                        lote.setProducto(loteExistente.getProducto());
                    }

                    Lote loteActualizado = loteService.guardar(lote);

                    // Ya no necesitamos actualizar manualmente el stock aquí
                    // El stock se calculará automáticamente al consultar el producto

                    return ResponseEntity.ok(loteActualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> eliminarLote(@PathVariable Integer id) {
        return loteService.obtenerPorId(id)
                .map(lote -> {
                    loteService.eliminar(id); // Ahora realiza eliminación lógica (desactivación)
                    // Ya no necesitamos actualizar manualmente el stock aquí
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}/activar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> activarLote(@PathVariable Integer id) {
        // Primero obtener el lote para verificar su fecha de vencimiento
        Optional<Lote> loteOpt = loteService.obtenerPorId(id);

        if (!loteOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Lote lote = loteOpt.get();
        Date fechaActual = new Date();

        // Verificar si el lote está vencido
        if (lote.getFechaVencimiento() != null && lote.getFechaVencimiento().before(fechaActual)) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body("No se puede activar un lote vencido. La fecha de vencimiento es " +
                            lote.getFechaVencimiento());
        }

        // Si no está vencido, proceder con la activación
        Optional<Lote> loteActivado = loteService.activarLote(id);

        return loteActivado
                .map(l -> ResponseEntity.ok(l))
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/desactivar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> desactivarLote(@PathVariable Integer id) {
        return loteService.desactivarLote(id)
                .map(lote -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("mensaje", "Lote desactivado correctamente");
                    response.put("lote", lote);
                    return ResponseEntity.ok(response);
                })
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Collections.singletonMap("error", "No se encontró un lote activo con ID: " + id)));
    }

    @GetMapping("/activos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Lote>> listarLotesActivos() {
        return ResponseEntity.ok(loteService.obtenerLotesActivos());
    }

    @GetMapping("/inactivos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<List<Lote>> listarLotesInactivos() {
        return ResponseEntity.ok(loteService.obtenerLotesInactivos());
    }

    @PatchMapping("/{id}/reducir")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> reducirCantidadLote(
            @PathVariable Integer id,
            @RequestParam Integer cantidad) {
        try {
            Lote lote = loteService.reducirCantidadLote(id, cantidad);
            return ResponseEntity.ok(lote);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    @PatchMapping("/producto/{idProducto}/reducir")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> reducirCantidadProductoLotes(
            @PathVariable Integer idProducto,
            @RequestParam Integer cantidad) {
        try {
            loteService.reducirCantidadDeLotes(idProducto, cantidad);
            return ResponseEntity.ok(Collections.singletonMap("mensaje", "Cantidad reducida correctamente"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", e.getMessage()));
        }
    }

    // Nuevo endpoint para procesar devoluciones
    @PatchMapping("/{id}/devolucion")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('registrar_entrada_producto')")
    public ResponseEntity<?> procesarDevolucion(
            @PathVariable Integer id,
            @RequestParam Integer cantidad) {
        try {
            if (cantidad <= 0) {
                return ResponseEntity.badRequest().body(
                        Map.of("mensaje", "La cantidad a devolver debe ser mayor a cero"));
            }

            Lote loteActualizado = loteService.procesarDevolucion(id, cantidad);

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Devolución procesada correctamente",
                    "lote", loteActualizado
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}