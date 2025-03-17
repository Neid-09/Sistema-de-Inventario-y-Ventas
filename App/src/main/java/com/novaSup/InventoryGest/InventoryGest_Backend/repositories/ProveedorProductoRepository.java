package com.novaSup.InventoryGest.InventoryGest_Backend.repositories;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.ProveedorProducto;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.ProveedorProductoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorProductoRepository extends JpaRepository<ProveedorProducto, ProveedorProductoId> {
}
