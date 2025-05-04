package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Proveedor;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.RegistMovimient;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProductoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProveedorRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.RegistMovimientRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.RegistMovimientService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementación del servicio para gestionar los registros de movimientos de productos.
 * Este servicio proporciona métodos para registrar y consultar movimientos de inventario.
 * No modifica directamente el stock, eso es responsabilidad del servicio de lotes.
 */
@Service
public class RegistMovimientServiceImpl implements RegistMovimientService {

    private final RegistMovimientRepository registMovimientRepository;
    private final ProveedorRepository proveedorRepository;
    private final ProductoRepository productoRepository;

    /**
     * Constructor con inyección de dependencias
     * 
     * @param registMovimientRepository Repositorio para acceder a los registros de movimientos
     * @param proveedorRepository Repositorio para acceder a los proveedores
     * @param productoRepository Repositorio para acceder a los productos
     */
    
    public RegistMovimientServiceImpl(
            RegistMovimientRepository registMovimientRepository,
            ProveedorRepository proveedorRepository,
            ProductoRepository productoRepository) {
        this.registMovimientRepository = registMovimientRepository;
        this.proveedorRepository = proveedorRepository;
        this.productoRepository = productoRepository;
    }

    @Override
    public List<RegistMovimient> obtenerTodas() {
        return registMovimientRepository.findAll();
    }

