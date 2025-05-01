package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.LoteRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProductoRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.NotificacionService; // Importar NotificacionService
import org.springframework.beans.factory.annotation.Autowired; // Importar Autowired
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
    }

    /**
     * Actualiza el stock de un producto en la base de datos
     * Si el producto está inactivo, no se actualizará el stock
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
                producto.setStock(stockCalculado);
                productoRepository.save(producto);
            }
        });
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