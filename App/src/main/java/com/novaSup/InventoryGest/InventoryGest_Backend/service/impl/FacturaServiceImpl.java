package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DatosFiscalesEmisorDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DatosFiscalesFacturaDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DatosFiscalesReceptorDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleImpuestoFacturaTemporalDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.*;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.DetalleImpuestoFacturaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.FacturaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.TipoImpuestoRepository; // Asumiendo que existe este repositorio
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ConfiguracionEmpresaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.FacturaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.FacturaPreviewDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaPreviewInfoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.ClientePreviewInfoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VendedorPreviewInfoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleVentaPreviewDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.ProductoPreviewInfoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleImpuestoFacturaPreviewDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.TipoImpuestoPreviewInfoDTO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;
    private final DetalleImpuestoFacturaRepository detalleImpuestoFacturaRepository;
    private final ConfiguracionEmpresaService configuracionEmpresaService;
    private final TipoImpuestoRepository tipoImpuestoRepository; // Necesario para obtener TipoImpuesto
    private final ObjectMapper objectMapper;

    public FacturaServiceImpl(FacturaRepository facturaRepository,
                              DetalleImpuestoFacturaRepository detalleImpuestoFacturaRepository,
                              ConfiguracionEmpresaService configuracionEmpresaService,
                              TipoImpuestoRepository tipoImpuestoRepository,
                              ObjectMapper objectMapper) {
        this.facturaRepository = facturaRepository;
        this.detalleImpuestoFacturaRepository = detalleImpuestoFacturaRepository;
        this.configuracionEmpresaService = configuracionEmpresaService;
        this.tipoImpuestoRepository = tipoImpuestoRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true) // Marcar como solo lectura ya que es una consulta
    public FacturaPreviewDTO obtenerFacturaPorId(int idFactura) {
        // Usar findById simple ahora que mapeamos a DTO
        Factura factura = facturaRepository.findById(idFactura).orElse(null);

        if (factura == null) {
            return null; // O lanzar una excepción específica de no encontrado
        }

        // Inicializar manualmente las relaciones lazy necesarias antes de mapear a DTO
        // Acceder a los getters fuerza la carga si la entidad está aún en contexto persistencia
        factura.getVenta();
        if (factura.getVenta() != null) {
            factura.getVenta().getCliente();
            factura.getVenta().getVendedor();
            factura.getVenta().getDetallesVenta().size(); // Forzar carga de la colección
            if (factura.getVenta().getDetallesVenta() != null) {
                 for (DetalleVenta dv : factura.getVenta().getDetallesVenta()) {
                     dv.getProducto(); // Forzar carga del Producto en cada detalle
                 }
            }
        }
        factura.getDetallesImpuesto().size(); // Forzar carga de la colección
        if (factura.getDetallesImpuesto() != null) {
            for (DetalleImpuestoFactura dif : factura.getDetallesImpuesto()) {
                dif.getTipoImpuesto(); // Forzar carga del TipoImpuesto en cada detalle
            }
        }

        // Mapear entidad Factura a FacturaPreviewDTO
        FacturaPreviewDTO facturaDTO = new FacturaPreviewDTO();
        facturaDTO.setIdFactura(factura.getIdFactura());
        facturaDTO.setNumeroFactura(factura.getNumeroFactura());
        facturaDTO.setFechaEmision(factura.getFechaEmision());
        facturaDTO.setSubtotal(factura.getSubtotal());
        facturaDTO.setTotalImpuestos(factura.getTotalImpuestos());
        facturaDTO.setTotalConImpuestos(factura.getTotalConImpuestos());
        // Asumiendo que DatosFiscalesFacturaDTO se puede construir del JSON o ya es el tipo correcto
        try {
             facturaDTO.setDatosFiscales(objectMapper.readValue(factura.getDatosFiscales(), DatosFiscalesFacturaDTO.class));
        } catch (JsonProcessingException e) {
            // Manejar error de serialización/deserialización si DatosFiscales no es válido
            // Podrías loggearlo o lanzar una excepción personalizada
            e.printStackTrace(); // O log más apropiado
            // Dependiendo del requerimiento, podrías establecer datosFiscales a null en el DTO
             facturaDTO.setDatosFiscales(null); // O manejar de otra forma
        }
        facturaDTO.setEstado(factura.getEstado());

        // Mapear Venta a VentaPreviewInfoDTO
        if (factura.getVenta() != null) {
            Venta venta = factura.getVenta();
            VentaPreviewInfoDTO ventaDTO = new VentaPreviewInfoDTO();
            ventaDTO.setIdVenta(venta.getIdVenta());

            // Mapear Cliente a ClientePreviewInfoDTO
            if (venta.getCliente() != null) {
                Cliente cliente = venta.getCliente();
                ventaDTO.setCliente(new ClientePreviewInfoDTO(
                        cliente.getIdCliente(),
                        cliente.getNombre(),
                        cliente.getRazonSocial(),
                        cliente.getIdentificacionFiscal(),
                        cliente.getDireccion(),
                        cliente.getDireccionFacturacion()
                        // Mapear otros campos necesarios del cliente
                ));
            }

            // Mapear Vendedor a VendedorPreviewInfoDTO
            if (venta.getVendedor() != null) {
                Vendedor vendedor = venta.getVendedor();
                ventaDTO.setVendedor(new VendedorPreviewInfoDTO(
                        vendedor.getIdVendedor(),
                        vendedor.getUsuario().getNombre() // O el campo nombre del vendedor
                        // Mapear otros campos necesarios del vendedor
                ));
            }

            // Mapear DetallesVenta a List<DetalleVentaPreviewDTO>
            if (venta.getDetallesVenta() != null) {
                List<DetalleVentaPreviewDTO> detallesVentaDTO = new ArrayList<>();
                for (DetalleVenta detalleVenta : venta.getDetallesVenta()) {
                    ProductoPreviewInfoDTO productoDTO = null;
                    if (detalleVenta.getProducto() != null) {
                        Producto producto = detalleVenta.getProducto();
                        productoDTO = new ProductoPreviewInfoDTO(
                                producto.getIdProducto(),
                                producto.getNombre(),
                                producto.getCodigo(),
                                producto.getPrecioVenta()
                                // Mapear otros campos necesarios del producto
                        );
                    }
                    // Mapear DetalleVenta a DetalleVentaPreviewDTO
                    detallesVentaDTO.add(new DetalleVentaPreviewDTO(
                            detalleVenta.getIdDetalle(), // Integer (según entidad y DTO)
                            productoDTO,
                            detalleVenta.getCantidad(), // Integer (según entidad y DTO)
                            detalleVenta.getPrecioUnitarioFinal(), // BigDecimal (según entidad y DTO)
                            detalleVenta.getSubtotal() // BigDecimal (según entidad y DTO)
                            // Mapear otros campos necesarios del detalle de venta
                    ));
                }
                ventaDTO.setDetallesVenta(detallesVentaDTO);
            }

            facturaDTO.setVentaInfo(ventaDTO);
        }

        // Mapear DetallesImpuesto a List<DetalleImpuestoFacturaPreviewDTO>
        if (factura.getDetallesImpuesto() != null) {
            List<DetalleImpuestoFacturaPreviewDTO> detallesImpuestoDTO = new ArrayList<>();
            for (DetalleImpuestoFactura detalleImpuesto : factura.getDetallesImpuesto()) {
                 TipoImpuestoPreviewInfoDTO tipoImpuestoDTO = null;
                 if (detalleImpuesto.getTipoImpuesto() != null) {
                     TipoImpuesto tipoImpuesto = detalleImpuesto.getTipoImpuesto();
                     tipoImpuestoDTO = new TipoImpuestoPreviewInfoDTO(
                         tipoImpuesto.getIdTipoImpuesto(), // int (según entidad y DTO)
                         tipoImpuesto.getNombre() // String (según entidad y DTO)
                         // Mapear otros campos necesarios del tipo de impuesto
                     );
                 }
                 // Mapear DetalleImpuestoFactura a DetalleImpuestoFacturaPreviewDTO
                detallesImpuestoDTO.add(new DetalleImpuestoFacturaPreviewDTO(
                        detalleImpuesto.getIdDetalle(), // Long (según entidad y DTO)
                        tipoImpuestoDTO,
                        detalleImpuesto.getBaseImponible(), // BigDecimal (según entidad y DTO)
                        detalleImpuesto.getTasaAplicada(), // BigDecimal (según entidad y DTO)
                        detalleImpuesto.getMontoImpuesto() // BigDecimal (según entidad y DTO)
                        // Mapear otros campos necesarios del detalle de impuesto
                ));
            }
            facturaDTO.setDetallesImpuesto(detallesImpuestoDTO);
        }

        return facturaDTO;
    }

    @Override
    @Transactional
    public Factura generarFactura(Venta ventaGuardada, List<DetalleImpuestoFacturaTemporalDTO> desgloseImpuestos) {
        Factura nuevaFactura = new Factura();

        // 1. Generar Número de Factura
        String numeroFactura = "F-" + ventaGuardada.getIdVenta() + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        nuevaFactura.setNumeroFactura(numeroFactura);

        // 2. Poblar Entidad Factura
        nuevaFactura.setVenta(ventaGuardada);
        nuevaFactura.setFechaEmision(LocalDateTime.now());
        nuevaFactura.setSubtotal(ventaGuardada.getSubtotal());
        nuevaFactura.setTotalImpuestos(ventaGuardada.getTotalImpuestos());
        nuevaFactura.setTotalConImpuestos(ventaGuardada.getTotalConImpuestos());
        nuevaFactura.setEstado("EMITIDA");

        // 3. Construir datos_fiscales JSON
        DatosFiscalesEmisorDTO emisorDTO = configuracionEmpresaService.obtenerDatosFiscalesEmisor();
        DatosFiscalesReceptorDTO receptorDTO;
        Cliente cliente = ventaGuardada.getCliente();
        //TODO: EL_ID_FISCAL_PUBLICO_GENERAL DEBE SER MODIFICABLE EN LA CONFIGURACION DE LA EMPRESA
        // Definir el identificador para "Público en General" o "Consumidor Final"
        // Este valor podría ser específico para Colombia (ej. "222222222222")
        // o un estándar si tu sistema debe ser más genérico.
        // Por ahora, usaremos un placeholder o un valor común si no hay cliente específico.
        final String ID_FISCAL_PUBLICO_GENERAL = "222222222222"; // Ejemplo para Colombia, ajustar según necesidad.

        // Determinar si se usan datos específicos del cliente o público en general
        if (cliente != null && 
            cliente.getIdentificacionFiscal() != null && 
            !cliente.getIdentificacionFiscal().trim().isEmpty() &&
            !cliente.getIdentificacionFiscal().trim().equalsIgnoreCase(ID_FISCAL_PUBLICO_GENERAL)) { // Comparar con el ID definido
            
            // Usar los datos fiscales específicos del cliente
            String razonSocialReceptor = (cliente.getRazonSocial() != null && !cliente.getRazonSocial().trim().isEmpty()) 
                                         ? cliente.getRazonSocial() 
                                         : cliente.getNombre(); // Fallback al nombre general si no hay razón social fiscal

            String direccionReceptor = (cliente.getDireccionFacturacion() != null && !cliente.getDireccionFacturacion().trim().isEmpty())
                                       ? cliente.getDireccionFacturacion()
                                       : cliente.getDireccion(); // Fallback a la dirección general
            
            String tipoFacturaCliente = (cliente.getTipoFacturaDefault() != null && !cliente.getTipoFacturaDefault().trim().isEmpty())
                                    ? cliente.getTipoFacturaDefault()
                                    : "P01"; // P01: Por definir (o el código de uso fiscal que aplique)

            receptorDTO = new DatosFiscalesReceptorDTO(
                    razonSocialReceptor,
                    cliente.getIdentificacionFiscal(),
                    direccionReceptor,
                    tipoFacturaCliente
            );
        } else {
            // Usar datos de PÚBLICO EN GENERAL
            receptorDTO = new DatosFiscalesReceptorDTO(
                    "PÚBLICO EN GENERAL", // O "CONSUMIDOR FINAL" para Colombia
                    ID_FISCAL_PUBLICO_GENERAL,
                    emisorDTO.getDireccionFacturacion(), // Considerar un domicilio genérico para público en general si es diferente al del emisor
                    "S01" // Uso CFDI: Sin efectos fiscales (o el código de uso que aplique)
            );
        }

        DatosFiscalesFacturaDTO datosFiscalesFacturaDTO = new DatosFiscalesFacturaDTO(emisorDTO, receptorDTO);
        try {
            String datosFiscalesJson = objectMapper.writeValueAsString(datosFiscalesFacturaDTO);
            nuevaFactura.setDatosFiscales(datosFiscalesJson);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error al serializar datos fiscales a JSON", e);
        }

        // 4. Guardar Factura principal
        Factura facturaGuardada = facturaRepository.save(nuevaFactura);

        // 5. Guardar DetalleImpuestoFactura
        if (desgloseImpuestos != null && !desgloseImpuestos.isEmpty()) {
            List<DetalleImpuestoFactura> detallesAGuardar = new ArrayList<>();
            for (DetalleImpuestoFacturaTemporalDTO detalleDTO : desgloseImpuestos) {
                DetalleImpuestoFactura detalleFactura = new DetalleImpuestoFactura();
                detalleFactura.setFactura(facturaGuardada);

                TipoImpuesto tipoImpuesto = tipoImpuestoRepository.findById(detalleDTO.getIdTipoImpuesto())
                        .orElseThrow(() -> new RuntimeException("Tipo de impuesto no encontrado con ID: " + detalleDTO.getIdTipoImpuesto()));
                detalleFactura.setTipoImpuesto(tipoImpuesto);

                detalleFactura.setBaseImponible(detalleDTO.getBaseImponible());
                detalleFactura.setTasaAplicada(detalleDTO.getTasaAplicada());
                detalleFactura.setMontoImpuesto(detalleDTO.getMontoImpuesto());
                detallesAGuardar.add(detalleFactura);
            }
            detalleImpuestoFacturaRepository.saveAll(detallesAGuardar);
            facturaGuardada.setDetallesImpuesto(detallesAGuardar);
        }

        return facturaGuardada;
    }
} 