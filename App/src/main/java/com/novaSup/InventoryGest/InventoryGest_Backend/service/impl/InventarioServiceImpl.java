package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaStock;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.RegistMovimient;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProductoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.AuditoriaStockService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.InventarioService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.LoteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.RegistMovimientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

/**
 * Implementación del servicio de coordinación para operaciones de inventario.
 * Este servicio actúa como una fachada que coordina las operaciones entre los servicios
 * de lotes y registro de movimientos.
 */
@Service
public class InventarioServiceImpl implements InventarioService {

    private final LoteService loteService;
    private final RegistMovimientService registMovimientService;
    private final ProductoRepository productoRepository;
    private final AuditoriaStockService auditoriaStockService;

    public InventarioServiceImpl(
            LoteService loteService,
            RegistMovimientService registMovimientService,
            ProductoRepository productoRepository,
            AuditoriaStockService auditoriaStockService) {
        this.loteService = loteService;
        this.registMovimientService = registMovimientService;
        this.productoRepository = productoRepository;
        this.auditoriaStockService = auditoriaStockService;
    }

    @Override
    @Transactional
    public RegistMovimient registrarCompraProducto(
            Producto producto,
            Integer cantidad,
            BigDecimal precioUnitario,
            String numeroLote,
            Date fechaVencimiento,
            String motivo) {

        // Validar datos de entrada
        validarDatosCompra(producto, cantidad, precioUnitario, numeroLote, fechaVencimiento);

        // Obtener el idProveedor del producto
        Integer idProveedor = producto.getIdProveedor();
        if (idProveedor == null && producto.getProveedor() != null) {
            idProveedor = producto.getProveedor().getIdProveedor();
        }

        // 1. Registrar el movimiento de entrada
        RegistMovimient movimiento = registMovimientService.registrarCompraProducto(
                producto,
                cantidad,
                precioUnitario,
                numeroLote,
                fechaVencimiento,
                idProveedor,
                motivo
        );

        // 2. Crear el lote con la cantidad inicial
        loteService.crearNuevoLote(
                producto,
                cantidad,
                numeroLote,
                fechaVencimiento,
                movimiento.getIdEntrada()
        );

        // 3. Actualizar el producto (en caso de ser necesario)
        actualizarProductoConNuevosPreciosSiCorresponde(producto, precioUnitario);

        return movimiento;
    }

    @Override
    @Transactional
    public RegistMovimient registrarVentaProducto(
            Producto producto,
            Integer cantidad,
            BigDecimal precioUnitario,
            String motivo) throws Exception {

        // Validar si el producto está activo
        if (!producto.getEstado()) {
            throw new IllegalStateException("No se puede registrar una venta para un producto inactivo: " + producto.getNombre());
        }

        // Validar que haya suficiente stock
        validarStockSuficiente(producto, cantidad);

        // --- Obtener idProveedor del producto ---
        Integer idProveedor = null;
        if (producto.getProveedor() != null) {
            idProveedor = producto.getProveedor().getIdProveedor();
        } else if (producto.getIdProveedor() != null) { // Fallback si solo se guarda el ID directamente
             idProveedor = producto.getIdProveedor();
        }
        // Considerar si se debe lanzar una excepción si el proveedor es nulo y es requerido

        // 1. Registrar el movimiento de salida, incluyendo idProveedor
        RegistMovimient movimiento = registMovimientService.registrarVentaProducto(
                producto,
                cantidad,
                precioUnitario,
                motivo,
                idProveedor // <--- Pasar el idProveedor obtenido
        );

        // 2. Reducir la cantidad en los lotes (FIFO/FEFO)
        loteService.reducirCantidadDeLotes(producto.getIdProducto(), cantidad);

        return movimiento;
    }

    @Override
    @Transactional
    public RegistMovimient registrarAjusteInventario(
            Producto producto,
            Integer cantidad,
            String motivo) throws Exception {

        // Validar si el producto está activo
        if (!producto.getEstado()) {
            throw new IllegalStateException("No se puede registrar un ajuste para un producto inactivo: " + producto.getNombre());
        }

        // Validar para ajustes negativos que haya suficiente stock
        if (cantidad < 0 && producto.getStock() < Math.abs(cantidad)) {
            throw new Exception("No hay suficiente stock para realizar el ajuste negativo. Stock actual: "
                    + producto.getStock() + ", Ajuste solicitado: " + Math.abs(cantidad));
        }

        // 1. Registrar el movimiento de ajuste
        RegistMovimient movimiento = registMovimientService.registrarAjusteInventario(
                producto,
                cantidad,
                motivo
        );

        // 2. Crear o reducir lotes según corresponda
        loteService.crearLoteAjuste(producto, cantidad, motivo);

        return movimiento;
    }

