package com.novaSup.InventoryGest.InventoryGest_Backend.scheduler;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.LoteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.NotificacionService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.interfaz.ProductoService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class InventarioScheduler {

    private final ProductoService productoService;
    private final LoteService loteService;
    private final NotificacionService notificacionService;

    public InventarioScheduler(ProductoService productoService, 
                              LoteService loteService, 
                              NotificacionService notificacionService) {
        this.productoService = productoService;
        this.loteService = loteService;
        this.notificacionService = notificacionService;
    }    
    
    // NOTA: Las verificaciones de stock bajo y sobrestock han sido movidas
    // para ejecutarse inmediatamente después de cada movimiento de inventario
    // en lugar de en horarios programados para mayor eficiencia y respuesta inmediata.


    // Verificar lotes próximos a vencer a las 5:30 PM todos los días
    @Scheduled(cron = "0 30 17 * * *") // A las 5:30 PM (17:30 en formato 24h)
    public void verificarLotesProximosAVencer() {
        System.out.println("Ejecutando verificación de lotes próximos a vencer a las 5:30 PM...");
        int diasMargen = 15; // Días de margen, podría ser configurable
        List<Lote> lotesProximosVencer = loteService.obtenerLotesProximosVencer(diasMargen);

        for (Lote lote : lotesProximosVencer) {
            String nombreProducto = lote.getProducto() != null ? lote.getProducto().getNombre() : "Desconocido";
            
            notificacionService.notificarLoteProximoAVencer(
                    lote.getIdLote(),
                    nombreProducto,
                    lote.getFechaVencimiento()
            );
        }
    }    // Resumen diario de notificaciones a las 8:00 PM todos los días
    // NOTA: El stock bajo y sobrestock se verifican en tiempo real después de cada movimiento,
    // pero se incluyen en el resumen solo para información estadística
    @Scheduled(cron = "0 0 20 * * *") // A las 8:00 PM (20:00 en formato 24h)
    public void resumenDiarioNotificaciones() {
        System.out.println("Ejecutando resumen diario de notificaciones a las 8:00 PM...");
        
        // Verificar todos los tipos de alertas para el resumen nocturno
        List<Producto> productosBajoStock = productoService.obtenerConStockBajo();
        List<Producto> productosConSobrestock = productoService.obtenerConSobrestock();
        List<Lote> lotesProximosVencer = loteService.obtenerLotesProximosVencer(15);
        
        // Enviar resumen solo si hay alertas
        if (!productosBajoStock.isEmpty() || !productosConSobrestock.isEmpty() || !lotesProximosVencer.isEmpty()) {
            String resumen = String.format(
                "Resumen del día: %d productos con stock bajo, %d productos con sobrestock, %d lotes próximos a vencer",
                productosBajoStock.size(),
                productosConSobrestock.size(),
                lotesProximosVencer.size()
            );
            
            notificacionService.notificarUsuariosRelevantes(
                "Resumen diario de inventario",
                resumen,
                "RESUMEN_DIARIO",
                null
            );
        }
    }

}