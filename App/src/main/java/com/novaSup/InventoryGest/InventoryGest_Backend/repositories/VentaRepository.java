package com.novaSup.InventoryGest.InventoryGest_Backend.repositories;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Integer> {
}