    @Override
    public List<RegistMovimient> obtenerPorProducto(Integer idProducto) {
        if (idProducto == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        return registMovimientRepository.findByProductoIdProducto(idProducto);
    }
    
    @Override
    public List<RegistMovimient> obtenerPorTipoMovimiento(String tipoMovimiento) {
        if (tipoMovimiento == null || tipoMovimiento.trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo o vacío");
        }
        return registMovimientRepository.findByTipoMovimiento(tipoMovimiento);
    }
    
    @Override
    public List<RegistMovimient> obtenerPorProveedor(Integer idProveedor) {
        if (idProveedor == null) {
            throw new IllegalArgumentException("El ID del proveedor no puede ser nulo");
        }
        return registMovimientRepository.findByProveedorIdProveedor(idProveedor);
    }
    
    @Override
    public List<RegistMovimient> obtenerPorRangoFechas(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        if (fechaInicio == null || fechaFin == null) {
            throw new IllegalArgumentException("Las fechas de inicio y fin no pueden ser nulas");
        }
        if (fechaInicio.isAfter(fechaFin)) {
            throw new IllegalArgumentException("La fecha de inicio no puede ser posterior a la fecha de fin");
        }
        return registMovimientRepository.findByFechaBetween(fechaInicio, fechaFin);
    }
    
    @Override
    public Optional<RegistMovimient> obtenerPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("El ID no puede ser nulo");
        }
        return registMovimientRepository.findById(id);
    }

    @Override
    public RegistMovimient guardar(RegistMovimient registMovimient) {
        validarRegistroMovimiento(registMovimient);
        return registMovimientRepository.save(registMovimient);
    }

    @Override
    public boolean existsEntradaByProductoId(Integer idProducto) {
        if (idProducto == null) {
            throw new IllegalArgumentException("El ID del producto no puede ser nulo");
        }
        return !registMovimientRepository.findByTipoMovimientoAndProductoIdProducto("ENTRADA", idProducto).isEmpty();
    }

    /**
     * {@inheritDoc}
     * Este método sólo registra el movimiento, no modifica el stock.
     * El stock debe ser modificado por el servicio de lotes.
     */
    @Override
    @Transactional
    public RegistMovimient registrarCompraProducto(
            Producto producto,
            Integer cantidad,
            BigDecimal precioUnitario,
            String numeroLote,
            java.util.Date fechaVencimiento,
            Integer idProveedor,
            String motivo) {

        // Validaciones básicas
        validarProducto(producto);
        validarCantidad(cantidad);
        validarPrecioUnitario(precioUnitario);

        // Crear el registro de movimiento de tipo ENTRADA
        RegistMovimient registroMovimiento = new RegistMovimient();
        registroMovimiento.setProducto(producto);
        registroMovimiento.setCantidad(cantidad);
        registroMovimiento.setPrecioUnitario(precioUnitario);
        registroMovimiento.setTipoMovimiento("ENTRADA");
        registroMovimiento.setFecha(LocalDateTime.now());
        registroMovimiento.setMotivo(motivo != null ? motivo : "Compra de producto");

        // Asignar proveedor si se proporciona
        if (idProveedor != null) {
            Optional<Proveedor> proveedor = proveedorRepository.findById(idProveedor);
            if (!proveedor.isPresent()) {
                throw new IllegalArgumentException("No se encontró al proveedor con ID: " + idProveedor);
            }
            registroMovimiento.setProveedor(proveedor.get());
        }

        // Guardar el registro de movimiento
        return registMovimientRepository.save(registroMovimiento);
    }

    /**
     * {@inheritDoc}
     * Este método sólo registra el movimiento, no modifica el stock.
     * El stock debe ser modificado por el servicio de lotes.
     */
    @Override
    @Transactional
    public RegistMovimient registrarVentaProducto(
            Producto producto,
            Integer cantidad,
            BigDecimal precioUnitario,
            String motivo,
            Integer idProveedor) {

        // Validaciones básicas
        validarProducto(producto);
        validarCantidad(cantidad);
        validarPrecioUnitario(precioUnitario);

        // Crear el registro de movimiento de tipo SALIDA
        RegistMovimient registroMovimiento = new RegistMovimient();
        registroMovimiento.setProducto(producto);
        // Para ventas, la cantidad se registra como negativa para indicar salida
        // CORRECCIÓN: La cantidad debe ser positiva aquí, el tipo de movimiento indica la salida.
        // Si se quiere registrar negativo, ajustar la lógica de validación y stock.
        // Por ahora, mantenemos la cantidad positiva como en la compra.
        // Ajuste: Se registrará la cantidad en positivo, el tipo "SALIDA" indica la disminución.
        registroMovimiento.setCantidad(cantidad); // Registrar cantidad positiva
        registroMovimiento.setPrecioUnitario(precioUnitario);
        registroMovimiento.setTipoMovimiento("SALIDA");
        registroMovimiento.setFecha(LocalDateTime.now());
        registroMovimiento.setMotivo(motivo != null ? motivo : "Venta de producto");

        // Asignar proveedor si se proporciona el ID
        if (idProveedor != null) {
            // Usar orElseThrow para manejar el caso de proveedor no encontrado si es obligatorio
            Proveedor proveedor = proveedorRepository.findById(idProveedor)
                    .orElseThrow(() -> new IllegalArgumentException("No se encontró al proveedor con ID: " + idProveedor));
            registroMovimiento.setProveedor(proveedor);
        }

        // Guardar el registro de movimiento
        return registMovimientRepository.save(registroMovimiento);
    }
    
    /**
     * {@inheritDoc}
     * Este método sólo registra el movimiento, no modifica el stock.
     * El stock debe ser modificado por el servicio de lotes.
     */
    @Override
    @Transactional
    public RegistMovimient registrarAjusteInventario(
            Producto producto,
            Integer cantidad,
            String motivo) throws Exception {
            
        // Validaciones básicas
        validarProducto(producto);
        
        if (cantidad == 0) {
            throw new IllegalArgumentException("La cantidad de ajuste no puede ser cero");
        }
        
        if (motivo == null || motivo.trim().isEmpty()) {
            throw new IllegalArgumentException("El motivo del ajuste es requerido");
        }

        // Crear el registro de movimiento según el tipo de ajuste
        RegistMovimient registroMovimiento = new RegistMovimient();
        registroMovimiento.setProducto(producto);
        registroMovimiento.setCantidad(Math.abs(cantidad));
        registroMovimiento.setPrecioUnitario(cantidad > 0 ? producto.getPrecioCosto() : producto.getPrecioVenta());
        
        if (cantidad > 0) {
            // Ajuste positivo (aumenta stock)
            registroMovimiento.setTipoMovimiento("ENTRADA");
            registroMovimiento.setMotivo("Ajuste de inventario (+): " + motivo);
        } else {
            // Ajuste negativo (disminuye stock)
            registroMovimiento.setTipoMovimiento("SALIDA");
            registroMovimiento.setMotivo("Ajuste de inventario (-): " + motivo);
        }
        
        registroMovimiento.setFecha(LocalDateTime.now());

        // Guardar el registro de movimiento
        return registMovimientRepository.save(registroMovimiento);
    }
    
    // Métodos auxiliares de validación
    
    /**
     * Valida que un registro de movimiento tenga todos los campos requeridos y valores válidos
     * 
     * @param registMovimient Registro de movimiento a validar
     * @throws IllegalArgumentException Si algún campo es inválido
     */
    private void validarRegistroMovimiento(RegistMovimient registMovimient) {
        if (registMovimient == null) {
            throw new IllegalArgumentException("El registro de movimiento no puede ser nulo");
        }
        
        if (registMovimient.getProducto() == null) {
            throw new IllegalArgumentException("El producto asociado al movimiento no puede ser nulo");
        }
        
        if (registMovimient.getCantidad() == null || registMovimient.getCantidad() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        
        if (registMovimient.getPrecioUnitario() == null || 
            registMovimient.getPrecioUnitario().compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor que cero");
        }
        
        if (registMovimient.getTipoMovimiento() == null || registMovimient.getTipoMovimiento().trim().isEmpty()) {
            throw new IllegalArgumentException("El tipo de movimiento no puede ser nulo o vacío");
        }
        
        // Validar que el tipo de movimiento sea válido
        String tipoMovimiento = registMovimient.getTipoMovimiento().toUpperCase();
        if (!tipoMovimiento.equals("ENTRADA") && !tipoMovimiento.equals("SALIDA")) {
            throw new IllegalArgumentException("Tipo de movimiento no válido. Debe ser ENTRADA o SALIDA");
        }
    }
    
    /**
     * Valida que un producto sea válido y exista en la base de datos
     * 
     * @param producto Producto a validar
     * @throws IllegalArgumentException Si el producto es inválido o no existe
     */
    private void validarProducto(Producto producto) {
        if (producto == null || producto.getIdProducto() == null) {
            throw new IllegalArgumentException("El producto es requerido");
        }
        
        // Verificar que el producto exista en la base de datos
        if (!productoRepository.existsById(producto.getIdProducto())) {
            throw new IllegalArgumentException("El producto con ID " + producto.getIdProducto() + " no existe");
        }
    }
    
    /**
     * Valida que una cantidad sea válida
     * 
     * @param cantidad Cantidad a validar
     * @throws IllegalArgumentException Si la cantidad es inválida
     */
    private void validarCantidad(Integer cantidad) {
        if (cantidad == null || cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
    }
    
    /**
     * Valida que un precio unitario sea válido
     * 
     * @param precioUnitario Precio unitario a validar
     * @throws IllegalArgumentException Si el precio unitario es inválido
     */
    private void validarPrecioUnitario(BigDecimal precioUnitario) {
        if (precioUnitario == null || precioUnitario.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio unitario debe ser mayor que cero");
        }
    }
}
