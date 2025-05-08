package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaRequestDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.*;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.VentaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.*;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.ArrayList;
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

    private final LoteService loteService; // Necesario para obtener el lote para el detalle

    // Eliminar si no se usa en otro lugar
    // @Autowired
    // private RegistMovimientService registMovimientService;

    // <--- Inyectar InventarioService
    private final InventarioService inventarioService;
    private final PromocionService promocionService; // Inyectar PromocionService

    public VentaServiceImpl(VentaRepository ventaRepository, DetalleVentaService detalleVentaService, ProductoService productoService, ClienteService clienteService, ComisionService comisionService, VendedorService vendedorService, LoteService loteService, InventarioService inventarioService, PromocionService promocionService) {
        this.ventaRepository = ventaRepository;
        this.detalleVentaService = detalleVentaService;
        this.productoService = productoService;
        this.clienteService = clienteService;
        this.comisionService = comisionService;
        this.vendedorService = vendedorService;
        this.loteService = loteService;
        this.inventarioService = inventarioService;
        this.promocionService = promocionService; // Añadir al constructor
    }

    @Override
    @Transactional // Toda la operación es una única transacción
    public Venta registrarVentaCompleta(VentaRequestDTO ventaRequest) throws Exception {
        // 1. Validar datos de entrada
        validarRequest(ventaRequest);

        // 2. Crear la entidad Venta (cabecera)
        Venta venta = new Venta();
        // ... (asignar datos a la venta)
        venta.setFecha(Timestamp.from(Instant.now()));
        venta.setIdCliente(ventaRequest.getIdCliente());
        venta.setIdVendedor(ventaRequest.getIdVendedor());
        venta.setRequiereFactura(ventaRequest.getRequiereFactura());
        venta.setAplicarImpuestos(ventaRequest.getAplicarImpuestos());
        venta.setNumeroVenta(ventaRequest.getNumeroVenta());
        venta.setTipoPago(ventaRequest.getTipoPago());

        // 3. Procesar detalles y calcular total
        BigDecimal totalVenta = BigDecimal.ZERO;
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
            inventarioService.registrarVentaProducto(producto, detalleDTO.getCantidad(), precioFinalUnitario, motivoVenta);

            // 2. Obtener el lote específico para asociar al detalle (después de la reducción)
            // Buscamos el lote más próximo a vencer que aún podría tener stock o del que se acaba de descontar.
            // Esta lógica podría necesitar refinamiento dependiendo de si necesitas el lote *antes* o *después* de descontar.
            // Asumimos que queremos el lote más próximo a vencer que fue afectado o aún tiene stock.
            List<Lote> lotesActivosProducto = loteService.obtenerPorProducto(producto.getIdProducto()); // Ordenados por fecha_vencimiento ASC
            Lote loteParaDetalle = lotesActivosProducto.stream()
                                      // .filter(l -> l.getCantidad() >= 0) // Podríamos necesitar ajustar este filtro
                                      .findFirst()
                                      .orElse(null); // Considerar qué hacer si no se encuentra un lote (aunque no debería pasar si registrarVentaProducto tuvo éxito)
            // --- Fin: Lógica de Inventario Centralizada ---

            // Crear DetalleVenta
            DetalleVenta detalle = new DetalleVenta();
            detalle.setVenta(venta);
            detalle.setProducto(producto);
            detalle.setLote(loteParaDetalle); // Asignar el Lote encontrado
            detalle.setCantidad(detalleDTO.getCantidad());
            detalle.setPrecioUnitarioOriginal(precioVentaOriginal);
            detalle.setPrecioUnitarioFinal(precioFinalUnitario);
            detalle.setIdPromocionAplicada(idPromocionAplicada);

            BigDecimal subtotal = precioFinalUnitario.multiply(new BigDecimal(detalleDTO.getCantidad()));
            detalle.setSubtotal(subtotal);

            // Calcular ganancia (opcional)
            BigDecimal costoUnitario = producto.getPrecioCosto() != null ? producto.getPrecioCosto() : BigDecimal.ZERO;
            detalle.setCostoUnitarioProducto(costoUnitario);
            BigDecimal gananciaDetalle = precioFinalUnitario.subtract(costoUnitario).multiply(new BigDecimal(detalleDTO.getCantidad()));
            detalle.setGananciaDetalle(gananciaDetalle);

            detallesParaGuardar.add(detalle);
            totalVenta = totalVenta.add(subtotal);

            // Ya no se llaman los servicios individuales aquí
            // loteService.reducirCantidadDeLotes(producto.getIdProducto(), detalleDTO.getCantidad());
            // registMovimientService.registrarVentaProducto(producto, detalleDTO.getCantidad(), precioVentaProducto, motivoVenta);
        }

        // 4. Establecer total en la venta y guardar cabecera
        venta.setTotal(totalVenta);
        Venta ventaGuardada = ventaRepository.save(venta);

        // 5. Asociar la venta guardada a los detalles y guardarlos
        for (DetalleVenta detalle : detallesParaGuardar) {
            detalle.setVenta(ventaGuardada);
        }
        detalleVentaService.guardarDetalles(detallesParaGuardar);

        // 6. Actualizar datos del cliente (si aplica)
        if (ventaGuardada.getIdCliente() != null) {
            clienteService.actualizarTotalComprado(ventaGuardada.getIdCliente(), totalVenta);
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
        Vendedor vendedor = vendedorService.obtenerVendedorConUsuario(request.getIdVendedor()) // Asumiendo que existe este método en VendedorService
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