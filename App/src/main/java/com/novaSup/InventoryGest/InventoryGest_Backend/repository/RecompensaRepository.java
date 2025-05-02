package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Recompensa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecompensaRepository extends JpaRepository<Recompensa, Integer> { // Extiende JpaRepository

    // Método para buscar todas las recompensas asociadas a un idCliente
    List<Recompensa> findByIdCliente(Integer idCliente);

    // Puedes añadir más métodos de consulta personalizados si son necesarios
}
