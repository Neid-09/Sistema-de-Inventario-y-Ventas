package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.service.FacturaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.FacturaPreviewDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaRequestDTO;
import jakarta.persistence.EntityNotFoundException;

@RestController
@RequestMapping("/api/facturas")
public class FacturaController {

    private final FacturaService facturaService;

    public FacturaController(FacturaService facturaService) {
        this.facturaService = facturaService;
    }

    @GetMapping("/{id}")
    public ResponseEntity<FacturaPreviewDTO> getFacturaById(@PathVariable int id) {
        FacturaPreviewDTO facturaDTO = facturaService.obtenerFacturaPorId(id);
        if (facturaDTO != null) {
            return ResponseEntity.ok(facturaDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Endpoint para previsualizar una factura antes de registrar la venta.
     * Recibe los datos de la venta propuesta y devuelve una previsualización de la factura sin persistirla.
     * @param ventaRequest DTO con la información de la venta propuesta.
     * @return ResponseEntity con la FacturaPreviewDTO o un mensaje de error.
     */
    @PostMapping("/preview")
    public ResponseEntity<FacturaPreviewDTO> previewFactura(@RequestBody VentaRequestDTO ventaRequest) {
        try {
            FacturaPreviewDTO facturaPreview = facturaService.previewFactura(ventaRequest);
            return ResponseEntity.ok(facturaPreview);
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Manejar errores de validación o estado
            return ResponseEntity.badRequest().build(); // O devolver un mensaje de error más específico
        } catch (EntityNotFoundException e) {
             // Manejar casos donde no se encuentra un producto, cliente o vendedor
            return ResponseEntity.notFound().build(); // O devolver un mensaje de error más específico
        } catch (Exception e) {
            // Manejar otros errores inesperados
            return ResponseEntity.internalServerError().build(); // O devolver un mensaje de error más específico
        }
    }
} 