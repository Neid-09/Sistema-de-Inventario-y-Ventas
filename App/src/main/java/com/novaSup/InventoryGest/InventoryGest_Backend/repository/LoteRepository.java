package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Lote;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface LoteRepository extends JpaRepository<Lote, Integer> {
    List<Lote> findByProductoIdProductoAndActivoTrue(Integer idProducto);
    List<Lote> findByFechaVencimientoBetweenAndActivoTrue(Date fechaInicio, Date fechaFin);
    List<Lote> findByFechaEntradaBetweenAndActivoTrue(Date fechaInicio, Date fechaFin);
    List<Lote> findByActivoTrue();
    Optional<Lote> findByIdLoteAndActivoTrue(Integer id);
    List<Lote> findByActivoFalse();
    Optional<Lote> findByIdLoteAndActivoFalse(Integer id);
    List<Lote> findByProductoIdProductoAndActivoTrueOrderByFechaVencimientoAsc(Integer idProducto);
    List<Lote> findByProductoIdProductoAndActivoTrueOrderByFechaVencimientoAscFechaEntradaAsc(Integer idProducto);

    @Modifying
    @Query("UPDATE Lote l SET l.activo = false WHERE l.activo = true AND l.fechaVencimiento < :fechaActual")
    void desactivarLotesVencidos(@Param("fechaActual") Date fechaActual);

}