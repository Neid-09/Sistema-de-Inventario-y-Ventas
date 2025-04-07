package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    List<Lote> findByProductoIdProductoAndActivoTrue(Integer idProducto);
    List<Lote> findByFechaVencimientoBetweenAndActivoTrue(Date fechaInicio, Date fechaFin);
    List<Lote> findByActivoTrue();
    Optional<Lote> findByIdLoteAndActivoTrue(Integer id);
    List<Lote> findByActivoFalse();
    Optional<Lote> findByIdLoteAndActivoFalse(Integer id);
}