package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaRequestDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaResponseDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleVentaResponseDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleVentaLoteUsoResponseDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVenta;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVentaLoteUso;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.VentaService;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor
@Slf4j
public class VentaController {

    private final VentaService ventaService;

    // Método helper para convertir DetalleVentaLoteUso a DetalleVentaLoteUsoResponseDTO
    private DetalleVentaLoteUsoResponseDTO convertToLoteUsoDto(DetalleVentaLoteUso loteUso) {
        if (loteUso == null) return null;
        DetalleVentaLoteUsoResponseDTO dto = new DetalleVentaLoteUsoResponseDTO();
        Lote lote = loteUso.getLote();
        if (lote != null) {
            dto.setIdLote(lote.getIdLote());
            dto.setCodigoLote(lote.getNumeroLote());
        }
        dto.setCantidadTomada(loteUso.getCantidadTomada());
        return dto;
    }

    // Método helper para convertir DetalleVenta a DetalleVentaResponseDTO
    private DetalleVentaResponseDTO convertToDetalleDto(DetalleVenta detalleVenta) {
        if (detalleVenta == null) return null;
        DetalleVentaResponseDTO dto = new DetalleVentaResponseDTO();
        dto.setIdDetalleVenta(detalleVenta.getIdDetalle());
        
        Producto producto = detalleVenta.getProducto();
        if (producto != null) {
            dto.setIdProducto(producto.getIdProducto());
            dto.setNombreProducto(producto.getNombre());
        }
        
        dto.setCantidad(detalleVenta.getCantidad());
        dto.setPrecioUnitarioOriginal(detalleVenta.getPrecioUnitarioOriginal());
        dto.setIdPromocionAplicada(detalleVenta.getIdPromocionAplicada());
        dto.setPrecioUnitarioFinal(detalleVenta.getPrecioUnitarioFinal());
        dto.setSubtotal(detalleVenta.getSubtotal());

        if (detalleVenta.getDetalleLotesUsados() != null && !detalleVenta.getDetalleLotesUsados().isEmpty()) {
            dto.setLotesUsados(detalleVenta.getDetalleLotesUsados().stream()
                .map(this::convertToLoteUsoDto)
                .collect(Collectors.toList()));
        } else {
            dto.setLotesUsados(new ArrayList<>());
        }
        return dto;
    }

    // Método helper simplificado para convertir Venta a VentaResponseDTO
    private VentaResponseDTO convertToDto(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setIdVenta(venta.getIdVenta());
        dto.setFecha(venta.getFecha());
        if (venta.getCliente() != null) {
            dto.setIdCliente(venta.getCliente().getIdCliente());
            if (venta.getCliente().getNombre() != null) {
                dto.setNombreCliente(venta.getCliente().getNombre());
            } else {
                log.trace("Cliente ID: {} no tiene un nombre asignado.", venta.getCliente().getIdCliente());
            }
        }
        if (venta.getVendedor() != null) {
            dto.setIdVendedor(venta.getVendedor().getIdVendedor());
            Vendedor vendedor = venta.getVendedor();
            if (vendedor != null && vendedor.getUsuario() != null) {
                 dto.setNombreVendedor(vendedor.getUsuario().getNombre());
            } else {
                 log.trace("Usuario no cargado para Vendedor ID: {} en Venta ID: {}", (vendedor != null ? vendedor.getIdVendedor() : "null"), venta.getIdVenta());
            }
        } else {
             log.trace("Vendedor no cargado para Venta ID: {}", venta.getIdVenta());
        }

        dto.setTotal(venta.getTotalConImpuestos());
        dto.setRequiereFactura(venta.getRequiereFactura());
        dto.setNumeroVenta(venta.getNumeroVenta());
        dto.setAplicarImpuestos(venta.getAplicarImpuestos());
        dto.setTipoPago(venta.getTipoPago()); // Añadir mapeo para tipoPago

        if (venta.getDetallesVenta() != null && !venta.getDetallesVenta().isEmpty()) {
            dto.setDetalles(venta.getDetallesVenta().stream()
                .map(this::convertToDetalleDto)
                .collect(Collectors.toList()));
        } else {
            dto.setDetalles(new ArrayList<>());
        }

        return dto;
    }

