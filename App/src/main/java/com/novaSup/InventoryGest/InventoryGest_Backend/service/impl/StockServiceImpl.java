package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.LoteRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.ProductoRepository;
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

    public StockServiceImpl(LoteRepository loteRepository, ProductoRepository productoRepository) {
        this.loteRepository = loteRepository;
        this.productoRepository = productoRepository;
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
     */
    @Transactional
    public void actualizarStockProducto(Integer idProducto) {
        productoRepository.findById(idProducto).ifPresent(producto -> {
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
     * Verifica y desactiva lotes vencidos
     */
    @Scheduled(cron = "0 0 0 * * ?") // Ejecutar a medianoche
    public void verificarYDesactivarLotesVencidos() {
        Date hoy = Date.from(LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant());
        List<Lote> todosLotes = loteRepository.findAll();

        for (Lote lote : todosLotes) {
            if (lote.getActivo() && lote.getFechaVencimiento() != null &&
                    lote.getFechaVencimiento().before(hoy)) {
                // El lote está vencido, desactivarlo
                lote.setActivo(false);
                loteRepository.save(lote);

                // Actualizar el stock del producto
                if (lote.getProducto() != null) {
                    actualizarStockProducto(lote.getProducto().getIdProducto());
                }
            }
        }
    }
}