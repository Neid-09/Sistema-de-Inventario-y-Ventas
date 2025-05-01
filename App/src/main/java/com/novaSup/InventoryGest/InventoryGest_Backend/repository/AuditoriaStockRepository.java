package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.AuditoriaStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditoriaStockRepository extends JpaRepository<AuditoriaStock, Integer> {
    List<AuditoriaStock> findByIdProducto(Integer idProducto);
    List<AuditoriaStock> findByFechaBetween(LocalDateTime fechaInicio, LocalDateTime fechaFin);
}