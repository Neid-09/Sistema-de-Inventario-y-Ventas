package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Caja;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.MovimientoCaja;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, Integer> {
    List<MovimientoCaja> findByCaja(Caja caja);
} 