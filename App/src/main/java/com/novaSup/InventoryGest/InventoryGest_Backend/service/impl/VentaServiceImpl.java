package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.dto.VentaRequestDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.*;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.VentaRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.ResultadoCalculoImpuestosDTO;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.*;
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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private final CajaService cajaService; // Inyectar CajaService
    private final MovimientoCajaService movimientoCajaService; // Inyectar MovimientoCajaService


    public VentaServiceImpl(VentaRepository ventaRepository, DetalleVentaService detalleVentaService, ProductoService productoService, ClienteService clienteService, ComisionService comisionService, VendedorService vendedorService, InventarioService inventarioService, PromocionService promocionService, CalculoImpuestoService calculoImpuestoService, FacturaService facturaService, CajaService cajaService, MovimientoCajaService movimientoCajaService) {
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
        this.cajaService = cajaService;
        this.movimientoCajaService = movimientoCajaService;
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
        } else {
            // Si no se proporciona idCliente, se busca el cliente "Venta General"
            final String NOMBRE_CLIENTE_GENERAL = "Venta General";
            Cliente clienteGeneral = clienteService.obtenerClientePorNombre(NOMBRE_CLIENTE_GENERAL)
                    .orElseThrow(() -> new EntityNotFoundException("Cliente '" + NOMBRE_CLIENTE_GENERAL + "' no encontrado. " +
                            "Asegúrese de que exista este cliente en la base de datos para ventas sin cliente específico."));
            venta.setCliente(clienteGeneral);
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
                detalle.getDetalleLotesUsados().clear(); // Limpiar por si acaso
                for (LoteReducidoInfoDTO loteInfo : resultadoInventario.getLotesAfectados()) {
                    DetalleVentaLoteUso dvu = new DetalleVentaLoteUso();
                    dvu.setDetalleVenta(detalle); 
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

        // ****** INICIO: LÓGICA DE MOVIMIENTO DE CAJA (INTEGRACIÓN) ******
        if ("EFECTIVO".equalsIgnoreCase(ventaGuardada.getTipoPago())) { // Asumiendo "EFECTIVO" como tipo de pago en efectivo
            // Obtener el usuario del vendedor asociado a la venta
            Usuario usuarioVendedor = ventaGuardada.getVendedor() != null ? ventaGuardada.getVendedor().getUsuario() : null;

            if (usuarioVendedor != null) {
                // Buscar la caja abierta para este usuario
                Caja cajaAbierta = cajaService.getCajaAbiertaByUsuario(usuarioVendedor);

                if (cajaAbierta != null) {
                    // Registrar el movimiento de caja tipo VENTA
                    String descripcionMovimiento = "Venta No. " + ventaGuardada.getNumeroVenta();
                    BigDecimal montoMovimiento = ventaGuardada.getTotalConImpuestos(); // El total de la venta es el ingreso en efectivo

                    movimientoCajaService.registrarMovimiento(
                        cajaAbierta,
                        "VENTA", // Tipo de movimiento
                        descripcionMovimiento,
                        montoMovimiento,
                        usuarioVendedor, // Usuario que realiza el movimiento (el vendedor)
                        ventaGuardada // Asociar a la entidad Venta
                    );
                } else {
                    // Manejar el caso donde no hay una caja abierta para el usuario/vendedor
                    // En un sistema real, esto podría indicar un problema grave o un flujo de trabajo incorrecto
                    // donde un vendedor intenta vender en efectivo sin una caja abierta.
                    // Lanzamos una excepción para detener la operación y notificar el problema.
                    throw new IllegalStateException("No se encontró caja abierta para el usuario: " + usuarioVendedor.getNombre() + ". No se puede registrar el movimiento de efectivo para la venta " + ventaGuardada.getIdVenta());
                }
            } else {
                 // Manejar el caso donde la venta no tiene un vendedor asociado con usuario
                 // Esto podría suceder si la venta se crea sin un vendedor o si el vendedor no tiene un usuario asociado.
                 // Dependiendo de las reglas de negocio, esto podría ser un error fatal para ventas en efectivo.
                 throw new IllegalStateException("La venta " + ventaGuardada.getIdVenta() + " no tiene un vendedor válido asociado para registrar el movimiento de caja.");
            }
        }
        // ****** FIN: LÓGICA DE MOVIMIENTO DE CAJA (INTEGRACIÓN) ******


        // 8. Devolver la venta completa RECARGADA
        Optional<Venta> ventaRecargadaOpt = ventaRepository.findVentaWithPrimaryDetailsById(ventaGuardada.getIdVenta());
        if (ventaRecargadaOpt.isEmpty()) {
            throw new EntityNotFoundException("Error al recargar la venta recién creada: " + ventaGuardada.getIdVenta());
        }
        Venta ventaRecargada = ventaRecargadaOpt.get();
        if (ventaRecargada.getDetallesVenta() != null && !ventaRecargada.getDetallesVenta().isEmpty()) {
            completarDetallesVentaConLotes(ventaRecargada.getDetallesVenta());
        }
        return ventaRecargada;
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Venta> obtenerVentaPorId(Integer id) {
        // Paso 1: Obtener la Venta con detalles primarios (incluyendo DetalleVenta y Producto)
        Optional<Venta> ventaOptional = ventaRepository.findVentaWithPrimaryDetailsById(id);

        if (ventaOptional.isPresent()) {
            Venta venta = ventaOptional.get();
            // Paso 2: Si la venta tiene detalles, cargar sus DetalleVentaLoteUso
            if (venta.getDetallesVenta() != null && !venta.getDetallesVenta().isEmpty()) {
                completarDetallesVentaConLotes(venta.getDetallesVenta());
            }
        }
        return ventaOptional;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> listarVentas() {
        // Paso 1: Obtener todas las Ventas con detalles primarios
        List<Venta> ventas = ventaRepository.findAllVentasWithPrimaryDetails();

        // Paso 2: Para cada venta, si tiene detalles, cargar sus DetalleVentaLoteUso
        // Recolectar todos los DetalleVenta de todas las ventas en una sola lista
        List<DetalleVenta> todosLosDetallesVenta = ventas.stream()
                .filter(v -> v.getDetallesVenta() != null)
                .flatMap(v -> v.getDetallesVenta().stream())
                .collect(Collectors.toList());

        if (!todosLosDetallesVenta.isEmpty()) {
            completarDetallesVentaConLotes(todosLosDetallesVenta);
        }
        return ventas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> obtenerVentasPorCliente(Integer idCliente) {
        // Paso 1: Obtener las Ventas del cliente con detalles primarios
        List<Venta> ventas = ventaRepository.findVentasByClienteWithPrimaryDetails(idCliente);
        
        List<DetalleVenta> todosLosDetallesVenta = ventas.stream()
                .filter(v -> v.getDetallesVenta() != null)
                .flatMap(v -> v.getDetallesVenta().stream())
                .collect(Collectors.toList());

        if (!todosLosDetallesVenta.isEmpty()) {
            completarDetallesVentaConLotes(todosLosDetallesVenta);
        }
        return ventas;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Venta> obtenerVentasPorVendedor(Integer idVendedor) {
        // Paso 1: Obtener las Ventas del vendedor con detalles primarios
        List<Venta> ventas = ventaRepository.findVentasByVendedorWithPrimaryDetails(idVendedor);

        List<DetalleVenta> todosLosDetallesVenta = ventas.stream()
                .filter(v -> v.getDetallesVenta() != null)
                .flatMap(v -> v.getDetallesVenta().stream())
                .collect(Collectors.toList());

        if (!todosLosDetallesVenta.isEmpty()) {
            completarDetallesVentaConLotes(todosLosDetallesVenta);
        }
        return ventas;
    }

    /**
     * Método helper para cargar y adjuntar DetalleVentaLoteUso a una lista de DetalleVenta.
     * @param detallesVentaOriginales Lista de DetalleVenta cuyas colecciones detalleLotesUsados necesitan ser pobladas.
     */
    private void completarDetallesVentaConLotes(List<DetalleVenta> detallesVentaOriginales) {
        if (detallesVentaOriginales == null || detallesVentaOriginales.isEmpty()) {
            return;
        }

        // Extraer los IDs de los DetalleVenta para la consulta secundaria
        Set<Integer> detalleVentaIds = detallesVentaOriginales.stream()
                .map(DetalleVenta::getIdDetalle)
                .collect(Collectors.toSet());

        // Paso 2.1: Cargar los DetalleVenta (con sus DetalleVentaLoteUso y Lotes) usando los IDs recolectados
        List<DetalleVenta> detallesConLotesCargados = ventaRepository.findDetalleVentaWithLotesByIdIn(detalleVentaIds);

        // Crear un mapa para un acceso eficiente a los detalles cargados por su ID
        Map<Integer, DetalleVenta> mapaDetallesConLotes = detallesConLotesCargados.stream()
                .collect(Collectors.toMap(DetalleVenta::getIdDetalle, Function.identity()));

        // Paso 2.2: Iterar sobre los detalles originales y establecer su colección de lotes usados
        // con la información obtenida en el paso anterior.
        detallesVentaOriginales.forEach(detalleOriginal -> {
            DetalleVenta detalleConLotes = mapaDetallesConLotes.get(detalleOriginal.getIdDetalle());
            if (detalleConLotes != null && detalleConLotes.getDetalleLotesUsados() != null) {
                // Reemplazamos la colección (posiblemente proxy no inicializado)
                // con la colección completamente cargada.
                // Es importante asegurarse de que DetalleVenta.detalleLotesUsados pueda ser modificado.
                // Si la colección original es inmutable o gestionada de forma especial, esto podría necesitar ajuste.
                // Usualmente, si es una List o Set estándar, esto funciona.
                detalleOriginal.setDetalleLotesUsados(new ArrayList<>(detalleConLotes.getDetalleLotesUsados()));
            } else {
                // Si por alguna razón no se encontraron lotes (o el detalle mismo), asegurar que sea una lista vacía
                detalleOriginal.setDetalleLotesUsados(Collections.emptyList());
            }
        });
    }

    // --- Métodos privados de validación ---

    private void validarRequest(VentaRequestDTO request) throws Exception {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud de venta no puede ser nula.");
        }
        if (request.getDetalles() == null || request.getDetalles().isEmpty()) {
            throw new IllegalArgumentException("La venta debe tener al menos un detalle.");
        }
        // Validar que idVendedor exista, si se proporciona
        if (request.getIdVendedor() != null && vendedorService.obtenerPorId(request.getIdVendedor()).isEmpty()) {
            throw new EntityNotFoundException("El vendedor con ID " + request.getIdVendedor() + " no existe.");
        }
        // Validar que idCliente exista, si se proporciona
        if (request.getIdCliente() != null && clienteService.obtenerClientePorId(request.getIdCliente()).isEmpty()) {
            throw new EntityNotFoundException("El cliente con ID " + request.getIdCliente() + " no existe.");
        }
         for (VentaRequestDTO.DetalleVentaDTO detalleDTO : request.getDetalles()) {
            if (detalleDTO.getIdProducto() == null || detalleDTO.getCantidad() == null) {
                throw new IllegalArgumentException("Cada detalle de venta debe tener ID de producto y cantidad.");
            }
            if (detalleDTO.getCantidad() <= 0) {
                throw new IllegalArgumentException("La cantidad del producto debe ser mayor que cero.");
            }
            Producto producto = productoService.obtenerPorId(detalleDTO.getIdProducto())
                    .orElseThrow(() -> new EntityNotFoundException("Producto no encontrado: " + detalleDTO.getIdProducto()));
            if (!producto.getEstado()) {
                throw new IllegalStateException("El producto '" + producto.getNombre() + "' está inactivo y no se puede vender.");
            }
        }
    }

}