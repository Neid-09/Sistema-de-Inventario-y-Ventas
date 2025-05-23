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
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ClienteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.CalculoImpuestoService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.VendedorService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.PromocionService;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.FacturaPreviewDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaPreviewInfoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.ClientePreviewInfoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VendedorPreviewInfoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleVentaPreviewDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.ProductoPreviewInfoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleImpuestoFacturaPreviewDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.TipoImpuestoPreviewInfoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaRequestDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.ResultadoCalculoImpuestosDTO;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.Optional;

@Service
public class FacturaServiceImpl implements FacturaService {

    private final FacturaRepository facturaRepository;
    private final DetalleImpuestoFacturaRepository detalleImpuestoFacturaRepository;
    private final ConfiguracionEmpresaService configuracionEmpresaService;
    private final TipoImpuestoRepository tipoImpuestoRepository; // Necesario para obtener TipoImpuesto
    private final ObjectMapper objectMapper;
    private final ProductoService productoService;
    private final ClienteService clienteService;
    private final CalculoImpuestoService calculoImpuestoService;
    private final VendedorService vendedorService;
    private final PromocionService promocionService;

    public FacturaServiceImpl(FacturaRepository facturaRepository,
                              DetalleImpuestoFacturaRepository detalleImpuestoFacturaRepository,
                              ConfiguracionEmpresaService configuracionEmpresaService,
                              TipoImpuestoRepository tipoImpuestoRepository,
                              ObjectMapper objectMapper,
                              ProductoService productoService,
                              ClienteService clienteService,
                              CalculoImpuestoService calculoImpuestoService,
                              VendedorService vendedorService,
                              PromocionService promocionService) {
        this.facturaRepository = facturaRepository;
        this.detalleImpuestoFacturaRepository = detalleImpuestoFacturaRepository;
        this.configuracionEmpresaService = configuracionEmpresaService;
        this.tipoImpuestoRepository = tipoImpuestoRepository;
        this.objectMapper = objectMapper;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.calculoImpuestoService = calculoImpuestoService;
        this.vendedorService = vendedorService;
        this.promocionService = promocionService;
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
            !cliente.getIdentificacionFiscal().trim().equalsIgnoreCase(ID_FISCAL_PUBLICO_GENERAL)) {

            String razonSocialReceptor = (cliente.getRazonSocial() != null && !cliente.getRazonSocial().trim().isEmpty())
                                         ? cliente.getRazonSocial()
                                         : cliente.getNombre();

            String direccionReceptor = (cliente.getDireccionFacturacion() != null && !cliente.getDireccionFacturacion().trim().isEmpty())
                                       ? cliente.getDireccionFacturacion()
                                       : cliente.getDireccion();

            String tipoFacturaCliente = (cliente.getTipoFacturaDefault() != null && !cliente.getTipoFacturaDefault().trim().isEmpty())
                                    ? cliente.getTipoFacturaDefault()
                                    : "P01"; // P01: Por definir

            receptorDTO = new DatosFiscalesReceptorDTO(
                    razonSocialReceptor,
                    cliente.getIdentificacionFiscal(),
                    direccionReceptor,
                    tipoFacturaCliente
            );
        } else {
            receptorDTO = new DatosFiscalesReceptorDTO(
                    "PÚBLICO EN GENERAL",
                    ID_FISCAL_PUBLICO_GENERAL,
                    emisorDTO.getDireccionFacturacion(),
                    "S01"
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

    @Override
    @Transactional(readOnly = true) // No se persiste nada en la previsualización
    public FacturaPreviewDTO previewFactura(VentaRequestDTO ventaRequest) throws Exception {
        // Validar datos de entrada (similar a VentaServiceImpl, pero simplificado)
        if (ventaRequest == null) {
            throw new IllegalArgumentException("La solicitud de venta para previsualización no puede ser nula.");
        }
        if (ventaRequest.getDetalles() == null || ventaRequest.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta para previsualización debe tener al menos un detalle.");
        }

        // Mapeo básico a un VentaPreviewInfoDTO temporal para el preview
        VentaPreviewInfoDTO ventaInfoDTO = new VentaPreviewInfoDTO();
        // No hay ID de venta ni fecha real en la previsualización
        // ventaInfoDTO.setIdVenta(null);
        // ventaInfoDTO.setFecha(null); // La fecha de emisión se generará en el FacturaPreviewDTO

        // Obtener y mapear Cliente (si existe)
        ClientePreviewInfoDTO clienteInfoDTO = null;
        Cliente cliente = null; // Usaremos esta variable para los datos fiscales
        if (ventaRequest.getIdCliente() != null) {
            cliente = clienteService.obtenerClientePorId(ventaRequest.getIdCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado para previsualización: " + ventaRequest.getIdCliente()));
            clienteInfoDTO = new ClientePreviewInfoDTO(
                cliente.getIdCliente(),
                cliente.getNombre(),
                cliente.getRazonSocial(),
                cliente.getIdentificacionFiscal(),
                cliente.getDireccion(),
                cliente.getDireccionFacturacion()
            );
        } else {
             // Si no se proporciona idCliente, se busca el cliente "Venta General"
            final String NOMBRE_CLIENTE_GENERAL = "Venta General";
            cliente = clienteService.obtenerClientePorNombre(NOMBRE_CLIENTE_GENERAL)
                    .orElseThrow(() -> new EntityNotFoundException("Cliente '" + NOMBRE_CLIENTE_GENERAL + "' no encontrado. Asegúrese de que exista este cliente en la base de datos para ventas sin cliente específico."));
             clienteInfoDTO = new ClientePreviewInfoDTO(
                cliente.getIdCliente(),
                cliente.getNombre(),
                cliente.getRazonSocial(), // Puede ser null
                cliente.getIdentificacionFiscal(), // Puede ser null/vacío
                cliente.getDireccion(), // Puede ser null
                cliente.getDireccionFacturacion() // Puede ser null
            );
        }
        ventaInfoDTO.setCliente(clienteInfoDTO);

        // Obtener y mapear Vendedor (si existe)
        VendedorPreviewInfoDTO vendedorInfoDTO = null;
        if (ventaRequest.getIdVendedor() != null) {
            Vendedor vendedor = vendedorService.obtenerVendedorConUsuario(ventaRequest.getIdVendedor())
                 .orElseThrow(() -> new EntityNotFoundException("Vendedor no encontrado o sin usuario activo para previsualización: " + ventaRequest.getIdVendedor()));
            vendedorInfoDTO = new VendedorPreviewInfoDTO(
                 vendedor.getIdVendedor(),
                 vendedor.getUsuario().getNombre()
            );
        }
        ventaInfoDTO.setVendedor(vendedorInfoDTO);

        // Procesar detalles, calcular subtotal general y detalles de impuestos temporales
        BigDecimal subtotalGeneralVenta = BigDecimal.ZERO;
        List<DetalleVentaPreviewDTO> detallesVentaPreview = new ArrayList<>();
        // Necesitamos una lista temporal de algo parecido a DetalleVenta para el cálculo de impuestos
        List<DetalleVenta> detallesTemporalesParaCalculoImpuestos = new ArrayList<>();

        for (VentaRequestDTO.DetalleVentaDTO detalleDTO : ventaRequest.getDetalles()) {
            Producto producto = productoService.obtenerPorId(detalleDTO.getIdProducto())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado para previsualización: " + detalleDTO.getIdProducto()));

            if (!producto.getEstado()) {
                 throw new IllegalStateException("El producto '" + producto.getNombre() + "' está inactivo y no se puede previsualizar su venta.");
            }

            BigDecimal precioVentaOriginal = producto.getPrecioVenta(); // O usar precioUnitarioFinal del DTO si existe
            BigDecimal precioFinalUnitario = precioVentaOriginal;

            Integer idCategoria = (producto.getCategoria() != null) ? producto.getCategoria().getIdCategoria() : null;

            Optional<Promocion> promocionOpt = promocionService.buscarPromocionAplicable(
                    producto.getIdProducto(),
                    idCategoria,
                    java.time.LocalDate.now() // Usar la fecha actual para buscar promociones
            );

            if (promocionOpt.isPresent()) {
                Promocion promocionEncontrada = promocionOpt.get();
                precioFinalUnitario = promocionService.aplicarDescuento(precioVentaOriginal, promocionEncontrada);
            }

            BigDecimal subtotalDetalle = precioFinalUnitario.multiply(new BigDecimal(detalleDTO.getCantidad()));
            subtotalGeneralVenta = subtotalGeneralVenta.add(subtotalDetalle);

            // Crear DetalleVentaPreviewDTO
            ProductoPreviewInfoDTO productoPreview = new ProductoPreviewInfoDTO(
                 producto.getIdProducto(),
                 producto.getNombre(),
                 producto.getCodigo(),
                 precioFinalUnitario // Usar el precio final calculado con promociones
            );
            detallesVentaPreview.add(new DetalleVentaPreviewDTO(
                 null, // No hay ID de detalle en la previsualización
                 productoPreview,
                 detalleDTO.getCantidad(),
                 precioFinalUnitario, // Usar el precio final calculado
                 subtotalDetalle
            ));

            // Crear DetalleVenta temporal para el cálculo de impuestos
            // Solo necesitamos los campos relevantes para CalculoImpuestoService
            DetalleVenta detalleTemp = new DetalleVenta();
            detalleTemp.setProducto(producto); // Necesario para obtener tasas de impuesto
            detalleTemp.setCantidad(detalleDTO.getCantidad());
            detalleTemp.setPrecioUnitarioFinal(precioFinalUnitario);
            detalleTemp.setSubtotal(subtotalDetalle); // Usar el subtotal calculado
            detallesTemporalesParaCalculoImpuestos.add(detalleTemp);
        }

        ventaInfoDTO.setDetallesVenta(detallesVentaPreview);

        // Calcular impuestos
        BigDecimal totalImpuestosCalculado = BigDecimal.ZERO;
        List<DetalleImpuestoFacturaPreviewDTO> detallesImpuestoPreview = new ArrayList<>();

        if (Boolean.TRUE.equals(ventaRequest.getAplicarImpuestos())) {
            if (calculoImpuestoService == null) {
                 // Esto no debería pasar con la inyección, pero buena práctica validar
                 throw new IllegalStateException("CalculoImpuestoService no ha sido inyectado correctamente y se requieren impuestos para previsualización.");
            }
            // Usar los detalles temporales y una fecha actual para el cálculo
            ResultadoCalculoImpuestosDTO resultadoCalculo = calculoImpuestoService.calcularImpuestosParaVenta(detallesTemporalesParaCalculoImpuestos, new java.util.Date());
            totalImpuestosCalculado = resultadoCalculo.getTotalImpuestosVenta();

            // Mapear DetalleImpuestoFacturaTemporalDTO a DetalleImpuestoFacturaPreviewDTO
            if (resultadoCalculo.getDesgloseImpuestos() != null) {
                 for (DetalleImpuestoFacturaTemporalDTO detalleImpTemp : resultadoCalculo.getDesgloseImpuestos()) {
                      // Necesitamos obtener el TipoImpuesto para el preview DTO
                      TipoImpuesto tipoImpuesto = tipoImpuestoRepository.findById(detalleImpTemp.getIdTipoImpuesto())
                            .orElse(null); // O manejar el error si el tipo de impuesto no existe

                      TipoImpuestoPreviewInfoDTO tipoImpuestoPreview = null;
                      if (tipoImpuesto != null) {
                          tipoImpuestoPreview = new TipoImpuestoPreviewInfoDTO(
                                tipoImpuesto.getIdTipoImpuesto(),
                                tipoImpuesto.getNombre()
                          );
                      }

                      detallesImpuestoPreview.add(new DetalleImpuestoFacturaPreviewDTO(
                            null, // No hay ID de detalle de impuesto en la previsualización
                            tipoImpuestoPreview,
                            detalleImpTemp.getBaseImponible(),
                            detalleImpTemp.getTasaAplicada(),
                            detalleImpTemp.getMontoImpuesto()
                      ));
                 }
            }
        }

        // Construir DatosFiscalesFacturaDTO (replicando lógica de generarFactura)
        DatosFiscalesEmisorDTO emisorDTO = configuracionEmpresaService.obtenerDatosFiscalesEmisor();
        DatosFiscalesReceptorDTO receptorDTO;
        
        // El ID_FISCAL_PUBLICO_GENERAL debería obtenerse de la configuración de la empresa
        // Por ahora, usar el mismo placeholder que en generarFactura
        final String ID_FISCAL_PUBLICO_GENERAL = "222222222222"; // Ejemplo para Colombia

        if (cliente != null &&
            cliente.getIdentificacionFiscal() != null &&
            !cliente.getIdentificacionFiscal().trim().isEmpty() &&
            !cliente.getIdentificacionFiscal().trim().equalsIgnoreCase(ID_FISCAL_PUBLICO_GENERAL)) {

            String razonSocialReceptor = (cliente.getRazonSocial() != null && !cliente.getRazonSocial().trim().isEmpty())
                                         ? cliente.getRazonSocial()
                                         : cliente.getNombre();

            String direccionReceptor = (cliente.getDireccionFacturacion() != null && !cliente.getDireccionFacturacion().trim().isEmpty())
                                       ? cliente.getDireccionFacturacion()
                                       : cliente.getDireccion();

            String tipoFacturaCliente = (cliente.getTipoFacturaDefault() != null && !cliente.getTipoFacturaDefault().trim().isEmpty())
                                    ? cliente.getTipoFacturaDefault()
                                    : "P01"; // P01: Por definir

            receptorDTO = new DatosFiscalesReceptorDTO(
                    razonSocialReceptor,
                    cliente.getIdentificacionFiscal(),
                    direccionReceptor,
                    tipoFacturaCliente
            );
        } else {
            receptorDTO = new DatosFiscalesReceptorDTO(
                    "PÚBLICO EN GENERAL",
                    ID_FISCAL_PUBLICO_GENERAL,
                    emisorDTO.getDireccionFacturacion(),
                    "S01"
            );
        }

        DatosFiscalesFacturaDTO datosFiscalesFacturaDTO = new DatosFiscalesFacturaDTO(emisorDTO, receptorDTO);

        // Construir FacturaPreviewDTO
        FacturaPreviewDTO facturaPreviewDTO = new FacturaPreviewDTO();
        // No hay ID de factura en la previsualización
        // facturaPreviewDTO.setIdFactura(null);
        // No hay número de factura real, podríamos generar uno temporal o dejarlo null
        // facturaPreviewDTO.setNumeroFactura(null);
        facturaPreviewDTO.setFechaEmision(LocalDateTime.now()); // Usar la fecha actual para el preview
        facturaPreviewDTO.setSubtotal(subtotalGeneralVenta.setScale(2, RoundingMode.HALF_UP));
        facturaPreviewDTO.setTotalImpuestos(totalImpuestosCalculado.setScale(2, RoundingMode.HALF_UP));
        facturaPreviewDTO.setTotalConImpuestos(subtotalGeneralVenta.add(totalImpuestosCalculado).setScale(2, RoundingMode.HALF_UP));
        // No hay estado real en la previsualización
        // facturaPreviewDTO.setEstado(null);

        // Los DatosFiscales se setean como el DTO, no como JSON string
        facturaPreviewDTO.setDatosFiscales(datosFiscalesFacturaDTO);

        facturaPreviewDTO.setVentaInfo(ventaInfoDTO); // Setear la info de venta construida
        facturaPreviewDTO.setDetallesImpuesto(detallesImpuestoPreview); // Setear los detalles de impuesto construidos

        return facturaPreviewDTO;
    }
} 