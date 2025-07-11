package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.RegistMovimient;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.LoteRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.LoteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.NotificacionService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.RegistMovimientService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.ProductoService; // Import ProductoService
import com.novaSup.InventoryGest.InventoryGest_Backend.dto.LoteReducidoInfoDTO; // Importar DTO
import java.util.ArrayList; // Importar ArrayList
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementación del servicio de gestión de lotes.
 * Este servicio es el encargado principal de administrar el stock de productos
 * a través de la creación, modificación y eliminación de lotes.
 * Cada operación que afecta al stock genera un registro de movimiento correspondiente.
 */
@Service
public class LoteServiceImpl implements LoteService {

    private final LoteRepository loteRepository;
    private final StockServiceImpl stockService;
    private final RegistMovimientService registMovimientService;
    private final ProductoService productoService; // Inject ProductoService

    public LoteServiceImpl(
            LoteRepository loteRepository,
            StockServiceImpl stockService,
            NotificacionService notificacionService,
            RegistMovimientService registMovimientService,
            ProductoService productoService) { // Add ProductoService to constructor
        this.loteRepository = loteRepository;
        this.stockService = stockService;
        this.registMovimientService = registMovimientService;
        this.productoService = productoService; // Initialize ProductoService
    }

    @Override
    public List<Lote> obtenerTodos() {
        return loteRepository.findAll();
    }

    @Override
    public Optional<Lote> obtenerPorId(Integer id) {
        return loteRepository.findById(id);
    }

    @Override
    public List<Lote> obtenerPorProducto(Integer idProducto) {
        return loteRepository.findByProductoIdProductoAndActivoTrue(idProducto);
    }

