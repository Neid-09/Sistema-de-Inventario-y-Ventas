package com.novaSup.InventoryGest.InventoryGest_Backend.controller;

import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.FacturaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.FacturaPreviewDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaRequestDTO;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

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

    /**
     * Endpoint para generar el PDF de una factura existente por su ID.
     * @param id ID de la factura a generar en PDF.
     * @return ResponseEntity con los bytes del PDF y el Content-Type adecuado, o un estado de error.
     */
    @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> generarPdfFacturaExistente(@PathVariable int id) {
        try {
            byte[] pdfBytes = facturaService.generarPdfFactura(id);
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            // Opcional: Sugerir un nombre de archivo para la descarga
            headers.setContentDispositionFormData("attachment", "factura_" + id + ".pdf");
            headers.setContentLength(pdfBytes.length);

            return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);

        } catch (EntityNotFoundException e) {
             // Manejar caso donde no se encuentra la factura
            return ResponseEntity.notFound().build(); // 404 Not Found
        } catch (Exception e) {
            // Manejar otros errores inesperados durante la generación del PDF
            // Loggear el error para diagnóstico
            e.printStackTrace(); // Reemplazar con logging adecuado en un entorno real
            return ResponseEntity.internalServerError().build(); // 500 Internal Server Error
        }
    }

    /**
     * Endpoint para generar el PDF de una previsualización de factura.
     * Recibe los datos de la venta propuesta y devuelve el PDF de la previsualización.
     * @param ventaRequest DTO con la información de la venta propuesta.
     * @return ResponseEntity con los bytes del PDF de previsualización y el Content-Type adecuado, o un estado de error.
     */
    @PostMapping("/preview/pdf")
    public ResponseEntity<byte[]> generarPdfPreviewFactura(@RequestBody VentaRequestDTO ventaRequest) {
         try {
             byte[] pdfBytes = facturaService.generarPdfPreview(ventaRequest);
             HttpHeaders headers = new HttpHeaders();
             headers.setContentType(MediaType.APPLICATION_PDF);
             // Sugerir un nombre de archivo para la descarga (indicando que es una preview)
             headers.setContentDispositionFormData("attachment", "preview_factura.pdf");
             headers.setContentLength(pdfBytes.length);

             return new ResponseEntity<>(pdfBytes, headers, org.springframework.http.HttpStatus.OK);

         } catch (IllegalArgumentException | IllegalStateException e) {
             // Manejar errores de validación o estado
             return ResponseEntity.badRequest().build(); // 400 Bad Request
         } catch (EntityNotFoundException e) {
             // Manejar casos donde no se encuentra un producto, cliente o vendedor durante la previsualización
             return ResponseEntity.notFound().build(); // 404 Not Found
         } catch (Exception e) {
             // Manejar otros errores inesperados durante la previsualización o generación del PDF
             // Loggear el error para diagnóstico
             e.printStackTrace(); // Reemplazar con logging adecuado en un entorno real
             return ResponseEntity.internalServerError().build(); // 500 Internal Server Error
         }
    }
} 