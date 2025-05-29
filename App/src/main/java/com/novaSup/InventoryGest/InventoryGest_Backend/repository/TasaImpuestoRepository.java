package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.TasaImpuesto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TasaImpuestoRepository extends JpaRepository<TasaImpuesto, Integer> {
    // Aquí puedes añadir métodos de consulta personalizados si son necesarios más adelante
} 