    /**
     * Endpoint para registrar una nueva venta completa (Venta, Detalles, Stock, Comisión).
     * Utiliza VentaRequestDTO para recibir los datos.
     * @param ventaRequest DTO con la información de la venta.
     * @return ResponseEntity con la Venta creada o un mensaje de error.
     */
    @PostMapping
    public ResponseEntity<VentaResponseDTO> procesarVentaCompleta(@RequestBody VentaRequestDTO ventaRequest) {
        log.info("Intentando registrar una venta completa: {}", ventaRequest);
        try {
            Venta nuevaVenta = ventaService.registrarVentaCompleta(ventaRequest);
            log.info("Venta registrada exitosamente con ID: {}", nuevaVenta.getIdVenta());
            VentaResponseDTO responseDto = convertToDto(nuevaVenta);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Error de validación o estado durante el registro de la venta: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (EntityNotFoundException e) {
            log.warn("Entidad no encontrada durante el registro de la venta: {}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado procesando la venta: {}", ventaRequest, e);
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Error interno al procesar la venta.", e);
        }
    }

    /**
     * Obtiene una venta específica por su ID.
     * @param id ID de la venta.
     * @return ResponseEntity con la Venta encontrada o 404 si no existe.
     */
    @GetMapping("/{id}")
    public ResponseEntity<VentaResponseDTO> obtenerVentaPorId(@PathVariable Integer id) {
        log.info("Buscando venta con ID: {}", id);
        return ventaService.obtenerVentaPorId(id)
                .map(this::convertToDto)
                .map(dto -> {
                    log.info("Venta encontrada con ID: {} y {} detalles", id, (dto.getDetalles() != null ? dto.getDetalles().size() : 0));
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("Venta no encontrada con ID: {}", id);
                    return ResponseEntity.notFound().build();
                });
    }

    /**
     * Lista todas las ventas registradas.
     * @return ResponseEntity con la lista de ventas.
     */
    @GetMapping
    public ResponseEntity<List<VentaResponseDTO>> listarVentas() {
        log.info("Buscando todas las ventas");
        List<Venta> ventas = ventaService.listarVentas();
        List<VentaResponseDTO> dtos = ventas.stream()
                                            .map(this::convertToDto)
                                            .collect(Collectors.toList());
        log.info("Se encontraron {} ventas", dtos.size());
        return ResponseEntity.ok(dtos);
    }

    // --- Endpoints Adicionales ---

    /**
     * Obtiene las ventas asociadas a un cliente específico.
     * @param idCliente ID del cliente.
     * @return Lista de ventas del cliente.
     */
    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentasPorCliente(@PathVariable Integer idCliente) {
         log.info("Buscando ventas para el cliente ID: {}", idCliente);
         List<Venta> ventas = ventaService.obtenerVentasPorCliente(idCliente);
         List<VentaResponseDTO> dtos = ventas.stream()
                                             .map(this::convertToDto)
                                             .collect(Collectors.toList());
         log.info("Se encontraron {} ventas para el cliente ID: {}", dtos.size(), idCliente);
         return ResponseEntity.ok(dtos);
    }

     /**
     * Obtiene las ventas asociadas a un vendedor específico.
     * @param idVendedor ID del vendedor.
     * @return Lista de ventas del vendedor.
     */
    @GetMapping("/vendedor/{idVendedor}")
    public ResponseEntity<List<VentaResponseDTO>> obtenerVentasPorVendedor(@PathVariable Integer idVendedor) {
         log.info("Buscando ventas para el vendedor ID: {}", idVendedor);
         List<Venta> ventas = ventaService.obtenerVentasPorVendedor(idVendedor);
         List<VentaResponseDTO> dtos = ventas.stream()
                                             .map(this::convertToDto)
                                             .collect(Collectors.toList());
         log.info("Se encontraron {} ventas para el vendedor ID: {}", dtos.size(), idVendedor);
         return ResponseEntity.ok(dtos);
    }

}