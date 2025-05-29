package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.TipoImpuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TipoImpuestoRepository extends JpaRepository<TipoImpuesto, Integer> {
    // Aquí puedes añadir métodos de consulta personalizados si son necesarios más adelante
} 