    @Override
    @Transactional
    public Lote registrarDevolucion(
            Integer idLote,
            Integer cantidad,
            String motivo) throws Exception {

        // 1. Buscar el lote
        Lote lote = loteService.obtenerPorId(idLote)
                .orElseThrow(() -> new Exception("Lote no encontrado con ID: " + idLote));

        // Validar si el producto asociado al lote está activo
        if (lote.getProducto() != null && !lote.getProducto().getEstado()) {
            throw new IllegalStateException("No se puede registrar una devolución para un producto inactivo: " + lote.getProducto().getNombre());
        }

        // 2. Crear movimiento de entrada (devolución)
        BigDecimal precioUnitario = (lote.getProducto().getPrecioCosto() != null)
                ? lote.getProducto().getPrecioCosto()
                : new BigDecimal("0");

        RegistMovimient movimiento = new RegistMovimient();
        movimiento.setProducto(lote.getProducto());
        movimiento.setCantidad(cantidad);
        movimiento.setPrecioUnitario(precioUnitario);
        movimiento.setTipoMovimiento("ENTRADA");
        movimiento.setFecha(java.time.LocalDateTime.now());
        movimiento.setMotivo(motivo != null ? motivo : "Devolución al lote: " + lote.getNumeroLote());

        registMovimientService.guardar(movimiento);

        // 3. Procesar la devolución en el lote
        return loteService.procesarDevolucion(idLote, cantidad);
    }
    
    @Override
    @Transactional
    public AuditoriaStock registrarAuditoriaStock(
            Producto producto,
            Integer stockReal,
            String motivo,
            Integer idUsuario) throws Exception {
        
        // Validaciones básicas
        if (producto == null || producto.getIdProducto() == null) {
            throw new IllegalArgumentException("El producto es requerido");
        }
        
        if (stockReal == null || stockReal < 0) {
            throw new IllegalArgumentException("El stock real no puede ser negativo y es requerido");
        }
        
        // Recargar el producto para tener la información de stock actualizada
        Producto productoActualizado = productoRepository.findById(producto.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));
        
        // Realizar la auditoría y ajustar el stock
        return auditoriaStockService.registrarDiferenciaInventario(
                productoActualizado, 
                stockReal, 
                motivo, 
                idUsuario
        );
    }

    @Override
    public List<Lote> obtenerLotesPorProducto(Integer idProducto) {
        return loteService.obtenerPorProducto(idProducto);
    }

    @Override
    public List<RegistMovimient> obtenerMovimientosPorProducto(Integer idProducto) {
        return registMovimientService.obtenerPorProducto(idProducto);
    }

    // Métodos auxiliares

    private void validarDatosCompra(
            Producto producto,
            Integer cantidad,
            BigDecimal precioUnitario,
            String numeroLote,
            Date fechaVencimiento) {

        if (producto == null || producto.getIdProducto() == null) {
            throw new IllegalArgumentException("El producto es requerido");
        }

        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }

        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor que cero");
        }

        if (numeroLote == null || numeroLote.trim().isEmpty()) {
            throw new IllegalArgumentException("El número de lote es requerido");
        }

        if (fechaVencimiento == null) {
            throw new IllegalArgumentException("La fecha de vencimiento es requerida");
        }

        // Validar que la fecha de vencimiento sea futura
        Date hoy = new Date();
        if (fechaVencimiento.before(hoy)) {
            throw new IllegalArgumentException("La fecha de vencimiento debe ser futura");
        }
    }

    private void validarStockSuficiente(Producto producto, Integer cantidad) throws Exception {
        if (producto == null || producto.getIdProducto() == null) {
            throw new IllegalArgumentException("El producto es requerido");
        }

        // Recargar el producto para tener la información de stock actualizada
        Producto productoActualizado = productoRepository.findById(producto.getIdProducto())
                .orElseThrow(() -> new IllegalArgumentException("Producto no encontrado"));

        if (productoActualizado.getStock() < cantidad) {
            throw new Exception("No hay suficiente stock disponible para el producto: " + producto.getNombre()
                    + ". Stock actual: " + productoActualizado.getStock() + ", Cantidad solicitada: " + cantidad);
        }
    }

    private void actualizarProductoConNuevosPreciosSiCorresponde(Producto producto, BigDecimal precioUnitario) {
        // Aquí se podría implementar una lógica para actualizar el precio de costo del producto
        // en función del nuevo precio de compra, si es necesario
        // Por ejemplo, calcular un promedio ponderado de precios de compra
    }
}