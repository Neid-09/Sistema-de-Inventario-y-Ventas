package com.novaSup.InventoryGest.repositories;

import com.novaSup.InventoryGest.model.ProveedorProducto;

import com.novaSup.InventoryGest.model.ProveedorProductoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProveedorProductoRepository extends JpaRepository<ProveedorProducto, ProveedorProductoId> {
}
