package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.LoteRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProductoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.NotificacionService; // Importar NotificacionService
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@Service
public class StockServiceImpl {

    private final LoteRepository loteRepository;
    private final ProductoRepository productoRepository;
    private final NotificacionService notificacionService; // Inyectar NotificacionService

    // Inyectar NotificacionService en el constructor
    public StockServiceImpl(LoteRepository loteRepository, ProductoRepository productoRepository, NotificacionService notificacionService) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
        this.notificacionService = notificacionService;
    }

    /**
     * Calcula el stock total de un producto basado en sus lotes activos
     */
    public int calcularStockProducto(Integer idProducto) {
        List<Lote> lotes = loteRepository.findByProductoIdProductoAndActivoTrue(idProducto);
        return lotes.stream()
                .mapToInt(Lote::getCantidad)
                .sum();
    }    /**
     * Actualiza el stock de un producto en la base de datos
     * Si el producto está inactivo, no se actualizará el stock
     * Después de actualizar el stock, verifica inmediatamente los niveles
     * y envía notificaciones si es necesario (stock bajo o sobrestock)
     */
    @Transactional
    public void actualizarStockProducto(Integer idProducto) {
        productoRepository.findById(idProducto).ifPresent(producto -> {
            // Verificar si el producto está activo
            if (!producto.getEstado()) {
                // Si el producto está inactivo, no actualizamos el stock
                // pero tampoco lanzamos excepción para permitir listar productos
                return;
            }

            int stockCalculado = calcularStockProducto(idProducto);
            if (producto.getStock() != stockCalculado) {
                int stockAnterior = producto.getStock();
                producto.setStock(stockCalculado);
                productoRepository.save(producto);
                
                // Verificación inmediata de niveles de stock después del cambio
                verificarYNotificarNivelesStock(producto, stockAnterior, stockCalculado);
            }
        });
    }    /**
     * Verifica los niveles de stock de un producto y envía notificaciones inmediatas
     * si se detecta stock bajo o sobrestock
     */
    private void verificarYNotificarNivelesStock(Producto producto, int stockAnterior, int stockActual) {
        try {
            // Solo procesar si el producto está activo
            if (!producto.getEstado()) {
                return;
            }

            // Verificar stock bajo (solo si se configuró un mínimo y el stock actual está por debajo)
            if (producto.getStockMinimo() != null && stockActual <= producto.getStockMinimo()) {
                // Solo notificar si el stock cambió de estar bien a estar bajo, o si disminuyó aún más
                if (stockAnterior > producto.getStockMinimo() || stockActual < stockAnterior) {
                    notificacionService.notificarUsuariosRelevantes(
                            "Stock Bajo: " + producto.getNombre(),
                            "El producto '" + producto.getNombre() + "' (Código: " + producto.getCodigo() +
                                    ") tiene un stock actual de " + stockActual +
                                    " unidades, por debajo del mínimo de " + producto.getStockMinimo() + " unidades.",
                            "STOCK_BAJO",
                            producto.getIdProducto()
                    );
                }
            }

            // Verificar sobrestock (solo si se configuró un máximo y el stock actual está por encima)
            if (producto.getStockMaximo() != null && stockActual > producto.getStockMaximo()) {
                // Solo notificar si el stock cambió de estar bien a sobrestock, o si aumentó aún más
                if (stockAnterior <= producto.getStockMaximo() || stockActual > stockAnterior) {
                    notificacionService.notificarUsuariosRelevantes(
                            "Sobrestock: " + producto.getNombre(),
                            "El producto '" + producto.getNombre() + "' (Código: " + producto.getCodigo() +
                                    ") tiene un stock actual de " + stockActual +
                                    " unidades, superando el máximo recomendado de " + producto.getStockMaximo() + " unidades.",
                            "SOBRESTOCK",
                            producto.getIdProducto()
                    );
                }
            }

        } catch (Exception e) {
            // Log del error pero no fallar la actualización del stock
            System.err.println("Error al verificar niveles de stock para notificaciones: " + e.getMessage());
        }
    }

    /**
     * Obtiene los lotes activos para un producto
     */
    public List<Lote> obtenerLotesActivosPorProducto(Integer idProducto) {
        return loteRepository.findByProductoIdProductoAndActivoTrue(idProducto);
    }

    /**
     * Verifica y desactiva lotes vencidos, y notifica sobre la desactivación.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Ejecutar a medianoche
    @Transactional // Asegurar transacción para guardar lote y actualizar stock
    public void verificarYDesactivarLotesVencidos() {
        Date hoy = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        // Buscar solo lotes activos para optimizar
        List<Lote> lotesActivos = loteRepository.findByActivoTrue();

        for (Lote lote : lotesActivos) {
            if (lote.getFechaVencimiento() != null && lote.getFechaVencimiento().before(hoy)) {
                // El lote está vencido, desactivarlo
                lote.setActivo(false);
                loteRepository.save(lote);

                // Notificar sobre la desactivación del lote vencido
                String nombreProducto = lote.getProducto() != null ? lote.getProducto().getNombre() : "Desconocido";
                String numeroLote = lote.getNumeroLote() != null ? lote.getNumeroLote() : "N/A";
                String fechaVencimientoStr = lote.getFechaVencimiento().toString(); // Sabemos que no es null aquí

                notificacionService.notificarUsuariosRelevantes(
                        "Lote Vencido Desactivado: " + nombreProducto,
                        "El lote '" + numeroLote + "' del producto '" + nombreProducto +
                                "' (vencido el " + fechaVencimientoStr + ") ha sido desactivado automáticamente.",
                        "LOTE_VENCIDO", // Tipo de notificación
                        lote.getIdLote() // Referencia al lote
                );

                // Actualizar el stock del producto asociado
                if (lote.getProducto() != null) {
                    actualizarStockProducto(lote.getProducto().getIdProducto());
                }
            }
        }
    }
}