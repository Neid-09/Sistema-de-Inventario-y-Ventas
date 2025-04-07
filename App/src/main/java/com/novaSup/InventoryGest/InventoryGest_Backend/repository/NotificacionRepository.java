package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    List<Notificacion> findByIdUsuarioOrderByFechaDesc(Integer idUsuario);
    List<Notificacion> findByIdUsuarioAndLeidaOrderByFechaDesc(Integer idUsuario, Boolean leida);
}