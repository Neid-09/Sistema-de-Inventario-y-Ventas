package com.novaSup.InventoryGest.InventoryGest_Backend.scheduler;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Notificacion;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.UsuarioRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.LoteService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.NotificacionService;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.ProductoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class InventarioScheduler {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private LoteService loteService;

    @Autowired
    private NotificacionService notificacionService;

    // Verificar productos con bajo stock diariamente a las 8:00 AM
    @Scheduled(cron = "0 0 8 * * *") // Ejecutar diariamente a las 8:00 AM
    public void verificarProductosBajoStock() {
        List<Producto> productosBajoStock = productoService.obtenerConStockBajo();
        if (!productosBajoStock.isEmpty()) {
            notificacionService.notificarUsuariosRelevantes(
                    "Productos con bajo stock",
                    "Hay " + productosBajoStock.size() + " productos con stock por debajo del mínimo",
                    "STOCK_BAJO",
                    null  // No hay referencia específica
            );
        }
    }

    // Verificar productos con sobrestock diariamente a las 10:00 AM
    @Scheduled(cron = "0 0 10 * * *") // Ejecutar diariamente a las 10:00 AM
    public void verificarProductosSobrestock() {
        List<Producto> productosConSobrestock = productoService.obtenerConSobrestock();

        for (Producto producto : productosConSobrestock) {
            notificacionService.notificarUsuariosRelevantes(
                    "Sobrestock: " + producto.getNombre(),
                    "El producto " + producto.getNombre() + " (Código: " +
                            producto.getCodigo() + ") tiene un stock de " +
                            producto.getStock() + " unidades, superando el máximo recomendado.",
                    "ALERTA_SOBRESTOCK",
                    producto.getIdProducto()
            );
        }
    }

    // Verificar lotes próximos a vencer cada día a las 9:00 AM
    @Scheduled(cron = "0 0 9 * * *") // Ejecutar diariamente a las 9:00 AM
    public void verificarLotesProximosAVencer() {
        int diasMargen = 15; // Días de margen, podría ser configurable
        List<Lote> lotesProximosVencer = loteService.obtenerLotesProximosVencer(diasMargen);

        for (Lote lote : lotesProximosVencer) {
            String nombreProducto = lote.getProducto() != null ? lote.getProducto().getNombre() : "Desconocido";
            String numeroLote = lote.getNumeroLote() != null ? lote.getNumeroLote() : "N/A";
            String fechaVencimientoStr = lote.getFechaVencimiento() != null ? lote.getFechaVencimiento().toString() : "N/A";

            notificacionService.notificarUsuariosRelevantes(
                    "Lote próximo a vencer: " + nombreProducto,
                    "El lote '" + numeroLote + "' del producto '" + nombreProducto +
                            "' vencerá el " + fechaVencimientoStr + ".",
                    "LOTE_PROXIMO_VENCER", // Tipo de notificación más específico
                    lote.getIdLote() // Referencia al lote específico
            );
        }
        // Ya no se envía una notificación general, sino una por cada lote.
        // if (!lotesProximosVencer.isEmpty()) {
        //     notificacionService.notificarUsuariosRelevantes(
        //             "Lotes próximos a vencer",
        //             "Hay " + lotesProximosVencer.size() + " lotes que vencerán en los próximos " + diasMargen + " días",
        //             "LOTES_VENCER",
        //             null  // No hay referencia específica
        //     );
        // }
    }

}