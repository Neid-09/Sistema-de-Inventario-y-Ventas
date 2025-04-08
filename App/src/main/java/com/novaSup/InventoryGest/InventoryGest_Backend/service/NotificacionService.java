package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Notificacion;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;

import java.util.List;
import java.util.Optional;

public interface NotificacionService {
    List<Notificacion> obtenerTodasLasNotificaciones();
    List<Notificacion> obtenerNotificacionesPorUsuario(Integer idUsuario);
    List<Notificacion> obtenerNotificacionesNoLeidasPorUsuario(Integer idUsuario);
    Optional<Notificacion> obtenerPorId(Integer id);
    Notificacion crear(Notificacion notificacion);
    Notificacion marcarComoLeida(Integer idNotificacion);
    Notificacion marcarComoNoLeida(Integer idNotificacion);
    void eliminar(Integer idNotificacion);

    // Métodos específicos para notificaciones de inventario
    void notificarStockBajo(Producto producto);
    void notificarLoteProximoAVencer(Integer idLote, String nombreProducto, java.util.Date fechaVencimiento);

    // Método nuevo para notificar a todos los usuarios relevantes (administradores y vendedores)
    void notificarUsuariosRelevantes(String titulo, String mensaje, String tipo, Integer idReferencia);

    // Modificar el método existente para usar la nueva función
    void crearNotificacionAdministradores(String titulo, String mensaje, String tipo, Integer idReferencia);

    // Método único para sobrestock (eliminar el duplicado)
    void notificarSobrestock(Producto producto);
}