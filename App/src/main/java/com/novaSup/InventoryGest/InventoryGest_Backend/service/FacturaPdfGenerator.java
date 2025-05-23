package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfPCell;

import java.awt.Color; // Importar Color de AWT

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

    private static final String LOGO_PATH = "img/menuPrincipal/logoProvicional.jpg"; // Path al logo en resources
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

            // 1. Encabezado (Nombre del negocio y Logo)
            addHeader(document, datosFactura.getEmisor());

            // Espacio
            document.add(new Paragraph(" "));

            // 2. Información de Factura, Cliente y Fecha
            addFacturaInfo(document, datosFactura);

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

    private void addHeader(Document document, DatosFiscalesEmisorPdfDTO emisor) throws DocumentException, IOException {
        PdfPTable headerTable = new PdfPTable(2); // 2 columnas: Logo y datos del emisor
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{1, 3}); // Proporción de ancho: 1 para logo, 3 para datos
        headerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Columna 1: Logo
        try {
            ClassPathResource resource = new ClassPathResource(LOGO_PATH);
            Image logo = Image.getInstance(StreamUtils.copyToByteArray(resource.getInputStream()));
            logo.scaleToFit(100, 100); // Escalar logo
            PdfPCell logoCell = new PdfPCell(logo);
            logoCell.setBorder(Rectangle.NO_BORDER);
            logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
            logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(logoCell);
        } catch (IOException | BadElementException e) {
            // Si el logo no se encuentra o es inválido, añadir una celda vacía o un mensaje
            PdfPCell emptyCell = new PdfPCell(new Phrase("Logo no disponible", FONT_NORMAL));
            emptyCell.setBorder(Rectangle.NO_BORDER);
             emptyCell.setHorizontalAlignment(Element.ALIGN_LEFT);
             emptyCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            headerTable.addCell(emptyCell);
             System.err.println("Error al cargar el logo: " + e.getMessage()); // Log para depuración
        }

        // Columna 2: Datos del Emisor
        Chunk businessName = new Chunk(emisor.getNombre() != null ? emisor.getNombre() : "", FONT_TITLE);
        Chunk businessId = new Chunk("\nIdentificación: " + (emisor.getIdentificacion() != null ? emisor.getIdentificacion() : ""), FONT_NORMAL);
        Chunk businessAddress = new Chunk("\nDirección: " + (emisor.getDireccion() != null ? emisor.getDireccion() : ""), FONT_NORMAL);

        Paragraph emisorInfo = new Paragraph();
        emisorInfo.add(businessName);
        emisorInfo.add(businessId);
        emisorInfo.add(businessAddress);

        PdfPCell emisorCell = new PdfPCell(emisorInfo);
        emisorCell.setBorder(Rectangle.NO_BORDER);
        emisorCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        emisorCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        headerTable.addCell(emisorCell);

        document.add(headerTable);
    }

    private void addFacturaInfo(Document document, FacturaPdfData datosFactura) throws DocumentException {
        // Tabla para organizar la info de factura, cliente y fecha
        PdfPTable infoTable = new PdfPTable(2); // Dos columnas
        infoTable.setWidthPercentage(100);
        infoTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        // Columna 1: Número de Factura y Fecha
        Paragraph facturaDetails = new Paragraph();
        Chunk numFacturaLabel = new Chunk("Factura No: ", FONT_BOLD);
        Chunk numFacturaValue = new Chunk(datosFactura.getNumeroFactura() != null ? datosFactura.getNumeroFactura() : "N/A (Previsualización)", FONT_NORMAL);
        facturaDetails.add(numFacturaLabel);
        facturaDetails.add(numFacturaValue);

        facturaDetails.add(new Paragraph(" ", FONT_NORMAL)); // Espacio

        Chunk fechaLabel = new Chunk("Fecha: ", FONT_BOLD);
        Chunk fechaValue = new Chunk(datosFactura.getFechaEmision() != null ? datosFactura.getFechaEmision().toString() : "N/A", FONT_NORMAL);
        facturaDetails.add(fechaLabel);
        facturaDetails.add(fechaValue);

        PdfPCell facturaCell = new PdfPCell(facturaDetails);
        facturaCell.setBorder(Rectangle.NO_BORDER);
        facturaCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        infoTable.addCell(facturaCell);

        // Columna 2: Información del Cliente
        Paragraph clienteDetails = new Paragraph();
        clienteDetails.add(new Chunk("Información del Cliente:", FONT_BOLD));
        clienteDetails.add(new Paragraph(" ", FONT_NORMAL)); // Espacio

        if (datosFactura.getReceptor() != null) {
            Chunk nombreLabel = new Chunk("Nombre/Razón Social: ", FONT_BOLD);
            Chunk nombreValue = new Chunk(datosFactura.getReceptor().getNombreRazonSocial() != null ? datosFactura.getReceptor().getNombreRazonSocial() : "N/A", FONT_NORMAL);
            clienteDetails.add(nombreLabel);
            clienteDetails.add(nombreValue);

             clienteDetails.add(new Paragraph(" ", FONT_NORMAL)); // Espacio

            Chunk idLabel = new Chunk("Identificación Fiscal: ", FONT_BOLD);
            Chunk idValue = new Chunk(datosFactura.getReceptor().getIdentificacion() != null ? datosFactura.getReceptor().getIdentificacion() : "N/A", FONT_NORMAL);
             clienteDetails.add(idLabel);
             clienteDetails.add(idValue);

            clienteDetails.add(new Paragraph(" ", FONT_NORMAL)); // Espacio

            Chunk dirLabel = new Chunk("Dirección: ", FONT_BOLD);
            Chunk dirValue = new Chunk(datosFactura.getReceptor().getDireccion() != null ? datosFactura.getReceptor().getDireccion() : "N/A", FONT_NORMAL);
             clienteDetails.add(dirLabel);
             clienteDetails.add(dirValue);

             // Opcional: Uso fiscal si se quiere incluir
             if (datosFactura.getReceptor().getUsoFiscal() != null && !datosFactura.getReceptor().getUsoFiscal().isEmpty()) {
                  clienteDetails.add(new Paragraph(" ", FONT_NORMAL)); // Espacio
                  Chunk usoLabel = new Chunk("Uso Fiscal: ", FONT_BOLD);
                  Chunk usoValue = new Chunk(datosFactura.getReceptor().getUsoFiscal(), FONT_NORMAL);
                  clienteDetails.add(usoLabel);
                  clienteDetails.add(usoValue);
             }

        } else {
            clienteDetails.add(new Phrase("Información del cliente no disponible.", FONT_NORMAL));
        }

        PdfPCell clienteCell = new PdfPCell(clienteDetails);
        clienteCell.setBorder(Rectangle.NO_BORDER);
        clienteCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        infoTable.addCell(clienteCell);

        document.add(infoTable);
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
        addTableCell(totalsTable, "Subtotal:", FONT_BOLD, Element.ALIGN_RIGHT, null);
        addTableCell(totalsTable, priceFormat.format(datosFactura.getSubtotal()), FONT_NORMAL, Element.ALIGN_RIGHT, null);

        // Impuestos
        if (datosFactura.getDetallesImpuesto() != null && !datosFactura.getDetallesImpuesto().isEmpty()) {
             for (DetalleImpuestoPdfDTO detalleImpuesto : datosFactura.getDetallesImpuesto()) {
                 addTableCell(totalsTable, detalleImpuesto.getNombreTipoImpuesto() + ":", FONT_BOLD, Element.ALIGN_RIGHT, null);
                 addTableCell(totalsTable, priceFormat.format(detalleImpuesto.getMontoImpuesto()), FONT_NORMAL, Element.ALIGN_RIGHT, null);
             }
             // Total de impuestos si se muestra por separado
              if (datosFactura.getTotalImpuestos() != null && BigDecimal.ZERO.compareTo(datosFactura.getTotalImpuestos()) != 0) { // Mostrar solo si no es cero
                 addTableCell(totalsTable, "Total Impuestos:", FONT_BOLD, Element.ALIGN_RIGHT, null);
                 addTableCell(totalsTable, priceFormat.format(datosFactura.getTotalImpuestos()), FONT_NORMAL, Element.ALIGN_RIGHT, null);
              }
        }

        // Total Final
        addTableCell(totalsTable, "Total:", FONT_BOLD, Element.ALIGN_RIGHT, null);
        addTableCell(totalsTable, priceFormat.format(datosFactura.getTotalConImpuestos()), FONT_BOLD, Element.ALIGN_RIGHT, null);

        document.add(totalsTable);
    }

    private void addFooter(Document document) throws DocumentException {
        Paragraph footer = new Paragraph("Gracias por su compra!", FONT_NORMAL);
        footer.setAlignment(Element.ALIGN_CENTER);
        document.add(footer);
    }

    // Método helper para añadir celdas a la tabla
    private void addTableCell(PdfPTable table, String text, Font font, int alignment, Color backgroundColor) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(5f);
        if (backgroundColor != null) {
            cell.setBackgroundColor(backgroundColor);
        }
        table.addCell(cell);
    }
} 