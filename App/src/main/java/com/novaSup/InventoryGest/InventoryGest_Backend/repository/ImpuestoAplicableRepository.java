package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.ImpuestoAplicable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface ImpuestoAplicableRepository extends JpaRepository<ImpuestoAplicable, Integer> {

    /**
     * Encuentra los impuestos aplicables para un producto o categoría específica en una fecha determinada.
     * Se asegura de cargar la TasaImpuesto y el TipoImpuesto asociados para evitar problemas N+1.
     * Un impuesto es aplicable si:
     * - Está marcado como 'aplica = true'.
     * - La fechaActual está dentro del rango de fechaInicio y fechaFin (fechaFin puede ser null, indicando que no hay fecha de finalización).
     * - El impuesto está asociado directamente al idProducto proporcionado O está asociado directamente al idCategoria proporcionado.
     */
    @Query("SELECT ia FROM ImpuestoAplicable ia " +
           "LEFT JOIN FETCH ia.tasaImpuesto ti " +
           "LEFT JOIN FETCH ti.tipoImpuesto tim " +
           "WHERE ia.aplica = true " +
           "AND :fechaActual >= ia.fechaInicio " +
           "AND (ia.fechaFin IS NULL OR :fechaActual <= ia.fechaFin) " +
           "AND (" +
           "  (ia.producto IS NOT NULL AND ia.producto.idProducto = :idProducto) OR " +
           "  (ia.categoria IS NOT NULL AND ia.categoria.idCategoria = :idCategoria)" +
           ")")
    List<ImpuestoAplicable> findImpuestosAplicables(
            @Param("idProducto") Integer idProducto,
            @Param("idCategoria") Integer idCategoria,
            @Param("fechaActual") Date fechaActual);
} 