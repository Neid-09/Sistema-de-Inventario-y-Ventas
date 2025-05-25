package com.novaSup.InventoryGest.InventoryGest_Backend.service.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;

import java.awt.Color; // Importar Color de AWT

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DatosFiscalesEmisorPdfDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleImpuestoPdfDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleProductoPdfDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DatosFiscalesReceptorPdfDTO;
import com.lowagie.text.Phrase;
import org.springframework.stereotype.Service;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils; // Para copiar el stream del logo

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.List; // Importar java.util.List explícitamente

@Service
public class FacturaPdfGenerator {

    private static final String LOGO_PATH = "img/menuPrincipal/logo.png"; // Path al logo en resources
    private static final Font FONT_TITLE = new Font(Font.HELVETICA, 18, Font.BOLD);
    private static final Font FONT_NORMAL = new Font(Font.HELVETICA, 10, Font.NORMAL);
    private static final Font FONT_BOLD = new Font(Font.HELVETICA, 10, Font.BOLD);
    private static final Color COLOR_HEADER_TABLE = new Color(220, 220, 220); // Gris claro

    public byte[] generarPdfBytes(FacturaPdfData datosFactura) throws IOException, DocumentException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document(PageSize.LETTER, 36, 36, 36, 36); // Márgenes: left, right, top, bottom

        try {
            PdfWriter.getInstance(document, baos);
            document.open();

            // 1. Encabezado (Título, datos del emisor, logo, fecha y número de factura)
            addHeaderSection(document, datosFactura.getEmisor(), datosFactura.getFechaEmision() != null ? datosFactura.getFechaEmision().toString() : "N/A", datosFactura.getNumeroFactura() != null ? datosFactura.getNumeroFactura() : "N/A (Previsualización)");

            // Espacio
            document.add(new Paragraph(" "));

            // 2. Información del Cliente
            addClientInfo(document, datosFactura.getReceptor());

            // Espacio
            document.add(new Paragraph(" "));

            // 3. Tabla con detalles de productos
            addDetallesProductoTable(document, datosFactura.getDetallesProducto());

             // Espacio
            document.add(new Paragraph(" "));

            // 4. Totales (Subtotal, Impuestos, Total)
            addTotales(document, datosFactura);

            // 5. Pie de página
            addFooter(document);

        } finally {
            if (document.isOpen()) {
                document.close();
            }
        }

