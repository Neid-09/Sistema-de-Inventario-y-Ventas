package com.novaSup.InventoryGest.InventoryGest_Backend.service;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Recompensa;
import java.util.List;
import java.util.Optional;

public interface RecompensaService {
    List<Recompensa> obtenerRecompensasPorCliente(Integer idCliente);
    Optional<Recompensa> obtenerRecompensaPorId(Integer idRecompensa);
    Recompensa canjearRecompensa(Integer idCliente, Recompensa recompensa);
    // No se suelen necesitar métodos para actualizar o eliminar recompensas individuales,
    // ya que representan un evento pasado (canje). Si es necesario, se pueden añadir.
}
