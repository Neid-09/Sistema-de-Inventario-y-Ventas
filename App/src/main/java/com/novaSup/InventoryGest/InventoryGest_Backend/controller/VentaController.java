package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaRequestDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaResponseDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Vendedor; // Import Vendedor
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario; // Import Usuario
import com.novaSup.InventoryGest.InventoryGest_Backend.service.VentaService;

import jakarta.persistence.EntityNotFoundException; // Mantener para manejo específico o relanzamiento
import lombok.RequiredArgsConstructor; // Usar inyección por constructor
import lombok.extern.slf4j.Slf4j; // Usar Lombok para logging
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException; // Usar para respuestas de error más claras

import java.util.List;
import java.util.stream.Collectors;
// Se eliminó la importación de Optional ya que se maneja de forma diferente ahora

@RestController
@RequestMapping("/api/ventas")
@RequiredArgsConstructor // Inyecta dependencias vía constructor
@Slf4j // Añade una instancia de logger llamada 'log'
public class VentaController {

    private final VentaService ventaService; // Usar final para dependencias inyectadas
    // No necesitamos UsuarioRepository aquí, el servicio se encargará

    // Método helper simplificado para convertir Venta a VentaResponseDTO
    private VentaResponseDTO convertToDto(Venta venta) {
        VentaResponseDTO dto = new VentaResponseDTO();
        dto.setIdVenta(venta.getIdVenta());
        dto.setFecha(venta.getFecha());
        dto.setIdCliente(venta.getIdCliente());
        dto.setIdVendedor(venta.getIdVendedor());
        dto.setTotal(venta.getTotal());
        dto.setRequiereFactura(venta.getRequiereFactura());
        dto.setNumeroVenta(venta.getNumeroVenta());
        dto.setAplicarImpuestos(venta.getAplicarImpuestos());

        // Asigna el nombre del vendedor SI la información ya fue cargada por el servicio
        Vendedor vendedor = venta.getVendedor();
        if (vendedor != null) {
            Usuario usuario = vendedor.getUsuario();
            if (usuario != null) {
                dto.setNombreVendedor(usuario.getNombre());
            } else {
                 // Opcional: Log si el usuario es null aunque el vendedor no lo sea
                 log.trace("Usuario no cargado para Vendedor ID: {} en Venta ID: {}", vendedor.getIdVendedor(), venta.getIdVenta());
            }
        } else {
             // Opcional: Log si el vendedor es null
             log.trace("Vendedor no cargado para Venta ID: {}", venta.getIdVenta());
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
            // El servicio debe devolver la Venta con el Vendedor y Usuario cargados si es posible
            Venta nuevaVenta = ventaService.registrarVentaCompleta(ventaRequest);
            log.info("Venta registrada exitosamente con ID: {}", nuevaVenta.getIdVenta());
            VentaResponseDTO responseDto = convertToDto(nuevaVenta);
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
        } catch (IllegalArgumentException | IllegalStateException e) {
            log.warn("Error de validación o estado durante el registro de la venta: {}", e.getMessage());
            // Considerar usar @ControllerAdvice para manejo centralizado de excepciones
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, e.getMessage(), e);
        } catch (EntityNotFoundException e) {
            log.warn("Entidad no encontrada durante el registro de la venta: {}", e.getMessage());
            // Considerar usar @ControllerAdvice para manejo centralizado de excepciones
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, e.getMessage(), e);
        } catch (Exception e) {
            log.error("Error inesperado procesando la venta: {}", ventaRequest, e);
            // Considerar usar @ControllerAdvice para manejo centralizado de excepciones
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
        // El servicio debe devolver la Venta con el Vendedor y Usuario cargados
        return ventaService.obtenerVentaPorId(id)
                .map(this::convertToDto)
                .map(dto -> {
                    log.info("Venta encontrada con ID: {}", id);
                    return ResponseEntity.ok(dto);
                })
                .orElseGet(() -> {
                    log.warn("Venta no encontrada con ID: {}", id);
                    // No es necesario lanzar ResponseStatusException aquí, solo devolver 404
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
        // El servicio debe devolver la lista de Ventas con Vendedor y Usuario cargados
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
         // El servicio debe devolver las Ventas con Vendedor y Usuario cargados
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
         // El servicio debe devolver las Ventas con Vendedor y Usuario cargados
         List<Venta> ventas = ventaService.obtenerVentasPorVendedor(idVendedor);
         List<VentaResponseDTO> dtos = ventas.stream()
                                             .map(this::convertToDto)
                                             .collect(Collectors.toList());
         log.info("Se encontraron {} ventas para el vendedor ID: {}", dtos.size(), idVendedor);
         return ResponseEntity.ok(dtos);
    }

}