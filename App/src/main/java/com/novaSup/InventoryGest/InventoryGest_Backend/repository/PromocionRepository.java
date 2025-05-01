package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Promocion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface PromocionRepository extends JpaRepository<Promocion, Integer> {
    List<Promocion> findByIdProducto(Integer idProducto);
    List<Promocion> findByIdCategoria(Integer idCategoria);

    @Query("SELECT p FROM Promocion p WHERE :fecha BETWEEN p.fechaInicio AND p.fechaFin")
    List<Promocion> findPromocionesActivas(LocalDate fecha);

    @Query("SELECT p FROM Promocion p WHERE p.idProducto = :idProducto AND :fecha BETWEEN p.fechaInicio AND p.fechaFin")
    List<Promocion> findPromocionesActivasPorProducto(Integer idProducto, LocalDate fecha);
}