    @Override
    @Transactional
    public Lote guardar(Lote lote) {
        Lote loteGuardado = loteRepository.save(lote);
        // Actualizar stock del producto
        if (lote.getProducto() != null) {
            stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
        } else if (lote.getIdProducto() != null) {
            stockService.actualizarStockProducto(lote.getIdProducto());
        }
        return loteGuardado;
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        Optional<Lote> loteOpt = loteRepository.findById(id);

        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();
            
            // Registrar movimiento de eliminación (ajuste negativo)
            try {
                registrarMovimientoAjuste(
                    lote.getProducto(),
                    -lote.getCantidad(),
                    "Eliminación de lote: " + lote.getNumeroLote()
                );
            } catch (Exception e) {
                throw new RuntimeException("Error al registrar movimiento de eliminación: " + e.getMessage());
            }
            
            // Desactivar en lugar de eliminar físicamente
            lote.setActivo(false);
            loteRepository.save(lote);

            // Actualizar stock
            if (lote.getProducto() != null) {
                stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
            }
        }
    }

    @Override
    public List<Lote> obtenerLotesProximosVencer(Integer diasAlerta) {
        // Calcular fecha límite
        LocalDate hoy = LocalDate.now();
        LocalDate fechaLimite = hoy.plusDays(diasAlerta);

        Date fechaActual = Date.from(hoy.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date fechaAlerta = Date.from(fechaLimite.atStartOfDay(ZoneId.systemDefault()).toInstant());

        return loteRepository.findByFechaVencimientoBetweenAndActivoTrue(fechaActual, fechaAlerta);
    }

    @Override
    public List<Lote> obtenerLotesPorRangoFechaEntrada(Date fechaInicio, Date fechaFin) {
        return loteRepository.findByFechaEntradaBetweenAndActivoTrue(fechaInicio, fechaFin);
    }

    @Override
    @Transactional
    public Optional<Lote> activarLote(Integer id) {
        Optional<Lote> loteOpt = loteRepository.findByIdLoteAndActivoFalse(id);

        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();

            // Verificar si el lote está vencido
            Date fechaActual = new Date();
            if (lote.getFechaVencimiento() != null && lote.getFechaVencimiento().before(fechaActual)) {
                throw new IllegalStateException("No se puede activar un lote vencido");
            }
            
            // Ya no registramos movimiento, solo actualizamos el estado del lote
            lote.setActivo(true);
            Lote loteActualizado = loteRepository.save(lote);

            // Actualizar stock
            if (lote.getProducto() != null) {
                stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
            }

            return Optional.of(loteActualizado);
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public Optional<Lote> desactivarLote(Integer id) {
        Optional<Lote> loteOpt = loteRepository.findByIdLoteAndActivoTrue(id);

        if (loteOpt.isPresent()) {
            Lote lote = loteOpt.get();
            
            // Verificar si el lote está vencido
            Date fechaActual = new Date();
            boolean loteVencido = lote.getFechaVencimiento() != null && lote.getFechaVencimiento().before(fechaActual);
            
            // Solo registrar movimiento si el lote está vencido
            if (loteVencido) {
                try {
                    registrarMovimientoAjuste(
                        lote.getProducto(),
                        -lote.getCantidad(),
                        "Desactivación de lote vencido: " + lote.getNumeroLote()
                    );
                } catch (Exception e) {
                    throw new RuntimeException("Error al registrar movimiento de desactivación: " + e.getMessage());
                }
            }
            
            lote.setActivo(false);
            Lote loteActualizado = loteRepository.save(lote);

            // Actualizar stock
            if (lote.getProducto() != null) {
                stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
            }

            return Optional.of(loteActualizado);
        }

        return Optional.empty();
    }

    @Override
    public List<Lote> obtenerLotesInactivos() {
        return loteRepository.findByActivoFalse();
    }

    @Override
    public List<Lote> obtenerLotesActivos() {
        return loteRepository.findByActivoTrue();
    }

    @Override
    @Transactional
    public Lote reducirCantidadLote(Integer idLote, Integer cantidad) throws Exception {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser mayor que cero");
        }

        Optional<Lote> loteOpt = loteRepository.findById(idLote);

        if (!loteOpt.isPresent()) {
            throw new Exception("Lote no encontrado");
        }

        Lote lote = loteOpt.get();

        if (!lote.getActivo()) {
            throw new Exception("No se puede reducir cantidad de un lote inactivo");
        }

        // Verificar si el producto asociado está activo
        if (lote.getProducto() == null || !lote.getProducto().getEstado()) { // Use getEstado()
            throw new Exception("No se puede reducir cantidad de un lote asociado a un producto inactivo");
        }

        if (lote.getCantidad() < cantidad) {
            throw new Exception("Cantidad a reducir excede el stock disponible en el lote");
        }
        
        // Registrar movimiento de reducción de cantidad (salida)
        // Utilizamos el precio del producto si está disponible
        BigDecimal precioUnitario = (lote.getProducto() != null && lote.getProducto().getPrecioVenta() != null) 
            ? lote.getProducto().getPrecioVenta() 
            : new BigDecimal("0");
            
        RegistMovimient registroMovimiento = new RegistMovimient();
        registroMovimiento.setProducto(lote.getProducto());
        registroMovimiento.setCantidad(cantidad);
        registroMovimiento.setPrecioUnitario(precioUnitario);
        registroMovimiento.setTipoMovimiento("SALIDA");
        registroMovimiento.setFecha(LocalDateTime.now());
        registroMovimiento.setMotivo("Reducción de cantidad en lote: " + lote.getNumeroLote());
        
        registMovimientService.guardar(registroMovimiento);

        // Reducir cantidad
        lote.setCantidad(lote.getCantidad() - cantidad);

        // Si la cantidad llega a cero, desactivar el lote
        if (lote.getCantidad() == 0) {
            lote.setActivo(false);
        }

        Lote loteActualizado = loteRepository.save(lote);

        // Actualizar stock del producto
        if (lote.getProducto() != null) {
            stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
        }

        return loteActualizado;
    }

    @Override
    @Transactional
    public List<LoteReducidoInfoDTO> reducirCantidadDeLotes(Integer idProducto, Integer cantidadTotalAReducir) throws Exception { // Firma modificada
        if (cantidadTotalAReducir <= 0) {
            throw new IllegalArgumentException("La cantidad a reducir debe ser mayor que cero.");
        }

        Producto producto = productoService.obtenerPorId(idProducto)
                .orElseThrow(() -> new Exception("Producto no encontrado con ID: " + idProducto));

        // Validar stock general primero (opcional, pero buena práctica)
        // Esta validación ya existe en InventarioServiceImpl -> validarStockSuficiente
        // if (producto.getStock() < cantidadTotalAReducir) {
        //     throw new Exception("Stock insuficiente para el producto " + producto.getNombre() +
        //                       ". Stock actual: " + producto.getStock() + ", Solicitado: " + cantidadTotalAReducir);
        // }

        // Obtener lotes activos, ordenados por fecha de vencimiento y luego por fecha de entrada (FIFO/FEFO)
        List<Lote> lotesDisponibles = loteRepository.findByProductoIdProductoAndActivoTrueOrderByFechaVencimientoAscFechaEntradaAsc(idProducto);

        List<LoteReducidoInfoDTO> lotesAfectados = new ArrayList<>();
        int cantidadRestanteAReducir = cantidadTotalAReducir;

        for (Lote lote : lotesDisponibles) {
            if (cantidadRestanteAReducir == 0) {
                break; // Ya se cubrió la cantidad necesaria
            }

            if (lote.getCantidad() == null || lote.getCantidad() <= 0) {
                continue; // Lote sin stock, pasar al siguiente
            }

            int cantidadTomadaDeEsteLote = Math.min(lote.getCantidad(), cantidadRestanteAReducir);

            lote.setCantidad(lote.getCantidad() - cantidadTomadaDeEsteLote);
            cantidadRestanteAReducir -= cantidadTomadaDeEsteLote;

            lotesAfectados.add(new LoteReducidoInfoDTO(lote, cantidadTomadaDeEsteLote));

            if (lote.getCantidad() == 0) {
                lote.setActivo(false); // Desactivar lote si se agota
            }
            loteRepository.save(lote); // Guardar cambios en el lote
        }

        if (cantidadRestanteAReducir > 0) {
            // Esto no debería ocurrir si la validación de stock general en InventarioService es correcta
            // y la suma de cantidades en lotes es consistente con el stock del producto.
            throw new Exception("Stock insuficiente en los lotes para el producto " + producto.getNombre() +
                              ". Faltaron: " + cantidadRestanteAReducir + " unidades por asignar desde lotes.");
        }

        // La actualización del stock general del producto se maneja en StockServiceImpl,
        // que debería ser llamado después de esta operación (por ejemplo, desde InventarioServiceImpl o VentaServiceImpl)
        // o mediante triggers/eventos si el diseño lo permite.
        // Por ahora, asumimos que stockService.actualizarStockProducto(idProducto) será llamado.
        // Es importante asegurar que se llame para mantener la consistencia del campo 'stock' en Producto.
        stockService.actualizarStockProducto(idProducto); // Asegurar que el stock total del producto se actualice

        return lotesAfectados;
    }

    @Override
    @Transactional
    public Lote procesarDevolucion(Integer idLote, Integer cantidad) throws Exception {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad a devolver debe ser mayor que cero");
        }

        Optional<Lote> loteOpt = loteRepository.findById(idLote);

        if (!loteOpt.isPresent()) {
            throw new Exception("Lote no encontrado");
        }

        Lote lote = loteOpt.get();

        // Verificar si el lote está vencido para posible alerta
        Date fechaActual = new Date();
        if (lote.getFechaVencimiento() != null && lote.getFechaVencimiento().before(fechaActual)) {
            // Se podría generar una alerta o log aquí
        }
        
        // Registrar movimiento de devolución (entrada)
        // Utilizamos el precio del producto si está disponible
        BigDecimal precioUnitario = (lote.getProducto() != null && lote.getProducto().getPrecioCosto() != null) 
            ? lote.getProducto().getPrecioCosto() 
            : new BigDecimal("0");
            
        RegistMovimient registroMovimiento = new RegistMovimient();
        registroMovimiento.setProducto(lote.getProducto());
        registroMovimiento.setCantidad(cantidad);
        registroMovimiento.setPrecioUnitario(precioUnitario);
        registroMovimiento.setTipoMovimiento("ENTRADA");
        registroMovimiento.setFecha(LocalDateTime.now());
        registroMovimiento.setMotivo("Devolución a lote: " + lote.getNumeroLote());
        
        registMovimientService.guardar(registroMovimiento);

        // Para devoluciones, reactivamos el lote si estaba inactivo
        if (!lote.getActivo()) {
            lote.setActivo(true);
        }

        // Aumentar cantidad
        lote.setCantidad(lote.getCantidad() + cantidad);

        Lote loteActualizado = loteRepository.save(lote);

        // Actualizar stock del producto
        if (lote.getProducto() != null) {
            stockService.actualizarStockProducto(lote.getProducto().getIdProducto());
        }

        return loteActualizado;
    }

    @Override
    @Transactional
    public Lote crearNuevoLote(Producto producto, Integer cantidad, String numeroLote, Date fechaVencimiento, Integer idEntrada) {
        if (producto == null || producto.getIdProducto() == null) {
            throw new IllegalArgumentException("El producto es requerido");
        }

        // Verificar si el producto está activo
        if (!producto.getEstado()) { // Use getEstado()
            throw new IllegalArgumentException("No se puede crear un lote para un producto inactivo.");
        }

        if (cantidad <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor que cero");
        }
        
        // Verificar que el producto tenga un proveedor asignado
        if (producto.getProveedor() == null) {
            throw new IllegalArgumentException("No se puede crear un lote para un producto sin proveedor asignado. Por favor, asigne un proveedor al producto antes de crear un lote.");
        }

        // Crear nuevo lote
        Lote lote = new Lote();
        lote.setProducto(producto);
        lote.setNumeroLote(numeroLote != null ? numeroLote : generarNumeroLote());
        lote.setFechaEntrada(new Date()); // Fecha actual
        lote.setFechaVencimiento(fechaVencimiento);
        lote.setCantidad(cantidad);
        lote.setActivo(true);
        lote.setIdEntrada(idEntrada);
        
        // Si no viene de un registro de movimiento existente, creamos uno nuevo
        if (idEntrada == null) {
            BigDecimal precioUnitario = (producto.getPrecioCosto() != null) 
                ? producto.getPrecioCosto() 
                : new BigDecimal("0");
                
            RegistMovimient registroMovimiento = new RegistMovimient();
            registroMovimiento.setProducto(producto);
            registroMovimiento.setProveedor(producto.getProveedor());
            registroMovimiento.setCantidad(cantidad);
            registroMovimiento.setPrecioUnitario(precioUnitario);
            registroMovimiento.setTipoMovimiento("ENTRADA");
            registroMovimiento.setFecha(LocalDateTime.now());
            registroMovimiento.setMotivo("Creación de lote: " + lote.getNumeroLote());
            
            RegistMovimient movimientoGuardado = registMovimientService.guardar(registroMovimiento);
            lote.setIdEntrada(movimientoGuardado.getIdEntrada());
        }

        // Guardar el lote
        Lote loteGuardado = guardar(lote);

        // El método guardar ya actualiza el stock del producto

        return loteGuardado;
    }

    @Override
    @Transactional
    public Lote crearLoteAjuste(Producto producto, Integer cantidad, String motivo) throws Exception {
        if (producto == null || producto.getIdProducto() == null) {
            throw new IllegalArgumentException("El producto es requerido");
        }

        // Verificar si el producto está activo
        if (!producto.getEstado()) { // Use getEstado()
            throw new IllegalArgumentException("No se puede crear un lote de ajuste para un producto inactivo.");
        }        if (cantidad == 0) {
            throw new IllegalArgumentException("La cantidad de ajuste no puede ser cero");
        }
        
        // NOTA: El registro de movimiento ya se realiza en InventarioServiceImpl.registrarAjusteInventario()
        // por lo tanto NO lo registramos aquí para evitar duplicación

        // Generar número de lote para ajuste
        String numeroLote = "AJUSTE-" + generarNumeroLote();

        if (cantidad > 0) {
            // Ajuste positivo: crear un nuevo lote con la cantidad adicional
            Lote lote = new Lote();
            lote.setProducto(producto);
            lote.setNumeroLote(numeroLote);
            lote.setFechaEntrada(new Date()); // Fecha actual
            // No establecemos fecha de vencimiento para lotes de ajuste
            lote.setCantidad(cantidad);
            lote.setActivo(true);

            // Guardar el lote
            return guardar(lote);
        } else {
            // Ajuste negativo: reducir la cantidad de los lotes existentes
            try {
                reducirCantidadDeLotes(producto.getIdProducto(), Math.abs(cantidad));

                // Crear un lote "fantasma" solo para registro (no afecta stock)
                Lote loteRegistro = new Lote();
                loteRegistro.setProducto(producto);
                loteRegistro.setNumeroLote(numeroLote);
                loteRegistro.setFechaEntrada(new Date());
                loteRegistro.setCantidad(0); // No afecta stock
                loteRegistro.setActivo(false);

                return loteRegistro;
            } catch (Exception e) {
                throw new Exception("No se pudo realizar el ajuste negativo: " + e.getMessage());
            }
        }
    }
    
    /**
     * Método auxiliar para registrar un movimiento de ajuste de inventario
     */
    private RegistMovimient registrarMovimientoAjuste(Producto producto, Integer cantidad, String motivo) throws Exception {
        if (producto == null) {
            throw new IllegalArgumentException("El producto no puede ser nulo");
        }
        
        if (cantidad == 0) {
            throw new IllegalArgumentException("La cantidad no puede ser cero");
        }
        
        // Precio unitario basado en si es entrada (precio de compra) o salida (precio de venta)
        BigDecimal precioUnitario;
        if (cantidad > 0) {
            precioUnitario = (producto.getPrecioCosto() != null) ? producto.getPrecioCosto() : new BigDecimal("0");
        } else {
            precioUnitario = (producto.getPrecioVenta() != null) ? producto.getPrecioVenta() : new BigDecimal("0");
        }
        
        // Crear registro de movimiento
        RegistMovimient registroMovimiento = new RegistMovimient();
        registroMovimiento.setProducto(producto);
        registroMovimiento.setCantidad(Math.abs(cantidad));
        registroMovimiento.setPrecioUnitario(precioUnitario);
        registroMovimiento.setTipoMovimiento(cantidad > 0 ? "ENTRADA" : "SALIDA");
        registroMovimiento.setFecha(LocalDateTime.now());
        registroMovimiento.setMotivo(motivo);
        
        return registMovimientService.guardar(registroMovimiento);
    }

    // Método auxiliar para generar números de lote únicos
    private String generarNumeroLote() {
        return "LOT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
