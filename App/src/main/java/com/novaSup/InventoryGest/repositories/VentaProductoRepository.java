package com.novaSup.InventoryGest.repositories;

import com.novaSup.InventoryGest.model.VentaProducto;
import com.novaSup.InventoryGest.model.VentaProductoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VentaProductoRepository extends JpaRepository<VentaProducto, VentaProductoId> {
}