        return baos.toByteArray();
    }

    private void addHeaderSection(Document document, DatosFiscalesEmisorPdfDTO emisor, String fechaEmision, String numeroFactura) throws DocumentException, IOException {
        // Título principal "FACTURA DE VENTA"
        Paragraph title = new Paragraph("FACTURA DE VENTA", FONT_TITLE);
        title.setAlignment(Element.ALIGN_CENTER);
        document.add(title);

        // Espacio
        document.add(new Paragraph(" "));

        // Tabla para organizar logo, datos del emisor, fecha y número de factura
        PdfPTable headerDetailsTable = new PdfPTable(2); // 2 columnas
        headerDetailsTable.setWidthPercentage(100);
        headerDetailsTable.setWidths(new float[]{3, 2}); // Proporción de ancho: datos emisor/logo, fecha/numero
        headerDetailsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Celda izquierda: Logo y Datos del Emisor
        PdfPTable emisorLogoTable = new PdfPTable(2); // Tabla anidada para logo y datos emisor
        emisorLogoTable.setWidthPercentage(100);
        emisorLogoTable.setWidths(new float[]{1, 3}); // Proporción: logo, datos
        emisorLogoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Logo (columna 1 de emisorLogoTable)
        try {
            ClassPathResource resource = new ClassPathResource(LOGO_PATH);
            Image logo = Image.getInstance(StreamUtils.copyToByteArray(resource.getInputStream()));
            logo.scaleToFit(80, 80); // Escalar logo un poco más pequeño
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            logoCell.setVerticalAlignment(Element.ALIGN_TOP);
            emisorLogoTable.addCell(logoCell);
        } catch (IOException | BadElementException e) {
            PdfPCell emptyCell = new PdfPCell(new Phrase("Logo no disponible", FONT_NORMAL));
            emptyCell.setBorder(Rectangle.NO_BORDER);
            emptyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            emptyCell.setVerticalAlignment(Element.ALIGN_TOP); // Alinear arriba
            emisorLogoTable.addCell(emptyCell);
            System.err.println("Error al cargar el logo: " + e.getMessage());
        }

        // Datos del Emisor (columna 2 de emisorLogoTable)
        Paragraph emisorInfo = new Paragraph();
        emisorInfo.setSpacingAfter(5f); // Espacio después del párrafo

        Chunk businessName = new Chunk(emisor.getNombre() != null ? emisor.getNombre() : "", FONT_BOLD); // Nombre en negrita
        emisorInfo.add(businessName);

        if (emisor.getDireccion() != null && !emisor.getDireccion().isEmpty()) {
             emisorInfo.add(new Phrase("\nDirección: " + emisor.getDireccion(), FONT_NORMAL));
        }
         if (emisor.getIdentificacion() != null && !emisor.getIdentificacion().isEmpty()) {
             emisorInfo.add(new Phrase("\nNIT: " + emisor.getIdentificacion(), FONT_NORMAL));
        }

        PdfPCell emisorCell = new PdfPCell(emisorInfo);
        emisorCell.setBorder(Rectangle.NO_BORDER);
        emisorCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        emisorCell.setVerticalAlignment(Element.ALIGN_TOP); // Alinear arriba
        emisorLogoTable.addCell(emisorCell);

        // Añadir la tabla anidada a la celda izquierda de headerDetailsTable
        PdfPCell leftCell = new PdfPCell(emisorLogoTable);
        leftCell.setBorder(Rectangle.NO_BORDER);
        leftCell.setVerticalAlignment(Element.ALIGN_TOP);
        headerDetailsTable.addCell(leftCell);

        // Celda derecha: Fecha y Número de Factura
        Paragraph facturaDetails = new Paragraph();
        facturaDetails.setSpacingAfter(5f); // Espacio después del párrafo

        Chunk fechaLabel = new Chunk("Fecha: ", FONT_BOLD);
        Chunk fechaValue = new Chunk(fechaEmision, FONT_NORMAL);
        facturaDetails.add(fechaLabel);
        facturaDetails.add(fechaValue);

        facturaDetails.add(new Phrase("\n")); // Salto de línea

        Chunk numFacturaLabel = new Chunk("Factura No: ", FONT_BOLD);
        Chunk numFacturaValue = new Chunk(numeroFactura, FONT_NORMAL);
        facturaDetails.add(numFacturaLabel);
        facturaDetails.add(numFacturaValue);

        PdfPCell facturaCell = new PdfPCell(facturaDetails);
        facturaCell.setBorder(Rectangle.NO_BORDER);
        facturaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        facturaCell.setVerticalAlignment(Element.ALIGN_TOP); // Alinear arriba
        headerDetailsTable.addCell(facturaCell);

        document.add(headerDetailsTable);
    }

    // Renombrado y modificado para ser solo la información del cliente
    private void addClientInfo(Document document, DatosFiscalesReceptorPdfDTO receptor) throws DocumentException {
        // Sección de Información del Cliente
        Paragraph clientTitle = new Paragraph("CLIENTE", FONT_BOLD);
        clientTitle.setSpacingAfter(5f); // Espacio después del título

        PdfPTable clientTable = new PdfPTable(1); // Una sola columna para los detalles
        clientTable.setWidthPercentage(100);
        clientTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        Paragraph clientDetails = new Paragraph();
        clientDetails.setSpacingAfter(5f); // Espacio después del párrafo

        if (receptor != null) {
            Chunk nombreLabel = new Chunk("Nombre/Razón Social: ", FONT_BOLD);
            Chunk nombreValue = new Chunk(receptor.getNombreRazonSocial() != null ? receptor.getNombreRazonSocial() : "N/A", FONT_NORMAL);
            clientDetails.add(nombreLabel);
            clientDetails.add(nombreValue);

             clientDetails.add(new Phrase("\n")); // Salto de línea

            Chunk idLabel = new Chunk("NIT/Identificación: ", FONT_BOLD); // Etiqueta actualizada
            Chunk idValue = new Chunk(receptor.getIdentificacion() != null ? receptor.getIdentificacion() : "N/A", FONT_NORMAL);
             clientDetails.add(idLabel);
             clientDetails.add(idValue);

            clientDetails.add(new Phrase("\n")); // Salto de línea

            Chunk dirLabel = new Chunk("Dirección: ", FONT_BOLD);
            Chunk dirValue = new Chunk(receptor.getDireccion() != null ? receptor.getDireccion() : "N/A", FONT_NORMAL);
             clientDetails.add(dirLabel);
             clientDetails.add(dirValue);

             // Uso fiscal si se quiere incluir
             if (receptor.getUsoFiscal() != null && !receptor.getUsoFiscal().isEmpty()) {
                  clientDetails.add(new Phrase("\n")); // Salto de línea
                  Chunk usoLabel = new Chunk("Uso Fiscal: ", FONT_BOLD);
                  Chunk usoValue = new Chunk(receptor.getUsoFiscal(), FONT_NORMAL);
                  clientDetails.add(usoLabel);
                  clientDetails.add(usoValue);
             }

        } else {
            clientDetails.add(new Phrase("Información del cliente no disponible.", FONT_NORMAL));
        }

        PdfPCell clientCell = new PdfPCell(clientDetails);
        clientCell.setBorder(Rectangle.NO_BORDER);
        clientCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        clientTable.addCell(clientCell);

        document.add(clientTitle);
        document.add(clientTable);
    }

    private void addDetallesProductoTable(Document document, List<DetalleProductoPdfDTO> detallesProducto) throws DocumentException {
        PdfPTable table = new PdfPTable(5); // Cod, Nombre, Cantidad, Precio Unitario, Subtotal
        table.setWidthPercentage(100);
        table.setWidths(new float[]{1, 3, 1, 1.5f, 1.5f}); // Proporciones de ancho
        table.setSpacingBefore(10f);
        table.setSpacingAfter(10f);

        // Encabezados de la tabla
        addTableCell(table, "Código", FONT_BOLD, Element.ALIGN_CENTER, COLOR_HEADER_TABLE);
        addTableCell(table, "Producto", FONT_BOLD, Element.ALIGN_LEFT, COLOR_HEADER_TABLE);
        addTableCell(table, "Cantidad", FONT_BOLD, Element.ALIGN_CENTER, COLOR_HEADER_TABLE);
        addTableCell(table, "Precio Unitario", FONT_BOLD, Element.ALIGN_RIGHT, COLOR_HEADER_TABLE);
        addTableCell(table, "Subtotal", FONT_BOLD, Element.ALIGN_RIGHT, COLOR_HEADER_TABLE);

        // Filas de detalles de productos
        if (detallesProducto != null && !detallesProducto.isEmpty()) {
            DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("es", "CO")); // Usar Locale.of para evitar el constructor deprecated
            DecimalFormat priceFormat = new DecimalFormat("#,##0.00", symbols);

            for (DetalleProductoPdfDTO detalle : detallesProducto) {
                addTableCell(table, detalle.getCodigo(), FONT_NORMAL, Element.ALIGN_CENTER, null);
                addTableCell(table, detalle.getNombre(), FONT_NORMAL, Element.ALIGN_LEFT, null);
                addTableCell(table, detalle.getCantidad().toString(), FONT_NORMAL, Element.ALIGN_CENTER, null);
                addTableCell(table, priceFormat.format(detalle.getPrecioUnitario()), FONT_NORMAL, Element.ALIGN_RIGHT, null);
                addTableCell(table, priceFormat.format(detalle.getSubtotal()), FONT_NORMAL, Element.ALIGN_RIGHT, null);
            }
        } else {
             // Añadir una fila indicando que no hay detalles
             PdfPCell noDetailsCell = new PdfPCell(new Phrase("No hay detalles de productos para mostrar.", FONT_NORMAL));
             noDetailsCell.setColspan(5);
             noDetailsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
             noDetailsCell.setPadding(10f);
             table.addCell(noDetailsCell);
        }

        document.add(table);
    }

     private void addTotales(Document document, FacturaPdfData datosFactura) throws DocumentException {
        PdfPTable totalsTable = new PdfPTable(2); // Etiquetas y valores
        totalsTable.setWidthPercentage(50); // Tabla más pequeña, alineada a la derecha
        totalsTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalsTable.setWidths(new float[]{2, 2}); // Proporción para etiquetas y valores
        totalsTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        DecimalFormatSymbols symbols = new DecimalFormatSymbols(Locale.of("es", "CO")); // Usar Locale.of para evitar el constructor deprecated
        DecimalFormat priceFormat = new DecimalFormat("#,##0.00", symbols);

        // Subtotal
        addTableCell(totalsTable, "Subtotal:", FONT_NORMAL, Element.ALIGN_RIGHT, null); // Etiqueta Subtotal con FONT_NORMAL
        addTableCell(totalsTable, priceFormat.format(datosFactura.getSubtotal()), FONT_NORMAL, Element.ALIGN_RIGHT, null);

        // Impuestos
        if (datosFactura.getDetallesImpuesto() != null && !datosFactura.getDetallesImpuesto().isEmpty()) {
             for (DetalleImpuestoPdfDTO detalleImpuesto : datosFactura.getDetallesImpuesto()) {
                 // Opcional: Podrías ajustar el formato si quieres que los impuestos se vean diferentes,
                 // pero por ahora los mantenemos similar al subtotal.
                 addTableCell(totalsTable, detalleImpuesto.getNombreTipoImpuesto() + ":", FONT_NORMAL, Element.ALIGN_RIGHT, null);
                 addTableCell(totalsTable, priceFormat.format(detalleImpuesto.getMontoImpuesto()), FONT_NORMAL, Element.ALIGN_RIGHT, null);
             }
             // Total de impuestos si se muestra por separado
              if (datosFactura.getTotalImpuestos() != null && BigDecimal.ZERO.compareTo(datosFactura.getTotalImpuestos()) != 0) { // Mostrar solo si no es cero
                 addTableCell(totalsTable, "Total Impuestos:", FONT_NORMAL, Element.ALIGN_RIGHT, null);
                 addTableCell(totalsTable, priceFormat.format(datosFactura.getTotalImpuestos()), FONT_NORMAL, Element.ALIGN_RIGHT, null);
              }
        }

        // Total Final - Destacado como en la imagen
        // Añadir una línea de separación antes del total si hay impuestos
        if ((datosFactura.getDetallesImpuesto() != null && !datosFactura.getDetallesImpuesto().isEmpty()) ||
            (datosFactura.getTotalImpuestos() != null && BigDecimal.ZERO.compareTo(datosFactura.getTotalImpuestos()) != 0)) {
             PdfPCell separatorCell = new PdfPCell(new Phrase(""));
             separatorCell.setColspan(2);
             separatorCell.setBorder(Rectangle.TOP); // Borde superior como separador
             separatorCell.setBorderColor(Color.BLACK);
             separatorCell.setPadding(2f);
             totalsTable.addCell(separatorCell);
        }


        addTableCell(totalsTable, "Total:", FONT_BOLD, Element.ALIGN_RIGHT, null); // Etiqueta Total en negrita
        addTableCell(totalsTable, priceFormat.format(datosFactura.getTotalConImpuestos()), FONT_BOLD, Element.ALIGN_RIGHT, null); // Valor Total en negrita

        document.add(totalsTable);
    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("Gracias por su compra!", FONT_NORMAL);
        footer.setAlignment(Element.ALIGN_CENTER);
        // Añadir un espacio antes del pie de página para separarlo de los totales
        footer.setSpacingBefore(20f);
        document.add(footer);
    }

    // Método helper para añadir celdas a la tabla
    private void addTableCell(PdfPTable table, String text, Font font, int alignment, Color backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        cell.setBorder(Rectangle.NO_BORDER); // Eliminar bordes de las celdas por defecto para un look más limpio
        if (backgroundColor != null) {
            cell.setBackgroundColor(backgroundColor);
        }
        table.addCell(cell);
    }
} 