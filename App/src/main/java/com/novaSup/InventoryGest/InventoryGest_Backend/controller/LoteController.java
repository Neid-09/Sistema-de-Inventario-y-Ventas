package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.LoteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
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
            Lote nuevoLote = loteService.guardar(lote);

            // Actualizar el stock del producto
            productoService.actualizarStock(lote.getIdProducto(),
                    lote.getProducto().getStock() + lote.getCantidad());

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
                    // Calcular diferencia de cantidad para actualizar stock del producto
                    int diferenciaCantidad = lote.getCantidad() - loteExistente.getCantidad();

                    lote.setIdLote(id);
                    if (lote.getIdProducto() != null) {
                        productoService.obtenerPorId(lote.getIdProducto()).ifPresent(lote::setProducto);
                    }

                    Lote loteActualizado = loteService.guardar(lote);

                    // Actualizar stock del producto si cambió la cantidad
                    if (diferenciaCantidad != 0) {
                        productoService.actualizarStock(loteActualizado.getProducto().getIdProducto(),
                                loteActualizado.getProducto().getStock() + diferenciaCantidad);
                    }

                    return ResponseEntity.ok(loteActualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> eliminarLote(@PathVariable Integer id) {
        return loteService.obtenerPorId(id)
                .map(lote -> {
                    // Restar del stock antes de "eliminar" (desactivar)
                    productoService.actualizarStock(lote.getProducto().getIdProducto(),
                            lote.getProducto().getStock() - lote.getCantidad());

                    loteService.eliminar(id); // Ahora realiza eliminación lógica
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{id}/activar")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> activarLote(@PathVariable Integer id) {
        return loteService.activarLote(id)
                .map(lote -> {
                    // Incrementar el stock del producto al reactivar el lote
                    productoService.actualizarStock(lote.getProducto().getIdProducto(),
                            lote.getProducto().getStock() + lote.getCantidad());

                    return ResponseEntity.ok().body(
                            Map.of("mensaje", "Lote activado correctamente", "lote", lote)
                    );
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/inactivos")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<List<Lote>> listarLotesInactivos() {
        return ResponseEntity.ok(loteService.obtenerLotesInactivos());
    }
}