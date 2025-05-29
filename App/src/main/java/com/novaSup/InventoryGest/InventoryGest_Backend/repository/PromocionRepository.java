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

    @Query("SELECT p FROM Promocion p WHERE p.activo = true AND :fecha BETWEEN p.fechaInicio AND p.fechaFin")
    List<Promocion> findPromocionesActivas(LocalDate fecha);

    @Query("SELECT p FROM Promocion p WHERE p.activo = true AND p.idProducto = :idProducto AND :fecha BETWEEN p.fechaInicio AND p.fechaFin")
    List<Promocion> findPromocionesActivasPorProducto(Integer idProducto, LocalDate fecha);

    @Query("SELECT p FROM Promocion p WHERE p.activo = true AND :fechaActual BETWEEN p.fechaInicio AND p.fechaFin AND (p.idProducto = :idProducto OR (p.idProducto IS NULL AND p.idCategoria = :idCategoria))")
    List<Promocion> findActivePromocionesByProductoOrCategoria(Integer idProducto, Integer idCategoria, LocalDate fechaActual);
}