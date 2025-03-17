package com.novaSup.InventoryGest.InventoryGest_Backend.repositories;

import com.novaSup.InventoryGest.InventoryGest_Backend.model.VentaProducto;
import com.novaSup.InventoryGest.InventoryGest_Backend.model.VentaProductoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaProductoRepository extends JpaRepository<VentaProducto, VentaProductoId> {
}
