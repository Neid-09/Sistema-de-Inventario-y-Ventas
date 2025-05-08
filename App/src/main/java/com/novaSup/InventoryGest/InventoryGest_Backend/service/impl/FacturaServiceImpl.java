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
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ClienteService; // Inyectado según especificación
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ConfiguracionEmpresaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.FacturaService;
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
    private final ClienteService clienteService; // Aunque no se use directamente, se inyecta según lo pedido
    private final TipoImpuestoRepository tipoImpuestoRepository; // Necesario para obtener TipoImpuesto
    private final ObjectMapper objectMapper;

    public FacturaServiceImpl(FacturaRepository facturaRepository,
                              DetalleImpuestoFacturaRepository detalleImpuestoFacturaRepository,
                              ConfiguracionEmpresaService configuracionEmpresaService,
                              ClienteService clienteService,
                              TipoImpuestoRepository tipoImpuestoRepository,
                              ObjectMapper objectMapper) {
        this.facturaRepository = facturaRepository;
        this.detalleImpuestoFacturaRepository = detalleImpuestoFacturaRepository;
        this.configuracionEmpresaService = configuracionEmpresaService;
        this.clienteService = clienteService;
        this.tipoImpuestoRepository = tipoImpuestoRepository;
        this.objectMapper = objectMapper;
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

        String rfcPublicoGeneral = configuracionEmpresaService.obtenerRfcPublicoGeneral();

        // Determinar si es público en general o un cliente específico con datos fiscales
        boolean usarDatosPublicoGeneral = cliente == null || 
                                          cliente.getRfcFiscal() == null || 
                                          cliente.getRfcFiscal().trim().isEmpty() || 
                                          cliente.getRfcFiscal().trim().equalsIgnoreCase(rfcPublicoGeneral);

        if (usarDatosPublicoGeneral) {
            receptorDTO = new DatosFiscalesReceptorDTO(
                    "PÚBLICO EN GENERAL",
                    rfcPublicoGeneral,
                    emisorDTO.getDomicilio(), // Considerar un domicilio genérico para público en general si es diferente al del emisor
                    "S01" // Uso CFDI: Sin efectos fiscales (común para público en general)
            );
        } else {
            // Usar los datos fiscales específicos del cliente
            String razonSocialReceptor = (cliente.getRazonSocialFiscal() != null && !cliente.getRazonSocialFiscal().trim().isEmpty()) 
                                         ? cliente.getRazonSocialFiscal() 
                                         : cliente.getNombre(); // Fallback al nombre general si no hay razón social fiscal

            String direccionReceptor = (cliente.getDireccionFiscal() != null && !cliente.getDireccionFiscal().trim().isEmpty())
                                       ? cliente.getDireccionFiscal()
                                       : cliente.getDireccion(); // Fallback a la dirección general
            
            String usoCfdiCliente = (cliente.getUsoCfdiDefault() != null && !cliente.getUsoCfdiDefault().trim().isEmpty())
                                    ? cliente.getUsoCfdiDefault()
                                    : "P01"; // P01: Por definir (default si no se especifica en cliente)

            receptorDTO = new DatosFiscalesReceptorDTO(
                    razonSocialReceptor,
                    cliente.getRfcFiscal(),
                    direccionReceptor,
                    usoCfdiCliente
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