package com.novaSup.InventoryGest.InventoryGest_Backend.repository;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.EntradaProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EntradaProductoRepository extends JpaRepository<EntradaProducto, Integer> {
    List<EntradaProducto> findByProductoIdProducto(Integer idProducto);
}