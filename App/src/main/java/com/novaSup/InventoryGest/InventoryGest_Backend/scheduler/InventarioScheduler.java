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

    @Autowired
    private UsuarioRepository usuarioRepository;

    // Verificar productos con bajo stock diariamente a las 8:00 AM
    @Scheduled(cron = "0 0 8 * * *")
    public void verificarProductosBajoStock() {
        List<Producto> productosBajoStock = productoService.obtenerConStockBajo();
        if (!productosBajoStock.isEmpty()) {
            notificarUsuariosRelevantes("STOCK_BAJO",
                    "Productos con bajo stock",
                    "Hay " + productosBajoStock.size() + " productos con stock por debajo del mínimo");
        }
    }

    // Verificar productos con sobrestock diariamente a las 10:00 AM
    @Scheduled(cron = "0 0 10 * * *")
    public void verificarProductosSobrestock() {
        List<Producto> productosConSobrestock = productoService.obtenerConSobrestock();

        for (Producto producto : productosConSobrestock) {
            String titulo = "Sobrestock: " + producto.getNombre();
            String mensaje = "El producto " + producto.getNombre() + " (Código: " +
                    producto.getCodigo() + ") tiene un stock de " +
                    producto.getStock() + " unidades, superando el máximo recomendado.";

            // Usar el servicio de notificación en lugar del método privado
            notificacionService.notificarUsuariosRelevantes(
                    titulo,
                    mensaje,
                    "ALERTA_SOBRESTOCK",
                    producto.getIdProducto()
            );
        }
    }

    // Verificar lotes próximos a vencer cada día a las 9:00 AM
    @Scheduled(cron = "0 0 9 * * *")
    public void verificarLotesProximosAVencer() {
        List<Lote> lotesProximosVencer = loteService.obtenerLotesProximosVencer(15); // 15 días de margen
        if (!lotesProximosVencer.isEmpty()) {
            notificarUsuariosRelevantes("LOTES_VENCER",
                    "Lotes próximos a vencer",
                    "Hay " + lotesProximosVencer.size() + " lotes que vencerán en los próximos 15 días");
        }
    }

    // Método para enviar notificaciones a usuarios relevantes
    private void notificarUsuariosRelevantes(String tipo, String titulo, String mensaje) {
        // Notificar a administradores
        usuarioRepository.findByRolNombre("ADMINISTRADOR").forEach(admin -> {
            Notificacion notificacion = new Notificacion();
            notificacion.setIdUsuario(admin.getIdUsuario());
            notificacion.setTitulo(titulo);
            notificacion.setMensaje(mensaje);
            notificacion.setFecha(LocalDateTime.now());
            notificacion.setLeida(false);
            notificacion.setTipo(tipo);

            notificacionService.crear(notificacion);
        });

        // Notificar a usuarios con permisos específicos
        if (tipo.equals("STOCK_BAJO")) {
            usuarioRepository.findByPermiso("ver_productos").forEach(usuario -> {
                // Evitar duplicados para administradores
                if (!usuario.getRol().getNombre().equalsIgnoreCase("ADMINISTRADOR")) {
                    Notificacion notificacion = new Notificacion();
                    notificacion.setIdUsuario(usuario.getIdUsuario());
                    notificacion.setTitulo(titulo);
                    notificacion.setMensaje(mensaje);
                    notificacion.setFecha(LocalDateTime.now());
                    notificacion.setLeida(false);
                    notificacion.setTipo(tipo);

                    notificacionService.crear(notificacion);
                }
            });
        }
    }
}