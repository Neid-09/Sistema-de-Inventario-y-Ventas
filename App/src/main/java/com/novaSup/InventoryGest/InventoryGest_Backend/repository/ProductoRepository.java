package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Integer> {
    List<Producto> findByEstadoTrue();
    Optional<Producto> findByCodigo(String codigo);
    List<Producto> findByCategoria_IdCategoria(Integer idCategoria);

    @Query("SELECT p FROM Producto p WHERE p.stock <= p.stockMinimo AND p.estado = true")
    List<Producto> findProductosConStockBajo();
}