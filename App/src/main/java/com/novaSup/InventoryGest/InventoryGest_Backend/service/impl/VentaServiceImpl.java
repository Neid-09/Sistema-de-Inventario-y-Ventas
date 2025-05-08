package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaRequestDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.*;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.VentaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.*;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.ResultadoCalculoImpuestosDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.FacturaService;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.LoteReducidoInfoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.ResultadoRegistroVentaProductoDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.DetalleVentaLoteUso;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para la gestión de Ventas.
 * Coordina las operaciones de creación de ventas, detalles, actualización de stock y cálculo de comisiones.
 */
@Service
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;

    private final DetalleVentaService detalleVentaService;

    private final ProductoService productoService;

    private final ClienteService clienteService;

    private final ComisionService comisionService;

    private final VendedorService vendedorService;

    // Eliminar si no se usa en otro lugar
    // @Autowired
    // private RegistMovimientService registMovimientService;

    // <--- Inyectar InventarioService
    private final InventarioService inventarioService;
    private final PromocionService promocionService; // Inyectar PromocionService
    private final CalculoImpuestoService calculoImpuestoService;
    private final FacturaService facturaService;

    public VentaServiceImpl(VentaRepository ventaRepository, DetalleVentaService detalleVentaService, ProductoService productoService, ClienteService clienteService, ComisionService comisionService, VendedorService vendedorService, InventarioService inventarioService, PromocionService promocionService, CalculoImpuestoService calculoImpuestoService, FacturaService facturaService) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaService = detalleVentaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.comisionService = comisionService;
        this.vendedorService = vendedorService;
        this.inventarioService = inventarioService;
        this.promocionService = promocionService;
        this.calculoImpuestoService = calculoImpuestoService;
        this.facturaService = facturaService;
    }

    @Override
    @Transactional // Toda la operación es una única transacción
    public Venta registrarVentaCompleta(VentaRequestDTO ventaRequest) throws Exception {
        // 1. Validar datos de entrada
        validarRequest(ventaRequest);

        // 2. Crear la entidad Venta (cabecera)
        Venta venta = new Venta();
        // Asignar cliente y vendedor si los IDs están presentes
        if (ventaRequest.getIdCliente() != null) {
            Cliente cliente = clienteService.obtenerClientePorId(ventaRequest.getIdCliente())
                .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado: " + ventaRequest.getIdCliente()));
            venta.setCliente(cliente);
        }
        if (ventaRequest.getIdVendedor() != null) {
            Vendedor vendedor = vendedorService.obtenerVendedorConUsuario(ventaRequest.getIdVendedor())
                .orElseThrow(() -> new EntityNotFoundException("Vendedor no encontrado o sin usuario activo: " + ventaRequest.getIdVendedor()));
            venta.setVendedor(vendedor);
        }
        
        venta.setFecha(Timestamp.from(Instant.now()));
        venta.setRequiereFactura(ventaRequest.getRequiereFactura());
        venta.setAplicarImpuestos(ventaRequest.getAplicarImpuestos());
        venta.setNumeroVenta(ventaRequest.getNumeroVenta());
        venta.setTipoPago(ventaRequest.getTipoPago());

        // 3. Procesar detalles y calcular subtotal general
        BigDecimal subtotalGeneralVenta = BigDecimal.ZERO;
        List<DetalleVenta> detallesParaGuardar = new ArrayList<>();

        for (VentaRequestDTO.DetalleVentaDTO detalleDTO : ventaRequest.getDetalles()) {
            Producto producto = productoService.obtenerPorId(detalleDTO.getIdProducto())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + detalleDTO.getIdProducto()));

            if (!producto.getEstado()) {
                 throw new IllegalStateException("El producto '" + producto.getNombre() + "' está inactivo y no se puede vender.");
            }

            BigDecimal precioVentaOriginal = producto.getPrecioVenta();
            BigDecimal precioFinalUnitario = precioVentaOriginal;
            Integer idPromocionAplicada = null;

            Integer idCategoria = (producto.getCategoria() != null) ? producto.getCategoria().getIdCategoria() : null;

            Optional<Promocion> promocionOpt = promocionService.buscarPromocionAplicable(
                    producto.getIdProducto(),
                    idCategoria,
                    java.time.LocalDate.now()
            );

            if (promocionOpt.isPresent()) {
                Promocion promocionEncontrada = promocionOpt.get();
                precioFinalUnitario = promocionService.aplicarDescuento(precioVentaOriginal, promocionEncontrada);
                idPromocionAplicada = promocionEncontrada.getIdPromocion();
            }

            // --- Inicio: Lógica de Inventario Centralizada ---
            // 1. Registrar la salida de inventario (esto reduce lotes y crea movimiento)
            String motivoVenta = "Venta " + (venta.getNumeroVenta() != null ? venta.getNumeroVenta() : "[Pendiente]");
            // La validación de stock suficiente ahora ocurre dentro de inventarioService.registrarVentaProducto
            ResultadoRegistroVentaProductoDTO resultadoInventario = inventarioService.registrarVentaProducto(
                producto, 
                detalleDTO.getCantidad(), 
                precioFinalUnitario, 
                motivoVenta
            );

            // 2. Obtener el lote específico para asociar al detalle (después de la reducción)
            // Buscamos el lote más próximo a vencer que aún podría tener stock o del que se acaba de descontar.
            // Esta lógica podría necesitar refinamiento dependiendo de si necesitas el lote *antes* o *después* de descontar.
            // Asumimos que queremos el lote más próximo a vencer que fue afectado o aún tiene stock.
            // List<Lote> lotesActivosProducto = loteService.obtenerPorProducto(producto.getIdProducto()); 
            // Lote loteParaDetalle = lotesActivosProducto.stream()
            //                           .findFirst()
            //                           .orElse(null); 
            // --- Fin: Lógica de Inventario Centralizada ---

            // Crear DetalleVenta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            // detalle.setLote(loteParaDetalle); // LÍNEA A ELIMINAR
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitarioOriginal(precioVentaOriginal);
            detalle.setPrecioUnitarioFinal(precioFinalUnitario);
            detalle.setIdPromocionAplicada(idPromocionAplicada);

            BigDecimal subtotal = precioFinalUnitario.multiply(new BigDecimal(detalleDTO.getCantidad()));
            detalle.setSubtotal(subtotal);

            // Calcular ganancia (opcional)
            BigDecimal costoUnitario = producto.getPrecioCosto();
            if (costoUnitario == null) {
                throw new IllegalStateException("El producto '" + producto.getNombre() + "' (ID: " + producto.getIdProducto() + ") no tiene un precio de costo definido. No se puede calcular la ganancia.");
            }
            detalle.setCostoUnitarioProducto(costoUnitario);
            BigDecimal gananciaDetalle = precioFinalUnitario.subtract(costoUnitario).multiply(new BigDecimal(detalleDTO.getCantidad()));
            detalle.setGananciaDetalle(gananciaDetalle);

            // NUEVA LÓGICA: Asociar los lotes usados al detalleVenta
            if (resultadoInventario != null && resultadoInventario.getLotesAfectados() != null) {
                for (LoteReducidoInfoDTO loteInfo : resultadoInventario.getLotesAfectados()) {
                    DetalleVentaLoteUso dvu = new DetalleVentaLoteUso();
                    dvu.setDetalleVenta(detalle); // Se asocia aquí, antes de guardar el detalle
                    dvu.setLote(loteInfo.getLote());
                    dvu.setCantidadTomada(loteInfo.getCantidadTomada());
                    detalle.getDetalleLotesUsados().add(dvu);
                }
            }

            detallesParaGuardar.add(detalle);
            subtotalGeneralVenta = subtotalGeneralVenta.add(subtotal);
        }

        // 4. Calcular impuestos y establecer totales en la venta
        BigDecimal totalImpuestosCalculado = BigDecimal.ZERO;
        List<com.novaSup.InventoryGest.InventoryGest_Backend.dto.DetalleImpuestoFacturaTemporalDTO> desgloseImpuestosParaFactura = new ArrayList<>();

        if (Boolean.TRUE.equals(venta.getAplicarImpuestos())) {
            if (calculoImpuestoService == null) {
                 throw new IllegalStateException("CalculoImpuestoService no ha sido inyectado correctamente y se requieren impuestos.");
            }
            ResultadoCalculoImpuestosDTO resultadoCalculo = calculoImpuestoService.calcularImpuestosParaVenta(detallesParaGuardar, new Date(venta.getFecha().getTime()));
            totalImpuestosCalculado = resultadoCalculo.getTotalImpuestosVenta();
            desgloseImpuestosParaFactura = resultadoCalculo.getDesgloseImpuestos();
        }

        venta.setSubtotal(subtotalGeneralVenta.setScale(2, RoundingMode.HALF_UP));
        venta.setTotalImpuestos(totalImpuestosCalculado.setScale(2, RoundingMode.HALF_UP));
        venta.setTotalConImpuestos(subtotalGeneralVenta.add(totalImpuestosCalculado).setScale(2, RoundingMode.HALF_UP));

        // Guardar cabecera de Venta
        Venta ventaGuardada = ventaRepository.save(venta);

        // 5. Asociar la venta guardada a los detalles y guardarlos
        for (DetalleVenta detalle : detallesParaGuardar) {
            detalle.setVenta(ventaGuardada);
        }
        detalleVentaService.guardarDetalles(detallesParaGuardar);

        // ****** INICIO: LÓGICA DE FACTURACIÓN ******
        if (Boolean.TRUE.equals(ventaGuardada.getRequiereFactura())) {
            if (facturaService == null) {
                throw new IllegalStateException("FacturaService no ha sido inyectado correctamente y se requiere factura.");
            }
            try {
                facturaService.generarFactura(ventaGuardada, desgloseImpuestosParaFactura);
            } catch (Exception e) {
                throw new RuntimeException("Error durante la generación de la factura para la venta " + ventaGuardada.getIdVenta(), e);
            }
        }
        // ****** FIN: LÓGICA DE FACTURACIÓN ******

        // 6. Actualizar datos del cliente (si aplica)
        if (ventaGuardada.getCliente() != null) {
            clienteService.actualizarTotalComprado(ventaGuardada.getCliente().getIdCliente(), ventaGuardada.getTotalConImpuestos());
        }

        // 7. Calcular y guardar comisión
        comisionService.calcularYGuardarComision(ventaGuardada);

        // 8. Devolver la venta completa RECARGADA
        return ventaRepository.findWithDetailsById(ventaGuardada.getIdVenta())
                .orElseThrow(() -> new EntityNotFoundException("Error al recargar la venta recién creada: " + ventaGuardada.getIdVenta()));
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> obtenerVentaPorId(Integer id) {
        // Usar el método con JOIN FETCH
        return ventaRepository.findWithDetailsById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        // Usar el método con JOIN FETCH
        return ventaRepository.findAllWithDetails();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> obtenerVentasPorCliente(Integer idCliente) {
        // Usar el método con JOIN FETCH
        return ventaRepository.findByIdClienteWithDetails(idCliente);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> obtenerVentasPorVendedor(Integer idVendedor) {
        // Usar el método con JOIN FETCH
        return ventaRepository.findByIdVendedorWithDetails(idVendedor);
    }

    // --- Métodos privados de validación ---

    private void validarRequest(VentaRequestDTO request) throws Exception {
        if (request.getIdVendedor() == null) {
            throw new IllegalArgumentException("El ID del vendedor es obligatorio.");
        }
        // Validar que el vendedor exista y su usuario asociado esté activo
        Vendedor vendedor = vendedorService.obtenerVendedorConUsuario(request.getIdVendedor())
                 .orElseThrow(() -> new EntityNotFoundException("Vendedor no encontrado con ID: " + request.getIdVendedor()));

        // Ahora valida el estado del Usuario asociado al Vendedor
        if (vendedor.getUsuario() == null) {
             throw new IllegalStateException("El vendedor ID " + request.getIdVendedor() + " no tiene un usuario asociado.");
        }
        if (!Boolean.TRUE.equals(vendedor.getUsuario().getEstado())) {
             throw new IllegalStateException("El usuario asociado al vendedor ID " + request.getIdVendedor() + " no está activo.");
        }

        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un detalle.");
        }
        for (VentaRequestDTO.DetalleVentaDTO detalle : request.getDetalles()) {
            if (detalle.getIdProducto() == null || detalle.getCantidad() == null || detalle.getCantidad() <= 0) {
                throw new IllegalArgumentException("Datos inválidos (ID Producto o Cantidad) en el detalle del producto ID: " + detalle.getIdProducto());
            }
        }
        // Validar cliente si se proporciona ID
        if (request.getIdCliente() != null) {
             clienteService.obtenerClientePorId(request.getIdCliente())
                     .orElseThrow(() -> new EntityNotFoundException("Cliente no encontrado: " + request.getIdCliente()));
        }
    }

}