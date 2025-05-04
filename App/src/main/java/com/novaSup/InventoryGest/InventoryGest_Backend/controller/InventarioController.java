package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaStock;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.RegistMovimient;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.InventarioService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProveedorService;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

/**
 * Controlador para operaciones de inventario que afectan tanto a lotes como a movimientos.
 * Este controlador coordina operaciones complejas como compras, ventas, ajustes y devoluciones.
 */
@RestController
@RequestMapping("/api/inventario")
@CrossOrigin("*")
public class InventarioController {

    private final InventarioService inventarioService;
    private final ProductoService productoService;

    public InventarioController(
            InventarioService inventarioService,
            ProductoService productoService,
            ProveedorService proveedorService) {
        this.inventarioService = inventarioService;
        this.productoService = productoService;
    }

    /**
     * Registra una compra de producto (entrada)
     */
    @PostMapping("/compra")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('registrar_entrada_producto')")
    public ResponseEntity<?> registrarCompra(@RequestBody CompraDTO compraDTO) {
        try {
            // Validar que el producto exista
            Producto producto = productoService.obtenerPorId(compraDTO.idProducto)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
            
            // Asignar el idProveedor al producto si se proporciona en el DTO
            if (compraDTO.idProveedor != null) {
                producto.setIdProveedor(compraDTO.idProveedor);
            }

            // Registrar la compra
            RegistMovimient movimiento = inventarioService.registrarCompraProducto(
                    producto,
                    compraDTO.cantidad,
                    compraDTO.precioUnitario != null ? compraDTO.precioUnitario : producto.getPrecioCosto(),
                    compraDTO.numeroLote,
                    compraDTO.fechaVencimiento,
                    compraDTO.motivo
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar compra: " + e.getMessage()));
        }
    }

    /**
     * Registra una venta de producto (salida)
     */
    @PostMapping("/venta")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('registrar_salida_producto')")
    public ResponseEntity<?> registrarVenta(@RequestBody VentaDTO ventaDTO) {
        try {
            // Validar que el producto exista
            Producto producto = productoService.obtenerPorId(ventaDTO.idProducto)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            // Registrar la venta
            RegistMovimient movimiento = inventarioService.registrarVentaProducto(
                    producto,
                    ventaDTO.cantidad,
                    ventaDTO.precioUnitario != null ? ventaDTO.precioUnitario : producto.getPrecioVenta(),
                    ventaDTO.motivo
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar venta: " + e.getMessage()));
        }
    }

    /**
     * Registra un ajuste de inventario
     */
    @PostMapping("/ajuste")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR')")
    public ResponseEntity<?> registrarAjuste(@RequestBody AjusteDTO ajusteDTO) {
        try {
            // Validar que el producto exista
            Producto producto = productoService.obtenerPorId(ajusteDTO.idProducto)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            // Registrar el ajuste
            RegistMovimient movimiento = inventarioService.registrarAjusteInventario(
                    producto,
                    ajusteDTO.cantidad,
                    ajusteDTO.motivo
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(movimiento);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar ajuste: " + e.getMessage()));
        }
    }

    /**
     * Registra una auditoría de stock (CASO 3)
     * Se usa cuando se encuentra una diferencia entre el inventario físico y el sistema
     */
    @PostMapping("/auditoria")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('auditar_stock')")
    public ResponseEntity<?> registrarAuditoriaStock(@RequestBody AuditoriaDTO auditoriaDTO) {
        try {
            // Validar que el producto exista
            Producto producto = productoService.obtenerPorId(auditoriaDTO.idProducto)
                    .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

            // Validar datos de entrada
            if (auditoriaDTO.stockReal == null) {
                return ResponseEntity.badRequest().body(Map.of("error", "El stock real es obligatorio"));
            }

            // Registrar la auditoría
            AuditoriaStock auditoria = inventarioService.registrarAuditoriaStock(
                    producto,
                    auditoriaDTO.stockReal,
                    auditoriaDTO.motivo,
                    auditoriaDTO.idUsuario
            );

            // Preparar respuesta con información detallada
            Map<String, Object> respuesta = new HashMap<>();
            respuesta.put("auditoria", auditoria);
            respuesta.put("mensaje", "Se ha actualizado el stock del producto debido a la diferencia encontrada");
            respuesta.put("stockAnterior", auditoria.getStockEsperado());
            respuesta.put("stockNuevo", auditoria.getStockReal());
            respuesta.put("diferencia", auditoria.getStockReal() - auditoria.getStockEsperado());

            return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar auditoría: " + e.getMessage()));
        }
    }

    /**
     * Registra una devolución a un lote
     */
    @PostMapping("/devolucion")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('registrar_entrada_producto')")
    public ResponseEntity<?> registrarDevolucion(@RequestBody DevolucionDTO devolucionDTO) {
        try {
            // Validar cantidad
            if (devolucionDTO.cantidad <= 0) {
                return ResponseEntity.badRequest().body(
                        Map.of("error", "La cantidad a devolver debe ser mayor a cero"));
            }

            // Registrar la devolución
            Lote lote = inventarioService.registrarDevolucion(
                    devolucionDTO.idLote,
                    devolucionDTO.cantidad,
                    devolucionDTO.motivo
            );

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Devolución procesada correctamente",
                    "lote", lote
            ));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar devolución: " + e.getMessage()));
        }
    }

    /**
     * Obtiene los lotes por producto
     */
    @GetMapping("/lotes/producto/{idProducto}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_productos')")
    public ResponseEntity<List<Lote>> obtenerLotesPorProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(inventarioService.obtenerLotesPorProducto(idProducto));
    }

    /**
     * Obtiene los movimientos por producto
     */
    @GetMapping("/movimientos/producto/{idProducto}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_entradas_productos')")
    public ResponseEntity<List<RegistMovimient>> obtenerMovimientosPorProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(inventarioService.obtenerMovimientosPorProducto(idProducto));
    }

    // DTOs para recibir datos en las peticiones

    public static class CompraDTO {
        public Integer idProducto;
        public Integer idProveedor;
        public Integer cantidad;
        public BigDecimal precioUnitario;
        public String motivo;
        public String numeroLote;
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        public Date fechaVencimiento;
    }

    public static class VentaDTO {
        public Integer idProducto;
        public Integer cantidad;
        public BigDecimal precioUnitario;
        public String motivo;
    }

    public static class AjusteDTO {
        public Integer idProducto;
        public Integer cantidad; // Positivo para aumentar, negativo para disminuir
        public String motivo;
    }
    
    public static class AuditoriaDTO {
        public Integer idProducto;
        public Integer stockReal;
        public String motivo;
        public Integer idUsuario;
    }

    public static class DevolucionDTO {
        public Integer idLote;
        public Integer cantidad;
        public String motivo;
    }
} 