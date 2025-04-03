package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.EntradaProducto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Proveedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.EntradaProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProveedorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/entradas")
public class EntradaProductoController {

    @Autowired
    private EntradaProductoService entradaProductoService;

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping
    public ResponseEntity<List<EntradaProducto>> obtenerTodasLasEntradas() {
        return ResponseEntity.ok(entradaProductoService.obtenerTodas());
    }

    @GetMapping("/producto/{idProducto}")
    public ResponseEntity<List<EntradaProducto>> obtenerPorProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(entradaProductoService.obtenerPorProducto(idProducto));
    }

    @GetMapping("/fecha")
    public ResponseEntity<List<EntradaProducto>> obtenerPorFecha(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta) {

        List<EntradaProducto> entradas = entradaProductoService.obtenerTodas();

        // Filtrar por fechas si se proporcionaron
        if (desde != null) {
            entradas = entradas.stream()
                    .filter(e -> e.getFecha().toLocalDate().isEqual(desde) || e.getFecha().toLocalDate().isAfter(desde))
                    .collect(Collectors.toList());
        }

        if (hasta != null) {
            entradas = entradas.stream()
                    .filter(e -> e.getFecha().toLocalDate().isEqual(hasta) || e.getFecha().toLocalDate().isBefore(hasta))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(entradas);
    }

    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<EntradaProducto>> obtenerPorTipo(@PathVariable String tipo) {
        List<EntradaProducto> entradas = entradaProductoService.obtenerTodas().stream()
                .filter(e -> e.getTipoMovimiento().equalsIgnoreCase(tipo))
                .collect(Collectors.toList());

        return ResponseEntity.ok(entradas);
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<EntradaProducto>> obtenerFiltrados(
            @RequestParam(required = false) Integer idProducto,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
            @RequestParam(required = false) String tipo) {

        List<EntradaProducto> entradas = entradaProductoService.obtenerTodas();

        // Aplicar filtros según parámetros recibidos
        if (idProducto != null) {
            entradas = entradas.stream()
                    .filter(e -> e.getProducto().getIdProducto().equals(idProducto))
                    .collect(Collectors.toList());
        }

        if (desde != null) {
            entradas = entradas.stream()
                    .filter(e -> e.getFecha().toLocalDate().isEqual(desde) || e.getFecha().toLocalDate().isAfter(desde))
                    .collect(Collectors.toList());
        }

        if (hasta != null) {
            entradas = entradas.stream()
                    .filter(e -> e.getFecha().toLocalDate().isEqual(hasta) || e.getFecha().toLocalDate().isBefore(hasta))
                    .collect(Collectors.toList());
        }

        if (tipo != null && !tipo.isEmpty()) {
            entradas = entradas.stream()
                    .filter(e -> e.getTipoMovimiento().equalsIgnoreCase(tipo))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(entradas);
    }

    @PostMapping
    public ResponseEntity<EntradaProducto> registrarMovimiento(@RequestBody MovimientoDTO movimientoDTO) {
        Optional<Producto> productoOpt = productoService.obtenerPorId(movimientoDTO.idProducto);

        if (!productoOpt.isPresent()) {
            return ResponseEntity.notFound().build();
        }

        Producto producto = productoOpt.get();
        EntradaProducto entrada = new EntradaProducto();
        entrada.setProducto(producto);
        entrada.setCantidad(movimientoDTO.cantidad);
        entrada.setFecha(LocalDateTime.now());
        entrada.setTipoMovimiento(movimientoDTO.tipoMovimiento);
        entrada.setMotivo(movimientoDTO.motivo);

        // Asignar proveedor si es una entrada
        if ("ENTRADA".equalsIgnoreCase(movimientoDTO.tipoMovimiento) && movimientoDTO.idProveedor != null) {
            Proveedor proveedor = proveedorService.obtenerPorId(movimientoDTO.idProveedor)
                    .orElseThrow(() -> new RuntimeException("Proveedor no encontrado"));
            entrada.setProveedor(proveedor);
        }

        // Usar precio específico según el tipo de movimiento
        if (movimientoDTO.precioUnitario != null) {
            entrada.setPrecioUnitario(movimientoDTO.precioUnitario);
        } else {
            if ("ENTRADA".equalsIgnoreCase(movimientoDTO.tipoMovimiento)) {
                entrada.setPrecioUnitario(producto.getPrecioCosto());
            } else {
                entrada.setPrecioUnitario(producto.getPrecioVenta());
            }
        }

        // Actualizar stock del producto
        if ("ENTRADA".equalsIgnoreCase(movimientoDTO.tipoMovimiento)) {
            producto.setStock(producto.getStock() + movimientoDTO.cantidad);
        } else if ("SALIDA".equalsIgnoreCase(movimientoDTO.tipoMovimiento)) {
            int nuevoStock = producto.getStock() - movimientoDTO.cantidad;
            if (nuevoStock < 0) {
                return ResponseEntity.badRequest().build(); // No hay suficiente stock
            }
            producto.setStock(nuevoStock);
        }

        productoService.guardar(producto); // Actualizar producto
        EntradaProducto entradaGuardada = entradaProductoService.guardar(entrada);

        return new ResponseEntity<>(entradaGuardada, HttpStatus.CREATED);
    }

    // Clase DTO para recibir los datos de movimiento
// Actualizar MovimientoDTO
    private static class MovimientoDTO {
        public Integer idProducto;
        public Integer idProveedor; // Nuevo campo
        public Integer cantidad;
        public String tipoMovimiento;
        public BigDecimal precioUnitario;
        public String motivo; // Nuevo campo
    }
}