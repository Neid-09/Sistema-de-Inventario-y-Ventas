package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.RegistMovimient;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.RegistMovimientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Controlador para consultar registros de movimientos (entradas y salidas).
 * Las operaciones que afectan al inventario (compras, ventas, ajustes) 
 * se manejan en InventarioController.
 */
@RestController
@RequestMapping("/api/movimientos")
@CrossOrigin("*")
public class RegistMovimientController {

    private final RegistMovimientService registMovimientService;

    @Autowired
    public RegistMovimientController(RegistMovimientService registMovimientService) {
        this.registMovimientService = registMovimientService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_entradas_productos')")
    public ResponseEntity<List<RegistMovimient>> obtenerTodosLosMovimientos() {
        return ResponseEntity.ok(registMovimientService.obtenerTodas());
    }

    @GetMapping("/producto/{idProducto}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_entradas_productos')")
    public ResponseEntity<List<RegistMovimient>> obtenerPorProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(registMovimientService.obtenerPorProducto(idProducto));
    }

    @GetMapping("/fecha")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_entradas_productos')")
    public ResponseEntity<List<RegistMovimient>> obtenerPorFecha(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<RegistMovimient> movimientos = registMovimientService.obtenerTodas();

        if (desde != null) {
            LocalDateTime fechaDesde = desde.atStartOfDay();
            movimientos = movimientos.stream()
                    .filter(m -> m.getFecha().isEqual(fechaDesde) || m.getFecha().isAfter(fechaDesde))
                    .collect(Collectors.toList());
        }

        if (hasta != null) {
            LocalDateTime fechaHasta = hasta.plusDays(1).atStartOfDay();
            movimientos = movimientos.stream()
                    .filter(m -> m.getFecha().isBefore(fechaHasta))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(movimientos);
    }

    @GetMapping("/tipo/{tipo}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_entradas_productos')")
    public ResponseEntity<List<RegistMovimient>> obtenerPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(registMovimientService.obtenerPorTipoMovimiento(tipo));
    }

    @GetMapping("/proveedor/{idProveedor}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_entradas_productos')")
    public ResponseEntity<List<RegistMovimient>> obtenerPorProveedor(@PathVariable Integer idProveedor) {
        return ResponseEntity.ok(registMovimientService.obtenerPorProveedor(idProveedor));
    }
    
    @GetMapping("/filtro")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_entradas_productos')")
    public ResponseEntity<List<RegistMovimient>> obtenerFiltrados(
            @RequestParam(required = false) Integer idProducto,
            @RequestParam(required = false) Integer idProveedor,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String tipo) {

        List<RegistMovimient> movimientos = registMovimientService.obtenerTodas();

        if (idProducto != null) {
            movimientos = movimientos.stream()
                    .filter(m -> m.getProducto().getIdProducto().equals(idProducto))
                    .collect(Collectors.toList());
        }

        if (idProveedor != null) {
            movimientos = movimientos.stream()
                    .filter(m -> m.getProveedor() != null && m.getProveedor().getIdProveedor().equals(idProveedor))
                    .collect(Collectors.toList());
        }

        if (desde != null) {
            LocalDateTime fechaDesde = desde.atStartOfDay();
            movimientos = movimientos.stream()
                    .filter(m -> m.getFecha().isEqual(fechaDesde) || m.getFecha().isAfter(fechaDesde))
                    .collect(Collectors.toList());
        }

        if (hasta != null) {
            LocalDateTime fechaHasta = hasta.plusDays(1).atStartOfDay();
            movimientos = movimientos.stream()
                    .filter(m -> m.getFecha().isBefore(fechaHasta))
                    .collect(Collectors.toList());
        }

        if (tipo != null && !tipo.isEmpty()) {
            movimientos = movimientos.stream()
                    .filter(m -> m.getTipoMovimiento().equalsIgnoreCase(tipo))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(movimientos);
    }
}
