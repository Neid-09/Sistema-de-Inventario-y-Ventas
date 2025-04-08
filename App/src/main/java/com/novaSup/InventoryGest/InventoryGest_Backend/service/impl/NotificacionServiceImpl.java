package com.novaSup.InventoryGest.InventoryGest_Backend.service.impl;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Notificacion;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Usuario;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.NotificacionRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.repository.UsuarioRepository;
import com.novaSup.InventoryGest.InventoryGest_Backend.service.NotificacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    @Autowired
    private NotificacionRepository notificacionRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Override
    public List<Notificacion> obtenerTodasLasNotificaciones() {
        return notificacionRepository.findAll();
    }

    @Override
    public List<Notificacion> obtenerNotificacionesPorUsuario(Integer idUsuario) {
        return notificacionRepository.findByIdUsuarioOrderByFechaDesc(idUsuario);
    }

    @Override
    public List<Notificacion> obtenerNotificacionesNoLeidasPorUsuario(Integer idUsuario) {
        return notificacionRepository.findByIdUsuarioAndLeidaOrderByFechaDesc(idUsuario, false);
    }

    @Override
    public Optional<Notificacion> obtenerPorId(Integer id) {
        return notificacionRepository.findById(id);
    }

    @Override
    public Notificacion crear(Notificacion notificacion) {
        notificacion.setFecha(LocalDateTime.now());
        if (notificacion.getLeida() == null) {
            notificacion.setLeida(false);
        }
        return notificacionRepository.save(notificacion);
    }

    @Override
    public Notificacion marcarComoLeida(Integer idNotificacion) {
        return notificacionRepository.findById(idNotificacion)
                .map(notificacion -> {
                    notificacion.setLeida(true);
                    return notificacionRepository.save(notificacion);
                })
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    @Override
    public Notificacion marcarComoNoLeida(Integer idNotificacion) {
        return notificacionRepository.findById(idNotificacion)
                .map(notificacion -> {
                    notificacion.setLeida(false);
                    return notificacionRepository.save(notificacion);
                })
                .orElseThrow(() -> new RuntimeException("Notificación no encontrada"));
    }

    @Override
    public void eliminar(Integer idNotificacion) {
        notificacionRepository.deleteById(idNotificacion);
    }

    @Override
    public void notificarStockBajo(Producto producto) {
        notificarUsuariosRelevantes(
                "Stock bajo",
                "El producto " + producto.getNombre() + " tiene un stock de " + producto.getStock() +
                        " unidades (mínimo: " + producto.getStockMinimo() + ")",
                "ALERTA_STOCK_BAJO",
                producto.getIdProducto()
        );
    }

    @Override
    public void notificarLoteProximoAVencer(Integer idLote, String nombreProducto, Date fechaVencimiento) {
        LocalDateTime fechaVenc = LocalDateTime.ofInstant(
                fechaVencimiento.toInstant(), ZoneId.systemDefault());

        String fechaFormateada = fechaVenc.toLocalDate().toString();

        notificarUsuariosRelevantes(
                "Lote próximo a vencer",
                "El lote del producto " + nombreProducto + " vencerá el " + fechaFormateada,
                "ALERTA_VENCIMIENTO",
                idLote
        );
    }

    @Override
    public void notificarSobrestock(Producto producto) {
        notificarUsuariosRelevantes(
                "Sobrestock detectado",
                "El producto " + producto.getNombre() + " tiene un stock de " + producto.getStock() +
                        " unidades (máximo: " + producto.getStockMaximo() + ")",
                "ALERTA_SOBRESTOCK",
                producto.getIdProducto()
        );
    }

    @Override
    public void notificarUsuariosRelevantes(String titulo, String mensaje, String tipo, Integer idReferencia) {
        // Lista para almacenar IDs de usuarios ya notificados
        List<Integer> idsUsuariosNotificados = new ArrayList<>();

        // 1. Notificar a administradores
        usuarioRepository.findByRolNombre("ADMINISTRADOR").forEach(admin -> {
            Notificacion notificacion = new Notificacion();
            notificacion.setIdUsuario(admin.getIdUsuario());
            notificacion.setTitulo(titulo);
            notificacion.setMensaje(mensaje);
            notificacion.setFecha(LocalDateTime.now());
            notificacion.setLeida(false);
            notificacion.setTipo(tipo);
            notificacion.setIdReferencia(idReferencia);

            this.crear(notificacion);
            idsUsuariosNotificados.add(admin.getIdUsuario());
        });

        // 2. Notificar a usuarios con el permiso ver_notificaciones (por rol o por asignación directa)
        usuarioRepository.findByPermisoEffective("ver_notificaciones").forEach(usuario -> {
            // Verificar que no haya sido notificado antes
            if (!idsUsuariosNotificados.contains(usuario.getIdUsuario())) {
                Notificacion notificacion = new Notificacion();
                notificacion.setIdUsuario(usuario.getIdUsuario());
                notificacion.setTitulo(titulo);
                notificacion.setMensaje(mensaje);
                notificacion.setFecha(LocalDateTime.now());
                notificacion.setLeida(false);
                notificacion.setTipo(tipo);
                notificacion.setIdReferencia(idReferencia);

                this.crear(notificacion);
                idsUsuariosNotificados.add(usuario.getIdUsuario());
            }
        });

        // 3. Notificar a usuarios con permisos específicos según el tipo de notificación
        String permisoRequerido = "ver_productos"; // Permiso por defecto para alertas de productos

        if (tipo.equals("ALERTA_VENCIMIENTO")) {
            permisoRequerido = "ver_lotes";
        }

        // Usar el nuevo método para obtener usuarios con permisos efectivos
        final String permiso = permisoRequerido;
        usuarioRepository.findByPermisoEffective(permiso).forEach(usuario -> {
            // Verificar que no haya sido notificado antes
            if (!idsUsuariosNotificados.contains(usuario.getIdUsuario())) {
                Notificacion notificacion = new Notificacion();
                notificacion.setIdUsuario(usuario.getIdUsuario());
                notificacion.setTitulo(titulo);
                notificacion.setMensaje(mensaje);
                notificacion.setFecha(LocalDateTime.now());
                notificacion.setLeida(false);
                notificacion.setTipo(tipo);
                notificacion.setIdReferencia(idReferencia);

                this.crear(notificacion);
                idsUsuariosNotificados.add(usuario.getIdUsuario());
            }
        });
    }

    @Override
    public void crearNotificacionAdministradores(String titulo, String mensaje, String tipo, Integer idReferencia) {
        // Reutilizar el método general
        notificarUsuariosRelevantes(titulo, mensaje, tipo, idReferencia);
    }

    // Método auxiliar para obtener IDs de administradores
    private List<Integer> obtenerIdsAdministradores() {
        return usuarioRepository.findByRolNombre("ROLE_ADMINISTRADOR")
                .stream()
                .map(Usuario::getIdUsuario)
                .collect(Collectors.toList());
    }
}