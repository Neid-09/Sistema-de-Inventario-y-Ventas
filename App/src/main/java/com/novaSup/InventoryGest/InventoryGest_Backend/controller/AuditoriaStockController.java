package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaStock;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.AuditoriaStockService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/auditoria-stock")
public class AuditoriaStockController {

    @Autowired
    private AuditoriaStockService auditoriaStockService;

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('auditar_stock') or hasAuthority('ver_auditorias')")
    public ResponseEntity<List<AuditoriaStock>> listarAuditorias() {
        return ResponseEntity.ok(auditoriaStockService.obtenerTodas());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('auditar_stock') or hasAuthority('ver_auditorias')")
    public ResponseEntity<?> obtenerAuditoria(@PathVariable Integer id) {
        return auditoriaStockService.obtenerPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/producto/{idProducto}")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('auditar_stock') or hasAuthority('ver_auditorias')")
    public ResponseEntity<List<AuditoriaStock>> obtenerAuditoriasPorProducto(@PathVariable Integer idProducto) {
        return ResponseEntity.ok(auditoriaStockService.obtenerPorProducto(idProducto));
    }

    @GetMapping("/fecha")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('auditar_stock') or hasAuthority('ver_auditorias')")
    public ResponseEntity<List<AuditoriaStock>> obtenerAuditoriasPorRangoFecha(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin) {
        return ResponseEntity.ok(auditoriaStockService.obtenerPorRangoFecha(fechaInicio, fechaFin));
    }

    @PostMapping
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('auditar_stock')")
    public ResponseEntity<?> registrarAuditoria(@RequestBody AuditoriaDTO auditoriaDTO) {
        try {
            // Validar datos de entrada
            if (auditoriaDTO.idProducto == null || auditoriaDTO.stockReal == null) {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "Los campos idProducto y stockReal son obligatorios");
                return ResponseEntity.badRequest().body(error);
            }

            // Verificar que el producto existe
            Optional<Producto> productoOptional = productoService.obtenerPorId(auditoriaDTO.idProducto);

            if (!productoOptional.isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("mensaje", "El producto especificado no existe");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
            }

            Producto producto = productoOptional.get();
            // Guardar el stock anterior antes de cualquier actualización
            Integer stockAnterior = producto.getStock();

            // Registrar la auditoría
            AuditoriaStock nuevaAuditoria = auditoriaStockService.registrarAuditoria(
                    auditoriaDTO.idProducto,
                    stockAnterior,
                    auditoriaDTO.stockReal,
                    auditoriaDTO.motivo,
                    auditoriaDTO.idUsuario
            );

            // Forzar la carga del producto relacionado
            nuevaAuditoria.setProducto(producto);

            // Si hay diferencia, actualizar el stock real en el producto
            if (!auditoriaDTO.stockReal.equals(stockAnterior)) {
                productoService.actualizarStock(auditoriaDTO.idProducto, auditoriaDTO.stockReal);

                Map<String, Object> respuesta = new HashMap<>();
                respuesta.put("auditoria", nuevaAuditoria);
                respuesta.put("mensaje", "Se ha actualizado el stock del producto debido a la diferencia encontrada");
                respuesta.put("stockAnterior", stockAnterior);
                respuesta.put("stockNuevo", auditoriaDTO.stockReal);

                return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaAuditoria);

        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al registrar la auditoría: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @GetMapping("/resumen")
    @PreAuthorize("hasRole('ROLE_ADMINISTRADOR') or hasAuthority('ver_auditorias')")
    public ResponseEntity<?> obtenerResumenAuditorias() {
        try {
            List<AuditoriaStock> auditorias = auditoriaStockService.obtenerTodas();

            // Contar auditorías con discrepancias
            long auditoriaConDiscrepancia = auditorias.stream()
                    .filter(a -> !a.getStockEsperado().equals(a.getStockReal()))
                    .count();

            Map<String, Object> resumen = new HashMap<>();
            resumen.put("totalAuditorias", auditorias.size());
            resumen.put("auditoriasConDiscrepancia", auditoriaConDiscrepancia);

            return ResponseEntity.ok(resumen);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("mensaje", "Error al obtener el resumen de auditorías: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    // Clase DTO para recibir los datos de auditoría
    public static class AuditoriaDTO {
        public Integer idProducto;
        public Integer stockReal;
        public String motivo;
        public Integer idUsuario;
    }
}